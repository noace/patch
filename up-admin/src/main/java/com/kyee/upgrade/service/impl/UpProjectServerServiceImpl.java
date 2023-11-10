package com.kyee.upgrade.service.impl;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

import com.google.common.collect.Lists;
import com.kyee.feign.IFeignClient;
import com.kyee.upgrade.domain.UpPatchClient;
import com.kyee.upgrade.domain.UpUpgradeLogClient;
import com.kyee.upgrade.domain.UpUpgradeRecordClient;
import com.kyee.upgrade.domain.vo.UpUpgradeDataDTO;
import com.kyee.upgrade.mapper.UpPatchClientMapper;
import com.kyee.upgrade.mapper.UpUpgradeLogClientMapper;
import com.kyee.upgrade.mapper.UpUpgradeRecordClientMapper;
import com.kyee.upgrade.service.IUpUpgradeRecordClientService;
import com.kyee.upgrade.utils.*;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.domain.entity.SysUser;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.common.utils.ShiroUtils;
import com.ruoyi.common.utils.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kyee.upgrade.mapper.UpProjectServerMapper;
import com.kyee.upgrade.domain.UpProjectServer;
import com.kyee.upgrade.service.IUpProjectServerService;
import com.ruoyi.common.core.text.Convert;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

/**
 * 项目应用服务Service业务层处理
 *
 * @author lijunqiang
 * @date 2022-04-12
 */
@Service
@Transactional
public class UpProjectServerServiceImpl implements IUpProjectServerService
{

    private static final Logger log = LoggerFactory.getLogger(UpProjectServerServiceImpl.class);

    @Autowired
    private UpProjectServerMapper upProjectServerMapper;

    @Autowired
    private UpPatchClientMapper upPatchClientMapper;

    @Autowired
    private UpUpgradeLogClientMapper upUpgradeLogClientMapper;

    @Autowired
    private UpUpgradeRecordClientMapper upUpgradeRecordClientMapper;

    @Autowired
    private IFeignClient feignClient;

    @Autowired
    private IUpUpgradeRecordClientService recordClientService;

    /**
     * 查询项目应用服务
     *
     * @param serverId 项目应用服务ID
     * @return 项目应用服务
     */
    @Override
    public UpProjectServer selectUpProjectServerById(Integer serverId)
    {
        return upProjectServerMapper.selectUpProjectServerById(serverId);
    }

    /**
     * 查询项目应用服务列表
     *
     * @param upProjectServer 项目应用服务
     * @return 项目应用服务
     */
    @Override
    public List<UpProjectServer> selectUpProjectServerList(UpProjectServer upProjectServer)
    {
        return upProjectServerMapper.selectUpProjectServerList(upProjectServer);
    }

    /**
     * 新增项目应用服务
     *
     * @param upProjectServer 项目应用服务
     * @return 结果
     */
    @Override
    public int insertUpProjectServer(UpProjectServer upProjectServer)
    {
        upProjectServer.setCreateTime(DateUtils.getNowDate());
        SysUser sysUser = ShiroUtils.getSysUser();
        upProjectServer.setCreateBy(sysUser.getUserName());
        return upProjectServerMapper.insertUpProjectServer(upProjectServer);
    }

    /**
     * 修改项目应用服务
     *
     * @param upProjectServer 项目应用服务
     * @return 结果
     */
    @Override
    public int updateUpProjectServer(UpProjectServer upProjectServer)
    {
        upProjectServer.setUpdateTime(DateUtils.getNowDate());
        upProjectServer.setUpdateBy(ShiroUtils.getSysUser().getUserName());
        return upProjectServerMapper.updateUpProjectServer(upProjectServer);
    }

    /**
     * 删除项目应用服务对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteUpProjectServerByIds(String ids)
    {
        return upProjectServerMapper.deleteUpProjectServerByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除项目应用服务信息
     *
     * @param serverId 项目应用服务ID
     * @return 结果
     */
    @Override
    public int deleteUpProjectServerById(Integer serverId)
    {
        return upProjectServerMapper.deleteUpProjectServerById(serverId);
    }

    /**
     * 查询服务器列表
     * @param patchId 补丁Id
     * @return
     */
    @Override
    public List<UpProjectServer> getUpgradeServers(Long patchId,UpProjectServer server) {

        log.info("------------------查询服务器列表开始------------------");

        // 根据端patchId查询项目和产品id
        UpPatchClient patchClient = upPatchClientMapper.selectUpPatchClientById(patchId);
        List<UpProjectServer> upPatchServerList = new ArrayList<>();
        if (!Objects.isNull(patchClient)) {
            // 查询服务器列表
            server.setProductId(patchClient.getProductId());
            server.setProjectId(patchClient.getProjectId());
            //增加type，防止把项目应用服务的数据库服务也过滤掉
            server.setType("升级界面");
            upPatchServerList  = upProjectServerMapper.selectUpProjectServerList(server);
            if (CollectionUtils.isEmpty(upPatchServerList)) {
                return upPatchServerList;
            }
            for (UpProjectServer projectServer : upPatchServerList) {
                // 如果服务器状态为空，则执行远程命令查询服务器状态
//                if (StringUtils.isEmpty(projectServer.getServerStatus())) {
//                LogUtil.recordUpgradeLog(projectServer.getServerName() + "服务器状态为: [" + projectServer.getServerStatus() + "]" , patchId, projectServer.getServerId());

                String tomcatPath = projectServer.getTomcatPath();
                String tomcatName = tomcatPath.substring(tomcatPath.lastIndexOf(File.separator) + 1);

//                LogUtil.recordUpgradeLog(projectServer.getServerName() + "服务器, Tomcat名称为: [" + tomcatName + "]" , patchId, projectServer.getServerId());

                String findCommand = "ps -ef | grep java | grep "+ tomcatName +" | grep -v grep | wc -l";
                    try {
                        String status = RemoteCommandUtil.remoteCommandUtil(projectServer.getSshUser(), projectServer.getSshPassword(),
                                projectServer.getServerIp(), projectServer.getSshPort(), findCommand);
                        if ("1".equals(status)) {
                            // Tomcat启动中
                            projectServer.setServerStatus(ConstantUtil.ServerStatus.START_OK.getValue());
                            log.info(projectServer.getServerName() + "服务器已启动");
//                            LogUtil.recordUpgradeLog( projectServer.getServerName() + "服务器已启动: " + findCommand, patchId, projectServer.getServerId());
                        } else {
                            projectServer.setServerStatus(ConstantUtil.ServerStatus.NOT_START.getValue());
                            log.info(projectServer.getServerName() + "服务器未启动");
//                            LogUtil.recordUpgradeLog(projectServer.getServerName() + "服务器未启动", patchId, projectServer.getServerId());
                        }
                    } catch (Exception e) {
                        log.info(e.getMessage());
//                        LogUtil.recordUpgradeLog(projectServer.getServerName() + "查询服务器状态失败" + e.getMessage(), patchId, projectServer.getServerId());
                    }
                //查询该服务器最后升级包、最后升级人员、最后升级时间
                UpUpgradeDataDTO patchDTO = new UpUpgradeDataDTO();
                patchDTO.setServerId(server.getServerId());

                List<UpUpgradeRecordClient> lastUpgradeRecordList = upUpgradeRecordClientMapper.getLastUpgradeRecordList(patchDTO);
                //取出该服务器最后升级的记录
                if (lastUpgradeRecordList.size()>0) {
                    UpUpgradeRecordClient lastUpgradeRecord = lastUpgradeRecordList.get(0);
                    projectServer.setLastUpgradePackage(lastUpgradeRecord.getPatchFileName());
                    projectServer.setLastUpgradePerson(lastUpgradeRecord.getLastUpWorker());
                    projectServer.setLastUpgradeTime(lastUpgradeRecord.getLastUpTime());
                }

                UpUpgradeRecordClient upgradeRecordClient = upUpgradeRecordClientMapper.getUpgradeRecordClient(patchId, projectServer.getServerId());
                    if (!Objects.isNull(upgradeRecordClient)) {
                        String upStatus = upgradeRecordClient.getUpStatus();
                        updateServerStatus(projectServer, patchId, null, upStatus, null);
                        projectServer.setUpgradeStatus(upStatus);
//                        LogUtil.recordUpgradeLog(projectServer.getServerName() + "查询升级状态：" + upStatus, patchId, projectServer.getServerId());
                    } else {
                        upProjectServerMapper.updateUpProjectServer(projectServer);
                        projectServer.setUpgradeStatus(ConstantUtil.UpgradeStatus.NOT_UPGRADE.getValue());
//                        LogUtil.recordUpgradeLog(projectServer.getServerName() + "此前没有升级记录，升级状态默认为未升级", patchId, projectServer.getServerId());
                    }
//                }
            }
        }

        log.info("------------------查询服务器列表结束------------------");

        return upPatchServerList;
    }

    /**
     * 启动tomcat服务
     * @param patchId 补丁Id
     * @param serverId 服务Id
     */
    @Override
    public AjaxResult startServer(Long patchId, Integer serverId) {
        UpProjectServer server = upProjectServerMapper.selectUpProjectServerById(serverId);
        LogUtil.recordUpgradeLog("------------------------------------启动Tomcat服务开始------------------------------", patchId, serverId);
        if (ConstantUtil.ServerStatus.START_OK.getValue().equals(server.getServerStatus())) {
            LogUtil.recordUpgradeLog("------------------Tomcat服务器还未停止,请先停止服务器------------", patchId, serverId);
            return AjaxResult.error("服务器还未停止,请先停止服务器");
        }
        // 升级记录
        UpUpgradeRecordClient upgradeRecordClient = upUpgradeRecordClientMapper.getUpgradeRecordClient(patchId, serverId);
        if (!Objects.isNull(upgradeRecordClient) && ConstantUtil.UpgradeRecordStatus.UPGRADE_FAIL.getValue().equals(upgradeRecordClient.getUpStatus())) {
            // 升级失败不允许启动tomcat,请回退上一个版本，再启动
            LogUtil.recordUpgradeLog("------------------升级失败不能重启服务，请回退，再重新升级------------", patchId, serverId);
            return updateServerStatus(server, patchId, ConstantUtil.ServerStatus.NOT_START.getValue(), ConstantUtil.UpgradeRecordStatus.UPGRADE_FAIL.getValue(), "升级失败不能重启服务，请回退，再重新升级");
        }
        // 未升级不允许启动tomcat
        if (ConstantUtil.UpgradeStatus.NOT_UPGRADE.getValue().equals(upgradeRecordClient.getUpStatus())) {
            LogUtil.recordUpgradeLog("------------------未升级不能重启服务，请先升级------------", patchId, serverId);
            return updateServerStatus(server, patchId, ConstantUtil.ServerStatus.NOT_START.getValue(), ConstantUtil.UpgradeRecordStatus.UPGRADE_FAIL.getValue(), "未升级不能重启服务，请先升级");

        }
        String tomcatPath = server.getTomcatPath();
        if (OSValidator.isUnix()) {
            LogUtil.recordUpgradeLog("服务器系统为：" + server.getOsType(), patchId, serverId);
            // tomcat路径是否存在
//            if(fileExist(tomcatPath)){
                LogUtil.recordUpgradeLog("Tomcat安装路径为：" + server.getTomcatPath(), patchId, serverId);
                // 启动服务器
                String command = "cd " + tomcatPath + File.separator + "bin && sh startup.sh";
                try {
                    LogUtil.recordUpgradeLog("命令执行开始:" + command , patchId, serverId);
                    // 执行远程服务器命令（外网通的情况下）
                    RemoteCommandUtil.remoteCommandUtil(server.getSshUser(), server.getSshPassword(), server.getServerIp(), server.getSshPort(), command);
                    /*String message = CommandUtil.run(command);
                    System.out.println(message);*/
                    LogUtil.recordUpgradeLog("命令执行成功", patchId, serverId);
                } catch (Exception e) {
                    log.info(e.getMessage());
                    LogUtil.recordUpgradeLog("命令执行失败" + command, patchId, serverId);
                    return updateServerStatus(server, patchId, ConstantUtil.ServerStatus.NOT_START.getValue(), ConstantUtil.UpgradeRecordStatus.UPGRADE_FAIL.getValue(), "命令执行失败");
                }
            /*} else {
                LogUtil.recordUpgradeLog("tomcat路径不存在：" + tomcatPath, patchId, serverId);
                return updateServerStatus(server, patchId, ConstantUtil.ServerStatus.NOT_START.getValue(), ConstantUtil.UpgradeStatus.UPGRADE_FAIL.getValue(), "tomcat路径不存在");

            }*/
        } else {
            LogUtil.recordUpgradeLog("目前仅支持Unix系统，坐等后续扩展。", patchId, serverId);
            return updateServerStatus(server, patchId, ConstantUtil.ServerStatus.NOT_START.getValue(), ConstantUtil.UpgradeRecordStatus.UPGRADE_FAIL.getValue(), "目前仅支持Unix系统，坐等后续扩展。");
        }
        LogUtil.recordUpgradeLog("------------------------------------启动Tomcat服务结束------------------------------", patchId, serverId);

        updateServerStatus(server, patchId, ConstantUtil.ServerStatus.START_OK.getValue(), ConstantUtil.UpgradeRecordStatus.UPGRADE_OK.getValue(), null);

        // 设置端补丁包状态,同步修改云端补丁包状态
//        try {
//            updateUpgradeStatus(patchId, serverId);
//        } catch (Exception e) {
//            log.info(e.getMessage());
//            LogUtil.recordUpgradeLog("同步修改云端补丁包状态失败", patchId, serverId);
//            return updateServerStatus(server, patchId, ConstantUtil.ServerStatus.NOT_START.getValue(), ConstantUtil.UpgradeStatus.UPGRADE_FAIL.getValue(), "同步修改云端补丁包状态失败");
//        }

        String message = updateUpgradeStatus(patchId, serverId, server.getServerType());
        if (!StringUtils.isEmpty(message)) {
            log.info(message);
            LogUtil.recordUpgradeLog(message, patchId, serverId);
            updateServerStatus(server, patchId, ConstantUtil.ServerStatus.NOT_START.getValue(), ConstantUtil.UpgradeRecordStatus.UPGRADE_FAIL.getValue(), message);
            return AjaxResult.error(message);
        }

        LogUtil.recordUpgradeLog("------------------^v^ ^v^ ^v^服务器：【" + server.getServerName() + "】，IP：【 " + server.getServerIp() + "】自动升级结束^v^ ^v^ ^v^------------", patchId, serverId);
        return AjaxResult.success();
    }

    /**
     * 记录升级记录
     */
    /*public void recordUpgrade(Long patchId, Integer serverId, UpProjectServer server) {

        // 升级记录表，记录升级记录
        UpPatchClient patchClient = upPatchClientMapper.selectUpPatchClientById(patchId);
        // 查询历史升级记录
        UpUpgradeRecordClient upRecordClient = upUpgradeRecordClientMapper.getUpgradeRecordClient(patchId, serverId);
        // 升级记录
        UpUpgradeRecordClient recordClient = new UpUpgradeRecordClient();
        recordClient.setPatchId(patchId);
        recordClient.setServerId(serverId);
        recordClient.setServerName(server.getServerName());
        recordClient.setUpStatus(ConstantUtil.UpgradeStatus.UPGRADING.getValue());
//        if (!Objects.isNull(patchClient)) {
//            recordClient.setPatchFileName(patchClient.getPatchFileName());
//        } else {
//            LogUtil.recordUpgradeLog("没有此包的信息：patchId-" + patchId + "serverId: " + serverId, patchId, serverId);
//            // 记录日志
//            updateServerStatus(server, patchId, ConstantUtil.ServerStatus.NOT_START.getValue(), ConstantUtil.UpgradeStatus.UPGRADE_FAIL.getValue(), "没有此包的信息");
//        }

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

            LogUtil.recordUpgradeLog("上一次升级记录为: 第【"+ upRecordClient.getUpTimes() +"】次升级, 升级人："+ upRecordClient.getLastUpWorker() +", " +
                    "升级时间为：" + DateUtils.parseDateToStr(DateUtils.YYYY_MM_DD_HH_MM_SS, upRecordClient.getLastUpTime()), patchId, serverId);
        }
    }*/

    /**
     * 停止tomcat服务
     * @param patchId 补丁Id
     * @param serverId 服务Id
     */
    @Override
    public AjaxResult stopServer(Long patchId, Integer serverId) {
        // 为每一次升级生成进程Id,代表一次升级
        // TODO 生成进程Id,从停止开始，认为一次升级流程
        // String processId = IdUtils.fastSimpleUUID();

        // 1.判断是否是unix系统,Tomcat路径是否存在
        // 2.判断目标服务器状态是否已启动
        // 3.根据服务Id查询目标服务器信息然后执行Linux命令
        UpProjectServer server = upProjectServerMapper.selectUpProjectServerById(serverId);

        LogUtil.recordUpgradeLog("------------------^v^ ^v^ ^v^服务器：【" + server.getServerName() + "】，IP：【 " + server.getServerIp() + "】自动升级开始^v^ ^v^ ^v^------------", patchId, serverId);
        LogUtil.recordUpgradeLog("------------------------------------停止Tomcat服务开始------------------------------", patchId, serverId);

        if (ConstantUtil.ServerStatus.NOT_START.getValue().equals(server.getServerStatus())) {
            LogUtil.recordUpgradeLog("Tomcat已经是停止状态", patchId, serverId);
            return AjaxResult.error("Tomcat已经是停止状态");
        }

        String tomcatPath = server.getTomcatPath();
        if (OSValidator.isUnix()) {
            LogUtil.recordUpgradeLog("服务器系统为：" + server.getOsType(), patchId, serverId);
            // tomcat路径是否存在
//            if(fileExist(tomcatPath)){
                LogUtil.recordUpgradeLog("Tomcat安装路径为：" + server.getTomcatPath(), patchId, serverId);
                // 停止服务器
                try {
                    String command = "cd " + tomcatPath + File.separator + "bin && sh shutdown.sh";
                    LogUtil.recordUpgradeLog("命令执行开始:" + command , patchId, serverId);
                    // TODO 记录外网环境还是内网环境
                    // 执行远程服务器命令（外网通的情况下）
                    RemoteCommandUtil.remoteCommandUtil(server.getSshUser(), server.getSshPassword(), server.getServerIp(), server.getSshPort(), command);
                    // 延迟1s,待Tomcat完全停止
                    Thread.sleep(1000);
                    LogUtil.recordUpgradeLog("命令执行成功" , patchId, serverId);
                } catch (Exception e) {
                    log.info(e.getMessage());
                    LogUtil.recordUpgradeLog("命令执行失败", patchId, serverId);
                    return updateServerStatus(server, patchId, ConstantUtil.ServerStatus.NOT_START.getValue(), ConstantUtil.UpgradeRecordStatus.UPGRADE_FAIL.getValue(), "命令执行失败");
                }
//            } else {
//                LogUtil.recordUpgradeLog("tomcat路径不存在：" + tomcatPath, patchId, serverId);
//                return updateServerStatus(server, patchId, ConstantUtil.ServerStatus.NOT_START.getValue(), ConstantUtil.UpgradeStatus.UPGRADE_FAIL.getValue(), "tomcat路径不存在");
//            }
        } else {
            LogUtil.recordUpgradeLog("目前仅支持Unix系统，坐等后续扩展。", patchId, serverId);
            return updateServerStatus(server, patchId, ConstantUtil.ServerStatus.NOT_START.getValue(), ConstantUtil.UpgradeRecordStatus.UPGRADE_FAIL.getValue(), "目前仅支持Unix系统，坐等后续扩展。");
        }

        // 记录升级
//        recordClientService.recordUpgrade(patchId, ConstantUtil.UpgradeStatus.UPGRADING.getValue(), server);

        updateServerStatus(server, patchId, ConstantUtil.ServerStatus.NOT_START.getValue(), ConstantUtil.UpgradeStatus.UPGRADING.getValue(), null);

        // 设置端补丁包状态
        String message = updateUpgradeStatus(patchId, serverId, server.getServerType());
        if (!StringUtils.isEmpty(message)) {
            log.info(message);
            LogUtil.recordUpgradeLog(message, patchId, serverId);
            updateServerStatus(server, patchId, ConstantUtil.ServerStatus.NOT_START.getValue(), ConstantUtil.UpgradeRecordStatus.UPGRADE_FAIL.getValue(), message);
            return AjaxResult.error(message);
        }

        LogUtil.recordUpgradeLog("------------------------------------停止Tomcat服务结束------------------------------", patchId, serverId);
        return AjaxResult.success();
    }

    /**
     * 向目标服务器上传补丁包
     * @param patchId 补丁Id
     * @param serverId 服务Id
     */
    @Override
    public AjaxResult sendPatch(Long patchId, Integer serverId) {

        LogUtil.recordUpgradeLog("------------------------------------上传补丁包文件开始-------------------------------", patchId, serverId);
        // 根据端patchId查询项目和产品id
        UpPatchClient patchClient = upPatchClientMapper.selectUpPatchClientById(patchId);
        UpProjectServer server = upProjectServerMapper.selectUpProjectServerById(serverId);
        String compileList = patchClient.getCompileList();
        // 编译路径为空
        if (StringUtils.isEmpty(compileList)) {
            return updateServerStatus(server, patchId, null, ConstantUtil.UpgradeRecordStatus.UPGRADE_FAIL.getValue(), "编译路径为空，无法完成升级，请联系管理员");
        }
        String serverIp = server.getServerIp();
        String sshUser = server.getSshUser();
        String sshPassword = server.getSshPassword();
        Integer sshPort = server.getSshPort();
        String serverPath = server.getServerPath();
        if (!verifyServerStatus(server)) {
            LogUtil.recordUpgradeLog("------------------Tomcat服务器还未停止------------", patchId, serverId);
            return AjaxResult.error("Tomcat服务器还未停止,请先停止服务器");
        }
        // 上传补丁包文件前备份
        LogUtil.recordUpgradeLog("------------------备份上一个版本HIS开始------------", patchId, serverId);
        // 执行远程服务器命令（外网通的情况下）
        // tar -zcvf /home/xahot.tar.gz /xahot
        String tarPath = server.getServerPath();

        /*if (!fileExist(tarPath)) {
            LogUtil.recordUpgradeLog("服务器路径不存在：" + tarPath, patchId, serverId);
            return updateServerStatus(server, patchId,null, ConstantUtil.UpgradeStatus.UPGRADE_FAIL.getValue(), "服务器路径不存在");
        }*/
        String command = "cd " + tarPath + " && tar -zcvf "+ server.getDeployName() +".tar.gz "+ server.getDeployName();
        try {

            RemoteCommandUtil.remoteCommandUtil(sshUser, sshPassword, serverIp, sshPort, command);
        } catch (Exception e) {
            log.info(e.getMessage());
            LogUtil.recordUpgradeLog("备份命令执行失败：" + command, patchId, serverId);
            return updateServerStatus(server, patchId, null, ConstantUtil.UpgradeRecordStatus.UPGRADE_FAIL.getValue(), "备份命令执行失败");
        }

        LogUtil.recordUpgradeLog("备份上一个版本HIS路径：" + tarPath , patchId, serverId);
        LogUtil.recordUpgradeLog("------------------备份上一个版本HIS结束------------", patchId, serverId);

        String profile = RuoYiConfig.getProfile(); // profile: /opt/kyup/uploadPath
        // 包文件名 PATCH_YUNHIS-26573[HIS_V3][SQLY]_20220331155312.ZIP
        String patchFileName = patchClient.getPatchFileName();
        // 包名  PATCH_YUNHIS-26573[HIS_V3][SQLY]_20220331155312
        String patchName = patchFileName.split(".ZIP")[0];
        // 获取产品名称 HIS
        String productName = patchClient.getProductName();

        // 存放补丁包的路径
        String patchPath = profile + "/" + productName + "/" + patchFileName;
        LogUtil.recordUpgradeLog("补丁包的文件路径：" + patchPath, patchId, serverId);

        // 解压后的文件夹路径
        String unzipPath = profile + "/" + productName + "/" + patchName + "/";
        LogUtil.recordUpgradeLog("解压后的文件夹路径：" + unzipPath, patchId, serverId);

        // 解压包
        LogUtil.recordUpgradeLog("补丁包解压开始【" + patchFileName + "】", patchId, serverId);
        String fileCode = (String) System.getProperties().get("file.encoding");

        boolean unzipFlag = ZipFileUtil.unZipFiles(new File(patchPath), unzipPath, fileCode);
        if (unzipFlag) {
            LogUtil.recordUpgradeLog("补丁包解压成功【" + patchFileName + "】", patchId, serverId);
            // 解压成功，复制文件到目标服务器
            boolean usePassword = true;
            LogUtil.recordUpgradeLog("------------------上传补丁包命令执行开始----------------", patchId, serverId);
            // 获取补丁包解压后的文件路径
            try {
                String[] files = compileList.split(",");
//                SCPClient scpClient = SCPFile.createSCPClient(serverIp, sshPort, sshUser, sshPassword, null, usePassword);
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
//                    LogUtil.recordUpgradeLog("补丁包解压后的文件路径: " + compileFilePath, patchId, serverId);
//                    if (!Objects.isNull(scpClient)) {
                        // 传输文件
                    String remoteTargetPath = serverPath + fileUrl;

                    // 代码列表是新建目录，则先创建目录
                    File remoteFile = new File(remoteTargetPath);
                    if (!remoteFile.exists()) {
                        remoteFile.mkdirs();
                    }
//                        LogUtil.recordUpgradeLog("远程服务器文件路径: " + remoteTargetPath, patchId, serverId);
                        SCPFile.putFile(compileFilePath, remoteTargetPath,serverIp, sshPort, sshUser, sshPassword, null, usePassword);
//                    }
                }
            } catch (Exception e) {
                log.info(e.getMessage());
                LogUtil.recordUpgradeLog("补丁包文件上传失败【" + patchFileName + "】", patchId, serverId);
                return updateServerStatus(server, patchId, null, ConstantUtil.UpgradeRecordStatus.UPGRADE_FAIL.getValue(), "包文件上传失败");
            }
            LogUtil.recordUpgradeLog("------------------上传补丁包命令执行结束----------------", patchId, serverId);
        } else {
            // 解压失败
            LogUtil.recordUpgradeLog("补丁包解压失败【" + patchFileName + "】", patchId, serverId);
            return updateServerStatus(server, patchId, null, ConstantUtil.UpgradeRecordStatus.UPGRADE_FAIL.getValue(), "解压失败");
        }
        updateServerStatus(server, patchId,null, ConstantUtil.UpgradeStatus.UPGRADING.getValue(), null);

        // 设置端补丁包状态
        String message = updateUpgradeStatus(patchId, serverId, server.getServerType());
        if (!StringUtils.isEmpty(message)) {
            log.info(message);
            LogUtil.recordUpgradeLog(message, patchId, serverId);
            updateServerStatus(server, patchId, ConstantUtil.ServerStatus.NOT_START.getValue(), ConstantUtil.UpgradeRecordStatus.UPGRADE_FAIL.getValue(), message);
            return AjaxResult.error(message);
        }

        LogUtil.recordUpgradeLog("------------------------------------上传补丁包文件结束-------------------------------", patchId, serverId);
        return AjaxResult.success();
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
        return upUpgradeLogClientMapper.selectUpUpgradeLogClientList(logClient);
    }

    /** 回退
     * @param patchId 补丁包Id
     * @return serverId 服务Id
     */
    public AjaxResult rollbackPatch(Long patchId, Integer serverId) {

        UpProjectServer server = upProjectServerMapper.selectUpProjectServerById(serverId);
        String serverIp = server.getServerIp();
        String sshUser = server.getSshUser();
        String sshPassword = server.getSshPassword();
        Integer sshPort = server.getSshPort();
        LogUtil.recordUpgradeLog("------------------------------------回退上一个版本HIS开始------------------------------", patchId, serverId);

        if (!verifyServerStatus(server)) {
            LogUtil.recordUpgradeLog("------------------Tomcat服务器还未停止------------", patchId, serverId);
            return AjaxResult.error("Tomcat服务器还未停止,请先停止服务器");
        }
        // 执行远程服务器命令（外网通的情况下）
        // 解压升级之前备份的包
        // tar -zxvf /home/xahot.tar.gz /xahot
        String tarPath = server.getServerPath();

        /*if (!fileExist(tarPath)) {
            LogUtil.recordUpgradeLog("服务器路径不存在：" + tarPath, patchId, serverId);
            return updateServerStatus(server, patchId,null, ConstantUtil.UpgradeStatus.ROLLBACK_FAIL.getValue(), "服务器路径不存在");
        }*/
        String command = "cd " + tarPath + " && tar -zxvf "+ server.getDeployName() +".tar.gz";
        try {

            RemoteCommandUtil.remoteCommandUtil(sshUser, sshPassword, serverIp, sshPort, command);
        } catch (Exception e) {
            log.info(e.getMessage());
            LogUtil.recordUpgradeLog("回退命令执行失败: " + command, patchId, serverId);
            return updateServerStatus(server, patchId, null, ConstantUtil.UpgradeRecordStatus.UPGRADE_FAIL.getValue(), "回退命令执行失败");
        }

        LogUtil.recordUpgradeLog("回退上一个版本HIS路径：" + tarPath , patchId, serverId);

        // 设置端补丁包状态
        String message = updateUpgradeStatus(patchId, serverId, server.getServerType());
        if (!StringUtils.isEmpty(message)) {
            log.info(message);
            LogUtil.recordUpgradeLog(message, patchId, serverId);
            updateServerStatus(server, patchId, ConstantUtil.ServerStatus.NOT_START.getValue(), ConstantUtil.UpgradeRecordStatus.UPGRADE_FAIL.getValue(), message);
            return AjaxResult.error(message);
        }

        LogUtil.recordUpgradeLog("------------------------------------回退上一个版本HIS结束------------------------------", patchId, serverId);
        updateServerStatus(server, patchId, null, ConstantUtil.UpgradeStatus.NOT_UPGRADE.getValue(), null);
        return AjaxResult.success();
    }


    /** 校验服务器是否启动
     * @param server 服务器
     * @return boolean 是否停止
     */
    private boolean verifyServerStatus(UpProjectServer server) {

        return ConstantUtil.ServerStatus.NOT_START.getValue().equals(server.getServerStatus());
    }

    /**
     * 修改服务状态，提示信息
     * @param server 服务
     * @param serverStatus 服务状态
     * @param upgradeStatus 服务状态
     * @param message 提示信息
     * @return AjaxResult
     */
    private AjaxResult updateServerStatus(UpProjectServer server, Long patchId, String serverStatus, String upgradeStatus, String message) {

        if (!StringUtils.isEmpty(serverStatus)) {
            server.setServerStatus(serverStatus);
        }
        upProjectServerMapper.updateUpProjectServer(server);

        UpUpgradeRecordClient recordClient = new UpUpgradeRecordClient();
        recordClient.setUpStatus(upgradeStatus);
        recordClient.setServerId(server.getServerId());
        recordClient.setPatchId(patchId);
        upUpgradeRecordClientMapper.updateUpUpgradeRecordClient(recordClient);

//        LogUtil.recordUpgradeLog("修改服务器状态为: " + serverStatus + ", 修改升级状态为: " + upgradeStatus, patchId, server.getServerId());

        return StringUtils.isEmpty(message) ? AjaxResult.success() : AjaxResult.error(message);
    }

    /**
     * 修改端补丁包状态
     * @param patchId
     * @param serverType
     */
    public String updateUpgradeStatus(Long patchId, Integer serverId, String serverType) {
        UpPatchClient patchClient = upPatchClientMapper.selectUpPatchClientById(patchId);
        // 查询服务器列表
        UpProjectServer server = new UpProjectServer();
        server.setProductId(patchClient.getProductId());
        server.setProjectId(patchClient.getProjectId());
        List<UpProjectServer> upPatchServerList  = upProjectServerMapper.selectUpProjectServerList(server);

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
                patchClient.setPatchStatus(ConstantUtil.UpgradeStatus.UPGRADE_ALL.getValue());
                // 升级成功状态数量<服务器数量 && 升级成功状态数量不为0
            } else if (upSuccessStatusList.size() != 0 && upSuccessStatusList.size() < upPatchServerList.size()) {
                patchClient.setPatchStatus(ConstantUtil.UpgradeStatus.UPGRADE_PART.getValue());
                // 其他都为未升级
            } else {
                patchClient.setPatchStatus(ConstantUtil.UpgradeStatus.NOT_UPGRADE.getValue());
            }
            // 修改补丁包状态
            // upPatchClientMapper.updateUpPatchClientStatus(patchClient.getPatchId(), patchClient.getPatchStatus());
        }
        // 修改端补丁包状态和子包状态
        /*if ("Y".equals(patchClient.getMergePackageFlag())) {
            UpPatchClient client = new UpPatchClient();
            client.setPatchStatus(patchClient.getPatchStatus());
            client.setPatchId(patchClient.getPatchId());
            upPatchClientMapper.updateUpPatchClientChild(client);
        }*/
        SysUser sysUser = ShiroUtils.getSysUser();
        String username = sysUser.getUserName();
        UpPatchClient client = new UpPatchClient();
        client.setPatchStatus(patchClient.getPatchStatus());
        client.setPatchId(patchClient.getPatchId());
        client.setUpdateTime(new Date());
        client.setUpdateBy(username);
        if ("Y".equals(patchClient.getMergePackageFlag())) {
            client.setMergePackageFlag(String.valueOf(patchId));
        }
        //如果是合包并且测试服务器，则发布时不修改补丁包状态
//        if ("Y".equals(patchClient.getMergePackageFlag()) && "测试".equals(serverType)) {
//            return null;
//        }
        upPatchClientMapper.updatePatchAndSubPatchById(client);
        // 同步云端补丁包状态
//        String msg = syncCloudPatchStatus(patchClient);
        return null;
    }

    /**
     * 同步云端补丁包状态
     * @param patchClient 端补丁包
     */
    private String syncCloudPatchStatus(UpPatchClient patchClient) {
        SysUser sysUser = ShiroUtils.getSysUser();
        String username = sysUser.getUserName();
        String patchFileName = patchClient.getPatchFileName();
        String patchStatus = patchClient.getPatchStatus();
        String mergePackageFlag = patchClient.getMergePackageFlag();

        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("patchFileName", patchFileName);
        paramMap.put("patchStatus", patchStatus);
        paramMap.put("mergePackageFlag", mergePackageFlag);
        paramMap.put("updateBy", username);

        // 推送到云端的补丁状态
        Map<String, Object> patchFileMap = feignClient.updateState(paramMap);
        return !"0".equals(patchFileMap.get("code")+"") ? "同步云端补丁包状态失败" : null;
    }

    /**
     * 手动升级
     *
     * @param patchId  补丁包Id
     * @param serverId 服务器Id
     */
    @Transactional
    public AjaxResult handUpgrade(Long patchId, Integer serverId) {
        // 查询up_patch_client中是否有包及其子包的信息，没有，则提示拉取包
        // 修复up_patch_client和up_upgrade_record_client的数据
        // 修复up_upgrade_log_client的数据，此包为手动升级，升级时间，升级人
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
                if (ConstantUtil.UpgradeRecordStatus.UPGRADE_OK.getValue().equals(upgradeRecordClient.getUpStatus())) {
                    return AjaxResult.error("此补丁包已经升级过了，请选择未升级的补丁包");
                }
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
     * 查询数据库信息
     * @return 结果
     */
    @Override
    public List<UpUpgradeDataDTO> getDatabaseList(UpProjectServer server) {
        // 查询每个数据库最后执行的补丁包，执行人员，执行时间
        List<UpUpgradeDataDTO> databaseList = upProjectServerMapper.getDatabaseList(server);
        if (!CollectionUtils.isEmpty(databaseList)) {
            for (UpUpgradeDataDTO upgradeDataVo : databaseList) {
                /*// 数据库最后一次SQL执行成功的包信息：（升级成功，脚本已执行）
                List<UpUpgradeRecordClient> recordClients = upProjectServerMapper.getUpgradeDataByServerId(upgradeDataVo.getServerId());
                if (!CollectionUtils.isEmpty(recordClients)) {
                    UpUpgradeRecordClient client = recordClients.get(0);
                    if (client.getUpdateTime() != null) {
                        upgradeDataVo.setLastUpgradeTime(client.getUpdateTime());
                        upgradeDataVo.setLastUpgradePerson(client.getUpdateBy());
                    } else if (client.getLastUpTime() != null){
                        upgradeDataVo.setLastUpgradeTime(client.getLastUpTime());
                        upgradeDataVo.setLastUpgradePerson(client.getLastUpWorker());
                    } else {
                        upgradeDataVo.setLastUpgradeTime(client.getCreateTime());
                        upgradeDataVo.setLastUpgradePerson(client.getCreateBy());
                    }
                    upgradeDataVo.setLastUpgradePackage(client.getPatchFileName());
                } else {
                    upgradeDataVo.setUpgradeStatus(ConstantUtil.UpgradeStatus.NOT_UPGRADE.getValue());
                }
                */
            //查询该服务器最后升级包、最后升级人员、最后升级时间
            UpUpgradeDataDTO patchDTO = new UpUpgradeDataDTO();
            patchDTO.setUpgradeStatus(ConstantUtil.UpgradeRecordStatus.UPGRADE_OK.getValue());
            patchDTO.setPatchStatusList(Lists.newArrayList(ConstantUtil.UpgradeStatus.UPGRADE_PART.getValue(), ConstantUtil.UpgradeStatus.UPGRADE_ALL.getValue(), ConstantUtil.UpgradeStatus.SQL_EXECUTED.getValue()));
            patchDTO.setTestStatusList(Lists.newArrayList(ConstantUtil.UpgradeStatus.TEST_UPGRADE.getValue(), ConstantUtil.UpgradeStatus.SQL_TESTED.getValue()));
            patchDTO.setServerType(upgradeDataVo.getServerType());
            patchDTO.setProjectId(upgradeDataVo.getProjectId());
            patchDTO.setProductId(upgradeDataVo.getProductId());
            patchDTO.setServerId(upgradeDataVo.getServerId());
            List<UpUpgradeRecordClient> lastUpgradeRecordList = upUpgradeRecordClientMapper.getLastUpgradeRecordList(patchDTO);
            //取出该服务器最后升级的记录
            if (lastUpgradeRecordList.size()>0) {
                UpUpgradeRecordClient lastUpgradeRecord = lastUpgradeRecordList.get(0);
                upgradeDataVo.setLastUpgradePackage(lastUpgradeRecord.getPatchFileName());
                if (lastUpgradeRecord.getUpdateTime() == null) {
                    upgradeDataVo.setLastUpgradePerson(lastUpgradeRecord.getCreateBy());
                    upgradeDataVo.setLastUpgradeTime(lastUpgradeRecord.getCreateTime());
                } else {
                    upgradeDataVo.setLastUpgradePerson(lastUpgradeRecord.getUpdateBy());
                    upgradeDataVo.setLastUpgradeTime(lastUpgradeRecord.getUpdateTime());
                }
            }
            // 修改服务器状态
            upgradeDataVo.setUpdateBy(ShiroUtils.getSysUser().getUserName());
            upgradeDataVo.setUpdateTime(new Date());
            upProjectServerMapper.updateDatabaseServer(upgradeDataVo);
        }
        }
        return databaseList;
    }
}
