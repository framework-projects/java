/*
 * Copyright (c) 2016, Oracle and/or its affiliates. All rights reserved.
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

package jdk.tools.jlink.internal;

import java.io.*;
import java.util.Objects;

/**
 * A ResourcePoolEntry backed by a given byte[].
 */
class ByteArrayResourcePoolEntry extends AbstractResourcePoolEntry {
    private final byte[] buffer;

    /**
     * Create a new ByteArrayResourcePoolEntry.
     *
     * @param module The module name.
     * @param path The data path identifier.
     * @param type The data type.
     * @param buf  The byte buffer.
     */
    ByteArrayResourcePoolEntry(String module, String path, Type type, byte[] buffer) {
        super(module, path, type);
        this.buffer = Objects.requireNonNull(buffer);
    }

    @Override
    public byte[] contentBytes() {
        return buffer.clone();
    }

    @Override
    public InputStream content() {
        return new ByteArrayInputStream(buffer);
    }

    @Override
    public void write(OutputStream out) {
        try {
            out.write(buffer);
        } catch (IOException ex) {
            throw new UncheckedIOException(ex);
        }
    }

    @Override
    public long contentLength() {
        return buffer.length;
    }
}
