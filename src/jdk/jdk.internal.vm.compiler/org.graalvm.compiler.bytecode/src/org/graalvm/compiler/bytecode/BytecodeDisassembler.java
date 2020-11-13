/*
 * Copyright (c) 2012, 2018, Oracle and/or its affiliates. All rights reserved.
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


package org.graalvm.compiler.bytecode;

import jdk.vm.ci.meta.*;

import static org.graalvm.compiler.bytecode.Bytecodes.*;

/**
 * Utility for producing a {@code javap}-like disassembly of bytecode.
 */
public class BytecodeDisassembler {

    /**
     * Specifies if the disassembly for a single instruction can span multiple lines.
     */
    private final boolean multiline;

    private final boolean newLine;

    public BytecodeDisassembler(boolean multiline, boolean newLine) {
        this.multiline = multiline;
        this.newLine = newLine;
    }

    public BytecodeDisassembler(boolean multiline) {
        this(multiline, true);
    }

    public BytecodeDisassembler() {
        this(true, true);
    }

    public static String disassembleOne(ResolvedJavaMethod method, int bci) {
        return new BytecodeDisassembler(false, false).disassemble(method, bci, bci);
    }

    /**
     * Disassembles the bytecode of a given method in a {@code javap}-like format.
     *
     * @return {@code null} if {@code method} has no bytecode (e.g., it is native or abstract)
     */
    public String disassemble(ResolvedJavaMethod method) {
        return disassemble(method, 0, Integer.MAX_VALUE);
    }

    /**
     * Disassembles the bytecode of a given method in a {@code javap}-like format.
     *
     * @return {@code null} if {@code method} has no bytecode (e.g., it is native or abstract)
     */
    public String disassemble(ResolvedJavaMethod method, int startBci, int endBci) {
        return disassemble(new ResolvedJavaMethodBytecode(method), startBci, endBci);
    }

    /**
     * Disassembles {@code code} in a {@code javap}-like format.
     */
    public String disassemble(Bytecode code) {
        return disassemble(code, 0, Integer.MAX_VALUE);
    }

    /**
     * Disassembles {@code code} in a {@code javap}-like format.
     */
    public String disassemble(Bytecode code, int startBci, int endBci) {
        if (code.getCode() == null) {
            return null;
        }
        ResolvedJavaMethod method = code.getMethod();
        ConstantPool cp = code.getConstantPool();
        BytecodeStream stream = new BytecodeStream(code.getCode());
        StringBuilder buf = new StringBuilder();
        int opcode = stream.currentBC();
        try {
            while (opcode != Bytecodes.END) {
                int bci = stream.currentBCI();
                if (bci >= startBci && bci <= endBci) {
                    String mnemonic = Bytecodes.nameOf(opcode);
                    buf.append(String.format("%4d: %-14s", bci, mnemonic));
                    if (stream.nextBCI() > bci + 1) {
                        decodeOperand(buf, stream, cp, method, bci, opcode);
                    }
                    if (newLine) {
                        buf.append(String.format("%n"));
                    }
                }
                stream.next();
                opcode = stream.currentBC();
            }
        } catch (Throwable e) {
            throw new RuntimeException(String.format("Error disassembling %s%nPartial disassembly:%n%s", method.format("%H.%n(%p)"), buf.toString()), e);
        }
        return buf.toString();
    }

    private void decodeOperand(StringBuilder buf, BytecodeStream stream, ConstantPool cp, ResolvedJavaMethod method, int bci, int opcode) {
        // @formatter:off
        switch (opcode) {
            case BIPUSH         : buf.append(stream.readByte()); break;
            case SIPUSH         : buf.append(stream.readShort()); break;
            case NEW            :
            case CHECKCAST      :
            case INSTANCEOF     :
            case ANEWARRAY      : {
                int cpi = stream.readCPI();
                JavaType type = cp.lookupType(cpi, opcode);
                buf.append(String.format("#%-10d // %s", cpi, type.toJavaName()));
                break;
            }
            case GETSTATIC      :
            case PUTSTATIC      :
            case GETFIELD       :
            case PUTFIELD       : {
                int cpi = stream.readCPI();
                JavaField field = cp.lookupField(cpi, method, opcode);
                String fieldDesc = field.getDeclaringClass().getName().equals(method.getDeclaringClass().getName()) ? field.format("%n:%T") : field.format("%H.%n:%T");
                buf.append(String.format("#%-10d // %s", cpi, fieldDesc));
                break;
            }
            case INVOKEVIRTUAL  :
            case INVOKESPECIAL  :
            case INVOKESTATIC   : {
                int cpi = stream.readCPI();
                JavaMethod callee = cp.lookupMethod(cpi, opcode);
                String calleeDesc = callee.getDeclaringClass().getName().equals(method.getDeclaringClass().getName()) ? callee.format("%n:(%P)%R") : callee.format("%H.%n:(%P)%R");
                buf.append(String.format("#%-10d // %s", cpi, calleeDesc));
                break;
            }
            case INVOKEINTERFACE: {
                int cpi = stream.readCPI();
                JavaMethod callee = cp.lookupMethod(cpi, opcode);
                String calleeDesc = callee.getDeclaringClass().getName().equals(method.getDeclaringClass().getName()) ? callee.format("%n:(%P)%R") : callee.format("%H.%n:(%P)%R");
                buf.append(String.format("#%-10s // %s", cpi + ", " + stream.readUByte(bci + 3), calleeDesc));
                break;
            }
            case INVOKEDYNAMIC: {
                int cpi = stream.readCPI4();
                JavaMethod callee = cp.lookupMethod(cpi, opcode);
                String calleeDesc = callee.getDeclaringClass().getName().equals(method.getDeclaringClass().getName()) ? callee.format("%n:(%P)%R") : callee.format("%H.%n:(%P)%R");
                buf.append(String.format("#%-10d // %s", cpi, calleeDesc));
                break;
            }
            case LDC            :
            case LDC_W          :
            case LDC2_W         : {
                int cpi = stream.readCPI();
                Object constant = cp.lookupConstant(cpi);
                String desc = null;
                if (constant instanceof JavaConstant) {
                    JavaConstant c = ((JavaConstant) constant);
                    desc = c.toValueString();
                } else {
                    desc = constant.toString();
                }
                if (!multiline) {
                    desc = desc.replaceAll("\\n", "");
                }
                buf.append(String.format("#%-10d // %s", cpi, desc));
                break;
            }
            case RET            :
            case ILOAD          :
            case LLOAD          :
            case FLOAD          :
            case DLOAD          :
            case ALOAD          :
            case ISTORE         :
            case LSTORE         :
            case FSTORE         :
            case DSTORE         :
            case ASTORE         : {
                buf.append(String.format("%d", stream.readLocalIndex()));
                break;
            }
            case IFEQ           :
            case IFNE           :
            case IFLT           :
            case IFGE           :
            case IFGT           :
            case IFLE           :
            case IF_ICMPEQ      :
            case IF_ICMPNE      :
            case IF_ICMPLT      :
            case IF_ICMPGE      :
            case IF_ICMPGT      :
            case IF_ICMPLE      :
            case IF_ACMPEQ      :
            case IF_ACMPNE      :
            case GOTO           :
            case JSR            :
            case IFNULL         :
            case IFNONNULL      :
            case GOTO_W         :
            case JSR_W          : {
                buf.append(String.format("%d", stream.readBranchDest()));
                break;
            }
            case LOOKUPSWITCH   :
            case TABLESWITCH    : {
                BytecodeSwitch bswitch = opcode == LOOKUPSWITCH ? new BytecodeLookupSwitch(stream, bci) : new BytecodeTableSwitch(stream, bci);
                if (multiline) {
                    buf.append("{ // " + bswitch.numberOfCases());
                    for (int i = 0; i < bswitch.numberOfCases(); i++) {
                        buf.append(String.format("%n           %7d: %d", bswitch.keyAt(i), bswitch.targetAt(i)));
                    }
                    buf.append(String.format("%n           default: %d", bswitch.defaultTarget()));
                    buf.append(String.format("%n      }"));
                } else {
                    buf.append("[" + bswitch.numberOfCases()).append("] {");
                    for (int i = 0; i < bswitch.numberOfCases(); i++) {
                        buf.append(String.format("%d: %d", bswitch.keyAt(i), bswitch.targetAt(i)));
                        if (i != bswitch.numberOfCases() - 1) {
                            buf.append(", ");
                        }
                    }
                    buf.append(String.format("} default: %d", bswitch.defaultTarget()));
                }
                break;
            }
            case NEWARRAY       : {
                int typecode = stream.readLocalIndex();
                // Checkstyle: stop
                switch (typecode) {
                    case 4:  buf.append("boolean"); break;
                    case 5:  buf.append("char"); break;
                    case 6:  buf.append("float"); break;
                    case 7:  buf.append("double"); break;
                    case 8:  buf.append("byte"); break;
                    case 9:  buf.append("short"); break;
                    case 10: buf.append("int"); break;
                    case 11: buf.append("long"); break;
                }
                // Checkstyle: resume

                break;
            }
            case MULTIANEWARRAY : {
                int cpi = stream.readCPI();
                JavaType type = cp.lookupType(cpi, opcode);
                buf.append(String.format("#%-10s // %s", cpi + ", " + stream.readUByte(bci + 3), type.toJavaName()));
                break;
            }
        }
        // @formatter:on
    }

    public static JavaMethod getInvokedMethodAt(ResolvedJavaMethod method, int invokeBci) {
        if (method.getCode() == null) {
            return null;
        }
        ConstantPool cp = method.getConstantPool();
        BytecodeStream stream = new BytecodeStream(method.getCode());
        int opcode = stream.currentBC();
        while (opcode != Bytecodes.END) {
            int bci = stream.currentBCI();
            if (bci == invokeBci) {
                if (stream.nextBCI() > bci + 1) {
                    switch (opcode) {
                        case INVOKEVIRTUAL:
                        case INVOKESPECIAL:
                        case INVOKESTATIC: {
                            int cpi = stream.readCPI();
                            JavaMethod callee = cp.lookupMethod(cpi, opcode);
                            return callee;
                        }
                        case INVOKEINTERFACE: {
                            int cpi = stream.readCPI();
                            JavaMethod callee = cp.lookupMethod(cpi, opcode);
                            return callee;
                        }
                        case INVOKEDYNAMIC: {
                            int cpi = stream.readCPI4();
                            JavaMethod callee = cp.lookupMethod(cpi, opcode);
                            return callee;
                        }
                        default:
                            throw new InternalError(BytecodeDisassembler.disassembleOne(method, invokeBci));
                    }
                }
            }
            stream.next();
            opcode = stream.currentBC();
        }
        return null;
    }

    public static int getBytecodeAt(ResolvedJavaMethod method, int invokeBci) {
        if (method.getCode() == null) {
            return -1;
        }
        BytecodeStream stream = new BytecodeStream(method.getCode());
        int opcode = stream.currentBC();
        while (opcode != Bytecodes.END) {
            int bci = stream.currentBCI();
            if (bci == invokeBci) {
                return opcode;
            }
            stream.next();
            opcode = stream.currentBC();
        }
        return -1;
    }
}
