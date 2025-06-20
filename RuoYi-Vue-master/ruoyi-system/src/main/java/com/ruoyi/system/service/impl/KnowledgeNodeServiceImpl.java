package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.KnowledgeNodeMapper;
import com.ruoyi.system.domain.KnowledgeNode;
import com.ruoyi.system.service.IKnowledgeNodeService;

/**
 * 知识图谱节点，存储课程的知识图谱结构Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
@Service
public class KnowledgeNodeServiceImpl implements IKnowledgeNodeService 
{
    @Autowired
    private KnowledgeNodeMapper knowledgeNodeMapper;

    /**
     * 查询知识图谱节点，存储课程的知识图谱结构
     * 
     * @param nodeId 知识图谱节点，存储课程的知识图谱结构主键
     * @return 知识图谱节点，存储课程的知识图谱结构
     */
    @Override
    public KnowledgeNode selectKnowledgeNodeByNodeId(Long nodeId)
    {
        return knowledgeNodeMapper.selectKnowledgeNodeByNodeId(nodeId);
    }

    /**
     * 查询知识图谱节点，存储课程的知识图谱结构列表
     * 
     * @param knowledgeNode 知识图谱节点，存储课程的知识图谱结构
     * @return 知识图谱节点，存储课程的知识图谱结构
     */
    @Override
    public List<KnowledgeNode> selectKnowledgeNodeList(KnowledgeNode knowledgeNode)
    {
        return knowledgeNodeMapper.selectKnowledgeNodeList(knowledgeNode);
    }

    /**
     * 新增知识图谱节点，存储课程的知识图谱结构
     * 
     * @param knowledgeNode 知识图谱节点，存储课程的知识图谱结构
     * @return 结果
     */
    @Override
    public int insertKnowledgeNode(KnowledgeNode knowledgeNode)
    {
        knowledgeNode.setCreateTime(DateUtils.getNowDate());
        return knowledgeNodeMapper.insertKnowledgeNode(knowledgeNode);
    }

    /**
     * 修改知识图谱节点，存储课程的知识图谱结构
     * 
     * @param knowledgeNode 知识图谱节点，存储课程的知识图谱结构
     * @return 结果
     */
    @Override
    public int updateKnowledgeNode(KnowledgeNode knowledgeNode)
    {
        knowledgeNode.setUpdateTime(DateUtils.getNowDate());
        return knowledgeNodeMapper.updateKnowledgeNode(knowledgeNode);
    }

    /**
     * 批量删除知识图谱节点，存储课程的知识图谱结构
     * 
     * @param nodeIds 需要删除的知识图谱节点，存储课程的知识图谱结构主键
     * @return 结果
     */
    @Override
    public int deleteKnowledgeNodeByNodeIds(Long[] nodeIds)
    {
        return knowledgeNodeMapper.deleteKnowledgeNodeByNodeIds(nodeIds);
    }

    /**
     * 删除知识图谱节点，存储课程的知识图谱结构信息
     * 
     * @param nodeId 知识图谱节点，存储课程的知识图谱结构主键
     * @return 结果
     */
    @Override
    public int deleteKnowledgeNodeByNodeId(Long nodeId)
    {
        return knowledgeNodeMapper.deleteKnowledgeNodeByNodeId(nodeId);
    }
}
