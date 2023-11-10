package com.kyee.upgrade.service;

import java.util.List;
import com.kyee.upgrade.domain.UpCommand;

/**
 * 待执行命令Service接口
 * 
 * @author lijunqiang
 * @date 2021-10-18
 */
public interface IUpCommandService 
{
    /**
     * 查询待执行命令
     * 
     * @param commandId 待执行命令ID
     * @return 待执行命令
     */
    public UpCommand selectUpCommandById(Long commandId);

    /**
     * 查询待执行命令列表
     * 
     * @param upCommand 待执行命令
     * @return 待执行命令集合
     */
    public List<UpCommand> selectUpCommandList(UpCommand upCommand);

    /**
     * 新增待执行命令
     * 
     * @param upCommand 待执行命令
     * @return 结果
     */
    public int insertUpCommand(UpCommand upCommand);

    /**
     * 修改待执行命令
     * 
     * @param upCommand 待执行命令
     * @return 结果
     */
    public int updateUpCommand(UpCommand upCommand);

    /**
     * 批量删除待执行命令
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUpCommandByIds(String ids);

    /**
     * 删除待执行命令信息
     * 
     * @param commandId 待执行命令ID
     * @return 结果
     */
    public int deleteUpCommandById(Long commandId);
}
