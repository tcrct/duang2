package com.duangframework.utils;

import java.io.*;

/**
 * Created by laotang on 2019/7/21.
 */
public class Gbk2Utf8 {
    public static void main(String[] args) {
        transferAll("D:/sinosoft","src/java/com/sinosoft");
    }
    public static void transfer(String srcEncoding, File srctFile,  String targetEncoding, File targetFile){
//		String srcpath = "D:/sinosoft/common/schema/vo/Menu.java";
//		String targetpath = "src/java/com/sinosoft/common/schema/vo/Menu.java";
        BufferedReader bf=null;
        PrintWriter pw = null;
        try {
            FileInputStream fis = new FileInputStream(srctFile);
            bf = new BufferedReader(new InputStreamReader(fis,srcEncoding));
            pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(targetFile),targetEncoding),true);
            String text =null;
            while ((text=bf.readLine())!=null){
                pw.println(text);
            }
            pw.flush();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
            try {
                bf.close();
                pw.close();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }


    public static void transferAll(String srcDir,String targetDir){
        File srcDirFile = new File(srcDir);
        File targetDirFile = new File(targetDir);
//        File srcFiles [] =srcDirFile.listFiles();
//        File targetFiles [] =targetDirFile.listFiles();
        String[] srclist = srcDirFile.list();
        for(String path:srclist){
            File srcf = new File(srcDir+"/"+path);
            if(!srcf.isDirectory()){
//                System.out.println("-------------"+path);
                transfer("GBK", new File(srcDir+"/"+path), "UTF-8",new File(targetDir+"/"+path));
            }else{
//                System.out.println("==="+path);
                File targetf = new File(targetDir+"/"+path);
                if(!targetf.exists()){targetf.mkdir();}
                transferAll(srcDir+"/"+path,targetDir+"/"+path);
            }
        }
    }

}
