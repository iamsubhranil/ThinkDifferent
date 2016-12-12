/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views.windows;

import core.Heading;
import java.io.File;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import views.SceneManager;

/**
 *
 * @author Subhra
 */
public class SpecifiedLocations extends VBox {

    public SpecifiedLocations(File f) {
        super();
        setSpacing(10);
        Heading h = new Heading("locations in " + f.toString());

        Label sr = new Label("add location");
        sr.getStyleClass().add("item-title");

        Label des = new Label("type a location, or press browse");
        des.setWrapText(true);
        des.getStyleClass().add("item-title");

        Button brs = new Button("browse");

        FlowPane fp = new FlowPane(des, brs);
        fp.setHgap(10);

        TextField tf = new TextField();

        Label stat = new Label();
        stat.getStyleClass().add("item-title");
        stat.setWrapText(true);

        Button save = new Button("add location");
        save.setOnAction((ActionEvent evt) -> {
            SearchManager.addLocation(new File(f + tf.getText()));
            stat.setText("Added");
            SceneManager.goBack();
        });

        Label fixed = new Label(f.toString() + " ");

        tf.textProperty().addListener((obs, o, n) -> {
            File nf = new File(f + n);
            if (!nf.exists()) {
                stat.setText("directory does not exist");
                save.setDisable(true);
            } else if (!nf.isDirectory()) {
                stat.setText("you can't use a file as a location");
                save.setDisable(true);
            } else if (SearchManager.hasAlready(nf)) {
                stat.setText("we already have this directory as a search location");
                save.setDisable(true);
            } else if (SearchManager.hasParentAlready(nf)) {
                stat.setText("we already have it's parent directory as search location. We'll automatically sear"
                        + "ch this directory when we'll peek into that");
                save.setDisable(true);
            } else {
                stat.setText("");
                save.setDisable(false);
            }
        });

        BorderPane bp = new BorderPane();

        bp.setRight(save);

        BorderPane bp2 = new BorderPane();
        bp2.setLeft(fixed);
        bp2.setCenter(tf);

        getChildren().addAll(h, designLocs(f), sr, des, fp, bp2, stat, bp);

    }

    private ScrollPane designLocs(File f) {

        List<File> locs = SearchManager.getAllowedLocationsInDrive(f);

        VBox vb = new VBox();
        vb.setSpacing(10);
        vb.getStyleClass().add("background");
        if (!locs.isEmpty()) {
            locs.stream().map((loc) -> {
                System.out.println("Got from manager : " + loc);
                return loc;
            }).map((loc) -> new FileCheckBox(loc)).forEach((fcb) -> {
                vb.getChildren().add(fcb);
            });
        } else {
            vb.getChildren().add(new Label("no location specified"));
        }

        ScrollPane sp = new ScrollPane(vb);
        sp.getStyleClass().add("background");
        sp.setFitToWidth(true);
        sp.setFitToHeight(true);

        return sp;
    }

    class FileCheckBox extends CheckBox {

        public FileCheckBox(File text) {
            super(text.toString());
            setUserData(text);
            setSelected(true);
            addListener();

        }

        private void addListener() {
            selectedProperty().addListener((obs, o, n) -> {
                File toSend = (File) getUserData();
                System.out.println("Sending " + toSend + " to SearchManager..");
                if (n && !SearchManager.hasAlready(toSend)) {
                    SearchManager.addLocation((File) getUserData());
                } else {
                    SearchManager.removeLocation(toSend);
                }
            });
        }
    }

}
