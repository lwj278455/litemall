package org.linlinjava.litemall.db.service;

import org.apache.ibatis.annotations.Param;
import org.linlinjava.litemall.db.dao.LitemallDiscountMapper;
import org.linlinjava.litemall.db.domain.LitemallDiscount;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class LitemallDiscountService {
    @Autowired
    private LitemallDiscountMapper discountMapper;

    public LitemallDiscount selectEntityByDiscountId(Integer discountId) {
        return   discountMapper.selectEntityByDiscountId(discountId);
    }

    public List<LitemallDiscount> selectListByUserId(Integer userId){
        List<LitemallDiscount>  discountList = discountMapper.selectListByUserId(userId);
        return discountList;
    }


    public int delByDiscountId(Integer id){
        return discountMapper.delByDiscountId(id);
    }

    public int add(LitemallDiscount discount){
        return  discountMapper.add(discount);
    }
}
