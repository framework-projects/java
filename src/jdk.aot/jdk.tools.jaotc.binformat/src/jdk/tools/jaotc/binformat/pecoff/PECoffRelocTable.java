/*
 * Copyright (c) 2017, 2018, Oracle and/or its affiliates. All rights reserved.
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
 */



package jdk.tools.jaotc.binformat.pecoff;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import jdk.tools.jaotc.binformat.pecoff.PECoff.IMAGE_RELOCATION;

final class PECoffRelocTable {
    ArrayList<ArrayList<PECoffRelocEntry>> relocEntries;

    PECoffRelocTable(int numsects) {
        relocEntries = new ArrayList<>(numsects);
        for (int i = 0; i < numsects; i++) {
            relocEntries.add(new ArrayList<PECoffRelocEntry>());
        }
    }

    void createRelocationEntry(int sectindex, int offset, int symno, int type) {
        PECoffRelocEntry entry = new PECoffRelocEntry(offset, symno, type);
        relocEntries.get(sectindex).add(entry);
    }

    static int getAlign() {
        return (4);
    }

    int getNumRelocs(int sectionIndex) {
        return relocEntries.get(sectionIndex).size();
    }

    // Return the relocation entries for a single section
    // or null if no entries added to section
    byte[] getRelocData(int sectionIndex) {
        ArrayList<PECoffRelocEntry> entryList = relocEntries.get(sectionIndex);
        int entryCount = entryList.size();
        int allocCount = entryCount;

        if (entryCount == 0) {
            return null;
        }
        if (entryCount > 0xFFFF) {
            allocCount++;
        }
        ByteBuffer relocData = PECoffByteBuffer.allocate(allocCount * IMAGE_RELOCATION.totalsize);

        // If number of relocs exceeds 65K, add the real size
        // in a dummy first reloc entry
        if (entryCount > 0xFFFF) {
            PECoffRelocEntry entry = new PECoffRelocEntry(allocCount, 0, 0);
            relocData.put(entry.getArray());
        }

        // Copy each entry to a single ByteBuffer
        for (int i = 0; i < entryCount; i++) {
            PECoffRelocEntry entry = entryList.get(i);
            relocData.put(entry.getArray());
        }

        return (relocData.array());
    }
}
