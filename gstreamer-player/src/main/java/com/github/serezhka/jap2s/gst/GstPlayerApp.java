package com.github.serezhka.jap2s.gst;

import com.github.serezhka.jap2server.AirPlayServer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

@Slf4j
@SpringBootApplication
public class GstPlayerApp {

    private AirPlayServer airPlayServer;

    @Autowired
    public GstPlayerApp(@Value("${server.name}") String serverName,
                        @Value("${airplay.port}") int airPlayPort,
                        @Value("${airtunes.port}") int airTunesPort,
                        GstPlayer gstPlayer) {
        this.airPlayServer = new AirPlayServer(serverName, airPlayPort, airTunesPort, gstPlayer);
    }

    @Bean
    public GstPlayer initAriplay(@Value("${server.name}") String serverName,
                                 @Value("${airplay.port}") int airPlayPort,
                                 @Value("${airtunes.port}") int airTunesPort) {
        GstPlayer gstPlayer = new GstPlayer();
        airPlayServer = new AirPlayServer(serverName, airPlayPort, airTunesPort, gstPlayer);
        return gstPlayer;
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(GstPlayerApp.class)
//                .web(WebApplicationType.NONE)
                .web(WebApplicationType.SERVLET)
                .headless(false)
                .run(args);
    }

    @PostConstruct
    private void postConstruct() throws Exception {
        airPlayServer.start();
        log.info("AirPlay server started!");
    }

    @PreDestroy
    private void preDestroy() {
        airPlayServer.stop();
        log.info("AirPlay server stopped!");
    }
}
