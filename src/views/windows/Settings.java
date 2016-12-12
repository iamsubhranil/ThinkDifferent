/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views.windows;

import core.Heading;
import core.MediaManager;
import java.io.File;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioEqualizer;
import javafx.scene.media.EqualizerBand;
import javafx.scene.media.MediaPlayer;
import views.SceneManager;

/**
 *
 * @author Subhra
 */
public class Settings extends VBox {

    private final MediaPlayer mp = MediaManager.getMediaPlayer();

    public Settings() {
        super();
        setSpacing(10);
        Heading h = new Heading("set your preferences");

        getChildren().add(h);

        BorderPane bp = new BorderPane();
        bp.setLeft(volumeController());
        bp.setCenter(equalizationController());
        bp.setRight(balanceController());

        getChildren().addAll(bp, locationControl());

    }

    private Pane volumeController() {
        Label title = new Label("volume");
        title.getStyleClass().add("item-title");

        Slider volume = new Slider(0, 100, MediaManager.getVolume() * 100);
        volume.setOrientation(Orientation.VERTICAL);

        Label vol = new Label();
        vol.setText(String.format("%.0f", volume.getValue()));
        vol.getStyleClass().add("item-title");
        volume.valueProperty().addListener((obs, o, n) -> {
            double val = n.doubleValue();
            MediaManager.setVolume(val / 100);
            vol.setText(String.format("%.0f", val));
            MediaManager.saveVolBal();
        });

        VBox vb1 = new VBox(vol, volume);
        vb1.setAlignment(Pos.CENTER);

        VBox tot = new VBox(title, vb1);
        tot.setSpacing(10);

        return tot;
    }

    private Pane equalizationController() {
        if (mp != null) {
            Label l = new Label("equalizer");
            l.getStyleClass().add("item-title");

            AudioEqualizer eq = mp.getAudioEqualizer();
            ObservableList<EqualizerBand> oeq = eq.getBands();
            FlowPane fp = new FlowPane();
            fp.setHgap(20);
            fp.setVgap(10);
            oeq.stream().forEach((a) -> {
                double low = -24.0;
                double high = 12.0;
                double val = a.getGain();
                Slider s = new Slider(low, high, val);
                s.setOrientation(Orientation.VERTICAL);
                s.valueProperty().addListener((obs, o, n) -> {
                    a.setGain(n.doubleValue());
                    MediaManager.setEqualizer(eq);
                });
                fp.getChildren().add(s);
            });
            fp.setAlignment(Pos.CENTER);

            VBox vb = new VBox(l, fp);
            vb.setAlignment(Pos.CENTER);
            vb.setSpacing(10);
            return vb;
        }
        return null;
    }

    private Pane balanceController() {
        double bal = MediaManager.getBalance();

        Label tit = new Label("balance");
        tit.getStyleClass().add("item-title");

        Slider sbal = new Slider(-100, 100, -bal * 100);
        sbal.getStyleClass().add("slider-red");
        Label lbal = new Label((int) sbal.getValue() + "");
        lbal.getStyleClass().add("item-title");

        sbal.valueProperty().addListener((obs, o, n) -> {
            MediaManager.setBalance(-n.doubleValue() / 100);
            lbal.setText(String.format("%.0f", n.doubleValue()));
            MediaManager.saveVolBal();
        });

        VBox vb = new VBox(tit, lbal, sbal);
        vb.setSpacing(10);
        vb.setAlignment(Pos.CENTER);

        return vb;
    }

    private Pane locationControl() {
        Label l = new Label("search and library");
        l.getStyleClass().add("item-title");
        ExclusiveCheckBox c1 = new ExclusiveCheckBox("Search only the specified locations for songs");
        ExclusiveCheckBox c2 = new ExclusiveCheckBox("Search my entire PC for songs");
        c1.unselectedProperty().bindBidirectional(c2.selectedProperty());

        VBox locs = new VBox();
        c1.selectedProperty().addListener((obs, o, n) -> {
            locs.setVisible(n);

        });
        locs.setSpacing(10);
        Label l2 = new Label("available locations");
        l2.getStyleClass().add("item-title");
        File[] lo = File.listRoots();
        for (File loc1 : lo) {
            if (loc1.getTotalSpace() > 0) {
                CheckBox c = new CheckBox(loc1.toString());
                Button b = new Button("Specify locations");
                b.setOnAction((ActionEvent evt) -> {
                    SceneManager.swapToNext(new SpecifiedLocations(loc1));
                });
                c.selectedProperty().addListener((obs, o, n) -> {
                    b.setVisible(o);
                });
                if (SearchManager.hasAlready(loc1)) {
                    c.setSelected(true);
                }
                else if(SearchManager.hasParentAlready(loc1)){
                    c.setIndeterminate(true);
                }
                else{
                    c.setSelected(false);
                }
                FlowPane fp = new FlowPane(c, b);
                fp.setHgap(10);
                locs.getChildren().add(fp);
            }

        }

        VBox vb = new VBox(l, c1, locs, c2);
        vb.setSpacing(10);
        return vb;
    }

    class ExclusiveCheckBox extends CheckBox {

        private SimpleBooleanProperty unselectedProperty = new SimpleBooleanProperty(true);

        public ExclusiveCheckBox(String text) {
            super(text);

            selectedProperty().addListener((obs, o, n) -> {
                unselectedProperty.setValue(o);
            });
            unselectedProperty.addListener((obs, o, n) -> {
                setSelected(o);
            });
        }

        public SimpleBooleanProperty unselectedProperty() {
            return unselectedProperty;
        }
    }

}
