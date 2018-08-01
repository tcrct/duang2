package com.duangframework.report;

import com.duangframework.utils.IpUtils;

import java.util.Properties;

/**
 * 计算机信息
 * @author  Created by laotang on 2017/9/19.
 */
public class ComputerInfo implements java.io.Serializable{

    private static final String  PUBLIC_IP_FIELD = "publicIp";
    private static final String  PRIVATE_IP_FIELD = "privateIp";
    private static final String  JAVA_HOME_FIELD = "javaHome";
    private static final String  JAVA_VERSION_FIELD = "javaVersion";
    private static final String  OS_NAME_FIELD = "osName";
    private static final String  OS_ARCH_FIELD = "osArch";
    private static final String  OS_VERSION_FIELD = "osVersion";

    private static Properties SYSTEM_PROPER =System.getProperties();

    private String publicIp;
    private String privateIp;
    private String javaHome;
    private String javaVersion;
    private String osName;
    private String osArch;
    private String osVersion;

    public ComputerInfo() {

    }

    public ComputerInfo(String publicIp, String privateIp, String javaHome, String javaVersion, String osName, String osArch, String osVersion) {
        this.publicIp = publicIp;
        this.privateIp = privateIp;
        this.javaHome = javaHome;
        this.javaVersion = javaVersion;
        this.osName = osName;
        this.osArch = osArch;
        this.osVersion = osVersion;
    }

    public static ComputerInfo getInstance() {
        return new ComputerInfo(
                IpUtils.getLocalHostIP(true),
                IpUtils.getLocalHostIP(false),
                SYSTEM_PROPER.getProperty("java.home"),
                SYSTEM_PROPER.getProperty("java.version"),
                SYSTEM_PROPER.getProperty("os.name"),
                SYSTEM_PROPER.getProperty("os.arch"),
                SYSTEM_PROPER.getProperty("os.version")
                );
    }

    public void setPublicIp(String publicIp) {
        this.publicIp = publicIp;
    }

    public void setPrivateIp(String privateIp) {
        this.privateIp = privateIp;
    }

    public void setJavaHome(String javaHome) {
        this.javaHome = javaHome;
    }

    public void setJavaVersion(String javaVersion) {
        this.javaVersion = javaVersion;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public void setOsArch(String osArch) {
        this.osArch = osArch;
    }

    public void setOsVersion(String osVersion) {
        this.osVersion = osVersion;
    }

    public String getPublicIp() {
        return IpUtils.getLocalHostIP(true);
    }

    public String getPrivateIp() {
        return IpUtils.getLocalHostIP(false);
    }

    public String getJavaHome() {
        return SYSTEM_PROPER.getProperty("java.home");
    }

    public String getJavaVersion() {
        return SYSTEM_PROPER.getProperty("java.version");
    }

    public String getOsName() {
        return SYSTEM_PROPER.getProperty("os.name");
    }

    public String getOsArch() {
        return SYSTEM_PROPER.getProperty("os.arch");
    }

    public String getOsVersion() {
        return SYSTEM_PROPER.getProperty("os.version");
    }

    public void all(){
        System.out.println("Java的运行环境版本："+ SYSTEM_PROPER.getProperty("java.version"));
        System.out.println("Java的运行环境供应商："+ SYSTEM_PROPER.getProperty("java.vendor"));
        System.out.println("Java供应商的URL："+ SYSTEM_PROPER.getProperty("java.vendor.url"));
        System.out.println("Java的安装路径："+ SYSTEM_PROPER.getProperty("java.home"));
        System.out.println("Java的虚拟机规范版本："+ SYSTEM_PROPER.getProperty("java.vm.specification.version"));
        System.out.println("Java的虚拟机规范供应商："+ SYSTEM_PROPER.getProperty("java.vm.specification.vendor"));
        System.out.println("Java的虚拟机规范名称："+ SYSTEM_PROPER.getProperty("java.vm.specification.name"));
        System.out.println("Java的虚拟机实现版本："+ SYSTEM_PROPER.getProperty("java.vm.version"));
        System.out.println("Java的虚拟机实现供应商："+ SYSTEM_PROPER.getProperty("java.vm.vendor"));
        System.out.println("Java的虚拟机实现名称："+ SYSTEM_PROPER.getProperty("java.vm.name"));
        System.out.println("Java运行时环境规范版本："+ SYSTEM_PROPER.getProperty("java.specification.version"));
        System.out.println("Java运行时环境规范供应商："+ SYSTEM_PROPER.getProperty("java.specification.vender"));
        System.out.println("Java运行时环境规范名称："+ SYSTEM_PROPER.getProperty("java.specification.name"));
        System.out.println("Java的类格式版本号："+ SYSTEM_PROPER.getProperty("java.class.version"));
        System.out.println("Java的类路径："+ SYSTEM_PROPER.getProperty("java.class.path"));
        System.out.println("加载库时搜索的路径列表："+ SYSTEM_PROPER.getProperty("java.library.path"));
        System.out.println("默认的临时文件路径："+ SYSTEM_PROPER.getProperty("java.io.tmpdir"));
        System.out.println("一个或多个扩展目录的路径："+ SYSTEM_PROPER.getProperty("java.ext.dirs"));
        System.out.println("操作系统的名称："+ SYSTEM_PROPER.getProperty("os.name"));
        System.out.println("操作系统的构架："+ SYSTEM_PROPER.getProperty("os.arch"));
        System.out.println("操作系统的版本："+ SYSTEM_PROPER.getProperty("os.version"));
        System.out.println("文件分隔符："+ SYSTEM_PROPER.getProperty("file.separator"));//在 unix 系统中是＂／＂ System.out.println("路径分隔符："+SYSTEM_PROPER.getProperty("path.separator"));//在 unix 系统中是＂:＂ System.out.println("行分隔符："+SYSTEM_PROPER.getProperty("line.separator"));//在 unix 系统中是＂/n＂ System.out.println("用户的账户名称："+SYSTEM_PROPER.getProperty("user.name"));
        System.out.println("用户的主目录："+ SYSTEM_PROPER.getProperty("user.home"));
        System.out.println("用户的当前工作目录："+ SYSTEM_PROPER.getProperty("user.dir"));
    }


}
