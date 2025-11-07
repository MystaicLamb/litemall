package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallGoodsQuestionMapper;
import org.linlinjava.litemall.db.domain.LitemallGoodsQuestion;
import org.linlinjava.litemall.db.domain.LitemallGoodsQuestionExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallGoodsQuestionService {
    @Resource
    private LitemallGoodsQuestionMapper goodsQuestionMapper;

    public List<LitemallGoodsQuestion> queryByGoodsId(Integer goodsId, int offset, int limit) {
        LitemallGoodsQuestionExample example = new LitemallGoodsQuestionExample();
        example.setOrderByClause(LitemallGoodsQuestion.Column.addTime.desc());
        example.or().andGoodsIdEqualTo(goodsId).andDeletedEqualTo(false);
        PageHelper.startPage(offset, limit);
        return goodsQuestionMapper.selectByExample(example);
    }

    public int countByGoodsId(Integer goodsId) {
        LitemallGoodsQuestionExample example = new LitemallGoodsQuestionExample();
        example.or().andGoodsIdEqualTo(goodsId).andDeletedEqualTo(false);
        return (int) goodsQuestionMapper.countByExample(example);
    }

    public int save(LitemallGoodsQuestion goodsQuestion) {
        goodsQuestion.setAddTime(LocalDateTime.now());
        goodsQuestion.setUpdateTime(LocalDateTime.now());
        return goodsQuestionMapper.insertSelective(goodsQuestion);
    }

    public int updateAnswer(Integer id, String answer, Integer answerUserId) {
        LitemallGoodsQuestion goodsQuestion = new LitemallGoodsQuestion();
        goodsQuestion.setId(id);
        goodsQuestion.setAnswer(answer);
        goodsQuestion.setAnswerUserId(answerUserId);
        goodsQuestion.setAnswerTime(LocalDateTime.now());
        goodsQuestion.setUpdateTime(LocalDateTime.now());
        return goodsQuestionMapper.updateByPrimaryKeySelective(goodsQuestion);
    }

    public LitemallGoodsQuestion findById(Integer id) {
        return goodsQuestionMapper.selectByPrimaryKey(id);
    }

    public void deleteById(Integer id) {
        goodsQuestionMapper.logicalDeleteByPrimaryKey(id);
    }
}