package objects;

import java.util.Map;
import javazoom.jlgui.basicplayer.BasicController;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerListener;


// class-adapter for overriding only necessary files in anonymous classes
public class BasicPlayerListenerAdapter implements BasicPlayerListener{

    @Override
    public void opened(Object o, Map map) {
        
    }

    @Override
    public void progress(int i, long l, byte[] bytes, Map map) {
        
    }

    @Override
    public void stateUpdated(BasicPlayerEvent bpe) {
      
    }

    @Override
    public void setController(BasicController bc) {
        
    }

}
