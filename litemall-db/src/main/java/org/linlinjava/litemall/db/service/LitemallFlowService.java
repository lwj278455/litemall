package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallFlowMapper;
import org.linlinjava.litemall.db.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallFlowService {
    @Resource
    private LitemallFlowMapper litemallFlowMapper;


    public int add(LitemallFlow litemallFlow) {
        litemallFlow.setCreateTime(LocalDateTime.now());
        return litemallFlowMapper.insertSelective(litemallFlow);
    }

    public List<LitemallFlow> querySelective(Integer userId, Integer page, Integer limit, String sort, String order) {
        LitemallFlowExample example = new LitemallFlowExample();
        LitemallFlowExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(userId)) {
            criteria.andUserIdEqualTo(String.valueOf(userId));
        }

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, limit);
        return litemallFlowMapper.selectByExample(example);
    }
    public LitemallFlow findByOrderSn(String orderSn){
        LitemallFlowExample example = new LitemallFlowExample();
        LitemallFlowExample.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(orderSn)){
            criteria.andFlowNumEqualTo(orderSn);
        }
        return litemallFlowMapper.selectOneByExample(example);
    }
    public List<LitemallFlow> findByUserId(Integer userId,Integer page,Integer limit){
        LitemallFlowExample example = new LitemallFlowExample();
        LitemallFlowExample.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(userId)){
            criteria.andUserIdEqualTo(userId.toString());
        }
        PageHelper.startPage(page,limit);
        return litemallFlowMapper.selectByExample(example);
    }
}
