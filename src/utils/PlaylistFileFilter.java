package utils;

import java.io.File;
import javax.swing.filechooser.FileFilter;

// filter for for FileChooser - can select files with mp3 file extention only
public class PlaylistFileFilter extends FileFilter {

    private final String fileExtension;
    private final String fileDescription;

    public PlaylistFileFilter(String fileExtension, String fileDescription) {
        this.fileExtension = fileExtension;
        this.fileDescription = fileDescription;
    }

    @Override
    // only folders or files wit file extention of mp3 are allowed
    public boolean accept(File file) {
        return file.isDirectory() || file.getAbsolutePath().endsWith(fileExtension);
    }

    @Override
    //description of the mp3 format in a dialog window
    public String getDescription() {
        return fileDescription + " (*." + fileExtension + ")";
    }
}
