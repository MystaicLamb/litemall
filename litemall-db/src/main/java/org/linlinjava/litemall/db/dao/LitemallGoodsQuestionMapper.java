package org.linlinjava.litemall.db.dao;

import org.linlinjava.litemall.db.domain.LitemallGoodsQuestion;
import org.linlinjava.litemall.db.domain.LitemallGoodsQuestionExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface LitemallGoodsQuestionMapper {
    long countByExample(LitemallGoodsQuestionExample example);

    int deleteByExample(LitemallGoodsQuestionExample example);

    int deleteByPrimaryKey(Integer id);

    int insert(LitemallGoodsQuestion record);

    int insertSelective(LitemallGoodsQuestion record);

    List<LitemallGoodsQuestion> selectByExample(LitemallGoodsQuestionExample example);

    LitemallGoodsQuestion selectByPrimaryKey(Integer id);

    int updateByExampleSelective(@Param("record") LitemallGoodsQuestion record, @Param("example") LitemallGoodsQuestionExample example);

    int updateByExample(@Param("record") LitemallGoodsQuestion record, @Param("example") LitemallGoodsQuestionExample example);

    int updateByPrimaryKeySelective(LitemallGoodsQuestion record);

    int updateByPrimaryKey(LitemallGoodsQuestion record);
}