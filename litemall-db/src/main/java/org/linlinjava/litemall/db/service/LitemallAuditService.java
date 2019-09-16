package org.linlinjava.litemall.db.service;

import org.linlinjava.litemall.db.dao.LitemallAuditMapper;
import org.linlinjava.litemall.db.domain.LitemallAudit;
import org.linlinjava.litemall.db.domain.LitemallAuditExample;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
@Service
public class LitemallAuditService {
    @Resource
    private LitemallAuditMapper auditMapper;

    public LitemallAudit findByUserId(Integer userId){
        LitemallAuditExample example = new LitemallAuditExample();
        LitemallAuditExample.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(userId)){
            criteria.andUserIdEqualTo(userId);
        }
        return auditMapper.selectOneByExample(example);
    }

    public int add(LitemallAudit audit){
        return auditMapper.insert(audit);
    }

    public int update(LitemallAudit audit){
       return  auditMapper.updateByPrimaryKeySelective(audit);
    }
}
