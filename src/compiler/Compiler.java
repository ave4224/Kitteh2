/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package compiler;
import java.io.File;
import java.nio.file.Files;
import java.util.ArrayList;

/**
 *
 * @author leijurv
 */
public class Compiler {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        byte[] program = Files.readAllBytes(new File("/Users/leijurv/Documents/fizzbuzz").toPath());
        ArrayList<String> k = Preprocessor.preprocess(new String(program));
        ArrayList<Object> lol = new ArrayList<>();
        for (String l : k) {
            lol.add(l);
        }
        ArrayList<Command> commands = Processor.parse(lol, new Context());
        System.out.println(commands);
        IREmitter emit = new IREmitter();
        for (Command com : commands) {
            com.generateTAC(null, emit);
        }
        for (int i = 0; i < emit.result.size(); i++) {
            System.out.println(i + ":     " + emit.result.get(i));
        }
    }
}
