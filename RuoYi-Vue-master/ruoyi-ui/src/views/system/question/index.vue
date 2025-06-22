<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="题库ID，关联question_bank表" prop="bankId">
        <el-input
          v-model="queryParams.bankId"
          placeholder="请输入题库ID，关联question_bank表"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="知识点ID，关联knowledge_node表" prop="knowledgeNodeId">
        <el-input
          v-model="queryParams.knowledgeNodeId"
          placeholder="请输入知识点ID，关联knowledge_node表"
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
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="questionList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="题目ID，主键，自增" align="center" prop="questionId" />
      <el-table-column label="题库ID，关联question_bank表" align="center" prop="bankId" />
      <el-table-column label="知识点ID，关联knowledge_node表" align="center" prop="knowledgeNodeId" />
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
      @pagination="getList"
    />

    <!-- 添加或修改题目，存储题库中的题目信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="题库ID，关联question_bank表" prop="bankId">
          <el-input v-model="form.bankId" placeholder="请输入题库ID，关联question_bank表" />
        </el-form-item>
        <el-form-item label="知识点ID，关联knowledge_node表" prop="knowledgeNodeId">
          <el-input v-model="form.knowledgeNodeId" placeholder="请输入知识点ID，关联knowledge_node表" />
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
  </div>
</template>

<script>
import { listQuestion, getQuestion, delQuestion, addQuestion, updateQuestion } from "@/api/system/question"

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
      }
    }
  },
  created() {


    // 临时设置courseId，后续可以从路由参数获取
    this.courseId = this.$route.query.courseId || this.$route.params.courseId || 1 // 临时设置为1，实际应该从课程模块获取

    // 根据courseId查询题目
    this.getQuestionsByCourse()

    // 初始化随机组卷配置
    this.calculateQuestions()

    this.getList()


    this.getList()


  },
  methods: {
    /** 查询题目，存储题库中的题目信息列表 */
    getList() {
      this.loading = true
      listQuestion(this.queryParams).then(response => {
        this.questionList = response.rows
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
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
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
              this.getList()
            })
          } else {
            addQuestion(this.form).then(response => {
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
      const questionIds = row.questionId || this.ids
      this.$modal.confirm('是否确认删除题目，存储题库中的题目信息编号为"' + questionIds + '"的数据项？').then(function() {
        return delQuestion(questionIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/question/export', {
        ...this.queryParams
      }, `question_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
