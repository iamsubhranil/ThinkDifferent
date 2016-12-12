/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views.windows;

import core.DataLoader;
import core.Heading;
import core.MediaManager;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import views.SceneManager;

/**
 *
 * @author Subhra
 */
public class Loader extends VBox {

    public Loader() {
        super();
        setSpacing(10);
        Heading h = new Heading("just a minute");

        Label l = new Label("have patience while we load your preferences");

        BorderPane bp = new BorderPane();

        Label statLabel = new Label("please wait..");

        statLabel.getStyleClass().add("item-title");

        bp.setCenter(statLabel);

        getChildren().addAll(h, l, bp);

        statLabel.textProperty().addListener((obs, o, n) -> {
            statLabel.setText(n);
        });

        load(statLabel.textProperty());

    }

    private void load(StringProperty msg) {

        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    sleep(1000);
                    DataLoader.loadAll(msg, () -> {
                        SceneManager.hideBackButton();
                        if(MediaManager.getAllSongs().length==0){
                                SceneManager.swapToNext(new AddLocation());
                        }
                        else {
                            NowPlaying n = new NowPlaying();
                            SceneManager.swapToNext(n);
                            MediaManager.setNowPlaying(n);
                            n.restoreState();
                        }
                    });
                } catch (InterruptedException ex) {
                    Logger.getLogger(Loader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        };

        t.start();

    }

}
