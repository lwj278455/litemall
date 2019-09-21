package org.linlinjava.litemall.db.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.linlinjava.litemall.db.domain.LitemallCash;
import org.linlinjava.litemall.db.domain.LitemallCashExample;

public interface LitemallCashMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_cash
     *
     * @mbg.generated
     */
    long countByExample(LitemallCashExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_cash
     *
     * @mbg.generated
     */
    int deleteByExample(LitemallCashExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_cash
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_cash
     *
     * @mbg.generated
     */
    int insert(LitemallCash record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_cash
     *
     * @mbg.generated
     */
    int insertSelective(LitemallCash record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_cash
     *
     * @mbg.generated
     */
    LitemallCash selectOneByExample(LitemallCashExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_cash
     *
     * @mbg.generated
     */
    LitemallCash selectOneByExampleSelective(@Param("example") LitemallCashExample example, @Param("selective") LitemallCash.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_cash
     *
     * @mbg.generated
     */
    List<LitemallCash> selectByExampleSelective(@Param("example") LitemallCashExample example, @Param("selective") LitemallCash.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_cash
     *
     * @mbg.generated
     */
    List<LitemallCash> selectByExample(LitemallCashExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_cash
     *
     * @mbg.generated
     */
    LitemallCash selectByPrimaryKeySelective(@Param("id") Long id, @Param("selective") LitemallCash.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_cash
     *
     * @mbg.generated
     */
    LitemallCash selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_cash
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") LitemallCash record, @Param("example") LitemallCashExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_cash
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") LitemallCash record, @Param("example") LitemallCashExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_cash
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(LitemallCash record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_cash
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(LitemallCash record);
}