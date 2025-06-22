package com.ruoyi.system.domain;

import java.math.BigDecimal;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 试卷，一个试卷库包含多个试卷对象 test_paper
 * 
 * @author ruoyi
 * @date 2025-06-20
 */
public class TestPaper extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 试卷ID，主键，自增 */
    private Long paperId;

    /** 试卷库ID，关联course_paper_library表 */
    @Excel(name = "试卷库ID，关联course_paper_library表")
    private Long libraryId;

    /** 试卷名称（如"期中测试卷"） */
    @Excel(name = "试卷名称", readConverterExp = "如='期中测试卷'")
    private String paperName;

    /** 试卷描述 */
    @Excel(name = "试卷描述")
    private String paperDesc;

    /** 总分 */
    @Excel(name = "总分")
    private BigDecimal totalScore;

    public void setPaperId(Long paperId) 
    {
        this.paperId = paperId;
    }

    public Long getPaperId() 
    {
        return paperId;
    }

    public void setLibraryId(Long libraryId) 
    {
        this.libraryId = libraryId;
    }

    public Long getLibraryId() 
    {
        return libraryId;
    }

    public void setPaperName(String paperName) 
    {
        this.paperName = paperName;
    }

    public String getPaperName() 
    {
        return paperName;
    }

    public void setPaperDesc(String paperDesc) 
    {
        this.paperDesc = paperDesc;
    }

    public String getPaperDesc() 
    {
        return paperDesc;
    }

    public void setTotalScore(BigDecimal totalScore) 
    {
        this.totalScore = totalScore;
    }

    public BigDecimal getTotalScore() 
    {
        return totalScore;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("paperId", getPaperId())
            .append("libraryId", getLibraryId())
            .append("paperName", getPaperName())
            .append("paperDesc", getPaperDesc())
            .append("totalScore", getTotalScore())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
