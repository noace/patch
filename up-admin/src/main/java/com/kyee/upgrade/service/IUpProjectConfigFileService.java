package com.kyee.upgrade.service;

import java.util.List;
import com.kyee.upgrade.domain.UpProjectConfigFile;

/**
 * 项目产品Service接口
 * 
 * @author lijunqiang
 * @date 2022-06-25
 */
public interface IUpProjectConfigFileService 
{
    /**
     * 查询项目产品
     * 
     * @param projectProductId 项目产品ID
     * @return 项目产品
     */
    public UpProjectConfigFile selectUpProjectConfigFileById(Integer projectProductId);

    /**
     * 查询项目产品列表
     * 
     * @param upProjectConfigFile 项目产品
     * @return 项目产品集合
     */
    public List<UpProjectConfigFile> selectUpProjectConfigFileList(UpProjectConfigFile upProjectConfigFile);

    /**
     * 新增项目产品
     * 
     * @param upProjectConfigFile 项目产品
     * @return 结果
     */
    public int insertUpProjectConfigFile(UpProjectConfigFile upProjectConfigFile);

    /**
     * 修改项目产品
     * 
     * @param upProjectConfigFile 项目产品
     * @return 结果
     */
    public int updateUpProjectConfigFile(UpProjectConfigFile upProjectConfigFile);

    /**
     * 批量删除项目产品
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUpProjectConfigFileByIds(String ids);

    /**
     * 删除项目产品信息
     * 
     * @param projectProductId 项目产品ID
     * @return 结果
     */
    public int deleteUpProjectConfigFileById(Integer projectProductId);
}
