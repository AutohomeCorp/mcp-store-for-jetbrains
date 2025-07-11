package com.autohome.mcpstore.utils;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import com.autohome.mcpstore.enums.OsEnum;
import com.intellij.openapi.application.ApplicationNamesInfo;

public class SystemUtil {

    public static String getLocalHostIP() {
        try {
            List<InetAddress> lst = new ArrayList<InetAddress>();
            String ipAddr = "255.255.255.255";
            InetAddress ip = null;

            Enumeration<NetworkInterface> allNetInterfaces = NetworkInterface.getNetworkInterfaces();
            while (allNetInterfaces.hasMoreElements()) {
                NetworkInterface netInterface = (NetworkInterface) allNetInterfaces.nextElement();
                Enumeration<InetAddress> addresses = netInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    ip = (InetAddress) addresses.nextElement();
                    if (ip != null && ip instanceof Inet4Address) {
                        lst.add(ip);
                    }
                }
            }
            if (lst.isEmpty()) {
                return ipAddr;
            }
            if (lst.size() == 1) {
                return lst.get(0).getHostAddress();
            }
            for (InetAddress inetAddress : lst) {
                ipAddr = inetAddress.getHostAddress();
                if (ipAddr.indexOf("127.0.0.1") == -1) {
                    return ipAddr;
                }
            }

            return ipAddr;
        } catch (SocketException e) {
            return "1270.0.1";
        }
    }

    public static String getLocalDeviceName() {
        try {
            return InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String getLoginUserName() {
        return System.getProperty("user.name");
    }

    public static OsEnum getOs() {
        String osName = System.getProperty("os.name").toLowerCase();
        if (osName.contains("win")) {
            return OsEnum.WINDOWS;
        } else if (osName.contains("mac")) {
            return OsEnum.MAC;
        } else if (osName.contains("nix") || osName.contains("nux") || osName.contains("aix")) {
            return OsEnum.LINUX;
        } else {
            return OsEnum.UNKNOWN;
        }
    }

    public static String getUserRoamingDataHome() {
        OsEnum os = getOs();
        switch (os) {
            case WINDOWS:
                return System.getenv("APPDATA");
            case MAC:
                return System.getProperty("user.home") + "/Library/Application Support";
            case LINUX:
                return System.getProperty("user.home") + "/.config";
            default:
                return null;
        }
    }

    public static String getUserHome() {
        return System.getProperty("user.home");
    }

    public static String getIdeName() {
        return ApplicationNamesInfo.getInstance().getProductName().toLowerCase();
    }
}
