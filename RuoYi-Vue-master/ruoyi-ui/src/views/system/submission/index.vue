<template>
  <div class="app-container">
    <!-- 统计卡片 -->
    <el-row :gutter="20" class="mb20">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
              <i class="el-icon-document"></i>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ total }}</div>
              <div class="stat-label">总提交数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);">
              <i class="el-icon-time"></i>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ pendingCount }}</div>
              <div class="stat-label">待评分</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
              <i class="el-icon-check"></i>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ gradedCount }}</div>
              <div class="stat-label">已评分</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);">
              <i class="el-icon-star-on"></i>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ avgScore }}</div>
              <div class="stat-label">平均分</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 统计图表区域 -->
    <el-row :gutter="20" class="mb20">
      <el-col :span="12">
        <el-card class="chart-card" shadow="hover">
          <div slot="header" class="chart-header">
            <span>提交趋势分析</span>
            <el-button type="text" @click="refreshTrendChart">
              <i class="el-icon-refresh"></i>
            </el-button>
          </div>
          <div class="chart-container">
            <div ref="trendChart" class="chart" style="height: 300px;"></div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="12">
        <el-card class="chart-card" shadow="hover">
          <div slot="header" class="chart-header">
            <span>评分分布</span>
            <el-button type="text" @click="refreshScoreChart">
              <i class="el-icon-refresh"></i>
            </el-button>
          </div>
          <div class="chart-container">
            <div ref="scoreChart" class="chart" style="height: 300px;"></div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 进度统计卡片 -->
    <el-row :gutter="20" class="mb20">
      <el-col :span="8">
        <el-card class="progress-card" shadow="hover">
          <div class="progress-header">
            <span class="progress-title">评分进度</span>
            <span class="progress-percentage">{{ gradingProgress }}%</span>
          </div>
          <el-progress 
            :percentage="gradingProgress" 
            :stroke-width="8"
            color="#409EFF"
            :show-text="false"
          ></el-progress>
          <div class="progress-details">
            <span>已评分: {{ gradedCount }}</span>
            <span>待评分: {{ pendingCount }}</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="progress-card" shadow="hover">
          <div class="progress-header">
            <span class="progress-title">平均分趋势</span>
            <span class="progress-percentage">{{ avgScore }}分</span>
          </div>
          <div class="score-trend">
            <div class="trend-indicator" :class="scoreTrendClass">
              <i :class="scoreTrendIcon"></i>
              <span>{{ scoreTrendText }}</span>
            </div>
          </div>
          <div class="progress-details">
            <span>最高分: {{ maxScore }}分</span>
            <span>最低分: {{ minScore }}分</span>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="progress-card" shadow="hover">
          <div class="progress-header">
            <span class="progress-title">提交活跃度</span>
            <span class="progress-percentage">{{ activityLevel }}</span>
          </div>
          <div class="activity-indicator">
            <div class="activity-dots">
              <span class="dot" :class="{ active: i < activityScore }" v-for="i in 5" :key="i"></span>
            </div>
          </div>
          <div class="progress-details">
            <span>本周提交: {{ weeklySubmissions }}</span>
            <span>本月提交: {{ monthlySubmissions }}</span>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索区域 -->
    <el-card class="search-card" shadow="never">
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="学生姓名" prop="studentName">
        <el-input
                v-model="queryParams.studentName"
                placeholder="请输入学生姓名"
          clearable
                prefix-icon="el-icon-user"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="任务名称" prop="taskName">
        <el-input
                v-model="queryParams.taskName"
                placeholder="请输入任务名称"
          clearable
                prefix-icon="el-icon-edit-outline"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="提交状态" prop="isGraded">
              <el-select v-model="queryParams.isGraded" placeholder="请选择提交状态" clearable style="width: 100%">
                <el-option label="待评分" value="0" />
                <el-option label="已评分" value="1" />
              </el-select>
      </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
      <el-form-item label="提交时间" prop="submissionTime">
              <el-date-picker
          v-model="queryParams.submissionTime"
                type="daterange"
                range-separator="至"
                start-placeholder="开始日期"
                end-placeholder="结束日期"
          value-format="yyyy-MM-dd"
                style="width: 100%"
        />
      </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="课程名称" prop="courseName">
        <el-input
                v-model="queryParams.courseName"
                placeholder="请输入课程名称"
          clearable
                prefix-icon="el-icon-reading"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
          </el-col>
          <el-col :span="8">
      <el-form-item>
              <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
              <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
          </el-col>
        </el-row>
    </el-form>
    </el-card>

    <!-- 操作按钮区域 -->
    <el-card class="action-card" shadow="never">
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          icon="el-icon-plus"
            size="small"
          @click="openAddDialog"
          v-hasPermi="['system:submission:add']"
          >新增提交</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          icon="el-icon-edit"
            size="small"
          :disabled="single"
            @click="handleBatchGrade"
          v-hasPermi="['system:submission:edit']"
          >批量评分</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="warning"
            icon="el-icon-download"
            size="small"
            @click="handleExport"
            v-hasPermi="['system:submission:export']"
          >导出数据</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          icon="el-icon-delete"
            size="small"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:submission:remove']"
          >批量删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="primary"
            icon="el-icon-refresh"
            size="small"
            @click="refreshMockData"
            :loading="loading"
          >刷新数据</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <el-table
        ref="tables"
        v-loading="loading"
        :data="submissionList"
        @selection-change="handleSelectionChange"
        :header-cell-style="{ background: '#f5f7fa', color: '#606266' }"
        stripe
      >
      <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="序号" type="index" width="60" align="center" />

        <!-- 学生信息 -->
        <el-table-column label="学生信息" align="center" min-width="150">
        <template slot-scope="scope">
            <div class="student-info">
              <el-avatar :size="40" :src="scope.row.studentAvatar || '/img/profile.jpg'"></el-avatar>
              <div class="student-details">
                <div class="student-name">{{ scope.row.studentName || '未知学生' }}</div>
                <div class="student-id">ID: {{ scope.row.recordId }}</div>
              </div>
            </div>
        </template>
      </el-table-column>

        <!-- 任务信息 -->
        <el-table-column label="任务信息" align="center" min-width="200">
        <template slot-scope="scope">
            <div class="task-info">
              <div class="task-name">{{ scope.row.taskName || '未知任务' }}</div>
              <div class="task-course">{{ scope.row.courseName || '未知课程' }}</div>
              <div class="task-deadline">截止: {{ parseTime(scope.row.dueDate, '{m}-{d} {h}:{i}') }}</div>
            </div>
        </template>
      </el-table-column>

        <!-- 提交内容预览 -->
        <el-table-column label="提交内容" align="center" min-width="200">
          <template slot-scope="scope">
            <div class="submission-content">
              <div class="content-preview" v-if="scope.row.submissionContent">
                {{ scope.row.submissionContent.substring(0, 50) }}{{ scope.row.submissionContent.length > 50 ? '...' : '' }}
              </div>
              <div class="file-info" v-if="scope.row.submissionFile">
                <i class="el-icon-document"></i>
                <span>已上传文件</span>
              </div>
            </div>
          </template>
        </el-table-column>

        <!-- 提交时间 -->
        <el-table-column label="提交时间" align="center" width="160">
          <template slot-scope="scope">
            <div class="submission-time">
              <div class="time">{{ parseTime(scope.row.submissionTime, '{y}-{m}-{d}') }}</div>
              <div class="time-detail">{{ parseTime(scope.row.submissionTime, '{h}:{i}') }}</div>
            </div>
          </template>
        </el-table-column>

        <!-- 评分状态 -->
        <el-table-column label="评分状态" align="center" width="120">
          <template slot-scope="scope">
            <el-tag
              :type="scope.row.isGraded === '1' ? 'success' : 'warning'"
              size="small"
            >
              {{ scope.row.isGraded === '1' ? '已评分' : '待评分' }}
            </el-tag>
          </template>
        </el-table-column>

        <!-- 分数 -->
        <el-table-column label="分数" align="center" width="120">
          <template slot-scope="scope">
            <div class="score-display" v-if="scope.row.score">
              <div class="score-progress">
                <el-progress 
                  :percentage="parseFloat(scope.row.score)" 
                  :stroke-width="6"
                  :show-text="false"
                  :color="getScoreColor(scope.row.score)"
                ></el-progress>
                <div class="score-text">
                  <span class="score">{{ scope.row.score }}</span>
                  <span class="score-unit">分</span>
                </div>
              </div>
              <div class="score-level">{{ getScoreLevel(scope.row.score) }}</div>
            </div>
            <span v-else class="no-score">--</span>
          </template>
        </el-table-column>

        <!-- 操作 -->
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="200">
        <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-view"
              @click="handleView(scope.row)"
            >查看</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:submission:edit']"
            >编辑</el-button>
            <el-button
              size="mini"
              type="text"
              icon="el-icon-star-on"
              @click="handleGrade(scope.row)"
              v-if="scope.row.isGraded !== '1'"
              v-hasPermi="['system:submission:edit']"
            >评分</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:submission:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
    </el-card>

    <!-- 新增/编辑对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学习记录ID" prop="recordId">
              <el-input-number v-model="form.recordId" placeholder="请输入学习记录ID" style="width: 100%" />
        </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="任务ID" prop="taskId">
              <el-input-number v-model="form.taskId" placeholder="请输入任务ID" style="width: 100%" />
        </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学生姓名" prop="studentName">
              <el-input v-model="form.studentName" placeholder="请输入学生姓名" />
        </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="任务名称" prop="taskName">
              <el-input v-model="form.taskName" placeholder="请输入任务名称" />
        </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="课程名称" prop="courseName">
              <el-input v-model="form.courseName" placeholder="请输入课程名称" />
        </el-form-item>
          </el-col>
          <el-col :span="12">
        <el-form-item label="提交时间" prop="submissionTime">
              <el-date-picker
            v-model="form.submissionTime"
                type="datetime"
                placeholder="请选择提交时间"
                value-format="yyyy-MM-dd HH:mm:ss"
                style="width: 100%"
              />
        </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="提交内容">
          <editor v-model="form.submissionContent" :min-height="200"/>
        </el-form-item>
        <el-form-item label="提交文件">
          <file-upload v-model="form.submissionFile"/>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="评分状态" prop="isGraded">
              <el-select v-model="form.isGraded" placeholder="请选择评分状态" style="width: 100%">
                <el-option label="待评分" value="0" />
                <el-option label="已评分" value="1" />
              </el-select>
        </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="分数" prop="score">
              <el-input-number 
                v-model="form.score" 
                :min="0" 
                :max="100" 
                :precision="1"
                style="width: 100%"
                placeholder="请输入分数"
              />
        </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="评分评语" prop="gradeComment">
          <el-input 
            v-model="form.gradeComment" 
            type="textarea" 
            :rows="3"
            placeholder="请输入评分评语" 
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 详情查看对话框 -->
    <el-dialog title="提交详情" :visible.sync="viewOpen" width="900px" append-to-body>
      <div class="submission-detail" v-if="viewData">
        <el-row :gutter="20">
          <el-col :span="8">
            <div class="detail-card">
              <h4>学生信息</h4>
              <div class="student-detail">
                <el-avatar :size="60" :src="viewData.studentAvatar || '/img/profile.jpg'"></el-avatar>
                <div class="student-info">
                  <div class="name">{{ viewData.studentName || '未知学生' }}</div>
                  <div class="id">学号: {{ viewData.studentId || '--' }}</div>
                  <div class="class">班级: {{ viewData.className || '--' }}</div>
                </div>
              </div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="detail-card">
              <h4>任务信息</h4>
              <div class="task-detail">
                <div class="task-name">{{ viewData.taskName || '未知任务' }}</div>
                <div class="course-name">{{ viewData.courseName || '未知课程' }}</div>
                <div class="deadline">截止时间: {{ parseTime(viewData.dueDate, '{y}-{m}-{d} {h}:{i}') }}</div>
              </div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="detail-card">
              <h4>提交信息</h4>
              <div class="submission-info">
                <div class="submit-time">提交时间: {{ parseTime(viewData.submissionTime, '{y}-{m}-{d} {h}:{i}') }}</div>
                <div class="status">
                  <el-tag :type="viewData.isGraded === '1' ? 'success' : 'warning'">
                    {{ viewData.isGraded === '1' ? '已评分' : '待评分' }}
                  </el-tag>
                </div>
                <div class="score" v-if="viewData.score">
                  分数: <span class="score-value">{{ viewData.score }}</span> 分
                </div>
              </div>
            </div>
          </el-col>
        </el-row>

        <el-divider content-position="left">提交内容</el-divider>
        <div class="content-detail" v-html="viewData.submissionContent"></div>

        <el-divider content-position="left" v-if="viewData.submissionFile">提交文件</el-divider>
        <div class="file-detail" v-if="viewData.submissionFile">
          <el-link type="primary" :href="viewData.submissionFile" target="_blank">
            <i class="el-icon-document"></i>
            查看文件
          </el-link>
        </div>

        <el-divider content-position="left" v-if="viewData.gradeComment">评分评语</el-divider>
        <div class="comment-detail" v-if="viewData.gradeComment">
          {{ viewData.gradeComment }}
        </div>
      </div>
    </el-dialog>

    <!-- 批量评分对话框 -->
    <el-dialog title="批量评分" :visible.sync="batchGradeOpen" width="600px" append-to-body>
      <el-form ref="batchGradeForm" :model="batchGradeForm" :rules="batchGradeRules" label-width="100px">
        <el-form-item label="评分标准" prop="gradeStandard">
          <el-radio-group v-model="batchGradeForm.gradeStandard">
            <el-radio label="excellent">优秀 (90-100分)</el-radio>
            <el-radio label="good">良好 (80-89分)</el-radio>
            <el-radio label="pass">及格 (60-79分)</el-radio>
            <el-radio label="fail">不及格 (0-59分)</el-radio>
            <el-radio label="custom">自定义分数</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="自定义分数" v-if="batchGradeForm.gradeStandard === 'custom'" prop="customScore">
          <el-input-number
            v-model="batchGradeForm.customScore"
            :min="0"
            :max="100"
            :precision="1"
            style="width: 200px"
          />
        </el-form-item>
        <el-form-item label="评语模板" prop="commentTemplate">
          <el-select v-model="batchGradeForm.commentTemplate" placeholder="请选择评语模板" style="width: 100%">
            <el-option label="作业完成度很高，思路清晰，值得表扬！" value="excellent" />
            <el-option label="作业质量不错，但还有提升空间。" value="good" />
            <el-option label="作业基本完成，需要继续努力。" value="pass" />
            <el-option label="作业需要重新提交，请认真完成。" value="fail" />
            <el-option label="自定义评语" value="custom" />
          </el-select>
        </el-form-item>
        <el-form-item label="自定义评语" v-if="batchGradeForm.commentTemplate === 'custom'" prop="customComment">
          <el-input
            v-model="batchGradeForm.customComment"
            type="textarea"
            :rows="3"
            placeholder="请输入自定义评语"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitBatchGrade">确 定</el-button>
        <el-button @click="batchGradeOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listSubmission, getSubmission, delSubmission, addSubmission, updateSubmission } from "@/api/system/submission"
import * as echarts from 'echarts'
import { parseTime as ruoyiParseTime } from '@/utils/ruoyi'

export default {
  name: "Submission",
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      submissionList: [],
      title: "",
      open: false,
      viewOpen: false,
      batchGradeOpen: false,
      viewData: null,
      batchGradeForm: {
        gradeStandard: 'good',
        customScore: 85,
        commentTemplate: 'good',
        customComment: ''
      },
      batchGradeRules: {
        gradeStandard: [
          { required: true, message: "请选择评分标准", trigger: "change" }
        ]
      },
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        studentName: null,
        taskName: null,
        courseName: null,
        submissionTime: null,
        isGraded: null,
      },
      form: {
        userId: null
      },
      rules: {
        recordId: [
          { required: true, message: "学习记录ID不能为空", trigger: "blur" }
        ],
        taskId: [
          { required: true, message: "任务ID不能为空", trigger: "blur" }
        ],
        submissionTime: [
          { required: true, message: "提交时间不能为空", trigger: "blur" }
        ],
      },
      // 图表实例
      trendChart: null,
      scoreChart: null,
      // 模拟数据 - 留空占位，已不再使用
      mockData: {}
    }
  },
  computed: {
    pendingCount() {
      return this.submissionList.filter(item => item.isGraded === '0').length
    },
    gradedCount() {
      return this.submissionList.filter(item => item.isGraded === '1').length
    },
    avgScore() {
      const gradedItems = this.submissionList.filter(item => item.isGraded === '1' && item.score)
      if (gradedItems.length === 0) return '--'
      const total = gradedItems.reduce((sum, item) => sum + parseFloat(item.score), 0)
      return (total / gradedItems.length).toFixed(1)
    },
    // 评分进度
    gradingProgress() {
      if (this.total === 0) return 0
      return Math.round((this.gradedCount / this.total) * 100)
    },
    // 分数统计
    maxScore() {
      const gradedItems = this.submissionList.filter(item => item.isGraded === '1' && item.score)
      if (gradedItems.length === 0) return '--'
      return Math.max(...gradedItems.map(item => parseFloat(item.score)))
    },
    minScore() {
      const gradedItems = this.submissionList.filter(item => item.isGraded === '1' && item.score)
      if (gradedItems.length === 0) return '--'
      return Math.min(...gradedItems.map(item => parseFloat(item.score)))
    },
    // 分数趋势
    scoreTrendClass() {
      const avg = parseFloat(this.avgScore)
      if (avg >= 85) return 'trend-up'
      if (avg >= 70) return 'trend-stable'
      return 'trend-down'
    },
    scoreTrendIcon() {
      const avg = parseFloat(this.avgScore)
      if (avg >= 85) return 'el-icon-top'
      if (avg >= 70) return 'el-icon-minus'
      return 'el-icon-bottom'
    },
    scoreTrendText() {
      const avg = parseFloat(this.avgScore)
      if (avg >= 85) return '优秀'
      if (avg >= 70) return '良好'
      return '需改进'
    },
    // 活跃度
    activityLevel() {
      const score = this.activityScore
      if (score >= 4) return '极高'
      if (score >= 3) return '高'
      if (score >= 2) return '中等'
      return '低'
    },
    activityScore() {
      // 模拟活跃度计算
      return Math.min(5, Math.max(1, Math.floor(this.weeklySubmissions / 5)))
    },
    weeklySubmissions() {
      // 模拟本周提交数
      return Math.floor(Math.random() * 20) + 10
    },
    monthlySubmissions() {
      // 模拟本月提交数
      return Math.floor(Math.random() * 80) + 40
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.initCharts()
    })
    // 监听窗口大小变化
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    // 移除事件监听
    window.removeEventListener('resize', this.handleResize)
    // 销毁图表实例
    if (this.trendChart) {
      this.trendChart.dispose()
    }
    if (this.scoreChart) {
      this.scoreChart.dispose()
    }
  },
  created() {
    this.getList()
  },
  methods: {
    // 初始化图表
    initCharts() {
      this.initTrendChart()
      this.initScoreChart()
    },
    // 初始化趋势图
    initTrendChart() {
      if (!this.$refs.trendChart) return
      
      try {
        this.trendChart = echarts.init(this.$refs.trendChart)
        
        // 根据实际数据计算最近12周的提交趋势
        const now = new Date()
        const oneDay = 24 * 60 * 60 * 1000
        const oneWeek = 7 * oneDay
        const weeks = []
        const weeklySubmissions = Array(12).fill(0)
        
        // 生成最近12周的日期范围
        for (let i = 11; i >= 0; i--) {
          const endDate = new Date(now - i * oneWeek)
          const startDate = new Date(endDate - oneWeek + oneDay)
          const weekLabel = `第${12-i}周`
          weeks.push(weekLabel)
        }
        
        // 按周统计提交数量
        this.submissionList.forEach(submission => {
          if (submission.submissionTime) {
            const submissionDate = new Date(submission.submissionTime.replace(/-/g, '/'))
            for (let i = 0; i < 12; i++) {
              const weekEnd = new Date(now - i * oneWeek)
              const weekStart = new Date(weekEnd - oneWeek + oneDay)
              if (submissionDate >= weekStart && submissionDate <= weekEnd) {
                weeklySubmissions[11-i]++
                break
              }
            }
          }
        })
        
        // 当没有数据时显示提示信息
        const hasData = weeklySubmissions.some(count => count > 0)
        
        const option = {
          title: {
            text: '近12周提交趋势',
            left: 'center',
            textStyle: {
              fontSize: 14,
              fontWeight: 'normal'
            },
            subtext: hasData ? '' : '暂无提交数据',
            subtextStyle: {
              color: '#909399'
            }
          },
          tooltip: {
            trigger: 'axis',
            axisPointer: {
              type: 'cross'
            }
          },
          grid: {
            left: '3%',
            right: '4%',
            bottom: '3%',
            containLabel: true
          },
          xAxis: {
            type: 'category',
            data: weeks,
            axisLine: {
              lineStyle: {
                color: '#ddd'
              }
            }
          },
          yAxis: {
            type: 'value',
            name: '提交数量',
            minInterval: 1,  // 确保Y轴刻度为整数
            axisLine: {
              lineStyle: {
                color: '#ddd'
              }
            }
          },
          series: [{
            name: '提交数量',
            data: weeklySubmissions,
            type: 'line',
            smooth: true,
            lineStyle: {
              color: '#409EFF',
              width: 3
            },
            areaStyle: {
              color: {
                type: 'linear',
                x: 0,
                y: 0,
                x2: 0,
                y2: 1,
                colorStops: [{
                  offset: 0, color: 'rgba(64,158,255,0.3)'
                }, {
                  offset: 1, color: 'rgba(64,158,255,0.1)'
                }]
              }
            },
            itemStyle: {
              color: '#409EFF',
              borderWidth: 2,
              borderColor: '#fff'
            }
          }]
        }
        this.trendChart.setOption(option)
        
        // 添加图表加载完成状态
        this.$nextTick(() => {
          const container = this.$refs.trendChart.parentElement
          if (container) {
            container.classList.add('loaded')
          }
        })
      } catch (error) {
        console.error('趋势图初始化失败:', error)
        this.$message.error('图表加载失败，请刷新页面重试')
      }
    },
    // 初始化评分分布图
    initScoreChart() {
      if (!this.$refs.scoreChart) return
      
      try {
        this.scoreChart = echarts.init(this.$refs.scoreChart)
        
        // 计算真实评分分布数据
        const scoreDistribution = [
          { name: '优秀(90-100)', value: 0, color: '#67C23A' },
          { name: '良好(80-89)', value: 0, color: '#409EFF' },
          { name: '中等(70-79)', value: 0, color: '#E6A23C' },
          { name: '及格(60-69)', value: 0, color: '#F56C6C' },
          { name: '不及格(0-59)', value: 0, color: '#909399' }
        ]
        
        // 使用实际表格数据计算分布
        const gradedItems = this.submissionList.filter(item => item.isGraded === '1' && item.score)
        gradedItems.forEach(item => {
          const score = parseFloat(item.score)
          if (score >= 90) {
            scoreDistribution[0].value++
          } else if (score >= 80) {
            scoreDistribution[1].value++
          } else if (score >= 70) {
            scoreDistribution[2].value++
          } else if (score >= 60) {
            scoreDistribution[3].value++
          } else {
            scoreDistribution[4].value++
          }
        })
        
        // 当没有数据时显示提示信息
        const hasData = scoreDistribution.some(item => item.value > 0)
        
        const option = {
          title: {
            text: '评分分布统计',
            left: 'center',
            textStyle: {
              fontSize: 14,
              fontWeight: 'normal'
            },
            subtext: hasData ? '' : '暂无评分数据',
            subtextStyle: {
              color: '#909399'
            }
          },
          tooltip: {
            trigger: 'item',
            formatter: '{a} <br/>{b}: {c} ({d}%)'
          },
          legend: {
            orient: 'vertical',
            left: 'left',
            data: scoreDistribution.map(item => item.name)
          },
          series: [{
            name: '评分分布',
            type: 'pie',
            radius: ['40%', '70%'],
            center: ['60%', '50%'],
            avoidLabelOverlap: false,
            label: {
              show: false,
              position: 'center'
            },
            emphasis: {
              label: {
                show: true,
                fontSize: '18',
                fontWeight: 'bold'
              }
            },
            labelLine: {
              show: false
            },
            data: scoreDistribution
          }]
        }
        this.scoreChart.setOption(option)
        
        // 添加图表加载完成状态
        this.$nextTick(() => {
          const container = this.$refs.scoreChart.parentElement
          if (container) {
            container.classList.add('loaded')
          }
        })
      } catch (error) {
        console.error('评分分布图初始化失败:', error)
        this.$message.error('图表加载失败，请刷新页面重试')
      }
    },
    // 刷新趋势图
    refreshTrendChart() {
      if (this.trendChart) {
        this.trendChart.dispose()
        this.initTrendChart()
      }
    },
    // 刷新评分图
    refreshScoreChart() {
      if (this.scoreChart) {
        this.scoreChart.dispose()
        this.initScoreChart()
      }
    },
    // 包装 parseTime, 输出调试日志
    parseTime() {
      const res = ruoyiParseTime.apply(this, arguments)
      if (process.env.NODE_ENV !== 'production') {
        console.debug('[SubmissionIndex] parseTime 输入:', arguments[0], '输出:', res)
      }
      return res
    },
    getList() {
      this.loading = true
      listSubmission(this.queryParams).then(response => {
        // 兼容后端只返回 gradeScore 的情况
        this.submissionList = response.rows.map(item => {
          if (item.score == null && item.gradeScore != null) {
            item.score = item.gradeScore
          }
          return item
        })
        this.total = response.total
        console.table(this.submissionList.map(r => ({ id: r.submissionId, submissionTime: r.submissionTime, score: r.score, gradeScore: r.gradeScore })))
        this.loading = false
        // 数据更新后重新渲染图表
        this.$nextTick(() => {
          this.refreshTrendChart()
          this.refreshScoreChart()
        })
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        submissionId: null,
        recordId: null,
        taskId: null,
        studentName: null,
        studentAvatar: null,
        taskName: null,
        courseName: null,
        dueDate: null,
        submissionContent: null,
        submissionFile: null,
        submissionTime: null,
        isGraded: '0',
        score: null,
        gradeComment: null,
        className: null,
        studentId: null
      }
      this.resetForm("form")
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.submissionId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    openAddDialog() {
      this.form = { userId: this.$store.getters.id };
      this.open = true;
    },
    handleAdd() {
      this.form.userId = this.$store.getters.id;
      addSubmission(this.form).then(() => {
        this.$modal.msgSuccess('提交成功');
        this.open = false;
        this.getList();
      });
    },
    handleView(row) {
      this.viewData = row
      this.viewOpen = true
    },
    handleUpdate(row) {
      this.reset()
      const submissionId = row.submissionId || this.ids
      getSubmission(submissionId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改任务提交记录"
      })
    },
    handleGrade(row) {
      this.reset()
      const submissionId = row.submissionId
      getSubmission(submissionId).then(response => {
        this.form = response.data
        this.form.isGraded = '1'
        this.open = true
        this.title = "评分任务提交"
      })
    },
    handleBatchGrade() {
      if (this.ids.length === 0) {
        this.$modal.msgError("请选择要评分的记录")
        return
      }
      this.batchGradeOpen = true
    },
    submitBatchGrade() {
      this.$refs["batchGradeForm"].validate(valid => {
        if (valid) {
          let score = 85
          switch (this.batchGradeForm.gradeStandard) {
            case 'excellent': score = 95; break
            case 'good': score = 85; break
            case 'pass': score = 70; break
            case 'fail': score = 50; break
            case 'custom': score = this.batchGradeForm.customScore; break
          }
          let comment = '作业质量不错，但还有提升空间。'
          switch (this.batchGradeForm.commentTemplate) {
            case 'excellent': comment = '作业完成度很高，思路清晰，值得表扬！'; break
            case 'good': comment = '作业质量不错，但还有提升空间。'; break
            case 'pass': comment = '作业基本完成，需要继续努力。'; break
            case 'fail': comment = '作业需要重新提交，请认真完成。'; break
            case 'custom': comment = this.batchGradeForm.customComment; break
          }
          const promises = this.ids.map(id => {
            return updateSubmission({
              submissionId: id,
              isGraded: '1',
              score: score,
              gradeComment: comment
            })
          })
          Promise.all(promises).then(() => {
            this.$modal.msgSuccess("批量评分成功")
            this.batchGradeOpen = false
            this.getList()
            // 通知个人页面刷新任务提交记录
            this.$root.$emit('submissionRecordUpdated')
          })
        }
      })
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.submissionId != null) {
            updateSubmission(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
              // 通知个人页面刷新任务提交记录
              this.$root.$emit('submissionRecordUpdated')
            })
          } else {
            addSubmission(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
              // 通知个人页面刷新任务提交记录
              this.$root.$emit('submissionRecordUpdated')
            })
          }
        }
      })
    },
    handleDelete(row) {
      const submissionIds = row && row.submissionId ? [row.submissionId] : this.ids
      this.$modal.confirm('是否确认删除选中的任务提交记录？').then(function() {
        return delSubmission(submissionIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
        // 通知个人页面刷新任务提交记录
        this.$root.$emit('submissionRecordUpdated')
      }).catch(() => {})
    },
    handleExport() {
      this.download('system/submission/export', {
        ...this.queryParams
      }, `task_submission_${new Date().getTime()}.xlsx`)
    },
    resetForm(refName) {
      if (this.$refs[refName]) {
        this.$refs[refName].resetFields();
      }
    },
    getScoreColor(score) {
      if (score >= 90) return '#67C23A'
      if (score >= 80) return '#409EFF'
      if (score >= 70) return '#E6A23C'
      if (score >= 60) return '#409EFF'
      return '#F56C6C'
    },
    getScoreLevel(score) {
      if (score >= 90) return '优秀'
      if (score >= 80) return '良好'
      if (score >= 70) return '中等'
      if (score >= 60) return '及格'
      return '不及格'
    },
    handleResize() {
      // 处理窗口大小变化，重新调整图表大小
      if (this.trendChart) {
        this.trendChart.resize()
      }
      if (this.scoreChart) {
        this.scoreChart.resize()
      }
    },
    refreshMockData() {
      // 重新加载后端数据
      this.loading = true
      listSubmission(this.queryParams).then(response => {
        this.submissionList = response.rows
        this.total = response.total
        this.loading = false
        
        // 数据加载后重新渲染图表
        this.$nextTick(() => {
          this.refreshTrendChart()
          this.refreshScoreChart()
          this.$message.success('数据已刷新')
        })
      }).catch(() => {
        this.loading = false
        // 如果后端数据加载失败，仍然刷新图表显示
        this.refreshTrendChart()
        this.refreshScoreChart()
        this.$message.warning('数据刷新部分成功')
      })
    }
  }
}
</script>

<style scoped>
.app-container {
  padding: 20px;
  background: #f5f7fa;
  min-height: calc(100vh - 84px);
}
.mb20 { margin-bottom: 20px; }
.mb8 { margin-bottom: 8px; }
/* 统计卡片样式 */
.stat-card { 
  border-radius: 12px; 
  border: none; 
  transition: all 0.3s ease; 
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.stat-card:hover { 
  transform: translateY(-5px); 
  box-shadow: 0 8px 25px rgba(0,0,0,0.12);
}
.stat-content { 
  display: flex; 
  align-items: center; 
  padding: 15px;
}
.stat-icon { 
  width: 60px; 
  height: 60px; 
  border-radius: 12px; 
  display: flex; 
  align-items: center; 
  justify-content: center; 
  margin-right: 15px;
  transition: transform 0.3s ease;
}
.stat-card:hover .stat-icon {
  transform: scale(1.1);
}
.stat-icon i { 
  font-size: 24px; 
  color: white;
}
.stat-info { 
  flex: 1;
}
.stat-number { 
  font-size: 28px; 
  font-weight: bold; 
  color: #303133; 
  line-height: 1;
  margin-bottom: 4px;
}
.stat-label { 
  font-size: 14px; 
  color: #909399;
}
/* 搜索卡片样式 */
.search-card { 
  border-radius: 12px; 
  border: none; 
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.action-card { 
  border-radius: 12px; 
  border: none; 
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
.table-card { 
  border-radius: 12px; 
  border: none;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
}
/* 学生信息样式 */
.student-info { 
  display: flex; 
  align-items: center; 
  padding: 10px;
}
.student-details { 
  margin-left: 10px; 
  text-align: left;
}
.student-name { 
  font-weight: bold; 
  color: #303133; 
  margin-bottom: 2px;
}
.student-id { 
  font-size: 12px; 
  color: #909399;
}
/* 任务信息样式 */
.task-info { 
  text-align: left; 
  padding: 10px;
}
.task-name { 
  font-weight: bold; 
  color: #303133; 
  margin-bottom: 5px;
}
.task-course { 
  font-size: 12px; 
  color: #409EFF; 
  margin-bottom: 3px;
}
.task-deadline { 
  font-size: 12px; 
  color: #F56C6C;
}
/* 提交内容样式 */
.submission-content { 
  text-align: left; 
  padding: 10px;
}
.content-preview { 
  font-size: 13px; 
  color: #606266; 
  line-height: 1.4; 
  margin-bottom: 5px;
}
.file-info { 
  font-size: 12px; 
  color: #409EFF;
}
.file-info i { 
  margin-right: 3px;
}
/* 提交时间样式 */
.submission-time { 
  text-align: center;
}
.time { 
  font-weight: bold; 
  color: #303133; 
  margin-bottom: 2px;
}
.time-detail { 
  font-size: 12px; 
  color: #909399;
}
/* 分数显示样式 */
.score-display { 
  text-align: center;
}
.score-progress {
  display: flex;
  align-items: center;
  justify-content: center;
}
.score-text {
  margin-left: 10px;
}
.score { 
  font-size: 16px; 
  font-weight: bold; 
  color: #67C23A;
}
.score-unit { 
  font-size: 12px; 
  color: #909399; 
  margin-left: 2px;
}
.score-level {
  margin-top: 5px;
  font-size: 12px;
  color: #909399;
}
.no-score { 
  color: #C0C4CC; 
  font-size: 14px;
}
/* 详情对话框样式 */
.submission-detail { 
  padding: 20px;
}
.detail-card { 
  background: #f8f9fa; 
  border-radius: 8px; 
  padding: 15px; 
  height: 100%;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
}
.detail-card h4 { 
  margin: 0 0 15px 0; 
  color: #303133; 
  font-size: 16px;
}
.student-detail { 
  display: flex; 
  align-items: center;
}
.student-info .name { 
  font-weight: bold; 
  font-size: 16px; 
  margin-bottom: 5px;
}
.student-info .id, .student-info .class { 
  font-size: 12px; 
  color: #909399; 
  margin-bottom: 2px;
}
.task-detail .task-name { 
  font-weight: bold; 
  font-size: 14px; 
  margin-bottom: 5px;
}
.task-detail .course-name { 
  font-size: 12px; 
  color: #409EFF; 
  margin-bottom: 3px;
}
.task-detail .deadline { 
  font-size: 12px; 
  color: #F56C6C;
}
.submission-info .submit-time { 
  font-size: 12px; 
  color: #606266; 
  margin-bottom: 8px;
}
.submission-info .status { 
  margin-bottom: 8px;
}
.submission-info .score { 
  font-size: 12px; 
  color: #606266;
}
.submission-info .score-value { 
  font-weight: bold; 
  color: #67C23A; 
  font-size: 16px;
}
.content-detail { 
  background: #f8f9fa; 
  border-radius: 8px; 
  padding: 15px; 
  min-height: 100px; 
  line-height: 1.6;
  box-shadow: 0 1px 4px rgba(0,0,0,0.05);
}
.file-detail { 
  padding: 15px;
}
.comment-detail { 
  background: #f0f9ff; 
  border-left: 4px solid #409EFF; 
  padding: 15px; 
  border-radius: 4px; 
  line-height: 1.6;
}
/* 表格美化增强 */
.el-table th {
  background: #f5f7fa !important;
  font-weight: 600;
  color: #303133;
  border-bottom: 1px solid #ebeef5;
}
.el-table .el-table__row:hover {
  background: #f5f7fa !important;
}
.el-table .el-button--text {
  transition: all 0.2s ease;
}
.el-table .el-button--text:hover {
  color: #409EFF;
  transform: translateY(-1px);
}
/* 按钮美化 */
.el-button {
  border-radius: 6px;
  transition: all 0.2s ease;
}
.el-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}
/* 搜索表单美化 */
.el-form-item__label {
  font-weight: 500;
  color: #303133;
}
.el-input__inner {
  border-radius: 6px;
  transition: all 0.2s ease;
}
.el-input__inner:focus {
  border-color: #409EFF;
  box-shadow: 0 0 0 2px rgba(64,158,255,0.1);
}
/* 响应式设计 */
@media (max-width: 768px) {
  .app-container { 
    padding: 10px; 
  }
  .stat-content { 
    flex-direction: column; 
    text-align: center; 
  }
  .stat-icon { 
    margin-right: 0; 
    margin-bottom: 10px; 
  }
}

/* 图表卡片样式 */
.chart-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  transition: all 0.3s ease;
}
.chart-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0,0,0,0.1);
}
.chart-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-weight: 600;
  color: #303133;
}
.chart-container {
  padding: 10px 0;
}
.chart {
  width: 100%;
}

/* 进度卡片样式 */
.progress-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  transition: all 0.3s ease;
  padding: 20px;
}
.progress-card:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 16px rgba(0,0,0,0.1);
}
.progress-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}
.progress-title {
  font-weight: 600;
  color: #303133;
  font-size: 14px;
}
.progress-percentage {
  font-weight: bold;
  color: #409EFF;
  font-size: 16px;
}
.progress-details {
  display: flex;
  justify-content: space-between;
  margin-top: 10px;
  font-size: 12px;
  color: #909399;
}

/* 分数趋势指示器 */
.score-trend {
  margin: 15px 0;
}
.trend-indicator {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 8px 12px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: 500;
}
.trend-indicator i {
  margin-right: 5px;
}
.trend-up {
  background: rgba(103, 194, 58, 0.1);
  color: #67C23A;
}
.trend-stable {
  background: rgba(230, 162, 60, 0.1);
  color: #E6A23C;
}
.trend-down {
  background: rgba(245, 108, 108, 0.1);
  color: #F56C6C;
}

/* 活跃度指示器 */
.activity-indicator {
  margin: 15px 0;
}
.activity-dots {
  display: flex;
  justify-content: center;
  gap: 8px;
}
.dot {
  width: 12px;
  height: 12px;
  border-radius: 50%;
  background: #E4E7ED;
  transition: all 0.3s ease;
}
.dot.active {
  background: #409EFF;
  transform: scale(1.2);
  box-shadow: 0 2px 4px rgba(64, 158, 255, 0.3);
}

/* 动画效果 */
@keyframes fadeInUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}
.chart-card, .progress-card {
  animation: fadeInUp 0.6s ease-out;
}
.chart-card:nth-child(1) { animation-delay: 0.1s; }
.chart-card:nth-child(2) { animation-delay: 0.2s; }
.progress-card:nth-child(1) { animation-delay: 0.3s; }
.progress-card:nth-child(2) { animation-delay: 0.4s; }
.progress-card:nth-child(3) { animation-delay: 0.5s; }

/* 进度条美化 */
.el-progress-bar__outer {
  border-radius: 10px;
  background: #F5F7FA;
}
.el-progress-bar__inner {
  border-radius: 10px;
  transition: all 0.3s ease;
}

/* 图表加载状态 */
.chart-container {
  position: relative;
}
.chart-container::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  width: 20px;
  height: 20px;
  border: 2px solid #f3f3f3;
  border-top: 2px solid #409EFF;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  z-index: 1;
}
.chart-container.loaded::before {
  display: none;
}
@keyframes spin {
  0% { transform: translate(-50%, -50%) rotate(0deg); }
  100% { transform: translate(-50%, -50%) rotate(360deg); }
}
</style>
