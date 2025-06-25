<template>
  <div class="app-container">
    <!-- 统计概览卡片 -->
    <el-row :gutter="20" class="mb20">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);">
              <i class="el-icon-video-camera"></i>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ total }}</div>
              <div class="stat-label">总观看记录</div>
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
              <div class="stat-number">{{ formatTotalTime(totalWatchTime) }}</div>
              <div class="stat-label">总观看时长</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);">
              <i class="el-icon-data-analysis"></i>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ avgCompletionRate }}%</div>
              <div class="stat-label">平均完成率</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon" style="background: linear-gradient(135deg, #43e97b 0%, #38f9d7 100%);">
              <i class="el-icon-user"></i>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ activeLearners }}</div>
              <div class="stat-label">活跃学习者</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 高级搜索区域 -->
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
            <el-form-item label="视频资源" prop="resourceName">
        <el-input
                v-model="queryParams.resourceName"
                placeholder="请输入视频资源名称"
          clearable
                prefix-icon="el-icon-video-camera"
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="完成状态" prop="completionStatus">
              <el-select v-model="queryParams.completionStatus" placeholder="请选择完成状态" clearable style="width: 100%">
                <el-option label="未开始" value="not_started" />
                <el-option label="进行中" value="in_progress" />
                <el-option label="已完成" value="completed" />
                <el-option label="已暂停" value="paused" />
              </el-select>
      </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="观看时间" prop="lastWatchTime">
              <el-date-picker
                v-model="queryParams.lastWatchTime"
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
            <el-form-item label="完成率范围" prop="completionRange">
              <div class="completion-range-container">
                <div class="range-labels">
                  <span class="range-label">0%</span>
                  <span class="range-label">25%</span>
                  <span class="range-label">50%</span>
                  <span class="range-label">75%</span>
                  <span class="range-label">100%</span>
                </div>
                <el-slider
                  v-model="queryParams.completionRange"
                  range
                  :min="0"
                  :max="100"
                  :step="5"
                  :show-tooltip="true"
                  :format-tooltip="(val) => val + '%'"
                  style="width: 100%; margin: 10px 0;"
                />
                <div class="range-display">
                  <el-tag type="primary" size="small">
                    选择范围: {{ queryParams.completionRange[0] }}% - {{ queryParams.completionRange[1] }}%
                  </el-tag>
                </div>
                <div class="quick-select">
                  <span class="quick-label">快速选择:</span>
                  <el-button size="mini" type="text" @click="setRange([0, 25])">0-25%</el-button>
                  <el-button size="mini" type="text" @click="setRange([25, 50])">25-50%</el-button>
                  <el-button size="mini" type="text" @click="setRange([50, 75])">50-75%</el-button>
                  <el-button size="mini" type="text" @click="setRange([75, 100])">75-100%</el-button>
                  <el-button size="mini" type="text" @click="setRange([0, 100])">全部</el-button>
                </div>
              </div>
      </el-form-item>
          </el-col>
          <el-col :span="8">
            <div class="search-buttons">
              <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
              <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
            </div>
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
          @click="handleAdd"
            v-hasPermi="['system:record:add']"
          >新增记录</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          icon="el-icon-edit"
            size="small"
          :disabled="single"
          @click="handleUpdate"
            v-hasPermi="['system:record:edit']"
          >批量编辑</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="warning"
            icon="el-icon-download"
            size="small"
            @click="handleExport"
            v-hasPermi="['system:record:export']"
          >导出数据</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          icon="el-icon-delete"
            size="small"
          :disabled="multiple"
          @click="handleDelete"
            v-hasPermi="['system:record:remove']"
          >批量删除</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="info"
            icon="el-icon-data-analysis"
            size="small"
            @click="showAnalytics = true"
          >观看分析</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
            icon="el-icon-refresh"
            size="small"
            @click="testCharts"
          >测试图表</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>
    </el-card>

    <!-- 数据表格 -->
    <el-card class="table-card" shadow="never">
      <el-table
        ref="tables"
        v-loading="loading"
        :data="recordList"
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
                <div class="student-id">ID: {{ scope.row.learningRecordId }}</div>
              </div>
            </div>
        </template>
      </el-table-column>

        <!-- 视频资源信息 -->
        <el-table-column label="视频资源" align="center" min-width="200">
          <template slot-scope="scope">
            <div class="resource-info">
              <div class="resource-name">{{ scope.row.resourceName || '未知资源' }}</div>
              <div class="resource-course" style="color:#67C23A">{{ scope.row.courseName || '未知课程' }}</div>
              <div class="resource-category">{{ scope.row.resourceCategory || '未分类' }}</div>
              <div class="resource-duration">总时长: {{ formatDuration(scope.row.totalDuration) }}</div>
            </div>
          </template>
        </el-table-column>

        <!-- 观看进度 -->
        <el-table-column label="观看进度" align="center" min-width="200">
          <template slot-scope="scope">
            <div class="progress-info">
              <div class="progress-bar-container">
                <el-progress
                  :percentage="parseInt(scope.row.completionRate) || 0"
                  :color="getProgressColor(scope.row.completionRate)"
                  :stroke-width="10"
                  :show-text="false"
                  :text-inside="false"
                />
              </div>
              <div class="progress-text">{{ scope.row.completionRate || 0 }}%</div>
              <div class="progress-status">
                <el-tag :type="getStatusType(scope.row.completionRate)" size="mini">
                  {{ getStatusText(scope.row.completionRate) }}
                </el-tag>
              </div>
            </div>
          </template>
        </el-table-column>

        <!-- 观看时长 -->
        <el-table-column label="观看时长" align="center" width="160">
          <template slot-scope="scope">
            <div class="duration-info">
              <div class="watched-duration">{{ formatDuration(scope.row.watchedDuration) }}</div>
              <div class="total-duration">总时长: {{ formatDuration(scope.row.totalDuration) }}</div>
              <div class="efficiency">效率: {{ calculateEfficiency(scope.row.watchedDuration, scope.row.totalDuration) }}%</div>
            </div>
          </template>
        </el-table-column>

        <!-- 学习行为 -->
        <el-table-column label="学习行为" align="center" width="120">
          <template slot-scope="scope">
            <div class="behavior-info">
              <div class="skip-segments" v-if="scope.row.skipSegments">
                <i class="el-icon-arrow-right"></i> 跳过: {{ scope.row.skipSegments }}
              </div>
              <div class="repeat-segments" v-if="scope.row.repeatSegments">
                <i class="el-icon-refresh"></i> 重复: {{ scope.row.repeatSegments }}
              </div>
              <div class="no-behavior" v-if="!scope.row.skipSegments && !scope.row.repeatSegments">
                正常观看
              </div>
            </div>
          </template>
        </el-table-column>

        <!-- 观看时间 -->
        <el-table-column label="观看时间" align="center" width="160">
          <template slot-scope="scope">
            <div class="time-info">
              <div class="last-watch">最近: {{ parseTime(scope.row.lastWatchTime, '{y}-{m}-{d} {h}:{i}:{s}') }}</div>
              <div class="watch-frequency">频率: {{ getWatchFrequency(scope.row.lastWatchTime) }}</div>
            </div>
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
              icon="el-icon-data-line"
              @click="handleAnalytics(scope.row)"
            >分析</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
              v-hasPermi="['system:record:remove']"
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
            <el-form-item label="学习记录ID" prop="learningRecordId">
              <el-select v-model="form.learningRecordId" placeholder="请选择学习记录" style="width: 100%">
                <el-option
                  v-for="record in learningRecordList"
                  :key="record.recordId"
                  :label="record.studentName + ' - ' + record.courseName"
                  :value="record.recordId"
                />
              </el-select>
        </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="视频资源" prop="resourceId">
              <el-select v-model="form.resourceId" placeholder="请选择视频资源" style="width: 100%">
                <el-option
                  v-for="resource in resourceList"
                  :key="resource.resourceId"
                  :label="resource.resourceName"
                  :value="resource.resourceId"
                />
              </el-select>
        </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
        <el-form-item label="视频总时长" prop="totalDuration">
              <el-input-number
                v-model="form.totalDuration"
                :min="0"
                :precision="0"
                style="width: 100%"
                placeholder="请输入视频总时长(分钟)"
              />
        </el-form-item>
          </el-col>
          <el-col :span="12">
        <el-form-item label="观看时长" prop="watchedDuration">
              <el-input-number
                v-model="form.watchedDuration"
                :min="0"
                :precision="0"
                style="width: 100%"
                placeholder="请输入观看时长(分钟)"
              />
        </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
        <el-form-item label="完成率" prop="completionRate">
              <el-slider
                v-model="form.completionRate"
                :min="0"
                :max="100"
                :step="5"
                show-input
                input-size="mini"
                style="width: 100%"
              />
        </el-form-item>
          </el-col>
          <el-col :span="12">
        <el-form-item label="最后观看时间" prop="lastWatchTime">
              <el-date-picker
            v-model="form.lastWatchTime"
                type="datetime"
                placeholder="请选择最后观看时间"
                value-format="yyyy-MM-dd HH:mm:ss"
                style="width: 100%"
              />
        </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="跳过片段" prop="skipSegments">
              <el-input
                v-model="form.skipSegments"
                type="textarea"
                :rows="2"
                placeholder="请输入跳过的视频片段信息"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="重复观看片段" prop="repeatSegments">
              <el-input
                v-model="form.repeatSegments"
                type="textarea"
                :rows="2"
                placeholder="请输入重复观看的视频片段信息"
              />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 详情查看对话框 -->
    <el-dialog title="视频观看详情" :visible.sync="viewOpen" width="900px" append-to-body>
      <div class="record-detail" v-if="viewData">
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
              <h4>视频资源</h4>
              <div class="resource-detail">
                <div class="resource-name">{{ viewData.resourceName || '未知资源' }}</div>
                <div class="resource-course" style="color:#67C23A">{{ viewData.courseName || '未知课程' }}</div>
                <div class="resource-category">{{ viewData.resourceCategory || '未分类' }}</div>
                <div class="resource-duration">总时长: {{ formatDuration(viewData.totalDuration) }}</div>
              </div>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="detail-card">
              <h4>观看统计</h4>
              <div class="watch-stats">
                <div class="completion">完成率: {{ viewData.completionRate || 0 }}%</div>
                <div class="duration">观看时长: {{ formatDuration(viewData.watchedDuration) }}</div>
                <div class="efficiency">观看效率: {{ calculateEfficiency(viewData.watchedDuration, viewData.totalDuration) }}%</div>
              </div>
            </div>
          </el-col>
        </el-row>

        <el-divider content-position="left">观看进度详情</el-divider>
        <div class="progress-detail">
          <div class="progress-detail-container">
            <el-progress
              :percentage="parseInt(viewData.completionRate) || 0"
              :color="getProgressColor(viewData.completionRate)"
              :stroke-width="25"
              :show-text="true"
              :text-inside="false"
            />
            <div class="progress-detail-info">
              <div class="progress-detail-item">
                <span class="label">观看时长:</span>
                <span class="value">{{ formatDuration(viewData.watchedDuration) }}</span>
              </div>
              <div class="progress-detail-item">
                <span class="label">总时长:</span>
                <span class="value">{{ formatDuration(viewData.totalDuration) }}</span>
              </div>
              <div class="progress-detail-item">
                <span class="label">观看效率:</span>
                <span class="value">{{ calculateEfficiency(viewData.watchedDuration, viewData.totalDuration) }}%</span>
              </div>
            </div>
          </div>
        </div>

        <el-divider content-position="left">学习行为分析</el-divider>
        <div class="behavior-detail">
          <el-row :gutter="20">
            <el-col :span="12">
              <div class="behavior-card">
                <h5>跳过片段</h5>
                <div class="behavior-content">
                  {{ viewData.skipSegments || '无跳过记录' }}
                </div>
              </div>
            </el-col>
            <el-col :span="12">
              <div class="behavior-card">
                <h5>重复观看片段</h5>
                <div class="behavior-content">
                  {{ viewData.repeatSegments || '无重复记录' }}
                </div>
              </div>
            </el-col>
          </el-row>
        </div>

        <el-divider content-position="left">观看时间线</el-divider>
        <div class="timeline-detail">
          <el-timeline>
            <el-timeline-item
              v-for="(activity, index) in viewData.watchActivities"
              :key="index"
              :timestamp="parseTime(activity.time, '{y}-{m}-{d} {h}:{i}:{s}')"
              :type="activity.type"
            >
              {{ activity.description }}
            </el-timeline-item>
          </el-timeline>
        </div>
      </div>
    </el-dialog>

    <!-- 观看分析对话框 -->
    <el-dialog title="观看行为分析" :visible.sync="showAnalytics" width="1000px" append-to-body>
      <div class="analytics-content" v-loading="chartsLoading" element-loading-text="图表加载中...">
        <!-- 调试信息 -->
        <div v-if="chartsLoading === false" class="debug-info">
          <el-alert
            title="图表状态"
            :description="`记录数量: ${recordList.length}, 图表实例: ${completionChart ? '已创建' : '未创建'}`"
            type="info"
            show-icon
            :closable="false"
            style="margin-bottom: 20px;"
          />
        </div>

        <el-row :gutter="20">
          <el-col :span="12">
            <div class="chart-container">
              <h4>完成率分布</h4>
              <div ref="completionChart" style="height: 300px; border: 1px solid #ddd; background: #f9f9f9;">
                <div v-if="!completionChart" style="display: flex; align-items: center; justify-content: center; height: 100%; color: #999;">
                  图表加载中...
                </div>
              </div>
            </div>
          </el-col>
          <el-col :span="12">
            <div class="chart-container">
              <h4>观看时长统计</h4>
              <div ref="durationChart" style="height: 300px; border: 1px solid #ddd; background: #f9f9f9;">
                <div v-if="!durationChart" style="display: flex; align-items: center; justify-content: center; height: 100%; color: #999;">
                  图表加载中...
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
        <el-row :gutter="20" style="margin-top: 20px;">
          <el-col :span="24">
            <div class="chart-container">
              <h4>观看趋势分析</h4>
              <div ref="trendChart" style="height: 300px; border: 1px solid #ddd; background: #f9f9f9;">
                <div v-if="!trendChart" style="display: flex; align-items: center; justify-content: center; height: 100%; color: #999;">
                  图表加载中...
                </div>
              </div>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listRecord, getRecord, delRecord, addRecord, updateRecord } from "@/api/system/videoLearningRecord"
import { parseTime } from "@/utils/ruoyi"
import * as echarts from 'echarts'

export default {
  name: "VideoLearningRecord",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 视频学习记录表格数据
      recordList: [],

      updateTimer: null, // 定时器引用
      currentRecordId: null ,
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否显示详情
      viewOpen: false,
      // 是否显示分析
      showAnalytics: false,
      // 图表加载状态
      chartsLoading: false,
      // 详情数据
      viewData: null,
      // 学习记录列表
      learningRecordList: [],
      // 资源列表
      resourceList: [],
      // 图表实例
      completionChart: null,
      durationChart: null,
      trendChart: null,

      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        studentName: null,
        resourceName: null,
        completionStatus: null,
        lastWatchTime: null,
        completionRange: [0, 100],
        completionRateStart: null,
        completionRateEnd: null,
        params: {}
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        learningRecordId: [
          { required: true, message: "请选择学习记录", trigger: "change" }
        ],
        resourceId: [
          { required: true, message: "请选择视频资源", trigger: "change" }
        ],
        totalDuration: [
          { required: true, message: "请输入视频总时长", trigger: "blur" }
        ],
        watchedDuration: [
          { required: true, message: "请输入观看时长", trigger: "blur" }
        ],
        lastWatchTime: [
          { required: true, message: "请选择最后观看时间", trigger: "blur" }
        ],
      }
    }
  },
  computed: {
    totalWatchTime() {
      return this.recordList.reduce((sum, item) => sum + (parseInt(item.watchedDuration) || 0), 0)
    },
    avgCompletionRate() {
      if (this.recordList.length === 0) return 0
      const total = this.recordList.reduce((sum, item) => sum + (parseInt(item.completionRate) || 0), 0)
      return Math.round(total / this.recordList.length)
    },
    activeLearners() {
      const uniqueLearners = new Set(this.recordList.map(item => item.learningRecordId))
      return uniqueLearners.size
    }
  },
  created() {
    this.getList()
    this.getLearningRecordList()
    this.getResourceList()
  },
  mounted() {
    // 监听窗口大小变化，重绘图表
    window.addEventListener('resize', this.handleResize)
  },
  beforeDestroy() {
    // 移除事件监听器
    window.removeEventListener('resize', this.handleResize)
    // 销毁图表实例
    if (this.completionChart) {
      this.completionChart.dispose()
    }
    if (this.durationChart) {
      this.durationChart.dispose()
    }
    if (this.trendChart) {
      this.trendChart.dispose()
    }
  },
  methods: {
    // 定时更新观看记录
startUpdateTimer(recordId) {
  this.currentRecordId = recordId;
  this.updateTimer = setInterval(() => {
    this.updateWatchTime();
  }, 60000); // 每分钟更新一次
},

// 更新观看时长
updateWatchTime() {
  const currentRecord = this.recordList.find(item => item.recordId === this.currentRecordId);
  if (currentRecord) {
    currentRecord.watchedDuration += 60; // 增加60秒
    currentRecord.completionRate = this.calculateEfficiency(currentRecord.watchedDuration, currentRecord.totalDuration);
    updateRecord(currentRecord).then(response => {
      console.log('观看记录更新成功:', response);
    }).catch(error => {
      console.error('观看记录更新失败:', error);
    });
  }
},
    // 直接引用工具函数，保持统一解析逻辑
    parseTime,
    // 将秒数格式化为"X小时Y分钟"
    formatDuration(seconds) {
      if (!seconds && seconds !== 0) return '--'
      const sec = parseInt(seconds)
      const hours = Math.floor(sec / 3600)
      const mins = Math.floor((sec % 3600) / 60)
      return hours > 0 ? `${hours}小时${mins}分钟` : `${mins}分钟`
    },
    // 将秒数格式化为"X小时Y分钟"
    formatTotalTime(seconds) {
      if (!seconds && seconds !== 0) return '0小时'
      const sec = parseInt(seconds)
      const hours = Math.floor(sec / 3600)
      const mins = Math.floor((sec % 3600) / 60)
      return hours > 0 ? `${hours}小时${mins}分钟` : `${mins}分钟`
    },
    calculateEfficiency(watched, total) {
      if (!watched || !total) return 0
      return Math.round((watched / total) * 100)
    },
    getProgressColor(progress) {
      const p = parseInt(progress) || 0
      if (p >= 80) return '#67C23A'
      if (p >= 60) return '#E6A23C'
      if (p >= 30) return '#F56C6C'
      return '#909399'
    },
    getStatusType(progress) {
      const p = parseInt(progress) || 0
      if (p >= 100) return 'success'
      if (p >= 80) return 'warning'
      if (p >= 50) return 'info'
      return 'danger'
    },
    getStatusText(progress) {
      const p = parseInt(progress) || 0
      if (p >= 100) return '已完成'
      if (p >= 80) return '即将完成'
      if (p >= 50) return '进行中'
      return '刚开始'
    },
    getWatchFrequency(lastWatchTime) {
      if (!lastWatchTime) return '--'
      const now = new Date()
      const last = new Date(lastWatchTime)
      const diffDays = Math.floor((now - last) / (1000 * 60 * 60 * 24))
      if (diffDays === 0) return '今天'
      if (diffDays === 1) return '昨天'
      if (diffDays < 7) return `${diffDays}天前`
      return `${Math.floor(diffDays / 7)}周前`
    },
    getList() {
      this.loading = true
      listRecord(this.queryParams).then(response => {
        this.recordList = response.rows
        this.total = response.total
        this.loading = false
      }).catch(err => {
        this.loading = false
        console.error(err)
        if (this.$message) this.$message.error('获取数据失败')
      })
    },
    getLearningRecordList() {
      // 这里应该调用获取学习记录列表的API
      this.learningRecordList = [
        { recordId: 1, studentName: '张三', courseName: '高等数学' },
        { recordId: 2, studentName: '李四', courseName: '线性代数' },
        { recordId: 3, studentName: '王五', courseName: '概率论' }
      ]
    },
    getResourceList() {
      // 这里应该调用获取资源列表的API
      this.resourceList = [
        { resourceId: 1, resourceName: '高等数学第一章' },
        { resourceId: 2, resourceName: '线性代数基础' },
        { resourceId: 3, resourceName: '概率论入门' }
      ]
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        recordId: null,
        learningRecordId: null,
        resourceId: null,
        totalDuration: null,
        watchedDuration: null,
        skipSegments: null,
        repeatSegments: null,
        completionRate: 0,
        lastWatchTime: null
      }
      this.resetForm("form")
    },
    handleQuery() {
      this.loading = true;
      this.queryParams.pageNum = 1;

      // 确保params对象存在
      if (!this.queryParams.params) {
        this.queryParams.params = {};
      }

      // 处理完成率范围参数
      if (this.queryParams.completionRange && this.queryParams.completionRange.length === 2) {
        this.queryParams.completionRateStart = this.queryParams.completionRange[0];
        this.queryParams.completionRateEnd = this.queryParams.completionRange[1];
      }

      // 处理日期范围参数
      if (this.queryParams.lastWatchTime && this.queryParams.lastWatchTime.length === 2) {
        this.queryParams.params.beginTime = this.queryParams.lastWatchTime[0];
        this.queryParams.params.endTime = this.queryParams.lastWatchTime[1];
      } else {
        this.queryParams.params.beginTime = null;
        this.queryParams.params.endTime = null;
      }

      // 打印查询参数，方便调试
      console.log('查询参数:', JSON.stringify(this.queryParams));

      this.getList();
    },
    resetQuery() {
      this.resetForm("queryForm");

      // 重置查询参数
      this.queryParams = {
        pageNum: 1,
        pageSize: 10,
        studentName: null,
        resourceName: null,
        completionStatus: null,
        lastWatchTime: null,
        completionRange: [0, 100],
        completionRateStart: null,
        completionRateEnd: null,
        params: {}
      };

      this.handleQuery();
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.recordId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "新增视频学习记录"
    },
    handleView(row) {
      this.viewData = {
        ...row,
        watchActivities: [
          { time: '2025-06-22 10:00', type: 'primary', description: '开始观看视频' },
          { time: '2025-06-22 10:30', type: 'success', description: '完成第一章观看' },
          { time: '2025-06-22 11:00', type: 'warning', description: '暂停观看' }
        ]
      }
      this.viewOpen = true
    },
    handleUpdate(row) {
      this.reset()
      const recordId = row.recordId || this.ids
      getRecord(recordId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改视频学习记录"
      })
    },
    handleAnalytics(row) {
      this.showAnalytics = true
      this.chartsLoading = true

      // 延迟初始化图表，确保DOM已经渲染
      setTimeout(() => {
        this.$nextTick(() => {
          try {
            this.initCharts()
            this.updateCharts(row)
            this.chartsLoading = false
          } catch (error) {
            console.error('图表初始化失败:', error)
            this.chartsLoading = false
            this.$message.error('图表加载失败，请重试')
          }
        })
      }, 300)
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.recordId != null) {
            updateRecord(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addRecord(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    handleDelete(row) {
      const recordIds = row.recordId || this.ids
      this.$modal.confirm('是否确认删除选中的视频学习记录？').then(function() {
        return delRecord(recordIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    handleExport() {
      this.download('system/record/export', {
        ...this.queryParams
      }, `video_learning_record_${new Date().getTime()}.xlsx`)
    },
    resetForm(refName) {
      if (this.$refs[refName]) {
        this.$refs[refName].resetFields();
      }
    },
    initCharts() {
      try {
        // 检查容器是否存在
        if (this.$refs.completionChart) {
          this.completionChart = echarts.init(this.$refs.completionChart)
        }
        if (this.$refs.durationChart) {
          this.durationChart = echarts.init(this.$refs.durationChart)
        }
        if (this.$refs.trendChart) {
          this.trendChart = echarts.init(this.$refs.trendChart)
        }
      } catch (error) {
        console.error('图表初始化失败:', error)
      }
    },
    updateCharts(data) {
      try {
        // 基于实际数据生成图表数据
        const recordData = this.recordList || []

        // 计算完成率分布
        const completionRanges = {
          '0-25%': 0,
          '25-50%': 0,
          '50-75%': 0,
          '75-100%': 0,
          '已完成': 0
        }

        recordData.forEach(record => {
          const rate = parseInt(record.completionRate) || 0
          if (rate === 100) {
            completionRanges['已完成']++
          } else if (rate >= 75) {
            completionRanges['75-100%']++
          } else if (rate >= 50) {
            completionRanges['50-75%']++
          } else if (rate >= 25) {
            completionRanges['25-50%']++
          } else {
            completionRanges['0-25%']++
          }
        })

        // 计算观看时长分布
        const durationRanges = {
          '0-30分钟': 0,
          '30-60分钟': 0,
          '60-90分钟': 0,
          '90-120分钟': 0,
          '120分钟以上': 0
        }

        recordData.forEach(record => {
          const duration = parseInt(record.watchedDuration) || 0
          if (duration >= 120) {
            durationRanges['120分钟以上']++
          } else if (duration >= 90) {
            durationRanges['90-120分钟']++
          } else if (duration >= 60) {
            durationRanges['60-90分钟']++
          } else if (duration >= 30) {
            durationRanges['30-60分钟']++
          } else {
            durationRanges['0-30分钟']++
          }
        })

        // 更新完成率分布图
        if (this.completionChart) {
          const completionOption = {
            title: {
              text: '完成率分布',
              left: 'center',
              textStyle: {
                fontSize: 16,
                fontWeight: 'bold'
              }
            },
            tooltip: {
              trigger: 'item',
              formatter: '{a} <br/>{b}: {c} ({d}%)'
            },
            legend: {
              orient: 'vertical',
              left: 'left',
              top: 'middle'
            },
            series: [{
              name: '完成率',
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
              data: [
                { value: completionRanges['0-25%'], name: '0-25%', itemStyle: { color: '#ff6b6b' } },
                { value: completionRanges['25-50%'], name: '25-50%', itemStyle: { color: '#feca57' } },
                { value: completionRanges['50-75%'], name: '50-75%', itemStyle: { color: '#48dbfb' } },
                { value: completionRanges['75-100%'], name: '75-100%', itemStyle: { color: '#0abde3' } },
                { value: completionRanges['已完成'], name: '已完成', itemStyle: { color: '#10ac84' } }
              ]
            }]
          }
          this.completionChart.setOption(completionOption)
        }

        // 更新观看时长统计图
        if (this.durationChart) {
          const durationOption = {
            title: {
              text: '观看时长统计',
              left: 'center',
              textStyle: {
                fontSize: 16,
                fontWeight: 'bold'
              }
            },
            tooltip: {
              trigger: 'axis',
              axisPointer: {
                type: 'shadow'
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
              data: Object.keys(durationRanges),
              axisLabel: {
                rotate: 45
              }
            },
            yAxis: {
              type: 'value',
              name: '人数'
            },
            series: [{
              name: '观看人数',
              type: 'bar',
              data: Object.values(durationRanges),
              itemStyle: {
                color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                  { offset: 0, color: '#667eea' },
                  { offset: 1, color: '#764ba2' }
                ])
              },
              emphasis: {
                itemStyle: {
                  color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                    { offset: 0, color: '#764ba2' },
                    { offset: 1, color: '#667eea' }
                  ])
                }
              }
            }]
          }
          this.durationChart.setOption(durationOption)
        }

        // 更新观看趋势分析图
        if (this.trendChart) {
          // 使用真实数据生成趋势图
          // 按日期分组数据
          const dateGroups = {};
          const today = new Date();

          // 初始化最近6周的数据结构
          const weeks = [];
          const completionTrend = [];
          const durationTrend = [];

          // 生成最近6周的日期标签
          for (let i = 5; i >= 0; i--) {
            const weekStart = new Date(today);
            weekStart.setDate(today.getDate() - i * 7);
            const weekLabel = `第${6-i}周`;
            weeks.push(weekLabel);
            dateGroups[weekLabel] = {
              records: [],
              avgCompletion: 0,
              avgDuration: 0
            };
          }

          // 将记录分配到对应的周
          recordData.forEach(record => {
            if (!record.lastWatchTime) return;

            const recordDate = new Date(record.lastWatchTime);
            const daysDiff = Math.floor((today - recordDate) / (1000 * 60 * 60 * 24));
            const weekIndex = Math.floor(daysDiff / 7);

            if (weekIndex >= 0 && weekIndex < 6) {
              const weekLabel = `第${6-weekIndex}周`;
              dateGroups[weekLabel].records.push(record);
            }
          });

          // 计算每周的平均完成率和观看时长
          weeks.forEach(week => {
            const group = dateGroups[week];
            if (group.records.length > 0) {
              const totalCompletion = group.records.reduce((sum, record) =>
                sum + (parseInt(record.completionRate) || 0), 0);
              const totalDuration = group.records.reduce((sum, record) =>
                sum + (parseInt(record.watchedDuration) || 0), 0);

              group.avgCompletion = Math.round(totalCompletion / group.records.length);
              group.avgDuration = Math.round(totalDuration / group.records.length);
            }

            completionTrend.push(group.avgCompletion);
            durationTrend.push(group.avgDuration);
          });

          // 如果所有数据都是0，添加一些示例数据以便于展示
          if (completionTrend.every(val => val === 0) && durationTrend.every(val => val === 0)) {
            console.log('没有足够的历史数据，使用示例数据');
            for (let i = 0; i < 6; i++) {
              completionTrend[i] = 20 + i * 15;
              durationTrend[i] = 30 + i * 15;
            }
          }

          const trendOption = {
            title: {
              text: '观看趋势分析',
              left: 'center',
              textStyle: {
                fontSize: 16,
                fontWeight: 'bold'
              }
            },
            tooltip: {
              trigger: 'axis',
              axisPointer: {
                type: 'cross',
                label: {
                  backgroundColor: '#6a7985'
                }
              }
            },
            legend: {
              data: ['完成率', '观看时长'],
              top: '10%'
            },
            grid: {
              left: '3%',
              right: '4%',
              bottom: '3%',
              containLabel: true
            },
            xAxis: {
              type: 'category',
              boundaryGap: false,
              data: weeks
            },
            yAxis: [
              {
                type: 'value',
                name: '完成率(%)',
                min: 0,
                max: 100,
                position: 'left'
              },
              {
                type: 'value',
                name: '观看时长(分钟)',
                position: 'right'
              }
            ],
            series: [
              {
                name: '完成率',
                type: 'line',
                yAxisIndex: 0,
                data: completionTrend,
                smooth: true,
                lineStyle: {
                  color: '#667eea',
                  width: 3
                },
                itemStyle: {
                  color: '#667eea'
                },
                areaStyle: {
                  color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                    { offset: 0, color: 'rgba(102, 126, 234, 0.3)' },
                    { offset: 1, color: 'rgba(102, 126, 234, 0.1)' }
                  ])
                }
              },
              {
                name: '观看时长',
                type: 'line',
                yAxisIndex: 1,
                data: durationTrend,
                smooth: true,
                lineStyle: {
                  color: '#f093fb',
                  width: 3
                },
                itemStyle: {
                  color: '#f093fb'
                },
                areaStyle: {
                  color: new echarts.graphic.LinearGradient(0, 0, 0, 1, [
                    { offset: 0, color: 'rgba(240, 147, 251, 0.3)' },
                    { offset: 1, color: 'rgba(240, 147, 251, 0.1)' }
                  ])
                }
              }
            ]
          }
          this.trendChart.setOption(trendOption)
        }
      } catch (error) {
        console.error('图表更新失败:', error)
        this.$message.error('图表渲染失败，请重试')
      }
    },
    handleResize() {
      if (this.completionChart) {
        this.completionChart.resize()
      }
      if (this.durationChart) {
        this.durationChart.resize()
      }
      if (this.trendChart) {
        this.trendChart.resize()
      }
    },
    testCharts() {
      // 测试图表功能
      this.showAnalytics = true
      this.chartsLoading = true

      // 添加一些测试数据
      if (this.recordList.length === 0) {
        this.recordList = [
          { completionRate: 25, watchedDuration: 30 },
          { completionRate: 50, watchedDuration: 60 },
          { completionRate: 75, watchedDuration: 90 },
          { completionRate: 100, watchedDuration: 120 },
          { completionRate: 30, watchedDuration: 45 }
        ]
      }

      setTimeout(() => {
        this.$nextTick(() => {
          try {
            this.initCharts()
            this.updateCharts()
            this.chartsLoading = false
            this.$message.success('图表测试成功！')
          } catch (error) {
            console.error('图表测试失败:', error)
            this.chartsLoading = false
            this.$message.error('图表测试失败: ' + error.message)
          }
        })
      }, 300)
    },
    setRange(range) {
      this.queryParams.completionRange = range
    }
  }
}
</script>

<style scoped>
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

  .search-buttons {
    flex-direction: column;
    gap: 8px;
  }

  .progress-detail-info {
    flex-direction: column;
    gap: 10px;
  }
}

/* 整体布局优化 */
.app-container {
  padding: 24px;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  min-height: calc(100vh - 84px);
}

/* 卡片阴影和悬停效果增强 */
.stat-card,
.search-card,
.action-card,
.table-card {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
}

.stat-card:hover {
  transform: translateY(-8px);
  box-shadow: 0 12px 40px rgba(0, 0, 0, 0.15);
}

.search-card:hover,
.action-card:hover,
.table-card:hover {
  box-shadow: 0 4px 20px rgba(0, 0, 0, 0.12);
}

/* 表格样式优化 */
.el-table {
  border-radius: 8px;
  overflow: hidden;
}

.el-table th {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%) !important;
  color: white !important;
  font-weight: 600;
}

.el-table--striped .el-table__body tr.el-table__row--striped td {
  background: #fafbfc;
}

.el-table--striped .el-table__body tr.el-table__row--striped.current-row td {
  background: #e6f7ff;
}

/* 按钮样式优化 */
.el-button {
  border-radius: 6px;
  font-weight: 500;
  transition: all 0.3s ease;
}

.el-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}

/* 输入框样式优化 */
.el-input__inner,
.el-select .el-input__inner {
  border-radius: 6px;
  transition: all 0.3s ease;
}

.el-input__inner:focus,
.el-select .el-input__inner:focus {
  border-color: #667eea;
  box-shadow: 0 0 0 2px rgba(102, 126, 234, 0.2);
}

/* 进度条样式优化 */
.el-progress-bar__outer {
  border-radius: 10px;
  background: #f0f2f5;
}

.el-progress-bar__inner {
  border-radius: 10px;
  transition: all 0.3s ease;
}

/* 标签样式优化 */
.el-tag {
  border-radius: 4px;
  font-weight: 500;
}

/* 分页器样式优化 */
.el-pagination {
  text-align: center;
  margin-top: 20px;
}

.el-pagination .el-pager li {
  border-radius: 4px;
  transition: all 0.3s ease;
}

.el-pagination .el-pager li:hover {
  background: #667eea;
  color: white;
}

/* 对话框样式优化 */
.el-dialog {
  border-radius: 12px;
  overflow: hidden;
}

.el-dialog__header {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 20px 24px;
}

.el-dialog__title {
  color: white;
  font-weight: 600;
}

.el-dialog__headerbtn .el-dialog__close {
  color: white;
}

.el-dialog__body {
  padding: 24px;
}

/* 分割线样式优化 */
.el-divider__text {
  background: #f5f7fa;
  color: #303133;
  font-weight: 600;
  padding: 0 16px;
}

/* 时间线样式优化 */
.el-timeline-item__node {
  background: #667eea;
  border-color: #667eea;
}

.el-timeline-item__timestamp {
  color: #909399;
  font-size: 12px;
}

/* 滑块样式优化 */
.el-slider__runway {
  border-radius: 3px;
  height: 8px;
  background: #f0f2f5;
}

.el-slider__bar {
  border-radius: 3px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  height: 8px;
}

.el-slider__button {
  border: 3px solid #667eea;
  background: white;
  box-shadow: 0 2px 8px rgba(102, 126, 234, 0.3);
  width: 20px;
  height: 20px;
  transition: all 0.3s ease;
}

.el-slider__button:hover {
  transform: scale(1.2);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
}

.el-slider__button-wrapper {
  top: -6px;
}

/* 滑块工具提示样式 */
.el-slider__tooltip {
  background: #667eea;
  border-color: #667eea;
  color: white;
  font-weight: 500;
  padding: 4px 8px;
  border-radius: 4px;
}

.el-slider__tooltip::after {
  border-top-color: #667eea;
}

/* 统计卡片数字动画 */
.stat-number {
  animation: countUp 1s ease-out;
}

@keyframes countUp {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 表格行悬停效果 */
.el-table__body tr:hover > td {
  background: #f0f9ff !important;
}

/* 加载动画优化 */
.el-loading-mask {
  background: rgba(255, 255, 255, 0.9);
  backdrop-filter: blur(4px);
}

/* 空状态样式 */
.el-table__empty-block {
  padding: 40px 0;
}

.el-table__empty-text {
  color: #909399;
  font-size: 14px;
}

/* 基础样式 */
.mb20 { margin-bottom: 20px; }
.mb8 { margin-bottom: 8px; }

/* 统计卡片样式 */
.stat-card {
  border-radius: 12px;
  border: none;
  transition: all 0.3s ease;
}

.stat-content {
  display: flex;
  align-items: center;
  padding: 10px;
}

.stat-icon {
  width: 60px;
  height: 60px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 15px;
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
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 5px;
}

/* 完成率范围容器 */
.completion-range-container {
  position: relative;
  padding: 15px 0;
}

.range-labels {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  padding: 0 10px;
}

.range-label {
  font-size: 12px;
  color: #606266;
  font-weight: 500;
  background: #f5f7fa;
  padding: 2px 6px;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
}

.range-display {
  text-align: center;
  margin-top: 15px;
}

.range-display .el-tag {
  font-size: 13px;
  padding: 6px 12px;
  border-radius: 6px;
  font-weight: 500;
}

/* 搜索按钮容器 */
.search-buttons {
  display: flex;
  align-items: flex-end;
  height: 100%;
  padding-bottom: 18px;
  gap: 10px;
}

.search-buttons .el-button {
  flex: 1;
  height: 32px;
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

/* 资源信息样式 */
.resource-info {
  text-align: left;
  padding: 10px;
}

.resource-name {
  font-weight: bold;
  color: #303133;
  margin-bottom: 5px;
}

.resource-course {
  font-size: 12px;
  color: #67C23A;
  margin-bottom: 3px;
}

.resource-category {
  font-size: 12px;
  color: #409EFF;
  margin-bottom: 3px;
}

.resource-duration {
  font-size: 12px;
  color: #909399;
}

/* 进度信息样式 */
.progress-info {
  text-align: center;
  padding: 15px 10px;
}

.progress-bar-container {
  margin-bottom: 8px;
  padding: 0 5px;
}

.progress-text {
  font-size: 14px;
  font-weight: bold;
  color: #303133;
  margin: 5px 0;
}

.progress-status {
  margin-top: 8px;
}

/* 时长信息样式 */
.duration-info {
  text-align: center;
}

.watched-duration {
  font-weight: bold;
  color: #303133;
  margin-bottom: 2px;
}

.total-duration {
  font-size: 12px;
  color: #909399;
  margin-bottom: 2px;
}

.efficiency {
  font-size: 12px;
  color: #67C23A;
}

/* 行为信息样式 */
.behavior-info {
  text-align: center;
  font-size: 12px;
}

.skip-segments {
  color: #F56C6C;
  margin-bottom: 2px;
}

.repeat-segments {
  color: #E6A23C;
  margin-bottom: 2px;
}

.no-behavior {
  color: #67C23A;
}

/* 时间信息样式 */
.time-info {
  text-align: center;
}

.last-watch {
  font-weight: bold;
  color: #303133;
  margin-bottom: 2px;
}

.watch-frequency {
  font-size: 12px;
  color: #909399;
}

/* 详情对话框样式 */
.record-detail {
  padding: 20px;
}

.detail-card {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 15px;
  height: 100%;
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

.student-info .id,
.student-info .class {
  font-size: 12px;
  color: #909399;
  margin-bottom: 2px;
}

.resource-detail .resource-name {
  font-weight: bold;
  font-size: 14px;
  margin-bottom: 5px;
}

.resource-detail .resource-course {
  font-size: 12px;
  color: #67C23A;
  margin-bottom: 3px;
}

.resource-detail .resource-category {
  font-size: 12px;
  color: #409EFF;
  margin-bottom: 3px;
}

.resource-detail .resource-duration {
  font-size: 12px;
  color: #909399;
}

.watch-stats .completion {
  font-weight: bold;
  font-size: 14px;
  margin-bottom: 5px;
}

.watch-stats .duration,
.watch-stats .efficiency {
  font-size: 12px;
  color: #606266;
  margin-bottom: 2px;
}

.progress-detail {
  padding: 30px;
  background: #f8f9fa;
  border-radius: 12px;
  position: relative;
  overflow: hidden;
}

.progress-detail-container {
  position: relative;
  padding: 20px 0;
}

.progress-detail-info {
  display: flex;
  justify-content: space-around;
  margin-top: 20px;
  padding: 15px;
  background: rgba(255, 255, 255, 0.8);
  border-radius: 8px;
}

.progress-detail-item {
  text-align: center;
  flex: 1;
}

.progress-detail-item .label {
  display: block;
  font-size: 12px;
  color: #909399;
  margin-bottom: 5px;
}

.progress-detail-item .value {
  display: block;
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.behavior-detail {
  padding: 20px;
}

.behavior-card {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 15px;
  height: 100%;
}

.behavior-card h5 {
  margin: 0 0 10px 0;
  color: #303133;
  font-size: 14px;
}

.behavior-content {
  font-size: 12px;
  color: #606266;
  line-height: 1.4;
}

.timeline-detail {
  padding: 20px;
}

/* 分析图表样式 */
.analytics-content {
  padding: 20px;
}

.debug-info {
  margin-bottom: 20px;
}

.chart-container {
  background: #f8f9fa;
  border-radius: 8px;
  padding: 15px;
  margin-bottom: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.chart-container:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.chart-container h4 {
  margin: 0 0 15px 0;
  color: #303133;
  font-size: 16px;
  font-weight: 600;
  text-align: center;
}

/* 图表容器样式 */
.chart-container > div {
  width: 100% !important;
  height: 300px !important;
  min-height: 300px;
}

/* 确保图表能够正确渲染 */
.chart-container .echarts {
  width: 100% !important;
  height: 100% !important;
}

.quick-select {
  margin-top: 10px;
  text-align: center;
}

.quick-label {
  font-size: 12px;
  color: #909399;
  margin-right: 8px;
}

.quick-select .el-button {
  margin: 0 2px;
  color: #409EFF;
  font-size: 12px;
  padding: 2px 6px;
  border-radius: 3px;
  transition: all 0.3s ease;
}

.quick-select .el-button:hover {
  background: #ecf5ff;
  color: #409EFF;
  transform: translateY(-1px);
}
</style>
