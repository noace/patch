package com.kyee.upgrade.mapper;

import java.util.List;
import java.util.Map;

import com.kyee.upgrade.domain.UpPatchCodelist;
import com.kyee.upgrade.domain.vo.UpPatchCodeListPo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

/**
 * 补丁包代码列Mapper接口
 *
 * @author lijunqiang
 * @date 2021-09-23
 */
@Mapper
@Repository
public interface UpPatchCodelistMapper
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
     * 修改补丁包代码列
     *
     * @param upPatchCodelist 补丁包代码列
     * @return 结果
     */
    int updateUpPatchCodelistByCodeId(UpPatchCodelist upPatchCodelist);

    /**
     * 删除补丁包代码列
     *
     * @param codeId 补丁包代码列ID
     * @return 结果
     */
    public int deleteUpPatchCodelistById(Long codeId);

    /**
     * 批量删除补丁包代码列
     *
     * @param codeIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteUpPatchCodelistByIds(String[] codeIds);

    /**
     * 根据补丁Ids删除补丁包代码列
     * @param patchIds 补丁ids
     * @param loginName 用户名
     * @return 结果
     */
    int deleteUpPatchCodelistByPatchIds(@Param("patchIds") String[] patchIds, @Param("loginName") String loginName);

    int deleteUpPatchCodelistByPathId(@Param("patchId") Long patchId);

    /**
     * 根据补丁ID查询代码列表集合
     * @param patchIds
     * @return
     */
    List<UpPatchCodelist> selectUpPatchCodeListByPatchIds(@Param("patchIds") List<Long> patchIds);

    /**
     * 根据补丁ID查询代码列表集合
     * @param patchId
     * @return
     */
    List<UpPatchCodelist> selectCodeListByPatchId(@Param("patchId") Long patchId);

    /**
     * 根据补丁ID查询子包代码列表
     * @param patchIds
     * @return
     */
    List<Map<String,List<String>>> getSubByPatchIds(@Param("patchIds") List<Long> patchIds);


    /**
     * 包发布时查询代码列表
     * @param patchIds
     * @param productId
     * @param projectId
     * @param patchStatus
     * @return
     */
    List<UpPatchCodeListPo> getCodePathWithPublish(@Param("patchIds") List<Long> patchIds, @Param("productId") Integer productId,
                                                   @Param("projectId") Integer projectId, @Param("patchStatus") String patchStatus);
}
