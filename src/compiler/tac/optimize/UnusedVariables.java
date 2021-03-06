/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler.tac.optimize;
import compiler.Context.VarInfo;
import compiler.tac.TACConst;
import compiler.tac.TACStatement;
import compiler.tac.TempVarUsage;
import compiler.type.TypeStruct;
import compiler.x86.X86Param;
import compiler.x86.X86TypedRegister;
import java.util.List;

/**
 * note that this is a different optimization than unusedassignment. this one
 * only applies to tacconsts and it follows jumps (conditional or otherwise) to
 * check all locations a variable might be used. this one only looks for
 * variables that are set and unused, NOT for variables that are set and reset
 * (that's unusedassignment's job)
 *
 * x=y
 *
 * ... x isn't used no matter which jumps are followed or not followed
 *
 * end of function
 *
 * "x=y" can be removed no matter whether x is a tmp var or not
 *
 * @author leijurv
 */
public class UnusedVariables extends TACOptimization {
    @Override
    protected void run(List<TACStatement> block, int blockBegin) {
        for (int i = 0; i < block.size(); i++) {
            TACStatement ts = block.get(i);
            int pos = blockBegin + i;
            if (ts instanceof TACConst) {
                X86Param dest = ts.params[1];
                if (dest instanceof X86TypedRegister) {
                    continue;
                }
                if (dest instanceof VarInfo && ((VarInfo) dest).getName().contains(TempVarUsage.TEMP_STRUCT_FIELD_INFIX)) {
                    continue;//if you comment out this line, the tests fail.
                }
                if (ts.params[1].getType() instanceof TypeStruct) {
                    continue;
                }
                if (!isUsedAtOrAfter(pos, dest)) {
                    block.remove(i);
                    return;
                }
            }
        }
    }
}
