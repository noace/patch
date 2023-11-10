package com.kyee.upgrade.service;

import java.util.List;
import com.kyee.upgrade.domain.UpProjectPerson;

/**
 * 项目人员Service接口
 * 
 * @author lijunqiang
 * @date 2022-03-16
 */
public interface IUpProjectPersonService 
{
    /**
     * 查询项目人员
     * 
     * @param projectProductId 项目人员ID
     * @return 项目人员
     */
    public List<UpProjectPerson> selectUpProjectPersonById(Integer projectProductId);

    /**
     * 查询项目人员
     *
     * @param userId 项目人员ID
     * @return 项目人员
     */
    public List<UpProjectPerson> selectUpProjectPersonByUserId(Long userId);

    /**
     * 查询项目人员列表
     * 
     * @param upProjectPerson 项目人员
     * @return 项目人员集合
     */
    public List<UpProjectPerson> selectUpProjectPersonList(UpProjectPerson upProjectPerson);

    /**
     * 新增项目人员
     * 
     * @param upProjectPerson 项目人员
     * @return 结果
     */
    public int insertUpProjectPerson(UpProjectPerson upProjectPerson);

    /**
     * 修改项目人员
     * 
     * @param upProjectPerson 项目人员
     * @return 结果
     */
    public int updateUpProjectPerson(UpProjectPerson upProjectPerson);

    /**
     * 批量删除项目人员
     * 
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUpProjectPersonByIds(String ids);

    /**
     * 删除项目人员信息
     * 
     * @param projectProductId 项目人员ID
     * @return 结果
     */
    public int deleteUpProjectPersonById(Integer projectProductId);
}
