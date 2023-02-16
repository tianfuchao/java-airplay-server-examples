package com.github.serezhka.jap2s.vlcj;

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
public class VLCJPlayerApp {


    private  AirPlayServer[] airPlayServer;
    private VLCJPlayer[] vlcjPlayer;

    @Value("${server.size}")
    int serverSize;
    @Value("${server.name}")
    String serverName;
    @Value("${airplay.port}")
    int airPlayPort;
    @Value("${airtunes.port}")
    int airTunesPort;
//    @Autowired
//    public VLCJPlayerApp(@Value("${server.name}") String serverName,
//                         @Value("${airplay.port}") int airPlayPort,
//                         @Value("${airtunes.port}") int airTunesPort,
//                         VLCJPlayer vlcjPlayer) {
//
//    }

    @Bean
    public VLCJPlayer[] initAirPlayServer() throws Exception{
        airPlayServer=new AirPlayServer[serverSize];
        vlcjPlayer=new VLCJPlayer[serverSize];
        for (int i = 0; i < serverSize; i++) {
            vlcjPlayer[i]=new VLCJPlayer(i);
            this.airPlayServer[i] = new AirPlayServer(serverName+i, airPlayPort+i, airTunesPort+i, vlcjPlayer[i]);
            airPlayServer[i].start();
        }
        return vlcjPlayer;
    }

    public static void main(String[] args) {
        new SpringApplicationBuilder(VLCJPlayerApp.class)
                .web(WebApplicationType.SERVLET)
                .headless(false)
                .run(args);
    }

//    @PostConstruct
//    private void postConstruct() throws Exception {
//        for (int i = 0; i < 3; i++) {
//            airPlayServer[i].start();
//        }
//
//        log.info("AirPlay server started!");
//    }

    @PreDestroy
    private void preDestroy() {
        for (int i = 0; i < 3; i++) {
            airPlayServer[i].stop();
        }
        log.info("AirPlay server stopped!");
    }
}
