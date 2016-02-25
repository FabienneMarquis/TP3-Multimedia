package views;

import java.io.File;
import java.net.URL;

import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.media.MediaPlayer.Status;
import utils.WindowApp;

public class MusiquePaneCtrl extends WindowApp {

	 public static void main(String[] args) { launch(args); }

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
	
	private AnimationPaneCtrl animationPaneCtrl = null;

	private PlayPaneCtrl playPaneCtrl = null;
	
    private Media media = null;

    private void initAnimation()
    {
    	animationPaneCtrl = (new AnimationPaneCtrl()).loadView();
    	root.getChildren().add(animationPaneCtrl.getRoot());
    }
    
    private void initDragAndDrop()
    {
    	controlPane.setOnDragDropped( event -> {
	    	Dragboard db = event.getDragboard();
	        String erreur = null;
	        if (db.hasFiles())
	        {
	        	File f = db.getFiles().get(0);
	        	try {
		        	Media newMedia = new Media(f.toURI().toString());
		            
		        	if( newMedia.getError()!= null )
		        	{
		        		erreur = "Impossible de charger le fichier: " + f.getName();
		        	}
		        	else 
		        	{
		        		playPaneCtrl.bindSong(newMedia, f.getName());
		        	};
	        	}
	        	catch(MediaException e)
	        	{
	        		erreur = "Impossible de charger le fichier: " + f.getName() + " ( " + e.getMessage() + " )";
	        	}
	        }
	        if(erreur != null){
	        	Alert alert = new Alert(AlertType.ERROR, erreur);
	        	alert.showAndWait();        		        	
	        }
	        event.setDropCompleted(erreur != null);
			controlPane.getStyleClass().remove("DragOver");
	        event.consume();    	
	    });
    	
    	controlPane.setOnDragEntered( event -> {
	    	if (event.getDragboard().hasFiles()) 
	    	{
	    		controlPane.getStyleClass().add("DragOver");
	            event.consume();    	
	        }
	    });
    	
    	controlPane.setOnDragExited( event -> {
			controlPane.getStyleClass().remove("DragOver");
			event.consume();    	
	    });
    	
    	controlPane.setOnDragOver( event -> {
			if ( event.getDragboard().hasFiles() ) 
			{
	            event.acceptTransferModes(TransferMode.COPY);
	            event.consume();
	        }
	    });
   	
    }

    @FXML
    private void initialize()
    {
    	initAnimation();
    	initPlayPane();
    	initDragAndDrop();
    }
    
    private void initPlayPane()
    {
    	URL musique = getClass().getResource("/ressources/popcorn.mp3");
    	media = new Media(musique.toExternalForm());
    	
    	playPaneCtrl = (new PlayPaneCtrl()).loadView();
    	playPaneSlot.getChildren().add(playPaneCtrl.getRoot());
    	VBox.setVgrow(playPaneCtrl.getRoot(), Priority.SOMETIMES);
    	playPaneCtrl.config( 
    			newStatus->onPlayingStatusChange(newStatus), 
    			()->onEndOfMedia());
    	playPaneCtrl.bindSong(media, "Popcorn");
    	animationPaneCtrl.bindRate(playPaneCtrl.volumeProperty());
    }
    
    private void onEndOfMedia()
    {
		if(checkRejouer.isSelected()) playPaneCtrl.bindAndPlay(media, "Popcorn"); 
    }
    
    private void onPlayingStatusChange(Status newStatus)
    {
		if(newStatus == Status.PLAYING) animationPaneCtrl.play();
		else animationPaneCtrl.pause();
    }

    @Override
	protected String getFxml() { return "MusiquePane.fxml"; }

    @Override
	protected Parent getRoot() { return root; }
    
	@Override
	protected String getTitle() { return "TP3 - Musique - Fabienne Marquis"; }
    
}

