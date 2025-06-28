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
                完成时间：{{ getScoreSubmitTime(paper.paperId) }}
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
                    <span class="option-content">{{ option.optionContent || option }}</span>
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
  </div>
</template>
<script>
import { listPaper, getPaper } from "@/api/system/paper";
import { listScore, addScore, getScore } from "@/api/system/score";
import { listQuestion } from "@/api/system/question";
import { addTask } from "@/api/system/task";
import { listResource } from "@/api/system/resource";
import { parseTime } from "@/utils/ruoyi";

export default {
  name: "QuizAnswer",
  props: {
    courseId: {
      type: [String, Number],
      required: true
    }
  },
  data() {
    return {
      // 试卷列表
      paperList: [],
      // 成绩列表
      scoreList: [],
      // 已完成试卷
      completedPapers: [],
      // 未完成试卷
      uncompletedPapers: [],
      // 当前试卷详情
      currentPaper: {},
      // 当前试卷的题目列表
      questionList: [],
      // 当前试卷的成绩
      currentScore: null,
      // 试卷详情弹窗可见性
      paperDetailVisible: false,
      // 答题弹窗可见性
      quizVisible: false,
      // 用户答案
      userAnswers: [],
      // 是否已提交
      isSubmitted: false,
      // 最终得分
      finalScore: 0,
      // 计时器
      timer: null,
      // 剩余时间
      remainingTime: '',
      // 提交中状态
      isSubmitting: false,
      // 添加学习任务弹窗
      addTaskDialogVisible: false,
      addTaskForm: {
        taskName: '',
        taskType: '在线答题',
        dueDate: '',
        submitMethod: '在线答题',
        paperId: null,
        resourceId: null
      },
      addTaskRules: {
        taskName: [
          { required: true, message: '请输入任务名称', trigger: 'blur' }
        ],
        dueDate: [
          { required: true, message: '请选择截止日期', trigger: 'change' }
        ],
        paperId: [
          { required: true, message: '请选择关联试卷', trigger: 'change' }
        ]
      },
      // 可选试卷和资源列表
      selectablePapers: [],
      selectableResources: []
    };
  },
  created() {
    this.loadPapersAndScores();
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer);
    }
  },
  methods: {
    // 加载试卷和成绩
    loadPapersAndScores() {
      listPaper({ courseId: this.courseId }).then(response => {
        this.paperList = response.rows;
        return listScore({ courseId: this.courseId, userId: this.$store.getters.userId });
      }).then(response => {
        this.scoreList = response.rows;
        this.classifyPapers();
      });
    },
    // 分类试卷
    classifyPapers() {
      this.completedPapers = this.paperList.filter(paper => this.isPaperCompleted(paper.paperId));
      this.uncompletedPapers = this.paperList.filter(paper => !this.isPaperCompleted(paper.paperId));
    },
    // 判断试卷是否已完成
    isPaperCompleted(paperId) {
      return this.scoreList.some(score => score.paperId === paperId);
    },
    // 获取试卷得分
    getScore(paperId) {
      const score = this.scoreList.find(s => s.paperId === paperId);
      return score ? score.score : 'N/A';
    },
    // 获取提交时间
    getScoreSubmitTime(paperId) {
      const score = this.scoreList.find(s => s.paperId === paperId);
      return score ? this.formatDate(score.submitTime) : 'N/A';
    },
    // 格式化日期
    formatDate(date) {
      return parseTime(date, '{y}-{m}-{d} {h}:{i}');
    },
    // 查看试卷详情
    viewPaperDetail(paper) {
      this.currentPaper = paper;
      this.currentScore = this.scoreList.find(s => s.paperId === paper.paperId) || null;
      listQuestion({ paperId: paper.paperId }).then(response => {
        this.questionList = response.rows.map(q => {
          try {
            q.options = JSON.parse(q.questionContent).options;
            q.questionContent = JSON.parse(q.questionContent).stem;
          } catch (e) { /* do nothing */ }
          return q;
        });
        this.paperDetailVisible = true;
      });
    },
    // 开始作答
    startQuiz(paper) {
      this.currentPaper = paper;
      listQuestion({ paperId: paper.paperId }).then(response => {
        this.questionList = response.rows.map(q => {
          try {
            const content = JSON.parse(q.questionContent);
            q.options = content.options;
            q.questionContent = content.stem;
          } catch (e) {
            q.options = [];
          }
          return q;
        });
        this.userAnswers = new Array(this.questionList.length).fill(null);
        this.isSubmitted = false;
        this.isSubmitting = false;
        this.finalScore = 0;
        this.quizVisible = true;
        this.remainingTime = paper.timeLimit * 60;
        this.startTimer();
      });
    },
    // 开始计时器
    startTimer() {
      let seconds = this.remainingTime;
      if (isNaN(seconds) || seconds <= 0) {
        seconds = 3600; // 默认1小时
      }
      this.timer = setInterval(() => {
        if (seconds > 0) {
          seconds--;
          const mins = Math.floor(seconds / 60);
          const secs = seconds % 60;
          this.remainingTime = `${mins}分${secs}秒`;
        } else {
          clearInterval(this.timer);
          this.remainingTime = '时间到！';
          this.submitQuiz();
        }
      }, 1000);
    },
    // 取消答题
    cancelQuiz() {
      this.$confirm('确定要放弃本次作答吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        clearInterval(this.timer);
        this.quizVisible = false;
      });
    },
    // 提交答案
    submitQuiz() {
      this.isSubmitting = true;
      clearInterval(this.timer);
      this.calculateScore();
      const scoreData = {
        userId: this.$store.getters.userId,
        paperId: this.currentPaper.paperId,
        courseId: this.courseId,
        score: this.finalScore,
        answerContent: JSON.stringify(this.userAnswers)
      };
      // 在提交前，确保courseId存在
      if (!scoreData.courseId) {
          scoreData.courseId = this.currentPaper.courseId;
      }
      addScore(scoreData).then(() => {
        this.isSubmitted = true;
        this.isSubmitting = false;
        this.loadPapersAndScores(); // 重新加载数据
      }).catch(() => {
        this.isSubmitting = false;
      });
    },
    // 计算得分
    calculateScore() {
      let score = 0;
      this.questionList.forEach((question, index) => {
        if (this.isAnswerCorrect(question, this.userAnswers[index])) {
          score += question.score;
        }
      });
      this.finalScore = score;
    },
    // 判断答案是否正确
    isAnswerCorrect(question, userAnswer) {
      return question.answer === userAnswer;
    },
    // 获取答案样式类
    getAnswerClass(index) {
      const question = this.questionList[index];
      const userAnswer = this.userAnswers[index];
      if (!userAnswer) return 'unanswered';
      return this.isAnswerCorrect(question, userAnswer) ? 'correct' : 'incorrect';
    },
    // 关闭答题弹窗
    closeQuiz() {
      this.quizVisible = false;
    },
    // 处理答题弹窗关闭事件
    handleQuizDialogClose() {
      if (this.timer) {
        clearInterval(this.timer);
      }
    },
    // 跳转到题库
    goToQuestionBank() {
      this.$router.push({ name: 'QuestionBank' });
    },
  },
};
</script>

<style lang="scss" scoped>
.quiz-container {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', 'Microsoft YaHei', '\5FAE\8F6F\96C5\9ED1', Arial, sans-serif;
  padding: 20px;
}

.quiz-header {
  margin-bottom: 20px;
}

.quiz-section {
  margin-bottom: 20px;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both
}

.section-title {
  font-size: 1.2em;
  font-weight: bold;
}

.section-count {
  font-size: 0.9em;
  color: #888;
}

.paper-list {
  list-style: none;
  padding: 0;
}

.paper-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border: 1px solid #e0e0e0;
  border-radius: 5px;
  margin-bottom: 10px;
  transition: box-shadow 0.3s;
}

.paper-item:hover {
  box-shadow: 0 2px 12px 0 rgba(0,0,0,0.1);
}

.paper-info .paper-name {
  font-size: 1.1em;
  font-weight: bold;
  margin-bottom: 5px;
}

.paper-info .paper-details,
.paper-info .paper-desc {
  font-size: 0.9em;
  color: #555;
  margin-bottom: 5px;
}

.paper-actions .el-button {
  margin-left: 10px;
}

.empty-state {
  text-align: center;
  padding: 20px;
  color: #888;
}

/* 弹窗样式 */
.paper-detail-container h2, .quiz-container h2 {
  text-align: center;
  margin-bottom: 20px;
}

.paper-detail-container .paper-desc, .quiz-container .paper-desc {
  text-align: center;
  color: #888;
  margin-bottom: 20px;
}

.paper-detail-container .paper-stats, .quiz-container .paper-stats {
  text-align: center;
  margin-bottom: 20px;
}

.paper-detail-container .paper-stats span, .quiz-container .paper-stats span {
  margin: 0 15px;
}

.question-list {
  margin-top: 20px;
}

.question-item {
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 5px;
}

.question-header {
  font-weight: bold;
  margin-bottom: 10px;
}

.question-header .question-type {
  font-size: 0.8em;
  color: #fff;
  background-color: #409EFF;
  padding: 2px 5px;
  border-radius: 3px;
  margin-left: 10px;
}

.question-header .question-score,
.question-header .question-difficulty {
  font-size: 0.8em;
  color: #888;
  margin-left: 10px;
}

.question-content .content-text {
  margin-bottom: 10px;
}

.options-list {
  list-style: none;
  padding: 0;
}

.option-item {
  margin-bottom: 10px;
}

.answer-section {
  margin-top: 10px;
  font-weight: bold;
}

.answer-section .answer-content {
  color: #67C23A;
}

.result-container .result-header {
  text-align: center;
  margin-bottom: 20px;
}

.result-container .score-display {
  font-size: 1.5em;
}

.result-container .score-value {
  font-weight: bold;
  color: #409EFF;
}

.answer-comparison {
  margin-top: 10px;
}

.answer-comparison .user-answer,
.answer-comparison .correct-answer {
  margin-bottom: 5px;
}

.answer-comparison .answer-label {
  font-weight: bold;
}

.answer-comparison .answer-content.correct {
  color: #67C23A;
}

.answer-comparison .answer-content.incorrect {
  color: #F56C6C;
  text-decoration: line-through;
}
</style>
