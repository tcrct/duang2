package com.duangframework.kit;

import com.duangframework.exception.MvcException;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.Enumeration;
import java.util.regex.Pattern;

/**
 * 1： getUri() 获取相对路径，例如   ..\path\abc.txt
 * 2： getAbsolutePath() 获取绝对路径，但可能包含 ".." 或 "." 字符，例如  D:\otherPath\..\path\abc.txt
 * 3： getCanonicalPath() 获取绝对路径，但不包含 ".." 或 "." 字符，例如  D:\path\abc.txt
 *
 * @author Created by laotang
 * @date createed in 2018/6/12.
 */
public final class PathKit {

    public static final String VAR_REGEXP = ":(\\w+)";
    public static final String VAR_REPLACE = "([^#/?.]+)";
    public static final Pattern VAR_REGEXP_PATTERN = Pattern.compile(VAR_REGEXP);
    private static final String SLASH = "/";
    private static final Pattern VAR_FIXPATH_PATTERN = Pattern.compile("\\s");
    private static String webRootPath;
    private static String rootClassPath;

    /**
     * 验证过滤路径，如果路径前没有 / 则添加，后有/则移除，并替换所有%20字符
     *
     * @param path
     * @return
     */
    public static String fixPath(String path) {
        if (null == path) {
            return SLASH;
        }
        if (path.charAt(0) != '/') {
            path = SLASH + path;
        }
        if (path.length() > 1 && path.endsWith(SLASH)) {
            path = path.substring(0, path.length() - 1);
        }
        if (!path.contains("\\s")) {
            return path;
        }
        return VAR_FIXPATH_PATTERN.matcher(path).replaceAll("%20");
    }

    public static String cleanPath(String path) {
        if (path == null) {
            return null;
        }
        return path.replaceAll("[/]+", SLASH);
    }

    /**
     * 根据class文件取该文件的绝对路径
     *
     * @param clazz
     * @return
     */
    public static String getPath(Class clazz) {
        String path = clazz.getResource("").getPath();
        return new File(path).getAbsolutePath();
    }

    /**
     * 根据包路径取出绝对路径
     *
     * @param packagePath
     * @return
     */
    public static String getPath(String packagePath) {
        packagePath = packagePath.contains(".") ? packagePath.replace(".", "/") : packagePath;
        String path = ClassKit.getClassLoader().getResource(fixPath(packagePath)).getPath();
        return new File(path).getAbsolutePath();
    }

    /**
     * 根据包路径取出该路径下的所有文件URL对象
     *
     * @param packagePath
     * @return
     */
    public static Enumeration<URL> getPaths(String packagePath) {
        packagePath = packagePath.startsWith("/") ? packagePath.substring(1, packagePath.length()) : packagePath;
        try {
            return ClassKit.getClassLoader().getResources(packagePath.replace(".", "/"));
        } catch (IOException e) {
            throw new MvcException(e.getMessage(), e);
        }
    }

    // 注意：命令行返回的是命令行所在的当前路径
    public static String getRootClassPath() {
        if (rootClassPath == null) {
            try {
                String path = ClassKit.getClassLoader().getResource("").toURI().getPath();
                rootClassPath = new File(path).getAbsolutePath();
            } catch (Exception e) {
                try {
                    String path = PathKit.class.getProtectionDomain().getCodeSource().getLocation().getPath();
                    path = java.net.URLDecoder.decode(path, "UTF-8");
                    rootClassPath = path;
                } catch (UnsupportedEncodingException e1) {
                    throw new RuntimeException(e1);
                }
            }
        }

        return fixPath(rootClassPath);
    }

    /**
     * 设置项目根路径
     *
     * @param rootClassPath
     */
    public static void setRootClassPath(String rootClassPath) {
        PathKit.rootClassPath = rootClassPath;
    }

    /***
     * 根据类对象返回包路径
     * @param clazz
     * @return
     */
    public static String getPackagePath(Class<?> clazz) {
        Package p = clazz.getPackage();
        return p != null ? p.getName().replaceAll("\\.", "/") : "";
    }

    public static String getWebRootPath() {
        if (webRootPath == null) {
            webRootPath = detectWebRootPath();
        }
        return webRootPath;
    }

    /**
     * 设置WebRoot路径
     *
     * @param webRootPath
     */
    public static void setWebRootPath(String webRootPath) {
        if (webRootPath == null) {
            return;
        }
        PathKit.webRootPath = fixPath(webRootPath);
    }

    // 注意：命令行返回的是命令行所在路径的上层的上层路径
    private static String detectWebRootPath() {
        try {
            String path = PathKit.class.getResource("/").toURI().getPath();
            return new File(path).getParentFile().getParentFile().getCanonicalPath();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
