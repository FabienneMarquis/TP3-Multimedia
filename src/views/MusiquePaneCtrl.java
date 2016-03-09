package views;

import java.io.File;
import java.net.URL;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Preference;
import utils.WindowApp;

public class MusiquePaneCtrl extends WindowApp {

    public static void main(String[] args) {
        launch(args);
    }

    @FXML
    private HBox root;

    @FXML
    private VBox controlPane;

    @FXML
    private Label lblMusique;

    @FXML
    private VBox playPaneSlot;

    @FXML
    private CheckBox checkRejouer;

    @FXML
    private VBox vBoxRigth;

    @FXML
    private VBox vboxLeft;

    @FXML
    private Button btnTimeline;

    @FXML
    private ImageView imageTimeline;

    private static final int CONTROL_PANE_WIDTH = 240;

    private AnimationPaneCtrl animationPaneCtrl1 = null;
    private AnimationPaneCtrl animationPaneCtrl2 = null;
    private AnimationPaneCtrl animationPaneCtrl3 = null;
    private AnimationPaneCtrl animationPaneCtrl4 = null;

    private PlayPaneCtrl playPaneCtrl = null;

    private Media media = null;
    private String acdcBiB = "ACDC - Back In Black.mp3";

    private void initAnimation() {
        animationPaneCtrl1 = (new AnimationPaneCtrl()).loadView();
        animationPaneCtrl2 = (new AnimationPaneCtrl()).loadView();
        animationPaneCtrl3 = (new AnimationPaneCtrl()).loadView();
        animationPaneCtrl4 = (new AnimationPaneCtrl()).loadView();

        vboxLeft.getChildren().add(animationPaneCtrl3.getRoot());
        vboxLeft.getChildren().add(animationPaneCtrl4.getRoot());
        vBoxRigth.getChildren().add(animationPaneCtrl1.getRoot());
        vBoxRigth.getChildren().add(animationPaneCtrl2.getRoot());

    }

    private void initDragAndDrop() {
        controlPane.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            String erreur = null;
            if (db.hasFiles()) {
                File f = db.getFiles().get(0);
                try {
                    Media newMedia = new Media(f.toURI().toString());

                    if (newMedia.getError() != null) {
                        erreur = "Impossible de charger le fichier: " + f.getName();
                    } else {
                        playPaneCtrl.bindSong(newMedia, f.getName());
                    }
                } catch (MediaException e) {
                    erreur = "Impossible de charger le fichier: " + f.getName() + " ( " + e.getMessage() + " )";
                }
            }
            if (erreur != null) {
                Alert alert = new Alert(AlertType.ERROR, erreur);
                alert.showAndWait();
            }
            event.setDropCompleted(erreur != null);
            controlPane.getStyleClass().remove("DragOver");
            event.consume();
        });

        controlPane.setOnDragEntered(event -> {
            if (event.getDragboard().hasFiles()) {
                controlPane.getStyleClass().add("DragOver");
                event.consume();
            }
        });

        controlPane.setOnDragExited(event -> {
            controlPane.getStyleClass().remove("DragOver");
            event.consume();
        });

        controlPane.setOnDragOver(event -> {
            if (event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY);
                event.consume();
            }
        });
    }

    @FXML
    private void initialize() {
        initAnimation();
        initPlayPane();
        initDragAndDrop();
        initSize();
        initBtnTimeline();
    }

    private void initPlayPane() {
        URL musique = getClass().getResource("/ressources/" + acdcBiB);
        media = new Media(musique.toExternalForm());

        playPaneCtrl = (new PlayPaneCtrl()).loadView();
        playPaneSlot.getChildren().add(playPaneCtrl.getRoot());
        VBox.setVgrow(playPaneCtrl.getRoot(), Priority.SOMETIMES);
        playPaneCtrl.config(
                newStatus -> onPlayingStatusChange(newStatus),
                () -> onEndOfMedia());
        playPaneCtrl.bindSong(media, acdcBiB);
        animationPaneCtrl1.bindRate(playPaneCtrl.volumeProperty());
        animationPaneCtrl2.bindRate(playPaneCtrl.volumeProperty());
        animationPaneCtrl3.bindRate(playPaneCtrl.volumeProperty());
        animationPaneCtrl4.bindRate(playPaneCtrl.volumeProperty());
        vBoxRigth.setVgrow(animationPaneCtrl1.getRoot(), Priority.ALWAYS);
        vboxLeft.setVgrow(animationPaneCtrl3.getRoot(), Priority.ALWAYS);
        vBoxRigth.setVgrow(animationPaneCtrl2.getRoot(), Priority.ALWAYS);
        vboxLeft.setVgrow(animationPaneCtrl4.getRoot(), Priority.ALWAYS);
    }

    private void onEndOfMedia() {
        if (checkRejouer.isSelected()) playPaneCtrl.bindAndPlay(media, "Popcorn");
    }

    private void onPlayingStatusChange(Status newStatus) {
        if (newStatus == Status.PLAYING) {
            animationPaneCtrl1.play();
            animationPaneCtrl2.play();
            animationPaneCtrl3.play();
            animationPaneCtrl4.play();
        } else {
            animationPaneCtrl1.pause();
            animationPaneCtrl2.pause();
            animationPaneCtrl3.pause();
            animationPaneCtrl4.pause();
        }
    }

    @Override
    protected String getFxml() {
        return "MusiquePane.fxml";
    }

    @Override
    protected Parent getRoot() {
        return root;
    }

    @Override
    protected String getTitle() {
        return "TP3 - Musique - Fabienne Marquis";
    }

    private Stage getStage() {
        return (Stage) root.getScene().getWindow();
    }

    private void initSize() {
        controlPane.setMinWidth(CONTROL_PANE_WIDTH);
        controlPane.setMaxWidth(CONTROL_PANE_WIDTH);
        root.sceneProperty().addListener(dontcare -> {
            root.getScene().windowProperty().addListener(dontcareeither -> {
                Rectangle2D screenSize = Screen.getPrimary().getVisualBounds();
                getStage().setHeight(screenSize.getHeight() * 0.9);
                getStage().setWidth(largeurIdealDuStage());
            });
        });


    }

    private void stockerPreferences(){
        Preference pref = new Preference(){{
            stageWidth = getStage().getWidth();
            stageHeight = getStage().getHeight();
            Media media = playPaneCtrl.getBindedMedia();
            songFile = media == null ? null : media.getSource();
        }};
        pref.stocker();;
    }

    private double largeurIdealDuStage() {
        double largeurIdeal = CONTROL_PANE_WIDTH + getStage().getHeight();
        double largeurEcran = Screen.getPrimary().getVisualBounds().getWidth();
        return Math.min(largeurIdeal, largeurEcran * 0.9);
    }

    private double largeurIdealDuStageSansAnimation() {
        return CONTROL_PANE_WIDTH
                + root.getPadding().getLeft()
                + root.getPadding().getRight();
    }

    private void initBtnTimeline() {
        btnTimeline.setOnAction(event -> {
            double largeurActuelle = getStage().getWidth();
            double largeurCible = largeurActuelle > largeurIdealDuStageSansAnimation() ? largeurIdealDuStageSansAnimation() : this.largeurIdealDuStage();
            DoubleProperty stageWidthProxyProperty = new SimpleDoubleProperty(largeurActuelle);
            stageWidthProxyProperty.addListener(dontcare -> getStage().setWidth(stageWidthProxyProperty.get()));
            double opacityAimed = (animationPaneCtrl1.getRoot()).opacityProperty().getValue() == 0.0 ? 1.0 : 0.0;
            System.out.println(opacityAimed);
            DoubleProperty animationOppacityProperty = new SimpleDoubleProperty(opacityAimed == 0 ? 1.0 : 0.0);
            System.out.println(animationOppacityProperty.getValue());
            animationOppacityProperty.addListener(((observable, oldValue, newValue) -> {
                animationPaneCtrl1.getRoot().setOpacity((double) newValue);
                animationPaneCtrl2.getRoot().setOpacity((double) newValue);
                animationPaneCtrl3.getRoot().setOpacity((double) newValue);
                animationPaneCtrl4.getRoot().setOpacity((double) newValue);
            }));

            Timeline timeline = new Timeline(
                    new KeyFrame(Duration.millis(0),
                            new KeyValue(stageWidthProxyProperty, largeurActuelle),
                            new KeyValue(lblMusique.translateYProperty(), 0)),
                    new KeyFrame(Duration.millis(1000),
                            new KeyValue(lblMusique.translateYProperty(), 50)),
                    new KeyFrame(Duration.millis(2000),
                            new KeyValue(lblMusique.translateYProperty(), 0),
                            new KeyValue(lblMusique.scaleXProperty(), 1),
                            new KeyValue(stageWidthProxyProperty, largeurCible)),
                    new KeyFrame(Duration.millis(3000),
                            new KeyValue(lblMusique.scaleXProperty(), 1.5)),
                    new KeyFrame(Duration.millis(4000),
                            new KeyValue(stageWidthProxyProperty, largeurActuelle),
                            new KeyValue(lblMusique.scaleXProperty(), 1))
            );
            timeline.setOnFinished(dontcare -> System.out.println("timeline terminée"));
            timeline.playFromStart();
            System.out.println("timeline démarrée");

        });
    }

}

