/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler.tac.optimize;
import compiler.Context.VarInfo;
import compiler.tac.TACCast;
import compiler.tac.TACConst;
import compiler.tac.TACStatement;
import compiler.type.TypeFloat;
import compiler.type.TypeNumerical;
import compiler.x86.X86Const;
import java.util.List;

/**
 * tmp0 = CONSTint 5
 *
 * tmp2 = (byte) tmp0
 *
 * gets replaced with "tmp2=CONSTbyte 5"
 *
 * @author leijurv
 */
public class ConstantCasting extends TACOptimization {
    @Override
    protected void run(List<TACStatement> block, int blockBegin) {
        for (int i = 0; i < block.size() - 1; i++) {
            if (block.get(i) instanceof TACConst) {
                TACConst con = (TACConst) block.get(i);
                if (!(con.params[0] instanceof X86Const)) {
                    continue;
                }
                if (!(con.params[1] instanceof VarInfo)) {
                    continue;
                }
                if (!UselessTempVars.isTempVariable(((VarInfo) con.params[1]).getName())) {
                    continue;
                }
                if (block.get(i + 1) instanceof TACCast) {
                    TACCast cast = (TACCast) block.get(i + 1);
                    if (cast.params[0].equals(con.params[1])) {//cast source must equal const destination
                        TypeNumerical castingTo = (TypeNumerical) cast.params[1].getType();//cast destination type
                        if (castingTo instanceof TypeFloat) {
                            continue;//lol its not like you can do: movss $5, %xmm1
                        }
                        X86Const constantBeingCasted = (X86Const) con.params[0];
                        X86Const casted = new X86Const(constantBeingCasted.getValue(), castingTo);
                        con.replace(con.params[0], casted);//replace source with: the constant, now with the correct casted type
                        con.replace(con.params[1], cast.params[1]);//replace dest with: the destination of the original cast
                        block.remove(i + 1);
                        i--;
                    }
                }
            }
        }
    }
}
