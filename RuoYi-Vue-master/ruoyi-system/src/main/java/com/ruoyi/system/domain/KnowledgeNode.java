package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 知识图谱节点，存储课程的知识图谱结构对象 knowledge_node
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
public class KnowledgeNode extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 节点ID，主键，自增 */
    private Long nodeId;

    /** 课程ID，关联course表 */
    @Excel(name = "课程ID，关联course表")
    private Long courseId;

    /** 节点名称 */
    @Excel(name = "节点名称")
    private String nodeName;

    /** 节点描述 */
    @Excel(name = "节点描述")
    private String nodeDesc;

    /** 节点层级（1-顶级） */
    @Excel(name = "节点层级", readConverterExp = "1=-顶级")
    private Long nodeLevel;

    /** 父节点ID，自关联 */
    @Excel(name = "父节点ID，自关联")
    private Long parentId;

    public void setNodeId(Long nodeId) 
    {
        this.nodeId = nodeId;
    }

    public Long getNodeId() 
    {
        return nodeId;
    }

    public void setCourseId(Long courseId) 
    {
        this.courseId = courseId;
    }

    public Long getCourseId() 
    {
        return courseId;
    }

    public void setNodeName(String nodeName) 
    {
        this.nodeName = nodeName;
    }

    public String getNodeName() 
    {
        return nodeName;
    }

    public void setNodeDesc(String nodeDesc) 
    {
        this.nodeDesc = nodeDesc;
    }

    public String getNodeDesc() 
    {
        return nodeDesc;
    }

    public void setNodeLevel(Long nodeLevel) 
    {
        this.nodeLevel = nodeLevel;
    }

    public Long getNodeLevel() 
    {
        return nodeLevel;
    }

    public void setParentId(Long parentId) 
    {
        this.parentId = parentId;
    }

    public Long getParentId() 
    {
        return parentId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("nodeId", getNodeId())
            .append("courseId", getCourseId())
            .append("nodeName", getNodeName())
            .append("nodeDesc", getNodeDesc())
            .append("nodeLevel", getNodeLevel())
            .append("parentId", getParentId())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
