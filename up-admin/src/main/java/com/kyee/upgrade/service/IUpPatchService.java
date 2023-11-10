package com.kyee.upgrade.service;

import java.util.List;
import java.util.Map;

import com.kyee.upgrade.domain.UpPatch;
import com.ruoyi.common.core.domain.AjaxResult;

/**
 * 补丁包管理Service接口
 *
 * @author lijunqiang
 * @date 2021-06-12
 */
public interface IUpPatchService
{
    /**
     * 查询补丁包管理
     *
     * @param patchId 补丁包管理ID
     * @return 补丁包管理
     */
    UpPatch selectUpPatchById(Long patchId);

    /**
     * 查询补丁包管理列表
     *
     * @param upPatch 补丁包管理
     * @return 补丁包管理集合
     */
    List<UpPatch> selectUpPatchList(UpPatch upPatch);
    /**
     * 查询补丁包管理列表
     *
     * @return 补丁包管理集合
     */
    List<UpPatch> selectUpPatchChildList(List<UpPatch> upPatches);

    /**
     * 查询补丁包管理列表
     *
     * @param upPatch 补丁包管理
     * @return 补丁包管理集合
     */
    List<UpPatch> getUpPatchList(UpPatch upPatch);

    /**
     * 登记补丁包管理
     *
     * @param upPatch 登记信息
     * @return 结果
     */
    String registPatch(UpPatch upPatch) throws Exception;

    /**
     * 新增补丁包管理
     *
     * @param upPatch 补丁包管理
     * @return 结果
     */
    int insertUpPatch(UpPatch upPatch);

    /**
     * 修改补丁包管理
     *
     * @param upPatch 补丁包管理
     * @return 结果
     */
    int updateUpPatch(UpPatch upPatch) throws Exception;

    /**
     * 补丁合并
     *
     * @return 结果
     */
    AjaxResult mergeUpPatch(String ids, String updateBy) throws Exception;

    /**
     * 修改补丁包管理（不用会话信息）
     *
     * @param upPatch 补丁包管理
     * @return 结果
     */
    int updateUpPatchNoSession(UpPatch upPatch);

    /**
     * 修改补丁包管理
     *
     * @param upPatch 补丁包管理
     * @return 结果
     */
    int updateUpPatchByPatchFileName(UpPatch upPatch);

    /**
     * 批量删除补丁包管理
     *
     * @param ids 需要删除的数据ID
     * @return 结果
     */
    int deleteUpPatchByIds(String ids);

    /**
     * 删除补丁包管理信息
     *
     * @param patchId 补丁包管理ID
     * @return 结果
     */
    int deleteUpPatchById(Long patchId);

    /**
     * 批量发布补丁包
     * @param ids
     * @return
     */
    String publish(String ids) throws Exception;

    List<UpPatch> selectUpPatchListByIds(String ids);

    List<UpPatch> selectUpPatchListByParentPatchId(Long parentPatchId);

    List<UpPatch> selectUpPatchListByPatchFileName(String patchFileName);

    /**
     * 修改子补丁
     *
     * @param upPatch 补丁
     * @return 结果
     */
    int updateUpPatchClientChild(UpPatch upPatch);

    /**
     * 发现BUG
     * @param patchId
     * @return
     */
    void findBug(Long patchId) throws Exception;

    /**
     * 修复BUG
     * @param upPatch
     * @return
     */
    void repairPatch(UpPatch upPatch) throws Exception;


    /**
     * 获取补丁包登记参数
     *
     * @return 结果
     */
    Map<String, Object> getRegistPatchParams();


    /**
     * 查询打包分支
     * @param productId
     * @param projectId
     * @return
     */
    String searchPatchBranch(Integer productId, Integer projectId);

    /**
     * 根据提交序号查询代码列表
     *
     * @param commitId 提交id
     * @param productId 产品id
     * @param projectId 项目id
     * @return List<UpBuildLog> 结果
     */
    Map<String, Object> searchCode(String commitId, Integer productId, Integer projectId) throws Exception;

    /**
     * 根据任务号或需求号查询补丁包
     * @param jiraNo
     * @param demandNo
     * @return
     */
    List<Map> searchPatchs(String jiraNo, String demandNo, Integer productId, Integer projectId);

    /**
     * 设置加急状态
     * @param ids
     * @param status
     */
    void setExpeditedStatus(String ids, String status);

    /**
     * 已发布未升级的包则通知负责人
     * @param upPatchStatus
     * @param projectIds
     * @param productIds
     * @param jiraNos
     * @param topics
     * @param productNames
     * @param developer
     * @param patchFileName
     */
    void sendExpeditedUpgradeMassage(String upPatchStatus, String projectIds, String productIds, String jiraNos, String topics, String productNames, String projectNames, String developer, String patchFileName);

    /**
     * 根据提交序号查询代码列表
     *
     * @param commitId 提交id
     * @param productId 产品id
     * @param projectId 项目id
     * @return List<UpBuildLog> 结果
     */
    Map<String, Object> searchCodeByHisScheduled(String commitId, Integer productId, Integer projectId) throws Exception;
}
