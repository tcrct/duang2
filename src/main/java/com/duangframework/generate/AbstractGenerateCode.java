package com.duangframework.generate;

import com.duangframework.db.IdEntity;
import com.duangframework.kit.ClassKit;
import com.duangframework.kit.ObjectKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.core.helper.ClassHelper;
import com.duangframework.mvc.http.enums.ConstEnums;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public abstract  class AbstractGenerateCode {

    private static final String TEMPLATE_DIR_FIDLE = "template/";
    protected static final String CONTROLLER_FIDLE = "Controller";
    protected static final String SERVICE_FIDLE = "Service";
    protected static final String CACHE_SERVICE_FIDLE = "CacheService";
    protected static final String CACHE_KEY_ENUM_FIDLE = "CacheKeyEnum";
    protected static final String CACHE_FIDLE = "cache";
    protected static final String CACHE_ENUMS_FIDLE = "enums";


    protected static final Map<String, String> templateMap = new HashMap<>();
    protected static final Map<String,String> replaceValueMap = new HashMap<>();
    protected GenerateCodeModel model;


    public AbstractGenerateCode(GenerateCodeModel model) {
        this.model = model;
        getTemplateCode2Map();
        getReplaceValueMap();
    }



    protected void getReplaceValueMap() {
        Map<String,Object> map = ObjectKit.getFieldMap(model);
        if(ToolsKit.isNotEmpty(map) && replaceValueMap.isEmpty()) {
            for(Iterator<Map.Entry<String,Object>> iterator = map.entrySet().iterator(); iterator.hasNext();) {
                Map.Entry<String,Object> entry = iterator.next();
                replaceValueMap.put("${"+entry.getKey()+"}", entry.getValue()+"");
            }
        }
    }

    private void getTemplateCode2Map() {
        if(!templateMap.isEmpty()) {
            return;
        }
        try {
            URL url = GenerateCode.class.getClassLoader().getResource(TEMPLATE_DIR_FIDLE);
            String jarPath = url.toString().substring(0, url.toString().indexOf("!/") + 2);
            URL jarURL = new URL(jarPath);
            JarURLConnection jarCon = (JarURLConnection) jarURL.openConnection();
            JarFile jarFile = jarCon.getJarFile();
            Enumeration<JarEntry> jarEntrys = jarFile.entries();
            while (jarEntrys.hasMoreElements()) {
                JarEntry entry = jarEntrys.nextElement();
                String name = entry.getName();
                if (name.startsWith(TEMPLATE_DIR_FIDLE) && !entry.isDirectory()) {
                    InputStream input = GenerateCode.class.getClassLoader().getResourceAsStream(name);
                    if(ToolsKit.isNotEmpty(input)) {
                        String key = name.substring(name.lastIndexOf("/")+1, name.lastIndexOf("."));
                        templateMap.put(key, IOUtils.toString(input));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    protected abstract String build();
    protected abstract File file();

    protected File getFile(String subPackage, String type) {
        String packagePath = model.getBasePackage()+ ".generate";
        packagePath = packagePath.replace(".", File.separator);
        String path = model.getSourceDirPath()+File.separator+ packagePath+File.separator+ subPackage +File.separator+ model.getEntityName() + type + ".java";
        return new File(path);
    }

    protected String getSourceCode(String sourctCode) {
        if(replaceValueMap.isEmpty()){
            return "";
        }
        for(Iterator<Map.Entry<String,String>> iterator = replaceValueMap.entrySet().iterator(); iterator.hasNext();) {
            Map.Entry<String,String> entry = iterator.next();
            sourctCode = sourctCode.replace(entry.getKey(), entry.getValue());
        }
        return sourctCode;
    }

    public void generate() {
        String descText = build();
        System.out.println(descText);
        File file = file();
        try {
            String filePath = file.getAbsolutePath();
            File dir = new File(filePath.substring(0, filePath.lastIndexOf(File.separator)));
            if(!dir.exists()) {
                dir.mkdirs();
                System.out.println("directory is not exists, create it...");
            }
            FileUtils.writeStringToFile(file, descText, Charset.forName(ConstEnums.DEFAULT_CHAR_ENCODE.getValue()));
            System.out.println("build ["+filePath+"] is success");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
