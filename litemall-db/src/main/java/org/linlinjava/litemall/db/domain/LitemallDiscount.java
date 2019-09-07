package org.linlinjava.litemall.db.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LitemallDiscount {
    private Integer id;

    private String discountName;  //打折卡名称

    private BigDecimal discountPrice; //几折

    private BigDecimal discountTag; //多少钱可用

    private LocalDateTime addTime;//创建时间

    private LocalDateTime useTime;//使用时间

    private LocalDateTime pastTime;//过期时间

    private Integer userId; //所有人Id

    private Integer deleted;//逻辑删除

    public LitemallDiscount() {
    }

    public LitemallDiscount(Integer id, String discountName, BigDecimal discountPrice, BigDecimal discountTag, LocalDateTime addTime, LocalDateTime useTime, LocalDateTime pastTime, Integer userId, Integer deleted) {
        this.id = id;
        this.discountName = discountName;
        this.discountPrice = discountPrice;
        this.discountTag = discountTag;
        this.addTime = addTime;
        this.useTime = useTime;
        this.pastTime = pastTime;
        this.userId = userId;
        this.deleted = deleted;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDiscountName() {
        return discountName;
    }

    public void setDiscountName(String discountName) {
        this.discountName = discountName;
    }

    public BigDecimal getDiscountPrice() {
        return discountPrice;
    }

    public void setDiscountPrice(BigDecimal discountPrice) {
        this.discountPrice = discountPrice;
    }

    public BigDecimal getDiscountTag() {
        return discountTag;
    }

    public void setDiscountTag(BigDecimal discountTag) {
        this.discountTag = discountTag;
    }

    public LocalDateTime getAddTime() {
        return addTime;
    }

    public void setAddTime(LocalDateTime addTime) {
        this.addTime = addTime;
    }

    public LocalDateTime getUseTime() {
        return useTime;
    }

    public void setUseTime(LocalDateTime useTime) {
        this.useTime = useTime;
    }

    public LocalDateTime getPastTime() {
        return pastTime;
    }

    public void setPastTime(LocalDateTime pastTime) {
        this.pastTime = pastTime;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getDeleted() {
        return deleted;
    }

    public void setDeleted(Integer deleted) {
        this.deleted = deleted;
    }
}
