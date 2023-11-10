package com.kyee.upgrade.utils;

import com.jcraft.jsch.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
/**
 * @author danghanghang
 * @description 远程工具类
 * @date 2022/4/12 16:38
 */
public class RemoteUtil {

    private static final Logger log = LoggerFactory.getLogger(RemoteUtil.class);
    
    public static void main(String[] args) throws Exception {
        String userName = "root";// 用户名
        String password = "Danghang123@.";// 密码
        String host = "101.42.250.64";// 服务器地址
        String cmd = "ps -ef |grep java|grep tomcat|grep -v grep|wc -l";
        int port = 22;// 端口号
        String s = remoteCommandUtil(userName, password, host, port, cmd);
        System.out.println(s);
    }

    public static String remoteCommandUtil(String userName, String password, String host, int port,
                                           String cmd) throws Exception {
        JSch jsch = new JSch();
        Session session = null;
        ChannelExec channelExec = null;
        BufferedReader reader = null;
        InputStream in = null;
        StringBuilder sb = new StringBuilder();
        try {
            session = jsch.getSession(userName, host, port);
            session.setPassword(password);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            int timeout = 60000000;
            session.setTimeout(timeout);
            session.connect();
            channelExec = (ChannelExec) session.openChannel("exec");
            channelExec.setCommand(cmd);
            channelExec.setInputStream(null);
            channelExec.setErrStream(System.err);
            channelExec.connect();
            in = channelExec.getInputStream();
            reader = new BufferedReader(new InputStreamReader(in, Charset.forName("UTF-8")));
            String buf = null;
            while ((buf = reader.readLine()) != null) {
                sb.append(buf);
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("远程命令执行失败：{}", e.getMessage());
            throw new Exception("远程命令执行失败：" + e.getMessage());
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
                if (channelExec != null) {
                    channelExec.disconnect();
                }
                if (session != null) {
                    session.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    /**
     * @description：远程传输文件
     * @author：DangHangHang
     * @date：2023-08-16
     */
    public static void remoteTransferUtil(String userName, String password, String host, int port, String localPath,
                                          String remotePath) throws Exception {
        Session session = null;
        ChannelSftp channelSftp = null;
        try {
            JSch jsch = new JSch();
            session = jsch.getSession(userName, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect();
            channelSftp = (ChannelSftp) session.openChannel("sftp");
            channelSftp.connect();
            channelSftp.put(localPath, remotePath);
        } catch (JSchException | SftpException e) {
            e.printStackTrace();
            log.info("远程传输文件失败！" + e.getMessage());
            throw new Exception("远程传输文件失败！" + e.getMessage());
        } finally {
            if (channelSftp != null) {
                channelSftp.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }
}
