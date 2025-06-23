package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.ClassinfoMapper;
import com.ruoyi.system.domain.Classinfo;
import com.ruoyi.system.service.IClassinfoService;

/**
 * 班级信息，存储班级的基本信息Service业务层处理
 *
 * @author ruoyi
 * @date 2025-06-20
 */
@Service
public class ClassinfoServiceImpl implements IClassinfoService
{
    @Autowired
    private ClassinfoMapper classinfoMapper;

    /**
     * 查询班级信息，存储班级的基本信息
     *
     * @param classId 班级信息，存储班级的基本信息主键
     * @return 班级信息，存储班级的基本信息
     */
    @Override
    public Classinfo selectClassinfoByClassId(Long classId)
    {
        return classinfoMapper.selectClassinfoByClassId(classId);
    }

    /**
     * 查询班级信息，存储班级的基本信息列表
     *
     * @param classinfo 班级信息，存储班级的基本信息
     * @return 班级信息，存储班级的基本信息
     */
    @Override
    public List<Classinfo> selectClassinfoList(Classinfo classinfo)
    {
        return classinfoMapper.selectClassinfoList(classinfo);
    }

    /**
     * 新增班级信息，存储班级的基本信息
     *
     * @param classinfo 班级信息，存储班级的基本信息
     * @return 结果
     */
    @Override
    public int insertClassinfo(Classinfo classinfo)
    {
        classinfo.setCreateTime(DateUtils.getNowDate());
        return classinfoMapper.insertClassinfo(classinfo);
    }

    /**
     * 修改班级信息，存储班级的基本信息
     *
     * @param classinfo 班级信息，存储班级的基本信息
     * @return 结果
     */
    @Override
    public int updateClassinfo(Classinfo classinfo)
    {
        classinfo.setUpdateTime(DateUtils.getNowDate());
        return classinfoMapper.updateClassinfo(classinfo);
    }

    /**
     * 批量删除班级信息，存储班级的基本信息
     *
     * @param classIds 需要删除的班级信息，存储班级的基本信息主键
     * @return 结果
     */
    @Override
    public int deleteClassinfoByClassIds(Long[] classIds)
    {
        return classinfoMapper.deleteClassinfoByClassIds(classIds);
    }

/**
 * 删除班级信息，存储班级的基本信息信息
 *
 * @param classId
 * @return 结果
 */
@Override
public int deleteClassinfoByClassId(Long classId)
{
    return classinfoMapper.deleteClassinfoByClassId(classId);
}
}