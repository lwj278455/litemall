package org.linlinjava.litemall.wx.web;

import io.swagger.models.auth.In;
import org.linlinjava.litemall.core.util.JacksonUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.LitemallOrder;
import org.linlinjava.litemall.db.service.LitemallOrderService;
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
import sun.plugin.cache.JarCacheUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("wx/pay")
public class WxPayController {

    @Autowired
    private PayUtil payUtil;
    @Autowired
    private LitemallOrderService orderService;

    /**
     *  @LoginUser
     * @param body
     * @param request
     * @return
     */
    @PostMapping("payment")
    public Object paypar(@LoginUser Integer userId,@RequestBody String body, HttpServletRequest request) {
        if (StringUtils.isEmpty(body)&&StringUtils.isEmpty(userId)) {
            return ResponseUtil.badArgumentValue();
        }
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        LitemallOrder order =  orderService.findById(orderId);
        Map map =null;
        String orderResult = payUtil.pay(order.getActualPrice().doubleValue(),userId,request).toString();
        if (orderResult==null|| StringUtils.isEmpty(orderResult)){
            map=new HashMap();
            map.put("403","调用下单接口失败");
            return ResponseUtil.ok(map);
        }
        try {
            map= XmlUtil.doXMLParse(orderResult);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String prepay_id =  map.get("prepay_id").toString();
        String appId = WxAuthorization.APP_ID;
        String timeStamp = PayUtil.create_timestamp();
        String signType = "MD5";


        JsApiSignEntity orderEntity = new JsApiSignEntity();

        orderEntity.setAppId(WxAuthorization.APP_ID);
        orderEntity.setNonceStr(map.get("nonce_str").toString());
        orderEntity.set_package("prepay_id="+prepay_id);
        orderEntity.setTimeStamp(timeStamp);
        orderEntity.setSignType("MD5");
        String paySign=null;
        try {
            paySign = PayUtil.generateSignature(orderEntity);
            System.out.println("支付签名"+paySign);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        Map data = new HashMap();
        data.put("nonce_str",map.get("nonce_str"));
        data.put("package",orderEntity.get_package());
        data.put("appId",appId);
        data.put("timeStamp",timeStamp);
        data.put("signType","MD5");
        data.put("paySign",paySign);
        System.out.println(data);
        return data;
    }

}
