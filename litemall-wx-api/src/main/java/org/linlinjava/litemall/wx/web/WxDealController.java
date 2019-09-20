package org.linlinjava.litemall.wx.web;

import org.linlinjava.litemall.core.util.JacksonUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallOrder;
import org.linlinjava.litemall.db.domain.LitemallOrderGoods;
import org.linlinjava.litemall.db.service.LitemallOrderGoodsService;
import org.linlinjava.litemall.db.service.LitemallOrderService;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.linlinjava.litemall.wx.service.WxOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import sun.misc.Request;

import javax.servlet.http.HttpServletRequest;
import java.net.HttpURLConnection;
import java.util.List;

import static org.linlinjava.litemall.wx.util.WxResponseCode.AUTH_INVALID_ACCOUNT;
import static org.linlinjava.litemall.wx.util.WxResponseCode.ORDER_INVALID_OPERATION;

/**
 * 交易厅交易
 */
@RestController
@RequestMapping("wx/deal")
public class WxDealController {
    @Autowired
    private LitemallOrderGoodsService orderGoodsService;
    @Autowired
    private LitemallOrderService orderService;
    @Autowired
    private WxOrderService wxOrderService;
    @PostMapping("/sell")
    public Object sell(@LoginUser Integer userId, @RequestBody String body) {
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        if (StringUtils.isEmpty(userId)){
            return ResponseUtil.unlogin();
        }
        if (StringUtils.isEmpty(orderId)) {
            return ResponseUtil.badArgument();
        }

        LitemallOrder order = orderService.findById(orderId);

        if (userId != order.getUserId()) {
            return ResponseUtil.fail(AUTH_INVALID_ACCOUNT, "用户没有操作权限");
        }

        if (order.getOrderStatus() != 201) {
            return ResponseUtil.unauthz();
        }

        order.setDealStatus((short) 1);
        orderService.updateWithOptimisticLocker(order);
        return ResponseUtil.ok();
    }

    @GetMapping("/sellList")
    public Object sellList(@LoginUser Integer userId,
                           @RequestParam(defaultValue = "1") Integer dealStaus,
                           @RequestParam(defaultValue = "1") Integer page,
                           @RequestParam(defaultValue = "10") Integer limit,
                           @Sort @RequestParam(defaultValue = "add_time") String sort,
                           @Order @RequestParam(defaultValue = "asc") String order) {
        if (StringUtils.isEmpty(userId)){
            return ResponseUtil.unlogin();

        }
        if (StringUtils.isEmpty(dealStaus)) {
            return ResponseUtil.badArgument();
        }
        return wxOrderService.sellList(userId, dealStaus,page,limit,sort,order);
    }


    /**
     * 提交订单
     *
     * @param userId 用户ID
     * @param body 订单信息，{ cartId：xxx, addressId: xxx, couponId: xxx, message: xxx, grouponRulesId: xxx,  grouponLinkId: xxx}
     * @return 提交订单操作结果@LoginUser Integer userId
     */
    @PostMapping("submit")
    public Object submit(@LoginUser Integer userId,@RequestBody String body, HttpServletRequest request) {
        return wxOrderService.subAlipay(userId, body,request);
    }

}
