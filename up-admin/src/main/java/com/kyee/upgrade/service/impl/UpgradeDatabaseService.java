package com.kyee.upgrade.service.impl;

import com.kyee.upgrade.common.domain.service.ExecuteSQLCommonService;
import com.kyee.upgrade.domain.UpPatchClient;
import com.kyee.upgrade.domain.UpProjectServer;
import com.kyee.upgrade.domain.UpUpgradeLogClient;
import com.kyee.upgrade.domain.UpUpgradeRecordClient;
import com.kyee.upgrade.mapper.*;
import com.kyee.upgrade.service.IUpUpgradeRecordClientService;
import com.kyee.upgrade.utils.ConstantUtil;
import com.kyee.upgrade.utils.LogUtil;
import com.kyee.upgrade.utils.ZipFileUtil;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.exception.BusinessException;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * 数据库管理
 */
@Service
@Transactional(rollbackFor = RuntimeException.class)
public class UpgradeDatabaseService {
    private static final Logger log = LoggerFactory.getLogger(UpgradeDatabaseService.class);

    @Value(value = "${ruoyi.convertSql}")
    private String convertSql;
    @Value(value = "${ruoyi.detectPath}")
    private String detectPath;

    @Autowired
    private UpgradeDatabaseMapper databaseMapper;
    @Autowired
    private ExecuteSQLCommonService sqlCommonService;
    @Autowired
    private UpProjectServerMapper serverMapper;
    @Autowired
    private UpUpgradeLogClientMapper logClientMapper;
    @Autowired
    private UpPatchClientMapper patchClientMapper;
    @Autowired
    private UpUpgradeRecordClientMapper recordClientMapper;
    @Autowired
    private IUpUpgradeRecordClientService recordClientService;

    /**
     * 执行SQL
     * @param patchId
     * @param serverId
     */
    public void executeSQL(Long patchId, Integer serverId) {

        UpPatchClient patchClient = patchClientMapper.selectUpPatchClientById(patchId);
        String compileList = patchClient.getCompileList();
        // 编译路径为空
        if (StringUtils.isEmpty(compileList)) {
            throw new BusinessException("SQL路径为空，无法执行SQL，请联系管理员");
        }
        // 查询数据库信息
        UpProjectServer databaseInfo = databaseMapper.getDatabaseInfo(serverId);
        if (Objects.isNull(databaseInfo)) {
            throw new BusinessException("没有查询到数据库信息，请先维护");
        }
        String serverName = databaseInfo.getServerName();
        String hosname = databaseInfo.getServerIp();
        Integer port = databaseInfo.getServerPort();
        String dbname = databaseInfo.getServerPath();
        String username = databaseInfo.getSshUser();
        String password = databaseInfo.getSshPassword();

        String profile = RuoYiConfig.getProfile(); // profile: /opt/kyup/uploadPath
        // 包文件名 PATCH_YUNHIS-26573[HIS_V3][SQLY]_20220331155312.ZIP
        String patchFileName = patchClient.getPatchFileName();
        // 获取产品名称 HIS
        String productName = patchClient.getProductName();
        // 包名  PATCH_YUNHIS-26573[HIS_V3][SQLY]_20220331155312
        String patchName = patchFileName.split(".ZIP")[0];
        // 存放补丁包的路径
        String patchPath = profile + "/" + productName + "/" + patchFileName;
        LogUtil.recordUpgradeLog("补丁包的文件路径：" + patchPath, patchId, serverId);

        // 解压后的文件夹路径
        String unzipPath = profile + "/" + productName + "/" + patchName + "/";
        LogUtil.recordUpgradeLog("--------------解压后的文件夹路径：" + unzipPath, patchId, serverId);

        if (!new File(unzipPath).exists()) {
            // 解压包
            LogUtil.recordUpgradeLog("补丁包解压开始【" + patchFileName + "】", patchId, serverId);
            String fileCode = (String) System.getProperties().get("file.encoding");
            boolean unzipFlag = ZipFileUtil.unZipFiles(new File(patchPath), unzipPath, fileCode);
            if (!unzipFlag) {
                // 解压失败
                LogUtil.recordUpgradeLog("补丁包解压失败【" + patchFileName + "】", patchId, serverId);
                throw new BusinessException("解压失败！");
            }
            LogUtil.recordUpgradeLog("补丁包解压成功【" + patchFileName + "】", patchId, serverId);
        }

        // 获取补丁包解压后的文件路径
        String[] files = compileList.split(",");
        // 执行脚本
        try {
            for (String file : files) {
                if (file.startsWith("code")) {
                    continue;
                }
                if (file.startsWith("sql")) {
                    // 编译后code/下的文件路径
                    String sqlFilePath = unzipPath + file;
                    String fileName = sqlFilePath.substring(sqlFilePath.lastIndexOf("/") + 1);
                    LogUtil.recordUpgradeLog("--------------SQL文件名：" + fileName, patchId, serverId);
                    String convertSqlPath = sqlCommonService.detectSql(fileName, sqlFilePath, patchId, serverId);
                    if (StringUtils.isNotEmpty(convertSqlPath)) {
                        sqlCommonService.executeSQL(convertSqlPath, patchId, serverName, serverId, username, password, hosname, port, dbname);
                    } else {
                        LogUtil.recordUpgradeLog("-------------转换后的SQL脚本文件为空--------------", patchId, serverId);
                    }
                }
            }
            LogUtil.recordUpgradeLog("--------------脚本执行成功--------------", patchId, serverId);
            recordClientService.recordUpgrade(patchId, ConstantUtil.UpgradeRecordStatus.UPGRADE_OK.getValue(), databaseInfo, patchClient);
        } catch (Exception e) {
            recordClientService.recordUpgrade(patchId, ConstantUtil.UpgradeRecordStatus.UPGRADE_FAIL.getValue(), databaseInfo, patchClient);
            LogUtil.recordUpgradeLog("--------------脚本执行失败--------------" + e.getMessage(), patchId, serverId);
            log.info("脚本执行失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("脚本执行失败，请查看日志");
        }

        // 修改补丁包状态
        patchClient.setUpdateTime(new Date());
        patchClient.setUpBy(ShiroUtils.getSysUser().getUserName());
        if ("测试".equals(databaseInfo.getServerType())) {
            patchClient.setTestStatus(ConstantUtil.UpgradeStatus.SQL_TESTED.getValue());
        } else if ("生产".equals(databaseInfo.getServerType())) {
            patchClient.setPatchStatus(ConstantUtil.UpgradeStatus.SQL_EXECUTED.getValue());
        } else {
            throw new BusinessException("服务器类型错误,请检查：" + databaseInfo.getServerType());
        }
        patchClientMapper.updateUpPatchClient(patchClient);
    }

    /**
     * 查看升级日志
     * @param patchId 补丁Id
     * @param serverId 服务Id
     */
    public List<UpUpgradeLogClient> getServerLogs(Long patchId, Integer serverId) {
        UpUpgradeLogClient logClient = new UpUpgradeLogClient();
        logClient.setPatchId(patchId);
        logClient.setServerId(serverId);
        return logClientMapper.selectUpUpgradeLogClientList(logClient);
    }

    /**
     * 手动执行脚本
     * @param patchId 补丁Id
     * @param serverId 服务Id
     */
    public AjaxResult handExecute(Integer serverId, Long patchId) {
        UpProjectServer server = serverMapper.selectUpProjectServerById(serverId);
        try {
            String username = ShiroUtils.getSysUser().getUserName();
            Date date = DateUtils.getNowDate();
            UpUpgradeRecordClient recordClient = new UpUpgradeRecordClient();
            recordClient.setServerId(serverId);
            recordClient.setPatchId(patchId);
            recordClient.setUpStatus(ConstantUtil.UpgradeRecordStatus.UPGRADE_OK.getValue());
            recordClient.setRemark("手动执行脚本");
            UpUpgradeRecordClient upgradeRecordClient = recordClientMapper.getUpgradeRecordClient(patchId, serverId);
            if (!Objects.isNull(upgradeRecordClient)) {
                // 有升级记录，修改
                recordClient.setUpdateTime(new Date());
                recordClient.setUpdateBy(username);
                recordClient.setUpTimes(upgradeRecordClient.getUpTimes() + 1);
                if (1 == upgradeRecordClient.getUpTimes()) {
                    recordClient.setLastUpTime(upgradeRecordClient.getCreateTime());
                } else {
                    recordClient.setLastUpTime(upgradeRecordClient.getUpdateTime());
                }
                recordClient.setLastUpWorker(username);
                recordClientMapper.updateUpUpgradeRecordClient(recordClient);
            } else {
                // 此前没有升级记录,手动升级默认升级一次
                recordClient.setUpTimes(1);
                recordClient.setCreateBy(username);
                recordClient.setCreateTime(date);
                recordClientMapper.insertUpUpgradeRecordClient(recordClient);
            }

            UpPatchClient patchClient = new UpPatchClient();
            patchClient.setPatchId(patchId);
            // 修改补丁包状态
            patchClient.setUpdateTime(new Date());
            patchClient.setUpBy(ShiroUtils.getSysUser().getUserName());
            if ("测试".equals(server.getServerType())) {
                patchClient.setTestStatus(ConstantUtil.UpgradeStatus.SQL_TESTED.getValue());
            } else if ("生产".equals(server.getServerType())) {
                patchClient.setPatchStatus(ConstantUtil.UpgradeStatus.SQL_EXECUTED.getValue());
            } else {
                throw new Exception("服务器类型错误,请检查：" + server.getServerType());
            }
            patchClientMapper.updateUpPatchClient(patchClient);
            // TODO 子包
            // 修复日志表的数据，此包脚本为手动执行，执行时间，执行人
            UpUpgradeLogClient logClient = new UpUpgradeLogClient();
            logClient.setServerId(serverId);
            logClient.setPatchId(patchId);
            logClient.setUpTime(date);
            logClient.setCreateTime(date);
            logClient.setCreateBy(username);
            logClient.setLogContent("此补丁包脚本为手动执行，执行人：" + username + ", 执行时间：" + DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, date));
            logClientMapper.insertUpUpgradeLogClient(logClient);
        } catch (Exception e) {
            LogUtil.recordUpgradeLog("手动执行脚本失败", patchId, serverId);
            return AjaxResult.error("手动执行脚本失败!");
        }
        return AjaxResult.success();
    }
}
