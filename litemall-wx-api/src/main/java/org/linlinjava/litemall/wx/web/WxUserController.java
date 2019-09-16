package org.linlinjava.litemall.wx.web;

import com.alipay.api.AlipayApiException;
import io.swagger.models.auth.In;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.core.storage.StorageService;
import org.linlinjava.litemall.core.util.JacksonUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.*;
import org.linlinjava.litemall.db.service.*;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.linlinjava.litemall.wx.util.AlipayFund;
import org.linlinjava.litemall.wx.util.BASE64;
import org.linlinjava.litemall.wx.util.PayUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 用户服务
 */
@RestController
@RequestMapping("/wx/user")
@Validated
public class WxUserController {
    private final Log logger = LogFactory.getLog(WxUserController.class);

    @Autowired
    private StorageService storageService;

    @Autowired
    private LitemallUserService userService;
    @Autowired
    private LitemallAuditService auditService;
    @Autowired
    private LitemallCashService cashService;
    @Autowired
    private LitemallFlowService flowService;

    /**
     * 用户个人页面数据
     * <p>
     * 目前是用户订单统计信息
     *
     * @param userId 用户ID
     * @return 用户个人页面数据
     */
    @GetMapping("index")
    public Object list(@LoginUser Integer userId) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        Map<Object, Object> data = new HashMap<Object, Object>();
        LitemallUser user = userService.findById(userId);
        String name = new String(Base64.getDecoder().decode(user.getNickname()), StandardCharsets.UTF_8);
        user.setNickname(name);
        data.put("user", user);
        return ResponseUtil.ok(data);
    }

    /**
     * 绑定支付宝账号
     *
     * @param userId
     * @param body
     * @return
     */
    @PostMapping("/autonym")
    public Object autonym(@LoginUser Integer userId, @RequestBody String body) {
        String alipay_number = JacksonUtil.parseString(body, "alipay_number");
        String user_name = JacksonUtil.parseString(body, "user_name");
        List<String> picUrls = JacksonUtil.parseStringList(body, "picUrls");
        if (StringUtils.isEmpty(alipay_number) || StringUtils.isEmpty(user_name) || StringUtils.isEmpty(picUrls)) {
            return ResponseUtil.badArgumentValue();
        }
        LitemallAudit litemallAudit = auditService.findByUserId(userId);
        if (litemallAudit != null) {
            litemallAudit.setAlipayNumber(alipay_number);
            litemallAudit.setUserName(user_name);
            litemallAudit.setIdentity(picUrls.toArray(new String[]{}));
            litemallAudit.setUserId(userId);
            litemallAudit.setAuditStatus((byte) 1);
            auditService.update(litemallAudit);
            return ResponseUtil.ok(litemallAudit);
        }
        litemallAudit.setAlipayNumber(alipay_number);
        litemallAudit.setUserName(user_name);
        litemallAudit.setIdentity(picUrls.toArray(new String[]{}));
        litemallAudit.setUserId(userId);
        auditService.add(litemallAudit);
        return ResponseUtil.ok(litemallAudit);
    }

    /**
     * 查询出支付宝信息
     *
     * @param userId
     * @return
     */
    @GetMapping("identity")
    public Object findIdentity(@LoginUser Integer userId) {
        if (StringUtils.isEmpty(userId)) {
            return ResponseUtil.badArgumentValue();
        }
        return ResponseUtil.ok(auditService.findByUserId(userId));
    }

    /**
     * 提现
     *
     * @param userId
     * @param body
     * @return
     */
    @PostMapping("alipayfund")
    public Object alipayfund(@LoginUser Integer userId, @RequestBody String body) {
        String ampunt = JacksonUtil.parseString(body, "ampunt");
        LitemallUser user = userService.findById(userId);
        if (StringUtils.isEmpty(user.getAlipayNumber()) || StringUtils.isEmpty(user.getUsername())) {
            return ResponseUtil.fail(201, "未实名制，无法提现");
        }
        LitemallAudit audit = auditService.findByUserId(userId);
        int status = audit.getAuditStatus();
        if (status == 1) {
            return ResponseUtil.fail(101, "待审核中");
        }
        if (status == 3) {
            return ResponseUtil.fail(103, "审核未通过");
        }
        try {
            if (new BigDecimal(ampunt).compareTo(user.getUserPrice()) == -1) {
                AlipayFund.aliPay(PayUtil.create_timestamp(), user.getAlipayNumber(), ampunt, user.getUsername());
                LitemallCash cash = new LitemallCash();
                cash.setType("提现");
                cash.setPaidAmount(new BigDecimal(ampunt));
                cash.setAlipayNumber(user.getAlipayNumber());
                cash.setStatus("0");
                cash.setCashNum(PayUtil.create_timestamp());
                cashService.add(cash);
                return ResponseUtil.ok();
            }
            return ResponseUtil.fail(202, "输入金额大于现有金额");
        } catch (AlipayApiException e) {
            e.printStackTrace();
            return ResponseUtil.serious();
        }
    }


    /**
     * 提交验证图片
     *
     * @param files
     * @return
     * @throws IOException
     */
    @PostMapping("/upload")
    public Object upload(@RequestParam("files") MultipartFile files) throws IOException {
        String originalFilename = files.getOriginalFilename();
        String url = storageService.findUrl(files.getInputStream(), files.getSize(), files.getContentType(), originalFilename);
        return ResponseUtil.ok(url);
    }


    /**
     * 查看消费记录
     * userId 用户名称
     *
     * @return
     */
    @GetMapping("/personal")
    public Object personalLog(@LoginUser Integer userId) {
        if (StringUtils.isEmpty(userId)) {
            return ResponseUtil.badArgumentValue();
        }
        List<LitemallCash> cashList = cashService.findByUserId(userId);
        LitemallUser user = userService.findById(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("prcie", user.getUserPrice());
        map.put("cashList", cashList);
        return ResponseUtil.ok(map);
    }

    /**
     * 查看积分记录
     * userId 用户名称
     *
     * @return
     */
    @GetMapping("/integral")
    public Object integralLog(@LoginUser Integer userId) {
        if (StringUtils.isEmpty(userId)) {
            return ResponseUtil.badArgumentValue();
        }
        List<LitemallFlow> flowList = flowService.findByUserId(userId);
        LitemallUser user = userService.findById(userId);
        Map<String, Object> map = new HashMap<>();
        map.put("integral", user.getUserIntegr());
        map.put("flowList", flowList);
        return ResponseUtil.ok(map);
    }

    /**
     * 分享
     *
     * @param userId
     * @return
     */
    @GetMapping("share")
    public Object share(@LoginUser Integer userId) {
        LitemallUser user = userService.findById(userId);
        if (user != null) {
            return ResponseUtil.badArgumentValue();
        }
        return ResponseUtil.ok(user.getTicket());
    }

}