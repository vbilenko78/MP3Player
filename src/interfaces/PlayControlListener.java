package interfaces;

public interface PlayControlListener {
    
    void playStarted(String name);
    
    void processScroll(int position);
    
    void playFinished();
    
}
