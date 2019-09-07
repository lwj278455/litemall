package org.linlinjava.litemall.wx.web;

import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.LitemallOrder;
import org.linlinjava.litemall.db.domain.LitemallOrderGoods;
import org.linlinjava.litemall.db.service.LitemallOrderGoodsService;
import org.linlinjava.litemall.db.service.LitemallOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import sun.misc.Request;

import java.util.List;

/**
 *
 * 交易厅交易
 *
 */
@RestController
@RequestMapping("wx/deal")
public class WxDealController {

    private LitemallOrderGoodsService orderGoodsService;
    @Autowired
    private LitemallOrderService orderService;

    @GetMapping("/sell")
    public Object sell(Integer orderId,Integer number){
        LitemallOrder order = orderService.findById(orderId);
        if (order.getOrderStatus()!=2){
            return ResponseUtil.unauthz();
        }
        order.setDealStatus((short)1);
        orderService.updateWithOptimisticLocker(order);
        return ResponseUtil.ok(orderId);
    }
    @GetMapping("/sellList")
    public Object sellList(@RequestParam("dealStaus") Integer dealStaus){
        return orderService.sellList(1,dealStaus);
    }
}
