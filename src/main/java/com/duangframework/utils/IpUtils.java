package com.duangframework.utils;

import com.duangframework.kit.ToolsKit;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * Created by laotang on 2017/8/18.
 */
public class IpUtils {

    public static String getLocalHostIP() {
        return getLocalHostIP(true);
    }
    private static String  PUBLIC_IP = "";
    private static String  PRIVATE_IP = "";
    private static String  HIDE_IP = "";

    /**
     * 取本机IP
     * @param isPublic		是否取公网IP
     * @param isHide		是否隐藏前两位(*.*.12.34)
     * @return
     */
    public static String getLocalHostIP(boolean isPublic, boolean isHide) {
        String ip = "";
        if(isPublic) {
            if(ToolsKit.isEmpty(PUBLIC_IP)) {
                PUBLIC_IP = getLocalHostIP(true);
                ip = PUBLIC_IP;
            }
        } else {
            if(ToolsKit.isEmpty(PRIVATE_IP)) {
                PRIVATE_IP = getLocalHostIP(false);
                ip = PRIVATE_IP;
            }
        }
        if(isHide) {
            if(ToolsKit.isEmpty(HIDE_IP)) {
                ip = "*.*.*." + ip.substring(ip.lastIndexOf(".") + 1, ip.length());
            } else {
                ip = HIDE_IP;
            }
//			String[] ips = ip.split("\\.");
//			for(int i=2; i<ips.length; i++){
//				ip += ips[i]+".";
//			}
//			if(ip.endsWith(".")) ip = ip.substring(0, ip.length()-1);
        }
        return ip;
    }

    /**
     * 取本机IP地址
     * @param isPublicIp	是否取公网IP  为true时取公网IP，为false时取私网IP
     * @return
     */
    public static String getLocalHostIP(boolean isPublicIp) {
        try {
//	           InetAddress addr = InetAddress.getLocalHost();
//	           String ipAddress =  addr.getHostAddress();
//	           if (ipAddress.equals("127.0.0.1") && ipAddress.startsWith("10")) {
//	        	   ipAddress = getLocalMachineIp();
//	           }
//	           return ipAddress;
            return getLocalMachineIp(isPublicIp);
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    /**
     * 阿里云的ECS内网IP都是以10开头
     * @param isPublicIp
     * @return
     */
    private static String getLocalMachineIp(boolean isPublicIp) {
        InetAddressValidator validator = new InetAddressValidator();
        String candidate = new String();
        try {
            Enumeration<NetworkInterface> ifaces = NetworkInterface.getNetworkInterfaces();
            while (ifaces.hasMoreElements()) {
                NetworkInterface iface = (NetworkInterface) ifaces.nextElement();
                if (iface.isUp()) {
                    Enumeration<InetAddress> addresses = iface.getInetAddresses();
                    while (addresses.hasMoreElements()) {
                        InetAddress address = (InetAddress) addresses.nextElement();
                        if ((!address.isLinkLocalAddress()) && (address.getHostAddress() != null)) {
                            String ipAddress = address.getHostAddress();
                            if(isPublicIp){
                                if (!ipAddress.equals("127.0.0.1") && !ipAddress.startsWith("10")  && !ipAddress.startsWith("0")) {
                                    if (validator.isValidInet4Address(ipAddress)) {
                                        return ipAddress;
                                    }
                                }
                            } else {
                                if (!ipAddress.equals("127.0.0.1") && ipAddress.startsWith("10") ) {
                                    if (validator.isValidInet4Address(ipAddress)) {
                                        return ipAddress;
                                    }
                                }
                            }
                            if (validator.isValid(ipAddress)) {
                                candidate = ipAddress;
                            }
                        }
                    }
                }
            }
        } catch (SocketException localSocketException) {}
        return candidate;
    }
}
