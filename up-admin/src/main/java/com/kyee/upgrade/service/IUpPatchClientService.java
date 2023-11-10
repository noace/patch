package com.kyee.upgrade.service;

import java.util.List;

import com.kyee.upgrade.domain.UpPatch;
import com.kyee.upgrade.domain.UpPatchClient;
import com.kyee.upgrade.domain.vo.UpPatchClientExtend;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * 补丁Service接口
 *
 * @author zhanghaoyu
 * @date 2021-10-21
 */
public interface IUpPatchClientService
{
    /**
     * 查询补丁
     *
     * @param patchId 补丁ID
     * @return 补丁
     */
    public UpPatchClient selectUpPatchClientById(Long patchId);

    /**
     * 查询补丁列表
     *
     * @param upPatchClient 补丁
     * @return 补丁集合
     */
    public List<UpPatchClient> selectUpPatchClientList(UpPatchClient upPatchClient);

    /**
     * 查询补丁列表
     *
     * @param upPatchClient 补丁
     * @return 补丁集合
     */
    List<UpPatchClientExtend> getUpPatchClientList(UpPatchClient upPatchClient);

    /**
     * 新增补丁
     *
     * @param upPatchClient 补丁
     * @return 结果
     */
    public int insertUpPatchClient(UpPatchClient upPatchClient);

    /**
     * 修改补丁
     *
     * @param upPatchClient 补丁
     * @return 结果
     */
    public int updateUpPatchClient(UpPatchClient upPatchClient);

    /**
     * 批量删除补丁
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    public int deleteUpPatchClientByIds(String ids);

    /**
     * 删除补丁信息
     *
     * @param patchId 补丁ID
     * @return 结果
     */
    public int deleteUpPatchClientById(Long patchId);

    /**
     * 下载升级文件
     * @param sourceUrl
     * @param uploadUrl
     * @return
     * @throws Exception
     */
    public String downloadPatchFile(String sourceUrl, String uploadUrl, String serverUrl) throws Exception;

    /**
     * 根据patchFileName判断新增或插入
     * @param upPatchClient
     * @return
     * @throws Exception
     */
    public Long addORUpdate(UpPatchClient upPatchClient, String updateBy) throws Exception;

    public List<UpPatchClient> selectUpPatchListByParentPatchId(Long parentPatchId);

    /**
     * 修改子补丁
     *
     * @param upPatchClient 补丁
     * @return 结果
     */
    public int updateUpPatchClientChild(UpPatchClient upPatchClient);

    /**
     * 补丁回退
     *
     * @return 结果
     */
    public AjaxResult tempUpgrade(Long patchId, String patchFileName, String mergePackageFlag, String isUpgrade);

    /**
     * 补丁包升级
     *
     * @return 结果
     */
    public AjaxResult upgradePatch(Long patchId, String patchFileName, String mergePackageFlag, String isUpgrade, String patchFileUrl) throws Exception;

    /**
     * 查询补丁列表名称
     *
     * @param upPatchClient 补丁
     * @return 补丁集合
     */
    List<String> selectPatchFileName(UpPatchClient upPatchClient);

    /**
     * 拉取补丁包
     *
     * @param patchId
     */
    AjaxResult downloadPatchFile(String patchId);

    /**
     * 升级时校验重复文件
     * @param patchId
     * @return
     */
    AjaxResult validateUpgrade(Long patchId);

}
