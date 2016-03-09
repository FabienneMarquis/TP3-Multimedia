package utils;

import javafx.scene.media.Media;

import java.io.File;
import java.net.URLDecoder;

/**
 * Created by 1494778 on 2016-03-09.
 */
public class MediaUtil {

    public static String getMediaFileName(Media media){
        try{
            return URLDecoder.decode((new File(media.getSource())).getName(), "UTF-8");
        }catch (Exception e){
            System.out.println("fichier non valide: "+media.getSource());
            return "inconnu";
        }
    }
}
