package interfaces.impl;

import java.io.File;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javazoom.jlgui.basicplayer.BasicPlayer;
import javazoom.jlgui.basicplayer.BasicPlayerEvent;
import javazoom.jlgui.basicplayer.BasicPlayerException;
import interfaces.PlayControlListener;
import interfaces.Player;
import objects.BasicPlayerListenerAdapter;
import utils.FileUtils;

// implementation of a player for mp3 files
public class MP3Player implements Player {

    public static final String MP3_FILE_EXTENSION = "mp3";
    public static final String MP3_FILE_DESCRIPTION = "Файлы mp3";
    public static int MAX_VOLUME = 100;

    private long duration; // song duration in seconds
    private int bytesLen; // song size in bytes

    private final BasicPlayer basicPlayer = new BasicPlayer();// using library for playing mp3 files
    private String currentFileName;// current song
    private double currentVolume;

    private long secondsAmount; // seconds song already played

    private final PlayControlListener playControlListener;

    public MP3Player(PlayControlListener playControlListener) {
        this.playControlListener = playControlListener;
      
        basicPlayer.addBasicPlayerListener(new BasicPlayerListenerAdapter() {
           
            @Override
            public void progress(int bytesread, long microseconds, byte[] pcmdata, Map properties) {

                float progress = -1.0f;

                if ((bytesread > 0) && ((duration > 0))) {
                    progress = bytesread * 1.0f / bytesLen * 1.0f;
                }

                // сколько секунд прошло
                secondsAmount = (long) (duration * progress);

                if (duration != 0) {
                    int length = ((int) Math.round(secondsAmount * 1000 / duration));
                    MP3Player.this.playControlListener.processScroll(length);
                }
            }

            @Override
            public void opened(Object o, Map map) {
                duration = (long) Math.round((((Long) map.get("duration"))) / 1000000);
                bytesLen = (int) Math.round(((Integer) map.get("mp3.length.bytes")));

                // if map has a tag with mp3 song name - get it, if not - using the file name to get a song name
                String songName = map.get("title") != null ? map.get("title").toString() : FileUtils.getFileNameWithoutExtension(new File(o.toString()).getName());

                // if name of the song is too long - make it shorter
                if (songName.length() > 30) {
                    songName = songName.substring(0, 30) + "...";
                }

                MP3Player.this.playControlListener.playStarted(songName);

            }

            @Override
            public void stateUpdated(BasicPlayerEvent bpe) {
                int state = bpe.getCode();

                if (state == BasicPlayerEvent.EOM) {
                    MP3Player.this.playControlListener.playFinished();
                }

            }

        });
    }

    @Override
    public void play(String fileName) {

        try {
            // if continue to play song after it was paused
            if (currentFileName != null && currentFileName.equals(fileName) && basicPlayer.getStatus() == BasicPlayer.PAUSED) {
                basicPlayer.resume();
                return;
            }

            File mp3File = new File(fileName);

            currentFileName = fileName;
            basicPlayer.open(mp3File);
            basicPlayer.play();
            basicPlayer.setGain(currentVolume);

        } catch (BasicPlayerException ex) {
            Logger.getLogger(MP3Player.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    private double calcVolume(double currentValue) {
        currentVolume = (double) currentValue / MAX_VOLUME;
        return currentVolume;
    }

    @Override
    public void stop() {
        try {
            basicPlayer.stop();
        } catch (BasicPlayerException ex) {
            Logger.getLogger(MP3Player.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void pause() {
        try {
            basicPlayer.pause();
        } catch (BasicPlayerException ex) {
            Logger.getLogger(MP3Player.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // controls volume level
    @Override
    public void setVolume(double controlValue) {
        try {

            currentVolume = calcVolume(controlValue);
            basicPlayer.setGain(currentVolume);

        } catch (BasicPlayerException ex) {
            Logger.getLogger(MP3Player.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void jump(double controlPosition) {
        try {
            long skipBytes = (long) Math.round(((Integer) bytesLen) * controlPosition);
            basicPlayer.seek(skipBytes);
            basicPlayer.setGain(currentVolume);
        } catch (BasicPlayerException ex) {
            Logger.getLogger(MP3Player.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}