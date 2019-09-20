package org.linlinjava.litemall.wx.web;

import io.swagger.models.auth.In;
import org.linlinjava.litemall.core.util.JacksonUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.dao.LitemallUserRelationsMapper;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.service.*;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.linlinjava.litemall.wx.util.JsApiSignEntity;
import org.linlinjava.litemall.wx.util.PayUtil;
import org.linlinjava.litemall.wx.util.WxAuthorization;
import org.linlinjava.litemall.wx.util.XmlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("wx/pay")
public class WxPayController {

    @Autowired
    private PayUtil payUtil;
    @Autowired
    private LitemallOrderService orderService;
    @Autowired
    private LitemallCashService litemallCashService;
    @Autowired
    private LitemallUserRelationsService userRelationsService;
    @Autowired
    private LitemallUserService userService;
    @Autowired
    private LitemallContractService contractService;

    @GetMapping("/contract")
    public Object findContract(Integer status) {
        return ResponseUtil.ok(contractService.findByStatus(status));
    }


    /**
     * 代理商充值
     *
     * @param body
     * @param request
     * @return
     * @LoginUser
     */
    @PostMapping("payment")
    public Object paypar(@LoginUser Integer userId, @RequestBody String body, HttpServletRequest request) {
        if (StringUtils.isEmpty(userId)) {
            return ResponseUtil.unlogin();
        }
        if (StringUtils.isEmpty(body) && StringUtils.isEmpty(userId)) {
            return ResponseUtil.badArgumentValue();
        }
        Integer status = JacksonUtil.parseInteger(body, "status");
        double price = 0;
        if (status == 1) {
            price = 10000.00;
        } else if (status == 2) {
            price = 30000.00;
        } else if (status == 3) {
            price = 90000.00;
        }
        String orderNo = "d" + PayUtil.create_timestamp();
        LitemallCash litemallCash = new LitemallCash();
        litemallCash.setCashNum(orderNo);
        litemallCash.setPaidAmount(BigDecimal.valueOf(price));
        litemallCash.setUserId(userId);
        litemallCash.setStatus(String.valueOf(1));
        litemallCash.setType("成为代理商");
        litemallCashService.add(litemallCash);

        List<LitemallUserRelations> userRelationsList = userRelationsService.findByChildid(userId.toString());
        if (userRelationsList!=null){
            for (LitemallUserRelations userRelations:userRelationsList){
                LitemallUser user = userService.findById(userRelations.getUserid().intValue());
                if (user.getStatus()>status){
                    user.setUserPrice(new BigDecimal(price*0.3));
                }
            }
        }
        Map map = null;
        String orderResult = payUtil.pay(price, userId, orderNo, request).toString();
        if (orderResult == null || StringUtils.isEmpty(orderResult)) {
            map = new HashMap();
            map.put("403", "调用下单接口失败");
            return ResponseUtil.ok(map);
        }
        try {
            map = XmlUtil.doXMLParse(orderResult);
        } catch (Exception e) {
            e.printStackTrace();
        }


        String prepay_id = map.get("prepay_id").toString();
        String appId = WxAuthorization.APP_ID;
        String timeStamp = PayUtil.create_timestamp();
        String signType = "MD5";


        JsApiSignEntity orderEntity = new JsApiSignEntity();

        orderEntity.setAppId(WxAuthorization.APP_ID);
        orderEntity.setNonceStr(map.get("nonce_str").toString());
        orderEntity.set_package("prepay_id=" + prepay_id);
        orderEntity.setTimeStamp(timeStamp);
        orderEntity.setSignType("MD5");
        String paySign = null;
        try {
            paySign = PayUtil.generateSignature(orderEntity);
            System.out.println("支付签名" + paySign);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Map data = new HashMap();
        data.put("nonce_str", map.get("nonce_str"));
        data.put("package", orderEntity.get_package());
        data.put("appId", appId);
        data.put("timeStamp", timeStamp);
        data.put("signType", "MD5");
        data.put("paySign", paySign);
        System.out.println(data);
        return ResponseUtil.ok(data);
    }

    @PostMapping("pay")
    public Object pay(@LoginUser Integer userId, @RequestBody String body, HttpServletRequest request) {
        if (StringUtils.isEmpty(userId)) {
            return ResponseUtil.unlogin();
        }
        if (StringUtils.isEmpty(body) && StringUtils.isEmpty(userId)) {
            return ResponseUtil.badArgumentValue();
        }
        Integer status = JacksonUtil.parseInteger(body, "status");
        double price = 1.00;
        String orderNo = "d" + PayUtil.create_timestamp();
        LitemallCash litemallCash = new LitemallCash();
        litemallCash.setCashNum(orderNo);
        litemallCash.setPaidAmount(BigDecimal.valueOf(price));
        litemallCash.setUserId(userId);
        litemallCash.setStatus(String.valueOf(1));
        litemallCash.setType("成为体验官");
        litemallCashService.add(litemallCash);
        Map map = null;
        String orderResult = payUtil.pay(price, userId, orderNo, request).toString();
        if (orderResult == null || StringUtils.isEmpty(orderResult)) {
            map = new HashMap();
            map.put("403", "调用下单接口失败");
            return ResponseUtil.ok(map);
        }
        try {
            map = XmlUtil.doXMLParse(orderResult);
        } catch (Exception e) {
            e.printStackTrace();
        }


        String prepay_id = map.get("prepay_id").toString();
        String appId = WxAuthorization.APP_ID;
        String timeStamp = PayUtil.create_timestamp();
        String signType = "MD5";


        JsApiSignEntity orderEntity = new JsApiSignEntity();

        orderEntity.setAppId(WxAuthorization.APP_ID);
        orderEntity.setNonceStr(map.get("nonce_str").toString());
        orderEntity.set_package("prepay_id=" + prepay_id);
        orderEntity.setTimeStamp(timeStamp);
        orderEntity.setSignType("MD5");
        String paySign = null;
        try {
            paySign = PayUtil.generateSignature(orderEntity);
            System.out.println("支付签名" + paySign);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Map data = new HashMap();
        data.put("nonce_str", map.get("nonce_str"));
        data.put("package", orderEntity.get_package());
        data.put("appId", appId);
        data.put("timeStamp", timeStamp);
        data.put("signType", "MD5");
        data.put("paySign", paySign);
        System.out.println(data);
        return ResponseUtil.ok(data);
    }
}
