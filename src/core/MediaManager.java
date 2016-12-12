/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.media.AudioEqualizer;
import javafx.scene.media.EqualizerBand;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import views.windows.NowPlaying;
import views.windows.SerializableSimpleStringProperty;

/**
 *
 * @author Subhra
 */
public class MediaManager {

    private static MediaPlayer mediaPlayer;
    private static AudioEqualizer eq;
    private static EqualizerBand[] bands;
    private static double vol = -1;
    private static double bal = -90;
    private static Playlist playlist;
    private static NowPlaying np;
    private static ListView<Label> lv;
    private static Song currentSong;
    private static double duration;
    private static final Queue<SerializableSimpleStringProperty> vars = new LinkedList();
    private static final List<String> allPaths = new ArrayList<>();
    private static final List<String> allTitlesBack = new ArrayList<>();
    private static final List<Song> allSongs = new ArrayList<>();
    private static final List<Song> artists = new ArrayList<>();
    private static final List<Song> albums = new ArrayList<>();
    private static final List<Label> allLabels = new ArrayList<>();
    private static int noOfSongs = 0;
    private static int totalSongsLock = 0;

    public static MediaPlayer getMediaPlayer() {
        return mediaPlayer;
    }

    public static void setNowPlaying(NowPlaying n) {
        np = n;
    }

    public static double getVolume() {
        return vol;
    }

    public static double getBalance() {
        return bal;
    }

    public static void setMediaPlayer(MediaPlayer mp) {
        mediaPlayer = mp;
        if (vol == -1) {
            vol = mp.getVolume();
        } else {
            mp.setVolume(vol);
        }
        if (bal == -90) {
            bal = mp.getBalance();
        } else {
            mp.setBalance(bal);
        }
        if (bands == null) {
            eq = mp.getAudioEqualizer();
            bands = new EqualizerBand[0];
            bands = eq.getBands().toArray(bands);
        } else {
            mp.getAudioEqualizer().getBands().setAll(bands);
            if (eq == null) {
                eq = mp.getAudioEqualizer();
            }
        }
    }

    public static void setVolume(double vo) {
        vol = vo;
        if (mediaPlayer != null) {
            mediaPlayer.setVolume(vol);
        }
    }

    public static void setBalance(double b) {
        bal = b;
        if (mediaPlayer != null) {
            mediaPlayer.setBalance(b);
        }
    }

    public static void setEqualizer(AudioEqualizer ae) {
        eq = ae;
        bands = eq.getBands().toArray(bands);
        saveEqualizer();
    }

    public static void setBands(EqualizerBand[] bandss) {
        bands = bandss;
        if (eq != null) {
            eq.getBands().setAll(bands);
        }
    }

    public static String[] getAllSongs() {
        String[] son = new String[0];
        son = allPaths.toArray(son);
        return son;
    }

    public static void setAllSongs(String[] allSongs) {
        allPaths.clear();
        allPaths.addAll(Arrays.asList(allSongs));
    }

    public static void addAllSongs(String[] songs) {
        allPaths.addAll(Arrays.asList(songs));
    }

    public static void addSong2(Song s) {
        allPaths.add(s.getPath());
        allTitlesBack.add(s.getTitle());
        allSongs.add(s);
        
        PlaylistManager.addToLibrary(s);
        playlist = PlaylistManager.getCurrentPlaylist();
        if(allSongs.size()==totalSongsLock){
            saveAllSongs();
        }
    }

    static void designLabels() {
        SimpleIntegerProperty count = new SimpleIntegerProperty(0);
        allTitlesBack.stream().forEach((a) -> {
            Label l = new Label();
            l.setText(a);
            l.getStyleClass().add("item-title-small");
            l.setUserData(allSongs.get(count.get()));
            count.set(count.get() + 1);
            allLabels.add(l);
        });
        noOfSongs = count.get();
    }

    public static List getLabels() {
        return allLabels;
    }

    static void designDisplay() {
        lv = new ListView<>();
        lv.getStyleClass().setAll("list-view");
        lv.setEditable(false);
        lv.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        designLabels();
        lv.getItems().addAll(allLabels);
    }

    public static ListView getDisplay() {
        if (lv == null) {
            designDisplay();
        }
        if (noOfSongs < allTitlesBack.size()) {
            designDisplay();
        }
        return lv;
    }

    public static void registerPlayButtonForSongsSelection(Button b, ListView chosen) {
        chosen.getSelectionModel().selectedItemProperty().addListener((obs, o, n) -> {
            if (chosen.getSelectionModel().getSelectedItems().isEmpty()) {
                b.setDisable(true);
            } else {
                b.setDisable(false);
            }
        });
    }

    public static void playSelectedSongs(ListView chosen) {
        ObservableList<Label> oe = chosen.getSelectionModel().getSelectedItems();
        List<Song> temp = new LinkedList();
        oe.forEach((a) -> {
            temp.add(((Song) a.getUserData()));
        });
        playlist = new Playlist(FXCollections.observableList(temp), "now playing");
        setPlaylist(playlist);
    }

    public static void addSongsToPlaylist(ListView chosen) {
        ObservableList<Label> oe = chosen.getSelectionModel().getSelectedItems();
        List<Song> temp = new LinkedList();
        oe.forEach((a) -> {
            temp.add(((Song) a.getUserData()));
        });
        np.addToNowPlaying(temp);
        playlist = np.getPlaylist();
        saveCurrentPlaylist();
    }

    public static Playlist getPlaylist() {
        return playlist;
    }
    
    public static void restorePlaylist(Playlist play){
        playlist = play;
        if(playlist==null){
            playlist = new Playlist(allSongs,"Now Playing");
        }
    }

    public static void setPlaylist(Playlist playlist) {
        MediaManager.playlist = playlist;
        if (np != null) {
            np.setPlayListAndPlay(playlist);
        }
        saveCurrentPlaylist();
    }

    public static void restoreState(double dur) {
        currentSong = playlist.getCurrentSong();
        if(currentSong==null){
            currentSong = allSongs.get(0);
        }
        duration = dur;
    }

    public static Song getCurrentSong() {
        return currentSong;
    }

    public static double getDuration() {
        return duration;
    }

    public static void saveState(SimpleStringProperty message) {
        Service s = new Service() {

            @Override
            protected Task createTask() {
                Task t = new Task() {

                    @Override
                    protected Object call() throws Exception {
                        while(allSongs.size()!=totalSongsLock){}
                        saveVolBal();
                        saveAllSongs();
                        saveEqualizer();
                        saveAllPlaylists();
                        return null;
                    }
                };
                t.setOnRunning((Event evt) -> {
                    Platform.runLater(() -> {
                        message.setValue("saving..");
                    });
                });
                t.setOnSucceeded((Event evt) -> {
                    Platform.runLater(() -> {
                        message.setValue("saved..");
                    });
                });
                return t;
            }

        };
        s.start();
    }

    public static void saveVolBal() {
        double[] values = {vol, bal};
        saveObject(values, "values.data");
    }

    public static void saveAllSongs() {
        MetaManager.printArtists();
        saveObject(allSongs, "songs.data");
    }

    public static void saveEqualizer() {
        if (eq != null) {
            SerializableAudioEqualizer sae = new SerializableAudioEqualizer(eq);
            saveObject(sae, "sound.data");
        }
    }

    public static void saveCurrentPlaylist() {
        if (playlist != null) {
            saveObject(new PlaylistS(playlist), "playlist.data");
        }
    }

    public static void saveAllPlaylists() {
        saveObject(PlaylistManager.getAllPlaylists(), "playlists.data");
    }

    public static void checkMediaState() {
        if (mediaPlayer != null && mediaPlayer.getStatus() == Status.PLAYING) {
            Double time = mediaPlayer.getCurrentTime().toSeconds();
            saveObject(time, "playing.data");
        }
    }

    private static void saveObject(Object o, String fileName) {
        try {
            ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
            oos.writeObject(o);
            oos.close();
        } catch (IOException ex) {
            Logger.getLogger(MediaManager.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
    
    public static void setLock(int num){
        totalSongsLock = num;
    }

}
