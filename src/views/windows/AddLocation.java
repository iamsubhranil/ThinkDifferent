/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views.windows;

import core.Heading;
import java.io.File;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import views.SceneManager;

/**
 *
 * @author Subhra
 */
public class AddLocation extends VBox {

    public AddLocation() {
        super();
        setSpacing(10);
        Heading h = new Heading("before we continue");

        Label l = new Label("add atleast one directory where we can peek in for songs");
        l.getStyleClass().add("item-title");
        l.setWrapText(true);

        TextField tf = new TextField();

        Label statLabel = new Label();
        statLabel.getStyleClass().add("item-title");

        Button cn = new Button("continue");
        cn.setDisable(true);
        cn.setOnAction((ActionEvent evt) -> {
            SearchManager.addLocation(new File(tf.getText()));
            SceneManager.hideBackButton();
            SceneManager.swapToNext(new MetaRetrieverDisplay());
        });

        tf.textProperty().addListener((obs, o, n) -> {
            File f = new File(n);
            if (!f.exists()) {
                statLabel.setText("directory does not exist");
                cn.setDisable(true);
            } else if (!f.isDirectory()) {
                statLabel.setText("you can't select a file");
                cn.setDisable(true);
            } else {
                statLabel.setText("");
                cn.setDisable(false);
            }
        });

        getChildren().addAll(h, l, tf, statLabel, cn);
    }

    private boolean hasMP3(File f) {
        for (String f2 : f.list()) {
            File f3 = new File(f + f2);
            if (f3.isDirectory()) {
                if (hasMP3(f3)) {
                    return true;
                }
            } else if (f3.getName().endsWith(".mp3")) {
                return true;
            }
        }
        return false;
    }

}
