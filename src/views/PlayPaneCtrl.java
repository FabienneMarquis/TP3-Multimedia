package views;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaPlayer.Status;
import javafx.scene.text.Text;
import javafx.util.Duration;
import utils.WindowApp;

public class PlayPaneCtrl extends WindowApp {

    public static void main(String[] args) {
        launch(args);
    }

    @FXML
    private VBox root;

    @FXML
    private VBox vboxVolume;

    @FXML
    private HBox hboxVolume;

    @FXML
    private Label lblVolume;

    @FXML
    private Text txtVolume;

    @FXML
    private Slider sldrVolume;

    @FXML
    private VBox vboxLecture;

    @FXML
    private HBox hboxLecture;

    @FXML
    private Label lblLecture;

    @FXML
    private Text txtLecture;

    @FXML
    private HBox hboxProgres;

    @FXML
    private Button btnBackward;

    @FXML
    private ProgressBar progLecture;

    @FXML
    private Button btnForward;

    @FXML
    private Button btnJouer;

    @FXML
    private Button btnPause;

    @FXML
    private Button btnArret;

    @FXML
    private HBox hboxFichier;

    @FXML
    private Label lblFichier;

    @FXML
    private Button btnretour;

    @FXML
    private Button btnNext;


    @FXML
    private TextArea areaMetadata;

    private Media bindedMedia;

    private MediaPlayer mediaPlayer = null;

    private Consumer<Status> onStatusChange;

    private Runnable onEndOfMedia;

    private InvalidationListener statusListener = dontcare -> {
        onStatusChange.accept(mediaPlayer.getStatus());
    };

    private InvalidationListener timeListener = dontcare -> updateProgression();

    private int lastProgres = -1;

    private int lastTotal = -1;

    private String artist;
    private String annee;
    private String album;
    private String piece;
    private String pieceDefault;
    private String titreSong = "";


    /**
     * Fait un bind puis démarre la pièce.
     *
     * @param newMedia
     */
    public void bindAndPlay(Media newMedia, String songDefault) {
        bindSong(newMedia, songDefault);
        if (isBinded()){ btnJouer.fire();
            bindedMedia=newMedia;}
    }


    /**
     * Coupe le player de toute association avec une pièce musicale.
     * C'est l'état par défaut au démarrage du composante.
     */
    public void bindNoSong() {
        unbindMediaPlayer();
        vboxLecture.setDisable(true);
        lblFichier.setText("Aucune");
        areaMetadata.clear();
        afficherProgressionAuBesoin(0, 0);
    }

    /**
     * Associe le player à une pièce musicale.
     * Si une pièce est déjà associée, elle sera remplacée.
     * Si une pièce est déjà en train de jouer, la nouvelle pièce démarre automatiquement.
     *
     * @param newMedia La pièce à jouer.
     *                 Si null, on efface la pièce sans la remplacer.
     */
    public void bindSong(Media newMedia, String songDefault) {
        if (newMedia == null) {
            bindNoSong();
            return;
        }
        boolean wasPlaying = mediaPlayer != null && mediaPlayer.getStatus() == Status.PLAYING;
        unbindMediaPlayer();
        vboxLecture.setDisable(false);
        pieceDefault = songDefault;
        bindMediaPlayer(newMedia);
        if (wasPlaying) Platform.runLater(() -> btnJouer.fire());
        else requestFocus(btnJouer);
    }

    /**
     * Configure le panneau après initialisation.
     *
     * @param onStatusChange Fonction qui sera appelée chaque fois que le statu du Player va changer.
     *                       Cette fonction recoit en parametre le nouveau statu du player.
     *                       Si null il ne se passera rien à ce moment.
     * @param onEndOfMedia   Fonction qui sera appelée chaque fois qu'une pièce prend fin.
     *                       Si null, il ne se passera rien à ce moment.
     */
    public void config(Consumer<Status> onStatusChange, Runnable onEndOfMedia) {
        this.onEndOfMedia = onEndOfMedia != null ? onEndOfMedia : () -> {
        };
        this.onStatusChange = onStatusChange != null ? onStatusChange : status -> {
        };
    }


    /**
     * @return La propriété volume
     */
    public DoubleProperty volumeProperty() {
        return sldrVolume.valueProperty();
    }

    private void afficherMetadonnees() {

        List<String> list = new ArrayList<String>();
        mediaPlayer.getMedia().getMetadata().forEach((key, o) -> {
            if (!key.startsWith("raw"))
                list.add(key + " : " + o + "\n");
            if (key.equals("album")) {
                album = o.toString();
            } else if (key.equals("artist")) {
                artist = o.toString();
            } else if (key.equals("title")) {
                piece = o.toString();
            } else if (key.equals("year")) {
                annee = o.toString();
            }
        });

        list.sort(null);
        areaMetadata.clear();
        if (list.isEmpty())
            areaMetadata.appendText("Aucune métadonnées disponibles");
        else
            list.forEach(elem -> areaMetadata.appendText(elem));
        areaMetadata.positionCaret(0);
    }

    private void changeTitreSong() {
        if (artist != null) {
            titreSong += artist;
        }
        if (annee != null) {
            titreSong += " - " + annee;
        }
        if (album != null) {
            titreSong += " - " + album;
        }
        if (piece != null && !piece.equals(album)) {
            titreSong += " - " + piece;
        } else if (piece == null) {
            titreSong += " - " + pieceDefault;
        }
        System.out.println(titreSong);
        if (titreSong == null) titreSong = "Inconnu";
        lblFichier.setText(titreSong);
    }


    private void afficherProgression(int progres_ds, int total_s) {
        progLecture.setProgress(progres_ds / 10.0 / total_s);
        int dixiemes = progres_ds % 10;
        int sec = progres_ds % 600 / 10;
        int min = progres_ds / 600;
        long secTot = total_s % 60;
        long minTot = total_s / 60;
        txtLecture.setText(String.format("%d:%02d.%d  /  %d:%02d", min, sec, dixiemes, minTot, secTot));
    }

    private void afficherProgressionAuBesoin(int progres_ms, int total_ms) {
        int progres_ds = progres_ms / 100;
        int total_s = total_ms / 1000;
        if (lastProgres != progres_ds || lastTotal != total_s) {
            lastProgres = progres_ms;
            lastTotal = total_s;
            afficherProgression(progres_ds, total_s);
        }
    }

    private void bindJouerPauseArret() {
        btnJouer.disableProperty().bind(
                mediaPlayer.statusProperty().isEqualTo(Status.PLAYING));
        btnPause.disableProperty().bind(mediaPlayer.statusProperty().isNotEqualTo(Status.PLAYING));
        btnArret.disableProperty().bind(
                mediaPlayer.statusProperty().isNotEqualTo(Status.PLAYING)
                        .and((mediaPlayer.statusProperty().isNotEqualTo(Status.PAUSED))));
    }

    private void bindMediaPlayer(Media newMedia) {
        mediaPlayer = new MediaPlayer(newMedia);

        mediaPlayer.statusProperty().addListener(statusListener);

        mediaPlayer.setOnReady(() -> {
            afficherMetadonnees();
            initProgression();
            changeTitreSong();
        });

        bindJouerPauseArret();

        // Volume
        mediaPlayer.volumeProperty().bind(sldrVolume.valueProperty().multiply(sldrVolume.valueProperty()).multiply(sldrVolume.valueProperty()));

        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer.seek(Duration.ZERO);
            btnArret.fire();
            Platform.runLater(() -> onEndOfMedia.run());
        });
    }

    private void initActions() {
        // Actions des boutons
        btnJouer.setOnAction(e -> {
            mediaPlayer.play();
            requestFocus(btnPause);
        });
        btnPause.setOnAction(e -> {
            mediaPlayer.pause();
            requestFocus(btnJouer);
        });
        btnArret.setOnAction(e -> {
            mediaPlayer.stop();
            requestFocus(btnJouer);
        });

        // Boutons forward / backward
        btnForward.setOnAction(e -> {
            mediaPlayer.seek(mediaPlayer.getCurrentTime().add(Duration.seconds(30)));
        });

        btnBackward.setOnAction(e -> {
            mediaPlayer.seek(mediaPlayer.getCurrentTime().subtract(Duration.seconds(30)));
        });
    }

    @FXML
    private void initialize() {
        txtVolume.textProperty().bind(sldrVolume.valueProperty().subtract(0.2).multiply(125).asString("%.0f%%"));
        config(null, null);
        initActions();
        bindNoSong();

        album = null;
        artist = null;
        piece = null;
        annee = null;
    }

    private void initProgression() {
        mediaPlayer.currentTimeProperty().addListener(timeListener);

        btnForward.disableProperty().bind(
                mediaPlayer.statusProperty().isNotEqualTo(Status.PLAYING));

        btnBackward.disableProperty().bind(
                mediaPlayer.statusProperty().isNotEqualTo(Status.PLAYING));

        updateProgression();

    }

    private boolean isBinded() {
        return mediaPlayer != null;
    }

    private void requestFocus(Node node) {
        Platform.runLater(() -> Platform.runLater(() -> Platform.runLater(() -> node.requestFocus())));
    }

    private void unbindMediaPlayer() {
        if (isBinded()) {
            mediaPlayer.stop();
            mediaPlayer.volumeProperty().unbind();
            mediaPlayer.statusProperty().removeListener(statusListener);
            mediaPlayer.currentTimeProperty().removeListener(timeListener);
            mediaPlayer = null;
        }
    }

    
    
    private void updateProgression() {
        int progres_ms = (int) mediaPlayer.getCurrentTime().toMillis();
        int total_ms = (int) mediaPlayer.getMedia().getDuration().toMillis();
        afficherProgressionAuBesoin(progres_ms, total_ms);
    }

    @Override
    protected String getFxml() {
        return "PlayPane.fxml";
    }

    @Override
    protected Parent getRoot() {
        return root;
    }

    @Override
    protected ContextMenu getTestMenu() {
        return new ContextMenu(
                new MenuItem("Bind Popcorn") {{
                    setOnAction(event -> {
                        URL musique = getClass().getResource("/ressources/popcorn.mp3");
                        bindSong(new Media(musique.toExternalForm()), "song");
                        Platform.runLater(() -> btnJouer.requestFocus());
                    });
                }},
                new MenuItem("Play Popcorn") {{
                    setOnAction(event -> {
                        URL musique = getClass().getResource("/ressources/popcorn.mp3");
                        bindAndPlay(new Media(musique.toExternalForm()), "song");
                        Platform.runLater(() -> btnJouer.requestFocus());
                    });
                }},
                new MenuItem("Play None") {{
                    setOnAction(event -> {
                        bindNoSong();
                    });
                }}
        );
    }




    @Override
    protected String getTitle() {
        return "TP3 - PlayPane";
    }

    public Media getBindedMedia() {
        return bindedMedia;
    }
}

