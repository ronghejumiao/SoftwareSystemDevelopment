<template>
  <div class="app-container">
    <!-- 课程信息显示区域 -->
    <el-card class="course-info-card" v-if="courseId">
      <div class="course-info">
        <el-row :gutter="20">
          <el-col :span="8">
            <div class="info-item">
              <span class="label">当前课程ID：</span>
              <span class="value">{{ courseId }}</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="label">题目总数：</span>
              <span class="value">{{ allQuestions.length }}题</span>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="info-item">
              <span class="label">状态：</span>
              <el-tag type="success">正常</el-tag>
            </div>
          </el-col>
        </el-row>
      </div>
    </el-card>

    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="题库ID" prop="bankId">
        <el-input
          v-model="queryParams.bankId"
          placeholder="请输入题库ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="知识点ID" prop="knowledgeNodeId">
        <el-input
          v-model="queryParams.knowledgeNodeId"
          placeholder="请输入知识点ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="难度等级" prop="difficultyLevel">
        <el-input
          v-model="queryParams.difficultyLevel"
          placeholder="请输入难度等级"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="分值" prop="score">
        <el-input
          v-model="queryParams.score"
          placeholder="请输入分值"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:question:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:question:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:question:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:question:export']"
        >导出</el-button>

      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain

          size="mini"
          @click="jumpToLibrary"
          v-hasPermi="['system:library']"
        >试卷库</el-button>

      </el-col>

      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          size="mini"
          @click="handleGeneratePaper"
          v-hasPermi="['system:library']"
        >生成试卷</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getQuestionsByCourse"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="questionList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="题目ID" align="center" prop="questionId" />
      <el-table-column label="题库ID" align="center" prop="bankId" />
      <el-table-column label="知识点ID" align="center" prop="knowledgeNodeId" />
      <el-table-column label="题目类型" align="center" prop="questionType" />
      <el-table-column label="题目内容" align="center" prop="questionContent" />
      <el-table-column label="选项内容" align="center" prop="options" />
      <el-table-column label="答案" align="center" prop="answer" />
      <el-table-column label="难度等级" align="center" prop="difficultyLevel" />
      <el-table-column label="分值" align="center" prop="score" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:question:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:question:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="handlePagination"
    />

    <!-- 添加或修改题目，存储题库中的题目信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="题库ID" prop="bankId">
          <el-input v-model="form.bankId" placeholder="请输入题库ID" />
        </el-form-item>
        <el-form-item label="知识点ID" prop="knowledgeNodeId">
          <el-input v-model="form.knowledgeNodeId" placeholder="请输入知识点ID" />
        </el-form-item>
        <el-form-item label="题目内容">
          <editor v-model="form.questionContent" :min-height="192"/>
        </el-form-item>
        <el-form-item label="选项内容" prop="options">
          <el-input v-model="form.options" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="答案" prop="answer">
          <el-input v-model="form.answer" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="难度等级" prop="difficultyLevel">
          <el-input v-model="form.difficultyLevel" placeholder="请输入难度等级" />
        </el-form-item>
        <el-form-item label="分值" prop="score">
          <el-input v-model="form.score" placeholder="请输入分值" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 试卷生成对话框 -->
    <el-dialog
      title="生成试卷"
      :visible.sync="paperDialogVisible"
      width="80%"
      append-to-body
      :close-on-click-modal="true"
      :close-on-press-escape="true"
      @close="closePaperDialog"
    >
      <div class="paper-generator">
        <!-- 试卷基本信息 -->
        <el-card class="config-card">
          <div slot="header">
            <span>试卷基本信息</span>
          </div>
          <el-form :model="paperInfo" label-width="120px">
            <el-form-item label="试卷名称">
              <el-input
                v-model="paperInfo.paperName"
                placeholder="请输入试卷名称"
                style="width: 400px;"
              ></el-input>
            </el-form-item>
            <el-form-item label="试卷描述">
              <el-input
                v-model="paperInfo.paperDesc"
                type="textarea"
                placeholder="请输入试卷描述"
                :rows="2"
                style="width: 400px;"
              ></el-input>
            </el-form-item>
          </el-form>
        </el-card>

        <!-- 组卷方式选择 -->
        <el-row :gutter="20" class="mb-20">
          <el-col :span="24">
            <el-radio-group v-model="paperConfig.mode" @change="handleModeChange">
              <el-radio label="manual">手动组卷</el-radio>
              <el-radio label="auto">随机组卷</el-radio>
            </el-radio-group>
          </el-col>
        </el-row>

        <!-- 手动组卷 -->
        <div v-if="paperConfig.mode === 'manual'" class="manual-mode">
          <el-row :gutter="20">
            <!-- 题目筛选 -->
            <el-col :span="16">
              <el-card class="filter-card">
                <div slot="header">
                  <span>题目筛选</span>
                </div>
                <el-form :inline="true" :model="filterForm" class="filter-form">
                  <el-form-item label="题目类型">
                    <el-select v-model="filterForm.questionType" placeholder="选择题目类型" clearable>
                      <el-option label="选择" value="选择"></el-option>
                      <el-option label="填空" value="填空"></el-option>
                      <el-option label="简答" value="简答"></el-option>
                    </el-select>
                  </el-form-item>
                  <el-form-item label="难度等级">
                    <el-select v-model="filterForm.difficultyLevel" placeholder="选择难度等级" clearable>
                      <el-option label="简单" value="简单"></el-option>
                      <el-option label="中等" value="中等"></el-option>
                      <el-option label="困难" value="困难"></el-option>
                    </el-select>
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" @click="filterQuestions">筛选</el-button>
                    <el-button @click="resetFilter">重置</el-button>
                  </el-form-item>
                </el-form>

                <!-- 题目统计信息 -->
                <div class="question-stats">
                  <el-row :gutter="20">
                    <el-col :span="6">
                      <div class="stat-item">
                        <span class="stat-label">选择题：</span>
                        <span class="stat-value">{{ allQuestions.filter(q => (q.questionType || '').includes('选择')).length }}题</span>
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="stat-item">
                        <span class="stat-label">填空题：</span>
                        <span class="stat-value">{{ allQuestions.filter(q => (q.questionType || '').includes('填空')).length }}题</span>
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="stat-item">
                        <span class="stat-label">简答题：</span>
                        <span class="stat-value">{{ allQuestions.filter(q => (q.questionType || '').includes('简答')).length }}题</span>
                      </div>
                    </el-col>
                    <el-col :span="6">
                      <div class="stat-item">
                        <span class="stat-label">总计：</span>
                        <span class="stat-value">{{ allQuestions.length }}题</span>
                      </div>
                    </el-col>
                  </el-row>
                </div>

                <!-- 题目列表 -->
                <div class="question-list">
                  <div
                    v-for="question in filteredQuestions"
                    :key="question.questionId"
                    class="question-item"
                    :class="{ 'selected': selectedQuestions.includes(question.questionId) }"
                    @click="toggleQuestion(question)"
                  >
                    <div class="question-header">
                      <span class="question-type">{{ question.questionType }}</span>
                      <span class="difficulty-level">{{ question.difficultyLevel }}</span>
                      <span class="score">{{ question.score }}分</span>
                    </div>
                    <div class="question-content">{{ question.questionContent }}</div>
                  </div>
                </div>
              </el-card>
            </el-col>

            <!-- 已选题目 -->
            <el-col :span="8">
              <el-card class="selected-card">
                <div slot="header">
                  <span>已选题目 ({{ selectedQuestions.length }})</span>
                  <el-button style="float: right; padding: 3px 0" type="text" @click="clearSelected">清空</el-button>
                </div>
                <div class="selected-list">
                  <div
                    v-for="questionId in selectedQuestions"
                    :key="questionId"
                    class="selected-item"
                  >
                    <div class="selected-header">
                      <span>{{ getQuestionById(questionId).questionType }}</span>
                      <span>{{ getQuestionById(questionId).difficultyLevel }}</span>
                      <span>{{ getQuestionById(questionId).score }}分</span>
                      <el-button
                        type="text"
                        size="mini"
                        @click="removeQuestion(questionId)"
                      >删除</el-button>
                    </div>
                    <div class="selected-content">{{ getQuestionById(questionId).questionContent }}</div>
                  </div>
                </div>
                <div class="total-info">
                  <p>总分：{{ totalScore }}分</p>
                  <p>题目数：{{ selectedQuestions.length }}题</p>
                  <p>选择题：{{ questionStats.choice }}题 | 填空题：{{ questionStats.fill }}题 | 简答题：{{ questionStats.essay }}题</p>
                  <p>简单：{{ questionStats.easy }}题 | 中等：{{ questionStats.medium }}题 | 困难：{{ questionStats.hard }}题</p>
                </div>
              </el-card>
            </el-col>
          </el-row>
        </div>

        <!-- 随机组卷 -->
        <div v-if="paperConfig.mode === 'auto'" class="auto-mode">
          <el-form :model="autoConfig" label-width="120px">
            <!-- 总分与分值控制 -->
            <el-card class="config-card">
              <div slot="header">
                <span>总分与分值控制</span>
              </div>
              <el-row :gutter="20">
                <el-col :span="8">
                  <el-form-item label="预设总分">
                    <el-input-number
                      v-model="autoConfig.totalScore"
                      :min="1"
                      :max="200"
                      @change="calculateQuestions"
                    ></el-input-number>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="总题目数量">
                    <el-input-number
                      v-model="autoConfig.totalQuestions"
                      :min="1"
                      :max="100"
                      @change="calculateQuestions"
                    ></el-input-number>
                  </el-form-item>
                </el-col>
              </el-row>

              <!-- 题型分值分配 -->
              <el-row :gutter="20">
                <el-col :span="8">
                  <el-form-item label="选择题分值">
                    <el-input-number
                      v-model="autoConfig.choiceScore"
                      :min="1"
                      :max="10"
                      @change="calculateQuestions"
                    ></el-input-number>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="填空题分值">
                    <el-input-number
                      v-model="autoConfig.fillScore"
                      :min="1"
                      :max="10"
                      @change="calculateQuestions"
                    ></el-input-number>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="简答题分值">
                    <el-input-number
                      v-model="autoConfig.essayScore"
                      :min="1"
                      :max="20"
                      @change="calculateQuestions"
                    ></el-input-number>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-card>

            <!-- 难度比例配置 -->
            <el-card class="config-card">
              <div slot="header">
                <span>难度比例配置</span>
              </div>
              <el-row :gutter="20">
                <el-col :span="8">
                  <el-form-item label="简单题比例">
                    <el-input-number
                      v-model="autoConfig.easyRatio"
                      :min="0"
                      :max="100"
                      @change="calculateQuestions"
                    ></el-input-number>
                    <span>%</span>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="中等题比例">
                    <el-input-number
                      v-model="autoConfig.mediumRatio"
                      :min="0"
                      :max="100"
                      @change="calculateQuestions"
                    ></el-input-number>
                    <span>%</span>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="困难题比例">
                    <el-input-number
                      v-model="autoConfig.hardRatio"
                      :min="0"
                      :max="100"
                      @change="calculateQuestions"
                    ></el-input-number>
                    <span>%</span>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-card>

            <!-- 题型比例配置 -->
            <el-card class="config-card">
              <div slot="header">
                <span>题型比例配置</span>
              </div>
              <el-row :gutter="20">
                <el-col :span="8">
                  <el-form-item label="选择题比例">
                    <el-input-number
                      v-model="autoConfig.choiceRatio"
                      :min="0"
                      :max="100"
                      @change="calculateQuestions"
                    ></el-input-number>
                    <span>%</span>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="填空题比例">
                    <el-input-number
                      v-model="autoConfig.fillRatio"
                      :min="0"
                      :max="100"
                      @change="calculateQuestions"
                    ></el-input-number>
                    <span>%</span>
                  </el-form-item>
                </el-col>
                <el-col :span="8">
                  <el-form-item label="简答题比例">
                    <el-input-number
                      v-model="autoConfig.essayRatio"
                      :min="0"
                      :max="100"
                      @change="calculateQuestions"
                    ></el-input-number>
                    <span>%</span>
                  </el-form-item>
                </el-col>
              </el-row>
            </el-card>

            <!-- 题目排序规则 -->
            <el-card class="config-card">
              <div slot="header">
                <span>题目排序规则</span>
              </div>
              <el-form-item label="排序方式">
                <el-radio-group v-model="autoConfig.sortRule">
                  <el-radio label="difficulty">按难度递增</el-radio>
                  <el-radio label="type">按题型分组</el-radio>
                  <el-radio label="random">随机排序</el-radio>
                </el-radio-group>
              </el-form-item>
            </el-card>

            <!-- 预览信息 -->
            <el-card class="config-card">
              <div slot="header">
                <span>预览信息</span>
              </div>
              <el-row :gutter="20">
                <el-col :span="6">
                  <p>选择题：{{ autoConfig.choiceCount }}题 ({{ autoConfig.choiceScore }}分/题)</p>
                </el-col>
                <el-col :span="6">
                  <p>填空题：{{ autoConfig.fillCount }}题 ({{ autoConfig.fillScore }}分/题)</p>
                </el-col>
                <el-col :span="6">
                  <p>简答题：{{ autoConfig.essayCount }}题 ({{ autoConfig.essayScore }}分/题)</p>
                </el-col>
                <el-col :span="6">
                  <p>总分：{{ calculatedTotalScore }}分</p>
                </el-col>
              </el-row>
            </el-card>
          </el-form>
        </div>
      </div>

      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="generatePaper">生成试卷</el-button>
        <el-button @click="closePaperDialog">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listQuestion, getQuestion, delQuestion, addQuestion, updateQuestion, getQuestionsByCourseId, getLibraryByCourseId, generatePaper } from "@/api/system/question"

export default {
  name: "Question",
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
      // 题目，存储题库中的题目信息表格数据
      questionList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        bankId: null,
        knowledgeNodeId: null,
        questionType: null,
        questionContent: null,
        options: null,
        answer: null,
        difficultyLevel: null,
        score: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        bankId: [
          { required: true, message: "题库ID，关联question_bank表不能为空", trigger: "blur" }
        ],
        questionType: [
          { required: true, message: "题目类型不能为空", trigger: "change" }
        ],
        questionContent: [
          { required: true, message: "题目内容不能为空", trigger: "blur" }
        ],
        answer: [
          { required: true, message: "答案不能为空", trigger: "blur" }
        ],
        difficultyLevel: [
          { required: true, message: "难度等级不能为空", trigger: "blur" }
        ],
        score: [
          { required: true, message: "分值不能为空", trigger: "blur" }
        ],
        createTime: [
          { required: true, message: "创建时间不能为空", trigger: "blur" }
        ],
      },
      // 试卷生成相关数据
      paperDialogVisible: false,
      courseId: null, // 从路由参数获取
      allQuestions: [], // 所有题目
      filteredQuestions: [], // 筛选后的题目
      paperConfig: {
        mode: 'manual', // manual: 手动组卷, auto: 随机组卷
      },
      filterForm: {
        questionType: '',
        difficultyLevel: '',
      },
      selectedQuestions: [], // 已选题目ID数组
      totalScore: 0,
      autoConfig: {
        totalScore: 100,
        totalQuestions: 20,
        choiceScore: 2,
        fillScore: 5,
        essayScore: 10,
        easyRatio: 40,
        mediumRatio: 30,
        hardRatio: 30,
        choiceRatio: 50,
        fillRatio: 30,
        essayRatio: 20,
        sortRule: 'difficulty',
        choiceCount: 0,
        fillCount: 0,
        essayCount: 0,
      },
      calculatedTotalScore: 0,
      paperInfo: {
        paperName: '',
        paperDesc: '',
      },
    }
  },
  computed: {
    // 计算已选题目数据
    selectedQuestionsData() {
      return this.selectedQuestions.map(id => this.getQuestionById(id))
    },

    // 计算题目统计信息
    questionStats() {
      const stats = {
        choice: 0,
        fill: 0,
        essay: 0,
        easy: 0,
        medium: 0,
        hard: 0
      }

      this.selectedQuestionsData.forEach(question => {
        // 按题型统计 - 使用模糊匹配
        const questionType = question.questionType || ''
        if (questionType.includes('选择')) stats.choice++
        else if (questionType.includes('填空')) stats.fill++
        else if (questionType.includes('简答')) stats.essay++

        // 按难度统计
        if (question.difficultyLevel === '简单') stats.easy++
        else if (question.difficultyLevel === '中等') stats.medium++
        else if (question.difficultyLevel === '困难') stats.hard++
      })

      return stats
    }
  },
  created() {
    // 临时设置courseId，后续可以从路由参数获取
    this.courseId = this.$route.query.courseId || this.$route.params.courseId || 10001 // 临时设置为1，实际应该从课程模块获取

    // 根据courseId查询题目
    this.getQuestionsByCourse()

    // 初始化随机组卷配置
    this.calculateQuestions()
  },
  methods: {
    jumpToLibrary() {
      this.$router.push({
        path: '/system/paper',
        query: { courseId: this.courseId }
      });
    },

    /** 查询题目，存储题库中的题目信息列表 */
    getList() {
      this.loading = true
      listQuestion(this.queryParams).then(response => {
        this.questionList = response.rows
        this.total = response.total
        this.loading = false
      })
    },

    /** 根据课程ID查询题目列表 */
    getQuestionsByCourse() {
      if (!this.courseId) {
        this.$modal.msgError("未找到课程ID")
        return
      }

      this.loading = true
      getQuestionsByCourseId(this.courseId).then(response => {
        this.allQuestions = response.data || []
        this.filteredQuestions = [...this.allQuestions]
        this.updateQuestionList() // 更新表格数据并应用分页
        this.loading = false
      }).catch(() => {
        this.$modal.msgError("加载题目失败")
        this.loading = false
      })
    },

    /** 更新表格数据并应用分页 */
    updateQuestionList() {
      const startIndex = (this.queryParams.pageNum - 1) * this.queryParams.pageSize
      const endIndex = startIndex + this.queryParams.pageSize
      this.questionList = this.allQuestions.slice(startIndex, endIndex)
      this.total = this.allQuestions.length
    },

    /** 处理分页变化 */
    handlePagination() {
      this.updateQuestionList()
    },

    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        questionId: null,
        bankId: null,
        knowledgeNodeId: null,
        questionType: null,
        questionContent: null,
        options: null,
        answer: null,
        difficultyLevel: null,
        score: null,
        createTime: null,
        updateTime: null
      }
      this.resetForm("form")
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.updateQuestionList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.queryParams.pageNum = 1
      this.updateQuestionList()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.questionId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加题目，存储题库中的题目信息"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const questionId = row.questionId || this.ids
      getQuestion(questionId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改题目，存储题库中的题目信息"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.questionId != null) {
            updateQuestion(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getQuestionsByCourse() // 重新加载课程题目
            })
          } else {
            addQuestion(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getQuestionsByCourse() // 重新加载课程题目
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const questionIds = row.questionId || this.ids
      this.$modal.confirm('是否确认删除题目，存储题库中的题目信息编号为"' + questionIds + '"的数据项？').then(function() {
        return delQuestion(questionIds)
      }).then(() => {
        this.getQuestionsByCourse() // 重新加载课程题目
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/question/export', {
        ...this.queryParams
      }, `question_${new Date().getTime()}.xlsx`)
    },

    // 试卷生成相关方法
    handleGeneratePaper() {
      if (!this.courseId) {
        this.$modal.msgError("未找到课程ID，请从课程详情页面进入")
        return
      }
      // 设置默认试卷信息
      this.paperInfo.paperName = `试卷_${new Date().toLocaleDateString()}`
      this.paperInfo.paperDesc = ''
      this.paperDialogVisible = true
    },

    // 组卷方式改变
    handleModeChange() {
      if (this.paperConfig.mode === 'auto') {
        this.calculateQuestions()
      }
    },

    // 筛选题目
    filterQuestions() {
      this.filteredQuestions = this.allQuestions.filter(question => {
        let match = true

        // 题型筛选 - 使用模糊匹配
        if (this.filterForm.questionType) {
          const questionType = question.questionType || ''
          const filterType = this.filterForm.questionType
          if (!questionType.includes(filterType)) {
            match = false
          }
        }

        // 难度等级筛选
        if (this.filterForm.difficultyLevel && question.difficultyLevel !== this.filterForm.difficultyLevel) {
          match = false
        }

        return match
      })
    },

    // 重置筛选
    resetFilter() {
      this.filterForm = {
        questionType: '',
        difficultyLevel: '',
      }
      this.filteredQuestions = [...this.allQuestions]
    },

    // 切换题目选择状态
    toggleQuestion(question) {
      const index = this.selectedQuestions.indexOf(question.questionId)
      if (index > -1) {
        this.selectedQuestions.splice(index, 1)
      } else {
        this.selectedQuestions.push(question.questionId)
      }
      this.calculateTotalScore()
    },

    // 清空已选题目
    clearSelected() {
      this.selectedQuestions = []
      this.calculateTotalScore()
    },

    // 移除已选题目
    removeQuestion(questionId) {
      const index = this.selectedQuestions.indexOf(questionId)
      if (index > -1) {
        this.selectedQuestions.splice(index, 1)
        this.calculateTotalScore()
      }
    },

    // 根据ID获取题目
    getQuestionById(questionId) {
      return this.allQuestions.find(q => q.questionId === questionId) || {}
    },

    // 计算总分
    calculateTotalScore() {
      this.totalScore = this.selectedQuestions.reduce((total, questionId) => {
        const question = this.getQuestionById(questionId)
        return total + (question.score || 0)
      }, 0)
    },

    // 计算随机组卷题目数量
    calculateQuestions() {
      const total = this.autoConfig.totalQuestions
      const choiceRatio = this.autoConfig.choiceRatio / 100
      const fillRatio = this.autoConfig.fillRatio / 100
      const essayRatio = this.autoConfig.essayRatio / 100

      this.autoConfig.choiceCount = Math.round(total * choiceRatio)
      this.autoConfig.fillCount = Math.round(total * fillRatio)
      this.autoConfig.essayCount = total - this.autoConfig.choiceCount - this.autoConfig.fillCount

      // 计算总分
      this.calculatedTotalScore =
        this.autoConfig.choiceCount * this.autoConfig.choiceScore +
        this.autoConfig.fillCount * this.autoConfig.fillScore +
        this.autoConfig.essayCount * this.autoConfig.essayScore
    },

    // 生成试卷
    generatePaper() {
      if (this.paperConfig.mode === 'manual') {
        if (this.selectedQuestions.length === 0) {
          this.$modal.msgError("请至少选择一道题目")
          return
        }
        this.generateManualPaper()
      } else {
        this.generateAutoPaper()
      }
    },

    // 生成手动组卷试卷
    generateManualPaper() {
      const selectedQuestionsData = this.selectedQuestions.map(id => this.getQuestionById(id))
      const paperData = {
        courseId: this.courseId,
        paperName: this.paperInfo.paperName || `手动组卷试卷_${new Date().toLocaleDateString()}`,
        paperDesc: this.paperInfo.paperDesc || `手动组卷，共${selectedQuestionsData.length}题，总分${this.totalScore}分`,
        totalScore: this.totalScore,
        content: selectedQuestionsData,
        mode: 'manual'
      }
      this.submitPaper(paperData)
    },

    // 生成随机组卷试卷
    generateAutoPaper() {
      // 验证配置
      if (!this.validateAutoConfig()) {
        return
      }

      // 根据配置筛选题目 - 使用模糊匹配
      const choiceQuestions = this.allQuestions.filter(q => (q.questionType || '').includes('选择'))
      const fillQuestions = this.allQuestions.filter(q => (q.questionType || '').includes('填空'))
      const essayQuestions = this.allQuestions.filter(q => (q.questionType || '').includes('简答'))

      // 按难度筛选
      const easyQuestions = this.allQuestions.filter(q => q.difficultyLevel === '简单')
      const mediumQuestions = this.allQuestions.filter(q => q.difficultyLevel === '中等')
      const hardQuestions = this.allQuestions.filter(q => q.difficultyLevel === '困难')

      // 随机选择题目
      const selectedQuestions = []

      // 选择题
      const choiceCount = this.autoConfig.choiceCount
      if (choiceQuestions.length >= choiceCount) {
        selectedQuestions.push(...this.getRandomQuestions(choiceQuestions, choiceCount))
      } else {
        this.$modal.msgError(`选择题数量不足，需要${choiceCount}题，实际只有${choiceQuestions.length}题`)
        return
      }

      // 填空题
      const fillCount = this.autoConfig.fillCount
      if (fillQuestions.length >= fillCount) {
        selectedQuestions.push(...this.getRandomQuestions(fillQuestions, fillCount))
      } else {
        this.$modal.msgError(`填空题数量不足，需要${fillCount}题，实际只有${fillQuestions.length}题`)
        return
      }

      // 简答题
      const essayCount = this.autoConfig.essayCount
      if (essayQuestions.length >= essayCount) {
        selectedQuestions.push(...this.getRandomQuestions(essayQuestions, essayCount))
      } else {
        this.$modal.msgError(`简答题数量不足，需要${essayCount}题，实际只有${essayQuestions.length}题`)
        return
      }

      // 排序
      this.sortQuestions(selectedQuestions)

      const paperData = {
        courseId: this.courseId,
        paperName: this.paperInfo.paperName || `随机组卷试卷_${new Date().toLocaleDateString()}`,
        paperDesc: this.paperInfo.paperDesc || `随机组卷，共${selectedQuestions.length}题，总分${this.calculatedTotalScore}分`,
        totalScore: this.calculatedTotalScore,
        content: selectedQuestions,
        mode: 'auto',
        config: this.autoConfig
      }
      this.submitPaper(paperData)
    },

    // 随机选择题目
    getRandomQuestions(questions, count) {
      const shuffled = [...questions].sort(() => 0.5 - Math.random())
      return shuffled.slice(0, count)
    },

    // 排序题目
    sortQuestions(questions) {
      if (this.autoConfig.sortRule === 'difficulty') {
        const difficultyOrder = { '简单': 1, '中等': 2, '困难': 3 }
        questions.sort((a, b) => difficultyOrder[a.difficultyLevel] - difficultyOrder[b.difficultyLevel])
      } else if (this.autoConfig.sortRule === 'type') {
        // 使用模糊匹配进行题型排序
        const typeOrder = { '选择': 1, '填空': 2, '简答': 3 }
        questions.sort((a, b) => {
          const aType = Object.keys(typeOrder).find(type => (a.questionType || '').includes(type)) || '其他'
          const bType = Object.keys(typeOrder).find(type => (b.questionType || '').includes(type)) || '其他'
          return (typeOrder[aType] || 999) - (typeOrder[bType] || 999)
        })
      } else {
        // random - 已经随机了，不需要再排序
      }
    },

    // 提交试卷
    submitPaper(paperData) {
      generatePaper(paperData).then(response => {
        this.$modal.msgSuccess("试卷生成成功")
        this.closePaperDialog()
        // 跳转到试卷库页面，并传递刷新参数和courseId
        this.$router.push({
          path: '/system/paper',
          query: {
            refresh: 'true',
            timestamp: new Date().getTime(),
            courseId: this.courseId
          }
        })
      }).catch(() => {
        this.$modal.msgError("试卷生成失败")
      })
    },

    // 关闭试卷生成弹窗
    closePaperDialog() {
      this.paperDialogVisible = false
      this.resetPaperConfig()
    },

    // 重置试卷配置
    resetPaperConfig() {
      this.paperConfig = { mode: 'manual' }
      this.filterForm = { questionType: '', difficultyLevel: '' }
      this.selectedQuestions = []
      this.totalScore = 0
      this.autoConfig = {
        totalScore: 100,
        totalQuestions: 20,
        choiceScore: 2,
        fillScore: 5,
        essayScore: 10,
        easyRatio: 40,
        mediumRatio: 30,
        hardRatio: 30,
        choiceRatio: 50,
        fillRatio: 30,
        essayRatio: 20,
        sortRule: 'difficulty',
        choiceCount: 0,
        fillCount: 0,
        essayCount: 0,
      }
      this.calculatedTotalScore = 0
      this.paperInfo = {
        paperName: '',
        paperDesc: '',
      }
    },

    // 验证随机组卷配置
    validateAutoConfig() {
      // 验证题型比例总和是否为100%
      const typeRatioSum = this.autoConfig.choiceRatio + this.autoConfig.fillRatio + this.autoConfig.essayRatio
      if (Math.abs(typeRatioSum - 100) > 0.1) {
        this.$modal.msgError(`题型比例总和必须为100%，当前为${typeRatioSum}%`)
        return false
      }

      // 验证难度比例总和是否为100%
      const difficultyRatioSum = this.autoConfig.easyRatio + this.autoConfig.mediumRatio + this.autoConfig.hardRatio
      if (Math.abs(difficultyRatioSum - 100) > 0.1) {
        this.$modal.msgError(`难度比例总和必须为100%，当前为${difficultyRatioSum}%`)
        return false
      }

      // 验证总分是否合理
      if (this.autoConfig.totalScore <= 0) {
        this.$modal.msgError("总分必须大于0")
        return false
      }

      // 验证题目数量是否合理
      if (this.autoConfig.totalQuestions <= 0) {
        this.$modal.msgError("题目数量必须大于0")
        return false
      }

      // 验证分值是否合理
      if (this.autoConfig.choiceScore <= 0 || this.autoConfig.fillScore <= 0 || this.autoConfig.essayScore <= 0) {
        this.$modal.msgError("各题型分值必须大于0")
        return false
      }

      return true
    },
  }
}
</script>

<style scoped>
.paper-generator {
  padding: 20px 0;
}

.mb-20 {
  margin-bottom: 20px;
}

.filter-card, .selected-card, .config-card {
  margin-bottom: 20px;
}

.filter-form {
  margin-bottom: 20px;
}

.question-list {
  max-height: 400px;
  overflow-y: auto;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
}

.question-item {
  padding: 15px;
  border-bottom: 1px solid #f0f0f0;
  cursor: pointer;
  transition: background-color 0.3s;
}

.question-item:hover {
  background-color: #f5f7fa;
}

.question-item.selected {
  background-color: #e1f3d8;
  border-left: 4px solid #67c23a;
}

.question-header {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
  font-size: 12px;
  color: #909399;
}

.question-type {
  background-color: #409eff;
  color: white;
  padding: 2px 8px;
  border-radius: 10px;
}

.difficulty-level {
  background-color: #e6a23c;
  color: white;
  padding: 2px 8px;
  border-radius: 10px;
}

.score {
  background-color: #f56c6c;
  color: white;
  padding: 2px 8px;
  border-radius: 10px;
}

.question-content {
  line-height: 1.6;
  color: #303133;
}

.selected-list {
  max-height: 300px;
  overflow-y: auto;
}

.selected-item {
  padding: 10px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  margin-bottom: 10px;
  background-color: #fafafa;
}

.selected-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
  font-size: 12px;
  color: #909399;
}

.selected-content {
  line-height: 1.5;
  color: #303133;
  font-size: 14px;
}

.total-info {
  margin-top: 15px;
  padding: 10px;
  background-color: #f0f9ff;
  border-radius: 4px;
  border-left: 4px solid #409eff;
}

.total-info p {
  margin: 5px 0;
  font-weight: bold;
  color: #303133;
}

.config-card .el-form-item {
  margin-bottom: 15px;
}

.config-card .el-input-number {
  width: 120px;
}

.config-card .el-radio-group {
  display: flex;
  gap: 20px;
}

.auto-mode .el-card {
  margin-bottom: 20px;
}

.auto-mode .el-card__header {
  background-color: #f5f7fa;
  font-weight: bold;
}

.auto-mode .el-row {
  margin-bottom: 15px;
}

.auto-mode .el-col {
  display: flex;
  align-items: center;
}

.auto-mode .el-form-item__label {
  font-weight: bold;
  color: #606266;
}

.auto-mode p {
  margin: 5px 0;
  color: #606266;
}

.course-info-card {
  margin-bottom: 20px;
  background-color: #f8f9fa;
  border: 1px solid #e9ecef;
}

.course-info {
  padding: 10px 0;
}

.info-item {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 8px 0;
}

.info-item .label {
  font-weight: bold;
  color: #606266;
}

.info-item .value {
  color: #409eff;
  font-weight: bold;
}

.question-stats {
  margin-bottom: 20px;
  padding: 15px;
  background-color: #f8f9fa;
  border-radius: 4px;
  border: 1px solid #e9ecef;
}

.stat-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 8px 12px;
  border: 1px solid #e4e7ed;
  border-radius: 4px;
  background-color: white;
  margin-bottom: 8px;
}

.stat-label {
  font-weight: bold;
  color: #303133;
}

.stat-value {
  color: #409eff;
  font-weight: bold;
}
</style>
