package com.kyee.feign;

import com.kyee.upgrade.domain.UpUpgradeRecordClient;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 跨服务器接口调用客户端
 *
 * @author zhanghaoyu
 * @date 2021-10-21
 */
@FeignClient(url = "${ruoyi.yunServerUrl}",name = "yunServerUrl",fallback = FeignClientImpl.class)
public interface IFeignClient {

    /**
     * 获取云服务器发布补丁列表
     * @return
     */
    @RequestMapping(value = "/server/getPatchFileList", method = RequestMethod.POST)
    @ResponseBody
    Map<String, Object> getPatchFileList(@RequestBody HashMap paramMap);

    /**
     * 根据id获取补丁信息
     * @return
     */
    @RequestMapping(value = "/server/getUpPatchByPatchId", method = RequestMethod.POST)
    @ResponseBody
    Map<String, Object> getUpPatchByPatchId(@RequestBody HashMap paramMap);

    /**
     * 合并云服务器发布补丁包
     * @return
     */
    @RequestMapping(value = "/server/mergeDownlod", method = RequestMethod.POST)
    @ResponseBody
    Map<String, Object> mergeDownlod(@RequestBody HashMap paramMap);

    /**
     * 获取云服务器子包列表
     * @return
     */
    @RequestMapping(value = "/server/getPatchChildList", method = RequestMethod.POST)
    @ResponseBody
    Map<String, Object> getPatchChildList(@RequestBody HashMap paramMap);

    /**
     * 取消合并
     * @return
     */
    @RequestMapping(value = "/server/cancleMerge", method = RequestMethod.POST)
    @ResponseBody
    Map<String, Object> cancleMerge(@RequestBody HashMap paramMap);

    /**
     * 推送到云端的补丁状态
     * @return
     */
    @RequestMapping(value = "/server/updateState", method = RequestMethod.POST)
    @ResponseBody
    Map<String, Object> updateState(@RequestBody HashMap paramMap);

    /**
     * 导入云服务器升级记录
     * @return
     */
    @RequestMapping(value = "/server/importUpgradeRecord", method = RequestMethod.POST)
    @ResponseBody
    Map<String, Object> importUpgradeRecord(@RequestBody ArrayList<UpUpgradeRecordClient> dataList);

    /**
     * 查询服务器列表
     * @return
     */
    @RequestMapping(value = "/server/upgradeServers", method = RequestMethod.POST)
    @ResponseBody
    Map<String, Object> getUpgradeServers(@RequestBody HashMap paramMap);


    /**
     * 合包失败重新打包
     * @return
     */
    @RequestMapping(value = "/server/rePatch", method = RequestMethod.POST)
    @ResponseBody
    Map<String, Object> rePatch(@RequestBody HashMap paramMap);

    /**
     * 获取所有的产品和项目
     * @return
     */
    @RequestMapping(value = "/server/getProductAndProject", method = RequestMethod.POST)
    @ResponseBody
    Map<String, Object> getProductAndProject();

    /**
     * 拉取时利用任务合并时间解决跨包问题
     * @return
     */
    @RequestMapping(value = "/server/pullValidatePatch", method = RequestMethod.POST)
    @ResponseBody
    Map<String, Object> pullValidatePatch(@RequestBody HashMap paramMap);
}
