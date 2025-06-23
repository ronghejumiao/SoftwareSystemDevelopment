package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.Classinfo;

/**
 * 班级信息，存储班级的基本信息Mapper接口
 *
 * @author ruoyi
 * @date 2025-06-20
 */
public interface ClassinfoMapper
{
    /**
     * 查询班级信息，存储班级的基本信息
     *
     * @param classId 班级信息，存储班级的基本信息主键
     * @return 班级信息，存储班级的基本信息
     */
    public Classinfo selectClassinfoByClassId(Long classId);

    /**
     * 查询班级信息，存储班级的基本信息列表
     *
     * @param classinfo 班级信息，存储班级的基本信息
     * @return 班级信息，存储班级的基本信息集合
     */
    public List<Classinfo> selectClassinfoList(Classinfo classinfo);

    /**
     * 新增班级信息，存储班级的基本信息
     *
     * @param classinfo 班级信息，存储班级的基本信息
     * @return 结果
     */
    public int insertClassinfo(Classinfo classinfo);

    /**
     * 修改班级信息，存储班级的基本信息
     *
     * @param classinfo 班级信息，存储班级的基本信息
     * @return 结果
     */
    public int updateClassinfo(Classinfo classinfo);

    /**
     * 删除班级信息，存储班级的基本信息
     *
     * @param classId 班级信息，存储班级的基本信息主键
     * @return 结果
     */
    public int deleteClassinfoByClassId(Long classId);

    /**
     * 批量删除班级信息，存储班级的基本信息
     *
     * @param classIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteClassinfoByClassIds(Long[] classIds);
}