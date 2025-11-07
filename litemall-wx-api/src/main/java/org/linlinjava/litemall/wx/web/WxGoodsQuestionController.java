package org.linlinjava.litemall.wx.web;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.linlinjava.litemall.core.util.ResponseUtil;
import org.linlinjava.litemall.db.domain.LitemallGoodsQuestion;
import org.linlinjava.litemall.db.service.LitemallGoodsQuestionService;
import org.linlinjava.litemall.db.service.LitemallGoodsService;
import org.linlinjava.litemall.db.service.LitemallOrderGoodsService;
import org.linlinjava.litemall.db.service.LitemallUserService;
import org.linlinjava.litemall.wx.annotation.LoginUser;
import org.linlinjava.litemall.wx.dto.UserInfo;
import org.linlinjava.litemall.wx.service.UserInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 商品提问服务
 */
@RestController
@RequestMapping("/wx/goods/question")
@Validated
public class WxGoodsQuestionController {
    private final Log logger = LogFactory.getLog(WxGoodsQuestionController.class);

    @Autowired
    private LitemallGoodsQuestionService goodsQuestionService;
    @Autowired
    private LitemallGoodsService goodsService;
    @Autowired
    private LitemallUserService userService;
    @Autowired
    private UserInfoService userInfoService;
    @Autowired
    private LitemallOrderGoodsService orderGoodsService;

    private Object validateQuestion(LitemallGoodsQuestion goodsQuestion) {
        String question = goodsQuestion.getQuestion();
        if (StringUtils.isEmpty(question)) {
            return ResponseUtil.badArgument();
        }

        Integer goodsId = goodsQuestion.getGoodsId();
        if (goodsId == null) {
            return ResponseUtil.badArgument();
        }
        if (goodsService.findById(goodsId) == null) {
            return ResponseUtil.badArgumentValue();
        }
        return null;
    }

    private Object validateAnswer(LitemallGoodsQuestion goodsQuestion) {
        String answer = goodsQuestion.getAnswer();
        if (StringUtils.isEmpty(answer)) {
            return ResponseUtil.badArgument();
        }

        Integer id = goodsQuestion.getId();
        if (id == null) {
            return ResponseUtil.badArgument();
        }
        if (goodsQuestionService.findById(id) == null) {
            return ResponseUtil.badArgumentValue();
        }
        return null;
    }

    /**
     * 发表商品提问
     *
     * @param userId  用户ID
     * @param goodsQuestion 商品提问内容
     * @return 发表提问操作结果
     */
    @PostMapping("post")
    public Object post(@LoginUser Integer userId, @RequestBody LitemallGoodsQuestion goodsQuestion) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        Object error = validateQuestion(goodsQuestion);
        if (error != null) {
            return error;
        }

        goodsQuestion.setUserId(userId);
        goodsQuestionService.save(goodsQuestion);
        return ResponseUtil.ok(goodsQuestion);
    }

    /**
     * 回答商品提问
     *
     * @param userId  用户ID
     * @param goodsQuestion 商品回答内容
     * @return 回答提问操作结果
     */
    @PostMapping("answer")
    public Object answer(@LoginUser Integer userId, @RequestBody LitemallGoodsQuestion goodsQuestion) {
        if (userId == null) {
            return ResponseUtil.unlogin();
        }
        Object error = validateAnswer(goodsQuestion);
        if (error != null) {
            return error;
        }

        // 检查用户是否有权限回答
        LitemallGoodsQuestion originalQuestion = goodsQuestionService.findById(goodsQuestion.getId());
        boolean canAnswer = orderGoodsService.checkUserBuyGoods(userId, originalQuestion.getGoodsId());
        // TODO: 检查是否是商家
        if (!canAnswer) {
            return ResponseUtil.fail("只有已购买用户或商家可以回答");
        }

        goodsQuestionService.updateAnswer(goodsQuestion.getId(), goodsQuestion.getAnswer(), userId);
        return ResponseUtil.ok();
    }

    /**
     * 商品提问列表
     *
     * @param goodsId  商品ID
     * @param page     分页页数
     * @param limit     分页大小
     * @return 商品提问列表
     */
    @GetMapping("list")
    public Object list(@NotNull Integer goodsId,
                       @RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit) {
        List<LitemallGoodsQuestion> questionList = goodsQuestionService.queryByGoodsId(goodsId, page, limit);

        List<Map<String, Object>> questionVoList = new ArrayList<>(questionList.size());
        for (LitemallGoodsQuestion question : questionList) {
            Map<String, Object> questionVo = new HashMap<>();
            questionVo.put("id", question.getId());
            questionVo.put("addTime", question.getAddTime());
            questionVo.put("question", question.getQuestion());
            questionVo.put("answer", question.getAnswer());
            questionVo.put("answerTime", question.getAnswerTime());
            UserInfo userInfo = userInfoService.getInfo(question.getUserId());
            questionVo.put("userInfo", userInfo);
            if (question.getAnswerUserId() != null) {
                UserInfo answerUserInfo = userInfoService.getInfo(question.getAnswerUserId());
                questionVo.put("answerUserInfo", answerUserInfo);
            }

            questionVoList.add(questionVo);
        }
        return ResponseUtil.okList(questionVoList, questionList);
    }
}