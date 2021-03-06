/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler.x86;
import compiler.type.Type;
import compiler.type.TypeFloat;
import compiler.type.TypeNumerical;

/**
 *
 * @author leijurv
 */
public class X86Const extends X86Param {
    private final String value;
    private final TypeNumerical type;
    public X86Const(String value, TypeNumerical type) {
        if (value == null || type == null) {
            throw new IllegalArgumentException(value + " " + type);
        }
        this.value = value;
        this.type = type;
        if (type instanceof TypeFloat) {
            throw new IllegalStateException();
        }
    }
    @Override
    public String x86() {
        return "$" + value;
    }
    public String getValue() {
        return value;
    }
    @Override
    public Type getType() {
        return type;
    }
    @Override
    public String toString() {
        return "CONST" + type + " " + value;
    }
}
