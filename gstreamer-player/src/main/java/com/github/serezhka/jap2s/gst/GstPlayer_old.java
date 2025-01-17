package com.github.serezhka.jap2s.gst;

import com.github.serezhka.jap2lib.rtsp.AudioStreamInfo;
import com.github.serezhka.jap2lib.rtsp.VideoStreamInfo;
import com.github.serezhka.jap2server.AirplayDataConsumer;
import lombok.extern.slf4j.Slf4j;
import org.freedesktop.gstreamer.*;
import org.freedesktop.gstreamer.elements.AppSrc;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GstPlayer_old implements AirplayDataConsumer {

    private final Pipeline h264Pipeline;
//    private final Pipeline alacPipeline;
//    private final Pipeline aacEldPipeline;

    private final AppSrc h264Src;
//    private final AppSrc alacSrc;
//    private final AppSrc aacEldSrc;

//    private AudioStreamInfo.CompressionType audioCompressionType;
// gst-launch-1.0 -v tcpclientsrc host=127.0.0.1 port=5002 ! h264parse ! avdec_h264 ! videoconvert ! videorate ! capsfilter caps="video/x-raw, framerate=1/30" ! videoscale ! clockoverlay shaded-background=true font-desc="Sans, 24" ! timeoverlay ! jpegenc ! multifilesink location="frame.jpeg" max-files=0
    public GstPlayer_old() {
        h264Pipeline = (Pipeline) Gst.parseLaunch("appsrc name=h264-src ! h264parse ! avdec_h264 ! autovideosink sync=false");

        h264Src = (AppSrc) h264Pipeline.getElementByName("h264-src");
        h264Src.setStreamType(AppSrc.StreamType.STREAM);
        h264Src.setCaps(Caps.fromString("video/x-h264,colorimetry=bt709,stream-format=(string)byte-stream,alignment=(string)au"));
        h264Src.set("is-live", true);
        h264Src.set("format", Format.TIME);

        // 创建解码器元件
//        Element decoder = ElementFactory.make("jpegdec", "Decoder");

//        Element sink = ElementFactory.make("filesink", "sink");
//        sink.set("location", "D://screenshot.jpg");
//        sink.set("sync", "false");
//        h264Pipeline.addMany(sink);

//        h264Src.link(decoder);
//        h264Src.link(sink);

        h264Pipeline.play();


//        alacPipeline = (Pipeline) Gst.parseLaunch("appsrc name=alac-src ! avdec_alac ! audioconvert ! audioresample ! autoaudiosink sync=false"); // +
//
//        alacSrc = (AppSrc) alacPipeline.getElementByName("alac-src");
//        alacSrc.setStreamType(AppSrc.StreamType.STREAM);
//        alacSrc.setCaps(Caps.fromString("audio/x-alac,mpegversion=(int)4,channels=(int)2,rate=(int)44100,stream-format=raw,codec_data=(buffer)00000024616c616300000000000001600010280a0e0200ff00000000000000000000ac44"));
//        alacSrc.set("is-live", true);
//        alacSrc.set("format", Format.TIME);
//
//        alacPipeline.play();
//
//
//        aacEldPipeline = (Pipeline) Gst.parseLaunch("appsrc name=aac-eld-src ! avdec_aac ! audioconvert ! audioresample ! autoaudiosink sync=false"); // +
//
//        aacEldSrc = (AppSrc) aacEldPipeline.getElementByName("aac-eld-src");
//        aacEldSrc.setStreamType(AppSrc.StreamType.STREAM);
//        aacEldSrc.setCaps(Caps.fromString("audio/mpeg,mpegversion=(int)4,channnels=(int)2,rate=(int)44100,stream-format=raw,codec_data=(buffer)f8e85000"));
//        aacEldSrc.set("is-live", true);
//        aacEldSrc.set("format", Format.TIME);
//
//        aacEldPipeline.play();
    }

    Element sink;
    public void jt(){

        if(sink==null){
            sink = ElementFactory.make("filesink", "sink");
            sink.set("location", "screenshot1.jpg");
//        h264Pipeline.addMany(h264Src, sink);
            h264Src.link(sink);
        }
        h264Pipeline.pause();
        h264Pipeline.play();
//        h264Pipeline.setState(State.READY);
//        h264Pipeline.setState(State.PAUSED);
//        h264Pipeline.setState(State.PLAYING);

//        Element sink = ElementFactory.make("filesink", "sink");
//        sink.set("location", "screenshot1.jpg");
//        h264Src.link(sink);
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
//        Buffer buf = new Buffer(bytes.length);
//        buf.map(true).put(bytes);
//        // buf.setFlags(EnumSet.of(BufferFlags.LIVE));
//        switch (audioCompressionType) {
//            case ALAC:
//                alacSrc.pushBuffer(buf);
//                break;
//            case AAC_ELD:
//                aacEldSrc.pushBuffer(buf);
//                break;
//            default:
//                break;
//        }
    }

    @Override
    public void onVideoFormat(VideoStreamInfo videoStreamInfo) {
        log.info("onVideoFormat");
    }

    @Override
    public void onAudioFormat(AudioStreamInfo audioStreamInfo) {
        log.info("onAudioFormat: {}", audioStreamInfo.getAudioFormat());
//        this.audioCompressionType = audioStreamInfo.getCompressionType();
    }
}
