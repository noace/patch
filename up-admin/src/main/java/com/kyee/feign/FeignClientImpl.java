package com.kyee.feign;

import com.kyee.upgrade.domain.UpUpgradeRecordClient;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 跨服务器接口调用失败后执行
 *
 * @author zhanghaoyu
 * @date 2021-10-21
 */
@Component
public class FeignClientImpl implements IFeignClient{
    /**
     * 获取云服务器发布补丁列表失败后执行
     * @return resultMap
     */
    @Override
    public Map<String, Object> getPatchFileList(HashMap paramMap) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 404);
        resultMap.put("msg", "云服务器已关闭，无法获取发布补丁包列表！");
        return resultMap;
    }

    /**
     * 根据id获取补丁信息
     * @return resultMap
     */
    @Override
    public Map<String, Object> getUpPatchByPatchId(HashMap paramMap) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 404);
        resultMap.put("msg", "云服务器已关闭，获取云服务器发布补丁包失败！");
        return resultMap;
    }

    /**
     * 合并云服务器发布补丁包
     * @return resultMap
     */
    @Override
    public Map<String, Object> mergeDownlod(HashMap paramMap) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 404);
        resultMap.put("msg", "云服务器已关闭，无法合并云服务器发布补丁包！");
        return resultMap;
    }

    /**
     * 获取云服务器子包列表失败后执行
     * @return resultMap
     */
    @Override
    public Map<String, Object> getPatchChildList(HashMap paramMap) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 404);
        resultMap.put("msg", "云服务器已关闭，无法获取补丁包列表！");
        return resultMap;
    }

    /**
     * 取消合并失败执行
     * @return resultMap
     */
    @Override
    public Map<String, Object> cancleMerge(HashMap paramMap) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 404);
        resultMap.put("msg", "云服务器已关闭，无法取消合并！");
        return resultMap;
    }

    /**
     * 推送到云端的补丁状态
     * @return resultMap
     */
    @Override
    public Map<String, Object> updateState(HashMap paramMap) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 404);
        resultMap.put("msg", "云服务器已关闭，无法将升级结果推送到云端！");
        return resultMap;
    }

    /**
     * 导入云服务器升级记录
     * @return
     */
    @Override
    public Map<String, Object> importUpgradeRecord(ArrayList<UpUpgradeRecordClient> dataList) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 404);
        resultMap.put("msg", "云服务器已关闭，无法导入升级记录！");
        return resultMap;
    }

    /**
     * 查询服务器列表失败
     * @return resultMap
     */
    @Override
    public Map<String, Object> getUpgradeServers(HashMap paramMap) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 404);
        resultMap.put("msg", "云服务器已关闭，无法查询服务器列表！");
        return resultMap;
    }
    
    /**
     * 合包失败重新打包
     * @return resultMap
     */
    @Override
    public Map<String, Object> rePatch(HashMap paramMap) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 404);
        resultMap.put("msg", "云服务器已关闭，无法重新打包！");
        return resultMap;
    }

    @Override
    public Map<String, Object> getProductAndProject() {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 404);
        resultMap.put("msg", "获取产品和项目列表失败！");
        return resultMap;
    }

    /**
     * 拉取时利用任务合并时间解决跨包问题
     * @return
     */
    @Override
    public Map<String, Object> pullValidatePatch(HashMap paramMap) {
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("code", 404);
        resultMap.put("msg", "拉取时校验补丁包是否跨包失败！");
        return resultMap;
    }
}
