/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views.windows;

import core.Heading;
import core.MediaManager;
import core.MetaManager;
import core.Song;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.concurrent.Task;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import org.farng.mp3.id3.AbstractID3v2;
import org.farng.mp3.id3.ID3v1;
import views.SceneManager;

/**
 *
 * @author Subhra
 */
public class MetaRetrieverDisplay extends VBox {

    private static List<File> dirs;

    public static void setFolders(String[] di) {
        dirs = new LinkedList(FXCollections.observableArrayList(di));
    }

    public static void getSong(String path, Runnable toDo) {
        Media m = new Media(Paths.get(path).toUri().toString());
        MediaPlayer mp = new MediaPlayer(m);
        mp.setOnReady(() -> {
            ObservableMap<String, Object> ol = m.getMetadata();

            // Song s2 = new Song(path, title, artist, album);
            ///  qs.add(s2);
            // MediaManager.addSong2(s2);
        });
    }

    private static String checkIfNull(Object toCheck, String defVal) {
        String ret = defVal;
        if (toCheck != null && !((String) toCheck).equals("")) {
            ret = (String) toCheck;
        }
        return ret;
    }

    private String[] paths;
    private final SimpleStringProperty msg;
    private String path;
    private final ProgressBar pb;

    public MetaRetrieverDisplay() {
        super();
        setSpacing(10);
        msg = new SimpleStringProperty();

        Heading h = new Heading("you're all set");

        Label l = new Label("spare us a couple of minutes while we set up your library");
        l.getStyleClass().add("item-title");

        Label l2 = new Label("getting song metadata");
        pb = new ProgressBar(0);
        pb.setMaxWidth(300);
        pb.setProgress(0);
        msg.addListener((o, o2, n) -> {
            Platform.runLater(() -> {
                l2.setText(n);
            });
        });

        BorderPane bp = new BorderPane();
        bp.setCenter(l2);
        BorderPane bp2 = new BorderPane();
        bp2.setCenter(pb);
        getChildren().addAll(h, l, bp, bp2);
        updateTask();
    }

    private void updateTask() {
        Task task = new Task() {
            int num = 0;

            private void addSongs() {
                paths = new String[0];
                if (dirs == null) {
                    dirs = SearchManager.getAllowedLocations();
                }
                if (dirs.isEmpty()) {
                    SceneManager.hideBackButton();
                    SceneManager.swapToNext(new AddLocation());
                    cancel(true);
                } else {
                    Queue<String> files = new LinkedList();
                    dirs.stream().forEach((dir) -> {
                        addSongsFromDir(dir, files);
                    });
                    path = paths[0];
                }
            }

            private void addSongsFromDir(File dir, Queue<String> toAdd) {
                String[] fnm = dir.list();
                for (String f : fnm) {
                    try {
                        File tf = new File(dir + "\\" + f);
                        if (tf.isDirectory()) {
                            updateMessage("searching in " + tf.getName());
                            addSongsFromDir(tf, toAdd);
                        } else if (tf.getName().endsWith(".mp3")) {
                            toAdd.add(tf.getAbsolutePath());
                            num++;
                            paths = toAdd.toArray(paths);
                        }
                    } catch (Exception h) {
                        System.out.println(h);
                    }
                }
            }

            @Override
            protected Object call() throws Exception {
                addSongs();
                updateMessage("library updated..");
                return num;
            }
        };
        task.messageProperty().addListener((obs, o, n) -> {
            msg.setValue(n);
        });

        Task t = new Task() {
            @Override
            protected Object call() throws Exception {
                SimpleDoubleProperty count = new SimpleDoubleProperty(0);
                updateMessage("retrieving media info..");
                MediaManager.setLock(paths.length);
                for (String path : paths) {
                    count.setValue(count.getValue() + 1);
                    //     MediaPlayer mp = new MediaPlayer(new Media(Paths.get(path).toUri().toString()));
                    //     mp.setOnReady(() -> {
                    //         String title = checkIfNull(mp.getMedia().getMetadata().get("title"), new File(path).getName().replace(".mp3", ""));
                    //        String artist = checkIfNull(mp.getMedia().getMetadata().get("artist"), "Unknown Artist");
                    //        String album = checkIfNull(mp.getMedia().getMetadata().get("album"), "Unknown Album");
                    //        
                    //         Song s2 = new Song(path, title, artist, album);
                    //        MediaManager.addSong2(s2);
                    //         mp.dispose();
                    //         System.gc();
                    //         System.runFinalization();
                    // });
                    String title = "U/A";
                    String artist = "Unknown Artist";
                    String album = "Unknown Album";
                    try {
                        File media = new File(path);
                        title = media.getName().replace(".mp3", "");
                        final MP3File mp1 = new MP3File(media, false);
                        synchronized (mp1) {
                            if (mp1.hasID3v1Tag()) {
                                ID3v1 tag = mp1.getID3v1Tag();
                                title = checkIfNull(tag.getSongTitle(), title).trim();
                                artist = checkIfNull(tag.getArtist(), artist).trim();
                                album = checkIfNull(tag.getAlbum(), album).trim();
                            } else if (mp1.hasID3v2Tag()) {
                                AbstractID3v2 tag = mp1.getID3v2Tag();
                                title = checkIfNull(tag.getSongTitle(), title);
                                artist = checkIfNull(tag.getLeadArtist(), artist);
                                album = checkIfNull(tag.getAlbumTitle(), album);
                            }
                        }
                        
                    } catch (Exception e) {
                        System.out.println(e);
                    }
                    Song s2 = new Song(path, title, artist, album);//place outside try catch
                    MediaManager.addSong2(s2);//place outside try catch
                    double progress = (count.getValue() / paths.length);
                    pb.setProgress(progress);
                }
                updateMessage("saving media info..");
                //       MediaManager.saveAllSongs();
                //      MediaManager.saveState(msg);
                updateMessage("media info saved..");
                return null;
            }
        };
        t.messageProperty().addListener((obs, o, n) -> {
            msg.setValue(n);
        });
        t.setOnSucceeded((v) -> {
            SceneManager.hideBackButton();
            NowPlaying n = new NowPlaying();
            SceneManager.swapToNext(n);
            MediaManager.setNowPlaying(n);
        });
        t.setOnCancelled((v) -> {
            msg.setValue("task has been cancelled!");
        });
        t.setOnFailed((v) -> {
            msg.setValue("task has been failed!\nReason : " + v.getEventType().getName());
        });
        task.setOnSucceeded((v) -> {
            new Thread(t).start();
        });

        Thread adder = new Thread(task);
        adder.start();
    }

    private int calculateAndShowRem(int done, int total) {
        int rem = total - done;
        long mses = rem * 50;
        int sec = (int) mses / 1000;
        return sec;
    }

}
