package com.kyee.upgrade.mapper;

import java.util.List;
import com.kyee.upgrade.domain.UpCommand;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import org.apache.ibatis.annotations.Param;

/**
 * 待执行命令Mapper接口
 * 
 * @author lijunqiang
 * @date 2021-10-18
 */
@Mapper
@Repository
public interface UpCommandMapper 
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
     * 查询未删除的执行命令列表，按创建时间排序取第一行
     *
     * @return 待执行命令集合
     */
    public UpCommand selectUpCommandOld();

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
     * 删除待执行命令
     * 
     * @param commandId 待执行命令ID
     * @return 结果
     */
    public int deleteUpCommandById(Long commandId);

    /**
     * 批量删除待执行命令
     * 
     * @param commandIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteUpCommandByIds(String[] commandIds);

    /**
     * 根据补丁包Ids批量删除待执行命令
     *
     * @param patchIds 需要删除的数据ID
     * @param loginName 用户名
     * @return 结果
     */
    int deleteUpCommandByPatchIdsAndLoginName(@Param("patchIds") String[] patchIds, @Param("loginName") String loginName);
}
