/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views.windows;

import java.io.Serializable;
import javafx.beans.property.SimpleStringProperty;

/**
 *
 * @author Subhra
 */
public class SerializableSimpleStringProperty implements Serializable{
    private static final long serialVersionUID = 3727782286658898193L;
    
    
    private static SimpleStringProperty base;
    private String val;
    
    public SerializableSimpleStringProperty(SimpleStringProperty s){
        s.addListener((o,o2,n)->{
            val = n;
        });
        base = s;
    }
    
    public SimpleStringProperty getProp(){
        return base;
    }
    
    public String getValue(){
        return val;
    }
    
}
