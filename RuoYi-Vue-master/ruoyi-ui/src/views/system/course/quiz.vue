<template>
  <div class="quiz-container">
    <!-- 菜单栏切换 -->
    <el-card class="menu-header">
      <div slot="header" class="clearfix">
        <el-tabs v-model="activeTab" @tab-click="handleTabClick">
          <el-tab-pane label="作业" name="homework">
            <span slot="label">
              <i class="el-icon-edit-outline"></i>
              作业
            </span>
          </el-tab-pane>
          <el-tab-pane label="试卷" name="quiz">
            <span slot="label">
              <i class="el-icon-document"></i>
              试卷
            </span>
          </el-tab-pane>
        </el-tabs>
      </div>
    </el-card>

    <!-- 作业功能 -->
    <div v-if="activeTab === 'homework'" class="homework-content">
      <!-- 作业管理区域 -->
    <el-card class="homework-section">
      <div slot="header" class="clearfix">
        <span class="section-title">作业管理</span>
        <el-button
          style="float: right;"
          type="primary"
          plain
          size="mini"
          icon="el-icon-plus"
          @click="openAddHomeworkDialog"
        >
            添加作业
        </el-button>
        <el-button
          style="float: right; margin-right: 8px;"
          type="danger"
          plain
          size="mini"
          icon="el-icon-delete"
          :disabled="selectedHomeworkIds.length === 0"
          @click="deleteSelectedHomework"
        >
            批量删除
        </el-button>
      </div>
        <el-table 
          :data="homeworkList" 
          border 
          style="width: 100%"
          @selection-change="handleHomeworkSelectionChange"
        >
          <el-table-column type="selection" width="55"></el-table-column>
          <el-table-column prop="homeworkName" label="作业名称" />
          <el-table-column prop="homeworkDesc" label="作业描述" show-overflow-tooltip />
        <el-table-column prop="dueDate" label="截止时间" />
          <el-table-column prop="createTime" label="创建时间" />
          <el-table-column label="附件" width="180">
            <template #default="scope">
              <div v-if="getFilePaths(scope.row.filePaths).length > 0">
                <el-link
                  v-for="(file, idx) in getFilePaths(scope.row.filePaths)"
                  :key="idx"
                  :underline="false"
                  type="primary"
                  @click="downloadFile(file)"
                  style="margin-right: 8px;"
                >
                  {{ getFileName(file) }}
                </el-link>
              </div>
              <span v-else style="color: #bbb">无</span>
            </template>
          </el-table-column>
          <el-table-column label="操作" width="200">
          <template #default="scope">
            <el-button type="primary" size="small" @click="viewHomeworkDetail(scope.row)">查看详情</el-button>
              <el-button type="danger" size="small" @click="deleteHomework(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

      <!-- 已完成作业区域 -->
      <el-card class="homework-section">
        <div slot="header" class="clearfix">
          <span class="section-title">已完成作业</span>
          <span class="section-count">({{ completedHomework.length }}份)</span>
        </div>
        
        <div v-if="completedHomework.length === 0" class="empty-state">
          <i class="el-icon-document-checked"></i>
          <p>暂无已完成的作业</p>
        </div>
        
        <div v-else class="homework-list">
          <div 
            v-for="homework in completedHomework" 
            :key="homework.homeworkId"
            class="homework-item completed"
          >
            <div class="homework-info">
              <div class="homework-name">{{ homework.homeworkName }}</div>
              <div class="homework-details">
                <span class="detail-item">
                  <i class="el-icon-time"></i>
                  截止时间：{{ formatDate(homework.dueDate) }}
                </span>
                <span class="detail-item">
                  <i class="el-icon-date"></i>
                  完成时间：{{ formatDate(homework.submitTime) }}
                </span>
                <span class="detail-item" v-if="homework.gradeScore">
                  <i class="el-icon-trophy"></i>
                  得分：{{ homework.gradeScore }}分
                </span>
              </div>
              <div class="homework-desc" v-if="homework.homeworkDesc">
                {{ homework.homeworkDesc }}
              </div>
            </div>
            <div class="homework-actions">
              <el-button 
                type="primary" 
                size="small" 
                @click="viewHomeworkDetail(homework)"
              >
                查看详情
              </el-button>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 未完成作业区域 -->
      <el-card class="homework-section">
        <div slot="header" class="clearfix">
          <span class="section-title">未完成作业</span>
          <span class="section-count">({{ uncompletedHomework.length }}份)</span>
        </div>
        
        <div v-if="uncompletedHomework.length === 0" class="empty-state">
          <i class="el-icon-document"></i>
          <p>暂无未完成的作业</p>
        </div>
        
        <div v-else class="homework-list">
          <div 
            v-for="item in uncompletedHomework" 
            :key="(item.homework && item.homework.homeworkId) || item.homeworkId"
            class="homework-item uncompleted"
          >
            <div class="homework-info">
              <div class="homework-name">{{ (item.homework && item.homework.homeworkName) || item.homeworkName }}</div>
              <div class="homework-details">
                <span class="detail-item">
                  <i class="el-icon-time"></i>
                  截止时间：{{ formatDate((item.homework && item.homework.dueDate) || item.dueDate) }}
                </span>
                <span class="detail-item">
                  <i class="el-icon-date"></i>
                  创建时间：{{ formatDate((item.homework && item.homework.createTime) || item.createTime) }}
                </span>
                <span class="detail-item">
                  <i class="el-icon-warning"></i>
                  状态：未完成
                </span>
              </div>
              <div class="homework-desc" v-if="(item.homework && item.homework.homeworkDesc) || item.homeworkDesc">
                {{ (item.homework && item.homework.homeworkDesc) || item.homeworkDesc }}
              </div>
              <!-- 附件下载 -->
              <div v-if="(item.homework && item.homework.filePaths && getFilePaths(item.homework.filePaths).length > 0) || (item.filePaths && getFilePaths(item.filePaths).length > 0)" class="homework-files">
                <span>附件：</span>
                <el-link
                  v-for="(file, idx) in getFilePaths((item.homework && item.homework.filePaths) || item.filePaths)"
                  :key="idx"
                  :underline="false"
                  type="primary"
                  @click="downloadFile(file)"
                >
                  {{ getFileName(file) }}
                </el-link>
              </div>
            </div>
            <div class="homework-actions">
              <el-button 
                type="success" 
                size="small" 
                @click="openSubmitHomeworkDialog(item)"
              >
                去完成
              </el-button>
            </div>
          </div>
        </div>
      </el-card>

      <!-- 添加作业弹窗 -->
      <el-dialog
        title="添加作业"
        :visible.sync="addHomeworkVisible"
        width="500px"
        append-to-body
        :close-on-click-modal="false"
      >
        <el-form ref="addHomeworkForm" :model="addHomeworkForm" :rules="addHomeworkRules" label-width="100px">
          <el-form-item label="作业名称" prop="homeworkName">
            <el-input v-model="addHomeworkForm.homeworkName" placeholder="请输入作业名称" />
          </el-form-item>
          <el-form-item label="作业描述" prop="homeworkDesc">
            <el-input 
              v-model="addHomeworkForm.homeworkDesc" 
              type="textarea" 
              :rows="4"
              placeholder="请输入作业描述" 
            />
          </el-form-item>
          <el-form-item label="截止时间" prop="dueDate">
            <el-date-picker
              v-model="addHomeworkForm.dueDate"
              type="datetime"
              placeholder="选择截止时间"
              format="yyyy-MM-dd HH:mm:ss"
              value-format="yyyy-MM-dd HH:mm:ss"
            />
          </el-form-item>
          <el-form-item label="作业附件">
            <el-upload
              ref="homeworkUpload"
              :action="uploadUrl"
              :headers="uploadHeaders"
              :file-list="homeworkFileList"
              :on-success="handleHomeworkUploadSuccess"
              :on-remove="handleHomeworkFileRemove"
              :before-upload="beforeHomeworkUpload"
              :data="uploadHomeworkData"
              :on-change="handleHomeworkFileChange"
              multiple
              :limit="5"
              accept=".pdf,.doc,.docx,.txt"
            >
              <el-button size="small" type="primary">点击上传</el-button>
              <div slot="tip" class="el-upload__tip">只能上传pdf/doc/docx/txt文件，且不超过10MB</div>
            </el-upload>
          </el-form-item>
        </el-form>
        <div slot="footer" class="dialog-footer">
          <el-button @click="addHomeworkVisible = false">取 消</el-button>
          <el-button type="primary" @click="submitAddHomework" :loading="addHomeworkLoading">确 定</el-button>
        </div>
      </el-dialog>
    </div>

    <!-- 试卷功能 -->
    <div v-if="activeTab === 'quiz'" class="quiz-content">
      <!-- 题库跳转按钮 -->
      <el-card class="quiz-header">
        <div slot="header" class="clearfix">
          <span>课程试卷</span>
          <el-button
            style="float: right;"
            type="primary"
            plain
            size="mini"
            icon="el-icon-s-management"
            @click="goToQuestionBank"
          >
            查看题库
          </el-button>
        </div>
      </el-card>

    <!-- 已作答试卷区域 -->
    <el-card class="quiz-section">
      <div slot="header" class="clearfix">
        <span class="section-title">已作答试卷</span>
        <span class="section-count">({{ completedPapers.length }}份)</span>
      </div>
      
      <div v-if="completedPapers.length === 0" class="empty-state">
        <i class="el-icon-document"></i>
        <p>暂无已作答的试卷</p>
      </div>
      
      <div v-else class="paper-list">
        <div 
          v-for="paper in completedPapers" 
          :key="paper.paperId"
          class="paper-item completed"
        >
          <div class="paper-info">
            <div class="paper-name">{{ paper.paperName }}</div>
            <div class="paper-details">
              <span class="detail-item">
                <i class="el-icon-time"></i>
                总分：{{ paper.totalScore }}分
              </span>
              <span class="detail-item">
                <i class="el-icon-trophy"></i>
                得分：{{ getScore(paper.paperId) }}分
              </span>
              <span class="detail-item">
                <i class="el-icon-date"></i>
                完成时间：{{ formatDate(getScoreSubmitTime(paper.paperId)) }}
              </span>
            </div>
            <div class="paper-desc" v-if="paper.paperDesc">
              {{ paper.paperDesc }}
            </div>
          </div>
          <div class="paper-actions">
            <el-button 
              type="primary" 
              size="small" 
              @click="viewPaperDetail(paper)"
            >
              查看详情
            </el-button>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 未作答试卷区域 -->
    <el-card class="quiz-section">
      <div slot="header" class="clearfix">
        <span class="section-title">未作答试卷</span>
        <span class="section-count">({{ uncompletedPapers.length }}份)</span>
      </div>
      
      <div v-if="uncompletedPapers.length === 0" class="empty-state">
        <i class="el-icon-document"></i>
        <p>暂无未作答的试卷</p>
      </div>
      
      <div v-else class="paper-list">
        <div 
          v-for="paper in uncompletedPapers" 
          :key="paper.paperId"
          class="paper-item uncompleted"
        >
          <div class="paper-info">
            <div class="paper-name">{{ paper.paperName }}</div>
            <div class="paper-details">
              <span class="detail-item">
                <i class="el-icon-time"></i>
                总分：{{ paper.totalScore }}分
              </span>
              <span class="detail-item">
                <i class="el-icon-date"></i>
                创建时间：{{ formatDate(paper.createTime) }}
              </span>
              <span class="detail-item">
                <i class="el-icon-warning"></i>
                状态：未作答
              </span>
            </div>
            <div class="paper-desc" v-if="paper.paperDesc">
              {{ paper.paperDesc }}
            </div>
          </div>
          <div class="paper-actions">
            <el-button 
              type="success" 
              size="small" 
              @click="startQuiz(paper)"
            >
              开始作答
            </el-button>
          </div>
        </div>
      </div>
    </el-card>
    </div>

    <!-- 作业详情弹窗 -->
    <el-dialog
      title="作业详情"
      :visible.sync="homeworkDetailVisible"
      width="70%"
      append-to-body
    >
      <div v-if="currentHomework" class="homework-detail-container">
        <!-- 作业信息区 -->
        <div class="homework-info">
          <h2>{{ currentHomework.homeworkName }}</h2>
          <p class="homework-desc">{{ currentHomework.homeworkDesc }}</p>
          <div class="homework-stats">
            <span>截止时间：{{ formatDate(currentHomework.dueDate) }}</span>
            <span>创建时间：{{ formatDate(currentHomework.createTime) }}</span>
            <span>完成时间：{{ formatDate(currentHomework.submitTime) }}</span>
          </div>
          <!-- 附件 -->
          <div v-if="getFilePaths(currentHomework.filePaths).length > 0" class="homework-files">
            <h3>作业附件</h3>
            <div class="file-list">
              <div 
                v-for="(file, index) in getFilePaths(currentHomework.filePaths)" 
                :key="index"
                class="file-item"
              >
                <i class="el-icon-document"></i>
                <span class="file-name">{{ getFileName(file) }}</span>
                <el-button type="text" size="small" @click="downloadFile(file)">下载</el-button>
              </div>
            </div>
          </div>
        </div>
        <!-- 分数与评分 -->
        <div class="grade-info" style="margin-bottom: 16px;">
          <span v-if="currentHomework.gradeScore !== undefined && currentHomework.gradeScore !== null">得分：{{ currentHomework.gradeScore }}分</span>
          <span v-else style="color: #999;">未评分</span>
        </div>
        <!-- 提交内容 -->
        <div v-if="currentHomework.submissionContent" class="submission-content">
          <h3>提交内容</h3>
          <div class="content-text">{{ currentHomework.submissionContent }}</div>
        </div>
        <!-- 提交文件 -->
        <div v-if="currentHomework.submissionFile && getFilePaths(currentHomework.submissionFile).length > 0" class="submission-files">
          <h3>提交文件</h3>
          <div class="file-list">
            <div 
              v-for="(file, index) in getFilePaths(currentHomework.submissionFile)" 
              :key="index"
              class="file-item"
            >
              <i class="el-icon-document"></i>
              <span class="file-name">{{ getFileName(file) }}</span>
              <el-button type="text" size="small" @click="downloadFile(file)">下载</el-button>
            </div>
          </div>
        </div>
        <!-- 评分信息 -->
        <div v-if="currentHomework.gradeComment" class="grade-info">
          <h3>评分信息</h3>
          <div class="grade-comment">{{ currentHomework.gradeComment }}</div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="homeworkDetailVisible = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 提交作业弹窗 -->
    <el-dialog
      title="提交作业"
      :visible.sync="submitHomeworkVisible"
      width="60%"
      append-to-body
      :close-on-click-modal="false"
    >
      <div v-if="currentSubmitHomework" class="submit-homework-container">
        <!-- 提交表单区 -->
        <el-form ref="submitHomeworkForm" :model="submitHomeworkForm" :rules="submitHomeworkRules" label-width="100px">
          <el-form-item label="提交内容" prop="submissionContent">
            <el-input 
              v-model="submitHomeworkForm.submissionContent" 
              type="textarea" 
              :rows="6"
              placeholder="请输入作业内容" 
            />
          </el-form-item>
          <el-form-item label="提交文件">
            <el-upload
              ref="submitUpload"
              :action="uploadUrl"
              :headers="uploadHeaders"
              :file-list="submitFileList"
              :on-success="handleSubmitUploadSuccess"
              :on-remove="handleSubmitFileRemove"
              :before-upload="beforeSubmitUpload"
              :data="uploadSubmitData"
              multiple
              :limit="3"
              accept=".pdf,.doc,.docx,.txt"
            >
              <el-button size="small" type="primary">点击上传</el-button>
              <div slot="tip" class="el-upload__tip">只能上传pdf/doc/docx/txt文件，且不超过10MB</div>
            </el-upload>
          </el-form-item>
        </el-form>
        <!-- 作业信息区（单独div，避免被背景遮挡） -->
        <div class="submit-homework-info" style="margin-top: 24px; padding: 16px; background: #f8f9fa; border-radius: 8px; border: 1px solid #e4e7ed;">
          <h3 style="margin-bottom: 8px;">作业信息</h3>
          <div><b>名称：</b>{{ currentSubmitHomework.homework ? currentSubmitHomework.homework.homeworkName : currentSubmitHomework.homeworkName }}</div>
          <div><b>描述：</b>{{ currentSubmitHomework.homework ? currentSubmitHomework.homework.homeworkDesc : currentSubmitHomework.homeworkDesc }}</div>
          <div><b>截止时间：</b>{{ formatDate(currentSubmitHomework.homework ? currentSubmitHomework.homework.dueDate : currentSubmitHomework.dueDate) }}</div>
          <div><b>创建时间：</b>{{ formatDate(currentSubmitHomework.homework ? currentSubmitHomework.homework.createTime : currentSubmitHomework.createTime) }}</div>
          <div v-if="getFilePaths((currentSubmitHomework.homework && currentSubmitHomework.homework.filePaths) || currentSubmitHomework.filePaths).length > 0" style="margin-top: 8px;">
            <b>附件：</b>
            <el-link
              v-for="(file, idx) in getFilePaths((currentSubmitHomework.homework && currentSubmitHomework.homework.filePaths) || currentSubmitHomework.filePaths)"
              :key="idx"
              :underline="false"
              type="primary"
              @click="downloadFile(file)"
              style="margin-right: 8px;"
            >
              {{ getFileName(file) }}
            </el-link>
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="submitHomeworkVisible = false">取 消</el-button>
        <el-button type="primary" @click="submitHomework" :loading="submitHomeworkLoading">提 交</el-button>
      </div>
    </el-dialog>

    <!-- 试卷详情弹窗 -->
    <el-dialog
      title="试卷详情"
      :visible.sync="paperDetailVisible"
      width="80%"
      append-to-body
      :close-on-click-modal="true"
      :close-on-press-escape="true"
    >
      <div class="paper-detail-container">
        <!-- 试卷基本信息 -->
        <div class="paper-info">
          <h2>{{ currentPaper.paperName }}</h2>
          <p class="paper-desc">{{ currentPaper.paperDesc }}</p>
          <div class="paper-stats">
            <span>总分：{{ currentPaper.totalScore }}分</span>
            <span>题目数：{{ questionList.length }}题</span>
            <span v-if="currentScore">得分：{{ currentScore.score }}分</span>
            <span v-if="currentScore">完成时间：{{ formatDate(currentScore.submitTime) }}</span>
          </div>
        </div>
        <!-- 题目列表 -->
        <div class="question-list">
          <div
            v-for="(question, index) in questionList"
            :key="index"
            class="question-item"
          >
            <div class="question-header">
              <span class="question-number">{{ index + 1 }}.</span>
              <span class="question-type">{{ question.questionType }}</span>
              <span class="question-score">{{ question.score }}分</span>
              <span class="question-difficulty">{{ question.difficultyLevel }}</span>
            </div>
            <div class="question-content">
              <div class="content-text">{{ question.questionContent }}</div>
              <!-- 选择题选项 -->
              <div v-if="question.options && question.options.length > 0" class="options-list">
                <div
                  v-for="(option, optionIndex) in question.options"
                  :key="optionIndex"
                  class="option-item"
                >
                  <span class="option-label">{{ String.fromCharCode(65 + optionIndex) }}.</span>
                  <span class="option-content">{{ option }}</span>
                </div>
              </div>
              <!-- 答案 -->
              <div class="answer-section">
                <div class="answer-label">答案：</div>
                <div class="answer-content">{{ question.answer }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="paperDetailVisible = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 答题弹窗 -->
    <el-dialog
      title="试卷作答"
      :visible.sync="quizVisible"
      width="80%"
      append-to-body
      :close-on-click-modal="false"
      :close-on-press-escape="false"
      :show-close="!isSubmitting"
      @close="handleQuizDialogClose"
    >
      <div class="quiz-container">
        <!-- 试卷基本信息 -->
        <div class="paper-info">
          <h2>{{ currentPaper.paperName }}</h2>
          <p class="paper-desc">{{ currentPaper.paperDesc }}</p>
          <div class="paper-stats">
            <span>总分：{{ currentPaper.totalScore }}分</span>
            <span>题目数：{{ questionList.length }}题</span>
            <span>剩余时间：{{ remainingTime }}</span>
          </div>
        </div>
        <!-- 答题区域 -->
        <div v-if="!isSubmitted" class="question-list">
          <div
            v-for="(question, index) in questionList"
            :key="index"
            class="question-item"
          >
            <div class="question-header">
              <span class="question-number">{{ index + 1 }}.</span>
              <span class="question-type">{{ question.questionType }}</span>
              <span class="question-score">{{ question.score }}分</span>
              <span class="question-difficulty">{{ question.difficultyLevel }}</span>
            </div>
            <div class="question-content">
              <div class="content-text">{{ question.questionContent }}</div>
              <!-- 选择题选项 -->
              <div v-if="question.options && question.options.length > 0" class="options-list">
                <div
                  v-for="(option, optionIndex) in question.options"
                  :key="optionIndex"
                  class="option-item"
                >
                  <span class="option-label">{{ String.fromCharCode(65 + optionIndex) }}.</span>
                  <span class="option-content">{{ option }}</span>
                </div>
              </div>
              <!-- 答案 -->
              <div class="answer-section">
                <div class="answer-label">答案：</div>
                <div class="answer-content">{{ question.answer }}</div>
              </div>
            </div>
          </div>
        </div>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="quizVisible = false">关 闭</el-button>
        <el-button type="primary" :loading="isSubmitting" @click="submitQuiz">提交</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listPaperByCourseId, listPaper } from "@/api/system/paper";
import { getScoreByUserAndCourse, addScore } from "@/api/system/score";
import { getLearningRecordByUserAndCourse } from "@/api/system/learningRecord";
import { addTask } from "@/api/system/task";
import { addLearningRecord } from "@/api/system/learningRecord";
import { addSubmission } from "@/api/system/submission";
import { 
  listHomework, 
  addHomework, 
  delHomework, 
  getUserHomeworkStatus, 
  submitHomework, 
  uploadHomeworkFile 
} from "@/api/system/homework";
import * as echarts from 'echarts';
import { getToken } from '@/utils/auth';

export default {
  name: "Quiz",
  props: {
    courseId: {
      type: [String, Number],
      required: false
    }
  },
  data() {
    return {
      loading: false,
      paperList: [],
      scoreList: [],
      completedPapers: [],
      uncompletedPapers: [],
      
      // 试卷详情相关
      paperDetailVisible: false,
      currentPaper: {},
      questionList: [],
      currentScore: null,
      
      // 答题相关
      quizVisible: false,
      userAnswers: [],
      isSubmitted: false,
      isSubmitting: false,
      finalScore: 0,
      remainingTime: "30:00",
      timer: null,
      resourceList: [],
      addForm: {
        taskName: '',
        taskType: '',
        dueDate: '',
        submitMethod: '',
        taskDesc: '',
        courseId: '',
        paperId: null,
        resourceId: null
      },
      openAddDialog: false,
      addRules: {
        taskName: [
          { required: true, message: '请输入任务名称', trigger: 'blur' }
        ],
        taskType: [
          { required: true, message: '请选择任务类型', trigger: 'change' }
        ],
        dueDate: [
          { required: true, message: '请选择截止日期', trigger: 'change' }
        ],
        submitMethod: [
          { required: true, message: '请选择提交方式', trigger: 'change' }
        ],
        taskDesc: [
          { required: true, message: '请输入任务描述', trigger: 'blur' }
        ],
        paperId: [
          { required: true, message: '请选择试卷', trigger: 'change', validator: (rule, value, callback) => {
            if (this.addForm.submitMethod === '试卷完成' && !value) callback(new Error('请选择试卷'));
            else callback();
          }}
        ],
        resourceId: [
          { required: true, message: '请选择资源', trigger: 'change', validator: (rule, value, callback) => {
            if ((this.addForm.submitMethod === '资料阅读' || this.addForm.submitMethod === '视频观看') && !value) callback(new Error('请选择资源'));
            else callback();
          }}
        ]
      },
      // 作业相关数据
      activeTab: 'homework',
      homeworkList: [],
      completedHomework: [],
      uncompletedHomework: [],
      selectedHomeworkIds: [],
      addHomeworkVisible: false,
      addHomeworkForm: {
        homeworkName: '',
        homeworkDesc: '',
        dueDate: '',
        filePaths: []
      },
      addHomeworkRules: {
        homeworkName: [
          { required: true, message: '请输入作业名称', trigger: 'blur' }
        ],
        homeworkDesc: [
          { required: true, message: '请输入作业描述', trigger: 'blur' }
        ],
        dueDate: [
          { required: true, message: '请选择截止时间', trigger: 'change' }
        ]
      },
      homeworkDetailVisible: false,
      currentHomework: null,
      submitHomeworkVisible: false,
      currentSubmitHomework: null,
      submitHomeworkForm: {
        submissionContent: '',
        submissionFile: ''
      },
      submitHomeworkRules: {
        submissionContent: [
          { required: true, message: '请输入作业内容', trigger: 'blur' }
        ]
      },
      uploadUrl: process.env.VUE_APP_BASE_API + '/system/homework/upload',
      uploadHeaders: {
        Authorization: 'Bearer ' + getToken()
      },
      homeworkFileList: [],
      submitFileList: [],
      addHomeworkLoading: false,
      submitHomeworkLoading: false,
      uploadHomeworkData: {},
      uploadSubmitData: {},
      realCourseId: '',
    };
  },
  computed: {
    id() {
      return this.$store.state.user.id;
    },
    isTeacher() {
      // 根据用户角色判断是否为教师
      const roles = this.$store.state.user.roles;
      return roles && roles.includes('teacher');
    },
    filteredResourceList() {
      if (this.addForm.submitMethod === '资料阅读') {
        return this.resourceList.filter(r => r.resourceType === 'PPT' || r.resourceType === 'PDF');
      }
      if (this.addForm.submitMethod === '视频观看') {
        return this.resourceList.filter(r => r.resourceType === '视频');
      }
      return this.resourceList;
    }
  },
  watch: {
    courseId: {
      handler(newVal) {
        if (newVal && newVal !== 'undefined') {
          this.realCourseId = newVal;
          this.getPaperList();
          this.getScoreList();
          this.getHomeworkList();
          this.getUserHomeworkStatus();
          this.updateUploadData();
        }
      },
      immediate: true
    }
  },
  created() {
    this.initCourseId();
    console.log('[DEBUG-created] realCourseId:', this.realCourseId, 'props.courseId:', this.courseId, 'route:', this.$route);
    if (this.realCourseId && this.realCourseId !== 'undefined') {
      this.getPaperList();
      this.getScoreList();
      this.getHomeworkList();
      this.getUserHomeworkStatus();
      this.updateUploadData();
    }
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer);
    }
  },
  methods: {
    initCourseId() {
      // 多重兜底，确保每次都能拿到courseId
      this.realCourseId = this.courseId || (this.$route && this.$route.params && this.$route.params.courseId) || (this.$route && this.$route.query && this.$route.query.courseId) || '';
    },
    // 获取试卷列表
    getPaperList() {
      this.loading = true;
      console.log('开始获取试卷列表，courseId:', this.realCourseId);
      
      // 根据courseId查询试卷
      listPaperByCourseId(this.realCourseId).then(response => {
        console.log('API响应:', response);
        this.paperList = response.data || response.rows || [];
        console.log('试卷列表:', this.paperList);
        this.classifyPapers();
        this.loading = false;
      }).catch(error => {
        console.error('API调用失败:', error);
        // 如果新API不存在，回退到原来的API
        listPaper({ courseId: this.realCourseId, pageSize: 999 }).then(response => {
          console.log('回退API响应:', response);
          this.paperList = response.rows || [];
          this.classifyPapers();
          this.loading = false;
        }).catch(error2 => {
          console.error('回退API也失败:', error2);
          this.loading = false;
        });
      });
    },
    
    // 获取学生成绩列表
    getScoreList() {
      // 从store中获取用户ID
      const userId = this.id;
      console.log('获取成绩列表，userId:', userId, 'courseId:', this.realCourseId);
      
      // 确保courseId和userId都存在才调用API
      if (userId && this.realCourseId) {
        getScoreByUserAndCourse(userId, this.realCourseId).then(response => {
          console.log('成绩API响应:', response);
          this.scoreList = response.data || [];
          this.classifyPapers();
        }).catch(error => {
          console.error('成绩API调用失败:', error);
          // 如果API不存在，使用空数组
          this.scoreList = [];
          this.classifyPapers();
        });
      } else {
        console.log('userId或courseId不存在，使用空数组');
        // 使用空数组，表示没有成绩记录
        this.scoreList = [];
        this.classifyPapers();
      }
    },
    
    // 分类试卷
    classifyPapers() {
      const completedPaperIds = this.scoreList.map(score => score.paperId);
      
      this.completedPapers = this.paperList.filter(paper => 
        completedPaperIds.includes(paper.paperId)
      );
      
      this.uncompletedPapers = this.paperList.filter(paper => 
        !completedPaperIds.includes(paper.paperId)
      );
    },
    
    // 获取试卷得分
    getScore(paperId) {
      const scoreRecord = this.scoreList.find(score => score.paperId === paperId);
      return scoreRecord ? scoreRecord.score : 0;
    },
    
    // 获取提交时间
    getScoreSubmitTime(paperId) {
      const scoreRecord = this.scoreList.find(score => score.paperId === paperId);
      return scoreRecord ? scoreRecord.submitTime : '';
    },
    
    // 格式化日期
    formatDate(dateString) {
      if (!dateString) return '';
      const date = new Date(dateString);
      return date.toLocaleDateString('zh-CN');
    },
    
    // 跳转到题库
    goToQuestionBank() {
      this.$router.push({
        path: '/system/question/index',
        query: { courseId: this.realCourseId }
      });
    },
    
    // 查看试卷详情
    viewPaperDetail(paper) {
      this.currentPaper = paper;
      this.currentScore = this.scoreList.find(score => score.paperId === paper.paperId);
      this.parseQuestionContent(paper);
      this.paperDetailVisible = true;
    },
    
    // 开始作答
    startQuiz(paper) {
      this.currentPaper = paper;
      this.parseQuestionContent(paper);
      this.userAnswers = new Array(this.questionList.length).fill('');
      this.isSubmitted = false;
      this.isSubmitting = false;
      this.finalScore = 0;
      this.remainingTime = "30:00";
      this.quizVisible = true;
      this.startTimer();
    },
    
    // 解析题目内容
    parseQuestionContent(paper) {
      try {
        // 解析JSON格式的题目内容
        if (paper.content && typeof paper.content === 'string') {
          this.questionList = JSON.parse(paper.content);
        } else if (Array.isArray(paper.content)) {
          this.questionList = paper.content;
        } else {
          this.questionList = [];
        }
        
        // 处理每个题目的选项数据
        this.questionList.forEach(question => {
          // 如果options是字符串，尝试解析为数组
          if (question.options && typeof question.options === 'string') {
            try {
              // 先尝试JSON.parse解析
              const parsed = JSON.parse(question.options);
              if (Array.isArray(parsed)) {
                question.options = parsed;
              } else {
                // 如果不是数组，按换行符分割
                question.options = question.options.split('\n').map(opt => opt.trim()).filter(opt => opt);
              }
            } catch (e) {
              // 如果JSON解析失败，按换行符分割
              question.options = question.options.split('\n').map(opt => opt.trim()).filter(opt => opt);
            }
          }
          // 确保options是数组
          if (!Array.isArray(question.options)) {
            question.options = [];
          }
        });
      } catch (error) {
        console.error('解析题目内容失败:', error);
        this.$modal.msgError('解析题目内容失败');
        this.questionList = [];
      }
    },
    
    // 开始计时器
    startTimer() {
      let timeLeft = 30 * 60; // 30分钟
      this.timer = setInterval(() => {
        timeLeft--;
        const minutes = Math.floor(timeLeft / 60);
        const seconds = timeLeft % 60;
        this.remainingTime = `${minutes.toString().padStart(2, '0')}:${seconds.toString().padStart(2, '0')}`;
        
        if (timeLeft <= 0) {
          clearInterval(this.timer);
          this.submitQuiz();
        }
      }, 1000);
    },
    
    // 取消答题
    cancelQuiz() {
      this.$modal.confirm('确定要取消答题吗？已作答的内容将不会保存。').then(() => {
        this.quizVisible = false;
      }).catch(() => {});
    },
    
    // 提交答案
    submitQuiz() {
      this.isSubmitting = true;
      
      // 计算得分
      this.finalScore = this.calculateScore();
      
      const userId = this.id;
      let currentRecordId = null; // 保存学习记录ID，供后续Promise链使用
      
      // 获取或创建学习记录
      getLearningRecordByUserAndCourse(userId, this.realCourseId).then(async learningRecord => {
        let record = learningRecord;
        if (!record) {
          await addLearningRecord({ userId, courseId: this.realCourseId, joinTime: new Date(), courseProgress: 0 });
          record = await getLearningRecordByUserAndCourse(userId, this.realCourseId);
        }
        if (!record) throw new Error('获取学习记录失败');

        currentRecordId = record.recordId;

        // 创建成绩记录
        const scoreData = {
          learningRecordId: currentRecordId,
          paperId: this.currentPaper.paperId,
          score: this.finalScore,
          scoreDesc: `得分：${this.finalScore}/${this.currentPaper.totalScore}`,
          submitTime: new Date()
        };
        return addScore(scoreData);
      }).then(() => {
        // 创建任务提交记录，便于任务提交管理界面展示
        const submissionPayload = {
          recordId: currentRecordId,
          taskId: this.currentPaper.paperId, // 使用试卷ID作为任务ID关联
          submissionContent: `完成试卷 ${this.currentPaper.paperName}`,
          submissionTime: this.formatDateTime(new Date()),
          isGraded: '1',
          score: this.finalScore,
          gradeComment: '系统自动评分',
          userId: userId,
          createTime: this.formatDateTime(new Date())
        };
        return addSubmission(submissionPayload);
      }).then(()=>{
        this.isSubmitted = true;
        this.isSubmitting = false;
        if (this.timer) {
          clearInterval(this.timer);
        }
        this.$modal.msgSuccess('答题完成！');
        // 通知任务提交记录界面刷新
        this.$root.$emit('submissionRecordUpdated');
      }).catch(error => {
        console.error('提交答案失败:', error);
        this.$modal.msgError('提交答案失败，请重试');
        this.isSubmitting = false;
      });
    },
    
    // 计算得分
    calculateScore() {
      let totalScore = 0;
      
      this.questionList.forEach((question, index) => {
        const userAnswer = this.userAnswers[index];
        if (userAnswer && this.isAnswerCorrect(question, userAnswer)) {
          totalScore += question.score || 0;
        }
      });
      
      return totalScore;
    },
    
    // 判断答案是否正确
    isAnswerCorrect(question, userAnswer) {
      const correctAnswer = question.answer;
      
      // 选择题
      if (question.options && question.options.length > 0) {
        return userAnswer === correctAnswer;
      }
      
      // 填空题
      return userAnswer && userAnswer.trim().toLowerCase() === correctAnswer.trim().toLowerCase();
    },
    
    // 获取答案样式类
    getAnswerClass(index) {
      const question = this.questionList[index];
      const userAnswer = this.userAnswers[index];
      
      if (!userAnswer) return 'unanswered';
      
      if (this.isAnswerCorrect(question, userAnswer)) {
        return 'correct';
      } else {
        return 'incorrect';
      }
    },
    
    // 关闭答题弹窗
    closeQuiz() {
      this.quizVisible = false;
      // 刷新试卷列表
      this.getPaperList();
      this.getScoreList();
    },

    handleQuizDialogClose() {
      if (this.timer) {
        clearInterval(this.timer);
        this.timer = null;
      }
    },

    // 弹窗打开时拉取试卷和资源
    async openAddTaskDialog() {
      this.openAddDialog = true;
      // 拉取试卷
      const paperRes = await this.$api.paper.listPaper({ courseId: this.realCourseId });
      this.paperList = paperRes.rows || [];
      // 拉取资源
      const resourceRes = await this.$api.resource.listResource({ courseId: this.realCourseId });
      this.resourceList = resourceRes.rows || [];
    },
    // 提交
    submitAddTask() {
      this.$refs.addForm.validate(async valid => {
        if (!valid) return;
        // 清理冗余字段
        if (this.addForm.submitMethod === '试卷完成') {
          this.addForm.resourceId = null;
        } else if (this.addForm.submitMethod === '资料阅读' || this.addForm.submitMethod === '视频观看') {
          this.addForm.paperId = null;
        } else {
          this.addForm.paperId = null;
          this.addForm.resourceId = null;
        }
        this.addForm.courseId = this.realCourseId;
        await addTask(this.addForm);
        this.openAddDialog = false;
        this.$message.success('添加成功');
        this.initData();
      });
    },
    // 工具函数：格式化日期为yyyy-MM-dd HH:mm:ss
    formatDateTime(date) {
      if (!date) return '';
      const d = new Date(date);
      const pad = n => n < 10 ? '0' + n : n;
      return d.getFullYear() + '-' + pad(d.getMonth() + 1) + '-' + pad(d.getDate()) + ' '
        + pad(d.getHours()) + ':' + pad(d.getMinutes()) + ':' + pad(d.getSeconds());
    },
    // 处理作业相关逻辑
    handleTabClick() {
      // 切换标签页时重新加载数据
      if (this.activeTab === 'homework') {
        this.getHomeworkList();
        this.getUserHomeworkStatus();
      } else if (this.activeTab === 'quiz') {
        this.getPaperList();
        this.getScoreList();
      }
    },
    handleHomeworkSelectionChange(selection) {
      this.selectedHomeworkIds = selection.map(item => item.homeworkId);
    },
    deleteHomework(homework) {
      this.$modal.confirm('确定要删除这个作业吗？').then(() => {
        delHomework(homework.homeworkId).then(() => {
          this.$modal.msgSuccess('删除成功');
          this.getHomeworkList();
        }).catch(error => {
          console.error('删除作业失败:', error);
          this.$modal.msgError('删除失败');
        });
      }).catch(() => {});
    },
    deleteSelectedHomework() {
      if (this.selectedHomeworkIds.length === 0) {
        this.$modal.msgWarning('请选择要删除的作业');
        return;
      }
      
      this.$modal.confirm(`确定要删除选中的 ${this.selectedHomeworkIds.length} 个作业吗？`).then(() => {
        const deletePromises = this.selectedHomeworkIds.map(id => delHomework(id));
        Promise.all(deletePromises).then(() => {
          this.$modal.msgSuccess('删除成功');
          this.getHomeworkList();
          this.selectedHomeworkIds = [];
        }).catch(error => {
          console.error('批量删除作业失败:', error);
          this.$modal.msgError('删除失败');
        });
      }).catch(() => {});
    },
    openAddHomeworkDialog() {
      this.addHomeworkVisible = true;
      this.addHomeworkForm = {
        homeworkName: '',
        homeworkDesc: '',
        dueDate: '',
        filePaths: []
      };
      this.homeworkFileList = [];
    },
    submitAddHomework() {
      this.$refs.addHomeworkForm.validate(async valid => {
        if (!valid) return;
        console.log('[DEBUG] submitAddHomework homeworkFileList:', this.homeworkFileList);
        if (this.homeworkFileList.some(f => f.status && f.status !== 'success')) {
          this.$modal.msgError('请等待所有文件上传完成后再提交！');
          return;
        }
        // 兼容后端返回msg为路径
        const filePathsArr = this.homeworkFileList
          .filter(f => f.status === 'success' && f.response && (f.response.data || f.response.msg))
          .map(f => f.response.data || f.response.msg)
          .filter(path => !!path && path !== 'null');
        console.log('[DEBUG] filePathsArr:', filePathsArr);
        this.addHomeworkLoading = true;
        try {
          const homeworkData = {
            ...this.addHomeworkForm,
            courseId: this.realCourseId,
            filePaths: JSON.stringify(filePathsArr)
          };
          console.log('[DEBUG] 发布作业 homeworkData:', homeworkData);
          await addHomework(homeworkData);
          this.$modal.msgSuccess('发布成功');
          this.addHomeworkVisible = false;
          this.homeworkFileList = [];
          this.getHomeworkList();
        } catch (error) {
          console.error('发布作业失败:', error);
          this.$modal.msgError('发布失败');
        } finally {
          this.addHomeworkLoading = false;
        }
      });
    },
    handleHomeworkUploadSuccess(response, file, fileList) {
      console.log('[DEBUG] 上传成功 response:', response, 'file:', file, 'fileList:', fileList);
      // 兼容后端返回msg为路径
      if (response && response.code === 200 && (response.data || response.msg)) {
        file.response = response.data ? { data: response.data } : { msg: response.msg };
      }
      this.homeworkFileList = fileList.map(f => {
        if (f.status === 'success' && (!f.response || (!f.response.data && !f.response.msg))) {
          return { ...f, response: { msg: f.url || '' } };
        }
        return f;
      });
      this.homeworkFileList = JSON.parse(JSON.stringify(this.homeworkFileList));
      console.log('[DEBUG] 上传成功后 homeworkFileList:', this.homeworkFileList);
      this.$modal.msgSuccess('文件上传成功');
    },
    handleHomeworkFileRemove(file, fileList) {
      console.log('[DEBUG] 文件移除 file:', file, 'fileList:', fileList);
      this.homeworkFileList = fileList;
    },
    beforeHomeworkUpload(file) {
      console.log('[DEBUG] beforeUpload file:', file);
      const isValidType = /\.(pdf|doc|docx|txt)$/i.test(file.name);
      const isLt10M = file.size / 1024 / 1024 < 10;
      if (!isValidType) {
        this.$modal.msgError('只能上传PDF/DOC/DOCX/TXT格式的文件!');
        return false;
      }
      if (!isLt10M) {
        this.$modal.msgError('文件大小不能超过10MB!');
        return false;
      }
      return true;
    },
    handleHomeworkFileChange(file, fileList) {
      console.log('[DEBUG] on-change file:', file, 'fileList:', fileList);
    },
    viewHomeworkDetail(homework) {
      // 如果有submission字段，合并到currentHomework，便于详情弹窗展示
      if (homework.submission) {
        this.currentHomework = {
          ...homework,
          submissionContent: homework.submission.submissionContent,
          submissionFile: homework.submission.submissionFile,
          gradeScore: homework.submission.gradeScore,
          gradeComment: homework.submission.gradeComment,
          submitTime: homework.submission.submissionTime
        };
      } else {
        this.currentHomework = homework;
      }
      this.homeworkDetailVisible = true;
    },
    openSubmitHomeworkDialog(homework) {
      this.currentSubmitHomework = homework;
      this.submitHomeworkForm = {
        submissionContent: '',
        submissionFile: ''
      };
      this.submitFileList = [];
      // 修正：初始化上传参数
      this.uploadSubmitData = {
        courseId: this.realCourseId,
        userId: this.userId || this.id,
        role: 'student'
      };
      this.submitHomeworkVisible = true;
    },
    handleSubmitUploadSuccess(response, file, fileList) {
      // 兼容后端返回msg为路径
      if (response && response.code === 200 && (response.data || response.msg)) {
        file.response = response.data ? { data: response.data } : { msg: response.msg };
      }
      this.submitFileList = fileList.map(f => {
        if (f.status === 'success' && (!f.response || (!f.response.data && !f.response.msg))) {
          return { ...f, response: { msg: f.url || '' } };
        }
        return f;
      });
      this.submitFileList = JSON.parse(JSON.stringify(this.submitFileList));
      this.$modal.msgSuccess('文件上传成功');
    },
    handleSubmitFileRemove(file, fileList) {
      this.submitFileList = fileList;
    },
    beforeSubmitUpload(file) {
      const isValidType = /\.(pdf|doc|docx|txt)$/i.test(file.name);
      const isLt10M = file.size / 1024 / 1024 < 10;
      
      if (!isValidType) {
        this.$modal.msgError('只能上传PDF/DOC/DOCX/TXT格式的文件!');
        return false;
      }
      if (!isLt10M) {
        this.$modal.msgError('文件大小不能超过10MB!');
        return false;
      }
      return true;
    },
    getFileName(filePath) {
      if (!filePath) return '';
      return filePath.split('/').pop() || filePath;
    },
    downloadFile(filePath) {
      if (!filePath) return;
      // 强制拼接后端域名
      const backend = 'http://localhost:8080';
      let url = filePath;
      if (!/^https?:\/\//.test(filePath)) {
        url = backend + filePath;
      }
      console.log('[DEBUG] 下载 filePath:', url);
      window.open(url, '_blank');
    },
    getHomeworkList() {
      listHomework({ courseId: this.realCourseId }).then(response => {
        this.homeworkList = response.data || response.rows || [];
      }).catch(error => {
        console.error('获取作业列表失败:', error);
        this.homeworkList = [];
      });
    },
    getUserHomeworkStatus() {
      const userId = this.id;
      if (!userId || !this.realCourseId) return;
      getUserHomeworkStatus(this.realCourseId, userId).then(response => {
        const data = response.data || {};
        // 新结构：completed/uncompleted均为[{homework, task, submission}]，前端需适配
        this.completedHomework = (data.completed || []).map(item => {
          return {
            ...item.task,
            homework: item.homework,
            submission: item.submission
          };
        });
        this.uncompletedHomework = (data.uncompleted || []).map(item => {
          // 保证taskId等任务字段在顶层
          return {
            ...item.task,
            homework: item.homework
          };
        });
      }).catch(error => {
        console.error('获取作业状态失败:', error);
      });
    },
    initData() {
      this.getPaperList();
      this.getScoreList();
      this.getHomeworkList();
      this.getUserHomeworkStatus();
    },
    getFilePaths(filePaths) {
      if (!filePaths) return [];
      
      if (typeof filePaths === 'string') {
        try {
          // 尝试解析JSON字符串
          const parsed = JSON.parse(filePaths);
          return Array.isArray(parsed) ? parsed : [filePaths];
        } catch (e) {
          // 如果不是JSON，按逗号分割
          return filePaths.split(',').filter(path => path.trim());
        }
      } else if (Array.isArray(filePaths)) {
        return filePaths;
      } else {
        return [];
      }
    },
    updateUploadData() {
      // 默认教师上传
      this.uploadHomeworkData = {
        courseId: this.realCourseId,
        userId: this.id,
        role: 'teacher'
      };
      this.uploadSubmitData = {
        courseId: this.realCourseId,
        userId: this.id,
        role: 'student'
      };
    },
    submitHomework() {
      this.initCourseId();
      // 调试输出
      console.log('[DEBUG-submitHomework] realCourseId:', this.realCourseId, 'props.courseId:', this.courseId, 'route:', this.$route);
      console.log('[DEBUG-submitHomework] uncompletedHomework:', this.uncompletedHomework);
      console.log('[DEBUG-submitHomework] currentSubmitHomework:', this.currentSubmitHomework, JSON.stringify(this.currentSubmitHomework));
      // 获取taskId
      const taskId = this.currentSubmitHomework.taskId || (this.currentSubmitHomework.task && this.currentSubmitHomework.task.taskId);
      if (!taskId) {
        this.$modal.msgError('未能确定任务ID，无法提交！');
        this.submitHomeworkLoading = false;
        return;
      }
      // 获取homeworkId
      const homeworkId = (this.currentSubmitHomework.homework && this.currentSubmitHomework.homework.homeworkId) || this.currentSubmitHomework.homeworkId;
      // 修正：上传文件路径取自submitFileList，兼容data/msg/url
      const submissionFile = JSON.stringify(this.submitFileList.map(file => file.response?.data || file.response?.msg || file.url));
      const submitData = {
        taskId: taskId,
        homeworkId: homeworkId,
        courseId: this.realCourseId,
        userId: this.userId || this.id,
        submissionContent: this.submitHomeworkForm.submissionContent,
        submissionFile: submissionFile,
        // ... 其他需要的字段
      };
      console.log('[DEBUG-submitHomework] submitData:', submitData);
      this.submitHomeworkLoading = true;
      try {
        Promise.resolve(submitHomework(submitData)).then(res => {
          console.log('[DEBUG-submitHomework] 提交返回:', res);
          if (res && res.code === 200) {
            this.$modal.msgSuccess('提交成功');
            this.submitHomeworkVisible = false;
            this.getUserHomeworkStatus();
          } else {
            this.$modal.msgError('提交失败: ' + (res && res.msg ? res.msg : '未知错误'));
          }
        }).catch(error => {
          console.error('提交作业失败:', error);
          this.$modal.msgError('提交失败: ' + (error && error.message ? error.message : error));
        }).finally(() => {
          this.submitHomeworkLoading = false;
        });
      } catch (error) {
        console.error('提交作业异常:', error);
        this.$modal.msgError('提交异常: ' + (error && error.message ? error.message : error));
        this.submitHomeworkLoading = false;
      }
    },
  }
};
</script>

<style scoped>
.quiz-container {
  padding: 20px;
}

.quiz-header {
  margin-bottom: 20px;
}

.quiz-section {
  margin-bottom: 20px;
}

.section-title {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
}

.section-count {
  font-size: 14px;
  color: #909399;
  margin-left: 8px;
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

.paper-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.paper-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border: 1px solid #EBEEF5;
  border-radius: 8px;
  transition: all 0.3s;
}

.paper-item:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.paper-item.completed {
  border-left: 4px solid #67C23A;
  background-color: #f0f9ff;
}

.paper-item.uncompleted {
  border-left: 4px solid #E6A23C;
  background-color: #fdf6ec;
}

.paper-item .paper-info {
  flex: 1;
}

.paper-name {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.paper-details {
  display: flex;
  gap: 16px;
  margin-bottom: 8px;
}

.detail-item {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #606266;
}

.detail-item i {
  margin-right: 4px;
}

.paper-desc {
  font-size: 14px;
  color: #909399;
  line-height: 1.4;
}

.paper-actions {
  margin-left: 16px;
}

/* 试卷详情弹窗样式 */
.paper-detail-container {
  padding: 20px 0;
}

.paper-detail-container .paper-info,
.el-dialog .quiz-container .paper-info {
  text-align: center;
  margin-bottom: 30px;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px;
}

.paper-detail-container .paper-info h2,
.el-dialog .quiz-container .paper-info h2 {
  margin: 0 0 10px 0;
  font-size: 24px;
  font-weight: bold;
}

.paper-detail-container .paper-info .paper-desc,
.el-dialog .quiz-container .paper-info .paper-desc {
  margin: 10px 0;
  font-size: 14px;
  opacity: 0.9;
}

.paper-stats {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin-top: 15px;
  flex-wrap: wrap;
}

.paper-stats span {
  background: rgba(255, 255, 255, 0.2);
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
}

.question-list {
  max-height: 600px;
  overflow-y: auto;
}

.question-item {
  margin-bottom: 30px;
  padding: 20px;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  background: #fff;
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
  transition: all 0.3s ease;
}

.question-item:hover {
  box-shadow: 0 4px 20px 0 rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
}

.question-header {
  display: flex;
  align-items: center;
  margin-bottom: 15px;
  padding-bottom: 10px;
  border-bottom: 2px solid #f0f0f0;
}

.question-number {
  font-size: 18px;
  font-weight: bold;
  color: #409eff;
  margin-right: 15px;
  min-width: 30px;
}

.question-type {
  background: #409eff;
  color: white;
  padding: 4px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 500;
  margin-right: 10px;
}

.question-score {
  background: #67c23a;
  color: white;
  padding: 4px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 500;
  margin-right: 10px;
}

.question-difficulty {
  background: #e6a23c;
  color: white;
  padding: 4px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 500;
}

.question-content {
  padding-left: 45px;
}

.content-text {
  font-size: 16px;
  line-height: 1.8;
  color: #303133;
  margin-bottom: 15px;
  text-align: justify;
}

.options-list {
  margin: 15px 0;
  padding: 15px;
  background: #f8f9fa;
  border-radius: 6px;
  border-left: 4px solid #409eff;
}

.option-item {
  display: flex;
  align-items: flex-start;
  margin-bottom: 10px;
  padding: 8px 0;
}

.option-item:last-child {
  margin-bottom: 0;
}

.option-label {
  font-weight: bold;
  color: #409eff;
  margin-right: 10px;
  min-width: 20px;
}

.option-content {
  flex: 1;
  line-height: 1.6;
  color: #606266;
}

.option-radio {
  width: 100%;
  margin-bottom: 8px;
}

.option-radio .el-radio__label {
  display: flex;
  align-items: flex-start;
  width: 100%;
}

.fill-blank-section {
  margin: 15px 0;
}

.answer-input {
  width: 100%;
}

.answer-section {
  margin-top: 20px;
  padding: 15px;
  background: #f0f9ff;
  border-radius: 6px;
  border-left: 4px solid #67c23a;
}

.answer-label {
  font-weight: bold;
  color: #67c23a;
  margin-bottom: 8px;
}

.answer-content {
  color: #303133;
  line-height: 1.6;
  font-size: 14px;
}

/* 答题结果样式 */
.result-container {
  padding: 20px 0;
}

.result-header {
  text-align: center;
  margin-bottom: 30px;
  padding: 20px;
  background: linear-gradient(135deg, #67c23a 0%, #85ce61 100%);
  color: white;
  border-radius: 8px;
}

.result-header h3 {
  margin: 0 0 15px 0;
  font-size: 24px;
  font-weight: bold;
}

.score-display {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 10px;
}

.score-label {
  font-size: 16px;
}

.score-value {
  font-size: 32px;
  font-weight: bold;
}

.score-total {
  font-size: 16px;
  opacity: 0.8;
}

.answer-comparison {
  margin-top: 20px;
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 20px;
}

.user-answer, .correct-answer {
  padding: 15px;
  border-radius: 6px;
}

.user-answer {
  background: #f8f9fa;
  border-left: 4px solid #409eff;
}

.correct-answer {
  background: #f0f9ff;
  border-left: 4px solid #67c23a;
}

.answer-content.correct {
  color: #67c23a;
  font-weight: bold;
}

.answer-content.incorrect {
  color: #f56c6c;
  font-weight: bold;
}

.answer-content.unanswered {
  color: #909399;
  font-style: italic;
}

/* 滚动条样式 */
.question-list::-webkit-scrollbar {
  width: 6px;
}

.question-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.question-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.question-list::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}

.clearfix:after {
  clear: both;
}

/* 菜单栏样式 */
.menu-header {
  margin-bottom: 20px;
}

.menu-header .el-tabs__header {
  margin-bottom: 0;
}

.menu-header .el-tabs__item {
  font-size: 16px;
  font-weight: 500;
}

.menu-header .el-tabs__item i {
  margin-right: 8px;
}

/* 作业内容样式 */
.homework-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.homework-section {
  margin-bottom: 20px;
}

/* 作业列表样式 */
.homework-list {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.homework-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px;
  border: 1px solid #EBEEF5;
  border-radius: 8px;
  transition: all 0.3s;
}

.homework-item:hover {
  box-shadow: 0 2px 12px 0 rgba(0, 0, 0, 0.1);
}

.homework-item.completed {
  border-left: 4px solid #67C23A;
  background-color: #f0f9ff;
}

.homework-item.uncompleted {
  border-left: 4px solid #E6A23C;
  background-color: #fdf6ec;
}

.homework-item .homework-info {
  flex: 1;
}

.homework-name {
  font-size: 16px;
  font-weight: bold;
  color: #303133;
  margin-bottom: 8px;
}

.homework-details {
  display: flex;
  gap: 16px;
  margin-bottom: 8px;
}

.homework-desc {
  font-size: 14px;
  color: #909399;
  line-height: 1.4;
}

.homework-actions {
  margin-left: 16px;
}

/* 作业详情样式 */
.homework-detail-container {
  padding: 20px 0;
}

.homework-detail-container .homework-info {
  text-align: center;
  margin-bottom: 30px;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px;
}

.homework-detail-container .homework-info h2 {
  margin: 0 0 10px 0;
  font-size: 24px;
  font-weight: bold;
}

.homework-detail-container .homework-info .homework-desc {
  margin: 10px 0;
  font-size: 14px;
  opacity: 0.9;
}

.homework-stats {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin-top: 15px;
  flex-wrap: wrap;
}

.homework-stats span {
  background: rgba(255, 255, 255, 0.2);
  padding: 8px 16px;
  border-radius: 20px;
  font-size: 14px;
  font-weight: 500;
}

/* 文件列表样式 */
.file-list {
  margin: 15px 0;
}

.file-item {
  display: flex;
  align-items: center;
  padding: 12px;
  border: 1px solid #e4e7ed;
  border-radius: 6px;
  margin-bottom: 8px;
  background: #f8f9fa;
}

.file-item:last-child {
  margin-bottom: 0;
}

.file-item i {
  color: #409eff;
  margin-right: 8px;
  font-size: 16px;
}

.file-name {
  flex: 1;
  color: #606266;
  font-size: 14px;
}

/* 提交内容样式 */
.submission-content {
  margin: 20px 0;
  padding: 20px;
  background: #f8f9fa;
  border-radius: 8px;
  border-left: 4px solid #409eff;
}

.submission-content h3 {
  margin: 0 0 15px 0;
  color: #303133;
  font-size: 16px;
  font-weight: bold;
}

.content-text {
  color: #606266;
  line-height: 1.6;
  font-size: 14px;
  white-space: pre-wrap;
}

/* 评分信息样式 */
.grade-info {
  margin: 20px 0;
  padding: 20px;
  background: #f0f9ff;
  border-radius: 8px;
  border-left: 4px solid #67c23a;
}

.grade-info h3 {
  margin: 0 0 15px 0;
  color: #303133;
  font-size: 16px;
  font-weight: bold;
}

.grade-comment {
  color: #606266;
  line-height: 1.6;
  font-size: 14px;
  white-space: pre-wrap;
}

/* 提交作业容器样式 */
.submit-homework-container {
  padding: 20px 0;
}

.submit-homework-container .homework-info {
  margin-bottom: 30px;
  padding: 20px;
  background: linear-gradient(135deg, #409eff 0%, #67c23a 100%);
  color: white;
  border-radius: 8px;
}

.submit-homework-container .homework-info h3 {
  margin: 0 0 10px 0;
  font-size: 20px;
  font-weight: bold;
}

.submit-homework-container .homework-info .homework-desc {
  margin: 10px 0;
  font-size: 14px;
  opacity: 0.9;
}

.submit-homework-container .homework-stats {
  display: flex;
  gap: 20px;
  margin-top: 15px;
  flex-wrap: wrap;
}

.submit-homework-container .homework-stats span {
  background: rgba(255, 255, 255, 0.2);
  padding: 6px 12px;
  border-radius: 15px;
  font-size: 12px;
  font-weight: 500;
}

/* 试卷内容样式 */
.quiz-content {
  display: flex;
  flex-direction: column;
  gap: 20px;
}
</style>