package org.linlinjava.litemall.db.service;

import com.github.pagehelper.PageHelper;
import org.linlinjava.litemall.db.dao.LitemallReviewMapper;
import org.linlinjava.litemall.db.domain.LitemallReview;
import org.linlinjava.litemall.db.domain.LitemallReviewExample;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class LitemallReviewService {
    @Resource
    private LitemallReviewMapper reviewMapper;

    public List<LitemallReview> queryGoodsByGid(Integer goodsId, Integer page, Integer size) {
        LitemallReviewExample example = new LitemallReviewExample();
        example.or().andGoodsIdEqualTo(goodsId).andDeletedEqualTo(false);
        example.setOrderByClause("add_time desc");
        PageHelper.startPage(page, size);
        return reviewMapper.selectByExample(example);
    }

    public List<LitemallReview> querySelective(String userId, String goodsId, Integer page, Integer size, String sort, String order) {
        LitemallReviewExample example = new LitemallReviewExample();
        LitemallReviewExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(userId)) {
            criteria.andUserIdEqualTo(Integer.valueOf(userId));
        }
        if (!StringUtils.isEmpty(goodsId)) {
            criteria.andGoodsIdEqualTo(Integer.valueOf(goodsId));
        }
        criteria.andDeletedEqualTo(false);

        if (!StringUtils.isEmpty(sort) && !StringUtils.isEmpty(order)) {
            example.setOrderByClause(sort + " " + order);
        }

        PageHelper.startPage(page, size);
        return reviewMapper.selectByExample(example);
    }

    public int countSelective(String userId, String goodsId, Integer page, Integer size, String sort, String order) {
        LitemallReviewExample example = new LitemallReviewExample();
        LitemallReviewExample.Criteria criteria = example.createCriteria();

        if (!StringUtils.isEmpty(userId)) {
            criteria.andUserIdEqualTo(Integer.valueOf(userId));
        }
        if (!StringUtils.isEmpty(goodsId)) {
            criteria.andGoodsIdEqualTo(Integer.valueOf(goodsId));
        }
        criteria.andDeletedEqualTo(false);

        return (int) reviewMapper.countByExample(example);
    }

    public int count(Integer goodsId) {
        LitemallReviewExample example = new LitemallReviewExample();
        example.or().andGoodsIdEqualTo(goodsId).andDeletedEqualTo(false);
        return (int) reviewMapper.countByExample(example);
    }

    public void deleteById(Integer id) {
        reviewMapper.logicalDeleteByPrimaryKey(id);
    }

    public void add(LitemallReview review) {
        review.setAddTime(LocalDateTime.now());
        review.setUpdateTime(LocalDateTime.now());
        reviewMapper.insertSelective(review);
    }

    public LitemallReview findById(Integer id) {
        return reviewMapper.selectByPrimaryKey(id);
    }

    public int updateById(LitemallReview review) {
        review.setUpdateTime(LocalDateTime.now());
        return reviewMapper.updateByPrimaryKeySelective(review);
    }
}