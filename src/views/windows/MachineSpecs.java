/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views.windows;

import core.Heading;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Properties;
import java.util.Queue;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 *
 * @author Subhra
 */
public class MachineSpecs extends VBox{
    
    public MachineSpecs(){
        super();
        
        Heading h = new Heading("system properties");
        
        getChildren().addAll(h);
        getChildren().addAll(showSpecs());
        
        
        
    }
    
    private Queue<Node> showSpecs(){
        Properties p = System.getProperties();
        Enumeration<Object> p2 = p.elements();
        Enumeration<Object> p3 = p.keys();
        Queue<Node> ql = new LinkedList();
        while(p2.hasMoreElements()){
            Label l1 = new Label((String)p3.nextElement());
            l1.getStyleClass().add("item-title");
            Label l2 = new Label((String)p2.nextElement());
            ql.add(l1);
            ql.add(l2);
        }
        return ql;
    }
    
    
}
