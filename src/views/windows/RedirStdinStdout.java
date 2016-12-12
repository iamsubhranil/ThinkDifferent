/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views.windows;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import javafx.scene.control.TextArea;

/**
 *
 * @author Subhra
 */
public class RedirStdinStdout {
    
    private static final TextArea printer = new TextArea();
    
    private static final OutputStream ps = new TextAreaOutputStream(printer);
    
    public static void registerOutput(){
        System.setOut(new PrintStream(ps));
    }
    
    public static TextArea getOut(){
        return printer;
    }
    

/**
 *
 * @author Subhra
 */
static class TextAreaOutputStream extends OutputStream {

    private final TextArea slate;

    public TextAreaOutputStream(TextArea jt) {
        slate = jt;
    }

    @Override
    public void write(int b) throws IOException {

        slate.appendText(String.valueOf((char) b));

    }

}

    
}
