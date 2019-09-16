package org.linlinjava.litemall.db.service;

import org.linlinjava.litemall.db.dao.LitemallUserRelationsMapper;
import org.linlinjava.litemall.db.domain.LitemallUserRelations;
import org.linlinjava.litemall.db.domain.LitemallUserRelationsExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class LitemallUserRelationsService {
    private LitemallUserRelationsMapper userRelationsMapper;

    public List<LitemallUserRelations> findByChildid(String childid) {
        LitemallUserRelationsExample example = new LitemallUserRelationsExample();
        LitemallUserRelationsExample.Criteria criteria = example.createCriteria();
        if (!StringUtils.isEmpty(childid)){
            criteria.andChildidEqualTo(Long.valueOf(childid));
        }
        List<LitemallUserRelations> userRelationsList = userRelationsMapper.selectByExample(example);

        return userRelationsList;
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
