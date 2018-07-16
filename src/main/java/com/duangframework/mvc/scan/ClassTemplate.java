package com.duangframework.mvc.scan;

import com.duangframework.exception.MvcException;
import com.duangframework.kit.ClassKit;
import com.duangframework.kit.PathKit;
import com.duangframework.kit.ToolsKit;
import com.duangframework.mvc.http.enums.ConstEnums;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileFilter;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by laotang on 2018/6/16.
 */
public abstract class ClassTemplate {

    private static final Logger logger = LoggerFactory.getLogger(ClassTemplate.class);

    private static final Map<String, List<Class<?>>> allClassMap = new HashMap<>();

    protected String packageName;
    protected List<String> jarNames;

    protected ClassTemplate(String packageName, List<String> jarNames) {
        this.packageName = packageName;
        this.jarNames = ToolsKit.isEmpty(jarNames) ? new ArrayList<String>(1) : jarNames;
    }

    /**
     * 根据指定路径及jar文件前缀包名下的类对象集合
     * 如果类是抽象类或接口则不扫描到集合
     * @return
     */
    public List<Class<?>> getList() {
        String[] packagePathArray = packageName.split(",");
        List<Class<?>> classList = new ArrayList<>();
        for (String packagePathItem : packagePathArray) {
            List<Class<?>> classListItem = allClassMap.get(packagePathItem);
            if (ToolsKit.isEmpty(classListItem)) {
                classListItem = scanClass(packagePathItem, jarNames);
                if (ToolsKit.isNotEmpty(classListItem)) {
                    allClassMap.put(packagePathItem, classListItem);
                }
            }
            classList.addAll(classListItem);
        }
        return classList;
    }

    /**
     * 根据指定的包路径，取出所有在该路径下的类
     * @param packagePath       要扫描的类文件目录
     * @param jarNameList       允许扫描的JAR包前缀名，左匹配
     * @return
     */
    public List<Class<?>> scanClass(String packagePath, List<String> jarNameList) {
        Enumeration<URL> urlEnumeration = PathKit.getPaths(packagePath);
        if(ToolsKit.isEmpty(urlEnumeration)) {
            throw new MvcException("根据["+packagePath+"]路径取类时出错，该路径下没有类返回");
        }
        List<Class<?>> classList = new ArrayList<>();
        try {
            while (urlEnumeration.hasMoreElements()) {
                URL classUrl = urlEnumeration.nextElement();
                if (ToolsKit.isEmpty(classUrl)) {
                    continue;
                }
                String protocol = classUrl.getProtocol();
                if (ConstEnums.CLASS_URL_PROTOCOL_FILE_FIELD.getValue().equalsIgnoreCase(protocol)) {
                    String calssPath = classUrl.getPath().replaceAll("%20", " ");
                    addClass(classList, calssPath, packagePath);
                } else if (ConstEnums.CLASS_URL_PROTOCOL_JAR_FIELD.getValue().equalsIgnoreCase(protocol)) {
                    // 若在 jar 包中，则解析 jar 包中的 entry
                    JarURLConnection jarURLConnection = (JarURLConnection) classUrl.openConnection();
                    JarFile jarFile = jarURLConnection.getJarFile();
                    String jarFileName = jarFile.getName();
                    // 如果不是指定的jar包名(前缀匹配)则退出这次循环
                    int indexOf = jarFileName.lastIndexOf("/");
                    if(indexOf == -1) {
                        indexOf = jarFileName.lastIndexOf("\\");
                    }
                    if(indexOf == -1) {
                        throw new MvcException("取jar包取时出错");
                    }
                    jarFileName = jarFileName.substring(indexOf+1, jarFileName.length());
                    boolean isContains = false;
                    for(String key : jarNameList) {
                        if(jarFileName.startsWith(key)) {
                            isContains = true;
                            break;
                        }
                    }
                    if (!isContains) {
                        continue;
                    }
                    Enumeration<JarEntry> jarEntries = jarFile.entries();
                    while (jarEntries.hasMoreElements()) {
                        JarEntry jarEntry = jarEntries.nextElement();
                        String fileName = jarEntry.getName();
                        // 是class文件
                        if(fileName.endsWith(".class")) {
                            String subFileName = fileName.substring(fileName.lastIndexOf("/") + 1, fileName.length());
                            String filePkg = fileName.contains("/")? fileName.substring(0, fileName.length() - subFileName.length() - 1).replaceAll("/", ".") : "";
                            // 执行添加类操作
                            doAddClass(classList, filePkg, subFileName);
                        }
                    }
                }
            }
            return classList;
        } catch (Exception e) {
            logger.warn(e.getMessage(), e);
            return null;
        }
    }

    /**
     * 添加类到List集合
     * @param classList
     * @param classAbsolutePath     class类的绝对路径，不包括文件名
     * @param packageName
     */
    private void addClass(List<Class<?>> classList, String classAbsolutePath, String packageName) throws Exception {
        if (ToolsKit.isEmpty(classAbsolutePath)) {
            throw new NullPointerException("class类的绝对路径不能为空");
        }
        try {
            // 获取包名路径下的 文件或目录
            File[] files = new File(classAbsolutePath).listFiles(new FileFilter() {
                @Override
                public boolean accept(File pathName) {
                    // 如果是文件目录或是文件扩展名结尾含有.jar或.class
                    return pathName.isDirectory() ||
                            pathName.getName().endsWith(".jar") ||
                            pathName.getName().endsWith(".class");

                }
            });
            if (ToolsKit.isNotEmpty(files)) {
                // 遍历文件或目录
                for (File file : files) {
                    String fileName = file.getName();
                    // 判断是否为文件或目录
                    if (file.isFile()) {
                        // 执行添加类操作
                        doAddClass(classList, packageName, fileName);
                    } else {
                        // 子包路径
                        String subPackagePath = classAbsolutePath + "/" + fileName;
                        // 子包名
                        String subPackageName = packageName + "." + fileName;
                        // 递归调用
                        addClass(classList, subPackagePath, subPackageName);
                    }
                }
            }
        } catch (Exception e) {

        }
    }

    private void doAddClass(List<Class<?>> classList, String packageName, String fileName) throws Exception{
        if(fileName.endsWith(".class")) {
            // 获取类名, 移除扩展名
            String className = fileName.substring(0, fileName.lastIndexOf("."));
            if (ToolsKit.isEmpty(className)) {
                return;
            }
            Class<?> clazz = ClassKit.loadClass(ClassKit.getClassLoader().loadClass(packageName + "." + className), false);
            if(ToolsKit.isNotEmpty(clazz)) {
                checkAndAddClass(clazz, classList);
            }
        }
    }

    /**
     * 验证是否允许添加类
     */
    public abstract void checkAndAddClass(Class<?> clazz,  List<Class<?>> classList);
}
