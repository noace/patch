package com.kyee.upgrade.service;

import com.kyee.upgrade.domain.UpProjectModule;

import java.util.List;

/**
 * 模块名Service接口
 * 
 * @author lijunqiang
 * @date 2022-06-25
 */
public interface IUpProjectModuleService
{
    /**
     * 查询模块名
     * 
     * @param projectProductId 模块名ID
     * @return 模块名
     */
    public UpProjectModule selectUpProjectModuleById(Integer projectProductId);

    /**
     * 查询模块名列表
     * 
     * @param upProjectConfigFile 模块名
     * @return 模块名集合
     */
    public List<UpProjectModule> selectUpProjectModuleList(UpProjectModule upProjectConfigFile);

    /**
     * 新增模块名
     * 
     * @param upProjectConfigFile 模块名
     * @return 结果
     */
    public int insertUpProjectModule(UpProjectModule upProjectConfigFile) throws Exception;

    /**
     * 修改模块名
     * 
     * @param upProjectConfigFile 模块名
     * @return 结果
     */
    public int updateUpProjectModule(UpProjectModule upProjectConfigFile) throws Exception;

    /**
     * 批量删除模块名
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUpProjectModuleByIds(String ids);

    /**
     * 删除模块名信息
     * 
     * @param projectProductId 模块名ID
     * @return 结果
     */
    public int deleteUpProjectModuleById(Integer projectProductId);
}
