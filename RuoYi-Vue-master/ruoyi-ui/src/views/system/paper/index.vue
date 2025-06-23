<template>
  <div class="app-container">
    <!-- 课程信息显示区域 -->
    <el-card class="course-info-card" v-if="isStudentFromCourse && courseId">
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
              <span class="label">试卷总数：</span>
              <span class="value">{{ paperList.length }}份</span>
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

    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch && !isStudentFromCourse" label-width="68px">
      <el-form-item label="课程ID" prop="courseId">
        <el-input
          v-model="queryParams.courseId"
          placeholder="请输入课程ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="试卷库ID" prop="libraryId">
        <el-input
          v-model="queryParams.libraryId"
          placeholder="请输入试卷库ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="试卷名称" prop="paperName">
        <el-input
          v-model="queryParams.paperName"
          placeholder="请输入试卷名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="总分" prop="totalScore">
        <el-input
          v-model="queryParams.totalScore"
          placeholder="请输入总分"
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
          v-hasPermi="['system:paper:add']"
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
          v-hasPermi="['system:paper:edit']"
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
          v-hasPermi="['system:paper:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:paper:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="paperList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="试卷ID" align="center" prop="paperId" />
      <el-table-column label="试卷库ID" align="center" prop="libraryId" />
      <el-table-column label="试卷名称" align="center" prop="paperName" />
      <el-table-column label="试卷描述" align="center" prop="paperDesc" />
      <el-table-column label="总分" align="center" prop="totalScore" />
      <el-table-column label="试卷题目" align="center" width="120">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="primary"
            icon="el-icon-view"
            @click="handleViewQuestions(scope.row)"
          >详情</el-button>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:paper:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:paper:remove']"
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

    <!-- 添加或修改试卷，一个试卷库包含多个试卷对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="试卷库ID，关联course_paper_library表" prop="libraryId">
          <el-input v-model="form.libraryId" placeholder="请输入试卷库ID" />
        </el-form-item>
        <el-form-item label="试卷名称" prop="paperName">
          <el-input v-model="form.paperName" placeholder="请输入试卷名称" />
        </el-form-item>
        <el-form-item label="试卷描述" prop="paperDesc">
          <el-input v-model="form.paperDesc" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="总分" prop="totalScore">
          <el-input v-model="form.totalScore" placeholder="请输入总分" />
        </el-form-item>
        <el-form-item label="试卷题目内容，JSON格式存储">
          <editor v-model="form.content" :min-height="192"/>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 题目详情弹窗 -->
    <el-dialog 
      title="试卷题目详情" 
      :visible.sync="questionDetailVisible" 
      width="80%" 
      append-to-body
      :close-on-click-modal="true"
      :close-on-press-escape="true"
    >
      <div class="question-detail-container">
        <!-- 试卷基本信息 -->
        <div class="paper-info">
          <h2>{{ currentPaper.paperName }}</h2>
          <p class="paper-desc">{{ currentPaper.paperDesc }}</p>
          <div class="paper-stats">
            <span>总分：{{ currentPaper.totalScore }}分</span>
            <span>题目数：{{ questionList.length }}题</span>
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
        <el-button @click="questionDetailVisible = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listPaper, getPaper, delPaper, addPaper, updatePaper } from "@/api/system/paper"

export default {
  name: "Paper",
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
      // 试卷，一个试卷库包含多个试卷表格数据
      paperList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        courseId: null,
        libraryId: null,
        paperName: null,
        paperDesc: null,
        totalScore: null,
        content: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        libraryId: [
          { required: true, message: "试卷库ID，关联course_paper_library表不能为空", trigger: "blur" }
        ],
        paperName: [
          { required: true, message: "试卷名称不能为空", trigger: "blur" }
        ],
        totalScore: [
          { required: true, message: "总分不能为空", trigger: "blur" }
        ],
        createTime: [
          { required: true, message: "创建时间不能为空", trigger: "blur" }
        ],
      },
      // 题目详情相关属性
      questionDetailVisible: false,
      currentPaper: {},
      questionList: [],
      isStudentFromCourse: false,
      courseId: null
    }
  },
  created() {
    // 获取courseId，优先从路由参数获取
    this.courseId = this.$route.query.courseId || this.$route.params.courseId || null;
    
    // 判断是否为学生通过跳转进入
    this.isStudentFromCourse = !!this.courseId;
    
    // 如果是学生通过跳转进入，设置固定的courseId并禁用搜索
    if (this.isStudentFromCourse) {
      this.courseId = this.courseId || 1; // 如果没有courseId，默认使用1
      this.showSearch = false; // 禁用搜索功能
      this.queryParams.courseId = this.courseId; // 设置查询参数
    } else {
      // admin直接进入，展示所有试卷，允许搜索
      this.showSearch = true;
      // 移除courseId限制，允许查看所有试卷
      delete this.queryParams.courseId;
    }
    
    this.getList();
  },
  watch: {
    // 监听路由变化，确保页面刷新后保持courseId筛选
    '$route'(to, from) {
      const courseId = to.query.courseId || to.params.courseId || null;
      if (courseId !== this.courseId) {
        this.courseId = courseId;
        this.isStudentFromCourse = !!courseId;
        
        if (this.isStudentFromCourse) {
          this.showSearch = false;
          this.queryParams.courseId = courseId;
        } else {
          this.showSearch = true;
          delete this.queryParams.courseId;
        }
        
        this.getList();
      }
    }
  },
  methods: {
    /** 查询试卷，一个试卷库包含多个试卷列表 */
    getList() {
      this.loading = true
      listPaper(this.queryParams).then(response => {
        this.paperList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        paperId: null,
        libraryId: null,
        paperName: null,
        paperDesc: null,
        totalScore: null,
        createTime: null,
        updateTime: null,
        content: null
      }
      this.resetForm("form")
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.paperId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加试卷，一个试卷库包含多个试卷"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const paperId = row.paperId || this.ids
      getPaper(paperId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改试卷，一个试卷库包含多个试卷"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.paperId != null) {
            updatePaper(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addPaper(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const paperIds = row.paperId || this.ids
      this.$modal.confirm('是否确认删除试卷，一个试卷库包含多个试卷编号为"' + paperIds + '"的数据项？').then(function() {
        return delPaper(paperIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/paper/export', {
        ...this.queryParams
      }, `paper_${new Date().getTime()}.xlsx`)
    },
    handleViewQuestions(row) {
      this.currentPaper = row
      try {
        // 解析JSON格式的题目内容
        if (row.content && typeof row.content === 'string') {
          this.questionList = JSON.parse(row.content)
        } else if (Array.isArray(row.content)) {
          this.questionList = row.content
        } else {
          this.questionList = []
        }
        
        // 处理每个题目的选项数据
        this.questionList.forEach(question => {
          // 如果options是字符串，尝试解析为数组
          if (question.options && typeof question.options === 'string') {
            try {
              // 先尝试JSON.parse解析
              const parsed = JSON.parse(question.options)
              if (Array.isArray(parsed)) {
                question.options = parsed
              } else {
                // 如果不是数组，按换行符分割
                question.options = question.options.split('\n').map(opt => opt.trim()).filter(opt => opt)
              }
            } catch (e) {
              // 如果JSON解析失败，按换行符分割
              question.options = question.options.split('\n').map(opt => opt.trim()).filter(opt => opt)
            }
          }
          // 确保options是数组
          if (!Array.isArray(question.options)) {
            question.options = []
          }
        })
      } catch (error) {
        console.error('解析题目内容失败:', error)
        this.$modal.msgError('解析题目内容失败')
        this.questionList = []
      }
      this.questionDetailVisible = true
    }
  }
}
</script>

<style scoped>
.course-info-card {
  margin-bottom: 20px;
}

.course-info {
  padding: 10px 0;
}

.info-item {
  display: flex;
  align-items: center;
  justify-content: center;
}

.info-item .label {
  font-weight: bold;
  color: #606266;
  margin-right: 8px;
}

.info-item .value {
  color: #409EFF;
  font-weight: 500;
}

.question-detail-container {
  padding: 20px 0;
}

.paper-info {
  text-align: center;
  margin-bottom: 30px;
  padding: 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border-radius: 8px;
}

.paper-info h2 {
  margin: 0 0 10px 0;
  font-size: 24px;
  font-weight: bold;
}

.paper-desc {
  margin: 10px 0;
  font-size: 14px;
  opacity: 0.9;
}

.paper-stats {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin-top: 15px;
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
</style>
