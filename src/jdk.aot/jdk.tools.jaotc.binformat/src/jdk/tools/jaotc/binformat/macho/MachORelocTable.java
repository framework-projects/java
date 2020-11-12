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



package jdk.tools.jaotc.binformat.macho;

import java.nio.ByteBuffer;
import java.util.ArrayList;

import jdk.tools.jaotc.binformat.macho.MachO.reloc_info;

final class MachORelocTable {
    private final ArrayList<ArrayList<MachORelocEntry>> relocEntries;
    int fileOffset;

    MachORelocTable(int numsects) {
        relocEntries = new ArrayList<>(numsects);
        for (int i = 0; i < numsects; i++) {
            relocEntries.add(new ArrayList<MachORelocEntry>());
        }
    }

    void createRelocationEntry(int sectindex, int offset, int symno, int pcrel, int length, int isextern, int type) {
        MachORelocEntry entry = new MachORelocEntry(offset, symno, pcrel, length, isextern, type);
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
        ArrayList<MachORelocEntry> entryList = relocEntries.get(sectionIndex);

        if (entryList.size() == 0) {
            return null;
        }
        ByteBuffer relocData = MachOByteBuffer.allocate(entryList.size() * reloc_info.totalsize);

        // Copy each entry to a single ByteBuffer
        for (int i = 0; i < entryList.size(); i++) {
            MachORelocEntry entry = entryList.get(i);
            relocData.put(entry.getArray());
        }

        return (relocData.array());
    }
}
