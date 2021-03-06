/*
 * Copyright (c) 2010, 2013, Oracle and/or its affiliates. All rights reserved.
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
 */
/*
   Copyright 2009-2013 Attila Szegedi

   Redistribution and use in source and binary forms, with or without
   modification, are permitted provided that the following conditions are
   met:
   * Redistributions of source code must retain the above copyright
     notice, this list of conditions and the following disclaimer.
   * Redistributions in binary form must reproduce the above copyright
     notice, this list of conditions and the following disclaimer in the
     documentation and/or other materials provided with the distribution.
   * Neither the name of the copyright holder nor the names of
     contributors may be used to endorse or promote products derived from
     this software without specific prior written permission.

   THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
   IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
   TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A
   PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL COPYRIGHT HOLDER
   BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
   CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
   SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR
   BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
   WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
   OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
   ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/

package jdk.dynalink;

import jdk.dynalink.linker.ConversionComparator.Comparison;
import jdk.dynalink.linker.*;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;
import java.util.Objects;
import java.util.function.Supplier;

/**
 * Default implementation of the {@link LinkerServices} interface.
 */
final class LinkerServicesImpl implements LinkerServices {
    private static final ThreadLocal<SecureLookupSupplier> threadLookupSupplier = new ThreadLocal<>();

    private final TypeConverterFactory typeConverterFactory;
    private final GuardingDynamicLinker topLevelLinker;
    private final MethodHandleTransformer internalObjectsFilter;

    /**
     * Creates a new linker services object.
     *
     * @param typeConverterFactory the type converter factory exposed by the services.
     * @param topLevelLinker the top level linker used by the services.
     * @param internalObjectsFilter a method handle transformer that is supposed to act as the implementation of this
     * services' {@link #filterInternalObjects(java.lang.invoke.MethodHandle)} method.
     */
    LinkerServicesImpl(final TypeConverterFactory typeConverterFactory,
            final GuardingDynamicLinker topLevelLinker, final MethodHandleTransformer internalObjectsFilter) {
        this.typeConverterFactory = typeConverterFactory;
        this.topLevelLinker = topLevelLinker;
        this.internalObjectsFilter = internalObjectsFilter;
    }

    @Override
    public boolean canConvert(final Class<?> from, final Class<?> to) {
        return typeConverterFactory.canConvert(from, to);
    }

    @Override
    public MethodHandle asType(final MethodHandle handle, final MethodType fromType) {
        return typeConverterFactory.asType(handle, fromType);
    }

    @Override
    public MethodHandle getTypeConverter(final Class<?> sourceType, final Class<?> targetType) {
        return typeConverterFactory.getTypeConverter(sourceType, targetType);
    }

    @Override
    public Comparison compareConversion(final Class<?> sourceType, final Class<?> targetType1, final Class<?> targetType2) {
        return typeConverterFactory.compareConversion(sourceType, targetType1, targetType2);
    }

    /**
     * Used to marshal a checked exception out of Supplier.get() in getGuardedInvocation.
     */
    private static class LinkerException extends RuntimeException {
        private static final long serialVersionUID = 1L;

        public LinkerException(final Exception cause) {
            super(null, cause, true, false);
        }
    }

    @Override
    public GuardedInvocation getGuardedInvocation(final LinkRequest linkRequest) throws Exception {
        try {
            return getWithLookupInternal(() -> {
                try {
                    return topLevelLinker.getGuardedInvocation(linkRequest, this);
                } catch (final RuntimeException e) {
                    throw e;
                } catch (final Exception e) {
                    throw new LinkerException(e);
                }
            }, linkRequest.getCallSiteDescriptor());
        } catch (final LinkerException e) {
            throw (Exception)e.getCause();
        }
    }

    @Override
    public MethodHandle filterInternalObjects(final MethodHandle target) {
        return internalObjectsFilter != null ? internalObjectsFilter.transform(target) : target;
    }

    @Override
    public <T> T getWithLookup(final Supplier<T> operation, final SecureLookupSupplier lookupSupplier) {
        return getWithLookupInternal(
                Objects.requireNonNull(operation, "action"),
                Objects.requireNonNull(lookupSupplier, "lookupSupplier"));
    }

    private static <T> T getWithLookupInternal(final Supplier<T> operation, final SecureLookupSupplier lookupSupplier) {
        final SecureLookupSupplier prevLookupSupplier = threadLookupSupplier.get();
        final boolean differ = prevLookupSupplier != lookupSupplier;
        if (differ) {
            threadLookupSupplier.set(lookupSupplier);
        }
        try {
            return operation.get();
        } finally {
            if (differ) {
                threadLookupSupplier.set(prevLookupSupplier);
            }
        }
    }

    static Lookup getCurrentLookup() {
        final SecureLookupSupplier lookupSupplier = threadLookupSupplier.get();
        if (lookupSupplier != null) {
            return lookupSupplier.getLookup();
        }
        return MethodHandles.publicLookup();
    }
}
