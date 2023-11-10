package com.kyee.upgrade.utils;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.SCPClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.Objects;

/**
 * @author Roller
 * @date 2022/4/14 16:58
 */
public class SCPFile {

    private static final Logger logger = LogManager.getLogger(SCPFile.class);
//    private static String IP = "101.42.250.64";
//    private static int PORT = 22;
//    private static String USER = "root";//登录用户名
//    private static String PASSWORD = "Danghang123@.";//生成私钥的密码和登录密码，这两个共用这个密码
//    private static Connection connection = new Connection(IP, PORT);
//    private static String PRIVATEKEY = "*******";// 本机的私钥文件地址
//    private static boolean usePassword = true;// 使用用户名和密码来进行登录验证。如果为true则通过用户名和密码登录，false则使用rsa免密码登录
    private static final String DEFAULTCHART = "UTF-8";
    
    public static boolean isAuth(Connection connection, String user, String password, String PRIVATEKEY, boolean usePassword) {
        try {
            if (usePassword) {
                return connection.authenticateWithPassword(user, password);
            } else {
                return connection.authenticateWithPublicKey(user, new File(PRIVATEKEY), password);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public static void putFile(String localFile, String remoteTargetDirectory, String ip, int port, String user, String password, String PRIVATEKEY, boolean usePassword) {
        try {
            SCPClient scp = createSCPClient(ip, port, user, password, PRIVATEKEY, usePassword);
            if(!Objects.isNull(scp)) {
                scp.put(localFile, remoteTargetDirectory);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    // 创建SCP客户端
    public static SCPClient createSCPClient(String ip, int port, String user, String password, String PRIVATEKEY, boolean usePassword){
        Connection connection = new Connection(ip, port);
        try {
            connection.connect();
            boolean isAuthed = isAuth(connection, user, password, PRIVATEKEY, usePassword);
            if (isAuthed) {
                return connection.createSCPClient();
            } else {
                logger.info("认证失败!");
            }
        } catch (IOException e) {
            logger.info("创建SCPClient连接失败");
        }
        return null;
    }

    public static void main(String[] args) {
        String IP = "101.42.250.64";
        int PORT = 22;
        String USER = "root";//登录用户名
        String PASSWORD = "Danghang123@.";//生成私钥的密码和登录密码，这两个共用这个密码
//        Connection connection = new Connection(IP, PORT);
        String PRIVATEKEY = "*******";// 本机的私钥文件地址
        boolean usePassword = true;// 使用用户名和密码来进行登录验证。如果为true则通过用户名和密码登录，false则使用rsa免密码登录
//        Connection connection = new Connection(IP, PORT);
        String file = "G:\\百度\\UpPackage.log";
        try {
            putFile(file, "/opt/test/his", IP, PORT, USER, PASSWORD, PRIVATEKEY, usePassword);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
