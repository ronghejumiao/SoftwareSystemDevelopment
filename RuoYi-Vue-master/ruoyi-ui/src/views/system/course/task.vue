<template>
  <div class="task-container">
    <!-- 顶部筛选栏 -->
    <el-card class="task-header">
      <div slot="header" class="clearfix">
        <span>学习任务</span>
        <el-button
          v-hasRole="['admin','teacher']"
          style="float: right; margin-left: 8px;"
          type="danger"
          plain
          size="mini"
          icon="el-icon-delete"
          :disabled="selectedTaskIds.length === 0"
          @click="deleteSelectedTasks"
        >删除任务</el-button>
        <el-button
          v-hasRole="['admin','teacher']"
          style="float: right;"
          type="primary"
          plain
          size="mini"
          icon="el-icon-plus"
          @click="openAddDialog = true"
        >添加任务</el-button>
      </div>
      <el-form :inline="true" :model="filter" class="filter-form">
        <el-form-item label="任务类型">
          <el-select v-model="filter.taskType" placeholder="全部" clearable style="width: 140px;">
            <el-option label="全部" value="" />
            <el-option label="资料阅读" value="资料阅读" />
            <el-option label="视频观看" value="视频观看" />
            <el-option label="试卷完成" value="试卷完成" />
          </el-select>
        </el-form-item>
        <el-form-item label="截止时间">
          <el-select v-model="filter.dueOrder" placeholder="排序" clearable style="width: 140px;">
            <el-option label="正序" value="asc" />
            <el-option label="倒序" value="desc" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="mini" icon="el-icon-search" @click="applyFilter">筛选</el-button>
          <el-button size="mini" icon="el-icon-refresh" @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 任务列表 -->
    <el-card class="task-section">
      <div v-if="filteredCompletedTasks.length === 0 && filteredUncompletedTasks.length === 0" class="empty-state">
        <i class="el-icon-document"></i>
        <p>暂无学习任务</p>
      </div>
      <div v-else class="task-list">
        <!-- 已完成任务 -->
        <div v-for="task in filteredCompletedTasks" :key="task.taskId" class="task-item completed" :class="{selected: selectedTaskIds.includes(task.taskId)}" @click.self="toggleSelect(task.taskId)">
          <el-checkbox v-model="selectedTaskIds" :label="task.taskId" @click.stop />
          <div class="task-info">
            <div class="task-name">{{ task.taskName }}</div>
            <div class="task-details">
              <span class="detail-item"><i class="el-icon-s-flag"></i>类型：{{ task.taskType }}</span>
              <span class="detail-item"><i class="el-icon-date"></i>截止：{{ formatDate(task.dueDate) }}</span>
              <span class="detail-item"><i class="el-icon-s-operation"></i>提交方式：{{ task.submitMethod }}</span>
            </div>
            <div class="task-desc" v-if="task.taskDesc">{{ task.taskDesc }}</div>
          </div>
          <div class="task-status">已完成</div>
          <div class="task-actions">
            <el-button type="primary" size="small" @click.stop="viewDetail(task, true)">查看详情</el-button>
          </div>
        </div>
        <!-- 未完成任务 -->
        <div v-for="task in filteredUncompletedTasks" :key="task.taskId" class="task-item uncompleted" :class="{selected: selectedTaskIds.includes(task.taskId)}" @click.self="toggleSelect(task.taskId)">
          <el-checkbox v-model="selectedTaskIds" :label="task.taskId" @click.stop />
          <div class="task-info">
            <div class="task-name">{{ task.taskName }}</div>
            <div class="task-details">
              <span class="detail-item"><i class="el-icon-s-flag"></i>类型：{{ task.taskType }}</span>
              <span class="detail-item"><i class="el-icon-date"></i>截止：{{ formatDate(task.dueDate) }}</span>
              <span class="detail-item"><i class="el-icon-s-operation"></i>提交方式：{{ task.submitMethod }}</span>
            </div>
            <div class="task-desc" v-if="task.taskDesc">{{ task.taskDesc }}</div>
          </div>
          <div class="task-status uncompleted">未完成</div>
          <div class="task-actions">
            <el-button type="success" size="small" @click.stop="goToTask(task)">去完成</el-button>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 任务详情弹窗 -->
    <el-dialog title="任务详情" :visible.sync="detailDialogVisible" width="700px" append-to-body>
      <div v-if="currentTask" class="task-detail-container">
        <!-- 任务基本信息 -->
        <div class="task-detail-info-section">
          <div class="task-detail-header">
            <h3 class="task-detail-title">{{ currentTask.taskName }}</h3>
            <div class="task-type-badge">
              <el-tag :type="currentTask.taskType === '资料阅读' ? 'info' : 'primary'" size="medium">
                {{ currentTask.taskType }}
              </el-tag>
            </div>
          </div>
          <div class="task-detail-meta">
            <span class="meta-item">
              <i class="el-icon-time"></i>
              截止时间：{{ formatDate(currentTask.dueDate) }}
            </span>
            <span class="meta-item">
              <i class="el-icon-s-operation"></i>
              提交方式：{{ currentTask.submitMethod }}
            </span>
          </div>
        </div>

        <!-- 任务描述 -->
        <div v-if="currentTask.taskDesc" class="task-desc-section">
          <div class="content-card">
            <div class="card-header">
              <i class="el-icon-document"></i>
              <span>任务描述</span>
            </div>
            <div class="card-body">
              <div class="content-text">{{ currentTask.taskDesc }}</div>
            </div>
          </div>
        </div>

        <!-- 提交记录 -->
        <div class="task-submission-section">
          <div class="content-card">
            <div class="card-header">
              <i class="el-icon-upload"></i>
              <span>提交记录</span>
            </div>
            <div class="card-body">
              <div v-if="currentSubmission" class="submission-info">
                <div class="submission-meta">
                  <span class="meta-item">
                    <i class="el-icon-time"></i>
                    提交时间：{{ formatDate(currentSubmission.submissionTime) }}
                  </span>
                </div>
                
                <!-- 提交内容 -->
                <div v-if="currentSubmission.submissionContent" class="submission-content">
                  <div class="section-title">提交内容：</div>
                  <div class="content-text">{{ currentSubmission.submissionContent }}</div>
                </div>
                
                <!-- 提交文件 -->
                <div v-if="currentSubmission.submissionFile" class="submission-files">
                  <div class="section-title">提交文件：</div>
                  <div class="file-list">
                    <div class="file-item">
                      <i class="el-icon-document"></i>
                      <span class="file-name">{{ getFileName(currentSubmission.submissionFile) }}</span>
                      <el-button type="primary" size="mini" @click="downloadSubmissionFile(currentSubmission.submissionFile)">
                        <i class="el-icon-download"></i>
                        下载
                      </el-button>
                    </div>
                  </div>
                </div>
              </div>
              <div v-else class="no-submission">
                <i class="el-icon-warning"></i>
                <span>暂无提交记录</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailDialogVisible = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 添加任务弹窗 -->
    <el-dialog title="添加学习任务" :visible.sync="openAddDialog" width="500px" append-to-body @open="onOpenAddDialog">
      <el-form :model="addForm" :rules="addRules" ref="addForm" label-width="90px">
        <el-form-item label="任务名称" prop="taskName">
          <el-input v-model="addForm.taskName" placeholder="请输入任务名称" />
        </el-form-item>
        <el-form-item label="任务类型" prop="taskType">
          <el-select v-model="addForm.taskType" placeholder="请选择任务类型">
            <el-option label="资料阅读" value="资料阅读" />
            <el-option label="作业完成" value="作业完成" />
          </el-select>
        </el-form-item>
        <el-form-item label="截止时间" prop="dueDate">
          <el-date-picker v-model="addForm.dueDate" type="datetime" placeholder="请选择截止时间" style="width:100%" />
        </el-form-item>
        <el-form-item label="提交方式" prop="submitMethod">
          <el-select v-model="addForm.submitMethod" placeholder="请选择提交方式">
            <el-option label="资料阅读" value="资料阅读" />
            <el-option label="作业完成" value="作业完成" />
          </el-select>
        </el-form-item>
        <!-- 任务内容选择 -->
        <el-form-item v-if="addForm.submitMethod === '资料阅读'" label="任务内容" prop="resourceId">
          <el-select v-model="addForm.resourceId" placeholder="请选择资源" filterable>
            <el-option v-for="res in filteredResourceList" :key="res.resourceId" :label="res.resourceName + '（' + res.resourceType + '）'" :value="res.resourceId" />
          </el-select>
          <el-button type="text" @click="openResourceFilterDialog">更多筛选</el-button>
        </el-form-item>
        <el-form-item v-if="addForm.submitMethod === '作业完成'" label="任务内容" prop="homeworkId">
          <el-select v-model="addForm.homeworkId" placeholder="请选择作业" filterable>
            <el-option v-for="hw in homeworkList" :key="hw.homeworkId" :label="hw.homeworkName" :value="hw.homeworkId" />
          </el-select>
          <el-button type="text" @click="openHomeworkFilterDialog">更多筛选</el-button>
        </el-form-item>
        <el-form-item label="任务描述" prop="taskDesc">
          <el-input v-model="addForm.taskDesc" type="textarea" placeholder="请输入任务描述" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="openAddDialog = false">取 消</el-button>
        <el-button type="primary" @click="submitAddTask">确 定</el-button>
      </div>
    </el-dialog>

    <!-- 试卷筛选弹窗 -->
    <el-dialog title="筛选试卷" :visible.sync="paperFilterDialog" width="700px" append-to-body>
      <el-form :inline="true" :model="paperFilter" class="filter-form" style="margin-bottom:10px;">
        <el-form-item label="试卷名称">
          <el-input v-model="paperFilter.paperName" placeholder="输入试卷名称" clearable />
        </el-form-item>
        <el-form-item label="总分">
          <el-input v-model="paperFilter.totalScore" placeholder="输入总分" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="mini" icon="el-icon-search">搜索</el-button>
          <el-button size="mini" @click="paperFilter.paperName='';paperFilter.totalScore=''">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="filteredPaperTable" highlight-current-row @row-click="selectPaperRow" height="350" style="width:100%;">
        <el-table-column prop="paperId" label="试卷ID" width="80" align="center" />
        <el-table-column prop="paperName" label="试卷名称" min-width="120" />
        <el-table-column prop="paperDesc" label="试卷描述" min-width="180" />
        <el-table-column prop="totalScore" label="总分" width="80" align="center" />
        <el-table-column label="操作" width="100" align="center">
          <template slot-scope="scope">
            <el-button type="primary" size="mini" @click.stop="selectPaperRow(scope.row)">选择</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button @click="paperFilterDialog=false">关 闭</el-button>
      </div>
    </el-dialog>
    <!-- 资源筛选弹窗 -->
    <el-dialog title="筛选资源" :visible.sync="resourceFilterDialog" width="800px" append-to-body>
      <el-form :inline="true" :model="resourceFilter" class="filter-form" style="margin-bottom:10px;">
        <el-form-item label="资源名称">
          <el-input v-model="resourceFilter.resourceName" placeholder="输入资源名称" clearable />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="resourceFilter.resourceType" placeholder="全部" clearable style="width:120px;">
            <el-option label="全部" value="" />
            <el-option label="PPT" value="ppt" />
            <el-option label="PDF" value="pdf" />
            <el-option label="视频" value="视频" />
          </el-select>
        </el-form-item>
        <el-form-item label="上传者ID">
          <el-input v-model="resourceFilter.uploaderId" placeholder="输入上传者ID" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="mini" icon="el-icon-search" @click="applyResourceFilter">搜索</el-button>
          <el-button size="mini" @click="resetResourceFilter">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="filteredResourceTable" highlight-current-row @row-click="selectResourceRow" height="350" style="width:100%;">
        <el-table-column prop="resourceId" label="资源ID" width="80" align="center" />
        <el-table-column label="资源名称" align="center" prop="resourceName">
          <template slot-scope="scope">
            <el-link type="primary" @click="handleDownloadAndSubmit(scope.row)">
              {{ scope.row.resourceName }}
            </el-link>
          </template>
        </el-table-column>
        <el-table-column prop="resourceType" label="类型" width="80" align="center" />
        <el-table-column prop="uploaderId" label="上传者ID" width="100" align="center" />
        <el-table-column prop="uploadTime" label="上传时间" min-width="140" />
        <el-table-column label="操作" width="100" align="center">
          <template slot-scope="scope">
            <el-button type="primary" size="mini" @click.stop="selectResourceRow(scope.row)">选择</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button @click="resourceFilterDialog=false">关 闭</el-button>
      </div>
    </el-dialog>
    <!-- 作业筛选弹窗 -->
    <el-dialog title="筛选作业" :visible.sync="homeworkFilterDialog" width="700px" append-to-body>
      <el-form :inline="true" :model="homeworkFilter" class="filter-form" style="margin-bottom:10px;">
        <el-form-item label="作业名称">
          <el-input v-model="homeworkFilter.homeworkName" placeholder="输入作业名称" clearable />
        </el-form-item>
        <el-form-item label="截止时间">
          <el-date-picker v-model="homeworkFilter.dueDate" type="date" placeholder="选择截止时间" clearable />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" size="mini" icon="el-icon-search" @click="applyHomeworkFilter">搜索</el-button>
          <el-button size="mini" @click="resetHomeworkFilter">重置</el-button>
        </el-form-item>
      </el-form>
      <el-table :data="filteredHomeworkTable" highlight-current-row @row-click="selectHomeworkRow" height="350" style="width:100%;">
        <el-table-column prop="homeworkId" label="作业ID" width="80" align="center" />
        <el-table-column prop="homeworkName" label="作业名称" min-width="120" />
        <el-table-column prop="homeworkDesc" label="作业描述" min-width="180" />
        <el-table-column prop="dueDate" label="截止时间" width="120" align="center" />
        <el-table-column label="操作" width="100" align="center">
          <template slot-scope="scope">
            <el-button type="primary" size="mini" @click.stop="selectHomeworkRow(scope.row)">选择</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button @click="homeworkFilterDialog=false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listTask, addTask, delTask } from '@/api/system/task';
import { listSubmission } from '@/api/system/submission';
import { getLearningRecordByUserAndCourse, addLearningRecord } from '@/api/system/learningRecord';
import { listPaper, listPaperByCourseId } from '@/api/system/paper';
import { listResource } from '@/api/system/resource';
import { listHomework } from '@/api/system/homework';
import { getUserHomeworkStatus } from '@/api/system/homework';
import { mapState } from 'vuex';

export default {
  name: 'CourseTask',
  props: {
    courseId: {
      type: [String, Number],
      required: true
    }
  },
  data() {
    return {
      taskList: [],
      submissionList: [],
      recordId: null,
      filter: {
        taskType: '',
        dueOrder: ''
      },
      addForm: {
        taskName: '',
        taskType: '',
        dueDate: '',
        submitMethod: '',
        taskDesc: '',
        courseId: '',
        paperId: null,
        resourceId: null,
        homeworkId: null
      },
      addRules: {
        taskName: [{ required: true, message: '请输入任务名称', trigger: 'blur' }],
        taskType: [{ required: true, message: '请选择任务类型', trigger: 'change' }],
        dueDate: [{ required: true, message: '请选择截止时间', trigger: 'change' }],
        submitMethod: [{ required: true, message: '请选择提交方式', trigger: 'change' }]
      },
      openAddDialog: false,
      detailDialogVisible: false,
      currentTask: null,
      currentSubmission: null,
      selectedTaskIds: [],
      paperList: [],
      resourceList: [],
      paperFilterDialog: false,
      resourceFilterDialog: false,
      paperFilter: { paperName: '', totalScore: '' },
      resourceFilter: { resourceName: '', resourceType: '', uploaderId: '' },
      homeworkList: [],
      homeworkFilterDialog: false,
      homeworkFilter: { homeworkName: '', dueDate: '' },
      realCourseId: '',
      completedHomework: [],
      uncompletedHomework: [],
      uploadData: {
        courseId: '',
        userId: '',
        role: 'teacher'
      },
    };
  },
  computed: {
    ...mapState({
      userId: state => state.user.id
    }),
    // 已完成任务
    completedTaskIds() {
      return this.submissionList.map(s => s.taskId);
    },
    completedTasks() {
      return this.taskList.filter(t => this.completedTaskIds.includes(t.taskId));
    },
    uncompletedTasks() {
      return this.taskList.filter(t => !this.completedTaskIds.includes(t.taskId));
    },
    // 排序与筛选
    filteredCompletedTasks() {
      let arr = this.completedTasks;
      if (this.filter.taskType) arr = arr.filter(t => t.taskType === this.filter.taskType);
      arr = this.sortTasks(arr);
      return arr;
    },
    filteredUncompletedTasks() {
      let arr = this.uncompletedTasks;
      if (this.filter.taskType) arr = arr.filter(t => t.taskType === this.filter.taskType);
      arr = this.sortTasks(arr);
      return arr;
    },
    filteredPaperList() {
      return this.paperList;
    },
    filteredResourceList() {
      if (this.addForm.submitMethod === '资料阅读') {
        return this.resourceList.filter(r =>
          r.resourceType && (r.resourceType.toLowerCase() === 'ppt' || r.resourceType.toLowerCase() === 'pdf')
        );
      }
      if (this.addForm.submitMethod === '视频观看') {
        return this.resourceList.filter(r =>
          r.resourceType && r.resourceType.toLowerCase() === '视频'
        );
      }
      return this.resourceList;
    },
    filteredPaperTable() {
      let arr = this.paperList;
      if (this.paperFilter.paperName) arr = arr.filter(p => p.paperName && p.paperName.includes(this.paperFilter.paperName));
      if (this.paperFilter.totalScore) arr = arr.filter(p => String(p.totalScore) === String(this.paperFilter.totalScore));
      return arr;
    },
    filteredResourceTable() {
      // 根据提交方式筛选资源类型
      let arr = this.resourceList;
      if (this.addForm.submitMethod === '资料阅读') {
        // 资料阅读：展示PPT和PDF类型
        arr = arr.filter(r => r.resourceType && 
          (r.resourceType.toLowerCase() === 'ppt' || r.resourceType.toLowerCase() === 'pdf'));
      } else if (this.addForm.submitMethod === '视频观看') {
        // 视频观看：展示视频类型
        arr = arr.filter(r => r.resourceType && r.resourceType.toLowerCase() === '视频');
      }
      
      // 应用筛选条件
      if (this.resourceFilter.resourceName) {
        arr = arr.filter(r => r.resourceName && r.resourceName.includes(this.resourceFilter.resourceName));
      }
      if (this.resourceFilter.resourceType) {
        arr = arr.filter(r => r.resourceType && r.resourceType.toLowerCase() === this.resourceFilter.resourceType.toLowerCase());
      }
      if (this.resourceFilter.uploaderId) {
        arr = arr.filter(r => String(r.uploaderId) === String(this.resourceFilter.uploaderId));
      }
      return arr;
    },
    filteredHomeworkTable() {
      let arr = this.homeworkList;
      if (this.homeworkFilter && this.homeworkFilter.homeworkName) arr = arr.filter(hw => hw.homeworkName && hw.homeworkName.includes(this.homeworkFilter.homeworkName));
      if (this.homeworkFilter && this.homeworkFilter.dueDate) arr = arr.filter(hw => hw.dueDate && hw.dueDate.startsWith(this.homeworkFilter.dueDate));
      return arr;
    },
  },
  watch: {
    courseId: {
      handler(newVal) {
        if (newVal && newVal !== 'undefined') {
          this.realCourseId = newVal;
          this.getTaskList();
          this.getHomeworkList();
          this.getUserHomeworkStatus();
          this.updateUploadData();
        }
      },
      immediate: true
    },
    userId: {
      handler(val) {
        if (val) this.initData();
      },
      immediate: true
    }
  },
  created() {
    if ((!this.courseId || this.courseId === 'undefined') && this.$route && this.$route.params && this.$route.params.courseId) {
      this.courseId = this.$route.params.courseId;
    }
    this.initCourseId();
    this.initData();
  },
  methods: {
    initCourseId() {
      this.realCourseId = this.courseId || this.$route.params.courseId || this.$route.query.courseId || '';
    },
    async initData() {
      if (!this.realCourseId || !this.userId) return;
      // 获取学习记录
      let rec = await getLearningRecordByUserAndCourse(this.userId, this.realCourseId);
      if (!rec) {
        // 自动注册学习记录
        await addLearningRecord({
          userId: this.userId,
          courseId: this.realCourseId,
          joinTime: new Date(),
          courseProgress: 0
        });
        rec = await getLearningRecordByUserAndCourse(this.userId, this.realCourseId);
      }
      if (rec) {
        this.recordId = rec.recordId;
        await this.getTaskList();
        await this.getSubmissionList();
      }
    },
    async getTaskList() {
      const res = await listTask({ courseId: this.realCourseId, pageSize: 999 });
      this.taskList = res.rows || res.data || [];
    },
    async getSubmissionList() {
      if (!this.recordId) return;
      const res = await listSubmission({ recordId: this.recordId, pageSize: 999 });
      this.submissionList = res.rows || res.data || [];
    },
    sortTasks(arr) {
      // 先按类型顺序，再按截止时间
      const typeOrder = { '资料阅读': 1, '视频观看': 2, '试卷完成': 3 };
      arr = arr.slice().sort((a, b) => {
        if (typeOrder[a.submitMethod] !== typeOrder[b.submitMethod]) {
          return typeOrder[a.submitMethod] - typeOrder[b.submitMethod];
        }
        if (this.filter.dueOrder === 'asc') {
          return new Date(a.dueDate) - new Date(b.dueDate);
        } else if (this.filter.dueOrder === 'desc') {
          return new Date(b.dueDate) - new Date(a.dueDate);
        }
        return a.taskId - b.taskId;
      });
      return arr;
    },
    applyFilter() {
      // 触发computed刷新
      this.filter = { ...this.filter };
    },
    resetFilter() {
      this.filter = { taskType: '', dueOrder: '' };
    },
    formatDate(date) {
      if (!date) return '';
      const d = new Date(date);
      return d.toLocaleString('zh-CN');
    },
    viewDetail(task, completed) {
      this.currentTask = task;
      if (completed) {
        this.currentSubmission = this.submissionList.find(s => s.taskId === task.taskId);
      } else {
        this.currentSubmission = null;
      }
      this.detailDialogVisible = true;
    },
    goToTask(task) {
      // 只emit事件，不直接操作$router
      if (task.submitMethod === '资料阅读') {
        this.$emit('switch-tab', 'resources');
      } else if (task.submitMethod === '作业完成') {
        this.$emit('switch-tab', 'quiz');
      } else if (task.submitMethod === '视频观看') {
        this.$message.warning('视频观看功能暂未实现');
      }
    },
    async onOpenAddDialog() {
      // 拉取试卷和资源
      const paperRes = await listPaperByCourseId(this.realCourseId);
      this.paperList = paperRes.rows || paperRes.data || [];
      const resourceRes = await listResource({ courseId: this.realCourseId, pageNum: 1, pageSize: 999 });
      this.resourceList = resourceRes.rows || resourceRes.data || [];
      // 拉取本课程作业
      const homeworkRes = await listHomework({ courseId: this.realCourseId });
      this.homeworkList = homeworkRes.rows || homeworkRes.data || [];
    },
    openPaperFilterDialog() {
      this.paperFilterDialog = true;
    },
    openResourceFilterDialog() {
      this.resourceFilterDialog = true;
    },
    selectPaperRow(row) {
      this.addForm.paperId = row.paperId;
      this.paperFilterDialog = false;
    },
    selectResourceRow(row) {
      this.addForm.resourceId = row.resourceId;
      this.resourceFilterDialog = false;
    },
    async submitAddTask() {
      if (!this.realCourseId || this.realCourseId === 'undefined') {
        this.realCourseId = this.courseId || this.$route.params.courseId || this.$route.query.courseId || '';
      }
      this.$refs.addForm.validate(async valid => {
        if (!valid) return;
        this.addForm.courseId = this.realCourseId;
        // 清理冗余字段
        if (this.addForm.submitMethod === '试卷完成') {
          this.addForm.resourceId = null;
        } else if (this.addForm.submitMethod === '资料阅读' || this.addForm.submitMethod === '视频观看') {
          this.addForm.paperId = null;
        } else {
          this.addForm.paperId = null;
          this.addForm.resourceId = null;
        }
        await addTask(this.addForm);
        this.$message.success('添加成功');
        this.openAddDialog = false;
        this.addForm = { taskName: '', taskType: '', dueDate: '', submitMethod: '', taskDesc: '', courseId: '', paperId: null, resourceId: null, homeworkId: null };
        await this.getTaskList();
      });
    },
    toggleSelect(taskId) {
      const idx = this.selectedTaskIds.indexOf(taskId);
      if (idx === -1) this.selectedTaskIds.push(taskId);
      else this.selectedTaskIds.splice(idx, 1);
    },
    async deleteSelectedTasks() {
      if (this.selectedTaskIds.length === 0) return;
      this.$confirm('确定要删除选中的任务吗？', '提示', { type: 'warning' }).then(async () => {
        for (const id of this.selectedTaskIds) {
          await this.deleteTaskById(id);
        }
        this.selectedTaskIds = [];
        this.$message.success('删除成功');
        await this.getTaskList();
      }).catch(() => {});
    },
    async deleteTaskById(taskId) {
      await delTask(taskId);
    },
    fileUrl(path) {
      if (!path) return '';
      return process.env.VUE_APP_BASE_API + path;
    },
    async getHomeworkList() {
      try {
        const res = await listHomework({ courseId: this.realCourseId });
        this.homeworkList = res.rows || res.data || [];
      } catch (error) {
        console.error('获取作业列表失败:', error);
        this.homeworkList = [];
      }
    },
    async getUserHomeworkStatus() {
      if (!this.userId || !this.realCourseId) return;
      
      try {
        const res = await getUserHomeworkStatus(this.realCourseId, this.userId);
        const data = res.data || {};
        // 处理已完成和未完成的作业
        this.completedHomework = (data.completed || []).map(item => ({
          ...item.task,
          homework: item.homework,
          submission: item.submission
        }));
        this.uncompletedHomework = (data.uncompleted || []).map(item => ({
          ...item.task,
          homework: item.homework
        }));
      } catch (error) {
        console.error('获取用户作业状态失败:', error);
        this.completedHomework = [];
        this.uncompletedHomework = [];
      }
    },
    updateUploadData() {
      // 设置上传参数
      this.uploadData = {
        courseId: this.realCourseId,
        userId: this.userId,
        role: 'teacher'
      };
    },
    handleHomeworkUploadSuccess(response, file, fileList) {
      console.log('[DEBUG] 上传成功 response:', response, 'file:', file, 'fileList:', fileList);
      // 兼容后端返回msg为路径
      if (response && response.code === 200 && response.msg) {
        file.response = { msg: response.msg };
      }
      this.homeworkFileList = fileList.map(f => {
        if (f.status === 'success') {
          if (!f.response || !f.response.msg) {
            return { ...f, response: { msg: f.url || '' } };
          }
        }
        return f;
      });
      this.homeworkFileList = JSON.parse(JSON.stringify(this.homeworkFileList));
      console.log('[DEBUG] 上传成功后 homeworkFileList:', this.homeworkFileList);
      this.$modal.msgSuccess('文件上传成功');
    },
    submitAddHomework() {
      this.initCourseId();
      // ...校验省略
      this.$refs.submitHomeworkForm.validate(async valid => {
        if (!valid) return;
        this.submitHomeworkLoading = true;
        try {
          // 关键：获取taskId
          let taskId = this.currentSubmitHomework.taskId;
          if (!taskId && this.currentSubmitHomework.task) {
            taskId = this.currentSubmitHomework.task.taskId;
          }
          // 兜底：如果还有问题，尝试从uncompletedHomework中查找
          if (!taskId && this.currentSubmitHomework.homeworkId) {
            const match = this.uncompletedHomework.find(
              item => (item.homework && item.homework.homeworkId) === this.currentSubmitHomework.homeworkId
            );
            if (match && match.taskId) taskId = match.taskId;
            if (match && match.task && match.task.taskId) taskId = match.task.taskId;
          }
          if (!taskId) {
            this.$modal.msgError('未能确定任务ID，无法提交作业，请联系管理员！');
            this.submitHomeworkLoading = false;
            return;
          }
          const submitData = {
            taskId, // 必须带上
            homeworkId: this.currentSubmitHomework.homeworkId,
            courseId: this.realCourseId,
            userId: this.id,
            submissionContent: this.submitHomeworkForm.submissionContent,
            submissionFile: JSON.stringify(this.submitFileList.map(file => file.response?.data || file.url)),
            submissionTime: this.formatDateTime(new Date())
          };
          console.log('[DEBUG-submitHomework] submitData:', submitData);
          await submitHomework(submitData);
          this.$modal.msgSuccess('提交成功');
          this.submitHomeworkVisible = false;
          this.getUserHomeworkStatus();
        } catch (error) {
          console.error('提交作业失败:', error);
          this.$modal.msgError('提交失败');
        } finally {
          this.submitHomeworkLoading = false;
        }
      });
    },
    downloadFile(filePath) {
      if (!filePath) return;
      // 拼接完整后端地址
      let url = filePath;
      if (!/^https?:\/\//.test(filePath)) {
        // 自动补全协议和host
        url = window.location.origin + filePath;
      }
      console.log('[DEBUG] 下载 filePath:', url);
      window.open(url, '_blank');
    },
    openHomeworkFilterDialog() {
      this.homeworkFilterDialog = true;
    },
    applyResourceFilter() {
      // 触发computed刷新
      this.resourceFilter = { ...this.resourceFilter };
    },
    resetResourceFilter() {
      this.resourceFilter = { resourceName: '', resourceType: '', uploaderId: '' };
    },
    applyHomeworkFilter() {
      this.homeworkFilter = { ...this.homeworkFilter };
    },
    resetHomeworkFilter() {
      this.homeworkFilter = { homeworkName: '', dueDate: '' };
    },
    selectHomeworkRow(row) {
      this.addForm.homeworkId = row.homeworkId;
      this.homeworkFilterDialog = false;
    },
    async handleDownloadAndSubmit(resource) {
      let userId = this.$store.getters.id;
      if (!userId) {
        await this.$store.dispatch('user/getInfo');
        userId = this.$store.getters.id;
      }
      const courseId = resource.courseId;
      if (!userId || !courseId) {
        this.$modal.msgError('无法获取用户或课程信息，userId: ' + userId + ', courseId: ' + courseId);
        return;
      }
      // ...后续逻辑不变
    },
    // 获取文件名
    getFileName(filePath) {
      if (!filePath) return '未知文件';
      const parts = filePath.split('/');
      return parts[parts.length - 1] || '未知文件';
    },
    
    // 下载提交文件
    downloadSubmissionFile(file) {
      if (!file) return;
      
      // 构建下载URL
      let url = file;
      if (!/^https?:\/\//.test(file)) {
        // 如果不是完整URL，添加后端地址
        url = 'http://localhost:8080' + file;
      }
      
      console.log('[DEBUG] 下载文件:', url);
      
      // 创建临时链接进行下载
      const link = document.createElement('a');
      link.href = url;
      link.target = '_blank';
      link.download = this.getFileName(file);
      document.body.appendChild(link);
      link.click();
      document.body.removeChild(link);
    },
  }
};
</script>

<style scoped>
.task-container {
  padding: 20px;
}

/* 页面顶部筛选栏样式 */
.task-header {
  margin-bottom: 20px;
}

.task-section {
  margin-bottom: 20px;
}

.filter-form {
  margin-bottom: 10px;
}

/* 筛选表单样式优化 */
.filter-form .el-form-item {
  margin-bottom: 10px;
  margin-right: 15px;
}

.filter-form .el-select {
  width: 140px;
}

.filter-form .el-button {
  margin-right: 8px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .filter-form {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }
  
  .filter-form .el-form-item {
    margin-right: 0;
    margin-bottom: 10px;
  }
  
  .filter-form .el-select {
    width: 100%;
  }
  
  .task-details {
    flex-direction: column;
    gap: 8px;
  }
  
  .task-item {
    flex-direction: column;
    align-items: flex-start;
    gap: 12px;
  }
  
  .task-status,
  .task-actions {
    margin-left: 0;
    align-self: flex-end;
  }
  
  .task-actions {
    flex-direction: row;
    gap: 8px;
  }
}

@media (max-width: 480px) {
  .task-container {
    padding: 10px;
  }
  
  .task-header .el-button {
    margin-bottom: 5px;
  }
  
  .task-name {
    font-size: 14px;
  }
  
  .detail-item {
    font-size: 12px;
  }
}

/* 任务列表样式 */
.task-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.task-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border: 1px solid #EBEEF5;
  border-radius: 8px;
  transition: all 0.3s;
  background: #fff;
}

.task-item.completed {
  border-left: 4px solid #67C23A;
  background-color: #f0f9ff;
}

.task-item.uncompleted {
  border-left: 4px solid #E6A23C;
  background-color: #fdf6ec;
}

.task-item.selected {
  box-shadow: 0 0 0 2px #409EFF;
  background: #e6f7ff;
}

.task-item:hover {
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
  transform: translateY(-1px);
}

.task-info {
  flex: 1;
}

.task-name {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.task-details {
  display: flex;
  gap: 16px;
  margin-bottom: 8px;
  flex-wrap: wrap;
}

.detail-item {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #606266;
  background: rgba(255, 255, 255, 0.7);
  padding: 4px 8px;
  border-radius: 4px;
}

.detail-item i {
  margin-right: 4px;
  color: #409eff;
}

.task-desc {
  font-size: 14px;
  color: #909399;
  line-height: 1.4;
}

.task-status {
  margin-left: 16px;
  font-size: 14px;
  font-weight: bold;
  color: #67C23A;
  min-width: 60px;
  text-align: center;
  padding: 4px 8px;
  background: rgba(103, 194, 58, 0.1);
  border-radius: 4px;
}

.task-status.uncompleted {
  color: #E6A23C;
  background: rgba(230, 162, 60, 0.1);
}

.task-actions {
  margin-left: 16px;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.task-actions .el-button {
  border-radius: 6px;
  transition: all 0.3s ease;
}

.task-actions .el-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.15);
}

.empty-state {
  text-align: center;
  padding: 40px 0;
  color: #909399;
}

.empty-state i {
  font-size: 48px;
  margin-bottom: 16px;
  display: block;
}

.empty-state p {
  margin: 0;
  font-size: 14px;
}

.task-item .el-checkbox {
  margin-right: 12px;
}

/* 任务详情弹窗样式 */
.task-detail-container {
  padding: 20px;
}

.task-detail-info-section {
  margin-bottom: 20px;
}

.task-detail-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.task-detail-title {
  font-size: 20px;
  font-weight: bold;
  color: #303133;
  margin: 0;
}

.task-type-badge {
  align-self: flex-start;
}

.task-detail-meta {
  display: flex;
  gap: 20px;
  flex-wrap: wrap;
}

.task-detail-meta .meta-item {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #606266;
  background: #f5f7fa;
  padding: 6px 12px;
  border-radius: 4px;
}

.task-detail-meta .meta-item i {
  margin-right: 6px;
  color: #409eff;
}

.task-desc-section,
.task-submission-section {
  margin-bottom: 20px;
}

.content-card {
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.card-header {
  background: #f8f9fa;
  padding: 12px 15px;
  border-bottom: 1px solid #e4e7ed;
  display: flex;
  align-items: center;
  font-weight: 600;
  color: #303133;
}

.card-header i {
  margin-right: 8px;
  color: #409eff;
}

.card-body {
  padding: 15px;
}

.content-text {
  line-height: 1.6;
  color: #606266;
  white-space: pre-wrap;
  word-break: break-word;
}

.section-title {
  font-weight: 600;
  color: #303133;
  margin-bottom: 8px;
}

.submission-meta {
  margin-bottom: 15px;
}

.submission-content,
.submission-files {
  margin-bottom: 15px;
}

.file-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
}

.file-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 10px;
  background: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e4e7ed;
  transition: all 0.3s ease;
}

.file-item:hover {
  background: #e6f7ff;
  border-color: #91d5ff;
  box-shadow: 0 2px 8px rgba(24, 144, 255, 0.15);
}

.file-item i {
  margin-right: 8px;
  color: #409eff;
}

.file-name {
  flex: 1;
  margin-right: 10px;
  color: #606266;
  word-break: break-all;
}

.file-item .el-button {
  transition: all 0.3s ease;
}

.file-item .el-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.no-submission {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 40px 20px;
  color: #909399;
  font-size: 14px;
}

.no-submission i {
  margin-right: 8px;
  font-size: 20px;
}

/* 弹窗按钮样式 */
.dialog-footer {
  text-align: right;
  padding-top: 20px;
}

.dialog-footer .el-button {
  margin-left: 10px;
  border-radius: 6px;
  transition: all 0.3s ease;
}

.dialog-footer .el-button:hover {
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
}
</style> 