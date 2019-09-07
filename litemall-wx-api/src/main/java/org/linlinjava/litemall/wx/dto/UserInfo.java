package org.linlinjava.litemall.wx.dto;

import com.github.binarywang.java.emoji.EmojiConverter;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

public class UserInfo {
    private EmojiConverter emojiConverter = EmojiConverter.getInstance();

    private String openid;//微信唯一标识
    private String nickName;//用户昵称
    private String headimgurl;//用户头像
    private String country;//国家
    private String province;//省份
    private String city;//城市
    private String unionid;//微信用户唯一表示
    private Byte sex;//性别

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }


    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getHeadimgurl() {
        return headimgurl;
    }

    public void setHeadimgurl(String headimgurl) {
        this.headimgurl = headimgurl;
    }

    public String getUnionid() {
        return unionid;
    }

    public void setUnionid(String unionid) {
        this.unionid = unionid;
    }

    public Byte getSex() {
        return sex;
    }

    public void setSex(Byte sex) {
        this.sex = sex;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }


    public UserInfo() {
    }

    public UserInfo(String openid, String headimgurl, String country, String province, String city, String unionid, Byte sex) {
        this.openid=openid;

        this.headimgurl = headimgurl;
        this.country = country;
        this.province = province;
        this.city = city;
        this.unionid = unionid;
        this.sex = sex;
    }

}
