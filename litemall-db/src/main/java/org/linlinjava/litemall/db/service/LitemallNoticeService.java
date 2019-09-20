package org.linlinjava.litemall.db.service;

import org.linlinjava.litemall.db.dao.LitemallNoticeMapper;
import org.linlinjava.litemall.db.domain.LitemallNotice;
import org.linlinjava.litemall.db.domain.LitemallNoticeExample;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
@Service
public class LitemallNoticeService {
    @Resource
    private LitemallNoticeMapper litemallNoticeMapper;


    public LitemallNotice list(){
        LitemallNoticeExample example = new LitemallNoticeExample();
        LitemallNoticeExample.Criteria criteria =   example.createCriteria();
        return litemallNoticeMapper.selectOneByExample(example);
    }

}
