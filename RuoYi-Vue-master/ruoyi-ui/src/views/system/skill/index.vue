<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="学生ID，关联sys_user表" prop="studentId">
        <el-input
          v-model="queryParams.studentId"
          placeholder="请输入学生ID，关联sys_user表"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="能力要求ID，关联course_skill_requirement表" prop="requirementId">
        <el-input
          v-model="queryParams.requirementId"
          placeholder="请输入能力要求ID，关联course_skill_requirement表"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="能力评分" prop="skillScore">
        <el-input
          v-model="queryParams.skillScore"
          placeholder="请输入能力评分"
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
          v-hasPermi="['system:skill:add']"
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
          v-hasPermi="['system:skill:edit']"
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
          v-hasPermi="['system:skill:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:skill:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="skillList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID，主键，自增" align="center" prop="id" />
      <el-table-column label="学生ID，关联sys_user表" align="center" prop="studentId" />
      <el-table-column label="能力要求ID，关联course_skill_requirement表" align="center" prop="requirementId" />
      <el-table-column label="能力评分" align="center" prop="skillScore" />
      <el-table-column label="评分更新原因" align="center" prop="updateReason" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:skill:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:skill:remove']"
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

    <!-- 添加或修改学生能力，基于课程能力要求构建对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="学生ID，关联sys_user表" prop="studentId">
          <el-input v-model="form.studentId" placeholder="请输入学生ID，关联sys_user表" />
        </el-form-item>
        <el-form-item label="能力要求ID，关联course_skill_requirement表" prop="requirementId">
          <el-input v-model="form.requirementId" placeholder="请输入能力要求ID，关联course_skill_requirement表" />
        </el-form-item>
        <el-form-item label="能力评分" prop="skillScore">
          <el-input v-model="form.skillScore" placeholder="请输入能力评分" />
        </el-form-item>
        <el-form-item label="评分更新原因" prop="updateReason">
          <el-input v-model="form.updateReason" type="textarea" placeholder="请输入内容" />
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
import { listSkill, getSkill, delSkill, addSkill, updateSkill } from "@/api/system/skill"

export default {
  name: "Skill",
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
      // 学生能力，基于课程能力要求构建表格数据
      skillList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        studentId: null,
        requirementId: null,
        skillScore: null,
        updateReason: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        studentId: [
          { required: true, message: "学生ID，关联sys_user表不能为空", trigger: "blur" }
        ],
        requirementId: [
          { required: true, message: "能力要求ID，关联course_skill_requirement表不能为空", trigger: "blur" }
        ],
        skillScore: [
          { required: true, message: "能力评分不能为空", trigger: "blur" }
        ],
        updateTime: [
          { required: true, message: "最后更新时间不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询学生能力，基于课程能力要求构建列表 */
    getList() {
      this.loading = true
      listSkill(this.queryParams).then(response => {
        this.skillList = response.rows
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
        id: null,
        studentId: null,
        requirementId: null,
        skillScore: null,
        updateTime: null,
        updateReason: null
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
      this.ids = selection.map(item => item.id)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加学生能力，基于课程能力要求构建"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getSkill(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改学生能力，基于课程能力要求构建"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateSkill(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addSkill(this.form).then(response => {
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
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除学生能力，基于课程能力要求构建编号为"' + ids + '"的数据项？').then(function() {
        return delSkill(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/skill/export', {
        ...this.queryParams
      }, `skill_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
