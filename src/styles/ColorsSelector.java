package styles;


import java.awt.BorderLayout;
import javax.swing.JColorChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Subhra
 */
public class ColorsSelector extends JPanel{
    
    
    public static void sho(){
        JFrame jf = new JFrame();
        jf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jf.add(new ColorsSelector());
        jf.setVisible(true);
        jf.pack();
    }
    
    public static void main(String [] args){
        SwingUtilities.invokeLater(()->{
            sho();
        });
        
    }
    public ColorsSelector(){
        super();
        
        JColorChooser jcs = new JColorChooser();
        
        setLayout(new BorderLayout());
        
        add(jcs,"Center");
    }
    
}
