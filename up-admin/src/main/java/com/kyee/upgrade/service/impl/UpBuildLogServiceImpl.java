package com.kyee.upgrade.service.impl;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.kyee.upgrade.domain.UpProduct;
import com.kyee.upgrade.domain.UpProjectModule;
import com.kyee.upgrade.domain.UpProjectProduct;
import com.kyee.upgrade.mapper.UpProductMapper;
import com.kyee.upgrade.mapper.UpProjectModuleMapper;
import com.kyee.upgrade.mapper.UpProjectProductMapper;
import com.kyee.upgrade.utils.*;
import com.ruoyi.common.utils.StringUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.logging.log4j.util.PropertySource;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.kyee.upgrade.mapper.UpBuildLogMapper;
import com.kyee.upgrade.domain.UpBuildLog;
import com.kyee.upgrade.service.IUpBuildLogService;
import com.ruoyi.common.core.text.Convert;

/**
 * 构建日志Service业务层处理
 *
 * @author lijunqiang
 * @date 2021-10-20
 */
@Service
public class UpBuildLogServiceImpl implements IUpBuildLogService
{

    private static final Logger log = LoggerFactory.getLogger(UpBuildLogServiceImpl.class);

    @Autowired
    private UpBuildLogMapper upBuildLogMapper;

    /**
     * 查询构建日志
     *
     * @param buildId 构建日志ID
     * @return 构建日志
     */
    @Override
    public UpBuildLog selectUpBuildLogById(Long buildId)
    {
        return upBuildLogMapper.selectUpBuildLogById(buildId);
    }

    /**
     * 查询构建日志列表
     *
     * @param upBuildLog 构建日志
     * @return 构建日志
     */
    @Override
    public List<UpBuildLog> selectUpBuildLogList(UpBuildLog upBuildLog)
    {
        return upBuildLogMapper.selectUpBuildLogList(upBuildLog);
    }

    /**
     * 新增构建日志
     *
     * @param upBuildLog 构建日志
     * @return 结果
     */
    @Override
    public int insertUpBuildLog(UpBuildLog upBuildLog)
    {
        return upBuildLogMapper.insertUpBuildLog(upBuildLog);
    }

    /**
     * 修改构建日志
     *
     * @param upBuildLog 构建日志
     * @return 结果
     */
    @Override
    public int updateUpBuildLog(UpBuildLog upBuildLog)
    {
        return upBuildLogMapper.updateUpBuildLog(upBuildLog);
    }

    /**
     * 删除构建日志对象
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteUpBuildLogByIds(String ids)
    {
        return upBuildLogMapper.deleteUpBuildLogByIds(Convert.toLongArray(ids));
    }

    /**
     * 删除构建日志信息
     *
     * @param buildId 构建日志ID
     * @return 结果
     */
    @Override
    public int deleteUpBuildLogById(Long buildId)
    {
        return upBuildLogMapper.deleteUpBuildLogById(buildId);
    }

    /**
     * 根据补丁ID查询构建日志信息
     *
     * @param patchId 构建日志ID
     * @return List<UpBuildLog> 结果
     */
    @Override
    public List<UpBuildLog> getBuildLogsByPatchId(Long patchId)
    {
        List<UpBuildLog> buildLogs = upBuildLogMapper.getBuildLogsByPatchId(patchId);
        if (CollectionUtils.isEmpty(buildLogs)){
            UpBuildLog upBuildLog = new UpBuildLog();
            upBuildLog.setBuildLog("暂无日志");
            buildLogs.add(upBuildLog);
            return buildLogs;
        }
        // 根据补丁id获取最新的一次编译日志的processId
        String processId = buildLogs.stream()
                                    .sorted(Comparator.comparing(UpBuildLog::getBuildTime).reversed())
                                    .map(UpBuildLog::getProcessId)
                                    .collect(Collectors.toList())
                                    .get(0);

        return buildLogs.stream().filter(s -> processId.equals(s.getProcessId())).collect(Collectors.toList());
    }
}
