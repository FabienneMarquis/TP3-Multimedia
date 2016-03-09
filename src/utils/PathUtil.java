package utils;

import model.Preference;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;

/**
 * Created by 1494778 on 2016-03-09.
 */
public class PathUtil {
    private static String programDir = null;
    public static String getProgramDir() throws IOException{
        if (programDir == null){
            String dir = (new File(Preference.class.getProtectionDomain().getCodeSource().getLocation().getPath().getParentFile().getCanonicalPath()));
            programDir= URLEncoder.decode(dir, "UTF-8");
        }
        return programDir;
    }

    public static String getFullPath(String programmeRelativePath)throws IOException{
        return getProgramDir() + File.separator + programmeRelativePath;
    }
}
