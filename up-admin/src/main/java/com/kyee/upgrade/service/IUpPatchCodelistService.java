package com.kyee.upgrade.service;

import java.util.List;

import com.kyee.upgrade.domain.UpPatch;
import com.kyee.upgrade.domain.UpPatchCodelist;

/**
 * 补丁包代码列Service接口
 *
 * @author lijunqiang
 * @date 2021-09-23
 */
public interface IUpPatchCodelistService
{
    /**
     * 查询补丁包代码列
     *
     * @param codeId 补丁包代码列ID
     * @return 补丁包代码列
     */
    public UpPatchCodelist selectUpPatchCodelistById(Long codeId);

    /**
     * 查询补丁包代码列列表
     *
     * @param upPatchCodelist 补丁包代码列
     * @return 补丁包代码列集合
     */
    public List<UpPatchCodelist> selectUpPatchCodelistList(UpPatchCodelist upPatchCodelist);

    /**
     * 新增补丁包代码列
     *
     * @param upPatchCodelist 补丁包代码列
     * @return 结果
     */
    public int insertUpPatchCodelist(UpPatchCodelist upPatchCodelist);

    /**
     * 修改补丁包代码列
     *
     * @param upPatchCodelist 补丁包代码列
     * @return 结果
     */
    public int updateUpPatchCodelist(UpPatchCodelist upPatchCodelist);

    /**
     * 批量删除补丁包代码列
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUpPatchCodelistByIds(String ids);

    /**
     * 删除补丁包代码列信息
     *
     * @param codeId 补丁包代码列ID
     * @return 结果
     */
    public int deleteUpPatchCodelistById(Long codeId);

    /**
     *
     * @param patchIds
     * @return
     */
    List<UpPatchCodelist> selectUpPatchCodeListByPatchIds(List<Long> patchIds);

    /**
     *根据补丁信息插入补丁code列表
     * @param upPatch
     * @return
     */
    void insertUpBuildCodePathByUpPatch(UpPatch upPatch);
}
