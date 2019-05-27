package com.duangframework.hotswap;

import com.duangframework.kit.PropKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.enums.ConstEnums;
import com.duangframework.server.common.BootStrap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.*;

/**
 * Created by laotang on 2019/5/27.
 */
public class DuangClassLoader extends ClassLoader {

    private static final Logger logger = LoggerFactory.getLogger(DuangClassLoader.class);

    /**
     * 判断是否已经找到了BinUrlClassLoader的class文件，主要是为减少判断的性能消耗
     */
    private boolean isFindDuangClassLoaderClass = false;
    private String baseDir;

    public DuangClassLoader() {
        super(DuangClassLoader.class.getClassLoader());
        init();
    }

    public DuangClassLoader(ClassLoader classLoader) {
        super(classLoader);
        init();
    }

    private void init() {
        String packagePath = PropKit.get(ConstEnums.PROPERTIES.BASE_PACKAGE_PATH.getValue());
        if(!BootStrap.getInstants().isDevModel() && ToolsKit.isEmpty(packagePath)) {
            logger.info("热部署功能只允许在开发环境下运行");
            return;
        }
        recursionClassFile(new File(packagePath));
    }

    /**
     * 遍历项目的class文件
     */
    private void recursionClassFile(File classPathFile) {
        if (classPathFile.isDirectory()) {
            File[] files = classPathFile.listFiles();
            for (int i = 0; i < files.length; i++) {
                File file = files[i];
                recursionClassFile(file);
            }
        } else if (classPathFile.getName().indexOf(".class") != -1) {
            getClassData(classPathFile);
        }
    }

    /**
     * 获取类数据
     */
    private void getClassData(File classPathFile) {
        try {
            InputStream fin = new FileInputStream(classPathFile);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            int bufferSize = 4096;
            byte[] buffer = new byte[bufferSize];
            int byteNumRead = 0;
            while ((byteNumRead = fin.read(buffer)) != -1) {
                bos.write(buffer, 0, byteNumRead);
            }
            byte[] classBytes = bos.toByteArray();
            defineClass(getClassName(classPathFile), classBytes, 0, classBytes.length);
        } catch (FileNotFoundException e) {
            logger.warn(e.getMessage(), e);
        } catch (IOException e) {
            logger.warn(e.getMessage(), e);
        }
    }

    /**
     * 获取类文件
     */
    private String getClassName(File classPathFile) {
        String classPath = classPathFile.getPath();
        String packagePath = classPath.replace(baseDir, "");
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
