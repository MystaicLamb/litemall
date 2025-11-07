package org.linlinjava.litemall.wx.web;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.LitemallReview;
import org.linlinjava.litemall.db.service.LitemallOrderGoodsService;
import org.linlinjava.litemall.db.service.LitemallReviewService;
import org.linlinjava.litemall.db.service.LitemallGoodsService;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户评价服务
 */
@RestController
@RequestMapping("/wx/review")
@Validated
public class WxReviewController {
    private final Log logger = LogFactory.getLog(WxReviewController.class);

    @Autowired
    private LitemallReviewService reviewService;
    @Autowired
    private LitemallGoodsService goodsService;
    @Autowired
    private LitemallOrderGoodsService orderGoodsService;

    private Object validate(LitemallReview review) {
        String content = review.getContent();
        if (StringUtils.isEmpty(content)) {
            return ResponseUtil.badArgument();
        }

        Short star = review.getStar();
        if (star == null) {
            return ResponseUtil.badArgument();
        }
        if (star < 1 || star > 5) {
            return ResponseUtil.badArgumentValue();
        }

        Integer goodsId = review.getGoodsId();
        if (goodsId == null) {
            return ResponseUtil.badArgument();
        }
        if (goodsService.findById(goodsId) == null) {
            return ResponseUtil.badArgumentValue();
        }

        Integer orderGoodsId = review.getOrderGoodsId();
        if (orderGoodsId == null) {
            return ResponseUtil.badArgument();
        }
        if (orderGoodsService.findById(orderGoodsId) == null) {
            return ResponseUtil.badArgumentValue();
        }

        Boolean hasPicture = review.getHasPicture();
        if (hasPicture == null || !hasPicture) {
            review.setPicUrls(new String[0]);
        }
        return null;
    }

    /**
     * 发表商品评价
     *
     * @param userId 用户ID
     * @param review 评价内容
     * @return 发表评价操作结果
     */
    @PostMapping("/post")
    public Object post(@LoginUser Integer userId, @RequestBody LitemallReview review) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        Object error = validate(review);
        if (error != null) {
            return error;
        }

        review.setUserId(userId);
        reviewService.add(review);
        return ResponseUtil.ok(review);
    }

    /**
     * 获取商品评价列表
     *
     * @param goodsId 商品ID
     * @param page 分页页数
     * @param size 分页大小
     * @return 商品评价列表
     */
    @GetMapping("/list")
    public Object list(@NotNull Integer goodsId, @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer size) {
        List<LitemallReview> reviewList = reviewService.queryGoodsByGid(goodsId, page, size);
        return ResponseUtil.okList(reviewList);
    }

    /**
     * 获取商品评价数量
     *
     * @param goodsId 商品ID
     * @return 商品评价数量
     */
    @GetMapping("/count")
    public Object count(@NotNull Integer goodsId) {
        int count = reviewService.count(goodsId);
        Map<String, Object> result = new HashMap<>();
        result.put("count", count);
        return ResponseUtil.ok(result);
    }
}