package com.kyee.upgrade.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kyee.upgrade.mapper.UpBuildCodeChangesMapper;
import com.kyee.upgrade.domain.UpBuildCodeChanges;
import com.kyee.upgrade.service.IUpBuildCodeChangesService;
import com.ruoyi.common.core.text.Convert;

/**
 * 构建日志Service业务层处理
 * 
 * @author lijunqiang
 * @date 2021-10-21
 */
@Service
public class UpBuildCodeChangesServiceImpl implements IUpBuildCodeChangesService 
{
    @Autowired
    private UpBuildCodeChangesMapper upBuildCodeChangesMapper;

    /**
     * 查询构建日志
     * 
     * @param codeChangeId 构建日志ID
     * @return 构建日志
     */
    @Override
    public UpBuildCodeChanges selectUpBuildCodeChangesById(Long codeChangeId)
    {
        return upBuildCodeChangesMapper.selectUpBuildCodeChangesById(codeChangeId);
    }

    /**
     * 查询构建日志列表
     * 
     * @param upBuildCodeChanges 构建日志
     * @return 构建日志
     */
    @Override
    public List<UpBuildCodeChanges> selectUpBuildCodeChangesList(UpBuildCodeChanges upBuildCodeChanges)
    {
        return upBuildCodeChangesMapper.selectUpBuildCodeChangesList(upBuildCodeChanges);
    }

    /**
     * 新增构建日志
     * 
     * @param upBuildCodeChanges 构建日志
     * @return 结果
     */
    @Override
    public int insertUpBuildCodeChanges(UpBuildCodeChanges upBuildCodeChanges)
    {
        return upBuildCodeChangesMapper.insertUpBuildCodeChanges(upBuildCodeChanges);
    }

    /**
     * 修改构建日志
     * 
     * @param upBuildCodeChanges 构建日志
     * @return 结果
     */
    @Override
    public int updateUpBuildCodeChanges(UpBuildCodeChanges upBuildCodeChanges)
    {
        return upBuildCodeChangesMapper.updateUpBuildCodeChanges(upBuildCodeChanges);
    }

    /**
     * 删除构建日志对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteUpBuildCodeChangesByIds(String ids)
    {
        return upBuildCodeChangesMapper.deleteUpBuildCodeChangesByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除构建日志信息
     * 
     * @param codeChangeId 构建日志ID
     * @return 结果
     */
    @Override
    public int deleteUpBuildCodeChangesById(Long codeChangeId)
    {
        return upBuildCodeChangesMapper.deleteUpBuildCodeChangesById(codeChangeId);
    }
}
