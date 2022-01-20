package com.tuanbaol;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class DuplicateRouteHandler {
    static String PLUS_ROUTE = "0.0.0.0          0.0.0.0    10.185.35.254    10.185.32.104";
    static String VIP_ROUTE = "0.0.0.0          0.0.0.0    10.185.43.254     10.185.42.29";

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
        try {
            LogHelper.printToLog( "静态路由检查，扫描间隔" + period + "分钟。");
            Timer timer = new Timer("routeMonitorThread");
            //定时修改
            TimerTask timerTask = new TimerTask() {
                @Override
                public void run() {
                    try {
                        LogHelper.printToLog("开始检查。。。");
                        //获取当前路由
                        Runtime runtime = Runtime.getRuntime();
                        String[] cmd = new String[]{"cmd", "/c", "route print"};
                        Process process = runtime.exec(cmd);
                        String currRoute = LogHelper.streamToString(process.getInputStream());
                        //判断是否修改
                        if (currRoute.contains(PLUS_ROUTE) && currRoute.contains(VIP_ROUTE)) {
                            String[] delPlus = new String[]{"cmd", "/c", "route DELETE 0.0.0.0 MASK 0.0.0.0 10.185.35.254 -p"};
                            Process addProcess = runtime.exec(delPlus);
                            LogHelper.printToLog("删除路由错误："+LogHelper.streamToString(addProcess.getErrorStream()));
                            LogHelper.printToLog("删除路由返回："+LogHelper.streamToString(addProcess.getInputStream()));
                        }
                    } catch (Exception e) {

                    }
                }
            };
            timer.schedule(timerTask, 5000, period * 60 * 1000);
        } catch (Exception e) {

        }
    }

}
