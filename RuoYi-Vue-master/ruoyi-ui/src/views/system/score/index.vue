<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="学习记录ID，关联learning_record表" prop="learningRecordId">
        <el-input
          v-model="queryParams.learningRecordId"
          placeholder="请输入学习记录ID，关联learning_record表"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="任务ID，关联learning_task表" prop="taskId">
        <el-input
          v-model="queryParams.taskId"
          placeholder="请输入任务ID，关联learning_task表"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="试卷ID，关联test_paper表" prop="paperId">
        <el-input
          v-model="queryParams.paperId"
          placeholder="请输入试卷ID，关联test_paper表"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="得分" prop="score">
        <el-input
          v-model="queryParams.score"
          placeholder="请输入得分"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="提交时间" prop="submitTime">
        <el-date-picker clearable
          v-model="queryParams.submitTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择提交时间">
        </el-date-picker>
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
          v-hasPermi="['system:score:add']"
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
          v-hasPermi="['system:score:edit']"
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
          v-hasPermi="['system:score:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:score:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="scoreList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="成绩ID，主键，自增" align="center" prop="scoreId" />
      <el-table-column label="学习记录ID，关联learning_record表" align="center" prop="learningRecordId" />
      <el-table-column label="任务ID，关联learning_task表" align="center" prop="taskId" />
      <el-table-column label="试卷ID，关联test_paper表" align="center" prop="paperId" />
      <el-table-column label="得分" align="center" prop="score" />
      <el-table-column label="成绩描述" align="center" prop="scoreDesc" />
      <el-table-column label="提交时间" align="center" prop="submitTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.submitTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:score:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:score:remove']"
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

    <!-- 添加或修改成绩，记录学生的学习成绩信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="学习记录ID，关联learning_record表" prop="learningRecordId">
          <el-input v-model="form.learningRecordId" placeholder="请输入学习记录ID，关联learning_record表" />
        </el-form-item>
        <el-form-item label="任务ID，关联learning_task表" prop="taskId">
          <el-input v-model="form.taskId" placeholder="请输入任务ID，关联learning_task表" />
        </el-form-item>
        <el-form-item label="试卷ID，关联test_paper表" prop="paperId">
          <el-input v-model="form.paperId" placeholder="请输入试卷ID，关联test_paper表" />
        </el-form-item>
        <el-form-item label="得分" prop="score">
          <el-input v-model="form.score" placeholder="请输入得分" />
        </el-form-item>
        <el-form-item label="成绩描述" prop="scoreDesc">
          <el-input v-model="form.scoreDesc" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="提交时间" prop="submitTime">
          <el-date-picker clearable
            v-model="form.submitTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择提交时间">
          </el-date-picker>
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
import { listScore, getScore, delScore, addScore, updateScore } from "@/api/system/score"

export default {
  name: "Score",
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
      // 成绩，记录学生的学习成绩信息表格数据
      scoreList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        learningRecordId: null,
        taskId: null,
        paperId: null,
        score: null,
        scoreDesc: null,
        submitTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        learningRecordId: [
          { required: true, message: "学习记录ID，关联learning_record表不能为空", trigger: "blur" }
        ],
        createTime: [
          { required: true, message: "创建时间不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询成绩，记录学生的学习成绩信息列表 */
    getList() {
      this.loading = true
      listScore(this.queryParams).then(response => {
        this.scoreList = response.rows
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
        scoreId: null,
        learningRecordId: null,
        taskId: null,
        paperId: null,
        score: null,
        scoreDesc: null,
        submitTime: null,
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
      this.ids = selection.map(item => item.scoreId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加成绩，记录学生的学习成绩信息"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const scoreId = row.scoreId || this.ids
      getScore(scoreId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改成绩，记录学生的学习成绩信息"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.scoreId != null) {
            updateScore(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addScore(this.form).then(response => {
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
      const scoreIds = row.scoreId || this.ids
      this.$modal.confirm('是否确认删除成绩，记录学生的学习成绩信息编号为"' + scoreIds + '"的数据项？').then(function() {
        return delScore(scoreIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/score/export', {
        ...this.queryParams
      }, `score_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
