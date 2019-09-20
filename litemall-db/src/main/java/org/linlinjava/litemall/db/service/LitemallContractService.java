package org.linlinjava.litemall.db.service;

import org.linlinjava.litemall.db.dao.LitemallContractMapper;
import org.linlinjava.litemall.db.domain.LitemallContract;
import org.linlinjava.litemall.db.domain.LitemallContractExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.List;

@Service
public class LitemallContractService {
    @Resource
    private LitemallContractMapper contractMapper;

    public LitemallContract findByStatus(Integer status){
        LitemallContractExample example = new LitemallContractExample();
        LitemallContractExample.Criteria criteria =   example.createCriteria();
        if (!StringUtils.isEmpty(status)){
            criteria.andStatusEqualTo(status.byteValue());
        }
        return contractMapper.selectOneByExample(example);
    }
    public List<LitemallContract> findByList(){
        LitemallContractExample example = new LitemallContractExample();
        return contractMapper.selectByExampleWithBLOBs(example);
    }


    public int updateByStatus(LitemallContract contract){

        return contractMapper.updateByPrimaryKeySelective(contract);
    }
}
