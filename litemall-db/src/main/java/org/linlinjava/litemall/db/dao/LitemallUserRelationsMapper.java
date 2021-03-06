package org.linlinjava.litemall.db.dao;

import java.util.List;
import org.apache.ibatis.annotations.Param;
import org.linlinjava.litemall.db.domain.LitemallUserRelations;
import org.linlinjava.litemall.db.domain.LitemallUserRelationsExample;

public interface LitemallUserRelationsMapper {
    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_user_relations
     *
     * @mbg.generated
     */
    long countByExample(LitemallUserRelationsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_user_relations
     *
     * @mbg.generated
     */
    int deleteByExample(LitemallUserRelationsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_user_relations
     *
     * @mbg.generated
     */
    int deleteByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_user_relations
     *
     * @mbg.generated
     */
    int insert(LitemallUserRelations record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_user_relations
     *
     * @mbg.generated
     */
    int insertSelective(LitemallUserRelations record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_user_relations
     *
     * @mbg.generated
     */
    LitemallUserRelations selectOneByExample(LitemallUserRelationsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_user_relations
     *
     * @mbg.generated
     */
    LitemallUserRelations selectOneByExampleSelective(@Param("example") LitemallUserRelationsExample example, @Param("selective") LitemallUserRelations.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_user_relations
     *
     * @mbg.generated
     */
    List<LitemallUserRelations> selectByExampleSelective(@Param("example") LitemallUserRelationsExample example, @Param("selective") LitemallUserRelations.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_user_relations
     *
     * @mbg.generated
     */
    List<LitemallUserRelations> selectByExample(LitemallUserRelationsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_user_relations
     *
     * @mbg.generated
     */
    LitemallUserRelations selectByPrimaryKeySelective(@Param("id") Long id, @Param("selective") LitemallUserRelations.Column ... selective);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_user_relations
     *
     * @mbg.generated
     */
    LitemallUserRelations selectByPrimaryKey(Long id);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_user_relations
     *
     * @mbg.generated
     */
    int updateByExampleSelective(@Param("record") LitemallUserRelations record, @Param("example") LitemallUserRelationsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_user_relations
     *
     * @mbg.generated
     */
    int updateByExample(@Param("record") LitemallUserRelations record, @Param("example") LitemallUserRelationsExample example);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_user_relations
     *
     * @mbg.generated
     */
    int updateByPrimaryKeySelective(LitemallUserRelations record);

    /**
     * This method was generated by MyBatis Generator.
     * This method corresponds to the database table litemall_user_relations
     *
     * @mbg.generated
     */
    int updateByPrimaryKey(LitemallUserRelations record);
}