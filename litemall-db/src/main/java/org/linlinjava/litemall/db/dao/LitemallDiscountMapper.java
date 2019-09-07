package org.linlinjava.litemall.db.dao;

import org.apache.ibatis.annotations.Param;
import org.linlinjava.litemall.db.domain.LitemallDiscount;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface LitemallDiscountMapper {
      LitemallDiscount selectEntityByDiscountId(@Param("discountId") Integer discountId);


      List<LitemallDiscount> selectListByUserId(@Param("userId") Integer userId);


      int delByDiscountId(@Param("id") Integer id);

      int add(@Param("discount") LitemallDiscount discount);
}
