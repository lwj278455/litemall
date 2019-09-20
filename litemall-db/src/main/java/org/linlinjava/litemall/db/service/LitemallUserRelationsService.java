package org.linlinjava.litemall.db.service;

import org.linlinjava.litemall.db.dao.LitemallUserRelationsMapper;
import org.linlinjava.litemall.db.domain.LitemallUserRelations;
import org.linlinjava.litemall.db.domain.LitemallUserRelationsExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LitemallUserRelationsService {
    @Resource
    private LitemallUserRelationsMapper userRelationsMapper;

    public List<LitemallUserRelations> findByChildid(String childid) {
        LitemallUserRelationsExample example = new LitemallUserRelationsExample();
        LitemallUserRelationsExample.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(childid)){
            criteria.andChildidEqualTo(Long.valueOf(childid));
        }
        return userRelationsMapper.selectByExample(example);
    }

    public List<LitemallUserRelations> findByUserId(String userId) {
        LitemallUserRelationsExample example = new LitemallUserRelationsExample();
        LitemallUserRelationsExample.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(userId)){
            criteria.andUseridEqualTo(Long.valueOf(userId));
        }
        return userRelationsMapper.selectByExample(example);
    }
    public void add(LitemallUserRelations userRelations){
        userRelationsMapper.insertSelective(userRelations);
    }

    public List<LitemallUserRelations> findBySuperior(Long childid,Long fxLevel) {
        LitemallUserRelationsExample example = new LitemallUserRelationsExample();
        LitemallUserRelationsExample.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(childid)){
            criteria.andChildidEqualTo(Long.valueOf(childid));
        }
        if (!StringUtils.isEmpty(fxLevel)){
            criteria.andFxlevelEqualTo(Long.valueOf(fxLevel));
        }
        List<LitemallUserRelations> userRelationsList = userRelationsMapper.selectByExample(example);

        return userRelationsList;
    }
}
