package com.github.serezhka.jap2s.gst;

import com.github.serezhka.jap2lib.rtsp.AudioStreamInfo;
import com.github.serezhka.jap2lib.rtsp.VideoStreamInfo;
import com.github.serezhka.jap2server.AirplayDataConsumer;
import lombok.extern.slf4j.Slf4j;
import org.freedesktop.gstreamer.*;
import org.freedesktop.gstreamer.elements.AppSink;
import org.freedesktop.gstreamer.elements.AppSrc;
import org.springframework.stereotype.Component;

import java.nio.ByteBuffer;

@Slf4j
@Component
public class GstPlayer implements AirplayDataConsumer {

    private final Pipeline h264Pipeline;
//    private final Pipeline alacPipeline;
//    private final Pipeline aacEldPipeline;

    private final AppSrc h264Src;

    private AppSinkCallback sinkCallback;
//    private final AppSrc alacSrc;
//    private final AppSrc aacEldSrc;

    //    private AudioStreamInfo.CompressionType audioCompressionType;
// gst-launch-1.0 -v tcpclientsrc host=127.0.0.1 port=5002 ! h264parse ! avdec_h264 ! videoconvert ! videorate ! capsfilter caps="video/x-raw, framerate=1/30" ! videoscale ! clockoverlay shaded-background=true font-desc="Sans, 24" ! timeoverlay ! jpegenc ! multifilesink location="frame.jpeg" max-files=0
    public GstPlayer() {
//        h264Pipeline = (Pipeline) Gst.parseLaunch("appsrc name=h264-src ! h264parse ! avdec_h264 ! autovideosink sync=false");

        h264Pipeline = (Pipeline) Gst.parseLaunch("appsrc name=h264-src ! queue ! h264parse ! queue ! avdec_h264 ! videoconvert ! videorate ! capsfilter caps=\"video/x-raw, framerate=1/30\" ! videoscale ! jpegenc quality=100 ! appsink name=sink sync=false async=false");
        h264Src = (AppSrc) h264Pipeline.getElementByName("h264-src");
        h264Src.setStreamType(AppSrc.StreamType.STREAM);
        h264Src.setCaps(Caps.fromString("video/x-h264,colorimetry=bt709,stream-format=(string)byte-stream,alignment=(string)au"));
        h264Src.set("is-live", true);
        h264Src.set("format", Format.TIME);

        sinkCallback = new AppSinkCallback();
        AppSink appsinkElement = (AppSink) h264Pipeline.getElementByName("sink");
        appsinkElement.set("emit-signals", true);
        appsinkElement.set("max-buffers", 1);
        appsinkElement.set("drop", true);
        appsinkElement.connect(sinkCallback);
        h264Pipeline.play();
    }

    public byte[] screenshot() {
        if (sinkCallback != null) {
            return sinkCallback.getCurBuffer();
        }else {
            return null;
        }
    }

    class AppSinkCallback implements AppSink.NEW_SAMPLE {

        Buffer buffer;

        @Override
        public FlowReturn newSample(AppSink elem) {
            Sample sample = elem.pullSample();
            buffer = sample.getBuffer();
//            log.info("获取帧信息");
            return FlowReturn.OK;
////            byte[]
//            byte[] bf= buffer.map(false).array();
//            return null;
        }

        public byte[] getCurBuffer() {
            if (buffer == null) {
                return null;
            }
            ByteBuffer bb=  buffer.map(false);
            byte[] data = new byte[bb.remaining()];
            bb.get(data);
            return data;
        }
    }

    @Override
    public void onVideo(byte[] bytes) {
        Buffer buf = new Buffer(bytes.length);
        buf.map(true).put(bytes);
        // buf.setFlags(EnumSet.of(BufferFlags.LIVE));
        h264Src.pushBuffer(buf);
    }

    @Override
    public void onAudio(byte[] bytes) {
    }

    @Override
    public void onVideoFormat(VideoStreamInfo videoStreamInfo) {
        log.info("onVideoFormat");
    }

    @Override
    public void onAudioFormat(AudioStreamInfo audioStreamInfo) {
        log.info("onAudioFormat: {}", audioStreamInfo.getAudioFormat());
    }
}
