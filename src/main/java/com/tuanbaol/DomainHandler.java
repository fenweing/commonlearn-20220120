package com.tuanbaol;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class DomainHandler {
    static String plusIp = "10.185.35.254";
    static String vipIp = "10.185.43.254";
    static String plusPermanentRoute = "  255.255.255.255    10.185.35.254";
    static String PLUS_ROUTE = "0.0.0.0          0.0.0.0    10.185.35.254    10.185.32.104";
    static String VIP_ROUTE = "0.0.0.0          0.0.0.0    10.185.43.254     10.185.42.29";
    static String routeFilePath = "D:\\log\\routeRes.txt";
    static String domainFilePath = "D:\\log\\domain.txt";

    public static void main(String[] args) {
        Integer period = null;
        if (args != null && args.length > 0) {
            period = Integer.valueOf(args[0]);
        }
        handle(period);
    }

    public static void handle(Integer argPeriod) {
        int period = 2;
        period = argPeriod == null ? period : argPeriod;
        LogHelper.printToLog("域名处理，扫描间隔" + period + "分钟。");
        //定时任务扫描
        Timer timer = new Timer("domain_handle_thread");
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                try {
                    LogHelper.printToLog("开始扫描域名。。。");
                    //读取域名文件为行
                    List<String> domainList = readDomain();
                    if (domainList.size() <= 0) {
                        return;
                    }
                    //获取已有路由
                    Runtime runtime = Runtime.getRuntime();
                    String[] cmd = new String[]{"cmd", "/c", "route print"};
                    Process process = runtime.exec(cmd);
                    String currRoute = LogHelper.streamToString(process.getInputStream());
                    if (currRoute.contains(VIP_ROUTE)) {
                        return;
                    }
                    //循环ping每行域名，判断并加入路由和文件
                    for (String domain : domainList) {
                        String ip = null;
                        if (isValidIp(domain)) {
                            //domain 就是ip地址
                            ip = domain;
                        } else {
                            //domain为域名
                            String[] pingCmd = new String[]{"cmd", "/c", "ping " + domain};
                            process = runtime.exec(pingCmd);
                            String pingRes = LogHelper.streamToString(process.getInputStream());
                            if (pingRes == null || !pingRes.contains("的回复")) {
                                continue;
                            }
                            ip = resolveIpFromPingRes(pingRes, domain);
                        }
                        if (ip == null || ip == "") {
                            continue;
                        }
                        String permanentRoute = ip + plusPermanentRoute;
                        if (currRoute.contains(permanentRoute)) {
                            continue;
                        }
                        addPermanentRoute(ip);
                        addPermanentRouteToFile(ip);
                    }
                } catch (
                        Exception e) {
                    LogHelper.printToLog(e.getMessage());
                }
            }
        };
        timer.schedule(task, 5000, period * 60_000);
    }

    private static List<String> readDomain() {
        List<String> domainList = new ArrayList<>();
        try {
            FileReader fileReader = new FileReader(domainFilePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.equals("")) {
                    continue;
                }
                if (line.startsWith("http://")) {
                    line = line.substring(7);
                }
                if (line.startsWith("https://")) {
                    line = line.substring(8);
                }
                domainList.add(line);
            }
            return domainList;
        } catch (Exception e) {
            LogHelper.printToLog(e.getMessage());
            return domainList;
        }
    }

    private static String resolveIpFromPingRes(String pingRes, String domain) {
        try {
            String pattern = "[";
            int beginIndex = pingRes.indexOf(pattern), endIndex = pingRes.indexOf("]");

            if (beginIndex == -1 || endIndex == -1) {
                return "";
            }
            String ip = pingRes.substring(beginIndex + 1, endIndex);
            if (!isValidIp(ip)) {
                return "";
            }
            return ip;
        } catch (Exception e) {
            LogHelper.printToLog(e.getMessage());
            return "";
        }
    }

    private static boolean isValidIp(String ip) {
        try {
            String[] splits = ip.split("\\.");
            if (splits.length != 4) {
                LogHelper.printToLog("不合法ip：" + ip);
                return false;
            }
            for (int i = 0; i < splits.length; i++) {
                String split = splits[i];
                if (!isNum(split)) {
                    return false;
                }
                int part = Integer.valueOf(split);
                if (part < 0 || part > 255) {
                    LogHelper.printToLog("不合法ip：" + ip);
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void addPermanentRouteToFile(String ip) {
        File file = new File(routeFilePath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {

            }
        }
        String routeRes = "route ADD " + ip + " MASK 255.255.255.255 10.185.35.254 -p";
        try (FileOutputStream fos = new FileOutputStream(file, true)) {
            fos.write(routeRes.getBytes("gbk"));
            fos.write("\r\n".getBytes());
        } catch (Exception e) {

        }
    }

    private static void addPermanentRoute(String ip) throws IOException {
        Runtime runtime = Runtime.getRuntime();
        String[] addPlus = new String[]{"cmd", "/c", "route ADD " + ip + " MASK 255.255.255.255 10.185.35.254 -p"};
        Process addProcess = runtime.exec(addPlus);
        LogHelper.printToLog("添加路由" + ip + "错误: " + LogHelper.streamToString(addProcess.getErrorStream()));
        LogHelper.printToLog("添加路由" + ip + "返回: " + LogHelper.streamToString(addProcess.getInputStream()));
    }

    private static boolean isNum(String src) {
        if (src == null || src.length() == 0) {
            return false;
        }
        char[] chars = src.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char aChar = chars[i];
            if (aChar + 0 < 48 || aChar + 0 > 57) {
                return false;
            }
        }
        return true;
    }
}
