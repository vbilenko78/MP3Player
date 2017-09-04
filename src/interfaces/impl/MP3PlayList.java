package interfaces.impl;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.TooManyListenersException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import gui.JListDropHandler;
import interfaces.PlayList;
import interfaces.Player;
import objects.MP3;
import utils.FileUtils;

// playlist based on JList
public class MP3PlayList implements PlayList {

    public static final String PLAYLIST_FILE_EXTENSION = "pls";
    public static final String PLAYLIST_FILE_DESCRIPTION = "Playlist";
    private static final String EMPTY_STRING = "";

    private final Player player;
    private final JList playlist;
    
    private DefaultListModel model = new DefaultListModel();

    public MP3PlayList(JList playlist, Player player) {
        this.playlist = playlist;
        this.player = player;
        initDragDrop();
        initPlayList();
    }

    @Override
    public void next() {
        int nextIndex = playlist.getSelectedIndex() + 1;
        // if still within list boundaries
        if (nextIndex <= model.getSize() - 1) {
            playlist.setSelectedIndex(nextIndex);
            playFile();
        }
    }

    @Override
    public void prev() {
        int nextIndex = playlist.getSelectedIndex() - 1;
        // if still within list boundaries
        if (nextIndex >= 0) {
            playlist.setSelectedIndex(nextIndex);
            playFile();
        }
    }

    @Override
    public boolean search(String name) {

        // if nothing was entered into search filed - exit the method and no search
        if (name == null || name.trim().equals(EMPTY_STRING)) {
            return false;
        }

        // all object indexes found by search will be saved in a collection
        ArrayList<Integer> mp3FindedIndexes = new ArrayList();

        // iterate through collection to find respective name from search field
        for (int i = 0; i < model.getSize(); i++) {
            MP3 mp3 = (MP3) model.getElementAt(i);
            // performing search with uppercase so user can use any case desired
            if (mp3.getName().toUpperCase().contains(name.toUpperCase())) {
                mp3FindedIndexes.add(i);// найденный индексы добавляем в коллекцию
            }
        }

        // collection of indexes saving into an array
        int[] selectIndexes = new int[mp3FindedIndexes.size()];
        // if didn't find any song matching search criteria
        if (selectIndexes.length == 0) {
            return false;
        }
        // convert collection into an array since method for selected lines in JList works with arrays only
        for (int i = 0; i < selectIndexes.length; i++) {
            selectIndexes[i] = mp3FindedIndexes.get(i);
        }

        // select songs in playlist by indexes found earlier
        playlist.setSelectedIndices(selectIndexes);

        return true;
    }

    @Override
    public boolean savePlaylist(File file) {
        try {
            String fileExtension = FileUtils.getFileExtension(file);

            // file name (if need to add an extension to the file name when saving song)
            String fileNameForSave = (fileExtension != null && fileExtension.equals(PLAYLIST_FILE_EXTENSION)) ? file.getPath() : file.getPath() + "." + PLAYLIST_FILE_EXTENSION;

            FileUtils.serialize(model, fileNameForSave);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public boolean openFiles(File[] files) {

        boolean status = false;

        for (File file : files) {
            MP3 mp3 = new MP3(file.getName(), file.getPath());

            // if song exists in a list - don't add it again
            if (!model.contains(mp3)) {
                model.addElement(mp3);
                status = true;
            }
        }

        return status;
    }

    @Override
    public void playFile() {
        // getting selected indexes (song's number) of the songs
        int[] indexPlayList = playlist.getSelectedIndices();
        // if at least one song was chosen
        if (indexPlayList.length > 0) {
            Object selectedItem = model.getElementAt(indexPlayList[0]);
            if (!(selectedItem instanceof MP3)) {
                return;
            }
            // searching for the first selected song (we can play only one song at a time)
            MP3 mp3 = (MP3) selectedItem;
            player.play(mp3.getPath());
        }

    }

    @Override
    public boolean openPlayList(File file) {
        try {
            DefaultListModel mp3ListModel = (DefaultListModel) FileUtils.deserialize(file.getPath());
            this.model = mp3ListModel;
            playlist.setModel(mp3ListModel);
            return true;
        } catch (Exception e) {
        }
        return false;
    }

    @Override
    public void delete() {
        // getting selected indexes (song's number) of the songs
        int[] indexPlayList = playlist.getSelectedIndices();
        // if at least one song was chosen
        if (indexPlayList.length > 0) {
            // first saving all mp3 for deletion into separate collection
            ArrayList<MP3> mp3ListForRemove = new ArrayList();
            // delete all selected song from a playlist
            for (int i = 0; i < indexPlayList.length; i++) {
                MP3 mp3 = (MP3) model.getElementAt(indexPlayList[i]);
                mp3ListForRemove.add(mp3);
            }

            // remove mp3 from a playlist
            mp3ListForRemove.forEach((mp3) -> {
                model.removeElement(mp3);
            });

        }
    }

    @Override
    public void clear() {
        model.clear();
    }

    private void initPlayList() {

        playlist.setModel(model);
        playlist.setToolTipText("Song List");

        playlist.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                // if double click
                if (evt.getModifiers() == InputEvent.BUTTON1_MASK && evt.getClickCount() == 2) {
                    playFile();
                }
            }
        });

        playlist.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                int key = evt.getKeyCode();
                if (key == KeyEvent.VK_ENTER) {
                    playFile();
                }
            }
        });
    }

    private DropTarget dropTarget;

    private void initDragDrop() {

        try {
            dropTarget = new DropTarget(playlist, DnDConstants.ACTION_COPY_OR_MOVE, null);
            dropTarget.addDropTargetListener(new JListDropHandler(playlist));

        } catch (TooManyListenersException ex) {
            Logger.getLogger(MP3PlayList.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}