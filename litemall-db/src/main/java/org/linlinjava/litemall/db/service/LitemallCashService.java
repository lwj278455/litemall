package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallCashMapper;
import org.linlinjava.litemall.db.dao.LitemallFlowMapper;
import org.linlinjava.litemall.db.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallCashService {
    @Resource
    private LitemallCashMapper litemallCashMapper;


    public int add(LitemallCash litemallCash) {
        litemallCash.setCreated(LocalDateTime.now());
        return litemallCashMapper.insertSelective(litemallCash);
    }

    public List<LitemallCash> querySelective(Integer userId, Integer page, Integer limit, String sort, String order) {
        LitemallCashExample example = new LitemallCashExample();
        LitemallCashExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(userId)) {
            criteria.andUserIdEqualTo(userId);
        }

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        return litemallCashMapper.selectByExample(example);
    }
    public int updateById(LitemallCash cash) {
        return litemallCashMapper.updateByPrimaryKeySelective(cash);
    }
    public LitemallCash findById(Long id) {

        return litemallCashMapper.selectByPrimaryKey(id);
    }
    public List<LitemallCash> querySelectiveOrderNo(String orderNo) {
        LitemallCashExample example = new LitemallCashExample();
        LitemallCashExample.Criteria criteria = example.createCriteria();
        criteria.andCashNumEqualTo(orderNo);
        return litemallCashMapper.selectByExample(example);
    }
}
