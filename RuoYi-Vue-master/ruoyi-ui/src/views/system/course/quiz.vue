<template>
  <div class="quiz-container">
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
                  <el-radio 
                    v-model="userAnswers[index]" 
                    :label="String.fromCharCode(65 + optionIndex)"
                    class="option-radio"
                  >
                    <span class="option-label">{{ String.fromCharCode(65 + optionIndex) }}.</span>
                    <span class="option-content">{{ option }}</span>
                  </el-radio>
                </div>
              </div>
              
              <!-- 填空题 -->
              <div v-else class="fill-blank-section">
                <el-input
                  v-model="userAnswers[index]"
                  type="textarea"
                  :rows="3"
                  placeholder="请输入你的答案"
                  class="answer-input"
                />
              </div>
            </div>
          </div>
        </div>

        <!-- 答题结果 -->
        <div v-else class="result-container">
          <div class="result-header">
            <h3>答题完成！</h3>
            <div class="score-display">
              <span class="score-label">你的得分：</span>
              <span class="score-value">{{ finalScore }}分</span>
              <span class="score-total">/ {{ currentPaper.totalScore }}分</span>
            </div>
          </div>

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
                
                <!-- 答案对比 -->
                <div class="answer-comparison">
                  <div class="user-answer">
                    <div class="answer-label">你的答案：</div>
                    <div class="answer-content" :class="getAnswerClass(index)">
                      {{ userAnswers[index] || '未作答' }}
                    </div>
                  </div>
                  <div class="correct-answer">
                    <div class="answer-label">正确答案：</div>
                    <div class="answer-content correct">{{ question.answer }}</div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div slot="footer" class="dialog-footer">
        <el-button v-if="!isSubmitted" @click="cancelQuiz">取 消</el-button>
        <el-button 
          v-if="!isSubmitted" 
          type="primary" 
          @click="submitQuiz"
          :loading="isSubmitting"
        >
          提交答案
        </el-button>
        <el-button v-if="isSubmitted" @click="closeQuiz">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 添加任务弹窗 -->
    <el-dialog 
      title="添加学习任务" 
      :visible.sync="openAddDialog" 
      width="500px" 
      append-to-body
      @open="openAddTaskDialog"
    >
      <el-form :model="addForm" :rules="addRules" ref="addForm" label-width="90px">
        <el-form-item label="任务名称" prop="taskName">
          <el-input v-model="addForm.taskName" placeholder="请输入任务名称" />
        </el-form-item>
        <el-form-item label="任务类型" prop="taskType">
          <el-select v-model="addForm.taskType" placeholder="请选择任务类型">
            <el-option label="试卷完成" value="试卷完成" />
            <el-option label="资料阅读" value="资料阅读" />
            <el-option label="视频观看" value="视频观看" />
          </el-select>
        </el-form-item>
        <el-form-item label="截止日期" prop="dueDate">
          <el-date-picker
            v-model="addForm.dueDate"
            type="date"
            placeholder="选择截止日期"
            format="yyyy-MM-dd"
            value-format="yyyy-MM-dd"
          />
        </el-form-item>
        <el-form-item label="提交方式" prop="submitMethod">
          <el-select v-model="addForm.submitMethod" placeholder="请选择提交方式">
            <el-option label="试卷完成" value="试卷完成" />
            <el-option label="资料阅读" value="资料阅读" />
            <el-option label="视频观看" value="视频观看" />
          </el-select>
        </el-form-item>
        <el-form-item label="任务描述" prop="taskDesc">
          <el-input v-model="addForm.taskDesc" type="textarea" :rows="4" placeholder="请输入任务描述" />
        </el-form-item>
        <el-form-item v-if="addForm.submitMethod === '试卷完成'" label="选择试卷" prop="paperId">
          <el-select v-model="addForm.paperId" placeholder="请选择试卷">
            <el-option v-for="paper in paperList" :key="paper.paperId" :label="paper.paperName" :value="paper.paperId" />
          </el-select>
        </el-form-item>
        <el-form-item v-if="addForm.submitMethod === '资料阅读' || addForm.submitMethod === '视频观看'" label="选择资源" prop="resourceId">
          <el-select v-model="addForm.resourceId" placeholder="请选择资源">
            <el-option
              v-for="res in filteredResourceList"
              :key="res.resourceId"
              :label="res.resourceName + '（' + res.resourceType + '）'"
              :value="res.resourceId"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="openAddDialog = false">取 消</el-button>
        <el-button type="primary" @click="submitAddTask">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listPaperByCourseId, listPaper } from "@/api/system/paper";
import { getScoreByUserAndCourse, addScore } from "@/api/system/score";
import { getLearningRecordByUserAndCourse } from "@/api/system/learningRecord";
import { addTask } from "@/api/system/task";

export default {
  name: "Quiz",
  props: {
    courseId: {
      type: [String, Number],
      required: true
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
      }
    };
  },
  computed: {
    id() {
      return this.$store.state.user.id;
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
    // 监听courseId变化
    courseId: {
      handler(newCourseId) {
        if (newCourseId && newCourseId !== 'undefined' && newCourseId !== undefined) {
          this.getPaperList();
          this.getScoreList();
        }
      },
      immediate: true
    }
  },
  created() {
    // 如果courseId已经存在，立即加载数据
    if (this.courseId && this.courseId !== 'undefined' && this.courseId !== undefined) {
      this.getPaperList();
      this.getScoreList();
    }
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer);
    }
  },
  methods: {
    // 获取试卷列表
    getPaperList() {
      this.loading = true;
      console.log('开始获取试卷列表，courseId:', this.courseId);
      
      // 根据courseId查询试卷
      listPaperByCourseId(this.courseId).then(response => {
        console.log('API响应:', response);
        this.paperList = response.data || response.rows || [];
        console.log('试卷列表:', this.paperList);
        this.classifyPapers();
        this.loading = false;
      }).catch(error => {
        console.error('API调用失败:', error);
        // 如果新API不存在，回退到原来的API
        listPaper({ courseId: this.courseId, pageSize: 999 }).then(response => {
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
      console.log('获取成绩列表，userId:', userId, 'courseId:', this.courseId);
      
      // 确保courseId和userId都存在才调用API
      if (userId && this.courseId) {
        getScoreByUserAndCourse(userId, this.courseId).then(response => {
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
        path: '/system/question',
        query: { courseId: this.courseId }
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
      
      // 获取或创建学习记录
      const userId = this.id;
      getLearningRecordByUserAndCourse(userId, this.courseId).then(response => {
        const learningRecord = response.data;
        
        // 创建成绩记录
        const scoreData = {
          learningRecordId: learningRecord.recordId,
          paperId: this.currentPaper.paperId,
          score: this.finalScore,
          scoreDesc: `得分：${this.finalScore}/${this.currentPaper.totalScore}`,
          submitTime: new Date()
        };
        
        return addScore(scoreData);
      }).then(() => {
        this.isSubmitted = true;
        this.isSubmitting = false;
        if (this.timer) {
          clearInterval(this.timer);
        }
        this.$modal.msgSuccess('答题完成！');
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
      const paperRes = await this.$api.paper.listPaper({ courseId: this.courseId });
      this.paperList = paperRes.rows || [];
      // 拉取资源
      const resourceRes = await this.$api.resource.listResource({ courseId: this.courseId });
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
        this.addForm.courseId = this.courseId;
        await addTask(this.addForm);
        this.openAddDialog = false;
        this.$message.success('添加成功');
        this.initData();
      });
    }
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
</style> 