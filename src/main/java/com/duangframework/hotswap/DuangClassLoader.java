package com.duangframework.hotswap;

import com.duangframework.kit.PathKit;
import com.duangframework.kit.PropKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.server.common.BootStrap;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.*;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by laotang on 2019/5/27.
 */
public class DuangClassLoader extends ClassLoader {

    private static final Logger logger = LoggerFactory.getLogger(DuangClassLoader.class);


    /**
     * 判断是否已经找到了BinUrlClassLoader的class文件，主要是为减少判断的性能消耗
     */
    private boolean isFindDuangClassLoaderClass = false;
    private String BASE_PACKAGE_PATH;
    private static final String CLASS_EXTNAME = ".class";

    /**
     * 所有业务类的名称
     */
    private Set<String> CLASSLOADER_SET;


    public DuangClassLoader() {
        super(DuangClassLoader.class.getClassLoader());
        init();
    }

    public DuangClassLoader(ClassLoader classLoader) {
        super(classLoader);
        init();
    }


    private static String getBasePackagePath(String packagePath){
        return PathKit.getWebRootPath() + File.separator + "target" + File.separator + "classes" + File.separator + packagePath.replace(".", File.separator);
    }

    private void init() {
        String packagePath = PropKit.get(ConstEnums.PROPERTIES.BASE_PACKAGE_PATH.getValue());
        packagePath = getBasePackagePath(packagePath);
        if(!BootStrap.getInstants().isDevModel() && ToolsKit.isEmpty(packagePath)) {
            logger.info("热部署功能只允许在开发环境下运行");
            return;
        }
        CLASSLOADER_SET = new HashSet<>();
        scanClass2File(packagePath);
    }

    public Set<String> getClassKeySet() {
        return CLASSLOADER_SET;
    }
    private FileFilter ClassFileFilter(File dir, String extName) {
        return new FileFilter() {
            @Override
            public boolean accept(File file){
                if (CLASS_EXTNAME.equalsIgnoreCase(extName)) {
                    return ((file.isFile()) && (file.getName().endsWith(extName))) || (file.isDirectory());
                }
                throw new IllegalArgumentException();
            }
        };
    }

    /**
     * 遍历项目的class文件
     */
    private void scanClass2File(String folderPath)
    {
        File dir = new File(folderPath);
        if ((!dir.exists()) || (!dir.isDirectory())) {
            throw new IllegalArgumentException(dir.getAbsolutePath() + " is not exists or not is directory!");
        }
        File[] files = dir.listFiles(ClassFileFilter(dir, CLASS_EXTNAME));
        for (int i = 0; i < files.length; i++) {
            File file = files[i];
            if (file.isDirectory()) {
                scanClass2File(file.getAbsolutePath());
            } else if (file.isFile()) {
//                String classPath = getClassAbsolutePath(file);
//                String classKey = classPath + ".class";
//                if (classPath.startsWith(BASE_PACKAGE_PATH)) {
                    getClassData(file.getAbsoluteFile());
//                }
            }
        }
    }

    private static String getClassAbsolutePath(File file) {
        String classPath = file.getAbsolutePath().replaceAll("\\\\", "/");
        classPath = classPath.substring(classPath.indexOf("/classes") + "/classes".length() + 1, classPath.length() - 6);
        classPath = classPath.replaceAll("/", ".");
        return classPath;
    }

    /**
     * 获取类数据
     */
    private void getClassData(File classPathFile) {
        try {
            String classKey = getClassAbsolutePath(classPathFile);
            loadClass(classKey,true);
            /*
            InputStream fin = new FileInputStream(classPathFile);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int bufferSize = 4096;
            byte[] buffer = new byte[bufferSize];
            int byteNumRead = 0;
            while ((byteNumRead = fin.read(buffer)) != -1) {
                bos.write(buffer, 0, byteNumRead);
            }
            byte[] classBytes = bos.toByteArray();
            String classKey = getClassAbsolutePath(classPathFile);
            System.out.println(classKey);
            if(!"com.signetz.openapi.dto.v2.python.BaseEntity".equalsIgnoreCase(classKey)) {
                defineClass(classKey, classBytes, 0, classBytes.length);
            }
            */
            CLASSLOADER_SET.add(classKey);
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
        }

    }

    /**
     * 获取类文件
     */
    private String getClassName(File classPathFile) {
        String classPath = classPathFile.getPath();
        String packagePath = classPath.replace(BASE_PACKAGE_PATH, "");
        String className = packagePath.replace("\\", ".").substring(1);
        return className.replace(".class", "");
    }


    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class cls = null;
        cls = findLoadedClass(name);
        if (cls == null) {
//            cls = getSystemClassLoader().loadClass(name);
//            System.out.println(getSystemClassLoader().toString());
//            System.out.println(getParent());
            cls=getParent().loadClass(name);

        }
        if (cls == null) {
            throw new ClassNotFoundException(name);
        }
        if (resolve) {
            resolveClass(cls);
        }
        return cls;
    }

}
