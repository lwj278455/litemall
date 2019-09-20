package org.linlinjava.litemall.admin.web;

import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.linlinjava.litemall.admin.annotation.RequiresPermissionsDesc;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.LitemallContract;
import org.linlinjava.litemall.db.service.LitemallContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/contract")
@Validated
public class AdminContractController {
    @Autowired
    private LitemallContractService contractService;

    @RequiresPermissions("admin:contract:list")
    @RequiresPermissionsDesc(menu = {"合同管理", "合同管理"}, button = "查询")
    @GetMapping("/list")
    public Object findList() {
        return ResponseUtil.okList(contractService.findByList());
    }

    @RequiresPermissions("admin:contract:update")
    @RequiresPermissionsDesc(menu = {"合同管理", "合同管理"}, button = "修改")
    @PostMapping ("/update")
    public Object update(@RequestBody LitemallContract contract) {
        return ResponseUtil.ok(contractService.updateByStatus(contract));
    }
}
