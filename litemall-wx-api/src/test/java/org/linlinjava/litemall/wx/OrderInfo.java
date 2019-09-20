package org.linlinjava.litemall.wx;

public class OrderInfo {
    private String appid;// 云代付ID
    private String channel;// 标识，wx微信 ali支付宝
    private String order_no;// 订单号
    private String amount;//金额
    private String sign;// 签名
    private String recipient_account_type;// 支付宝标识
    private String recipient_account;// 支付宝号
    private String recipient_name;// 支付宝姓名
    private String description;// 订单描述
    private String recipient_openid;//微信用户标识

    public String getAppid() {
        return appid;
    }

    public void setAppid(String appid) {
        this.appid = appid;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getOrder_no() {
        return order_no;
    }

    public void setOrder_no(String order_no) {
        this.order_no = order_no;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getRecipient_account_type() {
        return recipient_account_type;
    }

    public void setRecipient_account_type(String recipient_account_type) {
        this.recipient_account_type = recipient_account_type;
    }

    public String getRecipient_account() {
        return recipient_account;
    }

    public void setRecipient_account(String recipient_account) {
        this.recipient_account = recipient_account;
    }

    public String getRecipient_name() {
        return recipient_name;
    }

    public void setRecipient_name(String recipient_name) {
        this.recipient_name = recipient_name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getRecipient_openid() {
        return recipient_openid;
    }

    public void setRecipient_openid(String recipient_openid) {
        this.recipient_openid = recipient_openid;
    }
}
