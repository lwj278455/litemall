package org.linlinjava.litemall.wx.service;


import com.github.binarywang.wxpay.bean.notify.WxPayNotifyResponse;
import com.github.binarywang.wxpay.bean.notify.WxPayOrderNotifyResult;
import com.github.binarywang.wxpay.bean.result.BaseWxPayResult;
import com.github.binarywang.wxpay.constant.WxPayConstants;
import com.github.binarywang.wxpay.exception.WxPayException;
import com.github.binarywang.wxpay.service.WxPayService;

import com.github.pagehelper.util.StringUtil;
import org.apache.commons.io.IOUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.core.express.ExpressService;
import org.linlinjava.litemall.core.express.dao.ExpressInfo;
import org.linlinjava.litemall.core.notify.NotifyService;
import org.linlinjava.litemall.core.notify.NotifyType;
import org.linlinjava.litemall.core.qcode.QCodeService;
import org.linlinjava.litemall.core.util.*;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.service.*;
import org.linlinjava.litemall.db.util.OrderHandleOption;
import org.linlinjava.litemall.db.util.OrderUtil;
import org.linlinjava.litemall.wx.util.*;
import org.linlinjava.litemall.wx.util.WxOrderResultEntity;
import org.linlinjava.litemall.wx.vo.Sign;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;


import static org.linlinjava.litemall.wx.util.WxResponseCode.*;

/**
 * 订单服务
 *
 * <p>
 * 订单状态：
 * 101 订单生成，未支付；102，下单后未支付用户取消；103，下单后未支付超时系统自动取消
 * 201 支付完成，商家未发货；202，订单生产，已付款未发货，但是退款取消；
 * 301 商家发货，用户未确认；
 * 401 用户确认收货； 402 用户没有确认收货超过一定时间，系统自动确认收货；
 *
 * <p>
 * 用户操作：
 * 当101用户未付款时，此时用户可以进行的操作是取消订单，或者付款操作
 * 当201支付完成而商家未发货时，此时用户可以取消订单并申请退款
 * 当301商家已发货时，此时用户可以有确认收货的操作
 * 当401用户确认收货以后，此时用户可以进行的操作是删除订单，评价商品，或者再次购买
 * 当402系统自动确认收货以后，此时用户可以删除订单，评价商品，或者再次购买
 *
 * <p>
 * 注意：目前不支持订单退货和售后服务
 */
@Service
public class WxOrderService {
    private final Log logger = LogFactory.getLog(WxOrderService.class);

    @Autowired
    private LitemallUserService userService;
    @Autowired
    private LitemallOrderService orderService;
    @Autowired
    private LitemallOrderGoodsService orderGoodsService;
    @Autowired
    private LitemallAddressService addressService;
    @Autowired
    private LitemallCartService cartService;
    @Autowired
    private LitemallRegionService regionService;
    @Autowired
    private LitemallGoodsProductService productService;
    @Autowired
    private WxPayService wxPayService;
    @Autowired
    private NotifyService notifyService;
    @Autowired
    private LitemallUserFormIdService formIdService;
    @Autowired
    private LitemallGrouponRulesService grouponRulesService;
    @Autowired
    private LitemallGrouponService grouponService;
    @Autowired
    private QCodeService qCodeService;
    @Autowired
    private ExpressService expressService;
    @Autowired
    private LitemallCommentService commentService;
    @Autowired
    private LitemallCouponService couponService;
    @Autowired
    private LitemallCouponUserService couponUserService;
    @Autowired
    private CouponVerifyService couponVerifyService;
    @Autowired
    private LitemallDiscountService discountService;
    @Autowired
    private LitemallGoodsService goodsService;
    @Autowired
    private LitemallGoodsSpecificationService goodsSpecificationService;
    @Autowired
    private LitemallGoodsProductService goodsProductService;
    @Autowired
    private LitemallUserRelationsService userRelationsService;

    @Value("${litemall.wx.app-id}")
    private String APP_ID = "";
    @Value("${litemall.wx.key}")
    private String KEY = "";
    @Autowired
    private LitemallFlowService litemallFlowService;
    @Autowired
    private LitemallCashService cashService;

    /**
     * 订单列表
     *
     * @param userId   用户ID
     * @param showType 订单信息：
     *                 0，全部订单；
     *                 1，待付款；
     *                 2，待发货；
     *                 3，待收货；
     *                 4，待评价。
     * @param page     分页页数
     * @param limit    分页大小
     * @return 订单列表
     */
    public Object list(Integer userId, Integer showType, Integer page, Integer limit, String sort, String order) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }

        List<Short> orderStatus = OrderUtil.orderStatus(showType);
        List<LitemallOrder> orderList = orderService.queryByOrderStatus(userId, orderStatus, page, limit, sort, order);
        List<Map<String, Object>> orderVoList = new ArrayList<>(orderList.size());
        for (LitemallOrder o : orderList) {
            Map<String, Object> orderVo = new HashMap<>();
            orderVo.put("id", o.getId());
            orderVo.put("orderSn", o.getOrderSn());
            orderVo.put("actualPrice", o.getActualPrice());
            orderVo.put("orderStatusText", o.getOrderStatus());
            orderVo.put("handleOption", OrderUtil.build(o));
            orderVo.put("comments", o.getComments() != 0 ? true : false);

            List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(o.getId());
            List<Map<String, Object>> orderGoodsVoList = new ArrayList<>(orderGoodsList.size());
            for (LitemallOrderGoods orderGoods : orderGoodsList) {
                Map<String, Object> orderGoodsVo = new HashMap<>();
                orderGoodsVo.put("id", orderGoods.getId());
                orderGoodsVo.put("goodsName", orderGoods.getGoodsName());
                orderGoodsVo.put("number", orderGoods.getNumber());
                orderGoodsVo.put("picUrl", orderGoods.getPicUrl());
                orderGoodsVo.put("price", orderGoods.getPrice());
                orderGoodsVo.put("specifications", orderGoods.getSpecifications());
                orderGoodsVoList.add(orderGoodsVo);
            }
            orderVo.put("goodsList", orderGoodsVoList);
            orderVoList.add(orderVo);
        }

        return ResponseUtil.okList(orderVoList, orderList);
    }

    public Object sellList(Integer userId, Integer dealStaus, Integer page, Integer limit, String sort, String order) {
        List<LitemallOrder> orderList = orderService.queryByDeal(userId, dealStaus, page, limit, sort, order);//查询出正在转卖的商品
        List<Map<String, Object>> orderVoList = new ArrayList<>(orderList.size());
        for (LitemallOrder o : orderList) {
            Map<String, Object> orderVo = new HashMap<>();
            orderVo.put("id", o.getId());
            orderVo.put("orderSn", o.getOrderSn());
            orderVo.put("actualPrice", o.getActualPrice());
            orderVo.put("orderStatusText", o.getOrderStatus());
            orderVo.put("handleOption", OrderUtil.build(o));
            List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(o.getId());
            List<Map<String, Object>> orderGoodsVoList = new ArrayList<>(orderGoodsList.size());
            for (LitemallOrderGoods orderGoods : orderGoodsList) {
                Map<String, Object> orderGoodsVo = new HashMap<>();
                orderGoodsVo.put("id", orderGoods.getId());
                orderGoodsVo.put("goodsName", orderGoods.getGoodsName());
                orderGoodsVo.put("number", orderGoods.getNumber());
                orderGoodsVo.put("picUrl", orderGoods.getPicUrl());
                orderGoodsVo.put("price", orderGoods.getPrice());
                orderGoodsVo.put("specifications", orderGoods.getSpecifications());
                orderGoodsVoList.add(orderGoodsVo);
            }
            orderVo.put("goodsList", orderGoodsVoList);
            orderVo.put("index", orderList.size());
            orderVoList.add(orderVo);
        }

        return ResponseUtil.okList(orderVoList, orderList);
    }

    /**
     * 订单详情
     *
     * @param userId  用户ID
     * @param orderId 订单ID
     * @return 订单详情
     */
    public Object detail(Integer userId, Integer orderId) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }

        // 订单信息
        LitemallOrder order = orderService.findById(orderId);

        if (null == order) {
            return ResponseUtil.fail(ORDER_UNKNOWN, "订单不存在");
        }
        Map<String, Object> orderVo = new HashMap<String, Object>();
        orderVo.put("id", order.getId());
        orderVo.put("orderSn", order.getOrderSn());
        orderVo.put("addTime", order.getAddTime());
        orderVo.put("consignee", order.getConsignee());
        orderVo.put("mobile", order.getMobile());
        orderVo.put("address", order.getAddress());
        orderVo.put("goodsPrice", order.getGoodsPrice());
        orderVo.put("integral_price", order.getIntegralPrice());
        orderVo.put("freightPrice", order.getFreightPrice());
        orderVo.put("actualPrice", order.getActualPrice());
        orderVo.put("orderStatusText", order.getOrderStatus());
        orderVo.put("handleOption", OrderUtil.build(order));
        orderVo.put("expCode", order.getShipChannel());
        orderVo.put("expNo", order.getShipSn());
        orderVo.put("coupon_price", order.getCouponPrice());


        List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(order.getId());

        Map<String, Object> result = new HashMap<>();
        result.put("orderInfo", orderVo);
        result.put("orderGoods", orderGoodsList);

        if (order.getOrderStatus().equals(OrderUtil.STATUS_SHIP)) {
            ExpressInfo ei = expressService.getExpressInfo(order.getShipChannel(), order.getShipSn());
            result.put("expressInfo", ei);
        }

        return ResponseUtil.ok(result);

    }

    /**
     * 提交订单
     * <p>
     * 1. 创建订单表项和订单商品表项;
     * 2. 购物车清空;
     * 3. 优惠券设置已用;
     * 4. 商品货品库存减少;
     * 5. 如果是团购商品，则创建团购活动表项。
     *
     * @param userId 用户ID
     * @param body   订单信息，
     * @return 提交订单操作结果
     */
    @Transactional
    public Object submit(Integer userId, String body, HttpServletRequest request) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        if (body == null) {
            return ResponseUtil.badArgument();
        }
        Integer number = JacksonUtil.parseInteger(body, "number");//商品数量
        Integer goodsId = JacksonUtil.parseInteger(body, "goodsId"); // 获取商品id
        Integer productId = JacksonUtil.parseInteger(body, "productId"); //获取商品规格id
        Integer addressId = JacksonUtil.parseInteger(body, "addressId"); //获取地址id
        Integer discountId = JacksonUtil.parseInteger(body, "discountId");//获取打折卡id
        Integer userIntegr = JacksonUtil.parseInteger(body, "userIntegr");//积分
        Integer frontprice = JacksonUtil.parseInteger(body, "frontprice");//前端价格
        //判断如果信息为空返回参数错误
        if (goodsId == null || addressId == null || productId == null) {
            return ResponseUtil.badArgument();
        }
        LitemallGoods goods = goodsService.findById(goodsId);//根据id查询出商品
        LitemallGoodsProduct goodsProduct = goodsProductService.findById(productId);//根据id查询出规格
        BigDecimal goodsprice = goodsProduct.getPrice();//把商品价格转换成big类型
        BigDecimal price = BigDecimal.ZERO;//真正付的价格
        BigDecimal couponprice = BigDecimal.ZERO;//优惠价格
        System.out.println("进入提交订单接口===userId========="+userId);
        System.out.println("进入提交订单接口==========discountId========"+discountId);
        System.out.println("进入提交订单接口==========number==========="+number);
        if (!StringUtils.isEmpty(discountId) && discountId != 0) {//判断此人是否使用打折卡
            LitemallDiscount discount = discountService.selectEntityByDiscountId(discountId);  //获取打折卡的信息
            BigDecimal discuntPrice = discount.getDiscountPrice();//几折
            couponprice = goodsprice.multiply(discuntPrice);//商品折扣*真正付的价格=优惠价格
            discountService.delByDiscountId(discountId);//更新打折卡
        } else if (number > 0 && StringUtils.isEmpty(discountId)||discountId==0) {
            for (int i = 0; number > i; i++) {
                LitemallDiscount litemallDiscount = new LitemallDiscount();
                System.out.println("添加优惠券userId++++++++++======"+userId);
                litemallDiscount.setUserId(userId);
                litemallDiscount.setDiscountName("二折卡");
                litemallDiscount.setDiscountTag(goodsprice);
                litemallDiscount.setAddTime(LocalDateTime.now());
                discountService.add(litemallDiscount);
            }
        }
        Integer total_Integral = null; // 剩余积分
        BigDecimal integralprice = BigDecimal.ZERO; //积分抵扣价格
        if (!StringUtils.isEmpty(userIntegr) || userIntegr > 0) {//判断是否使用积分
            Double integral = userIntegr * 0.01;
            integralprice = new BigDecimal(integral.toString());
            LitemallUser user = userService.findById(userId);
            total_Integral = user.getUserIntegr() - userIntegr;
            user.setUserIntegr(total_Integral);
            userService.updateById(user);

        }
        //获取地址详情信息
        LitemallAddress litemallAddress = addressService.queryByAddressId(addressId);
        //获取收货人手机号,名称
        LitemallUser user = userService.findById(userId);
        //减少商品库存
        Short num = 1;
        if (productService.reduceStock(productId, num) < 0) {
            //如果商品库存小于1返回错误信息
            return ResponseUtil.updatedDataFailed();
        }

        //获取订单地址
        LitemallAddress address = addressService.queryByAddressId(addressId);
        String addressDetail = address.getProvince() + address.getCity() + address.getCounty() + address.getAddressDetail();

        //算出价格
        BigDecimal amount = new BigDecimal(number.toString());

        BigDecimal retailprice = amount.multiply(goodsprice);

        price = amount.multiply(goodsprice).subtract(couponprice).subtract(integralprice);

        Integer orderId = null;
        //添加订单
        LitemallOrder order = new LitemallOrder();
        order.setUserId(userId);//用户id
        order.setConsignee(address.getName());
        String orderSn = PayUtil.create_timestamp();
        order.setOrderSn(orderSn);//订单编号
        order.setMobile(address.getTel());//电话号码
        order.setAddress(addressDetail);//订单地址
        order.setGoodsPrice(goodsprice);//商品价格
        order.setIntegralPrice(integralprice); //积分抵免
        order.setCouponPrice(couponprice);//优惠价格
        order.setOrderPrice(retailprice);//订单价格
        order.setActualPrice(price);//实付费用
        orderService.add(order);
        orderId = order.getId();
//        if (!StringUtils.isEmpty(integralprice) || integralprice.compareTo(BigDecimal.ZERO) != 0) {
//            LitemallFlow flow = new LitemallFlow();
//            flow.setFlowNum(orderSn);
//            flow.setPaidIntegral(userIntegr.toString());
//            flow.setTotalIntegral(total_Integral.toString());
//            flow.setPaidMethod("消费");
//            flow.setCreateTime(LocalDateTime.now());
//            litemallFlowService.add(flow);
//        }
        LitemallOrderGoods orderGoods = new LitemallOrderGoods();
        orderGoods.setOrderId(orderId);//订单表id
        orderGoods.setGoodsId(goodsId);
        orderGoods.setGoodsName(goods.getName());
        orderGoods.setGoodsSn(goods.getGoodsSn());
        orderGoods.setProductId(productId);
        orderGoods.setNumber(Short.parseShort(number.toString()));
        orderGoods.setPrice(goodsProduct.getPrice());
        orderGoods.setSpecifications(goodsProduct.getSpecifications());
        orderGoods.setPicUrl(goods.getPicUrl());
        orderGoodsService.add(orderGoods);

        Map map = null;
        String orderResult = prepay(userId, price, orderId, request).toString();
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

    /**
     * 判断是否有积分
     *
     * @return
     */
    @Transactional
    public Object isAgent(Integer userId) {
        if (StringUtils.isEmpty(userId)){
            return ResponseUtil.unlogin();
        }
        LitemallUser user = userService.findById(userId);
        return ResponseUtil.ok(user.getUserIntegr());
    }

    /**
     * 判断是否可使用优惠券
     *
     * @return
     */
    @Transactional
    public Object isDiscounts(Integer userId,BigDecimal retailprice) {
        if (StringUtils.isEmpty(userId)){
            return ResponseUtil.unlogin();
        }
        System.out.println("进入优惠卷接口==========="+userId);
        List<LitemallDiscount> discountList = discountService.selectListByUserId(userId);
        LitemallDiscount discount = null;
        for (LitemallDiscount discounts : discountList) {
            if (discounts.getDiscountTag().compareTo(retailprice) == 0) {
                discount = discounts;
                return ResponseUtil.ok(discount);
            }
        }
        return ResponseUtil.ok(false);
    }


    @Transactional
    public Object subAlipay(Integer userId, String body, HttpServletRequest request) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        if (body == null) {
            return ResponseUtil.badArgument();
        }
        System.out.println("进入支付=====");
        Integer addressId = JacksonUtil.parseInteger(body, "addressId"); //获取地址id
        Integer rgid = JacksonUtil.parseInteger(body, "Rgid");//订单商品id
        //判断如果信息为空返回参数错误
        if (StringUtils.isEmpty(addressId) || StringUtils.isEmpty(rgid)) {
            return ResponseUtil.badArgument();
        }
        LitemallOrderGoods litemallOrderGoods = orderGoodsService.findById(rgid);

        LitemallOrder order = orderService.findById(litemallOrderGoods.getOrderId());
        order.setDealStatus((short) 2);
        orderService.updateWithOptimisticLocker(order);
        LitemallGoods goods = goodsService.findById(litemallOrderGoods.getGoodsId());
        //获取订单地址
        LitemallAddress address = addressService.queryByAddressId(addressId);
        String addressDetail = address.getProvince() + address.getCity() + address.getCounty() + address.getAddressDetail();
        Integer orderId = null;

        BigDecimal prcie = BigDecimal.ZERO;

        //添加订单
        LitemallOrder neworder = new LitemallOrder();
        neworder.setUserId(userId);//用户id
        neworder.setConsignee(address.getName());
        String orderSn = PayUtil.create_timestamp();
        neworder.setOrderSn(orderSn);//订单编号
        neworder.setMobile(address.getTel());//电话号码
        neworder.setAddress(addressDetail);//订单地址
        neworder.setGoodsPrice(order.getGoodsPrice());//商品价格
        neworder.setIntegralPrice(order.getIntegralPrice()); //积分抵免
        neworder.setCouponPrice(order.getCouponPrice());//优惠价格
        neworder.setOrderPrice(order.getOrderPrice());//订单价格
        neworder.setActualPrice(order.getActualPrice());//实付费用
        orderService.add(neworder);

        orderId = order.getId();
        prcie = neworder.getGoodsPrice();
        LitemallUser user = userService.findById(userId);
        if (!StringUtils.isEmpty(order.getUserId())) {
            LitemallCash litemallCash = new LitemallCash();
            litemallCash.setUserId(order.getUserId());
            litemallCash.setCashNum(PayUtil.create_timestamp());
            BigDecimal paidprice = prcie.multiply(new BigDecimal(0.78)).setScale(2, RoundingMode.HALF_UP);
            litemallCash.setPaidAmount(paidprice);
            litemallCash.setRemBalance(user.getUserPrice());
            litemallCash.setType("收益");
            cashService.add(litemallCash);
            user.setUserPrice(user.getUserPrice().add(paidprice));
            userService.updateById(user);
        }


        LitemallOrderGoods orderGoods = new LitemallOrderGoods();
        orderGoods.setOrderId(neworder.getId());//订单表id
        orderGoods.setGoodsId(litemallOrderGoods.getGoodsId());
        orderGoods.setGoodsName(goods.getName());
        orderGoods.setGoodsSn(goods.getGoodsSn());
        orderGoods.setProductId(litemallOrderGoods.getProductId());
        orderGoods.setPrice(litemallOrderGoods.getPrice());
        orderGoods.setSpecifications(litemallOrderGoods.getSpecifications());
        orderGoods.setPicUrl(litemallOrderGoods.getPicUrl());
        orderGoodsService.add(orderGoods);

        Map map = null;
        String orderResult = prepay(userId, prcie, neworder.getId(), request).toString();
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
        data.put("package",  orderEntity.get_package());
        data.put("appId", appId);
        data.put("timeStamp", timeStamp);
        data.put("signType", "MD5");
        data.put("paySign", paySign);
        System.out.println(data);
        return ResponseUtil.ok(data);
    }

    /**
     * 取消订单
     * <p>
     * 1. 检测当前订单是否能够取消；
     * 2. 设置订单取消状态；
     * 3. 商品货品库存恢复；
     * 4. TODO 优惠券；
     * 5. TODO 团购活动。
     *
     * @param userId 用户ID
     * @param body   订单信息，{ orderId：xxx }
     * @return 取消订单操作结果
     */
    @Transactional
    public Object cancel(Integer userId, String body) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        if (orderId == null) {
            return ResponseUtil.badArgument();
        }

        LitemallOrder order = orderService.findById(orderId);
        if (order == null) {
            return ResponseUtil.badArgumentValue();
        }
        if (!order.getUserId().equals(userId)) {
            return ResponseUtil.badArgumentValue();
        }

        LocalDateTime preUpdateTime = order.getUpdateTime();

        // 检测是否能够取消
        OrderHandleOption handleOption = OrderUtil.build(order);
        if (!handleOption.isCancel()) {
            return ResponseUtil.fail(ORDER_INVALID_OPERATION, "订单不能取消");
        }

        // 设置订单已取消状态
        order.setOrderStatus(OrderUtil.STATUS_CANCEL);
        order.setEndTime(LocalDateTime.now());
        if (orderService.updateWithOptimisticLocker(order) == 0) {
            throw new RuntimeException("更新数据已失效");
        }

        // 商品货品数量增加
        List<LitemallOrderGoods> orderGoodsList = orderGoodsService.queryByOid(orderId);
        for (LitemallOrderGoods orderGoods : orderGoodsList) {
            Integer productId = orderGoods.getProductId();
            Short number = orderGoods.getNumber();
            if (productService.addStock(productId, number) == 0) {
                throw new RuntimeException("商品货品库存增加失败");
            }
        }
        String orderSn = order.getOrderSn();
        LitemallFlow flow = litemallFlowService.findByOrderSn(orderSn);
        if (flow != null) {
            LitemallUser user = userService.findById(userId);
            Integer integral = user.getUserIntegr() + Integer.valueOf(flow.getPaidIntegral());

            flow.setCreateTime(LocalDateTime.now());
            flow.setPaidMethod("获取");
            flow.setTotalIntegral(integral.toString());
            flow.setPaidIntegral(flow.getPaidIntegral());
            flow.setFlowNum(PayUtil.create_timestamp());
            flow.setUserId(userId.toString());
            litemallFlowService.add(flow);
            user.setUserIntegr(integral);
            userService.updateById(user);
        }

        return ResponseUtil.ok();
    }

    /**
     * 付款订单的预支付会话标识
     * <p>
     * 1. 检测当前订单是否能够付款
     * 2. 微信商户平台返回支付订单ID
     * 3. 设置订单付款状态
     *
     * @param userId  用户ID
     * @param orderId 订单信息，{ orderId：xxx }
     * @return 支付订单ID
     */
    @Transactional
    public Object prepay(Integer userId, BigDecimal price, Integer orderId, HttpServletRequest request) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        if (orderId == null) {
            return ResponseUtil.badArgument();
        }

        LitemallOrder order = orderService.findById(orderId);
        if (order == null) {
            return ResponseUtil.badArgumentValue();
        }
        if (!order.getUserId().equals(userId)) {
            return ResponseUtil.badArgumentValue();
        }

        LitemallUser user = userService.findById(userId);

        String productName = "SweetCity";
        int number = (int) ((Math.random() * 9) * 1000);//随机数
        String openId = user.getWeixinOpenid();
        WxOrderEntity orderEntity = new WxOrderEntity();

        orderEntity.setAppid(WxAuthorization.APP_ID);
        orderEntity.setMch_id(WxAuthorization.MCH_ID);
        orderEntity.setBody(productName);
        orderEntity.setOut_trade_no(order.getOrderSn());
        orderEntity.setDevice_info("WEB");
        orderEntity.setTrade_type("JSAPI");
//        orderEntity.setOut_trade_no(orderNumber);
        orderEntity.setTotal_fee(price.multiply(new BigDecimal(100)).intValue());
        orderEntity.setOpenid(openId);
        //接收支付结果的地址
        orderEntity.setNotify_url(WxAuthorization.NOTIFY_URL);
        //32位随机数(UUID去掉-就是32位的)
        String uuid = UUID.randomUUID().toString().replace("-", "");
        orderEntity.setNonce_str(uuid);
        //生成签名
        String sign = null;
        try {
            sign = PayUtil.generateSignature(orderEntity);
            System.out.println("预支付签名" + sign);
        } catch (Exception e) {
            e.printStackTrace();
        }
        orderEntity.setSign(sign);
        String xml = null;
        try {
            xml = WxAuthorization.sendPost("https://api.mch.weixin.qq.com/pay/unifiedorder", orderEntity);
            System.out.println("支付获取返回结果"+xml);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (UnrecoverableKeyException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        Map map = null;
        try {
            map = XmlUtil.doXMLParse(xml);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String return_code = (String) map.get("return_code");
        if (!return_code.equals("SUCCESS")) {
            xml = "";
        }
        return xml;
    }

    /**
     * 微信付款成功或失败回调接口
     * <p>
     * 1. 检测当前订单是否是付款状态;
     * 2. 设置订单付款成功状态相关信息;
     * 3. 响应微信商户平台.
     *
     * @param request  请求内容
     * @param response 响应内容
     * @return 操作结果
     */
    @Transactional
    public Object payNotify(HttpServletRequest request, HttpServletResponse response) throws Exception {
        System.out.println("进入回调方法");
        BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
        String line = null;
        StringBuilder sb = new StringBuilder();
        while ((line = br.readLine()) != null) {

            sb.append(line);
        }
        String notityXml = sb.toString();
        String resXml = "";
        Map map = XmlUtil.doXMLParse(notityXml);
        System.out.println(map);
        if (!map.get("result_code").toString().equals("SUCCESS")){
            System.out.println("失败"+notityXml);
            resXml = "<xml>" + "<return_code><![CDATA[FAIL]]></return_code>"
                    + "<return_msg><![CDATA[报文为空]]></return_msg>" + "</xml> ";
            return resXml;
        }
        String orderSn = map.get("out_trade_no").toString();
        String payId = map.get("transaction_id").toString();
        String total_fee = map.get("total_fee").toString();
        if (orderSn.substring(0, 1).equals("d")) {
            if (Double.parseDouble(BaseWxPayResult.fenToYuan(Integer.valueOf(map.get("total_fee").toString())))<200){
                List<LitemallCash> cashList = cashService.querySelectiveOrderNo(orderSn);
                LitemallCash cash = new LitemallCash();
                cash.setId(cashList.get(0).getId());
                cash.setStatus("0");
                cashService.updateById(cash);
                LitemallUser user = userService.findById(cashList.get(0).getUserId().intValue());
                user.setExperience((byte)1);
                userService.updateById(user);

                List<LitemallUserRelations> userRelationsList = userRelationsService.findByChildid(user.getId().toString());
                if (!StringUtils.isEmpty(userRelationsList)&&userRelationsList.size()!=0){
                    LitemallUser newuser =  userService.findById(userRelationsList.get(0).getUserid().intValue());
                    BigDecimal userPrice = BigDecimal.ONE.multiply(new BigDecimal("0.3"));
                    newuser.setUserPrice(userPrice.add(newuser.getUserPrice()));
                    userService.updateById(newuser);
                    LitemallCash litemallCash = new LitemallCash();
                    litemallCash.setUserId(userRelationsList.get(0).getUserid().intValue());
                    litemallCash.setStatus("0");
                    litemallCash.setRemBalance(newuser.getUserPrice());
                    litemallCash.setPaidAmount(userPrice);
                    litemallCash.setType("下级充值代理");
                    litemallCash.setCashNum(PayUtil.create_timestamp());
                    cashService.add(litemallCash);
                    List<LitemallUserRelations> newuserRelationsList = userRelationsService.findByChildid(newuser.getId().toString());
                    if (!StringUtils.isEmpty(newuserRelationsList)&&newuserRelationsList.size()!=0){
                        LitemallUser seconduser =  userService.findById(newuserRelationsList.get(0).getUserid().intValue());
                        BigDecimal newuserPrice = BigDecimal.ONE.multiply(new BigDecimal("0.2"));
                        seconduser.setUserPrice(newuserPrice.add(seconduser.getUserPrice()));
                        userService.updateById(seconduser);
                        LitemallCash litemallCash1 = new LitemallCash();
                        litemallCash1.setUserId(newuserRelationsList.get(0).getUserid().intValue());
                        litemallCash1.setStatus("0");
                        litemallCash1.setRemBalance(seconduser.getUserPrice());
                        litemallCash1.setPaidAmount(newuserPrice);
                        litemallCash1.setType("下级充值代理");
                        litemallCash1.setCashNum(PayUtil.create_timestamp());
                        cashService.add(litemallCash1);
                    }
                }

            }else {
                List<LitemallCash> cashList = cashService.querySelectiveOrderNo(orderSn);
                LitemallCash cash = new LitemallCash();
                cash.setId(cashList.get(0).getId());
                cash.setStatus("0");
                cashService.updateById(cash);

                LitemallUser userInfo = userService.findById(cashList.get(0).getUserId());
                userInfo.setId(cashList.get(0).getUserId());
                BigDecimal integer = cashList.get(0).getPaidAmount().add(BigDecimal.valueOf(userInfo.getUserIntegr()));
                userInfo.setUserIntegr(Integer.valueOf(integer.toString()));
                switch ( BaseWxPayResult.fenToYuan(Integer.valueOf(map.get("total_fee").toString()))){
                    case "10000.00":
                        userInfo.setUserLevel((byte)2);
                        break;
                    case "30000.00":
                        userInfo.setUserLevel((byte)1);
                        break;
                    case "90000.00":
                        userInfo.setUserLevel((byte)0);
                        break;
                }
                userService.updateById(userInfo);

                List<LitemallUserRelations> userRelationsList = userRelationsService.findByChildid(userInfo.getId().toString());

                if (!StringUtils.isEmpty(userRelationsList)&&userRelationsList.size()!=0){
                   LitemallUser user =  userService.findById(userRelationsList.get(0).getUserid().intValue());
                   BigDecimal userPrice = BigDecimal.ZERO;
                   switch (userInfo.getUserLevel()){
                       case 0:
                           userPrice = new BigDecimal("90000").multiply(new BigDecimal("0.30"));
                           break;
                       case 1:
                           userPrice = new BigDecimal("30000").multiply(new BigDecimal("0.30"));
                           break;
                       case 2:
                           userPrice = new BigDecimal("10000").multiply(new BigDecimal("0.30"));
                           break;
                   }
                   user.setUserPrice(userPrice);
                   userService.updateById(user);
                    LitemallCash litemallCash = new LitemallCash();
                    litemallCash.setUserId(userRelationsList.get(0).getUserid().intValue());
                    litemallCash.setStatus("0");
                    litemallCash.setRemBalance(user.getUserPrice());
                    litemallCash.setPaidAmount(userPrice);
                    litemallCash.setType("下级充值代理");
                    litemallCash.setCashNum(PayUtil.create_timestamp());
                    cashService.add(litemallCash);
                   List<LitemallUserRelations> newuserRelationsList = userRelationsService.findByChildid(user.getId().toString());

                   if (!StringUtils.isEmpty(newuserRelationsList)&&newuserRelationsList.size()!=0){
                        LitemallUser newuser =  userService.findById(userRelationsList.get(0).getUserid().intValue());
                        BigDecimal newuserPrice = BigDecimal.ZERO;
                        switch (userInfo.getUserLevel()){
                            case 0:
                                newuserPrice = new BigDecimal("90000").multiply(new BigDecimal("0.20"));
                                break;
                            case 1:
                                newuserPrice = new BigDecimal("30000").multiply(new BigDecimal("0.20"));
                                break;
                            case 2:
                                newuserPrice = new BigDecimal("10000").multiply(new BigDecimal("0.20"));
                                break;
                        }
                       newuser.setUserPrice(newuserPrice);
                        userService.updateById(newuser);
                       LitemallCash litemallCash1 = new LitemallCash();
                       litemallCash1.setUserId(userRelationsList.get(0).getUserid().intValue());
                       litemallCash1.setStatus("0");
                       litemallCash1.setRemBalance(newuser.getUserPrice());
                       litemallCash1.setPaidAmount(newuserPrice);
                       litemallCash1.setType("下级充值代理");
                       litemallCash1.setCashNum(PayUtil.create_timestamp());
                       cashService.add(litemallCash1);
                    }
                }
            }
        } else {
            // 分转化成元TotalFee
            String totalFee = BaseWxPayResult.fenToYuan(Integer.valueOf(map.get("total_fee").toString()));
            LitemallOrder order = orderService.findBySn(orderSn);
            if (order == null) {
                return WxPayNotifyResponse.fail("订单不存在 sn=" + orderSn);
            }

            // 检查这个订单是否已经处理过
            if (OrderUtil.isPayStatus(order) && order.getPayId() != null) {
                return WxPayNotifyResponse.success("订单已经处理成功!");
            }

            // 检查支付订单金额
            if (!totalFee.equals(order.getActualPrice().toString())) {
                return WxPayNotifyResponse.fail(order.getOrderSn() + " : 支付金额不符合 totalFee=" + totalFee);
            }

            order.setPayId(payId);
            order.setPayTime(LocalDateTime.now());
            order.setOrderStatus(OrderUtil.STATUS_PAY);

            List<LitemallUserRelations> userRelationsList = userRelationsService.findByChildid(order.getUserId().toString());
            System.out.println("userRelationsList++++++++++========="+userRelationsList);
            if (!StringUtils.isEmpty(userRelationsList)&&userRelationsList.size()!=0){
                LitemallUser newuser =  userService.findById(userRelationsList.get(0).getUserid().intValue());
                BigDecimal userPrice = new BigDecimal(totalFee).multiply(new BigDecimal("0.03"));
                System.out.println("userPrice================="+userPrice);
                newuser.setUserPrice(userPrice.add(newuser.getUserPrice()));
                userService.updateById(newuser);

                LitemallCash litemallCash = new LitemallCash();
                litemallCash.setUserId(userRelationsList.get(0).getUserid().intValue());
                litemallCash.setStatus("0");
                litemallCash.setRemBalance(newuser.getUserPrice());
                litemallCash.setPaidAmount(userPrice);
                litemallCash.setType("下级充值代理");
                litemallCash.setCashNum(PayUtil.create_timestamp());
                cashService.add(litemallCash);

                List<LitemallUserRelations> newuserRelationsList = userRelationsService.findByChildid(newuser.getId().toString());
                System.out.println("newuserRelationsList========="+newuserRelationsList);
                if (!StringUtils.isEmpty(newuserRelationsList)&&newuserRelationsList.size()!=0){
                    LitemallUser seconduser =  userService.findById(newuserRelationsList.get(0).getUserid().intValue());
                    BigDecimal newuserPrice = new BigDecimal(totalFee).multiply(new BigDecimal("0.02"));
                    seconduser.setUserPrice(newuserPrice.add(seconduser.getUserPrice()));
                    userService.updateById(seconduser);

                    LitemallCash litemallCash1 = new LitemallCash();
                    litemallCash1.setUserId(newuserRelationsList.get(0).getUserid().intValue());
                    litemallCash1.setStatus("0");
                    litemallCash1.setRemBalance(seconduser.getUserPrice());
                    litemallCash1.setPaidAmount(newuserPrice);
                    litemallCash1.setType("下级充值代理");
                    litemallCash1.setCashNum(PayUtil.create_timestamp());
                    cashService.add(litemallCash1);
                }
            }

            if (order.getIntegralPrice().compareTo(BigDecimal.ZERO) == 1) {
                LitemallFlow flow = new LitemallFlow();
                flow.setCreateTime(LocalDateTime.now());
                flow.setFlowNum(PayUtil.create_timestamp());
                flow.setPaidIntegral(order.getIntegralPrice().multiply(new BigDecimal(100)).toString());
                LitemallUser user = userService.findById(order.getUserId());
                flow.setTotalIntegral(user.getUserIntegr().toString());
                flow.setPaidMethod("消费");
                flow.setUserId(order.getUserId().toString());
                litemallFlowService.add(flow);
            }
            if (orderService.updateWithOptimisticLocker(order) == 0) {
                // 这里可能存在这样一个问题，用户支付和系统自动取消订单发生在同时
                // 如果数据库首先因为系统自动取消订单而更新了订单状态；
                // 此时用户支付完成回调这里也要更新数据库，而由于乐观锁机制这里的更新会失败
                // 因此，这里会重新读取数据库检查状态是否是订单自动取消，如果是则更新成支付状态。
                order = orderService.findBySn(orderSn);
                int updated = 0;
                if (OrderUtil.isAutoCancelStatus(order)) {
                    order.setPayId(payId);
                    order.setPayTime(LocalDateTime.now());
                    order.setOrderStatus(OrderUtil.STATUS_PAY);
                    updated = orderService.updateWithOptimisticLocker(order);
                }
            }
        }
        System.out.println("成功"+notityXml);
        //注意要判断微信支付重复回调，支付成功后微信会重复的进行回调
        //通知微信服务器已经支付成功
        resXml = "<xml>" + "<return_code><![CDATA[SUCCESS]]></return_code>"
                + "<return_msg><![CDATA[OK]]></return_msg>" + "</xml> ";
        return resXml;
    }

    /**
     * 订单申请退款
     * <p>
     * 1. 检测当前订单是否能够退款；
     * 2. 设置订单申请退款状态。
     *
     * @param userId 用户ID
     * @param body   订单信息，{ orderId：xxx }
     * @return 订单退款操作结果
     */
    public Object refund(Integer userId, String body) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        if (orderId == null) {
            return ResponseUtil.badArgument();
        }

        LitemallOrder order = orderService.findById(orderId);
        if (order == null) {
            return ResponseUtil.badArgument();
        }
        if (!order.getUserId().equals(userId)) {
            return ResponseUtil.badArgumentValue();
        }

        OrderHandleOption handleOption = OrderUtil.build(order);

        // 设置订单申请退款状态
        order.setOrderStatus(OrderUtil.STATUS_REFUND);

        if (orderService.updateWithOptimisticLocker(order) == 0) {
            return ResponseUtil.updatedDateExpired();
        }

//        //TODO 发送邮件和短信通知，这里采用异步发送
//        // 有用户申请退款，邮件通知运营人员
//        notifyService.notifyMail("退款申请", order.toString());

        return ResponseUtil.ok();
    }

    /**
     * 确认收货
     * <p>
     * 1. 检测当前订单是否能够确认收货；
     * 2. 设置订单确认收货状态。
     *
     * @param userId 用户ID
     * @param body   订单信息，{ orderId：xxx }
     * @return 订单操作结果
     */
    public Object confirm(Integer userId, String body) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        if (orderId == null) {
            return ResponseUtil.badArgument();
        }

        LitemallOrder order = orderService.findById(orderId);
        if (order == null) {
            return ResponseUtil.badArgument();
        }
        if (!order.getUserId().equals(userId)) {
            return ResponseUtil.badArgumentValue();
        }

        OrderHandleOption handleOption = OrderUtil.build(order);
        if (!handleOption.isConfirm()) {
            return ResponseUtil.fail(ORDER_INVALID_OPERATION, "订单不能确认收货");
        }

        Short comments = orderGoodsService.getComments(orderId);
        order.setComments(comments);

        order.setOrderStatus(OrderUtil.STATUS_CONFIRM);
        order.setConfirmTime(LocalDateTime.now());
        if (orderService.updateWithOptimisticLocker(order) == 0) {
            return ResponseUtil.updatedDateExpired();
        }
        return ResponseUtil.ok();
    }

    /**
     * 删除订单
     * <p>
     * 1. 检测当前订单是否可以删除；
     * 2. 删除订单。
     *
     * @param userId 用户ID
     * @param body   订单信息，{ orderId：xxx }
     * @return 订单操作结果
     */
    public Object delete(Integer userId, String body) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        Integer orderId = JacksonUtil.parseInteger(body, "orderId");
        if (orderId == null) {
            return ResponseUtil.badArgument();
        }

        LitemallOrder order = orderService.findById(orderId);
        if (order == null) {
            return ResponseUtil.badArgument();
        }
        if (!order.getUserId().equals(userId)) {
            return ResponseUtil.badArgumentValue();
        }

        OrderHandleOption handleOption = OrderUtil.build(order);
        if (!handleOption.isDelete()) {
            return ResponseUtil.fail(ORDER_INVALID_OPERATION, "订单不能删除");
        }

        // 订单order_status没有字段用于标识删除
        // 而是存在专门的delete字段表示是否删除
        orderService.deleteById(orderId);

        return ResponseUtil.ok();
    }

    /**
     * 待评价订单商品信息
     *
     * @param userId  用户ID
     * @param orderId 订单ID
     * @param goodsId 商品ID
     * @return 待评价订单商品信息
     */
    public Object goods(Integer userId, Integer orderId, Integer goodsId) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }

        List<LitemallOrderGoods> orderGoodsList = orderGoodsService.findByOidAndGid(orderId, goodsId);
        int size = orderGoodsList.size();

        Assert.state(size < 2, "存在多个符合条件的订单商品");

        if (size == 0) {
            return ResponseUtil.badArgumentValue();
        }

        LitemallOrderGoods orderGoods = orderGoodsList.get(0);
        return ResponseUtil.ok(orderGoods);
    }

    /**
     * 评价订单商品
     * <p>
     * 确认商品收货或者系统自动确认商品收货后7天内可以评价，过期不能评价。
     *
     * @param userId 用户ID
     * @param body   订单信息，{ orderId：xxx }
     * @return 订单操作结果
     */
    public Object comment(Integer userId, String body) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }

        Integer orderGoodsId = JacksonUtil.parseInteger(body, "orderGoodsId");

        if (orderGoodsId == null) {
            return ResponseUtil.badArgument();
        }

        LitemallOrderGoods orderGoods = orderGoodsService.findById(orderGoodsId);
        if (orderGoods == null) {
            return ResponseUtil.badArgumentValue();
        }

        Integer orderId = orderGoods.getOrderId();
        LitemallOrder order = orderService.findById(orderId);
        if (order == null) {
            return ResponseUtil.badArgumentValue();
        }


        String content = JacksonUtil.parseString(body, "content");
        if (StringUtils.isEmpty(content)) {
            return ResponseUtil.badArgumentValue();
        }
        Boolean hasPicture = true;
        List<String> picUrls = JacksonUtil.parseStringList(body, "picUrls");
        if (picUrls == null) {
            hasPicture = false;
        }

        // 1. 创建评价
        LitemallComment comment = new LitemallComment();
        comment.setUserId(userId);
        comment.setType((byte) 0);
        comment.setValueId(orderGoods.getGoodsId());
        comment.setContent(content);
        comment.setHasPicture(hasPicture);
        comment.setPicUrls(picUrls.toArray(new String[]{}));
        commentService.save(comment);

        // 2. 更新订单商品的评价列表
        orderGoods.setComment(comment.getId());
        orderGoodsService.updateById(orderGoods);

        // 3. 更新订单中未评价的订单商品可评价数量
        Short commentCount = order.getComments();
        if (commentCount > 0) {
            commentCount--;
        }

        order.setComments(commentCount);
        order.setOrderStatus((short) 502);
        orderService.updateWithOptimisticLocker(order);

        return ResponseUtil.ok();
    }

}