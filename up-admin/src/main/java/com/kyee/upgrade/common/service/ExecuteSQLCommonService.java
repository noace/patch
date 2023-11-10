package com.kyee.upgrade.common.service;

import com.kyee.upgrade.utils.CommandUtil;
import com.kyee.upgrade.utils.ConstantUtil;
import com.kyee.upgrade.utils.LogUtil;
import com.kyee.upgrade.utils.OSValidator;
import com.ruoyi.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.Arrays;
import java.util.List;

@Component
public class ExecuteSQLCommonService {

    private static final Logger log = LoggerFactory.getLogger(ExecuteSQLCommonService.class);

    @Value(value = "${ruoyi.detectPath}")
    private String detectPath;

    @Value(value = "${ruoyi.convertSql}")
    private String convertSql;

    /**
     * 1.脚本文件名全部大写，后缀全部为'.SQL'
     * 2.文件名前缀精确到分的时间，yyyyMMddHHmm
     * 3.SQL文件必须以exit;结尾
     * 4.SQL文件存放目录是否正确
     *   0DDL
     *      0TABLE 存放DDL脚本;  create, drop, alter, truncate
     *      2VIEW_FUNC 存放视图和函数;
     *      3PROCEDURE 存放存储过程;
     *   1DATA
     *      放置基础数据的脚本，添加数据、修改数据等
     *
     *
     * 检查SQL，执行SQL
     * @param fileName SQL文件名
     * @param sourceFilePath SQL文件路径
     * @param patchId
     * @param serverId
     * @throws Exception
     */
    public String detectSql(String fileName, String sourceFilePath, Long patchId, Integer serverId) throws Exception {

        LogUtil.recordUpgradeLog("--------------检测SQL开始--------------", patchId, serverId);
        log.info("--------------检测SQL开始--------------");
        if (fileName.contains(" ")) {
            log.info("--------------SQL脚本文件名包含空格，请修改后重试: " + fileName +"--------------");
            throw new Exception("SQL脚本文件名包含空格，请修改后重试: " + fileName);
        }

        // 校验SQL文件名称是否正确
        boolean upperCase = isUpperCase(fileName);
        if (!upperCase) {
            log.info("--------------SQL脚本文件名包含小写，请修改后重试: " + fileName +"--------------");
            throw new Exception("SQL脚本文件名包含小写，请修改后重试: " + fileName);
        }

        // 校验文件名时间格式
        if(!(fileName.split("_")[0].length() == 12)) {
            log.info("--------------SQL脚本文件名不符合命名规范【yyyyMMddHHmm_XXX.SQL】，请参考脚本规范后重试：" + fileName +"--------------");
            throw new Exception("SQL脚本文件名不符合命名规范【yyyyMMddHHmm_XXX.SQL】，请参考脚本规范后重试: " + fileName);
        }

        // 校验SQL文件是否存放对应目录
        String[] arr = sourceFilePath.split("/");
        String sqlDir = arr[arr.length-2];
        if (StringUtils.isNotEmpty(sqlDir)) {
            String[] nameArr = fileName.split("_");
            // 存放DDL的目录
            List<String> ddlName = Arrays.asList("CREATE", "DROP", "ALTER", "TRUNCATE");
            if (sqlDir.equals(ConstantUtil.SQLDIR.TABLE.getValue())) {
                if(!ddlName.contains(nameArr[1])) {
                    throw new Exception("SQL脚本文件存放目录不正确【0TABLE下只能存放DDL语句】或SQL文件名不规范，请参考脚本规范后重试: " + fileName);
                }
            }

            // 存放视图和函数
            List<String> viewAndFuncName = Arrays.asList("VIEW", "FUNC");
            if (sqlDir.equals(ConstantUtil.SQLDIR.VIEW_FUNC.getValue())) {
                if(!viewAndFuncName.contains(nameArr[1])) {
                    throw new Exception("SQL脚本文件存放目录不正确【2VIEW_FUNC下只能存放视图和函数语句】或SQL文件名不规范，请参考脚本规范后重试: " + fileName);
                }
            }

            // 存放存储过程
            List<String> names = Arrays.asList("PROC", "PKG", "SEQ", "TRI");
            if (sqlDir.equals(ConstantUtil.SQLDIR.PROCEDURE.getValue())) {
                if (!names.contains(nameArr[1])) {
                    throw new Exception("SQL脚本文件存放目录不正确【3PROCEDURE下只能存放存储过程语句,序列,包,触发器】或SQL文件名不规范，请参考脚本规范后重试: " + fileName);
                }
            }

            // 存放基础数据
            List<String> dataName = Arrays.asList("INSERT", "UPDATE", "DELETE");
            if (sqlDir.equals(ConstantUtil.SQLDIR.DATA.getValue())) {
                if(!dataName.contains(nameArr[1])) {
                    throw new Exception("SQL脚本文件存放目录不正确【1DATA下只能存放基础数据相关语句】或SQL文件名不规范，请参考脚本规范后重试: " + fileName);
                }
            }
        }
        String convertSqlPath = "";
        if (OSValidator.isUnix()) {
            // 校验SQL文件尾规范 必须以exit;结尾，否则sqlplus执行sql脚本之后，程序无法退出。
            String tailContent = "";
            int i = 1;
            while (StringUtils.isEmpty(tailContent)){
                String content = "tail -n " + i + " "  + sourceFilePath;
                String[] tailContentScript = {"/bin/sh", "-c", content};
                tailContent = CommandUtil.run(tailContentScript);
                i++;
            }
            log.info("--------------SQL脚本文件尾内容：" + tailContent + "--------------", patchId, serverId);
            LogUtil.recordUpgradeLog("--------------SQL脚本文件尾内容: " + tailContent +"--------------", patchId, serverId);

            if (!(StringUtils.isNotEmpty(tailContent) && tailContent.toLowerCase().contains("exit;"))) {
                throw new Exception("SQL脚本不符合规范【文件末尾最后一行必须以exit;结尾】，请参考脚本规范后重试。当前文件尾行: 【" + tailContent+"】");
            }

            // 新方案，强制退出  echo exit | sqlplus HIS_DEV/HIS_DEV@121.4.131.241:52773/orcl @END.SQL

            // SQL文件编码检测
            LogUtil.recordUpgradeLog("--------------SQL路径: " + sourceFilePath +"--------------", patchId, serverId);
            //            String path = ResourceUtils.getFile("classpath:shell/decodeDetect.py").getPath();
            log.info("获取检测脚本路径：" + detectPath);
            String[] detectSql = new String[]{};
            if (OSValidator.isWindows()) {
                detectSql = new String[]{"cmd", "/c", "python " + detectPath + " " + sourceFilePath};
            }
            if (OSValidator.isUnix()) {
                detectSql = new String[]{"/bin/sh", "-c", "python " + detectPath + " " + sourceFilePath};
            }
            String message = CommandUtil.run(detectSql);
            log.info("-----------------------------------------SQL脚本: " + "python" + detectPath + " " + sourceFilePath + "-----------------------------------------");
            log.info("-----------------------------------------SQL编码: " + message + "-----------------------------------------");
            if (StringUtils.isNotEmpty(message) && (message.contains(ConstantUtil.EncodeType.GB2312.getValue()) || message.contains(ConstantUtil.EncodeType.ISO_8859_1.getValue()))) {
                LogUtil.recordUpgradeLog("--------------SQL编码正确：" + message + "--------------", patchId, serverId);
                log.info("------------------------------------SQL编码正确-----------------------------------------");
            } else {
                LogUtil.recordUpgradeLog("--------------SQL脚本【"+ fileName + "】【" + message + "】编码错误，请修改后重试--------------", patchId, serverId);
                log.info("------------------------------------SQL编码错误-----------------------------------------" + message);
                throw new Exception("SQL脚本【"+ fileName + "】【" + message + "】编码错误，请修改后重试");
            }

            LogUtil.recordUpgradeLog("--------------检测SQL结束--------------", patchId, serverId);

            // SQL转码
            log.info("------------------------------------转换编码开始-----------------------------------------");
            LogUtil.recordUpgradeLog("--------------转换SQL编码开始--------------", patchId, serverId);
            // iconv -f encoding -t encoding inputfile -o outputfile  转换文件编码
            String name = sourceFilePath.substring(sourceFilePath.lastIndexOf("/") + 1);
            String convertName = "CONVERT_" + name;
            LogUtil.recordUpgradeLog("--------------转换编码后的SQL名称: " + convertName + "--------------", patchId, serverId);
            convertSqlPath = convertSql + File.separator + convertName;
            String convertSql = "iconv -f GB2312 -t UTF-8 " + sourceFilePath + " -o " + convertSqlPath;
            log.info("--------------转换SQL脚本：" + convertSql + "--------------", patchId, serverId);

            String[] convertScript = {"/bin/sh", "-c", convertSql};
            String convertSqlMsg = "";
            try {
                convertSqlMsg = CommandUtil.run(convertScript);
                log.info("------------------------------------转换编码成功-----------------------------------------");
                LogUtil.recordUpgradeLog("--------------转换SQL编码成功--------------", patchId, serverId);
            } catch (Exception e) {
                log.info("------------------------------------转换编码失败-----------------------------------------" + e.getMessage());
                throw new Exception("转换SQL编码失败." + convertSqlMsg);
            }
            log.info("------------------------------------转换编码结束-----------------------------------------");
            LogUtil.recordUpgradeLog("--------------转换SQL编码结束 --------------", patchId, serverId);

            return convertSqlPath;
        } else {
            LogUtil.recordUpgradeLog("--------------检测SQL结束--------------", patchId, serverId);
        }

        return convertSqlPath;
    }

    /**
     * 执行开发库SQL
     * @param convertSqlPath 转换编码后的SQL文件名
     * @param serverName
     * @param patchId
     * @param serverId
     * @throws Exception
     */
    public void executeSQL(String convertSqlPath, Long patchId, String serverName, Integer serverId, String username, String password, String hosname, Integer port, String dbname) throws Exception {

        LogUtil.recordUpgradeLog("--------------执行数据库【" + serverName +"】SQL开始 --------------", patchId, serverId);
        // 执行SQL
        String excuteScript = "echo exit | sqlplus " + username + "/" + password + "@" + hosname + ":" + port + "/" + dbname + " @" + convertSqlPath;
        String[] excuteSql = {"/bin/sh", "-c", excuteScript};
        String excuteSqlMsg = CommandUtil.run(excuteSql);
        if (StringUtils.isNotEmpty(excuteSqlMsg) && (excuteSqlMsg.contains("ORA-") || excuteSqlMsg.contains("SP2-"))) {
            // SQL执行失败
            // TODO sqlplus执行结果格式化输出到页面
            throw new Exception("执行开发库SQL失败,请查看错误详情：" + excuteSqlMsg);
        } else if(StringUtils.isNotEmpty(excuteSqlMsg) && (!excuteSqlMsg.toUpperCase().contains("END!") && !excuteSqlMsg.toUpperCase().contains("END !"))) {
            throw new Exception("执行标准库11gSQL异常，请检查后重试，可能原因：SQL没有【/】,SQL执行未结束,请查看错误详情：" + excuteSqlMsg);
        }
        LogUtil.recordUpgradeLog("执行开发库SQL成功：" + excuteSqlMsg, patchId, serverId);
        LogUtil.recordUpgradeLog("--------------执行数据库【" + serverName +"】SQL结束 --------------", patchId, serverId);
    }

    /**
     * 名称是否为大写
     * @param name
     * @return
     */
    private static boolean isUpperCase(String name) throws Exception{
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

}
