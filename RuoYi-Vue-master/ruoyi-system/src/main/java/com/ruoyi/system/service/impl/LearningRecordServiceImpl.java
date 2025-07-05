package com.ruoyi.system.service.impl;

import java.util.List;
import java.util.Date;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.LearningRecordMapper;
import com.ruoyi.system.domain.LearningRecord;
import com.ruoyi.system.service.ILearningRecordService;
import com.ruoyi.system.service.IStudentSkillAssessmentService;
import com.ruoyi.system.mapper.ScoreMapper;

/**
 * 学习记录，记录学生的课程学习进度和关联信息Service业务层处理
 * 
 * @author ruoyi
 * @date 2025-06-22
 */
@Service
public class LearningRecordServiceImpl implements ILearningRecordService 
{
    @Autowired
    private LearningRecordMapper learningRecordMapper;

    @Autowired
    private ScoreMapper scoreMapper;
    
    @Autowired
    private IStudentSkillAssessmentService studentSkillAssessmentService;

    /**
     * 查询学习记录，记录学生的课程学习进度和关联信息
     * 
     * @param recordId 学习记录，记录学生的课程学习进度和关联信息主键
     * @return 学习记录，记录学生的课程学习进度和关联信息
     */
    @Override
    public LearningRecord selectLearningRecordByRecordId(Long recordId)
    {
        return learningRecordMapper.selectLearningRecordByRecordId(recordId);
    }

    /**
     * 查询学习记录，记录学生的课程学习进度和关联信息列表
     * 
     * @param learningRecord 学习记录，记录学生的课程学习进度和关联信息
     * @return 学习记录，记录学生的课程学习进度和关联信息
     */
    @Override
    public List<LearningRecord> selectLearningRecordList(LearningRecord learningRecord)
    {
        return learningRecordMapper.selectLearningRecordList(learningRecord);
    }

    /**
     * 新增学习记录，记录学生的课程学习进度和关联信息
     * 
     * @param learningRecord 学习记录，记录学生的课程学习进度和关联信息
     * @return 结果
     */
    @Override
    public int insertLearningRecord(LearningRecord learningRecord)
    {
        // 如果已存在相同用户与课程的记录，则改为更新操作，避免唯一索引冲突
        LearningRecord existing = learningRecordMapper.selectLearningRecordByUserIdAndCourseId(
                learningRecord.getUserId(), learningRecord.getCourseId());

        if (learningRecord.getJoinTime() == null) {
            learningRecord.setJoinTime(new Date());
        }
        // 若数据库对 end_time 字段非空，但业务在新增时暂未结束，先将 endTime 与 joinTime 保持一致，避免插入报错
        if (learningRecord.getEndTime() == null) {
            learningRecord.setEndTime(learningRecord.getJoinTime());
        }

        learningRecord.setCreateTime(DateUtils.getNowDate());

        if (existing != null) {
            // 复用已有ID执行更新
            learningRecord.setRecordId(existing.getRecordId());
            return updateLearningRecord(learningRecord);
        } else {
            try {
                int result = learningRecordMapper.insertLearningRecord(learningRecord);
                
                // 触发能力评估
                if (result > 0) {
                    triggerAssessment(learningRecord.getUserId(), learningRecord.getCourseId());
                }
                
                return result;
            } catch (org.springframework.dao.DuplicateKeyException e) {
                // 并发场景下可能已被其他请求插入，转为更新
                LearningRecord dup = learningRecordMapper.selectLearningRecordByUserIdAndCourseId(
                        learningRecord.getUserId(), learningRecord.getCourseId());
                if (dup != null) {
                    learningRecord.setRecordId(dup.getRecordId());
                    return updateLearningRecord(learningRecord);
                }
                throw e;
            }
        }
    }

    /**
     * 修改学习记录，记录学生的课程学习进度和关联信息
     * 
     * @param learningRecord 学习记录，记录学生的课程学习进度和关联信息
     * @return 结果
     */
    @Override
    public int updateLearningRecord(LearningRecord learningRecord)
    {
        learningRecord.setUpdateTime(DateUtils.getNowDate());
        
        int result = learningRecordMapper.updateLearningRecord(learningRecord);
        
        // 触发能力评估
        if (result > 0) {
            triggerAssessment(learningRecord.getUserId(), learningRecord.getCourseId());
        }
        
        return result;
    }

    /**
     * 批量删除学习记录，记录学生的课程学习进度和关联信息
     * 
     * @param recordIds 需要删除的学习记录，记录学生的课程学习进度和关联信息主键
     * @return 结果
     */
    @Override
    public int deleteLearningRecordByRecordIds(Long[] recordIds)
    {
        // 获取要删除的记录信息，用于触发评估
        List<LearningRecord> records = learningRecordMapper.selectLearningRecordByRecordIds(recordIds);
        
        // 先删除关联成绩等子表记录，避免外键约束
        if (recordIds != null && recordIds.length > 0)
        {
            scoreMapper.deleteScoreByLearningRecordIds(recordIds);
            // TODO: 如果还有其他关联表（如 video_learning_record、task_submission），在此处一并删除
        }
        
        int result = learningRecordMapper.deleteLearningRecordByRecordIds(recordIds);
        
        // 触发能力评估
        if (result > 0 && records != null) {
            for (LearningRecord record : records) {
                triggerAssessment(record.getUserId(), record.getCourseId());
            }
        }
        
        return result;
    }

    /**
     * 删除学习记录，记录学生的课程学习进度和关联信息信息
     * 
     * @param recordId 学习记录，记录学生的课程学习进度和关联信息主键
     * @return 结果
     */
    @Override
    public int deleteLearningRecordByRecordId(Long recordId)
    {
        // 获取要删除的记录信息，用于触发评估
        LearningRecord record = learningRecordMapper.selectLearningRecordByRecordId(recordId);
        
        int result = learningRecordMapper.deleteLearningRecordByRecordId(recordId);
        
        // 触发能力评估
        if (result > 0 && record != null) {
            triggerAssessment(record.getUserId(), record.getCourseId());
        }
        
        return result;
    }

    @Override
    public com.ruoyi.system.domain.LearningRecord selectByUserIdAndCourseId(Long userId, Long courseId) {
        return learningRecordMapper.selectLearningRecordByUserIdAndCourseId(userId, courseId);
    }
    
    @Override
    public List<LearningRecord> selectLearningRecordListByUserAndCourse(Long userId, Long courseId) {
        LearningRecord query = new LearningRecord();
        query.setUserId(userId);
        query.setCourseId(courseId);
        return learningRecordMapper.selectLearningRecordList(query);
    }
    
    @Override
    public List<Long> selectAllStudentIdsByCourseId(Long courseId) {
        LearningRecord query = new LearningRecord();
        query.setCourseId(courseId);
        List<LearningRecord> records = learningRecordMapper.selectLearningRecordList(query);
        return records.stream()
                .map(LearningRecord::getUserId)
                .distinct()
                .collect(java.util.stream.Collectors.toList());
    }
    
    /**
     * 触发能力评估
     * @param userId 用户ID
     * @param courseId 课程ID
     */
    private void triggerAssessment(Long userId, Long courseId) {
        try {
            studentSkillAssessmentService.triggerAssessmentOnDataChange(userId, courseId);
        } catch (Exception e) {
            // 记录日志但不影响主业务流程
            System.err.println("触发能力评估失败: userId=" + userId + ", courseId=" + courseId + ", error=" + e.getMessage());
        }
    }
}
