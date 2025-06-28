package com.ruoyi.web.controller;

import java.util.List;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.Score;
import com.ruoyi.system.service.IScoreService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.utils.SecurityUtils;

/**
 * 成绩管理Controller
 * 
 * @author ruoyi
 * @date 2025-06-22
 */
@RestController
@RequestMapping("/system/score")
public class ScoreController extends BaseController
{
    @Autowired
    private IScoreService scoreService;

    /**
     * 查询成绩管理列表
     */
    @PreAuthorize("@ss.hasPermi('system:score:list')")
    @GetMapping("/list")
    public TableDataInfo list(Score score)
    {
        startPage();
        
        // 判断当前用户角色，如果是学生角色，则只能查看自己的成绩
        if (SecurityUtils.isStudent()) {
            // 设置查询条件为当前登录用户ID
            score.getParams().put("userId", SecurityUtils.getUserId());
            
            // 学生只能通过任务ID、学习记录ID和试卷ID进行搜索，清除其他搜索条件
            if (score.getTaskId() == null && score.getLearningRecordId() == null && score.getPaperId() == null) {
                // 不进行额外限制
            } else {
                // 保留这些字段的查询条件，其他清空
                Long taskId = score.getTaskId();
                Long learningRecordId = score.getLearningRecordId();
                Long paperId = score.getPaperId();
                
                score = new Score();
                score.setTaskId(taskId);
                score.setLearningRecordId(learningRecordId);
                score.setPaperId(paperId);
                
                score.getParams().put("userId", SecurityUtils.getUserId());
            }
        }
        
        List<Score> list = scoreService.selectScoreList(score);
        return getDataTable(list);
    }

    /**
     * 导出成绩管理列表
     */
    @PreAuthorize("@ss.hasPermi('system:score:export')")
    @Log(title = "成绩管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Score score)
    {
        // 学生只能导出自己的成绩
        if (SecurityUtils.isStudent()) {
            score.getParams().put("userId", SecurityUtils.getUserId());
        }
        
        List<Score> list = scoreService.selectScoreList(score);
        ExcelUtil<Score> util = new ExcelUtil<Score>(Score.class);
        util.exportExcel(response, list, "成绩管理数据");
    }

    /**
     * 获取成绩管理详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:score:query')")
    @GetMapping(value = "/{scoreId}")
    public AjaxResult getInfo(@PathVariable("scoreId") Long scoreId)
    {
        Score score = scoreService.selectScoreByScoreId(scoreId);
        
        // 如果是学生用户，验证这个成绩是否属于当前登录的学生
        if (SecurityUtils.isStudent()) {
            Score query = new Score();
            query.setScoreId(scoreId);
            query.getParams().put("userId", SecurityUtils.getUserId());
            
            List<Score> userScores = scoreService.selectScoreList(query);
            if (userScores == null || userScores.isEmpty()) {
                return AjaxResult.error("您没有权限查看该成绩记录");
            }
        }
        
        return success(score);
    }

    /**
     * 新增成绩管理
     */
    @PreAuthorize("@ss.hasPermi('system:score:add')")
    @Log(title = "成绩管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Score score)
    {
        // 学生不允许添加成绩
        if (SecurityUtils.isStudent()) {
            return AjaxResult.error("学生用户无权添加成绩记录");
        }
        
        return toAjax(scoreService.insertScore(score));
    }

    /**
     * 修改成绩管理
     */
    @PreAuthorize("@ss.hasPermi('system:score:edit')")
    @Log(title = "成绩管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Score score)
    {
        // 学生不允许修改成绩
        if (SecurityUtils.isStudent()) {
            return AjaxResult.error("学生用户无权修改成绩记录");
        }
        
        return toAjax(scoreService.updateScore(score));
    }

    /**
     * 删除成绩管理
     */
    @PreAuthorize("@ss.hasPermi('system:score:remove')")
    @Log(title = "成绩管理", businessType = BusinessType.DELETE)
	@DeleteMapping("/{scoreIds}")
    public AjaxResult remove(@PathVariable Long[] scoreIds)
    {
        // 学生不允许删除成绩
        if (SecurityUtils.isStudent()) {
            return AjaxResult.error("学生用户无权删除成绩记录");
        }
        
        return toAjax(scoreService.deleteScoreByScoreIds(scoreIds));
    }

    /**
     * 根据用户ID和课程ID查询成绩
     */
    @PreAuthorize("@ss.hasPermi('system:score:list')")
    @GetMapping("/user/{userId}/course/{courseId}")
    public AjaxResult getScoreByUserAndCourse(@PathVariable("userId") Long userId, @PathVariable("courseId") Long courseId)
    {
        // 学生只能查看自己的成绩
        if (SecurityUtils.isStudent() && !userId.equals(SecurityUtils.getUserId())) {
            return AjaxResult.error("学生用户只能查看自己的成绩记录");
        }
        
        List<Score> list = scoreService.selectScoreByUserAndCourse(userId, courseId);
        return success(list);
    }

    /**
     * 根据用户ID查询所有成绩
     */
    @PreAuthorize("@ss.hasPermi('system:score:list')")
    @GetMapping("/user/{userId}")
    public AjaxResult getScoreByUserId(@PathVariable("userId") Long userId)
    {
        // 学生只能查看自己的成绩
        if (SecurityUtils.isStudent() && !userId.equals(SecurityUtils.getUserId())) {
            return AjaxResult.error("学生用户只能查看自己的成绩记录");
        }
        
        List<Score> list = scoreService.selectScoreByUserId(userId);
        return success(list);
    }
}
