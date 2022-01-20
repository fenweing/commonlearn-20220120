package com.tuanbaol;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

public class LogHelper {
    private static String logFile = "D:\\log\\bat.log";

    public static void printToLog(String resLog) {
        File file = new File(logFile);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {

            }
        }
        try (FileOutputStream fos = new FileOutputStream(file, true)) {
            fos.write((new Date().toString() + " :").getBytes("gbk"));
            fos.write(resLog.getBytes("gbk"));
            fos.write("\r\n".getBytes());
        } catch (Exception e) {

        }
    }

    public static void printToLog(InputStream inputStream) {
        String resLog = streamToString(inputStream);
        printToLog(resLog);
    }

    public static void printConsole(InputStream inputStream) {
        String resLog = streamToString(inputStream);
        System.out.println(resLog);
    }

    public static String streamToString(InputStream inputStream) {
        try {
            byte[] bytes = new byte[1024];
            int r = 0;
            StringBuilder res = new StringBuilder();
            while ((r = inputStream.read(bytes)) != -1) {
                res.append(new String(bytes, 0, r, "gbk"));
            }
            return res.toString();
        } catch (Exception e) {
            return "";
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {

            }
        }
    }
}
