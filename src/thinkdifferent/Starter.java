/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package thinkdifferent;

import java.io.File;
import javafx.application.Application;
import javafx.stage.Stage;
import views.SceneManager;
import views.windows.Loader;
import views.windows.MetaRetrieverDisplay;

/**
 *
 * @author Subhra
 */
public class Starter extends Application {
    

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        System.setProperty("prism.lcdtext", "false");
        launch(args);
        if(args.length>0){
            for(String arg : args){
                File f = new File(arg);
                System.out.println(f);
            }
      //      MetaRetrieverDisplay.setFolders(args);
        }
    }
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("LaUD");
        SceneManager.setStage(primaryStage, new Loader());
    }
    
}
