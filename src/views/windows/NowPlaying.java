/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views.windows;

import core.Heading;
import core.MediaManager;
import core.Playlist;
import core.Song;
import java.io.File;
import java.nio.file.Paths;
import java.util.List;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import styles.ToggleSwitch;
import views.SceneManager;

/**
 *
 * @author Subhra
 */
public class NowPlaying extends VBox {

    private static Playlist playlist;
    private static Song song;
    private static int numSong = 0;

    private MediaPlayer mp = MediaManager.getMediaPlayer();
    private Media m;
    private final Button play;
    private Button pause;
    private final Button next;
    private final Button prev;
    private final SimpleBooleanProperty isPlaying = new SimpleBooleanProperty(false);
    private final Label title;
    private final Label year;
    private final Label composer;
    private final Label album;
    private final ImageView albumArt;
    private final Label artist;
    private ObservableMap om;
    private final BorderPane dtls;
    private final BorderPane imgPane;
    private final Slider progressBar;
    private final Slider[] bars;
    private final FlowPane visPane;
    private final Label updateStat;
    private final SimpleStringProperty msg;
    private final Label add;
    private final Button toAll;
    private final Button specs;
    private final Label curTime;
    private final Label totTime;
    private double seekTo = 0;
    private AllSongs allSongsWindow;

    public NowPlaying() {
        super();
        setSpacing(20);

        Heading h = new Heading("now playing");

        updateStat = new Label("");
        updateStat.setMaxWidth(580);
        updateStat.getStyleClass().add("item-title");

        title = new Label("");
        title.setMaxWidth(290);
        title.getStyleClass().add("item-title");

        year = new Label();
        year.getStyleClass().add("item-title");

        composer = new Label();
        composer.getStyleClass().add("item-title");

        album = new Label();
        album.getStyleClass().add("item-title");

        artist = new Label();
        artist.setMaxWidth(290);
        artist.getStyleClass().add("item-title");

        albumArt = new ImageView();

        imgPane = new BorderPane(albumArt);

        progressBar = new Slider();
        progressBar.valueChangingProperty().addListener((obs, wasChanging, isChanging) -> {
            if (!isChanging) {
                mp.seek(Duration.seconds(progressBar.getValue()));
            }
        });

        progressBar.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (!progressBar.isValueChanging()) {
                double currentTime = mp.getCurrentTime().toSeconds();
                if (Math.abs(currentTime - newValue.doubleValue()) > 0.5) {
                    mp.seek(Duration.seconds(newValue.doubleValue()));
                }
            }
        });

        dtls = new BorderPane();

        curTime = new Label("00:00");
        curTime.getStyleClass().add("item-title");
        curTime.setMinWidth(100);

        totTime = new Label("00:00");
        totTime.getStyleClass().add("item-title");
        totTime.setMinWidth(100);
        totTime.setTextAlignment(TextAlignment.RIGHT);
        totTime.setAlignment(Pos.CENTER_RIGHT);

        BorderPane stage3 = new BorderPane();

        stage3.setLeft(curTime);
        stage3.setCenter(year);
        stage3.setRight(totTime);

        VBox subV = new VBox(album, stage3);
        subV.setAlignment(Pos.CENTER);
        dtls.setCenter(subV);

        BorderPane bp1 = new BorderPane();
        bp1.setRight(artist);
        bp1.setLeft(title);

        visPane = new FlowPane();
        visPane.setAlignment(Pos.CENTER);

        bars = new Slider[60];
        for (int i = 0; i < 60; i++) {
            bars[i] = new Slider(-60, 0, -60);
            bars[i].setOrientation(Orientation.VERTICAL);
            visPane.getChildren().add(bars[i]);

        }

        play = new Button("play");
        play.setStyle("-fx-font-size: 30pt");
        play.setOnAction((ActionEvent evt) -> {
            if (mp != null) {
                mp.play();
                isPlaying.set(true);
            }
        });

        pause = new Button("pause");
        pause.setVisible(false);
        pause.setStyle("-fx-font-size: 30pt");
        pause.setOnAction((ActionEvent evt) -> {
            if (mp != null) {
                mp.pause();
                isPlaying.set(false);
            }
        });

        isPlaying.addListener((ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
            pause.setVisible(newValue);
            play.setVisible(oldValue);
        });

        prev = new Button("previous");
        prev.setOnAction((ActionEvent evt) -> {
            if (!(numSong == 0)) {
                mp.stop();
                numSong--;
                song = playlist.getPrevSong();
                refreshAll();
                if (mp.getStatus() == Status.PAUSED) {
                    pause.fire();
                } else {
                    play.fire();
                }
            }
        });

        next = new Button("next");
        next.setOnAction((ActionEvent evt) -> {
            if (!(numSong == playlist.size() - 1)) {
                mp.stop();
                numSong++;
                song = playlist.getNextSong();
                refreshAll();
                if (mp.getStatus() == Status.PAUSED) {
                    pause.fire();
                } else {
                    play.fire();
                }
            }
        });

        if (song != null) {
            refreshAll();
        } else {
            setState(true);
        }

        specs = new Button("specs");
        specs.setOnAction((val) -> {
            SceneManager.swapToNext(new MachineSpecs());
        });

        Button settings = new Button("settings");
        settings.setOnAction((ActionEvent evt) -> {
            SceneManager.swapToNext(new Settings());
        });

        ToggleSwitch ts = new ToggleSwitch();
        ts.selectedProperty().addListener((obs, o, n) -> {
            if (n) {
                showDetails();
            } else {
                refreshData();
            }
            requestLayout();
        });

        Label tsl = new Label("visualisation");
        tsl.getStyleClass().add("item-title");

        BorderPane svb = new BorderPane(null, null, tsl, ts, null);

        toAll = new Button("library");
        toAll.setOnAction((ActionEvent evt) -> {
            Platform.runLater(() -> {
                if (allSongsWindow == null) {
                    allSongsWindow = new AllSongs();
                }
                SceneManager.swapToNext(allSongsWindow);
            });
        });

        VBox rvb = new VBox(next, svb, specs);
        rvb.setSpacing(20);
        rvb.setAlignment(Pos.CENTER_RIGHT);

        VBox lvb = new VBox(prev, settings, toAll);
        lvb.setSpacing(20);

        BorderPane bp = new BorderPane();
        bp.setLeft(lvb);
        bp.setRight(rvb);
        bp.setCenter(new StackPane(play, pause));

        add = new Label("");
        add.getStyleClass().add("item-title");

        BorderPane head = new BorderPane();
        head.setLeft(h);
        head.setRight(add);
        head.setTop(updateStat);

        msg = new SimpleStringProperty();
        msg.addListener((obs, o, n) -> {
            if (n.endsWith("songs added..")) {
                Platform.runLater(() -> {
                    add.setText(n);
                });
            } else {
                Platform.runLater(() -> {
                    updateStat.setText(n);
                });
            }
        });

        getChildren().addAll(head, bp1, dtls, visPane, imgPane, progressBar, bp);
        restoreState();
    }

    public void restoreState() {
        if (MediaManager.getAllSongs() == null || MediaManager.getAllSongs().length == 0) {
            System.out.println("no songs in nowplaying");
        } else {
            playlist = MediaManager.getPlaylist();
            if (MediaManager.getCurrentSong() == null) {
                song = playlist.getNextSong();
                numSong = 0;
            } else {
                playSong(MediaManager.getCurrentSong());
            }
            if (!(MediaManager.getDuration() == 0)) {
                seekTo = MediaManager.getDuration();
                ChangeListener<Status> cls = new ChangeListener<Status>() {

                    @Override
                    public void changed(ObservableValue<? extends Status> observable, Status oldValue, Status newValue) {
                        if (newValue == Status.PLAYING) {
                            mp.statusProperty().removeListener(this);
                            mp.seek(Duration.seconds(seekTo));
                        }
                    }
                };
                mp.statusProperty().addListener(cls);
            }
            setState(false);
            activateLib();
        }
    }

    private boolean checkIntegrity() {
        for (String s : MediaManager.getAllSongs()) {
            File ts = new File(s);
            if (!ts.exists()) {
                return false;
            }
        }
        return true;
    }

    private void activateLib() {
        updateStat.setText("");
        add.setText("");
        toAll.setDisable(false);
        toAll.setText("library");
    }

    private void setState(boolean status) {
        progressBar.disabledProperty().addListener((obs, o, n) -> {
            next.setDisable(n);
            prev.setDisable(n);
        });
        progressBar.setDisable(status);
        title.setText("no song selected");
    }

    public void setPlayListAndPlay(Playlist songs) {
        playlist = songs;
        numSong = 0;
        playSong(playlist.getNextSong());
    }

    public void playSong(Song s) {
        song = s;
        pause.fire();
        refreshAll();
        play.fire();
    }

    private void refreshData() {
        om = m.getMetadata();
        String ttl = (String) om.get("title");
        if (ttl == null || ttl.equals("")) {
            String sor = m.getSource();
            String[] spl = sor.split("/");
            ttl = spl[spl.length - 1].replace("%20", " ");
        }
        title.setText(ttl);
        artist.setText((String) om.get("artist"));
        album.setText((String) om.get("album"));
        int yr = 0;
        try {
            yr = (int) om.get("year");
        } catch (Exception h) {
        }
        if (yr != 0) {
            year.setText(yr + "");
        } else {
            year.setText(null);
        }
        Image art = null;
        //(Image) om.get("image");
        if (art != null) {

            showImage(art);
        } else {
            showDetails();
        }
        progressBar.setMax(m.getDuration().toSeconds());
    }

    private void showImage(Image art) {
        getChildren().remove(dtls);
        getChildren().remove(visPane);
        albumArt.setImage(art);
        try {
            getChildren().add(2, imgPane);
        } catch (Exception h) {
        }
    }

    private void showDetails() {
        try {
            getChildren().add(2, dtls);
            getChildren().add(3, visPane);
        } catch (Exception h) {
        }
        getChildren().remove(imgPane);
    }

    public void addToNowPlaying(List<Song> songs) {
        playlist.addSongs(songs);
    }

    public Playlist getPlaylist() {
        return playlist;
    }

    private String getTime(int sec) {
        String tm = "";
        String hr = "";
        String min = "0";
        String sc = "";
        if (sec > 59) {
            int mn = sec / 60;
            sc = (sec - (mn * 60)) + "";
            if (mn > 59) {
                int hh = mn / 60;
                min = (mn - (mn * 60)) + "";
                hr = hh + "";
            } else {
                min = mn + "";
            }
        } else {
            sc = sec + "";
        }
        if (!hr.equals("")) {
            tm = hr + ":";
        }
        if (sc.length() == 1) {
            sc = "0" + sc;
        }
        if (min.length() == 1) {
            min = "0" + min;
        }
        tm = tm + min + ":" + sc;
        return tm;
    }

    private void refreshAll() {
        System.gc();
        setState(false);
        if (mp != null) {
            mp.setAudioSpectrumListener(null);
        }
        m = new Media(Paths.get(song.getPath()).toUri().toString());
        mp = new MediaPlayer(m);
        MediaManager.setMediaPlayer(mp);
        m.setOnError(() -> {
            m.getError().getType().toString();
        });
        mp.setOnReady(() -> {
            refreshData();
            totTime.setText(getTime((int) m.getDuration().toSeconds()));
        });
        progressBar.setValue(0);
        int i2 = 0;
        while (i2 < 60) {
            bars[i2].setValue(-60);
            i2++;
        }
        mp.currentTimeProperty().addListener((observable, oldValue, newValue) -> {
            if (!progressBar.isValueChanging()) {
                curTime.setText(getTime((int) newValue.toSeconds()) + "");
                progressBar.setValue(newValue.toSeconds());
            }
        });
        mp.setAudioSpectrumInterval(.08);
        mp.setAudioSpectrumNumBands(80);
        mp.setAudioSpectrumListener((timestamp, duration, magnitudes, phases) -> {
            int i = 0;
            while (i < 60) {
                bars[i].setValue(magnitudes[i]);
                i++;
            }
        });
        mp.setOnEndOfMedia(() -> {
            next.fire();
        });

    }

}
