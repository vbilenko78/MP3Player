package gui;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import objects.MP3;

public class JListDropHandler extends DropTargetAdapter {

    final JList jlist;

    public JListDropHandler(JList jlist) {
        this.jlist = jlist;
    }

  

    @Override
    public void drop(DropTargetDropEvent dtde) {

        Transferable transferable = dtde.getTransferable();
        if (dtde.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
            dtde.acceptDrop(dtde.getDropAction());
            try {
                
                List<File> transferData = (List<File>) transferable.getTransferData(DataFlavor.javaFileListFlavor);
                if (transferData != null && transferData.size() > 0) {
                    importMP3Files(transferData);
                    dtde.dropComplete(true);
                }

            } catch (UnsupportedFlavorException | IOException ex) {
            }
        } else {
            dtde.rejectDrop();
        }
    }

    private void importMP3Files(final List<File> files) {
        for (File file : files) {
            MP3 mp3 = new MP3(file.getName(), file.getPath());
            ((DefaultListModel)jlist.getModel()).addElement(mp3);
        }
    }

}
