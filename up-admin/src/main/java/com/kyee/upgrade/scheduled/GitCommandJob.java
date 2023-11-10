package com.kyee.upgrade.scheduled;

import com.alibaba.fastjson.JSON;
import com.kyee.upgrade.domain.*;
import com.kyee.upgrade.mapper.*;
import com.kyee.upgrade.scheduled.job.StructureProjectJob;
import com.kyee.upgrade.utils.ConstantUtil;
import com.ruoyi.common.config.RuoYiConfig;
import com.ruoyi.common.constant.Constants;
import com.ruoyi.common.utils.StringUtils;
import com.ruoyi.common.utils.uuid.IdUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 异步构建后端，实现从数据库定时读取待执行命令，判断获取workspace
 *
 * @author zhanghaoyu
 * @date 2021-09-23
 */
@Component
@RabbitListener(queues = "hello")
public class GitCommandJob {

    @Autowired
    private UpProductMapper upProductMapper;

    @Autowired
    private UpProjectProductMapper upProjectProductMapper;

    @Autowired
    private UpWorkspaceSourceMapper upWorkspaceSourceMapper;

    @Autowired
    private StructureProjectJob structureProjectJob;

    private static final Logger log = LoggerFactory.getLogger(GitCommandJob.class);

    @RabbitHandler
    public void executeExt(UpCommand upCommand) {

        String processId = IdUtils.fastSimpleUUID();

        // 读取空闲工作空间
        UpWorkspaceSource upWorkspaceSource = upWorkspaceSourceMapper.selectUpWorkspaceSourceOld(upCommand.getProductId());

        if (null != upWorkspaceSource) {
            Long sourceId = upWorkspaceSource.getSourceId();
            log.info("读取工作空间！id：【" + sourceId + "】");

            // 占用当前工作空间
            upWorkspaceSource.setSourceState(ConstantUtil.WorkSpaceSouceState.WORKING.getValue());
            upWorkspaceSource.setUpdateTime(new Date());
            upWorkspaceSourceMapper.updateUpWorkspaceSource(upWorkspaceSource);
            log.info("占用工作空间！id：【" + sourceId + "】");
        } else {
            log.info("无空闲工作空间，任务结束！");
        }
        try {
            // 组装workSpace绝对路径
            String sourceUrl = upWorkspaceSource.getSourceUrl();
            sourceUrl = RuoYiConfig.getWorkspacePath() + StringUtils.substringAfter(sourceUrl, Constants.RESOURCE_PREFIX);

            // 获取产品仓库地址
            UpProduct upProduct = upProductMapper.selectUpProductById(upCommand.getProductId());
            String repository = upProduct.getProductRepository();
            // 获取项目代码分支
            UpProjectProduct upProjectProduct = new UpProjectProduct();
            upProjectProduct.setProductId(upCommand.getProductId());
            upProjectProduct.setProjectId(upCommand.getProjectId());

            String branch = "";
            List<UpProjectProduct> upProjectProducts = upProjectProductMapper.selectUpProjectProductList(upProjectProduct);
            if (!CollectionUtils.isEmpty(upProjectProducts)) {
                branch = upProjectProducts.get(0).getSourceBranchName();
            }

            // 组装执行参数
            Map<String, Object> paramMap = new HashMap<>();
            paramMap.put("repository", repository);
            paramMap.put("sourceUrl", sourceUrl);
            paramMap.put("branch", branch);
            paramMap.put("patchId", upCommand.getPatchId());
            paramMap.put("commandInfo", upCommand.getCommandInfo());
            paramMap.put("processId", processId);
            paramMap.put("commandType", upCommand.getCommandType());
            paramMap.put("productId", upCommand.getProductId());

            // 执行命令
            log.info("命令开始执行，入参：【" + JSON.toJSONString(paramMap) + "】");
            structureProjectJob.executeCommand(paramMap);
            log.info("命令执行成功，任务结束！processId：【" + processId + "】");
        } catch (Throwable e) {
            e.printStackTrace();
            log.error("命令执行异常，任务结束！processId：【" + processId + "】", e.toString());
        } finally {
            // 解除占用的workSpace
            upWorkspaceSource.setSourceState(ConstantUtil.WorkSpaceSouceState.IDLE.getValue());
            upWorkspaceSourceMapper.updateUpWorkspaceSource(upWorkspaceSource);
            log.info("释放工作空间！");
        }
    }
}
