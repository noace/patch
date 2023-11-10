package com.kyee.upgrade.scheduled.job;

import com.alibaba.fastjson.JSONObject;
import com.kyee.upgrade.common.domain.MessageText;
import com.kyee.upgrade.common.domain.TencentMessage;
import com.kyee.upgrade.utils.ConstantUtil;
import com.ruoyi.common.utils.DateUtils;

import com.ruoyi.common.utils.StringUtils;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 企业微信发消息定时器 9点发送
 */
@Service
public class TencentMessageJob {

    private static final Logger log = LoggerFactory.getLogger(TencentMessageJob.class);

    // 企业微信通知KEY
    @Value(value = "${tencentMsg.hisKey}")
    private String hisKey;

    @Value(value = "${tencentMsg.projectKey}")
    private String projectKey;

    @Value(value = "${tencentMsg.productKey}")
    private String productKey;

    @Autowired
    private MessageContentService messageService;

    // his研发中心部门ID
    private static final String hisDeptId = "101";
    // 项目研发中心部门ID
    private static final String projectDeptId = "110";

    // 0 5 9 ? * *   每天9点05分
    // 0 0/1 * * * ? 每一分钟执行一次
//    @Scheduled(cron = "0 5 9 ? * *")
    public void messageJob() {

        noPublishMessageService(hisKey, hisDeptId);
        noPublishMessageService(projectKey, projectDeptId);
    }

    // 0 30 17 ? * *   每天下午17点30分
//    @Scheduled(cron = "0 30 17 ? * *")
    public void messageJob2() {
        noPublishMessageService(hisKey, hisDeptId);
        noPublishMessageService(projectKey, projectDeptId);
    }

    // 0 0/30 9-18 * * ?    每天9点到18点，每一小时发一次
//    @Scheduled(cron = "0 0 9-18 * * ?")
    public void messageJob3() {
        failMessageService(hisKey, hisDeptId);
        failMessageService(projectKey, projectDeptId);
    }

    /**
     * 未发布消息业务
     */
    private void noPublishMessageService(String key, String deptId) {
        // markdown格式
        List<String> userIds = new ArrayList<>();
        MessageText markdownText = new MessageText();
        TencentMessage markdownMsg = new TencentMessage();

        // text格式
        TencentMessage textMsg = new TencentMessage();
        MessageText textText = new MessageText();

        // 查询当天未发布的补丁包总数
        int count = messageService.getPatchWithSuccess(deptId);

        // 统计当天未发布的补丁包
        List<Map<String, Integer>> nameMap = messageService.getCountByUsername(deptId);

        if (count != 0) {
            List<String> userList = userIds.stream().distinct().collect(Collectors.toList());
            log.info(userList.toString());

            String dateMsg = DateUtils.dateMsg();
            // 构造实体
            StringBuilder sb = new StringBuilder();
            String content = "截止" + dateMsg + ",共有<font color=\"warning\">" + count + "</font>个补丁包未发布。\n ";
            if (!CollectionUtils.isEmpty(nameMap)) {
                for (Map<String, Integer> map : nameMap) {
                    userIds.add(String.valueOf(map.get("id")));
                    sb.append(">").append(map.get("name")).append(":<font color=\"comment\">").append(map.get("total")).append("</font>\n");
                }
            }

            markdownText.setContent(content + sb.toString());
            markdownMsg.setMsgtype("markdown");
            markdownMsg.setMarkdown(markdownText);

            textText.setContent("以下同事请注意：您的补丁包还未发布，请及时处理。\n如有疑问，请联系管理员:党行行");
            textText.setMentioned_list(userIds);
            textMsg.setMsgtype("text");
            textMsg.setText(textText);

            sendMessage(markdownMsg, key);
            if (userIds.size() != 0) {
                sendMessage(textMsg, key);
            }
        }
    }

    /**
     * 打包失败和发现BUG消息业务
     */
    private void failMessageService(String key, String deptId) {
        // markdown格式
        List<String> userIds = new ArrayList<>();
        // text格式
        TencentMessage textMsg = new TencentMessage();
        MessageText textText = new MessageText();
        StringBuilder sb = new StringBuilder();
        // 查询打包失败的补丁包
        List<Map<String, String>> failMap = messageService.getPatchWithFailAndBug(deptId);
        if (!CollectionUtils.isEmpty(failMap)) {
            for (Map<String, String> map : failMap) {
                if ("Y".equals(map.get("merge"))) {
                    continue;
                }
                userIds.add(String.valueOf(map.get("id")));
                if (ConstantUtil.UpPatchStatus.PKG_FAIL.getValue().equals(map.get("status"))) {
                    sb.append("JIRA任务号【").append(map.get("jiraNo")).append("】打包失败，请").append(map.get("username")).append("及时处理。").append("\n");
                } else if (ConstantUtil.UpPatchStatus.ROLLBACK.getValue().equals(map.get("status"))) {
                    sb.append("补丁包【").append(map.get("patchName")).append("】发现BUG，请").append(map.get("username")).append("按照BUG包流程及时处理。").append("\n");
                }
            }
        }
        List<String> userList = userIds.stream().distinct().collect(Collectors.toList());
        textText.setContent(sb.toString());
        textText.setMentioned_list(userList);
        textMsg.setMsgtype("text");
        textMsg.setText(textText);

        if (userIds.size() != 0) {
            sendMessage(textMsg,key);
        }
    }

    /**
     * 发送加急包升级消息
     */
    public void sendExpeditedUpgradeMassage(String productName, String productPrincipal, String developer, String patchFileName, String jiraNo, String topic,List<String> jobNos,List<String> names) {
        TencentMessage textMsg = new TencentMessage();
        textMsg.setMsgtype("text");
        String name = StringUtils.join(names, "、");
        MessageText textText = new MessageText();
        // 构造实体
        textText.setContent("【紧急任务通知】" + productPrincipal + "项目有紧急任务，请" + name + "及时升级，收到请回复！" + "\n项目名称：" + productPrincipal + "\n项目经理：" + name + "\nJIRA任务：" + jiraNo + " " + topic + "\n开发人员：" + developer + "\n补丁包名：" + patchFileName);
        textText.setMentioned_list(jobNos);
        textMsg.setMsgtype("text");
        textMsg.setText(textText);
        sendMessage(textMsg, productKey);
    }

    /**
     * 发送消息
     * @param message 消息实体
     */
    public void sendMessage(TencentMessage message, String key) {
        // 转换JSON
        String json = JSONObject.toJSONString(message);
        String url = "https://qyapi.weixin.qq.com/cgi-bin/webhook/send?key=" + key;
        OkHttpClient client = new OkHttpClient().newBuilder().build();
        MediaType mediaType = MediaType.parse("application/json");
        okhttp3.RequestBody body = okhttp3.RequestBody.create(mediaType, json);
        okhttp3.Request request = new okhttp3.Request.Builder()
                                                    .url(url)
                                                    .post(body)
                                                    .addHeader("Content-Type", "application/json")
                                                    .build();
        try {
            Response execute = client.newCall(request).execute();
            Objects.requireNonNull(execute.body()).string();
        } catch (IOException e) {
            log.info("发送消息失败");
            e.printStackTrace();
        }
    }
}
