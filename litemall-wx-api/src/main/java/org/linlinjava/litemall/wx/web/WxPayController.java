package org.linlinjava.litemall.wx.web;

import io.swagger.models.auth.In;
import org.linlinjava.litemall.core.util.JacksonUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.dao.LitemallUserRelationsMapper;
import org.linlinjava.litemall.db.domain.LitemallCash;
import org.linlinjava.litemall.db.domain.LitemallOrder;
import org.linlinjava.litemall.db.domain.LitemallUser;
import org.linlinjava.litemall.db.domain.LitemallUserRelations;
import org.linlinjava.litemall.db.service.LitemallCashService;
import org.linlinjava.litemall.db.service.LitemallOrderService;
import org.linlinjava.litemall.db.service.LitemallUserRelationsService;
import org.linlinjava.litemall.db.service.LitemallUserService;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.linlinjava.litemall.wx.util.JsApiSignEntity;
import org.linlinjava.litemall.wx.util.PayUtil;
import org.linlinjava.litemall.wx.util.WxAuthorization;
import org.linlinjava.litemall.wx.util.XmlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.HashMap;
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
        return data;
    }

}
