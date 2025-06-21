package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.KnowledgeNode;

/**
 * 知识图谱节点，存储课程的知识图谱结构Service接口
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
public interface IKnowledgeNodeService 
{
    /**
     * 查询知识图谱节点，存储课程的知识图谱结构
     * 
     * @param nodeId 知识图谱节点，存储课程的知识图谱结构主键
     * @return 知识图谱节点，存储课程的知识图谱结构
     */
    public KnowledgeNode selectKnowledgeNodeByNodeId(Long nodeId);

    /**
     * 查询知识图谱节点，存储课程的知识图谱结构列表
     * 
     * @param knowledgeNode 知识图谱节点，存储课程的知识图谱结构
     * @return 知识图谱节点，存储课程的知识图谱结构集合
     */
    public List<KnowledgeNode> selectKnowledgeNodeList(KnowledgeNode knowledgeNode);

    /**
     * 新增知识图谱节点，存储课程的知识图谱结构
     * 
     * @param knowledgeNode 知识图谱节点，存储课程的知识图谱结构
     * @return 结果
     */
    public int insertKnowledgeNode(KnowledgeNode knowledgeNode);

    /**
     * 修改知识图谱节点，存储课程的知识图谱结构
     * 
     * @param knowledgeNode 知识图谱节点，存储课程的知识图谱结构
     * @return 结果
     */
    public int updateKnowledgeNode(KnowledgeNode knowledgeNode);

    /**
     * 批量删除知识图谱节点，存储课程的知识图谱结构
     * 
     * @param nodeIds 需要删除的知识图谱节点，存储课程的知识图谱结构主键集合
     * @return 结果
     */
    public int deleteKnowledgeNodeByNodeIds(Long[] nodeIds);

    /**
     * 删除知识图谱节点，存储课程的知识图谱结构信息
     * 
     * @param nodeId 知识图谱节点，存储课程的知识图谱结构主键
     * @return 结果
     */
    public int deleteKnowledgeNodeByNodeId(Long nodeId);
}
