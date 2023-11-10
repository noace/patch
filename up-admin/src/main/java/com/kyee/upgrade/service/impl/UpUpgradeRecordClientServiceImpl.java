package com.kyee.upgrade.service.impl;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.kyee.upgrade.domain.*;
import com.kyee.upgrade.domain.vo.UpPatchClientExtend;
import com.kyee.upgrade.domain.vo.UpUpgradeDataDTO;
import com.kyee.upgrade.domain.vo.UpUpgradePatchDTO;
import com.kyee.upgrade.mapper.UpPatchClientMapper;
import com.kyee.upgrade.mapper.UpProjectServerMapper;
import com.kyee.upgrade.mapper.UpUpgradeLogClientMapper;
import com.kyee.upgrade.utils.*;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.StringUtils;
import org.apache.shiro.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kyee.upgrade.mapper.UpUpgradeRecordClientMapper;
import com.kyee.upgrade.service.IUpUpgradeRecordClientService;
import com.ruoyi.common.core.text.Convert;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * 升级管理Service业务层处理
 * 
 * @author zhanghaoyu
 * @date 2021-10-21
 */
@Service
@Transactional
public class UpUpgradeRecordClientServiceImpl implements IUpUpgradeRecordClientService {
    private static final Logger log = LoggerFactory.getLogger(UpUpgradeRecordClientServiceImpl.class);
    @Autowired
    private UpUpgradeRecordClientMapper upUpgradeRecordClientMapper;

    @Autowired
    private UpProjectServerMapper upProjectServerMapper;

    @Autowired
    private UpUpgradeLogClientMapper upUpgradeLogClientMapper;

    @Autowired
    private UpPatchClientMapper upPatchClientMapper;

    @Autowired
    private PatchRetryService patchRetryService;

    @Autowired
    private IUpUpgradeRecordClientService upgradeRecordClientService;

    /**
     * 查询升级管理
     * 
     * @param upgradeId 升级管理ID
     * @return 升级管理
     */
    @Override
    public UpUpgradeRecordClient selectUpUpgradeRecordClientById(Long upgradeId) {
        return upUpgradeRecordClientMapper.selectUpUpgradeRecordClientById(upgradeId);
    }

    /**
     * 查询升级管理列表
     * 
     * @param upUpgradeRecordClient 升级管理
     * @return 升级管理
     */
    @Override
    public List<UpUpgradeRecordClient> selectUpUpgradeRecordClientList(UpUpgradeRecordClient upUpgradeRecordClient)
    {
        return upUpgradeRecordClientMapper.selectUpUpgradeRecordClientList(upUpgradeRecordClient);
    }

    /**
     * 查询升级管理列表(新) TODO 待优化接口，将for循环中的查询解放出来
     *
     * @param upProjectServer 升级管理
     * @return 升级管理
     */
    @Override
    public List<UpProjectServer> selectUpUpgradeRecordClientList(UpProjectServer upProjectServer) throws Exception {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        //增加type，防止把项目应用服务的数据库服务也过滤掉
        upProjectServer.setType("升级界面");
        List<UpProjectServer> upPatchServerList = upProjectServerMapper.selectUpProjectServerList(upProjectServer);
        if (CollectionUtils.isEmpty(upPatchServerList)) {
            return upPatchServerList;
        }

        for (UpProjectServer projectServer : upPatchServerList) {
            // 查询未升级的补丁包数和未升级补丁包最小时间
            List<String> testStatus = Lists.newArrayList(ConstantUtil.UpgradeStatus.NOT_TEST.getValue(), ConstantUtil.UpgradeStatus.SQL_TESTED.getValue(),ConstantUtil.UpgradeStatus.TEST_UPGRADE.getValue());
            List<String> patchStatus = Lists.newArrayList(ConstantUtil.UpgradeStatus.NOT_UPGRADE.getValue(), ConstantUtil.UpgradeStatus.SQL_EXECUTED.getValue(),ConstantUtil.UpgradeStatus.UPGRADE_PART.getValue());
            Map<String, Object> unUpgradeMap = upProjectServerMapper.selectUnUpgrade(projectServer, testStatus, patchStatus);
            projectServer.setNoUpNum(Integer.parseInt(unUpgradeMap.get("noUpNum") + ""));
            projectServer.setNoUpMinTime(dateFormat.parse(unUpgradeMap.get("noUpMinTime") + ""));

            //远程获取是否启动
            String tomcatPath = projectServer.getTomcatPath();
            String tomcatName = tomcatPath.substring(tomcatPath.lastIndexOf(File.separator) + 1);
            String findCommand = "ps -ef | grep java | grep "+ tomcatName +" | grep -v grep | wc -l";
            try {
                String status = RemoteUtil.remoteCommandUtil(projectServer.getSshUser(), projectServer.getSshPassword(),
                        projectServer.getServerIp(), projectServer.getSshPort(), findCommand);
                if ("1".equals(status)) {
                    // Tomcat启动中
                    projectServer.setServerStatus(ConstantUtil.ServerStatus.START_OK.getValue());
                    log.info(projectServer.getServerName() + "服务器已启动");
                } else {
                    projectServer.setServerStatus(ConstantUtil.ServerStatus.NOT_START.getValue());
                    log.info(projectServer.getServerName() + "服务器未启动");
                }
            } catch (Exception e) {
                log.error(e.getMessage());
            }

            //查询该服务器最后升级包、最后升级人员、最后升级时间
            UpUpgradeDataDTO patchDTO = new UpUpgradeDataDTO();
            patchDTO.setUpgradeStatus(ConstantUtil.UpgradeRecordStatus.UPGRADE_OK.getValue());
            patchDTO.setPatchStatusList(Lists.newArrayList(ConstantUtil.UpgradeStatus.UPGRADE_PART.getValue(), ConstantUtil.UpgradeStatus.UPGRADE_ALL.getValue()));
            patchDTO.setTestStatusList(Lists.newArrayList(ConstantUtil.UpgradeStatus.TEST_UPGRADE.getValue()));
            patchDTO.setServerType(projectServer.getServerType());
            patchDTO.setProjectId(projectServer.getProjectId());
            patchDTO.setProductId(projectServer.getProductId());
            patchDTO.setServerId(projectServer.getServerId());
            List<UpUpgradeRecordClient> lastUpgradeRecordList = upUpgradeRecordClientMapper.getLastUpgradeRecordList(patchDTO);

            //取出该服务器最后升级的记录
            if (lastUpgradeRecordList.size()>0) {
                UpUpgradeRecordClient lastUpgradeRecord = lastUpgradeRecordList.get(0);
                projectServer.setLastUpgradePackage(lastUpgradeRecord.getPatchFileName());
                if (lastUpgradeRecord.getUpdateTime() == null) {
                    projectServer.setLastUpgradePerson(lastUpgradeRecord.getCreateBy());
                    projectServer.setLastUpgradeTime(lastUpgradeRecord.getCreateTime());
                } else {
                    projectServer.setLastUpgradePerson(lastUpgradeRecord.getUpdateBy());
                    projectServer.setLastUpgradeTime(lastUpgradeRecord.getUpdateTime());
                }
            }

            // 修改服务器状态
            projectServer.setUpdateBy(ShiroUtils.getSysUser().getUserName());
            projectServer.setUpdateTime(new Date());
            upProjectServerMapper.updateUpProjectServer(projectServer);
        }

        return upPatchServerList;
    }


    /**
     * 新增升级管理
     * 
     * @param upUpgradeRecordClient 升级管理
     * @return 结果
     */
    @Override
    public int insertUpUpgradeRecordClient(UpUpgradeRecordClient upUpgradeRecordClient) {
        SysUser user = (SysUser) SecurityUtils.getSubject().getPrincipal();
        upUpgradeRecordClient.setCreateBy(user.getUserName());
        upUpgradeRecordClient.setUpdateBy(user.getUserName());
        upUpgradeRecordClient.setCreateTime(DateUtils.getNowDate());
        upUpgradeRecordClient.setUpdateTime(DateUtils.getNowDate());
        return upUpgradeRecordClientMapper.insertUpUpgradeRecordClient(upUpgradeRecordClient);
    }

    /**
     * 修改升级管理
     * 
     * @param upUpgradeRecordClient 升级管理
     * @return 结果
     */
    @Override
    public int updateUpUpgradeRecordClient(UpUpgradeRecordClient upUpgradeRecordClient) {
        SysUser user = (SysUser) SecurityUtils.getSubject().getPrincipal();
        upUpgradeRecordClient.setUpdateBy(user.getUserName());
        upUpgradeRecordClient.setUpdateTime(DateUtils.getNowDate());
        return upUpgradeRecordClientMapper.updateUpUpgradeRecordClient(upUpgradeRecordClient);
    }

    /**
     * 删除升级管理对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteUpUpgradeRecordClientByIds(String ids) {
        return upUpgradeRecordClientMapper.deleteUpUpgradeRecordClientByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除升级管理信息
     * 
     * @param upgradeId 升级管理ID
     * @return 结果
     */
    @Override
    public int deleteUpUpgradeRecordClientById(Long upgradeId) {
        return upUpgradeRecordClientMapper.deleteUpUpgradeRecordClientById(upgradeId);
    }

    /**
     * 查询补丁包升级包列表
     *
     * @param dataVo
     * @return
     */
    @Override
    public List<UpPatchClientExtend> getUpgradePatchList(UpUpgradeDataDTO dataVo) {

        List<UpPatchClientExtend> clientList = new ArrayList<>();

        if ("测试".equals(dataVo.getServerType())) {
            dataVo.setServerType("测试");
            // 查询未测试和脚本已测试
            dataVo.setTestStatusList(Lists.newArrayList(ConstantUtil.UpgradeStatus.NOT_TEST.getValue(), ConstantUtil.UpgradeStatus.SQL_TESTED.getValue()));
            clientList = upUpgradeRecordClientMapper.getUpgradeRecordPatch(dataVo);
        }

        if ("生产".equals(dataVo.getServerType())) {
            dataVo.setServerType("生产");
            // 查询未升级和脚本已执行
            dataVo.setPatchStatusList(Lists.newArrayList(ConstantUtil.UpgradeStatus.NOT_UPGRADE.getValue(), ConstantUtil.UpgradeStatus.SQL_EXECUTED.getValue()));
            clientList.addAll(upUpgradeRecordClientMapper.getUpgradeRecordPatch(dataVo));

            dataVo.setPatchStatus(ConstantUtil.UpgradeStatus.UPGRADE_PART.getValue());
            clientList.addAll(upUpgradeRecordClientMapper.getPartUpgradePatch(dataVo));
        }

        if (!CollectionUtils.isEmpty(clientList)) {
            clientList = clientList.stream().sorted(Comparator.comparing(UpPatchClient::getBuildTime)).collect(Collectors.toList());
        }
        return clientList;
    }

    /**
     * 查询历史补丁包升级包列表
     *
     * @param dataVo
     * @return
     */
    @Override
    public List<UpPatchClientExtend> getHistoryUpgradePatchList(UpUpgradeDataDTO dataVo) {

        List<UpPatchClientExtend> clientList = new ArrayList<>();
        if ("测试".equals(dataVo.getServerType())) {
            dataVo.setServerType("测试");
            // 查询未测试和脚本已测试
            dataVo.setTestStatusList(Lists.newArrayList(ConstantUtil.UpgradeStatus.TEST_UPGRADE.getValue()));
            clientList = upUpgradeRecordClientMapper.getUpgradeRecordPatch(dataVo);
        }
        if ("生产".equals(dataVo.getServerType())) {
            dataVo.setServerType("生产");
            // 查询未升级和脚本已执行
            dataVo.setPatchStatusList(Lists.newArrayList(ConstantUtil.UpgradeStatus.UPGRADE_PART.getValue(), ConstantUtil.UpgradeStatus.UPGRADE_ALL.getValue()));
            clientList = upUpgradeRecordClientMapper.getUpgradeRecordPatch(dataVo);
        }

//        if (!CollectionUtils.isEmpty(clientList)) {
//            clientList = clientList.stream().sorted(Comparator.comparing(UpPatchClient::getBuildTime).reversed()).collect(Collectors.toList());
//        }
        return clientList;
    }


    /**
     * 启动tomcat服务
     *
     * @param serverId 服务Id
     */
    @Override
    public AjaxResult startServer(Integer serverId, Integer projectId, Integer productId) throws Exception {
        UpProjectServer server = upProjectServerMapper.selectUpProjectServerById(serverId);
        LogUtil.recordUpgradeLog("------------------------------------启动Tomcat服务开始------------------------------", null, serverId);
        if (ConstantUtil.ServerStatus.START_OK.getValue().equals(server.getServerStatus())) {
            return AjaxResult.error("服务器已经是启动状态！");
        }
        // 升级记录
        UpUpgradeRecordClient recordClient = new UpUpgradeRecordClient();
        recordClient.setUpStatus(ConstantUtil.UpgradeRecordStatus.UPGRADE_FAIL.getValue());
        recordClient.setServerId(serverId);
        List<UpUpgradeRecordClient> recordClientList = upUpgradeRecordClientMapper.selectUpUpgradeRecordClientList(recordClient);
        if (!CollectionUtils.isEmpty(recordClientList)) {
            return AjaxResult.error("升级失败不允许重启服务，请回退！");
        }

        // 未升级不允许启动tomcat
//        if (ConstantUtil.UpgradeStatus.NOT_UPGRADE.getValue().equals(upgradeRecordClient.getUpStatus())) {
//            LogUtil.recordUpgradeLog("------------------未升级不能重启服务，请先升级------------", null, serverId);
//            return updateServerStatus(server, null, ConstantUtil.ServerStatus.NOT_START.getValue(), ConstantUtil.UpgradeStatus.UPGRADE_FAIL.getValue(), "未升级不能重启服务，请先升级");
//
//        }

        String tomcatPath = server.getTomcatPath();
        if (!OSValidator.isUnix()) {
            LogUtil.recordUpgradeLog("目前仅支持Unix系统，坐等后续扩展!", null, serverId);
            return AjaxResult.error("目前仅支持Unix系统，坐等后续扩展!");
        }
        LogUtil.recordUpgradeLog("服务器系统为：" + server.getOsType(), null, serverId);
        // tomcat路径是否存在
        if (!verifyRemoteCatalogExist(tomcatPath, server)) {
            LogUtil.recordUpgradeLog("tomcat路径不存在：" + tomcatPath, null, serverId);
            return AjaxResult.error("tomcat路径不存在,请查看服务器配置！");
        }
        LogUtil.recordUpgradeLog("Tomcat安装路径为：" + server.getTomcatPath(), null, serverId);

        // 启动服务器
        String command = "cd " + tomcatPath + File.separator + "bin && sh startup.sh";
        try {
            LogUtil.recordUpgradeLog("启动Tomcat命令执行开始:" + command, null, serverId);
            // 执行远程服务器命令（外网通的情况下）
            RemoteUtil.remoteCommandUtil(server.getSshUser(), server.getSshPassword(), server.getServerIp(), server.getSshPort(), command);
            LogUtil.recordUpgradeLog("启动Tomcat命令执行成功", null, serverId);
        } catch (Exception e) {
            log.info(e.getMessage());
            LogUtil.recordUpgradeLog("启动Tomcat命令执行失败" + command, null, serverId);
            return AjaxResult.error("启动Tomcat命令执行失败！" + command);
        }
        LogUtil.recordUpgradeLog("------------------------------------启动Tomcat服务结束------------------------------", null, serverId);
        updateServerStatus(serverId, ConstantUtil.ServerStatus.START_OK.getValue(), null);

        // 统一在启动服务的时候，修改补丁包状态，并回传（在升级的时候单包处理了）
        // 修改补丁包状态（补丁包状态为脚本已执行，升级状态为升级成功）
        /*List<UpPatchClientExtend> patchClientList = new ArrayList<>();
        UpUpgradeDataDTO dataVo = new UpUpgradeDataDTO();
        dataVo.setProductId(productId);
        dataVo.setProjectId(projectId);
        dataVo.setServerId(serverId);
        dataVo.setSqlFlag("Y");
        dataVo.setServerType(server.getServerType());
        dataVo.setPatchStatusList(Lists.newArrayList(ConstantUtil.UpgradeStatus.SQL_EXECUTED.getValue(), ConstantUtil.UpgradeStatus.SQL_TESTED.getValue()));
        dataVo.setUpgradeStatusList(Lists.newArrayList(ConstantUtil.UpgradeRecordStatus.UPGRADE_OK.getValue()));
        List<UpPatchClientExtend> serverPatchList = upUpgradeRecordClientMapper.getStartServerPatch(dataVo);
        patchClientList.addAll(serverPatchList);

        UpUpgradeDataDTO noSqlPatch = new UpUpgradeDataDTO();
        noSqlPatch.setProductId(productId);
        noSqlPatch.setProjectId(projectId);
        noSqlPatch.setServerId(serverId);
        noSqlPatch.setSqlFlag("N");
        noSqlPatch.setServerType(server.getServerType());
        noSqlPatch.setPatchStatusList(Lists.newArrayList(ConstantUtil.UpgradeStatus.NOT_UPGRADE.getValue(), ConstantUtil.UpgradeStatus.NOT_TEST.getValue()));
        noSqlPatch.setUpgradeStatusList(Lists.newArrayList(ConstantUtil.UpgradeRecordStatus.UPGRADE_OK.getValue()));
        List<UpPatchClientExtend> noSqlPatchList = upUpgradeRecordClientMapper.getStartServerPatch(noSqlPatch);
        patchClientList.addAll(noSqlPatchList);

        if (!CollectionUtils.isEmpty(patchClientList)) {
            Map<String, List<UpPatchClientExtend>> map = patchClientList.stream().collect(Collectors.groupingBy(UpPatchClientExtend::getServerType));
            String serverType = server.getServerType();
            if (!map.isEmpty()) {
                List<UpPatchClientExtend> patchList = map.get(serverType);
                for (UpPatchClientExtend clientExtend : patchList) {
                    updateUpgradeStatus(clientExtend.getPatchId(), serverId, serverType);
                }
            }
        }*/
        LogUtil.recordUpgradeLog("------------------^v^ ^v^ ^v^服务器：【" + server.getServerName() + "】，IP：【 " + server.getServerIp() + "】自动升级结束^v^ ^v^ ^v^------------", null, serverId);
        return AjaxResult.success();
    }

    /**
     * 停止tomcat服务
     *
     * @param serverId 服务Id
     */
    @Override
    public AjaxResult stopServer(Integer serverId) {
        // 为每一次升级生成进程Id,代表一次升级
        // TODO 生成进程Id,从停止开始，认为一次升级流程
        // String processId = IdUtils.fastSimpleUUID();

        // 1.判断是否是unix系统,Tomcat路径是否存在
        // 2.判断目标服务器状态是否已启动
        // 3.根据服务Id查询目标服务器信息然后执行Linux命令
        UpProjectServer server = upProjectServerMapper.selectUpProjectServerById(serverId);

        LogUtil.recordUpgradeLog("------------------^v^ ^v^ ^v^服务器：【" + server.getServerName() + "】，IP：【 " + server.getServerIp() + "】自动升级开始^v^ ^v^ ^v^------------", null, serverId);
        if (ConstantUtil.ServerStatus.NOT_START.getValue().equals(server.getServerStatus())) {
            LogUtil.recordUpgradeLog("Tomcat已经是停止状态", null, serverId);
            return AjaxResult.error("Tomcat已经是停止状态");
        }
        String tomcatPath = server.getTomcatPath();

        if (!OSValidator.isUnix()) {
            LogUtil.recordUpgradeLog("目前仅支持Unix系统，坐等后续扩展", null, serverId);
            return AjaxResult.warn("目前仅支持Unix系统，坐等后续扩展");
        }
            // tomcat路径是否存在
        try {
            if (!verifyRemoteCatalogExist(tomcatPath, server)) {
                LogUtil.recordUpgradeLog("Tomcat路径不存在：" + tomcatPath, null, serverId);
                return AjaxResult.error("Tomcat路径不存在：" + tomcatPath);
            }
            LogUtil.recordUpgradeLog("Tomcat路径为：" + server.getTomcatPath(), null, serverId);
            // 停止服务器

            String command = "cd " + tomcatPath + File.separator + "bin && sh shutdown.sh";
            LogUtil.recordUpgradeLog("命令执行开始:" + command, null, serverId);
            // 执行远程服务器命令（外网通的情况下）
            RemoteUtil.remoteCommandUtil(server.getSshUser(), server.getSshPassword(), server.getServerIp(), server.getSshPort(), command);
            // 延迟1s,待Tomcat完全停止
            Thread.sleep(1000);
            LogUtil.recordUpgradeLog("命令执行成功", null, serverId);
        } catch (Exception e) {
            log.info(e.getMessage());
            LogUtil.recordUpgradeLog(e.getMessage(), null, serverId);
            return AjaxResult.error(e.getMessage() + ",请检查服务器信息是否正确！");
        }

        updateServerStatus(serverId, ConstantUtil.ServerStatus.NOT_START.getValue(), null);

        LogUtil.recordUpgradeLog("------------------------------------停止Tomcat服务结束------------------------------", null, serverId);
        return AjaxResult.success();
    }

    /**
     * 记录升级记录
     *
     * @param patchId  补丁ID
     * @param upStatus 升级状态
     * @param server   服务
     */
    public void recordUpgrade(Long patchId, String upStatus, UpProjectServer server, UpPatchClient patchClient) {
        // 查询历史升级记录
        Integer serverId = server.getServerId();
        UpUpgradeRecordClient upRecordClient = upUpgradeRecordClientMapper.getUpgradeRecordClient(patchId, serverId);

        // 升级记录实体
        UpUpgradeRecordClient recordClient = new UpUpgradeRecordClient();
        recordClient.setPatchId(patchId);
        recordClient.setServerId(serverId);
        recordClient.setServerName(server.getServerName());
        recordClient.setUpStatus(upStatus);
        // 新增升级记录
        Date date = new Date();
        String userName = ShiroUtils.getSysUser().getUserName();
        if (Objects.isNull(upRecordClient)) {
            recordClient.setUpTimes(1);
            recordClient.setPatchFileName(patchClient.getPatchFileName());
            recordClient.setCreateTime(date);
            recordClient.setLastUpWorker(userName);
            recordClient.setLastUpTime(date);
            recordClient.setCreateBy(userName);
            upUpgradeRecordClientMapper.insertUpUpgradeRecordClient(recordClient);
            LogUtil.recordUpgradeLog("新增升级记录: 服务器名称: " + server.getServerName() + ", 补丁名称: " + patchClient.getPatchFileName(), patchId, serverId);
        } else {
            // 修改升级记录
            recordClient.setPatchFileName(upRecordClient.getPatchFileName());
            recordClient.setUpTimes(upRecordClient.getUpTimes() + 1);
            // 如果是第二次升级，则上一次升级信息取创建信息，二次以后升级信息取修改信息
            if (upRecordClient.getUpTimes() == 1) {
                recordClient.setLastUpWorker(upRecordClient.getCreateBy());
                recordClient.setLastUpTime(upRecordClient.getCreateTime());
            } else {
                recordClient.setLastUpWorker(upRecordClient.getUpdateBy());
                recordClient.setLastUpTime(upRecordClient.getUpdateTime());
            }
            recordClient.setUpdateBy(userName);
            recordClient.setUpdateTime(date);
            upUpgradeRecordClientMapper.updateUpUpgradeRecordClient(recordClient);

            String upgradeTime = "";
            if (upRecordClient.getLastUpTime() != null) {
                upgradeTime = DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, upRecordClient.getLastUpTime());
            }
            LogUtil.recordUpgradeLog("上一次升级记录为: 第【" + upRecordClient.getUpTimes() + "】次升级, 升级人：" + upRecordClient.getLastUpWorker() + ", " +
                    "升级时间为：" + upgradeTime, patchId, serverId);
        }
    }

    /**
     * 升级包
     *
     * @param patchId  补丁Id
     * @param serverId 服务Id
     */
    @Override
    public AjaxResult sendPatch(Long patchId, Integer serverId, String serverType) {

        LogUtil.recordUpgradeLog("------------------------------------上传补丁包文件开始-------------------------------", patchId, serverId);
        UpPatchClient patchClient = upPatchClientMapper.selectUpPatchClientById(patchId);

        // 校验是否含有小于当前包编译时间并且有重复文件的补丁包 START YUNHIS-40292
        String validateMsg = upgradeRecordClientService.validateUpgrade(patchId, serverType, serverId);
        if (StringUtils.isNotEmpty(validateMsg)) {

            return AjaxResult.warn(validateMsg);
        }

        // 校验是否含有小于当前包编译时间并且有重复文件的补丁包 END YUNHIS-40292

        String compileList = patchClient.getCompileList();
        // 编译路径为空
        if (StringUtils.isEmpty(compileList)) {
            return AjaxResult.error("编译路径为空，无法完成升级，请联系管理员！" + compileList);
        }
        String profile = RuoYiConfig.getProfile(); // profile: /opt/kyup/uploadPath
        // 包文件名 PATCH_YUNHIS-26573[HIS_V3][SQLY]_20220331155312.ZIP
        String patchFileName = patchClient.getPatchFileName();
        // 包名  PATCH_YUNHIS-26573[HIS_V3][SQLY]_20220331155312
        String patchName = patchFileName.split(".ZIP")[0];
        // 获取产品名称 HIS
        String productName = patchClient.getProductName();
        // 服务器信息
        UpProjectServer server = upProjectServerMapper.selectUpProjectServerById(serverId);
        String serverIp = server.getServerIp();
        String sshUser = server.getSshUser();
        String sshPassword = server.getSshPassword();
        Integer sshPort = server.getSshPort();
        String serverPath = server.getServerPath();
        if (!ConstantUtil.ServerStatus.NOT_START.getValue().equals(server.getServerStatus())) {
            return AjaxResult.error("请先停止服务器，再升级！");
        }
        try {
            if (!ConstantUtil.ServerStatus.NOT_START.getValue().equals(server.getServerStatus())) {
                LogUtil.recordUpgradeLog("------------------Tomcat服务器还未停止------------", patchId, serverId);
                return AjaxResult.error("Tomcat服务器还未停止,请先停止服务器");
            }
            // 上传补丁包文件前备份
            LogUtil.recordUpgradeLog("------------------备份上一个版本开始------------", patchId, serverId);
            // 执行远程服务器命令（外网通的情况下）
            // tar -zcvf /home/xahot.tar.gz /xahot
            String tarPath = server.getServerPath();
            if (!verifyRemoteCatalogExist(tarPath, server)) {
                LogUtil.recordUpgradeLog("服务器文件夹路径不存在：" + tarPath, patchId, serverId);
                return AjaxResult.error("服务器文件夹路径不存在：" + tarPath);
            }

            // 升级前，备份到指定目录，包名为备份文件名
            // 备份根路径
            String YYYY_MM_DD = DateUtils.getDate();
            String rootBackupPath = RuoYiConfig.getBackupPath();
            // 备份包路径： 备份根路径 + 产品名 + 服务器名 + 包名 + 包名.tar.gz
            String patchBackupPath = rootBackupPath + File.separator + productName + File.separator
                    + server.getServerName() + File.separator + YYYY_MM_DD + File.separator + patchName + File.separator;

            patchBackupPath = new String(patchBackupPath.getBytes(), StandardCharsets.UTF_8);
            log.info("备份包目录: {}", patchBackupPath);

            if (!verifyRemoteCatalogExist(patchBackupPath, server)) {
                String mkdirScript = "mkdir -p " + patchBackupPath;
                RemoteUtil.remoteCommandUtil(sshUser, sshPassword, serverIp, sshPort, mkdirScript);
            }

            String command = "cd " + tarPath + " && tar -zcf " + patchBackupPath + patchName+".tar.gz " + server.getDeployName();

            try {
                RemoteUtil.remoteCommandUtil(sshUser, sshPassword, serverIp, sshPort, command);
            } catch (Exception e) {
                log.info(e.getMessage());
                LogUtil.recordUpgradeLog("备份命令执行失败：" + command, patchId, serverId);
                return AjaxResult.error("备份命令执行失败：" + command);
            }
            LogUtil.recordUpgradeLog("备份上一个版本路径：" + tarPath, patchId, serverId);
            LogUtil.recordUpgradeLog("------------------备份上一个版本结束------------", patchId, serverId);

            // 存放补丁包的路径
            String patchPath = profile + "/" + productName + "/" + patchFileName;
            LogUtil.recordUpgradeLog("补丁包的文件路径：" + patchPath, patchId, serverId);
            // 解压后的文件夹路径
            String unzipPath = profile + "/" + productName + "/" + patchName + "/";
            LogUtil.recordUpgradeLog("解压后的文件夹路径：" + unzipPath, patchId, serverId);
            if (!new File(unzipPath).exists()) {
                // 解压包
                LogUtil.recordUpgradeLog("补丁包解压开始【" + patchFileName + "】", patchId, serverId);
                String fileCode = (String) System.getProperties().get("file.encoding");
                boolean unzipFlag = ZipFileUtil.unZipFiles(new File(patchPath), unzipPath, fileCode);

                if (!unzipFlag) {
                    // 解压失败
                    LogUtil.recordUpgradeLog("补丁包解压失败【" + patchFileName + "】", patchId, serverId);
                    throw new Exception("解压失败！");
                }
                LogUtil.recordUpgradeLog("补丁包解压成功【" + patchFileName + "】", patchId, serverId);
            }
            // 解压成功，复制文件到目标服务器
            LogUtil.recordUpgradeLog("------------------上传补丁包命令执行开始----------------", patchId, serverId);
            // 获取补丁包解压后的文件路径
            String[] files = compileList.split(",");
            // 升级补丁包的文件
            for (String file : files) {
                if (!file.startsWith("code")) {
                    continue;
                }
                // 获取code/后的文件名
                int start = file.indexOf('/') + 1;
                int end = file.lastIndexOf('/') + 1;
                String fileUrl = file.substring(start, end);
                // 编译后code/下的文件路径
                String compileFilePath = unzipPath + file;
                LogUtil.recordUpgradeLog("补丁包解压后的文件路径: " + compileFilePath, patchId, serverId);
                // 传输文件
                String remoteTargetPath = serverPath + fileUrl;
                LogUtil.recordUpgradeLog("远程服务器文件路径: " + remoteTargetPath, patchId, serverId);

                String script = "test -d " + remoteTargetPath + " && echo 'true' || echo 'false'";
                String msg = RemoteUtil.remoteCommandUtil(sshUser, sshPassword, serverIp, sshPort, script);
                log.info("文件是否存在: " + msg);

                if ("true".equals(msg.trim())) {
                    log.info("目录已存在: " + remoteTargetPath);
                } else {
                    log.info("新建目录！");
                    String mkdirScript = "mkdir -p " + remoteTargetPath;
                    RemoteUtil.remoteCommandUtil(sshUser, sshPassword, serverIp, sshPort, mkdirScript);
                }

                boolean mkdirFlag = verifyRemoteCatalogExist(remoteTargetPath, server);
                log.info("远程文件目录是否存在：" + mkdirFlag);

                if (mkdirFlag) {
                    try {
                        RemoteUtil.remoteTransferUtil(sshUser, sshPassword, serverIp, sshPort, compileFilePath, remoteTargetPath);
                    } catch (Exception e) {
                        return AjaxResult.error(e.getMessage());
                    }
                } else {
                    return AjaxResult.error("远程文件目录创建失败！" + remoteTargetPath);
                }
            }
            LogUtil.recordUpgradeLog("------------------上传补丁包命令执行结束----------------", patchId, serverId);

            recordUpgrade(patchId, ConstantUtil.UpgradeRecordStatus.UPGRADE_OK.getValue(), server, patchClient);
            LogUtil.recordUpgradeLog("------------------------------------上传补丁包文件结束-------------------------------", patchId, serverId);
        } catch (Exception e) {
            recordUpgrade(patchId, ConstantUtil.UpgradeRecordStatus.UPGRADE_FAIL.getValue(), server, patchClient);
            LogUtil.recordUpgradeLog("上传补丁包文件失败【" + patchFileName + "】: " + e.getMessage(), patchId, serverId);
            return AjaxResult.error(e.getMessage());
        }

        try {
            updateUpgradeStatus(patchId, serverId, serverType);
        } catch (Exception e) {
            e.printStackTrace();
            return AjaxResult.error("同步云端补丁包状态失败！" + e.getMessage());
        }

        return AjaxResult.success();
    }


    /**
     * @description：验证远程目录是否存在
     * @author：DangHangHang
     * @date：2023-07-10
     */
    private boolean verifyRemoteCatalogExist(String remotePath, UpProjectServer server) throws Exception {

        String exist;
        try {
            String script = "if [ -d " + remotePath + " ]; then echo true; else echo false; fi";
            exist = RemoteUtil.remoteCommandUtil(server.getSshUser(), server.getSshPassword(), server.getServerIp(), server.getSshPort(), script);
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
        return "true".equals(exist);
    }

    /**
     * 回退
     *
     * @param patchId 补丁包Id
     * @return serverId 服务Id
     */
    public AjaxResult rollbackPatch(Long patchId, Integer serverId) {

        UpProjectServer server = upProjectServerMapper.selectUpProjectServerById(serverId);
        LogUtil.recordUpgradeLog("------------------------------------回退上一个版本开始------------------------------", patchId, serverId);

        UpPatchClient client = upPatchClientMapper.selectUpPatchClientById(patchId);
        String patchFileName = client.getPatchFileName();
        String patchName = patchFileName.split(".ZIP")[0];
        String productName = client.getProductName();
        // 备份路径
        String rootBackupPath = RuoYiConfig.getBackupPath();
        String YYYY_MM_DD = DateUtils.getDate();
        // 备份包路径： 备份根路径 + 产品名 + 服务器名 + 包名 + 包名.tar.gz
        String patchBackupPath = rootBackupPath + File.separator + productName + File.separator
                + server.getServerName() + File.separator + YYYY_MM_DD + File.separator + patchName + File.separator;
        patchBackupPath = new String(patchBackupPath.getBytes(), StandardCharsets.UTF_8);

        LogUtil.recordUpgradeLog("------------------------------------上一个版本备份路径 :" + patchBackupPath + "------------------------------", patchId, serverId);

        if (!ConstantUtil.ServerStatus.NOT_START.getValue().equals(server.getServerStatus())) {
            LogUtil.recordUpgradeLog("------------------Tomcat服务器还未停止------------", patchId, serverId);
            return AjaxResult.error("Tomcat服务器还未停止,请先停止服务器");
        }
        // 执行远程服务器命令（外网通的情况下）
        // 解压升级之前备份的包
        // tar -zxvf /home/xahot.tar.gz /xahot
        String tarPath = server.getServerPath();
        String command = "cd " + tarPath + " && tar -zxf " + patchBackupPath + patchName+".tar.gz";
        try {
            if (!verifyRemoteCatalogExist(tarPath, server)) {
                LogUtil.recordUpgradeLog("服务器路径不存在：" + tarPath, patchId, serverId);
                return AjaxResult.error("服务器文件夹路径不存在：" + tarPath);
            }

            if (!verifyRemoteCatalogExist(patchBackupPath, server)) {
                LogUtil.recordUpgradeLog("上一个版本备份路径不存在：" + patchBackupPath, patchId, serverId);
                return AjaxResult.error("上一个版本备份路径不存在：" + patchBackupPath);
            }
            RemoteUtil.remoteCommandUtil(server.getSshUser(), server.getSshPassword(), server.getServerIp(), server.getSshPort(), command);
        } catch (Exception e) {
            log.info(e.getMessage());
            LogUtil.recordUpgradeLog("回退命令执行失败: " + command, patchId, serverId);
            return AjaxResult.error("回退命令执行失败：" + command);
        }
        LogUtil.recordUpgradeLog("------------------------------------回退上一个版本结束------------------------------", patchId, serverId);

        // 修改端补丁包状态
        UpPatchClient patchClient = new UpPatchClient();
        patchClient.setPatchId(patchId);
        patchClient.setUpdateTime(new Date());
        patchClient.setUpBy(ShiroUtils.getSysUser().getUserName());
        recordUpgrade(patchId, ConstantUtil.UpgradeRecordStatus.ROLLBACK_OK.getValue(), server, patchClient);

        if ("测试".equals(server.getServerType())) {
            if ("Y".equals(client.getSqlFlag())) {
                patchClient.setTestStatus(ConstantUtil.UpgradeStatus.SQL_TESTED.getValue());
            } else {
                patchClient.setTestStatus(ConstantUtil.UpgradeStatus.NOT_TEST.getValue());
            }
        } else if ("生产".equals(server.getServerType())) {
            List<UpProjectServer> upPatchServerList = upProjectServerMapper.selectUpProjectServerList(server);
            List<Integer> serverIds = upPatchServerList.stream().map(UpProjectServer::getServerId).collect(Collectors.toList());
            // 升级记录
            List<UpUpgradeRecordClient> upgradeRecordClientList = upUpgradeRecordClientMapper.getUpgradeRecordClientList(patchId, serverIds);
            if (!CollectionUtils.isEmpty(upgradeRecordClientList)) {
                // 升级成功
                List<String> upSuccessStatusList = upgradeRecordClientList.stream()
                        .filter(s -> (ConstantUtil.UpgradeRecordStatus.UPGRADE_OK.getValue().equals(s.getUpStatus())))
                        .map(UpUpgradeRecordClient::getUpStatus).collect(Collectors.toList());
                log.info("有升级记录！");
                if (upSuccessStatusList.size() == 0) {
                    log.info("有脚本? :" + client.getSqlFlag());
                    if ("Y".equals(client.getSqlFlag())){
                        patchClient.setPatchStatus(ConstantUtil.UpgradeStatus.SQL_EXECUTED.getValue());
                    } else {
                        patchClient.setPatchStatus(ConstantUtil.UpgradeStatus.NOT_UPGRADE.getValue());
                    }
                }
            } else if ("Y".equals(client.getSqlFlag())){
                log.info("没有升级记录，有脚本的回退！");
                patchClient.setPatchStatus(ConstantUtil.UpgradeStatus.SQL_EXECUTED.getValue());
            } else {
                log.info("没有升级记录，没脚本的回退！");
                patchClient.setPatchStatus(ConstantUtil.UpgradeStatus.NOT_UPGRADE.getValue());
            }
        } else {
            return AjaxResult.error("服务器类型错误,请检查：" + server.getServerType());
        }
        upPatchClientMapper.updateUpPatchClient(patchClient);
        return AjaxResult.success();
    }

    /**
     * 手动升级
     *
     * @param patchId  补丁包Id
     * @param serverId 服务器Id
     */
    @Transactional
    public AjaxResult handUpgrade(Long patchId, Integer serverId) {
        UpProjectServer server = upProjectServerMapper.selectUpProjectServerById(serverId);
        try {
            SysUser sysUser = ShiroUtils.getSysUser();
            String username = sysUser.getUserName();
            Date date = DateUtils.getNowDate();

            UpUpgradeRecordClient recordClient = new UpUpgradeRecordClient();
            recordClient.setServerId(serverId);
            recordClient.setPatchId(patchId);
            recordClient.setUpStatus(ConstantUtil.UpgradeRecordStatus.UPGRADE_OK.getValue());
            recordClient.setRemark("手动升级");

            UpUpgradeRecordClient upgradeRecordClient = upUpgradeRecordClientMapper.getUpgradeRecordClient(patchId, serverId);
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
                upUpgradeRecordClientMapper.updateUpUpgradeRecordClient(recordClient);
            } else {
                // 此前没有升级记录
                // 手动升级默认升级一次
                recordClient.setUpTimes(1);
                recordClient.setCreateBy(username);
                recordClient.setCreateTime(date);
                upUpgradeRecordClientMapper.insertUpUpgradeRecordClient(recordClient);
            }
            // 修复up_patch_client中包及其子包,云端补丁包状态
            String message = updateUpgradeStatus(patchId, serverId, server.getServerType());
            if (!StringUtils.isEmpty(message)) {
                log.info(message);
                LogUtil.recordUpgradeLog(message, patchId, serverId);
                return AjaxResult.error(message);
            }

            // 修复up_upgrade_log_client的数据，此包为手动升级，升级时间，升级人
            UpUpgradeLogClient logClient = new UpUpgradeLogClient();
            logClient.setServerId(serverId);
            logClient.setPatchId(patchId);
            logClient.setUpTime(date);
            logClient.setCreateTime(date);
            logClient.setCreateBy(username);
            logClient.setLogContent("此补丁包为手动升级，升级人：" + username + ", 升级时间：" + DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, date));
            upUpgradeLogClientMapper.insertUpUpgradeLogClient(logClient);
        } catch (Exception e) {
            log.info(e.getMessage());
            LogUtil.recordUpgradeLog("手动升级失败", patchId, serverId);
            return AjaxResult.error("手动升级失败");
        }
        return AjaxResult.success();
    }

    /**
     * 修改端补丁包状态
     *
     * @param patchId
     * @param serverType
     */
    public String updateUpgradeStatus(Long patchId, Integer serverId, String serverType) throws Exception {
        UpPatchClient patchClient = upPatchClientMapper.selectUpPatchClientById(patchId);
        UpPatchClient client = new UpPatchClient();
        // 查询服务器列表
        UpProjectServer server = new UpProjectServer();
        server.setProductId(patchClient.getProductId());
        server.setProjectId(patchClient.getProjectId());
        server.setType("升级界面");
        server.setServerType(serverType);
        if ("生产".equals(serverType)) {
            List<UpProjectServer> upPatchServerList = upProjectServerMapper.selectUpProjectServerList(server);
            List<Integer> serverIds = upPatchServerList.stream().map(UpProjectServer::getServerId).collect(Collectors.toList());
            // 升级记录
            List<UpUpgradeRecordClient> upgradeRecordClientList = upUpgradeRecordClientMapper.getUpgradeRecordClientList(patchClient.getPatchId(), serverIds);
            if (!CollectionUtils.isEmpty(upgradeRecordClientList)) {
                // 升级成功
                List<String> upSuccessStatusList = upgradeRecordClientList.stream()
                        .filter(s -> (ConstantUtil.UpgradeRecordStatus.UPGRADE_OK.getValue().equals(s.getUpStatus())))
                        .map(UpUpgradeRecordClient::getUpStatus)
                        .collect(Collectors.toList());

                LogUtil.recordUpgradeLog("服务器数量: " + upPatchServerList.size(), patchId, serverId);
                LogUtil.recordUpgradeLog("升级成功数量: " + upSuccessStatusList.size(), patchId, serverId);
                LogUtil.recordUpgradeLog("未升级数量: " + (upPatchServerList.size() - upSuccessStatusList.size()), patchId, serverId);
                // 升级成功状态数量==服务器数量
                if (upSuccessStatusList.size() == upPatchServerList.size()) {
                    client.setPatchStatus(ConstantUtil.UpgradeStatus.UPGRADE_ALL.getValue());
                    // 升级成功状态数量<服务器数量 && 升级成功状态数量不为0
                } else if (upSuccessStatusList.size() != 0 && upSuccessStatusList.size() < upPatchServerList.size()) {
                    client.setPatchStatus(ConstantUtil.UpgradeStatus.UPGRADE_PART.getValue());
                    // 其他都为未升级
                } else {
                    client.setPatchStatus(ConstantUtil.UpgradeStatus.NOT_UPGRADE.getValue());
                }
            } else {
                client.setPatchStatus(ConstantUtil.UpgradeStatus.NOT_UPGRADE.getValue());
            }
        } else if ("测试".equals(serverType)){
            client.setTestStatus(ConstantUtil.UpgradeStatus.TEST_UPGRADE.getValue());
        }
        client.setProjectId(patchClient.getProjectId());
        client.setPatchId(patchClient.getPatchId());
        client.setPatchFileName(patchClient.getPatchFileName());
        client.setUpdateTime(new Date());
        if ("Y".equals(patchClient.getMergePackageFlag())) {
            client.setMergePackageFlag(String.valueOf(patchId));
        }
        upPatchClientMapper.updatePatchAndSubPatchById(client);
        log.info("------------------------服务类型：" + serverType + "----------------------");
        if ("生产".equals(serverType)) {
            log.info("------------------------同步云端状态Start：" + client.getPatchId() + "----------------------");
            client.setMergePackageFlag(patchClient.getMergePackageFlag());
            if (ConstantUtil.UpgradeStatus.UPGRADE_PART.getValue().equals(client.getPatchStatus())) {
                client.setPatchStatus(ConstantUtil.UpgradeStatus.UPGRADE_ALL.getValue());
            }
            patchRetryService.syncCloudPatchStatus(client);
            log.info("------------------------同步云端状态End: " + client.getPatchFileName() + "----------------------");
        }
        return null;
    }

    /**
     * 修改服务状态，提示信息
     *
     * @param serverId     服务Id
     * @param serverStatus 服务状态
     * @param message      提示信息
     * @return AjaxResult
     */
    private AjaxResult updateServerStatus(Integer serverId, String serverStatus, String message) {

        String username = ShiroUtils.getSysUser().getUserName();
        UpProjectServer server = new UpProjectServer();
        server.setServerStatus(serverStatus);
        server.setUpdateTime(new Date());
        server.setUpdateBy(username);
        server.setServerId(serverId);
        upProjectServerMapper.updateUpProjectServer(server);

        return StringUtils.isEmpty(message) ? AjaxResult.success() : AjaxResult.error(message);
    }

    /**
     * 查看升级日志
     *
     * @param patchId  补丁Id
     * @param serverId 服务Id
     */
    public List<UpUpgradeLogClient> getServerLogs(Long patchId, Integer serverId) {

        UpUpgradeLogClient logClient = new UpUpgradeLogClient();
        logClient.setPatchId(patchId);
        logClient.setServerId(serverId);
        return upUpgradeLogClientMapper.selectUpUpgradeLogClientList(logClient);
    }

    /**
     * 获取服务器状态
     * @param serverId
     * @return
     */
    public UpProjectServer getServerStatus(Integer serverId) {

        return upProjectServerMapper.selectUpProjectServerById(serverId);
    }

    /**
     * 查询未升级且小于当前包编译时间的补丁包列表
     *
     * @return 结果
     */
    @Override
    public String validateUpgrade(Long patchId, String serverType, Integer serverId) {
        UpPatchClient patchClient = upPatchClientMapper.selectUpPatchClientById(patchId);


        List<UpPatchClient> patchClientList = new ArrayList<>();
        UpUpgradePatchDTO patchDTO = new UpUpgradePatchDTO();
        // 查询未升级且小于当前包编译时间的补丁包列表

        patchDTO.setPatchId(patchId);
        patchDTO.setProductId(patchClient.getProductId());
        patchDTO.setProjectId(patchClient.getProjectId());
        patchDTO.setServerType(serverType);
        patchDTO.setServerId(serverId);
        patchDTO.setBuildTime(patchClient.getBuildTime());

        if ("测试".equals(serverType)) {
            patchDTO.setTestStatus(ConstantUtil.UpgradeStatus.TEST_UPGRADE.getValue());
            patchClientList.addAll(upPatchClientMapper.getTestUpgradePatch(patchDTO));
        } else if ("生产".equals(serverType)) {
            patchDTO.setPatchStatusList(Lists.newArrayList(ConstantUtil.UpgradeStatus.NOT_UPGRADE.getValue(), ConstantUtil.UpgradeStatus.SQL_EXECUTED.getValue()));
            patchClientList.addAll(upPatchClientMapper.getUpgradeRecordPatch(patchDTO));

            patchDTO.setPatchStatus(ConstantUtil.UpgradeStatus.UPGRADE_PART.getValue());
            patchClientList.addAll(upPatchClientMapper.getPartUpgradePatch(patchDTO));
        }

        if (!CollectionUtils.isEmpty(patchClientList)) {
            if (CollectionUtils.isEmpty(patchClientList)) {
                AjaxResult.success();
            }
            Set<String> nameList = new HashSet<>();
            Map<Long, UpPatchClient> idMap = patchClientList.stream().collect(Collectors.toMap(UpPatchClient::getPatchId, s -> s));

            // 待升级的包
            List<String> upgradeCodeList = Arrays.asList(patchClient.getCodeList().replaceAll("\r|\n", "").split(","));
            List<String> codeListPublish = upgradeCodeList.stream().distinct().filter(s -> !s.endsWith("SQL")).collect(Collectors.toList());

            for (UpPatchClient upPatchClient : patchClientList) {
                for (String codePath : codeListPublish) {
                    if (upPatchClient.getCodeList().contains(codePath)) {
                        UpPatchClient up = idMap.get(upPatchClient.getPatchId());
                        String patchFileName = up.getPatchFileName();
                        nameList.add(patchFileName);
                    }
                }
            }
            if (StringUtils.isNotEmpty(nameList)) {
                // 升级提示
                return "【请注意】系统检测到该补丁包已携带部分代码，请先升级【" + "," + nameList.toString() + "】，否则将出现升级问题！";
            }
        }
        return "";
    }
}
