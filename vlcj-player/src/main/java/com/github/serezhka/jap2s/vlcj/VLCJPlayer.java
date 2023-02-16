package com.github.serezhka.jap2s.vlcj;

import com.github.serezhka.jap2lib.rtsp.AudioStreamInfo;
import com.github.serezhka.jap2lib.rtsp.VideoStreamInfo;
import com.github.serezhka.jap2server.AirplayDataConsumer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import uk.co.caprica.vlcj.factory.MediaPlayerFactory;
import uk.co.caprica.vlcj.log.LogLevel;
import uk.co.caprica.vlcj.log.NativeLog;
import uk.co.caprica.vlcj.media.callback.nonseekable.NonSeekableInputStreamMedia;
import uk.co.caprica.vlcj.player.base.MediaPlayer;
import uk.co.caprica.vlcj.player.component.EmbeddedMediaPlayerComponent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;

@Slf4j
public class VLCJPlayer implements AirplayDataConsumer {

    private EmbeddedMediaPlayerComponent mediaPlayerComponent;
    private JFrame f;

    private final PipedOutputStream output;
    private PipedInputStream input;
    private NonSeekableInputStreamMedia nsism;

    private MediaPlayerFactory mediaPlayerFactory;
    private NativeLog nativeLog;
    private int deviceIndex;

    public VLCJPlayer(int deviceIndex) {
        this.deviceIndex=deviceIndex;
        output = new PipedOutputStream();

        new MediaThread().start();

        log.info("VLCJ Player started!");
    }

    class MediaThread extends Thread{
        @Override
        public void run() {
            super.run();
            mediaPlayerFactory = new MediaPlayerFactory( "--demux=h264", "--h264-fps=30","--file-caching=100","--network-caching=100");

//            nativeLog = mediaPlayerFactory.application().newLog();
//            nativeLog.setLevel(LogLevel.DEBUG);
//            nativeLog.addLogListener((level, module, file, line, name, header, id, message) ->
//                    log.debug("[VLCJ] [{}] [{}] {} {}", level, module, name, message));

            mediaPlayerComponent = new EmbeddedMediaPlayerComponent(mediaPlayerFactory, null, null, null, null);

            f = new JFrame("Test Player");
            f.setSize(320, 480);
            f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            f.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    mediaPlayerComponent.release();
                }
            });
            f.setContentPane(mediaPlayerComponent);
            f.setVisible(true);

            try {
                input = new PipedInputStream(output);
            } catch (IOException e) {
                throw new RuntimeException();
            }

            nsism = new NonSeekableInputStreamMedia(1024) {

                @Override
                protected InputStream onOpenStream() {
                    return input;
                }

                @Override
                protected void onCloseStream(InputStream inputStream) throws IOException {
                    inputStream.close();
                }

                @Override
                protected long onGetSize() {
                    return 0;
                }
            };

//            mediaPlayerComponent.mediaPlayer().media().play("tcp://localhost:5002","--demux=h264", "--h264-fps=30","--rtsp-tcp","--network-caching=100");
            mediaPlayerComponent.mediaPlayer().media().play(nsism);
            mediaPlayerComponent.mediaPlayer().controls().play();

//            MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
//            MediaPlayer mediaPlayer = mediaPlayerFactory.mediaPlayers().newMediaPlayer();
//            mediaPlayer.media().play("tcp://localhost:5002");
        }
    }



    /**
     * 是否显示
     * @param isVisible
     */
    public void miediaVisible(boolean isVisible){
        f.setVisible(isVisible);
    }

    /**
     * 保存屏幕画面
     * @return
     */
    public File saveImage(){
        File snapshotDirectory = new File( System.getProperty("user.home") );
        File snapshotFile = new File(snapshotDirectory, "snapshot-" +deviceIndex+ ".png");
        mediaPlayerComponent.mediaPlayer().snapshots().save(snapshotFile);
        return snapshotFile;
    }

    @Override
    public void onVideo(byte[] video) {
        try {
            output.write(video);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAudio(byte[] audio) {
    }

    @Override
    public void onVideoFormat(VideoStreamInfo videoStreamInfo) {
    }

    @Override
    public void onAudioFormat(AudioStreamInfo audioInfo) {
    }
}
