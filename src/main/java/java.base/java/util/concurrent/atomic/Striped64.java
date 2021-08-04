/*
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

/*
 *
 *
 *
 *
 *
 * Written by Doug Lea with assistance from members of JCP JSR-166
 * Expert Group and released to the public domain, as explained at
 * http://creativecommons.org/publicdomain/zero/1.0/
 */

package java.util.concurrent.atomic;

import java.lang.invoke.MethodHandles;
import java.lang.invoke.VarHandle;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.DoubleBinaryOperator;
import java.util.function.LongBinaryOperator;

/**
 * A package-local class holding common representation and mechanics
 * for classes supporting dynamic striping on 64bit values. The class
 * extends Number so that concrete subclasses must publicly do so.
 */
@SuppressWarnings("serial")
abstract class Striped64 extends Number {
    /*
     * This class maintains a lazily-initialized table of atomically
     * updated variables, plus an extra "base" field. The table size
     * is a power of two. Indexing uses masked per-thread hash codes.
     * Nearly all declarations in this class are package-private,
     * accessed directly by subclasses.
     *
     * Table entries are of class Cell; a variant of AtomicLong padded
     * (via @Contended) to reduce cache contention. Padding is
     * overkill for most Atomics because they are usually irregularly
     * scattered in memory and thus don't interfere much with each
     * other. But Atomic objects residing in arrays will tend to be
     * placed adjacent to each other, and so will most often share
     * cache lines (with a huge negative performance impact) without
     * this precaution.
     *
     * In part because Cells are relatively large, we avoid creating
     * them until they are needed.  When there is no contention, all
     * updates are made to the base field.  Upon first contention (a
     * failed CAS on base update), the table is initialized to size 2.
     * The table size is doubled upon further contention until
     * reaching the nearest power of two greater than or equal to the
     * number of CPUS. Table slots remain empty (null) until they are
     * needed.
     *
     * A single spinlock ("cellsBusy") is used for initializing and
     * resizing the table, as well as populating slots with new Cells.
     * There is no need for a blocking lock; when the lock is not
     * available, threads try other slots (or the base).  During these
     * retries, there is increased contention and reduced locality,
     * which is still better than alternatives.
     *
     * The Thread probe fields maintained via ThreadLocalRandom serve
     * as per-thread hash codes. We let them remain uninitialized as
     * zero (if they come in this way) until they contend at slot
     * 0. They are then initialized to values that typically do not
     * often conflict with others.  Contention and/or table collisions
     * are indicated by failed CASes when performing an update
     * operation. Upon a collision, if the table size is less than
     * the capacity, it is doubled in size unless some other thread
     * holds the lock. If a hashed slot is empty, and lock is
     * available, a new Cell is created. Otherwise, if the slot
     * exists, a CAS is tried.  Retries proceed by "double hashing",
     * using a secondary hash (Marsaglia XorShift) to try to find a
     * free slot.
     *
     * The table size is capped because, when there are more threads
     * than CPUs, supposing that each thread were bound to a CPU,
     * there would exist a perfect hash function mapping threads to
     * slots that eliminates collisions. When we reach capacity, we
     * search for this mapping by randomly varying the hash codes of
     * colliding threads.  Because search is random, and collisions
     * only become known via CAS failures, convergence can be slow,
     * and because threads are typically not bound to CPUS forever,
     * may not occur at all. However, despite these limitations,
     * observed contention rates are typically low in these cases.
     *
     * It is possible for a Cell to become unused when threads that
     * once hashed to it terminate, as well as in the case where
     * doubling the table causes no thread to hash to it under
     * expanded mask.  We do not try to detect or remove such cells,
     * under the assumption that for long-running instances, observed
     * contention levels will recur, so the cells will eventually be
     * needed again; and for short-lived ones, it does not matter.
     */

    /**
     * Padded variant of AtomicLong supporting only raw accesses plus CAS.
     *
     * JVM intrinsics note: It would be possible to use a release-only
     * form of CAS here, if it were provided.
     *
     * 为了提高性能，使用注解jdk.internal.vm.annotation.Contended，避免伪共享
     */
    @jdk.internal.vm.annotation.Contended static final class Cell {
        /**
         * 保存需要累加的值
         */
        volatile long value;
        Cell(long x) { value = x; }

        /**
         * 使用VarHandle类的CAS来更新value的值
         */
        final boolean cas(long cmp, long val) {
            return VALUE.compareAndSet(this, cmp, val);
        }
        final void reset() {
            VALUE.setVolatile(this, 0L);
        }
        final void reset(long identity) {
            VALUE.setVolatile(this, identity);
        }
        final long getAndSet(long val) {
            return (long)VALUE.getAndSet(this, val);
        }

        // VarHandle mechanics
        private static final VarHandle VALUE;
        static {
            try {
                MethodHandles.Lookup l = MethodHandles.lookup();
                VALUE = l.findVarHandle(Cell.class, "value", long.class);
            } catch (ReflectiveOperationException e) {
                throw new ExceptionInInitializerError(e);
            }
        }
    }

    /** Number of CPUS, to place bound on table size */
    static final int NCPU = Runtime.getRuntime().availableProcessors();

    /**
     * Table of cells. When non-null, size is a power of 2.
     * 存放元素cell的Hash表，大小为2的整数次幂
     */
    transient volatile Cell[] cells;

    /**
     * Base value, used mainly when there is no contention, but also as
     * a fallback during table initialization races. Updated via CAS.
     *
     * 基础值:
     * - 在没有竞争的情况下，累加的数通过CAS累加到base上
     * - 在数组cells初始化过程中时，数组中的元素cell不可用。此时累加的数会尝试通过CAS累加到base上
     */
    transient volatile long base;

    /**
     * Spinlock (locked via CAS) used when resizing and/or creating Cells.
     * 自旋锁，通过CAS加锁
     * 用于创建和扩容数组cells的Hash表
     */
    transient volatile int cellsBusy;

    /**
     * Package-private default constructor.
     */
    Striped64() {
    }

    /**
     * CASes the base field.
     */
    final boolean casBase(long cmp, long val) {
        return BASE.compareAndSet(this, cmp, val);
    }

    final long getAndSetBase(long val) {
        return (long)BASE.getAndSet(this, val);
    }

    /**
     * CASes the cellsBusy field from 0 to 1 to acquire lock.
     */
    final boolean casCellsBusy() {
        return CELLSBUSY.compareAndSet(this, 0, 1);
    }

    /**
     * Returns the probe value for the current thread.
     * Duplicated from ThreadLocalRandom because of packaging restrictions.
     */
    static final int getProbe() {
        return (int) THREAD_PROBE.get(Thread.currentThread());
    }

    /**
     * Pseudo-randomly advances and records the given probe value for the
     * given thread.
     * Duplicated from ThreadLocalRandom because of packaging restrictions.
     */
    static final int advanceProbe(int probe) {
        probe ^= probe << 13;   // xorshift
        probe ^= probe >>> 17;
        probe ^= probe << 5;
        THREAD_PROBE.set(Thread.currentThread(), probe);
        return probe;
    }

    /**
     * Handles cases of updates involving initialization, resizing,
     * creating new Cells, and/or contention. See above for
     * explanation. This method suffers the usual non-modularity
     * problems of optimistic retry code, relying on rechecked sets of
     * reads.
     *
     * @param x the value
     * @param fn the update function, or null for add (this convention
     * avoids the need for an extra field or function in LongAdder).
     * @param wasUncontended false if CAS failed before call
     */
    final void longAccumulate(long x, LongBinaryOperator fn,
                              boolean wasUncontended) {
        int h;

        /*
         * 获取当前线程的threadLocalRandomProbe的值作为hash的值，如果当前线程的threadLocalRandomProbe的值为0，说明当前线程是
         * 第一次进入该方法，就强制设置线程的threadLocalRandomProbe的值为ThreadLocalRandom类的成员静态私有变量probeGenerator的值。
         * 需要注意的是，如果threadLocalRandomProbe的值为0，表示新的线程开始参与cell争用的情况:
         * - 当前线程还未参与cell争用。可能是数组cells还没有完成初始化，进到当前方法就是为了初始化数组cells后来进行争用的，是第一次对
         * base执行CAS操作失败的情况
         * - 执行add()方法时，对数组cells中某个位置的cell对象的第一次CAS操作执行失败，wasUncontended设置为fasle，会在这里将
         * wasUncontended重新设置为true。
         * 只要是已经参与cell争用后操作的线程的threadLocalRandomProbe的值都不为0
         */
        if ((h = getProbe()) == 0) {
            // ThreadLocalRandom类强制初始化
            ThreadLocalRandom.current(); // force initialization
            // 设置h的值为0x9e3779b9
            h = getProbe();
            // 将没有争用的标识设置为true
            wasUncontended = true;
        }
        /**
         * CAS操作冲突标识
         * 表示当前线程根据hash的值对数组cells中指定位置的cell对象，执行CAS累加操作时是否与其余的线程发生了冲突，导致CAS操作失败
         * - true : 表示发生冲突，导致CAS操作失败
         * - false : 表示没有发生冲突
         */
        boolean collide = false;                // True if last slot nonempty
        done: for (;;) {
            Cell[] cs; Cell c; int n; long v;
            /*
             * 这里包含以下三个操作情况:
             * - 处理数组cells中已经正常初始化的情况。这个操作用来处理add()方法中的条件3和条件4的情况
             * - 处理数组cells中没有初始化或者数组cells的长度为0的情况。这个操作用来处理add()方法中的条件1和条件2的情况
             * - 处理数组cells中没有初始化完成，且其余线程正在执行对数组cells的初始化操作，也就是cellBusy为1的情况。此时尝试通过CAS
             * 操作将值累加到base上
             */
            // 处理数组cells中已经正常初始化的情况
            if ((cs = cells) != null && (n = cs.length) > 0) {
                if ((c = cs[(n - 1) & h]) == null) {
                    /*
                     * 处理add()方法中的条件3。当前线程根据hash的值寻找到数组cells中指定位置为null，说明没有线程在这个位置上设置过
                     * 值，不存在争用的情况，可以直接使用。这是可以使用x值作为初始值创建一个新的cell对象，对数组cells使用cellsBusy
                     * 加锁，然后将这个新的cell对象放到位置cells[threadLocalRandomProbe % cells.length上]
                     */
                    if (cellsBusy == 0) {       // Try to attach new Cell
                        // 将需要累加的值作为初始值创建一个新的cell对象
                        Cell r = new Cell(x);   // Optimistically create
                        // 如果cellBusy值为无锁状态0，就通过casCellBusy()方法执行CAS操作将cellBusy设置为加锁状态1
                        if (cellsBusy == 0 && casCellsBusy()) {
                            try {               // Recheck under lock
                                Cell[] rs; int m, j;
                                // 重新检查数组cells不为null，数组的长度大于0,并且当前线程根据hash的值对数组cells中的指定位置为null
                                if ((rs = cells) != null &&
                                    (m = rs.length) > 0 &&
                                    rs[j = (m - 1) & h] == null) {
                                    // 将新的cell对象放置到数组cells中null的位置
                                    rs[j] = r;
                                    break done;
                                }
                            } finally {
                                // 将cellBusy设置为无锁状态0
                                cellsBusy = 0;
                            }
                            continue;           // Slot is now non-empty
                        }
                    }
                    // 这里表示cellBusy为加锁状态1，存在线程正在更改数组cells，这时的CAS操作会产生冲突，此时将collide的值设置为false
                    collide = false;
                }

                /*
                 * 如果add()方法中的条件4通过CAS操作设置数组cells中cells[threadLocalRandomProbe % cells.length]位置上的对象cell
                 * 对象的value值为v+x失败，说明存在争用的情况，这时将wasUncontended设置为true。后面重新计算一个新的probe值作为
                 * hash的值，然后重新执行循环
                 */
                else if (!wasUncontended)       // CAS already known to fail
                    // 将没有争用的标识设置为true，然后在后面重新计算一个probe值，重新执行循环
                    wasUncontended = true;      // Continue after rehash

                /*
                 * 存在新的线程参与争用的情况，处理第一次进入当前方法时threadLocalRandomProbe的值为0的情况
                 * 也就是当前线程第一次参与cell争用的CAS操作执行失败，这里尝试将x值累加到cells[threadLocalRandomProbe % cells.length]
                 * 位置的value上，如果累加成功直接退出循环
                 */
                else if (c.cas(v = c.value,
                               (fn == null) ? v + x : fn.applyAsLong(v, x)))
                    break;

                /*
                 * 如果上面一个处理线程争用时累加操作执行失败，这时如果数组cells的长度超过的最大的系统CPU内核的数量，或者是数组cells
                 * 已经完成了扩容操作，就将冲突标识collide设置为false。后面重新计算一个probe值作为hash的值，然后重新执行循环
                 */
                else if (n >= NCPU || cells != cs)
                    collide = false;            // At max size or stale

                /*
                 * 如果没有发生CAS操作的冲突，就将冲突标识collide设置为true。后面重新计算一个probe值作为hash的值。然后重新执行循环
                 */
                else if (!collide)
                    /*
                     * 设置冲突标识的值为true，表示发生了冲突。需要后面重新计算一个probe值作为hash的值，然后重新执行循环
                     * 如果重新执行循环时，仍然走到这个分支时，冲突标识collide已经设置为true，这个分支的判断条件!collide判断为
                     * false，就跳过这个分支，进入下一个分支判断执行
                     */
                    collide = true;

                /*
                 * 扩容数组cells，参与cell对象争用的线程两次都失败，并且符合扩容的条件
                 */
                else if (cellsBusy == 0 && casCellsBusy()) {
                    try {
                        // 判断数组cells是否已经进行过扩容
                        if (cells == cs)        // Expand table unless stale
                            cells = Arrays.copyOf(cs, n << 1);
                    } finally {
                        cellsBusy = 0;
                    }
                    collide = false;
                    continue;                   // Retry with expanded table
                }
                // 重新计算一个probe值作为hash的值
                h = advanceProbe(h);
            }
            // 处理数组cells已经正常初始化的情况。如果数组cells还没有初始化或者数组cells的长度为0，会首先尝试获取cellBusy锁
            else if (cellsBusy == 0 && cells == cs && casCellsBusy()) {
                try {                           // Initialize table
                    if (cells == cs) {
                        // 初始化数组cells，初始容量为2
                        Cell[] rs = new Cell[2];
                        // 根据累加的值x创建一个新的cell对象。通过hash的值和1相与h & 1，将对象放到数组cells第0个或者第1个位置上
                        rs[h & 1] = new Cell(x);
                        cells = rs;
                        break done;
                    }
                } finally {
                    // 设置锁标识cellsBusy为无锁状态0
                    cellsBusy = 0;
                }
            }
            // Fall back on using base
            // 如果以上操作都失败，就尝试将需要累加的值x累加到base上
            else if (casBase(v = base,
                             (fn == null) ? v + x : fn.applyAsLong(v, x)))
                break done;
        }
    }

    private static long apply(DoubleBinaryOperator fn, long v, double x) {
        double d = Double.longBitsToDouble(v);
        d = (fn == null) ? d + x : fn.applyAsDouble(d, x);
        return Double.doubleToRawLongBits(d);
    }

    /**
     * Same as longAccumulate, but injecting long/double conversions
     * in too many places to sensibly merge with long version, given
     * the low-overhead requirements of this class. So must instead be
     * maintained by copy/paste/adapt.
     */
    final void doubleAccumulate(double x, DoubleBinaryOperator fn,
                                boolean wasUncontended) {
        int h;
        if ((h = getProbe()) == 0) {
            ThreadLocalRandom.current(); // force initialization
            h = getProbe();
            wasUncontended = true;
        }
        boolean collide = false;                // True if last slot nonempty
        done: for (;;) {
            Cell[] cs; Cell c; int n; long v;
            if ((cs = cells) != null && (n = cs.length) > 0) {
                if ((c = cs[(n - 1) & h]) == null) {
                    if (cellsBusy == 0) {       // Try to attach new Cell
                        Cell r = new Cell(Double.doubleToRawLongBits(x));
                        if (cellsBusy == 0 && casCellsBusy()) {
                            try {               // Recheck under lock
                                Cell[] rs; int m, j;
                                if ((rs = cells) != null &&
                                    (m = rs.length) > 0 &&
                                    rs[j = (m - 1) & h] == null) {
                                    rs[j] = r;
                                    break done;
                                }
                            } finally {
                                cellsBusy = 0;
                            }
                            continue;           // Slot is now non-empty
                        }
                    }
                    collide = false;
                }
                else if (!wasUncontended)       // CAS already known to fail
                    wasUncontended = true;      // Continue after rehash
                else if (c.cas(v = c.value, apply(fn, v, x)))
                    break;
                else if (n >= NCPU || cells != cs)
                    collide = false;            // At max size or stale
                else if (!collide)
                    collide = true;
                else if (cellsBusy == 0 && casCellsBusy()) {
                    try {
                        if (cells == cs)        // Expand table unless stale
                            cells = Arrays.copyOf(cs, n << 1);
                    } finally {
                        cellsBusy = 0;
                    }
                    collide = false;
                    continue;                   // Retry with expanded table
                }
                h = advanceProbe(h);
            }
            else if (cellsBusy == 0 && cells == cs && casCellsBusy()) {
                try {                           // Initialize table
                    if (cells == cs) {
                        Cell[] rs = new Cell[2];
                        rs[h & 1] = new Cell(Double.doubleToRawLongBits(x));
                        cells = rs;
                        break done;
                    }
                } finally {
                    cellsBusy = 0;
                }
            }
            // Fall back on using base
            else if (casBase(v = base, apply(fn, v, x)))
                break done;
        }
    }

    // VarHandle mechanics
    private static final VarHandle BASE;
    private static final VarHandle CELLSBUSY;
    private static final VarHandle THREAD_PROBE;
    static {
        try {
            MethodHandles.Lookup l = MethodHandles.lookup();
            BASE = l.findVarHandle(Striped64.class,
                    "base", long.class);
            CELLSBUSY = l.findVarHandle(Striped64.class,
                    "cellsBusy", int.class);
            l = java.security.AccessController.doPrivileged(
                    new java.security.PrivilegedAction<>() {
                        public MethodHandles.Lookup run() {
                            try {
                                return MethodHandles.privateLookupIn(Thread.class, MethodHandles.lookup());
                            } catch (ReflectiveOperationException e) {
                                throw new ExceptionInInitializerError(e);
                            }
                        }});
            THREAD_PROBE = l.findVarHandle(Thread.class,
                    "threadLocalRandomProbe", int.class);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

}
