/*
 * Copyright (c) 2000, 2018, Oracle and/or its affiliates. All rights reserved.
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
 */

package sun.jvm.hotspot.utilities;

import java.nio.charset.Charset;

/** A utility class encapsulating useful operations on C strings
    represented as Addresses */

public class CStringUtilities {
  /** Return the length of a null-terminated ASCII string in the
      remote process */
  public static int getStringLength(Address addr) {
    int i = 0;
    while (addr.getCIntegerAt(i, 1, false) != 0) {
      i++;
    }
    return i;
  }

  private static String encoding = System.getProperty("file.encoding", "US-ASCII");

  public static String getString(Address addr) {
    return getString(addr, Charset.forName(encoding));
  }

  /** Fetch a null-terminated ASCII string from the remote process.
      Returns null if the argument is null, otherwise returns a
      non-null string (for example, returns an empty string if the
      first character fetched is the null terminator). */
  public static String getString(Address addr, Charset charset) {
    if (addr == null) {
      return null;
    }

    List data = new ArrayList();
    byte val = 0;
    long i = 0;
    do {
      val = (byte) addr.getCIntegerAt(i, 1, false);
      if (val != 0) {
        data.add(new Byte(val));
      }
      ++i;
    } while (val != 0);

    // Convert to byte[] and from there to String
    byte[] bytes = new byte[data.size()];
    for (i = 0; i < data.size(); ++i) {
      bytes[(int) i] = ((Byte) data.get((int) i)).byteValue();
    }
    // FIXME: When we switch to use JDK 6 to build SA,
    // we can change the following to just return:
    // return new String(bytes, Charset.defaultCharset());
    return new String(bytes, charset);
  }
}
