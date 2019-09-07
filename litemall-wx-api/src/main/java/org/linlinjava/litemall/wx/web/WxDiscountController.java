package org.linlinjava.litemall.wx.web;

import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.service.LitemallDiscountService;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

@RestController
@RequestMapping("wx/discount")
@Validated
public class WxDiscountController {
    @Autowired
    private LitemallDiscountService discountService;
    @GetMapping("myList")
    public Object getMyDiscountList(@LoginUser Integer userId){
        ExecutorService executorService = Executors.newFixedThreadPool(2);
        Callable<List> discountListCall=()->discountService.selectListByUserId(userId);
        FutureTask<List> discountListTask=new FutureTask<>(discountListCall);

        executorService.submit(discountListTask);
        Map<String,Object> entity=new HashMap<>();

        try {
            entity.put("discountList",discountListTask.get());
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseUtil.ok(entity);
    }
}
