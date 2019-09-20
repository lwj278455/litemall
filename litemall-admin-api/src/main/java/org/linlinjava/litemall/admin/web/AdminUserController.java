package org.linlinjava.litemall.admin.web;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.core.util.JacksonUtil;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.core.validator.Order;
import org.linlinjava.litemall.core.validator.Sort;
import org.linlinjava.litemall.db.domain.LitemallAudit;
import org.linlinjava.litemall.db.domain.LitemallUser;
import org.linlinjava.litemall.db.domain.LitemallUserRelations;
import org.linlinjava.litemall.db.service.LitemallAuditService;
import org.linlinjava.litemall.db.service.LitemallUserRelationsService;
import org.linlinjava.litemall.db.service.LitemallUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/admin/user")
@Validated
public class AdminUserController {
    private final Log logger = LogFactory.getLog(AdminUserController.class);

    @Autowired
    private LitemallUserService userService;
    @Autowired
    private LitemallAuditService auditService;
    @Autowired
    private LitemallUserRelationsService userRelationsService;

    @RequiresPermissions("admin:user:list")
    @RequiresPermissionsDesc(menu = {"用户管理", "会员管理"}, button = "查询")
    @GetMapping("/list")
    public Object list(String username, String mobile,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
        List<LitemallUser> userList = userService.querySelective(username, mobile, page, limit, sort, order);
//        for (LitemallUser user:userList){
//            String name = new String(Base64.getDecoder().decode(user.getNickname()), StandardCharsets.UTF_8);
//            user.setNickname(name);
//        }
        return ResponseUtil.okList(userList);
    }

    @RequiresPermissions("admin:user:userlist")
    @RequiresPermissionsDesc(menu = {"用户管理", "会员管理"}, button = "查询")
    @GetMapping("/userlist")
    public Object userlist(String userId,
                           @RequestParam(defaultValue = "1") Integer page,
                           @RequestParam(defaultValue = "10") Integer limit,
                           @Sort @RequestParam(defaultValue = "add_time") String sort,
                           @Order @RequestParam(defaultValue = "desc") String order) {
        List<LitemallUserRelations> userRelationsList = userRelationsService.findByUserId(userId);
        List list = new ArrayList();
        for (int i = 0; i < userRelationsList.size(); i++) {
            list.add(userRelationsList.get(i).getChildid());
        }

        List<LitemallUser> userList  =   userService.queryUserId(list,page,limit,sort,order);

        return ResponseUtil.okList(userList);
    }

    @RequiresPermissions("admin:user:Auditlist")
    @RequiresPermissionsDesc(menu = {"用户管理", "审核管理"}, button = "查询")
    @GetMapping("/Auditlist")
    public Object Auditlist(String username,
                            @RequestParam(defaultValue = "1") Integer page,
                            @RequestParam(defaultValue = "10") Integer limit) {
        return ResponseUtil.okList(auditService.findByuserName(username));
    }

    @RequiresPermissions("admin:user:updateAudit")
    @RequiresPermissionsDesc(menu = {"用户管理", "审核管理"}, button = "修改")
    @PostMapping("/updateAudit")
    public Object updateAudit(@RequestBody String body) {
        String auditId = JacksonUtil.parseString(body, "id");
        boolean status = Boolean.parseBoolean(JacksonUtil.parseString(body, "auditStatus"));
        LitemallAudit litemallAudit = auditService.findById(Integer.valueOf(auditId));
        System.out.println(status);
        if (status) {
            litemallAudit.setAuditStatus((byte) 2);
        } else {
            litemallAudit.setAuditStatus((byte) 3);
        }
        auditService.update(litemallAudit);
        LitemallUser user = userService.findById(litemallAudit.getUserId());
        user.setUsername(litemallAudit.getUserName());
        user.setAlipayNumber(litemallAudit.getAlipayNumber());
        userService.updateById(user);
        return ResponseUtil.ok();
    }
}
