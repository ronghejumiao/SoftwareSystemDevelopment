package com.ruoyi.system.controller;

import java.util.List;
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

/**
 * 成绩，记录学生的学习成绩信息Controller
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
@RestController
@RequestMapping("/system/score")
public class ScoreController extends BaseController
{
    @Autowired
    private IScoreService scoreService;

    /**
     * 查询成绩，记录学生的学习成绩信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:score:list')")
    @GetMapping("/list")
    public TableDataInfo list(Score score)
    {
        startPage();
        List<Score> list = scoreService.selectScoreList(score);
        return getDataTable(list);
    }

    /**
     * 导出成绩，记录学生的学习成绩信息列表
     */
    @PreAuthorize("@ss.hasPermi('system:score:export')")
    @Log(title = "成绩，记录学生的学习成绩信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, Score score)
    {
        List<Score> list = scoreService.selectScoreList(score);
        ExcelUtil<Score> util = new ExcelUtil<Score>(Score.class);
        util.exportExcel(response, list, "成绩，记录学生的学习成绩信息数据");
    }

    /**
     * 获取成绩，记录学生的学习成绩信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:score:query')")
    @GetMapping(value = "/{scoreId}")
    public AjaxResult getInfo(@PathVariable("scoreId") Long scoreId)
    {
        return success(scoreService.selectScoreByScoreId(scoreId));
    }

    /**
     * 新增成绩，记录学生的学习成绩信息
     */
    @PreAuthorize("@ss.hasPermi('system:score:add')")
    @Log(title = "成绩，记录学生的学习成绩信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody Score score)
    {
        return toAjax(scoreService.insertScore(score));
    }

    /**
     * 修改成绩，记录学生的学习成绩信息
     */
    @PreAuthorize("@ss.hasPermi('system:score:edit')")
    @Log(title = "成绩，记录学生的学习成绩信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody Score score)
    {
        return toAjax(scoreService.updateScore(score));
    }

    /**
     * 删除成绩，记录学生的学习成绩信息
     */
    @PreAuthorize("@ss.hasPermi('system:score:remove')")
    @Log(title = "成绩，记录学生的学习成绩信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{scoreIds}")
    public AjaxResult remove(@PathVariable Long[] scoreIds)
    {
        return toAjax(scoreService.deleteScoreByScoreIds(scoreIds));
    }
}
