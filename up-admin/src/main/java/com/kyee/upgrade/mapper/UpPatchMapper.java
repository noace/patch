package com.kyee.upgrade.mapper;

import java.util.List;
import java.util.Map;

import com.kyee.upgrade.domain.UpPatch;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 补丁包管理Mapper接口
 *
 * @author lijunqiang
 * @date 2021-06-12
 */
@Mapper
@Repository
public interface UpPatchMapper
{
    /**
     * 查询补丁包管理
     *
     * @param patchId 补丁包管理ID
     * @return 补丁包管理
     */
    public UpPatch selectUpPatchById(Long patchId);

    /**
     * 查询补丁包管理列表
     *
     * @param upPatch 补丁包管理
     * @return 补丁包管理集合
     */
    public List<UpPatch> selectUpPatchList(UpPatch upPatch);
    /**
     * 查询补丁包管理列表
     *
     * @return 补丁包管理集合
     */
    public List<UpPatch> selectUpPatchChildList(@Param("upPatches")List<UpPatch> upPatches);

    /**
     * 查询补丁包管理列表(自自定义排序)
     *
     * @param upPatch 补丁包管理
     * @return 补丁包管理集合
     */
    public List<UpPatch> getUpPatchList(UpPatch upPatch);

    /**
     * 新增补丁包管理
     *
     * @param upPatch 补丁包管理
     * @return 结果
     */
    public int insertUpPatch(UpPatch upPatch);

    /**
     * 修改补丁包管理
     *
     * @param upPatch 补丁包管理
     * @return 结果
     */
    public int updateUpPatch(UpPatch upPatch);

    /**
     * 修改补丁包管理
     *
     * @param upPatch 补丁包管理
     * @return 结果
     */
    public int updateUpPatchByPatchFileName(UpPatch upPatch);

    /**
     * 删除补丁包管理
     *
     * @param patchId 补丁包管理ID
     * @return 结果
     */
    public int deleteUpPatchById(Long patchId);

    /**
     * 批量删除补丁包管理
     *
     * @param patchIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteUpPatchByIds(String[] patchIds);

    /**
     * 批量删除补丁包管理
     *
     * @param patchIds 需要删除的数据ID
     * @param loginName 用户名
     * @return 结果
     */
     int deleteUpPatchByIdsAndLoginName(@Param("patchIds") String[] patchIds, @Param("loginName") String loginName);

    /**updateUpPatchClientChild
     * 获取最新插入数据的ID
     * @return
     */
    public Long getLastInsertId();

    List<UpPatch> selectUpPatchListByIds(@Param("idList") List<String> idList);

    List<UpPatch> selectUpPatchListByParentPatchId(@Param("parentPatchId") Long parentPatchId);

    /**
     * 根据包名查询子包列表PatchId
     * @param patchFileName
     * @return
     */
    List<Long> selectSubUpPatchIdListByPatchFileName(@Param("patchFileName") String patchFileName, @Param("projectId") Integer projectId);

    /**
     * 批量更新
     * @param patchStatus
     * @param patchIdList
     */
    void batchUpdatePatch(@Param("patchStatus") String patchStatus, @Param("patchIdList") List<Long> patchIdList);

    /**
     * 修改子补丁
     *
     * @param upPatch 补丁
     * @return 结果
     */
    int updateUpPatchClientChild(UpPatch upPatch);

    /**
     * 查询补丁包管理列表数量
     *
     * @param upPatch 补丁包管理
     * @return 补丁包管理集合
     */
    int getCountByStatus(@Param("upPatch") UpPatch upPatch, @Param("deptId") String deptId);

    /**
     * 按姓名统计补丁包Map
     *
     * @param upPatch 补丁包管理
     * @return Map
     */
    List<Map<String, Integer>> getCountByUsername(@Param("upPatch") UpPatch upPatch, @Param("deptId") String deptId);

    /**
     * 查询通知用户userId
     *
     * @param patchId 补丁包管理ID
     * @return
     */
    Map<String,String> getMessageInfo(Long patchId);

    /**
     * 查询打包失败的补丁包
     *
     * @param statusList 补丁包状态
     * @return
     */
    List<Map<String, String>> getPatchWithFailAndBug(@Param("statusList") List<String> statusList, @Param("deptId") String deptId);

    /**
     * 根据任务号查询补丁包
     * @param jiraNo
     * @param productId
     * @param projectId
     * @return
     */
    List<Map> searchPatchs(@Param("jiraNo") String jiraNo, @Param("demandNo") String demandNo, @Param("productId") Integer productId, @Param("projectId") Integer projectId);

    /**
     * 设置加急包状态
     * @param ids
     * @param status
     */
    void setExpeditedStatus(@Param("idList") List<String> ids, @Param("status")String status);

    Map<String, String> getProductChargeName(@Param("projectId") Integer projectId,@Param("productId") Integer productId);

    Map<String, String> getBugTaskName(@Param("bugfixPatch") String bugfixPatch);

    String getChargeJobNo(@Param("id") String id);

    List<UpPatch> getUpPatchListBySonPackage(UpPatch upPatch);

    /**
     * 查询已打包的包中不包含某些包的列表
     * @param patch
     * @param patchIds
     * @return
     */
    List<UpPatch> getPatchListWithoutPatchId(@Param("patch") UpPatch patch, @Param("patchIds") List<String> patchIds);
}
