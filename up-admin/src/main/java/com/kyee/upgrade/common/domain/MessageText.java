package com.kyee.upgrade.common.domain;

import java.util.List;

/**
 * 企业微信机器人发消息text实体
 */
public class MessageText {

    private String content;

    private List<String> mentioned_list;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public List<String> getMentioned_list() {
        return mentioned_list;
    }

    public void setMentioned_list(List<String> mentioned_list) {
        this.mentioned_list = mentioned_list;
    }
}
