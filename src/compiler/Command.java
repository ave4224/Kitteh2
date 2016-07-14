/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;

/**
 *
 * @author leijurv
 */
public abstract class Command {
    public abstract void generateTAC(Context context, IREmitter emit);
    protected abstract int calculateTACLength();
    private Integer taclen = null;
    public int getTACLength() {
        if (taclen == null) {
            taclen = calculateTACLength();
        }
        return taclen;
    }
}
