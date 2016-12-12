/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views.windows;

import core.Heading;
import java.util.LinkedList;
import java.util.Queue;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.util.Duration;
import views.SceneManager;

/**
 *
 * @author Subhra
 */
public class Welcome extends BorderPane{
    
    public Welcome(){
        super();
        
        Heading h = new Heading("hi there");
        
        Queue<String> texts = new LinkedList();
        texts.add("Welcome to Think Different.");
        texts.add("We believe in that.");
        texts.add("And we'll make you do the same.");
        texts.add("Trust us.");
        texts.add("Better, feel it yourself.");
        texts.add("Think Different.");
        
        Queue<Label> labels = new LinkedList();
        while(!texts.isEmpty()){
            Label label = new Label(texts.poll());
            labels.add(label);
        }
        
        FlowPane sp = new FlowPane();
        
        Queue<ParallelTransition> qept = new LinkedList();
        while(!labels.isEmpty()){
            Label tl = labels.poll();
            BorderPane bp = new BorderPane(tl);
            sp.getChildren().add(bp);
            if(labels.size()>1){
            ParallelTransition ept = SceneManager.createExitAni(bp);
            ept.setDelay(Duration.millis(100));
            qept.add(ept);}
        }
        
        SequentialTransition st = new SequentialTransition();
        st.getChildren().addAll(qept);
        st.setDelay(Duration.millis(5000));
        st.setOnFinished((ActionEvent evt)->{
            System.out.println("done");
            SceneManager.swapToNext(new NowPlaying());
        });
        
        setTop(h);
        setCenter(sp);
        st.play();
    }
    
}
