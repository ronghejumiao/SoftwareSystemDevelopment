package com.ruoyi.system.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.alibaba.fastjson2.JSON;
import com.ruoyi.common.utils.DateUtils;
import com.ruoyi.system.domain.Question;
import com.ruoyi.system.domain.TestPaper;
import com.ruoyi.system.domain.CoursePaperLibrary;
import com.ruoyi.system.domain.vo.PaperGenerateRequest;
import com.ruoyi.system.mapper.TestPaperMapper;
import com.ruoyi.system.service.IPaperGenerateService;
import com.ruoyi.system.service.IQuestionService;
import com.ruoyi.system.service.ICoursePaperLibraryService;

/**
 * 试卷生成Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-01-01
 */
@Service
public class PaperGenerateServiceImpl implements IPaperGenerateService 
{
    @Autowired
    private IQuestionService questionService;
    
    @Autowired
    private ICoursePaperLibraryService coursePaperLibraryService;
    
    @Autowired
    private TestPaperMapper testPaperMapper;

    /**
     * 生成试卷
     * 
     * @param request 试卷生成请求
     * @return 生成的试卷
     */
    @Override
    public TestPaper generatePaper(PaperGenerateRequest request)
    {
        // 获取课程对应的试卷库
        CoursePaperLibrary library = coursePaperLibraryService.selectCoursePaperLibraryByCourseId(request.getCourseId());
        if (library == null) {
            throw new RuntimeException("未找到课程对应的试卷库");
        }

        // 创建试卷对象
        TestPaper testPaper = new TestPaper();
        testPaper.setLibraryId(library.getLibraryId());
        testPaper.setPaperName(request.getPaperName());
        testPaper.setPaperDesc(request.getPaperDesc());
        testPaper.setTotalScore(request.getTotalScore());
        testPaper.setCreateTime(DateUtils.getNowDate());

        // 根据模式处理试卷内容
        List<Question> paperQuestions;
        if ("manual".equals(request.getMode())) {
            // 手动组卷：直接使用传入的题目
            paperQuestions = request.getContent();
        } else {
            // 随机组卷：根据配置生成题目
            paperQuestions = generateRandomQuestions(request);
        }

        // 将题目转换为JSON格式存储
        String contentJson = JSON.toJSONString(paperQuestions);
        testPaper.setContent(contentJson);

        // 保存试卷到数据库
        testPaperMapper.insertTestPaper(testPaper);

        return testPaper;
    }

    /**
     * 根据配置生成随机题目
     * 
     * @param request 试卷生成请求
     * @return 生成的题目列表
     */
    private List<Question> generateRandomQuestions(PaperGenerateRequest request)
    {
        // 获取课程所有题目
        List<Question> allQuestions = questionService.selectQuestionsByCourseId(request.getCourseId());
        if (allQuestions.isEmpty()) {
            throw new RuntimeException("题库中没有题目");
        }

        PaperGenerateRequest.AutoConfig config = request.getConfig();
        List<Question> selectedQuestions = new ArrayList<>();

        // 按题型筛选题目
        List<Question> choiceQuestions = filterQuestionsByType(allQuestions, "选择");
        List<Question> fillQuestions = filterQuestionsByType(allQuestions, "填空");
        List<Question> essayQuestions = filterQuestionsByType(allQuestions, "简答");

        // 计算各题型需要的题目数量
        int totalQuestions = config.getTotalQuestions();
        int choiceCount = Math.round(totalQuestions * config.getChoiceRatio() / 100.0f);
        int fillCount = Math.round(totalQuestions * config.getFillRatio() / 100.0f);
        int essayCount = totalQuestions - choiceCount - fillCount;

        // 验证题目数量是否足够
        if (choiceQuestions.size() < choiceCount) {
            throw new RuntimeException("选择题数量不足，需要" + choiceCount + "题，实际只有" + choiceQuestions.size() + "题");
        }
        if (fillQuestions.size() < fillCount) {
            throw new RuntimeException("填空题数量不足，需要" + fillCount + "题，实际只有" + fillQuestions.size() + "题");
        }
        if (essayQuestions.size() < essayCount) {
            throw new RuntimeException("简答题数量不足，需要" + essayCount + "题，实际只有" + essayQuestions.size() + "题");
        }

        // 随机选择题目
        selectedQuestions.addAll(getRandomQuestions(choiceQuestions, choiceCount));
        selectedQuestions.addAll(getRandomQuestions(fillQuestions, fillCount));
        selectedQuestions.addAll(getRandomQuestions(essayQuestions, essayCount));

        // 根据排序规则排序
        sortQuestions(selectedQuestions, config.getSortRule());

        return selectedQuestions;
    }

    /**
     * 按题型筛选题目
     * 
     * @param questions 题目列表
     * @param type 题型
     * @return 筛选后的题目列表
     */
    private List<Question> filterQuestionsByType(List<Question> questions, String type)
    {
        List<Question> filtered = new ArrayList<>();
        for (Question question : questions) {
            if (type.equals(question.getQuestionType())) {
                filtered.add(question);
            }
        }
        return filtered;
    }

    /**
     * 随机选择指定数量的题目
     * 
     * @param questions 题目列表
     * @param count 需要的数量
     * @return 随机选择的题目列表
     */
    private List<Question> getRandomQuestions(List<Question> questions, int count)
    {
        if (count >= questions.size()) {
            return new ArrayList<>(questions);
        }

        List<Question> shuffled = new ArrayList<>(questions);
        Collections.shuffle(shuffled, new Random());
        return shuffled.subList(0, count);
    }

    /**
     * 根据规则排序题目
     * 
     * @param questions 题目列表
     * @param sortRule 排序规则
     */
    private void sortQuestions(List<Question> questions, String sortRule)
    {
        if ("difficulty".equals(sortRule)) {
            // 按难度递增排序
            Collections.sort(questions, (q1, q2) -> {
                int order1 = getDifficultyOrder(q1.getDifficultyLevel());
                int order2 = getDifficultyOrder(q2.getDifficultyLevel());
                return Integer.compare(order1, order2);
            });
        } else if ("type".equals(sortRule)) {
            // 按题型分组排序
            Collections.sort(questions, (q1, q2) -> {
                int order1 = getTypeOrder(q1.getQuestionType());
                int order2 = getTypeOrder(q2.getQuestionType());
                return Integer.compare(order1, order2);
            });
        }
        // random: 已经随机了，不需要再排序
    }

    /**
     * 获取难度等级排序值
     * 
     * @param difficulty 难度等级
     * @return 排序值
     */
    private int getDifficultyOrder(String difficulty)
    {
        switch (difficulty) {
            case "简单": return 1;
            case "中等": return 2;
            case "困难": return 3;
            default: return 0;
        }
    }

    /**
     * 获取题型排序值
     * 
     * @param type 题型
     * @return 排序值
     */
    private int getTypeOrder(String type)
    {
        switch (type) {
            case "选择": return 1;
            case "填空": return 2;
            case "简答": return 3;
            default: return 0;
        }
    }
} 