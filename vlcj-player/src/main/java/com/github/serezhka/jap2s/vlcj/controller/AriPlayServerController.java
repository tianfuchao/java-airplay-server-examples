package com.github.serezhka.jap2s.vlcj.controller;

import com.github.serezhka.jap2s.vlcj.VLCJPlayer;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

@RestController
@RequestMapping("/aripaly")
public class AriPlayServerController {

    @Resource
    VLCJPlayer[] vlcjPlayers;

    @RequestMapping(value = "/screenshot", method = {RequestMethod.GET})
    public void screenshot(int index, HttpServletResponse response) {
        try {
            VLCJPlayer vlcjPlayer= vlcjPlayers[index];
            File file= vlcjPlayer.saveImage();
            byte[] bytes = file2Byte(file);
            response.setContentType("application/x-png");
            response.addHeader("Content-Disposition", "attachment;filename=screenshot.png");
//            response.setContentType("image/jpeg");
            OutputStream out = response.getOutputStream();
            out.write(bytes);
            out.flush();
            //关闭响应输出流
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static byte[] file2Byte(File file) {
        byte[] fileBytes = null;
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            fileBytes = new byte[(int) file.length()];
            fis.read(fileBytes);
            fis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return fileBytes;
    }

}
