package com.ruoyi.web.controller;

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
import com.ruoyi.system.domain.KnowledgeNode;
import com.ruoyi.system.service.IKnowledgeNodeService;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.common.core.page.TableDataInfo;

/**
 * 知识图谱节点，存储课程的知识图谱结构Controller
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
@RestController
@RequestMapping("/system/node")
public class KnowledgeNodeController extends BaseController
{
    @Autowired
    private IKnowledgeNodeService knowledgeNodeService;

    /**
     * 查询知识图谱节点，存储课程的知识图谱结构列表
     */
    @PreAuthorize("@ss.hasPermi('system:node:list')")
    @GetMapping("/list")
    public TableDataInfo list(KnowledgeNode knowledgeNode)
    {
        startPage();
        List<KnowledgeNode> list = knowledgeNodeService.selectKnowledgeNodeList(knowledgeNode);
        return getDataTable(list);
    }

    /**
     * 导出知识图谱节点，存储课程的知识图谱结构列表
     */
    @PreAuthorize("@ss.hasPermi('system:node:export')")
    @Log(title = "知识图谱节点，存储课程的知识图谱结构", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, KnowledgeNode knowledgeNode)
    {
        List<KnowledgeNode> list = knowledgeNodeService.selectKnowledgeNodeList(knowledgeNode);
        ExcelUtil<KnowledgeNode> util = new ExcelUtil<KnowledgeNode>(KnowledgeNode.class);
        util.exportExcel(response, list, "知识图谱节点，存储课程的知识图谱结构数据");
    }

    /**
     * 获取知识图谱节点，存储课程的知识图谱结构详细信息
     */
    @PreAuthorize("@ss.hasPermi('system:node:query')")
    @GetMapping(value = "/{nodeId}")
    public AjaxResult getInfo(@PathVariable("nodeId") Long nodeId)
    {
        return success(knowledgeNodeService.selectKnowledgeNodeByNodeId(nodeId));
    }

    /**
     * 新增知识图谱节点，存储课程的知识图谱结构
     */
    @PreAuthorize("@ss.hasPermi('system:node:add')")
    @Log(title = "知识图谱节点，存储课程的知识图谱结构", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody KnowledgeNode knowledgeNode)
    {
        return toAjax(knowledgeNodeService.insertKnowledgeNode(knowledgeNode));
    }

    /**
     * 修改知识图谱节点，存储课程的知识图谱结构
     */
    @PreAuthorize("@ss.hasPermi('system:node:edit')")
    @Log(title = "知识图谱节点，存储课程的知识图谱结构", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody KnowledgeNode knowledgeNode)
    {
        return toAjax(knowledgeNodeService.updateKnowledgeNode(knowledgeNode));
    }

    /**
     * 删除知识图谱节点，存储课程的知识图谱结构
     */
    @PreAuthorize("@ss.hasPermi('system:node:remove')")
    @Log(title = "知识图谱节点，存储课程的知识图谱结构", businessType = BusinessType.DELETE)
	@DeleteMapping("/{nodeIds}")
    public AjaxResult remove(@PathVariable Long[] nodeIds)
    {
        return toAjax(knowledgeNodeService.deleteKnowledgeNodeByNodeIds(nodeIds));
    }
}
