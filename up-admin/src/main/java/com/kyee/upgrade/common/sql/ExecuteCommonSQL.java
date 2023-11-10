package com.kyee.upgrade.common.sql;

import com.kyee.upgrade.domain.UpPatch;
import com.kyee.upgrade.domain.UpProjectProduct;
import com.kyee.upgrade.mapper.UpPatchMapper;
import com.kyee.upgrade.mapper.UpProjectProductMapper;
import com.kyee.upgrade.utils.*;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.system.mapper.SysUserMapper;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ExecuteCommonSQL {
    private static final Logger log = LoggerFactory.getLogger(ExecuteCommonSQL.class);

    @Value(value = "${ruoyi.convertSql}")
    private String convertSql;

    @Value(value = "${ruoyi.detectPath}")
    private String detectPath;

    // TODO表检测
    @Value(value = "${ruoyi.detectTodo}")
    private String detectTodo;

    // 开发库配置
    @Value(value = "${sqlplus.config.hosname}")
    private String hosname;

    @Value(value = "${sqlplus.config.port}")
    private String port;

    @Value(value = "${sqlplus.config.username}")
    private String username;

    @Value(value = "${sqlplus.config.password}")
    private String password;

    @Value(value = "${sqlplus.config.dbname}")
    private String dbname;

    // 标准库11g配置
    @Value(value = "${sqlplus.standardConfig.standardHosname}")
    private String standardHosname;

    @Value(value = "${sqlplus.standardConfig.standardPort}")
    private String standardPort;

    @Value(value = "${sqlplus.standardConfig.standardUsername}")
    private String standardUsername;

    @Value(value = "${sqlplus.standardConfig.standardPassword}")
    private String standardPassword;

    @Value(value = "${sqlplus.standardConfig.standardDbname}")
    private String standardDbname;

    // 标准库12c配置
    @Value(value = "${sqlplus.standard12cConfig.standard12cHosname}")
    private String standard12cHosname;

    @Value(value = "${sqlplus.standard12cConfig.standard12cPort}")
    private String standard12cPort;

    @Value(value = "${sqlplus.standard12cConfig.standard12cUsername}")
    private String standard12cUsername;

    @Value(value = "${sqlplus.standard12cConfig.standard12cPassword}")
    private String standard12cPassword;

    @Value(value = "${sqlplus.standard12cConfig.standard12cDbname}")
    private String standard12cDbname;

    @Autowired
    private UpPatchMapper patchMapper;

    @Autowired
    private SysUserMapper sysUserMapper;

    @Autowired
    private UpProjectProductMapper projectProductMapper;

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
     * @param processId
     * @throws Exception
     */
    public void detectSql(String fileName, String sourceFilePath, Long patchId, String processId) throws Exception {

        LogUtil.recordBuildLog("--------------检测SQL开始--------------", patchId, processId);
        log.info("--------------检测SQL开始--------------");
        UpPatch upPatch = patchMapper.selectUpPatchById(patchId);
        if (!"Y".equals(upPatch.getThirdPartView())) {
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
//            String procedureName = "PROC";
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

            // 校验创建人是否是打包人员 Start
            if (OSValidator.isUnix()) {
                // 校验创建人是否是打包人员
                // awk '$0~/CREATED/{print $0}' 202304241751_ALTER_PHYSIOTHERAPY_RECORD.SQL

                // 修改人校验
                String UPDATED =  validateSqlPerson("UPDATED", sourceFilePath);
                String Updated =  validateSqlPerson("Updated", sourceFilePath);
                // 创建人校验
                String personLine;
                if (StringUtils.isEmpty(UPDATED) && StringUtils.isEmpty(Updated)) {
                    String CREATE =  validateSqlPerson("CREATED", sourceFilePath);
                    String Created =  validateSqlPerson("Created", sourceFilePath);

                    if (StringUtils.isEmpty(CREATE) && StringUtils.isEmpty(Created)) {
                        throw new Exception("脚本文件中没有查询到创建人和修改人的信息，请检查！");
                    }
                    personLine = StringUtils.isEmpty(Created) ? CREATE : Created;
                } else {
                    personLine = StringUtils.isEmpty(Updated) ? UPDATED : Updated;
                }

                LogUtil.recordBuildLog("--------------脚本创建人内容: " + personLine +"--------------", patchId, processId);

                if (StringUtils.isNotEmpty(personLine)) {
                    String person = "";
                    if (personLine.contains("BY")) {
                        person = personLine.substring(personLine.indexOf("BY") + 2).trim();
                    } else if (personLine.contains("By")) {
                        person = personLine.substring(personLine.indexOf("By") + 2).trim();
                    } else {
                        person = personLine.substring(personLine.indexOf("by") + 2).trim();
                    }

                    if (StringUtils.isEmpty(person)) {
                        throw new Exception("脚本文件中没有查询到创建人和修改人的信息，请检查！");
                    }

                    LogUtil.recordBuildLog("--------------脚本文件中的创建人与打包人员: " + person +"--------------", patchId, processId);

                    // 获取用户和拼音
                    String userName = upPatch.getUpdateBy();
                    log.info("打包人员：" + userName);

                    List<SysUser> sysUsers = sysUserMapper.getUsernameByPinYin(userName);
                    List<String> pinyinList = new ArrayList<>();
                    if (!CollectionUtils.isEmpty(sysUsers)) {
                        pinyinList = sysUsers.stream().map(SysUser::getUsernamePinyin).collect(Collectors.toList());
                        pinyinList.removeAll(Collections.singleton(null));
                    }

                    log.info("集合数量：" + pinyinList.size());

                    if (CollectionUtils.isEmpty(pinyinList)) {
                        String usernamePinyin = PinyinUtils.convertPinyin(userName);
                        if (!person.equals(usernamePinyin)) {
                            throw new Exception("脚本文件中的创建人或修改人与打包人员姓名不一致，请检查！脚本：【" + person +"】,打包：【" + usernamePinyin + "】");
                        }
                    } else {
                        String usernamePinyin = pinyinList.get(0);
                        log.info("打包人员拼音：" + usernamePinyin);
                        // 用户表中的昵称拼音不为空，则姓名为多音字
                        if (!person.equals(usernamePinyin)) {
                            throw new Exception("脚本文件中的创建人或修改人与打包人员姓名不一致，请检查！脚本：【" + person +"】,打包：【" + usernamePinyin + "】");
                        }
                    }
                } else {
                    throw new Exception("脚本文件中没有查询到创建人和修改人的信息，请检查！");
                }
                // 校验创建人是否是打包人员 End

                // 校验SQL文件尾规范 必须以exit;结尾，否则sqlplus执行sql脚本之后，程序无法退出。
                String tailContent = "";
                int i = 1;
                while (StringUtils.isEmpty(tailContent)){
                    String content = "tail -n " + i + " "  + sourceFilePath;
                    String[] tailContentScript = {"/bin/sh", "-c", content};
                    tailContent = CommandUtil.run(tailContentScript);
                    i++;
                }
                log.info("--------------SQL脚本文件尾内容：" + tailContent + "--------------", patchId, processId);
                LogUtil.recordBuildLog("--------------SQL脚本文件尾内容: " + tailContent +"--------------", patchId, processId);

                if (!(StringUtils.isNotEmpty(tailContent) && tailContent.toLowerCase().contains("exit;"))) {
                    throw new Exception("SQL脚本不符合规范【文件末尾最后一行必须以exit;结尾】，请参考脚本规范后重试。当前文件尾行: 【" + tailContent+"】");
                }

                // 校验存过END;后是否有/,是否结束(弃用，不太完美)
//                String endPoint = "grep -in 'end;' "  + sourceFilePath;
//                String[] tailContentScript = {"/bin/sh", "-c", endPoint};
//                String result = CommandUtil.run(tailContentScript);
//                if (StringUtils.isNotEmpty(result)) {
//                    endSign(result, sourceFilePath, patchId, processId);
//                }

                // 新方案，强制退出  echo exit | sqlplus HIS_DEV/HIS_DEV@121.4.131.241:52773/orcl @END.SQL

                // SQL文件编码检测
                LogUtil.recordBuildLog("--------------SQL路径: " + sourceFilePath +"--------------", patchId, processId);
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
                if (StringUtils.isNotEmpty(message) && (message.contains(ConstantUtil.EncodeType.GB2312.getValue()))) {
                    LogUtil.recordBuildLog("--------------SQL编码正确：" + message + "--------------", patchId, processId);
                    log.info("------------------------------------SQL编码正确-----------------------------------------");
                } else {
                    log.info("------------------------------------SQL编码错误-----------------------------------------" + message);
                    throw new Exception("SQL脚本【"+ fileName + "】【" + message + "】编码错误，请修改后重试");
                }

                LogUtil.recordBuildLog("--------------检测SQL结束--------------", patchId, processId);

                // SQL转码
                log.info("------------------------------------转换编码开始-----------------------------------------");
                LogUtil.recordBuildLog("--------------转换SQL编码开始--------------", patchId, processId);
                // iconv -f encoding -t encoding inputfile -o outputfile  转换文件编码
                String name = sourceFilePath.substring(sourceFilePath.lastIndexOf("/") + 1);
                String convertName = "CONVERT_" + name;
                LogUtil.recordBuildLog("--------------转换编码后的SQL名称: " + convertName + "--------------", patchId, processId);
                String convertSqlPath = convertSql + File.separator + convertName;
                String convertSql = "iconv -f GB2312 -t UTF-8 " + sourceFilePath + " -o " + convertSqlPath;
                log.info("--------------转换SQL脚本：" + convertSql + "--------------", patchId, processId);
                String[] convertScript = {"/bin/sh", "-c", convertSql};
                String convertSqlMsg = "";
                try {
                    convertSqlMsg = CommandUtil.run(convertScript);
                    log.info("------------------------------------转换编码成功-----------------------------------------");
                    LogUtil.recordBuildLog("--------------转换SQL编码成功--------------", patchId, processId);
                } catch (Exception e) {
                    log.info("------------------------------------转换编码失败-----------------------------------------" + e.getMessage());
                    throw new Exception("转换SQL编码失败." + convertSqlMsg);
                }
                log.info("------------------------------------转换编码结束-----------------------------------------");
                LogUtil.recordBuildLog("--------------转换SQL编码结束 --------------", patchId, processId);


                // 火神版现场SQL往DEV执行
                // 查询火神版现场
                List<UpProjectProduct> upProjectProducts = projectProductMapper.selectUpProjectProductList(new UpProjectProduct());

                // TODO 多线程优化
                if (!CollectionUtils.isEmpty(upProjectProducts)) {
                    for (UpProjectProduct pp : upProjectProducts) {
                        if(pp.getProjectId().equals(upPatch.getProjectId()) && pp.getProductId().equals(upPatch.getProductId())) {

                            if ("1".equals(pp.getExcuteDevFlag())) {
                                // 向开发库执行SQL脚本
                                executeSQL(convertSqlPath, patchId, processId);
                            }

                            if ("1".equals(pp.getExcuteFlag())) {
                                // 向标准库11g执行SQL脚本
                                executeStandardSQL(convertSqlPath, patchId, processId);

                                // 向标准库12c执行SQL脚本
                                executeStandard12cSQL(convertSqlPath, patchId, processId);
                            } else if("2".equals(pp.getExcuteFlag())){
                                if (sourceFilePath.contains(ConstantUtil.SQLDIR.TABLE.getValue())) {
                                    // 向标准库11g部分执行SQL脚本
                                    executeStandardSQL(convertSqlPath, patchId, processId);

                                    // 向标准库12c部分执行SQL脚本
                                    executeStandard12cSQL(convertSqlPath, patchId, processId);
                                }
                            }
                        }
                    }
                }
            } else {
                LogUtil.recordBuildLog("--------------检测SQL结束--------------", patchId, processId);
            }
        }
    }

    /**
     * 校验脚本创建人，修改人
     * @param name
     * @param sourceFilePath
     * @return
     * @throws Exception
     */

    private String validateSqlPerson(String name, String sourceFilePath) throws Exception {

//        String createdCommand = "awk '$0~/" + name + "/{print $0}' " + sourceFilePath;
        String createdCommand = "cat " + sourceFilePath + " | head -5 | awk '/" + name + "/{print $0}' ";
        String[] createdContentScript = {"/bin/sh", "-c", createdCommand};
        return CommandUtil.run(createdContentScript);
    }

    /**
     * 执行开发库SQL
     * @param convertSqlPath 转换编码后的SQL文件名
     * @param patchId
     * @param processId
     * @throws Exception
     */
    public String executeSQL(String convertSqlPath, Long patchId, String processId) throws Exception {

        LogUtil.recordBuildLog("--------------执行开发库SQL开始 --------------", patchId, processId);
        log.info("------------------------------------执行开发库SQL开始-----------------------------------------");
        // 执行SQL
        String excuteScript = "echo exit | sqlplus " + username + "/" + password + "@" + hosname + ":" + port + "/" + dbname + " @" + convertSqlPath;
        log.info("------------------------------------SQL脚本：-----------------------------------------" + excuteScript);
        String[] excuteSql = {"/bin/sh", "-c", excuteScript};
        String excuteSqlMsg = CommandUtil.run(excuteSql);
        if (StringUtils.isNotEmpty(excuteSqlMsg) && (excuteSqlMsg.contains("ORA-") || excuteSqlMsg.contains("SP2-"))) {
            // SQL执行失败
            log.info("------------------------------------执行开发库SQL失败-----------------------------------------" + excuteSqlMsg);

            // TODO sqlplus执行结果格式化输出到页面
            throw new Exception("执行开发库SQL失败,请查看错误详情：" + excuteSqlMsg);
        } else if(StringUtils.isNotEmpty(excuteSqlMsg) && (!excuteSqlMsg.toUpperCase().contains("END!") && !excuteSqlMsg.toUpperCase().contains("END !"))) {
            throw new Exception("执行开发库SQL异常，请检查后重试，可能原因：SQL没有【/】,SQL执行未结束,请查看错误详情：" + excuteSqlMsg);
        }
        LogUtil.recordBuildLog("执行开发库SQL成功：" + excuteSqlMsg, patchId, processId);
        log.info("------------------------------------执行开发库SQL成功-----------------------------------------");
        return excuteSqlMsg;
    }

    /**
     * 执行标准库11gSQL
     * @param convertSqlPath 转换编码后的SQL文件名
     * @param patchId
     * @param processId
     * @throws Exception
     */
    public void executeStandardSQL(String convertSqlPath, Long patchId, String processId) throws Exception {

        LogUtil.recordBuildLog("--------------执行标准库11gSQL开始 --------------", patchId, processId);
        log.info("------------------------------------执行标准库11gSQL开始-----------------------------------------");
        // 执行SQL
        String excuteStandardScript = "echo exit | sqlplus " + standardUsername + "/" + standardPassword + "@" + standardHosname + ":" + standardPort + "/" + standardDbname + " @" + convertSqlPath;
        log.info("------------------------------------SQL脚本：-----------------------------------------" + excuteStandardScript);
        String[] excuteSql = {"/bin/sh", "-c", excuteStandardScript};
        String excuteSqlMsg = CommandUtil.run(excuteSql);
        if (StringUtils.isNotEmpty(excuteSqlMsg) && (excuteSqlMsg.contains("ORA-") || excuteSqlMsg.contains("SP2-"))) {
            // SQL执行失败
            log.info("------------------------------------执行标准库11gSQL失败-----------------------------------------" + excuteSqlMsg);

            throw new Exception("执行标准库11gSQL失败,请查看错误详情：" + excuteSqlMsg);
        } else if(StringUtils.isNotEmpty(excuteSqlMsg) && (!excuteSqlMsg.toUpperCase().contains("END!") && !excuteSqlMsg.toUpperCase().contains("END !"))) {

            throw new Exception("执行标准库11gSQL异常，请检查后重试，可能原因：SQL没有【/】,SQL执行未结束,请查看错误详情：" + excuteSqlMsg);
        }
        LogUtil.recordBuildLog("执行标准库11gSQL成功：" + excuteSqlMsg, patchId, processId);
        log.info("------------------------------------执行标准库11gSQL成功-----------------------------------------");
    }

    /**
     * 执行标准库12cSQL
     * @param convertSqlPath 转换编码后的SQL文件名
     * @param patchId
     * @param processId
     * @throws Exception
     */
    public void executeStandard12cSQL(String convertSqlPath, Long patchId, String processId) throws Exception {

        LogUtil.recordBuildLog("--------------执行标准库12cSQL开始 --------------", patchId, processId);
        log.info("------------------------------------执行标准库12cSQL开始-----------------------------------------");
        // 执行SQL
        String excuteStandardScript = "echo exit | sqlplus " + standard12cUsername + "/" + standard12cPassword + "@" + standard12cHosname + ":" + standard12cPort + "/" + standard12cDbname + " @" + convertSqlPath;
        log.info("------------------------------------SQL脚本：-----------------------------------------" + excuteStandardScript);
        String[] excuteSql = {"/bin/sh", "-c", excuteStandardScript};
        String excuteSqlMsg = CommandUtil.run(excuteSql);
        if (StringUtils.isNotEmpty(excuteSqlMsg) && (excuteSqlMsg.contains("ORA-") || excuteSqlMsg.contains("SP2-"))) {
            // SQL执行失败
            log.info("------------------------------------执行标准库12cSQL失败-----------------------------------------" + excuteSqlMsg);

            throw new Exception("执行标准库12cSQL失败,请查看错误详情：" + excuteSqlMsg);
        } else if(StringUtils.isNotEmpty(excuteSqlMsg) && !excuteSqlMsg.toUpperCase().contains("END!") && !excuteSqlMsg.toUpperCase().contains("END !")) {

            throw new Exception("执行标准库12cSQL异常，请检查后重试，可能原因：SQL没有【/】,SQL执行未结束,请查看错误详情：" + excuteSqlMsg);
        }
        LogUtil.recordBuildLog("执行标准库12cSQL成功：" + excuteSqlMsg, patchId, processId);
        log.info("------------------------------------执行标准库12cSQL成功-----------------------------------------");
    }

    /**
     * 存储过程是否有结束标识'/'
     * @param result
     * @param sourceFilePath
     *
     *
     * "21:END;\n" +
     * "38:END;EXECUTE IMMEDIATE;\n" +
     * "39:END;"
     *
     * "21:END;--END;/"
     *
     * "21:---END;END;/"
     *
     */
    private void endSign(String result, String sourceFilePath, Long patchId, String processId) throws Exception {

        // 校验SQL文件内容，存过必须以/结尾，否则程序等待输入，挂起
        // END;后必须跟/
        LogUtil.recordBuildLog("--------------检测存储过程结束标记开始--------------", patchId, processId);

        log.info("result：" + result);
        LogUtil.recordBuildLog("--------------检测含有END;的结果行--------------", patchId, processId);
        LogUtil.recordBuildLog("--------------" + result + "--------------", patchId, processId);
        String resultStr = result.toUpperCase();

        // END;校验开始
        String[] arrs = resultStr.split("\n");
        // 多行END;
        if (arrs.length > 1) {
            for (String arr : arrs) {
                String[] end = arr.split("END;");
                // 是否被注释
                String[] nums = end[0].split(":");
                if (nums.length > 1) {
                    // 被备注过的 END; 跳过
                    if (nums[1].startsWith("-")) {
                        continue;
                    }
                }
                // 多行中同一行没有其他内容
                if (end.length == 1) {
                    findContent(nums[0], sourceFilePath, patchId, processId);
                    // 同一行有其他内容
                } else {
                    if(!"/".equals(String.valueOf(end[1].charAt(0)))) {
                        throw new Exception("SQL脚本不符合规范【END;】后必须跟结束标记【/】。第" + nums[0] + "行错误。当前【END;】后内容: 【" + end[1] +"】");
                    }
                    LogUtil.recordBuildLog("--------------当前【END;】后的内容: 【" + end[1] +"】--------------", patchId, processId);
                }
            }

            // 只有一行END;
        } else {
            String[] content = arrs[0].split("END;");
            // 同一行除了END;还有其他值 "21:END;--END;/"
            // 被备注过的 END; 跳过
            String[] nums = content[0].split(":");
            if (nums.length > 1) {
                // 被备注过的 END; 跳过
                if (nums[1].startsWith("-")) {
                    return;
                }
            }

            if (content.length > 1) {
                char ch = content[1].charAt(0);
                if(!"/".equals(String.valueOf(ch))) {
                    throw new Exception("SQL脚本不符合规范【END;】后必须跟结束标记【/】。第" + nums[0] + "行，错误。当前【END;】后内容: 【" + content[1] +"】");
                }
                LogUtil.recordBuildLog("--------------当前【END; 】后的内容: 【" + content[1] +"】--------------", patchId, processId);
            } else {
                // 同一行只有END;
                findContent(nums[0], sourceFilePath, patchId, processId);
            }
        }
        LogUtil.recordBuildLog("--------------检测存储过程结束标记结束--------------", patchId, processId);
    }

    /**
     * 遍历查找END;下一行不为空的值
     * @param num 行数
     * @param sourceFilePath
     * @return
     * @throws IOException
     */
    private void findContent(String num, String sourceFilePath, Long patchId, String processId) throws Exception {

        int number = Integer.parseInt(num);
//      sed -n '106p' convertSql/CONVERT_202211181547_CREATE_PTD_TODO_DISPENSE.SQL
        String nextContent = "";
        int line = number + 1;
        while (StringUtils.isEmpty(nextContent)) {
            String nextScript = "sed -n '" + line + "p' " + sourceFilePath;
            log.info("nextScript：" + nextScript);
            String[] nextLine = {"/bin/sh", "-c", nextScript};
            nextContent = CommandUtil.run(nextLine);
            line++;
        }

        LogUtil.recordBuildLog("--------------当前【END;】后的内容: 【" + nextContent +"】--------------", patchId, processId);
        if(!"/".equals(String.valueOf(nextContent.replaceAll("\n", "").charAt(0)))) {
            throw new Exception("SQL脚本不符合规范【END;】后必须跟结束标记【/】。第" + line + "行，错误。当前【END;】后内容: 【" + nextContent +"】");
        }
    }

    /*
     * 新增脚本参数时，校验字段description不为空，且字段包含关键字：作用，说明。且日志中给出提示信息
     * 1.校验是否是新增参数脚本
     * 2.获取脚本中所有添加的parmname 【grep -i "PARMName[[:space:]]*=" CONVERT_202307061710_INSERT_BAS_PARMS.SQL | awk -F"'" '{print $2}'】
     * 3.执行脚本查询parmname中的description的值
     * 4.给出提示信息
     */
    public void verifyParam(String sourceFilePath, Long patchId, String processId) throws Exception {


        String name = sourceFilePath.substring(sourceFilePath.lastIndexOf("/") + 1);
        if (!name.contains("INSERT_BAS_PARMS")) {
            return;
        }
        String convertName = "CONVERT_" + name;
        LogUtil.recordBuildLog("--------------转换编码后的SQL名称: " + convertName + "--------------", patchId, processId);
        String convertSqlPath = convertSql + File.separator + convertName;

        /*// 转换后的脚本路径：convertSqlPath  脚本名称：name
        if (name.contains("INSERT_BAS_PARMS")) {
            String verifySql = "grep -i 'PARMNAME[[:space:]]*=' " + convertSqlPath + " | awk -F\"'\" '{print $2}'";
            log.info("--------------获取参数SQL脚本：" + verifySql + "--------------", patchId, processId);
            String[] verifyScript = {"/bin/sh", "-c", verifySql};

            String verifySqlMsg = CommandUtil.run(verifyScript);
            log.info("--------------查询参数Parmname结果：" + verifySqlMsg + "--------------", patchId, processId);
            if (StringUtils.isNotEmpty(verifySqlMsg)) {
                String[] msg = verifySqlMsg.split("\n");
                log.info("--------------查询参数表Descriptions字段结果：" + Arrays.toString(msg) + "--------------", patchId, processId);
                for (String parmname : msg) {
                    if (StringUtils.isEmpty(parmname)) {
                        continue;
                    }

                    log.info("------------------------------------Paramname：" + parmname);

                    String[] executeSql = {"/bin/sh", "-c", detectTodo  + File.separator + "parmDetect.sh " + parmname};
                    CommandUtil.run(executeSql);

                    String[] catSql = {"/bin/sh", "-c", "cat " + detectTodo  + File.separator + "parm.sql"};
                    String catResult = CommandUtil.run(catSql);
                    log.info("查询参数SQL：" + catResult);

                    String excuteScript = "echo exit | sqlplus " + username + "/" + password + "@" + hosname + ":" + port + "/" + dbname + " @" + detectTodo + File.separator + "parm.sql";
                    log.info("------------------------------------执行开发库查询参数Parmname的SQL脚本：-----------------------------------------" + excuteScript);
                    String[] excuteSql = {"/bin/sh", "-c", excuteScript};
                    String result = CommandUtil.run(excuteSql);

                    log.info("--------------查询参数表Descriptions字段结果：" + result + "--------------", patchId, processId);
                    if (!result.contains("作用") || !result.contains("说明")) {

                    }
                }
            }*/

        String[] effectSql = {"/bin/sh", "-c", "grep '作用' " + convertSqlPath};
        String effectResult = CommandUtil.run(effectSql);
        log.info("查询作用参数SQL：" + effectResult);
        String[] explainSql = {"/bin/sh", "-c", "grep '说明' " + convertSqlPath};
        String explainResult = CommandUtil.run(explainSql);
        log.info("查询说明参数SQL：" + explainResult);
        if (StringUtils.isEmpty(effectResult) || StringUtils.isEmpty(explainResult)) {
            throw new Exception(
                    "增加系统参数脚本时，表BAS_PARMS的DESCRIPTIONS字段必须包含详细的作用和说明。\n" +
                            "以下是【门诊医生站药品检索是否使用ES】参数作为样例，请后续增加系统参数时务必以此样例为准：\n" +
                            "作用：控制门诊医生站检索药品列表的查询模式，在检索药品列表时是查询oracle数据库还是查询elasticsearch索引库，使用elasticsearch时查询速度比oracle要快。\n" +
                            "说明：查当参数设为【N】代表在门诊医生站检索药品列表时在oracle数据库进行数据查询，默认值为【N】；" +
                            "当参数设为【Y】时则代表在门诊医生站检索药品时用elasticsearch索引库查询数据。要开启该参数时需要运维人员部署elasticsearch服务，" +
                            "并且将该医院的药品医嘱数据同步到了elasticsearch中，同步使用“院级系统管理-》ES同步信息”页面来操作。"
            );
        }
    }
}
