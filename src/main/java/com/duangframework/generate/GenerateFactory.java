package com.duangframework.generate;

import com.duangframework.kit.PathKit;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.net.URL;
import java.util.Enumeration;

/**
 * Created by laotang on 2019/1/30.
 */
public class GenerateFactory {

    public static void getControllerSource() {
//        System.out.println(PathKit.getRootClassPath());
//        System.out.println(PathKit.getWebRootPath());
//        System.out.println(PathKit.getWebRootPath());
//        System.out.println(PathKit.getPath(""));

        Enumeration<URL> urlEnumeration = PathKit.getPaths("com.duangframework.generate");
        while (urlEnumeration.hasMoreElements()) {
            URL classUrl = urlEnumeration.nextElement();
            String protocol = classUrl.getProtocol();
            System.out.println(classUrl+"                      "+protocol);
        }


        String path = PathKit.getPath(GenerateFactory.class);
        path += "/template/MainController.txt";
        File file = new File(path);
        System.out.println(file.getAbsoluteFile());
        try {
            String controllerText = FileUtils.readFileToString(file);
            System.out.println(controllerText);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
