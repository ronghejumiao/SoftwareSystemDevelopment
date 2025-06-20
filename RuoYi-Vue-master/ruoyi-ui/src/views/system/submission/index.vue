<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="学习记录ID，关联learning_record表" prop="recordId">
        <el-input
          v-model="queryParams.recordId"
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
      <el-form-item label="提交用户ID，关联sys_user表" prop="userId">
        <el-input
          v-model="queryParams.userId"
          placeholder="请输入提交用户ID，关联sys_user表"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="提交时间" prop="submissionTime">
        <el-date-picker clearable
          v-model="queryParams.submissionTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择提交时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="是否评分" prop="isGraded">
        <el-input
          v-model="queryParams.isGraded"
          placeholder="请输入是否评分"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="评分分数" prop="gradeScore">
        <el-input
          v-model="queryParams.gradeScore"
          placeholder="请输入评分分数"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="评分人ID，关联sys_user表" prop="graderId">
        <el-input
          v-model="queryParams.graderId"
          placeholder="请输入评分人ID，关联sys_user表"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="评分时间" prop="gradeTime">
        <el-date-picker clearable
          v-model="queryParams.gradeTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择评分时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="租户ID" prop="tenantId">
        <el-input
          v-model="queryParams.tenantId"
          placeholder="请输入租户ID"
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
          v-hasPermi="['system:submission:add']"
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
          v-hasPermi="['system:submission:edit']"
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
          v-hasPermi="['system:submission:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:submission:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="submissionList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="提交ID，主键，自增" align="center" prop="submissionId" />
      <el-table-column label="学习记录ID，关联learning_record表" align="center" prop="recordId" />
      <el-table-column label="任务ID，关联learning_task表" align="center" prop="taskId" />
      <el-table-column label="提交用户ID，关联sys_user表" align="center" prop="userId" />
      <el-table-column label="提交内容" align="center" prop="submissionContent" />
      <el-table-column label="提交文件路径" align="center" prop="submissionFile" />
      <el-table-column label="提交时间" align="center" prop="submissionTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.submissionTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="是否评分" align="center" prop="isGraded" />
      <el-table-column label="评分分数" align="center" prop="gradeScore" />
      <el-table-column label="评分评语" align="center" prop="gradeComment" />
      <el-table-column label="评分人ID，关联sys_user表" align="center" prop="graderId" />
      <el-table-column label="评分时间" align="center" prop="gradeTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.gradeTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="租户ID" align="center" prop="tenantId" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:submission:edit']"
          >修改</el-button>
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

    <!-- 添加或修改任务提交记录，记录学生提交任务的信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="学习记录ID，关联learning_record表" prop="recordId">
          <el-input v-model="form.recordId" placeholder="请输入学习记录ID，关联learning_record表" />
        </el-form-item>
        <el-form-item label="任务ID，关联learning_task表" prop="taskId">
          <el-input v-model="form.taskId" placeholder="请输入任务ID，关联learning_task表" />
        </el-form-item>
        <el-form-item label="提交用户ID，关联sys_user表" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入提交用户ID，关联sys_user表" />
        </el-form-item>
        <el-form-item label="提交内容">
          <editor v-model="form.submissionContent" :min-height="192"/>
        </el-form-item>
        <el-form-item label="提交文件路径" prop="submissionFile">
          <file-upload v-model="form.submissionFile"/>
        </el-form-item>
        <el-form-item label="提交时间" prop="submissionTime">
          <el-date-picker clearable
            v-model="form.submissionTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择提交时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="是否评分" prop="isGraded">
          <el-input v-model="form.isGraded" placeholder="请输入是否评分" />
        </el-form-item>
        <el-form-item label="评分分数" prop="gradeScore">
          <el-input v-model="form.gradeScore" placeholder="请输入评分分数" />
        </el-form-item>
        <el-form-item label="评分评语" prop="gradeComment">
          <el-input v-model="form.gradeComment" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="评分人ID，关联sys_user表" prop="graderId">
          <el-input v-model="form.graderId" placeholder="请输入评分人ID，关联sys_user表" />
        </el-form-item>
        <el-form-item label="评分时间" prop="gradeTime">
          <el-date-picker clearable
            v-model="form.gradeTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择评分时间">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="删除标志" prop="delFlag">
          <el-input v-model="form.delFlag" placeholder="请输入删除标志" />
        </el-form-item>
        <el-form-item label="租户ID" prop="tenantId">
          <el-input v-model="form.tenantId" placeholder="请输入租户ID" />
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
import { listSubmission, getSubmission, delSubmission, addSubmission, updateSubmission } from "@/api/system/submission"

export default {
  name: "Submission",
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
      // 任务提交记录，记录学生提交任务的信息表格数据
      submissionList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        recordId: null,
        taskId: null,
        userId: null,
        submissionContent: null,
        submissionFile: null,
        submissionTime: null,
        isGraded: null,
        gradeScore: null,
        gradeComment: null,
        graderId: null,
        gradeTime: null,
        status: null,
        tenantId: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        recordId: [
          { required: true, message: "学习记录ID，关联learning_record表不能为空", trigger: "blur" }
        ],
        taskId: [
          { required: true, message: "任务ID，关联learning_task表不能为空", trigger: "blur" }
        ],
        userId: [
          { required: true, message: "提交用户ID，关联sys_user表不能为空", trigger: "blur" }
        ],
        submissionTime: [
          { required: true, message: "提交时间不能为空", trigger: "blur" }
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
    /** 查询任务提交记录，记录学生提交任务的信息列表 */
    getList() {
      this.loading = true
      listSubmission(this.queryParams).then(response => {
        this.submissionList = response.rows
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
        submissionId: null,
        recordId: null,
        taskId: null,
        userId: null,
        submissionContent: null,
        submissionFile: null,
        submissionTime: null,
        isGraded: null,
        gradeScore: null,
        gradeComment: null,
        graderId: null,
        gradeTime: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        status: null,
        remark: null,
        delFlag: null,
        tenantId: null
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
      this.ids = selection.map(item => item.submissionId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加任务提交记录，记录学生提交任务的信息"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const submissionId = row.submissionId || this.ids
      getSubmission(submissionId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改任务提交记录，记录学生提交任务的信息"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.submissionId != null) {
            updateSubmission(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addSubmission(this.form).then(response => {
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
      const submissionIds = row.submissionId || this.ids
      this.$modal.confirm('是否确认删除任务提交记录，记录学生提交任务的信息编号为"' + submissionIds + '"的数据项？').then(function() {
        return delSubmission(submissionIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/submission/export', {
        ...this.queryParams
      }, `submission_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
