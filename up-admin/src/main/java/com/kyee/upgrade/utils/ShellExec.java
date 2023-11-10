package com.kyee.upgrade.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class ShellExec {

    public static void main(String[] args) throws IOException {

        String commandType = "cmd";
        String param1 = "/c";
        String param2 = "C:\\Users\\Queue\\Desktop\\SQL\\shelll.sh";

        String[] commands = {"cmd", "/c", "e: & cd E:\\apache-tomcat-8.0.53\\bin & dir"};

        String cmd = "sqlplus gts1031/gts1031@ORACL @init-6.0.01.sql >D:/gts1031.log";

        String param3 = "C:\\Users\\Queue\\Desktop\\HISTEST.SQL";
        String param4 = "E:\\KYEE\\patch_upgrade_system\\up-admin\\src\\main\\java\\com\\kyee\\upgrade\\utils\\decodeDetect2.py";

        List<String> list = execAndReturn(commandType, param1, param4);
        System.out.println(list);
    }

    public static List<String> execAndReturn(String commandType, String param1, String param2) throws IOException {
        String[] commands = {commandType, param1, param2};
        Runtime rt = Runtime.getRuntime();
        Process proc = rt.exec(commands);
        List<String> result = new ArrayList<>();

        BufferedReader print = new BufferedReader(new
                InputStreamReader(proc.getInputStream()));

        BufferedReader stdError = new BufferedReader(new
                InputStreamReader(proc.getErrorStream()));

        // Read the output from the command
        String s = null;
        while ((s = print.readLine()) != null) {
            result.add(s);
        }

        // Read any errors from the attempted command
        while ((s = stdError.readLine()) != null) {
            throw new RuntimeException("shell comand exec failed: " + s);
        }
        return result;
    }
}
