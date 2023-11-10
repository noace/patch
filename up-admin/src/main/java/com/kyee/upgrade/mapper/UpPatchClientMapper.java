package com.kyee.upgrade.mapper;

import java.util.List;

import com.kyee.upgrade.domain.UpPatch;
import com.kyee.upgrade.domain.UpPatchClient;
import com.kyee.upgrade.domain.UpProjectServer;
import com.kyee.upgrade.domain.UpUpgradeLogClient;
import com.kyee.upgrade.domain.vo.UpPatchClientExtend;
import com.kyee.upgrade.domain.vo.UpUpgradeDataDTO;
import com.kyee.upgrade.domain.vo.UpUpgradePatchDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * 补丁Mapper接口
 *
 * @author zhanghaoyu
 * @date 2021-10-21
 */
@Mapper
@Repository
public interface UpPatchClientMapper
{
    /**
     * 查询补丁
     *
     * @param patchId 补丁ID
     * @return 补丁
     */
    public UpPatchClient selectUpPatchClientById(Long patchId);
    public UpPatchClient selectUpPatchClientByName(String bugPatchFileName);

    /**
     * 查询补丁列表
     *
     * @param upPatchClient 补丁
     * @return 补丁集合
     */
    public List<UpPatchClient> selectUpPatchClientList(UpPatchClient upPatchClient);
    public List<UpPatchClient> selectUpPatchClient(UpPatchClient upPatchClient);

    /**
     * 查询补丁列表
     *
     * @param upPatchClient 补丁
     * @return 补丁集合
     */
    List<UpPatchClientExtend> getUpPatchClientList(UpPatchClient upPatchClient);

    /**
     * 查询补丁列表个数
     *
     * @param upPatchClient 补丁
     * @return 补丁集合
     */
    int selectUpPatchClientListCount(UpPatchClient upPatchClient);

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
     * 修改补丁状态
     *
     * @param patchId 补丁
     * @return 结果
     */
    int updateUpPatchClientStatus(@Param("patchId") Long patchId, @Param("patchStatus") String patchStatus);

    /**
     * 删除补丁
     *
     * @param patchId 补丁ID
     * @return 结果
     */
    public int deleteUpPatchClientById(Long patchId);

    /**
     * 删除子补丁
     *
     * @param parentPatchId 补丁ID
     * @return 结果
     */
    public int deleteUpPatchClientChildByParentPatchId(String parentPatchId);

    /**
     * 批量删除补丁
     *
     * @param patchIds 需要删除的数据ID
     * @return 结果
     */
    public int deleteUpPatchClientByIds(String[] patchIds);

    List<UpPatchClient> selectUpPatchListByParentPatchId(@Param("parentPatchId") Long parentPatchId);

    /**
     * 获取最新插入数据的ID
     * @return
     */
    public Long getLastInsertId();

    /**
     * 修改子补丁
     *
     * @param upPatchClient 补丁
     * @return 结果
     */
    public int updateUpPatchClientChild(UpPatchClient upPatchClient);

    /**
     * 查询补丁包及其子包
     *
     * @param patchId 补丁ID
     * @return 补丁列表
     */
    List<UpPatchClient> getPatchAndSubPatchById(Long patchId);

    /**
     * 修改补丁包及其子包
     *
     * @param upPatchClient 补丁
     * @return 补丁列表
     */
    void updatePatchAndSubPatchById(UpPatchClient upPatchClient);

    /**
     * 查询补丁列表名称
     *
     * @param upPatchClient 补丁
     * @return 补丁集合
     */
    List<String> selectPatchFileName(UpPatchClient upPatchClient);

    void updateBugFlag(@Param("bugPatchFileName")String bugPatchFileName);

    void updateBugfixPatch(@Param("patchId")Long patchId, @Param("bugPatchFileName")String bugPatchFileName);

    /**
     * 根据子包包名查询补丁列表
     *
     * @param upPatchClient 补丁
     * @return 补丁集合
     */
    List<UpPatchClientExtend> getUpPatchListBySonPackage(UpPatchClient upPatchClient);

    /**
     * 下载时查询未升级且小于当前包编译时间的补丁包列表
     * @param upPatchClient
     * @return
     */
    List<UpPatchClient> getPatchByRepeatValidate(UpPatchClient upPatchClient);

    /**
     * 升级时查询未升级且小于当前包编译时间的补丁包列表
     * @param patchDTO
     * @return
     */
    List<UpPatchClient> getTestUpgradePatch(UpUpgradePatchDTO patchDTO);

    /**
     * 查询补丁包升级记录
     * @param patchDTO
     * @return
     */
    List<UpPatchClient> getUpgradeRecordPatch(UpUpgradePatchDTO patchDTO);

    /**
     * 部分升级的且升级记录状态为空
     * @param patchDTO
     * @return
     */
    List<UpPatchClient> getPartUpgradePatch(UpUpgradePatchDTO patchDTO);
}
