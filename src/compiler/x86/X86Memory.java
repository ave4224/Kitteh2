/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler.x86;
import compiler.type.Type;
import compiler.type.TypePointer;

/**
 *
 * @author leijurv
 */
public class X86Memory extends X86Param {
    public final X86Register reg;
    public final int offset;
    public final Type referencing;
    public X86Memory(int offset, X86Register reg, Type referencing) {
        this.offset = offset;
        this.reg = reg;
        this.referencing = referencing;
    }
    public X86Memory(X86Register reg, Type referencing) {
        this(0, reg, referencing);
    }
    @Override
    public String x86() {
        String r = reg.getRegister1(new TypePointer<>(referencing));
        if (offset == 0) {
            return "(" + r + ")";
        } else {
            return offset + "(" + r + ")";
        }
    }
    @Override
    public Type getType() {
        return referencing;
    }
    @Override
    public String toString() {
        return x86();
    }
    public boolean overlap(X86Param oth) {
        if (!(oth instanceof X86Memory)) {
            return false;
        }
        X86Memory o = (X86Memory) oth;
        if (o.reg != reg) {
            return false;
        }
        if (o.offset == offset) {
            return true;
        }
        int othBegin = o.offset;
        int othEnd = o.offset + o.referencing.getSizeBytes();
        for (int pos = offset; pos < offset + referencing.getSizeBytes(); pos++) {
            if (pos >= othBegin && pos < othEnd) {
                return true;
            }
        }
        return false;
    }
}
