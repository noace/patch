package com.kyee.upgrade.common.domain;

/**
 * 企业微信机器人发消息实体
 */
public class TencentMessage {

    private String msgtype;

    private MessageText text;

    private MessageText markdown;

    public String getMsgtype() {
        return msgtype;
    }

    public void setMsgtype(String msgtype) {
        this.msgtype = msgtype;
    }

    public MessageText getText() {
        return text;
    }

    public void setText(MessageText text) {
        this.text = text;
    }

    public MessageText getMarkdown() {
        return markdown;
    }

    public void setMarkdown(MessageText markdown) {
        this.markdown = markdown;
    }
}
