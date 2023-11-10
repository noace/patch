package com.kyee.upgrade.utils;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
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
 * @description 远程调用Linux命令工具类
 * @date 2022/4/12 16:38
 */
public class RemoteCommandUtil {

    private static final Logger log = LoggerFactory.getLogger(RemoteCommandUtil.class);
    
    public static void main(String[] args) throws Exception {
        String userName = "root";// 用户名
        String password = "Danghang123@.";// 密码
        String host = "101.42.250.64";// 服务器地址
//        String cmd = "netstat -ntlp";
        String cmd = "ps -ef |grep java|grep tomcat|grep -v grep|wc -l";
        String cmd3 = "cd /opt/software/tomcat/bin && ./startup.sh";
        String cmd2 = "cd /opt/software/tomcat/bin && ./shutdown.sh";
        int port = 22;// 端口号
        String s = remoteCommandUtil(userName, password, host, port, cmd);
        System.out.println(s);
//        remoteCommandUtil(userName, password, host, port, cmd2);
    }

    public static String remoteCommandUtil(String userName, String password, String host, int port, String cmd) throws Exception {
        JSch jsch = new JSch(); // 创建JSch对象
        Session session = null;
        ChannelExec channelExec = null;
        BufferedReader reader = null;
        InputStream in = null;
        StringBuilder sb = new StringBuilder();
        try {
            session = jsch.getSession(userName, host, port); // 根据用户名，主机ip，端口获取一个Session对象
            session.setPassword(password); // 设置密码
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config); // 为Session对象设置properties
            int timeout = 60000000;
            session.setTimeout(timeout); // 设置timeout时间
            session.connect(); // 通过Session建立链接
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
//                LogUtil.recordUpgradeLog("命令执行日志：" + buf, patchId, serverId);
//                log.info("命令执行日志：" + sb.toString());
            }
        } catch (Exception e) {
            e.printStackTrace();
            log.info("远程命令执行失败");
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
}
