/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package views;

import core.MediaManager;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.ParallelTransition;
import javafx.animation.SequentialTransition;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;

/**
 *
 * @author Subhra
 */
public class SceneManager {

    private static Stage base;

    private static Pane presentPane;

    private static Pane nextPane;

    private static Duration delay = Duration.ZERO;

    private static final String colorName = "Grey";

    private static final int height = 700;

    private static final int width = 800;

    private static boolean closing = false;

    private static boolean showBack = false;

    private static Button backButton;

    private static Pane pastPane;

    private static boolean reverse = false;

    public static void setStage(Stage s, Pane p) {
        base = s;
        base.setTitle("LaUD");
        base.setOnCloseRequest((WindowEvent e) -> {
            Platform.exit();
        });
        base.setResizable(false);
        base.setOnCloseRequest((WindowEvent evt) -> {
            evt.consume();
            if (!closing) {
                ParallelTransition pte = createExitAni(presentPane);
                pte.setOnFinished((ActionEvent et) -> {
                    MediaManager.checkMediaState();
                    Runtime.getRuntime().exit(0);
                });
                pte.play();
                closing = true;
            }
        });
        swapToNext(p);
    }

    public static void goBack() {
        //  backButton.fire();
        swapToNext(pastPane);
    }

    public static void doWhenReturn(EventHandler<MouseEvent> evt) {
        backButton.setOnMouseClicked(evt);
    }

    public static void vanish() {
        ParallelTransition pt = createExitAni(presentPane);
        pt.setOnFinished((ActionEvent evt) -> {
            Platform.exit();
        });
        pt.play();
    }

    public static ParallelTransition createEntranceAni(Pane p) {
        ParallelTransition pt = new ParallelTransition();
        int s = p.getChildren().size();
        int diff = 0;
        for (int i = 0; i < s; i++) {

            Node n = p.getChildren().get(i);
            if (n.getClass() == Pane.class) {
                ParallelTransition p2 = createEntranceAni((Pane) n);
                pt.getChildren().add(p2);
            } else {
                TranslateTransition tt = new TranslateTransition(Duration.millis(200), n);
                if (reverse) {
                    tt.setFromX(n.getLayoutX() + 100);
                } else {
                    tt.setFromX(n.getLayoutX() - 100);
                }

                tt.setToX(n.getLayoutX() - 10);
                tt.setRate(.8f);
                tt.setInterpolator(Interpolator.EASE_OUT);
                tt.setDelay(Duration.millis(diff));
                FadeTransition ft = new FadeTransition(Duration.millis(280), n);
                ft.setFromValue(0.0);
                ft.setToValue(1.0);
                if (n.getLayoutY() < height) {
                  //  diff = diff + 15;
                }
                pt.getChildren().addAll(tt, ft);
            }
        }
        return pt;
    }

    public static ParallelTransition createExitAni(Pane p) {
        ParallelTransition pt = new ParallelTransition();
        int s = p.getChildren().size();
        int diff = 0;
        for (int i = 0; i < s; i++) {
            Node n = p.getChildren().get(i);
            if (n.getClass() == Pane.class) {
                ParallelTransition p2 = createExitAni((Pane) n);
                pt.getChildren().add(p2);
            } else {
                TranslateTransition tt = new TranslateTransition(Duration.millis(200), n);
                tt.setFromX(n.getLayoutX());
                if (reverse) {
                    tt.setToX(n.getLayoutX() - 100);
                } else {
                    tt.setToX(n.getLayoutX() + 100);
                }
                // tt.setRate(2.0f);
                tt.setInterpolator(Interpolator.EASE_IN);
                tt.setDelay(Duration.millis(diff));
                FadeTransition ft = new FadeTransition(Duration.millis(150), n);
                ft.setFromValue(1.0);
                ft.setToValue(0.0);
                if (n.getLayoutY() < height) {
                  //  diff = diff + 15;
                }
                pt.getChildren().addAll(tt, ft);
            }
        }
        return pt;
    }

    public static void hide(Pane to) {
        to.getChildren().stream().forEach((n) -> {
            n.setVisible(false);
        });
    }

    public static void show(Pane to) {
        to.getChildren().stream().forEach((n) -> {
            n.setVisible(true);
        });
    }

    public static Button createBackButton(Pane p) {
        pastPane = p;
        Button bk = new Button("", new ImageView("icons/appbar.arrow.left2.png"));
        bk.setOnAction((ActionEvent evt) -> {
            reverse = true;
            swapToNext(p);
        });
        backButton = bk;
        return bk;
    }

    public static void addBackButton(Pane from, Pane to) {
        BorderPane fp = new BorderPane();
        fp.setLeft(createBackButton(from));
        to.getChildren().add(0, fp);
    }

    public static void swapToNext(Pane next) {
        nextPane = next;
        hide(next);
        next.getStyleClass().add("background");
        next.setPadding(new Insets(10, 10, 10, 10));
        SequentialTransition st = new SequentialTransition();
        if (presentPane != null) {
            st.getChildren().add(createExitAni(presentPane));
        }
        if (nextPane.getScene() == null) {
            if (!showBack) {
                showBack = true;
            } else if (presentPane != null) {
                addBackButton(presentPane, nextPane);
            }
        }
        st.setOnFinished((ActionEvent evt) -> {
            if (nextPane.getScene() == null) {
                nextPane.setPrefSize(width, height);
                ScrollPane sp = new ScrollPane(nextPane);
                sp.setFitToHeight(true);
                sp.setFitToWidth(true);
                Scene newScene = new Scene(sp, width, height);
                newScene.getStylesheets().add("styles/MStyles" + colorName + ".css");
                base.setScene(newScene);
            } else {
                base.setScene(nextPane.getScene());
            }
            base.show();
            show(next);
            createEntranceAni(nextPane).play();
            presentPane = nextPane;
            reverse = false;
        });

        st.setDelay(delay);
        st.play();
        delay = Duration.ZERO;
    }

    public static void hideBackButton() {
        showBack = false;
    }

    public static void swapAfterDelay(Pane next, Duration d) {
        delay = d;
        swapToNext(next);
    }

}
