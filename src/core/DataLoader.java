/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package core;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.event.Event;

/**
 *
 * @author Subhra
 */
public class DataLoader {

    private static Task t;

    public static void loadAll(StringProperty msg, Runnable next) {
        t = new Task() {
            @Override
            protected Object call() throws Exception {
                loadVolBal(msg);
                loadEqualizer(msg);
                loadLibrary(msg);
                loadPlaylist(msg);
                loadPlaying(msg);
                next.run();
                return null;
            }
        };
        t.setOnCancelled((Event evt) -> {
            msg.setValue("error occured! can't load one or more core components!");
        });
        t.setOnFailed((evt)->{
            msg.setValue("fatal error occured! ThinkDifferent failed to start!\nThinkDifferent is working on the cause, and will restart after that.\n"+evt.toString());
        });
        t.setOnSucceeded((Event evt) -> {
            msg.setValue("database loaded..");
            next.run();
        });
        t.setOnRunning((Event evt) -> {
            msg.setValue("starting loader task..");
        });
        t.run();
    }

    public static void loadVolBal(StringProperty msg) {
        try {
            Platform.runLater(() -> {
                msg.setValue("loading sound preferences..");
            });
            double[] o = (double[]) loadThing("values.data");
            MediaManager.setVolume((double) o[0]);
            MediaManager.setBalance((double) o[1]);
            Platform.runLater(() -> {
                msg.setValue("sound preferences loaded..");
            });
        } catch (IOException | ClassNotFoundException ex) {
            Platform.runLater(() -> {
                msg.setValue("can't load sound preferences..");
            });
        }
    }

    public static void loadPlaying(StringProperty msg) {
        try {
            Platform.runLater(() -> {
                msg.setValue("loading what you were playing..");
            });
            double o = (double) loadThing("playing.data");
            MediaManager.restoreState(o);
        } catch (IOException | ClassNotFoundException ex) {
            Platform.runLater(() -> {
                msg.setValue("can't load what you were playing..");
            });
        }
    }

    public static void loadEqualizer(StringProperty msg) {
        try {
            Platform.runLater(() -> {
                msg.setValue("loading equalizer..");
            });
            SerializableAudioEqualizer sae = (SerializableAudioEqualizer) loadThing("sound.data");
            MediaManager.setBands(sae.getEqualizerBands());
            Platform.runLater(() -> {
                msg.setValue("equalizer loaded..");
            });
        } catch (IOException | ClassNotFoundException ex) {
            Platform.runLater(() -> {
                msg.setValue("can't load equalizer..");
            });

        }
    }

    public static void loadLibrary(StringProperty msg) {
        try {
            Platform.runLater(() -> {
                msg.setValue("loading library..");
            });
            List<Song> o = (List<Song>) loadThing("songs.data");
            SimpleIntegerProperty prcn = new SimpleIntegerProperty(0);
            o.stream().map((os) -> {
                prcn.setValue(prcn.getValue() + 1);
                MediaManager.addSong2(os);
                return os;
            }).forEach((_item) -> {
                Platform.runLater(() -> {
                    msg.setValue("loading library ( " + (int) ((prcn.getValue() * 100) / o.size()) + "% done  ) ..");
                });
            });
            Platform.runLater(() -> {
                msg.setValue("library loaded..");
            });
        } catch (IOException | ClassNotFoundException ex) {
            Platform.runLater(() -> {
                msg.setValue("can't load library..");
                t.cancel(true);
            });
        }
    }

    private static Object loadThing(String name) throws FileNotFoundException, IOException, ClassNotFoundException {
        Object o;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(name))) {
            o = ois.readObject();
        }
        return o;
    }

    private static void loadPlaylist(StringProperty msg) {
        try {
            Platform.runLater(() -> {
                msg.setValue("loading playlist..");
            });
            PlaylistS playlistbak = (PlaylistS) loadThing("playlist.data");
            MediaManager.restorePlaylist(playlistbak.getPlaylist());
            Platform.runLater(() -> {
                msg.setValue("playlist loaded..");
            });
        } catch (IOException | ClassNotFoundException ex) {

            Platform.runLater(() -> {
                msg.setValue("can't load playlist..");
                MediaManager.restorePlaylist(null);
            });
        }
            PlaylistManager.loadAllPlaylists();
    }

}
