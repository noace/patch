package com.kyee.upgrade.common.patch;

import com.kyee.upgrade.utils.ConstantUtil;
import com.kyee.upgrade.utils.DotNetToJavaStringHelper;
import com.kyee.upgrade.utils.FileUtil;
import com.kyee.upgrade.utils.LogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;

/**
 * 描述：通用打包类
 * 作者：DangHangHang
 * 时间：2023年06月13日 10:39
 */
public class CommonPatchMethod {
    private static final Logger log = LoggerFactory.getLogger(CommonPatchMethod.class);

    /**
     * 描述：校验脚本名称是否为大写
     * 作者：DangHangHang
     * 时间：2023年06月13日 10:43
     */
    public static boolean isUpperCase(String name) throws Exception{
        try {
            for(int i = 0; i < name.length(); i++) {
                char c = name.charAt(i);
                if (Character.isLowerCase(c)) {
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            log.info(e.getMessage());
            throw new Exception("SQL脚本文件名异常,请检查脚本名称：" + name);
        }
    }

    /**
     * 描述：创建临时目录
     * 作者：DangHangHang
     * 时间：2023年06月13日 10:43
     */
    public static boolean clearAndCreateTempDir(String strTempPath){
        boolean result = false;
        File tempPathFile = new File(strTempPath);
        if(tempPathFile.exists()){
            FileUtil.delFile(tempPathFile);
            result = true;
        }else{
            tempPathFile.mkdirs();
            result = true;
        }
        return result;
    }

    /**
     * 描述：子目录
     * 作者：DangHangHang
     * 时间：2023年06月13日 10:42
     */
    public static String subPath(ConstantUtil.CodeType codeType) throws Exception{
        String result = "";
        if(codeType.isCode()){
            result = "code" + File.separator;
        }else if(codeType.isSql()){
            result = "sql" + File.separator;
        }else{
            throw new Exception("文件类型未知");
        }
        return result;
    }

    /**
     * 描述：根据代码列表获取项目名称
     * 作者：DangHangHang
     * 时间：2023年06月13日 10:41
     */
    public static String getProjectNameByCodeList(String codePath, Long patchId, String processId) throws Exception{
        String reuslt = "";
        if(!DotNetToJavaStringHelper.isNullOrEmpty(codePath)){
            codePath = codePath.replaceAll("\\\\", "/");
            if(codePath.indexOf("/") > 0){
                reuslt = codePath.substring(0,codePath.indexOf("/"));
            }else{
                LogUtil.recordBuildLog("代码路径分隔符错误！【"+codePath+"】", patchId, processId);
                throw new Exception("代码路径分隔符错误！"+codePath);
            }
        }else{
            LogUtil.recordBuildLog("代码路径错误！【"+codePath+"】", patchId, processId);
            throw new Exception("代码路径错误！");
        }
        return reuslt.trim();
    }

    /**
     * 描述：生成readme文件
     * 作者：DangHangHang
     * 时间：2023年06月13日 10:46
     */
    public static void afterCopySource(String zipFileName, String target, String projectComment,String topic, List<String> codeArray,
                                 List<String> jiraList, String configContent){
        boolean isSuccssCreateReadMe;
        boolean isSuccessWriteReadMe;
        String readMePath;
        readMePath = target + "readMe.txt";
        isSuccssCreateReadMe = createReadMe(readMePath);
        if(!isSuccssCreateReadMe){
            log.error("创建readMe.txt文件失败！");
        }
        isSuccessWriteReadMe = writeTxtFile(zipFileName,projectComment,topic,readMePath,codeArray, jiraList, configContent);
        if(!isSuccessWriteReadMe){
            log.error("写入readMe.txt文件失败！");
            log.error("zipFileName："+zipFileName);
            log.error("projectComment："+projectComment);
        }
    }

    /**
     * 描述：创建Readme文件
     * 作者：DangHangHang
     * 时间：2023年06月13日 10:48
     */
    public static boolean createReadMe(String readMePath) {
        boolean flag = false;
        File filename = new File(readMePath);
        if (!filename.exists()) {
            try {
                filename.createNewFile();
                flag = true;
            } catch (IOException e) {
                flag = false;
                // TODO Auto-generated catch block
                e.printStackTrace();
                log.error("创建readMe文件失败",e);
            }
        }
        return flag;
    }

    /**
     * 描述：写入TXT文件内容
     * 作者：DangHangHang
     * 时间：2023年06月13日 10:48
     */
    public static boolean writeTxtFile(String packageName, String packageComment,String topic, String readMePath,
                                       List<String> codeArray, List<String> jiraList, String configContent) {
        // 先读取原有文件内容，然后进行写入操作
        boolean flag = false;
        StringBuilder sb = new StringBuilder();
        for (String jira : jiraList) {
            sb.append(jira).append("\r\n");
        }
        packageComment = packageComment.replace(",\r\n", "\r\n");
        topic = topic.replace(",\r\n", "\r\n");

        String filein = "单任务包：" + packageName + "\r\n"
                + "--------------------------------" + "\r\n"
                + "该单任务包版本信息："+ "\r\n"
                + topic + "\r\n"
                + "--------------------------------" + "\r\n"
                + "备注："+ "\r\n"
                + packageComment + "\r\n"
                + "--------------------------------" + "\r\n"
                + "任务列表："+ "\r\n"
                + sb.toString() + "\r\n"
                + "--------------------------------" + "\r\n"
                + "修改配置文件内容："+ "\r\n"
                + configContent + "\r\n"
                + "--------------------------------" + "\r\n"
                + "代码列表："+ "\r\n";

        for (String code : codeArray) {
            filein = filein + code + "\r\n";
        }

        String temp = "";

        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;

        FileOutputStream fos = null;
        PrintWriter pw = null;
        try {
            // 文件路径
            File file = new File(readMePath);
            // 将文件读入输入流
            fis = new FileInputStream(file);
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            StringBuffer buf = new StringBuffer();

            // 保存该文件原有的内容
            for (int j = 1; (temp = br.readLine()) != null; j++) {
                buf = buf.append(temp);
                // System.getProperty("line.separator")
                // 行与行之间的分隔符 相当于“\n”
                buf = buf.append(System.getProperty("line.separator"));
            }
            buf.append(filein);

            fos = new FileOutputStream(file);
            pw = new PrintWriter(fos);
            pw.write(buf.toString().toCharArray());
            pw.flush();
            flag = true;
        } catch (IOException e1) {
            log.error("写入readme文件失败",e1);
        } finally {
            if (pw != null) {
                pw.close();
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("写入readme文件失败",e);
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("写入readme文件失败",e);
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("写入readme文件失败",e);
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                    log.error("写入readme文件失败",e);
                }
            }
        }
        return flag;
    }
}
