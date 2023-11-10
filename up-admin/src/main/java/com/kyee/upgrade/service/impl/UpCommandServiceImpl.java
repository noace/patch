package com.kyee.upgrade.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.kyee.upgrade.mapper.UpCommandMapper;
import com.kyee.upgrade.domain.UpCommand;
import com.kyee.upgrade.service.IUpCommandService;
import com.ruoyi.common.core.text.Convert;

/**
 * 待执行命令Service业务层处理
 * 
 * @author lijunqiang
 * @date 2021-10-18
 */
@Service
public class UpCommandServiceImpl implements IUpCommandService 
{
    @Autowired
    private UpCommandMapper upCommandMapper;

    /**
     * 查询待执行命令
     * 
     * @param commandId 待执行命令ID
     * @return 待执行命令
     */
    @Override
    public UpCommand selectUpCommandById(Long commandId)
    {
        return upCommandMapper.selectUpCommandById(commandId);
    }

    /**
     * 查询待执行命令列表
     * 
     * @param upCommand 待执行命令
     * @return 待执行命令
     */
    @Override
    public List<UpCommand> selectUpCommandList(UpCommand upCommand)
    {
        return upCommandMapper.selectUpCommandList(upCommand);
    }

    /**
     * 新增待执行命令
     * 
     * @param upCommand 待执行命令
     * @return 结果
     */
    @Override
    public int insertUpCommand(UpCommand upCommand)
    {
        upCommand.setCreateTime(DateUtils.getNowDate());
        return upCommandMapper.insertUpCommand(upCommand);
    }

    /**
     * 修改待执行命令
     * 
     * @param upCommand 待执行命令
     * @return 结果
     */
    @Override
    public int updateUpCommand(UpCommand upCommand)
    {
        return upCommandMapper.updateUpCommand(upCommand);
    }

    /**
     * 删除待执行命令对象
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    @Override
    public int deleteUpCommandByIds(String ids)
    {
        return upCommandMapper.deleteUpCommandByIds(Convert.toStrArray(ids));
    }

    /**
     * 删除待执行命令信息
     * 
     * @param commandId 待执行命令ID
     * @return 结果
     */
    @Override
    public int deleteUpCommandById(Long commandId)
    {
        return upCommandMapper.deleteUpCommandById(commandId);
    }
}
