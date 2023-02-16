package com.github.serezhka.jap2s.gst;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

@RestController
@RequestMapping("/aripaly")
public class Controller {

    @Resource
    GstPlayer gstPlayer;

    @RequestMapping(value = "/screenshot", method = {RequestMethod.GET})
    public void screenshot(HttpServletResponse response) {
        try {
            byte[] info=gstPlayer.screenshot();
            if(info==null){
                return;
            }
//            response.setContentType("application/x-png");
            response.addHeader("Content-Disposition", "attachment;filename=screenshot.jpg");
            response.setContentType("image/jpeg");
            OutputStream out = response.getOutputStream();
            out.write(info);
            out.flush();
            //关闭响应输出流
            out.close();
//            gstPlayer.jt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
