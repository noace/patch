package com.kyee.upgrade.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class GitFilePath {

    public GitFilePath() {
    }

    public String execCurl(String[] cmds) {
        ProcessBuilder process = new ProcessBuilder(cmds);
        Process p = null;
        BufferedReader reader = null;

        try {
            p = process.start();
            reader = new BufferedReader(new InputStreamReader(p.getInputStream(), "UTF-8"));
            StringBuilder builder = new StringBuilder();
            String line = null;

            while((line = reader.readLine()) != null) {
                builder.append(line);
                builder.append(System.getProperty("line.separator"));
            }

            reader.close();
            p.waitFor();
            p.destroy();
            return builder.toString();
        } catch (Throwable var8) {
            System.out.print("error");
            var8.printStackTrace();
            if (reader != null) {
                try {
                    reader.close();
                } catch (Exception var7) {
                    var7.printStackTrace();
                }
            }

            if (p != null) {
                p.destroy();
            }

            return null;
        }
    }
}
