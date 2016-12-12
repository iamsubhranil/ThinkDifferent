/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views.windows;

import core.MediaManager;
import core.PlaylistManager;
import core.Song;
import java.util.ArrayList;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import views.SceneManager;

/**
 *
 * @author Subhra
 */
public class AllSongs extends VBox {

    private static final ListView<Label> allSongs = MediaManager.getDisplay();
    private static List<Label> allLabels = MediaManager.getLabels();
    public static void refresh(){
        allLabels = MediaManager.getLabels();
        allSongs.getItems().clear();
        allSongs.getItems().addAll(allLabels);
    }
    private final Button playNow = new Button("play now");
    private final Label count;
    private final Label notFound;

    public AllSongs() {
        super();
        setSpacing(10);

        notFound = new Label("no items matched your search");
        notFound.setVisible(true);

        Button all = new Button("all songs");
        all.setBorder(Border.EMPTY);
        all.setOnAction((a)->{
            all.setDisable(true);
            refresh();
        });
        
        Button now = new Button("now playing");
        now.setDisable(true);
        now.setBorder(Border.EMPTY);
        now.setOnAction((a)->{
            now.setDisable(true);
        });
        
        createInvertibleListener(all,now);
        createInvertibleListener(now,all);
        
        Button b = new Button("add to playlist");
        b.setOnAction((e) -> {
            MediaManager.addSongsToPlaylist(allSongs);
            SceneManager.goBack();
        });

        playNow.setDisable(true);
        playNow.setOnAction((ActionEvent evt) -> {
            MediaManager.playSelectedSongs(allSongs);
            SceneManager.goBack();
        });

        Label s = new Label("search library");
        s.getStyleClass().add("item-title");

        TextField ser = new TextField();
        ser.textProperty().addListener((o2, o, n) -> {
            getChildren().remove(notFound);
            if (n != null && !n.equals("")) {
                search(n);
            } else {
                allSongs.getItems().clear();
                allSongs.getItems().addAll(allLabels);
            }
        });

        BorderPane bp1 = new BorderPane();
        bp1.setLeft(b);
        bp1.setRight(playNow);

        count = new Label("listing songs");
        count.getStyleClass().add("item-title");
        BorderPane m = new BorderPane();
        m.setTop(count);
        m.setLeft(new HBox(all,now));
        m.setRight(bp1);

        count.setText(MediaManager.getAllSongs().length + " songs");

        BorderPane bp = new BorderPane();
       
        bp.setLeft(new VBox(new Button("Artist")));
        bp.setCenter(allSongs);
        
        getChildren().addAll(m, s, ser, bp);

        MediaManager.registerPlayButtonForSongsSelection(playNow, allSongs);
    }
    
    private void createInvertibleListener(Button b1,Button b2){
        b1.disableProperty().addListener((a,b,n)->{
            b2.setDisable(!n);
        });
    }
    
    
    private void showNowPlayings(){
        List<Song> nowSongs = PlaylistManager.getCurrentPlaylist().getAllSongs();
        allSongs.getItems().clear();
        
    }

    private void search(String text) {
        allSongs.getItems().clear();
        List<Label> found = new ArrayList<>();
        allLabels.stream().forEach((l) -> {
            String org = l.getText();
            String low = org.toLowerCase();
            String up = org.toUpperCase();
            if (low.contains(text) || up.contains(text)) {
                found.add(l);
            }
        });
        if (found.isEmpty()) {
            allSongs.setVisible(false);
            getChildren().add(notFound);
        } else {
            allSongs.setVisible(true);
            allSongs.getItems().addAll(found);
        }
    }

}
