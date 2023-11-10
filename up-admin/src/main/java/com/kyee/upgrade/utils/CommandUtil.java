package com.kyee.upgrade.utils;

import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

public class CommandUtil {
    public static String run(String command) throws IOException {
        Scanner input = null;
        String result = "";
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
            try {
                //等待命令执行完成
                process.waitFor(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            InputStream is = process.getInputStream();
            input = new Scanner(is);
            while (input.hasNextLine()) {
                result += input.nextLine() + "\n";
            }
//            result = command + "\n" + result; //加上命令本身，打印出来
        } finally {
            if (input != null) {
                input.close();
            }
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }

    public static String run(String[] command) throws IOException {
        Scanner input = null;
        String result = "";
        Process process = null;
        try {
            process = Runtime.getRuntime().exec(command);
            try {
                //等待命令执行完成
                process.waitFor(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            InputStream is = process.getInputStream();
            input = new Scanner(is);
            while (input.hasNextLine()) {
                result += input.nextLine() + "\n";
            }
//            result = command + "\n" + result; //加上命令本身，打印出来
        } finally {
            if (input != null) {
                input.close();
            }
            if (process != null) {
                process.destroy();
            }
        }
        return result;
    }

    public static void main(String[] args) throws IOException {
        /*String command = "cmd /c g: & mvn clean install";
        String run = CommandUtil.run(command);
        System.out.println(run);*/

        /*String[] commands = {"cmd", "/c", "e: & cd E:\\apache-tomcat-8.0.53\\bin & dir"};
        String runs = CommandUtil.run(commands);
        String[] comm = {"cmd", "/c", "dir"};
//        String run = CommandUtil.run(comm);
        System.out.println(runs);
//        System.out.println(run);
        System.out.println("end...");*/

        String path = ResourceUtils.getFile("classpath:shell/decodeDetect.py").getPath();
        String s = path + " G:\\workspace3\\his_v3\\db\\sql\\1DATA\\20210311182635_INSERT_BAS_TASK.SQL";
        String[] command = {"cmd", "/c", s};
        String list = CommandUtil.run(command);
        System.out.println(list);
    }
}
