/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler.x86;

/**
 *
 * @author leijurv
 */
public class Comment extends X86Statement {
    private final String data;
    public Comment(String data) {
        this.data = "#" + data;
    }
    @Override
    public String toString() {
        return data;
    }
}
