<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="课程编号" prop="courseCode">
        <el-input
          v-model="queryParams.courseCode"
          placeholder="请输入课程编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="能力名称" prop="skillName">
        <el-input
          v-model="queryParams.skillName"
          placeholder="请输入能力名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="课程描述" prop="description">
        <el-input
          v-model="queryParams.description"
          placeholder="请输入课程描述"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="课堂要求描述" prop="requiredText">
        <el-input
          v-model="queryParams.requiredText"
          placeholder="请输入课堂要求描述"
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
          v-hasPermi="['system:requirement:add']"
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
          v-hasPermi="['system:requirement:edit']"
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
          v-hasPermi="['system:requirement:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:requirement:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="requirementList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="要求ID" align="center" prop="requirementId" />
      <el-table-column label="课程编号" align="center" prop="courseCode" />
      <el-table-column label="能力名称" align="center" prop="skillName" />
      <el-table-column label="课程描述" align="center" prop="description" />
      <el-table-column label="课堂要求描述" align="center" prop="requiredText" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:requirement:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:requirement:remove']"
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

    <!-- 添加或修改课程能力要求对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="课程编号" prop="courseCode">
          <el-input v-model="form.courseCode" placeholder="请输入课程编号" />
        </el-form-item>
        <el-form-item label="能力名称" prop="skillName">
          <el-input v-model="form.skillName" placeholder="请输入能力名称" />
        </el-form-item>
        <el-form-item label="课程描述" prop="description">
          <el-input v-model="form.description" placeholder="请输入课程描述" />
        </el-form-item>
        <el-form-item label="课堂要求描述" prop="requiredText">
          <el-input v-model="form.requiredText" placeholder="请输入课堂要求描述" />
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
import { listRequirement, getRequirement, delRequirement, addRequirement, updateRequirement } from "@/api/system/requirement"
import { listCourse, getCourse } from "@/api/system/course"

export default {
  name: "Requirement",
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
      // 课程能力要求表格数据
      requirementList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        courseCode: null,
        skillName: null,
        description: null,
        requiredText: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        courseCode: [
          { required: true, message: "课程编号不能为空", trigger: "blur" }
        ],
        skillName: [
          { required: true, message: "能力名称不能为空", trigger: "blur" }
        ],
        description: [
          { required: true, message: "课程描述不能为空", trigger: "blur" }
        ],
        requiredText: [
          { required: true, message: "课堂要求描述不能为空", trigger: "blur" }
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
    /** 查询课程能力要求列表 */
    async getList() {
      this.loading = true
      let params = { ...this.queryParams };
      // 通过courseCode查ID
      if (params.courseCode) {
        const res = await listCourse({ courseCode: params.courseCode, pageNum: 1, pageSize: 1 });
        if (res.rows && res.rows.length > 0) {
          params.courseId = res.rows[0].courseId;
        } else {
          params.courseId = -1; // 查不到时查一个不会有结果的ID
        }
      }
      listRequirement(params).then(async response => {
        const list = response.rows || [];
        // 批量查找所有courseId对应的courseCode
        const idSet = Array.from(new Set(list.map(item => item.courseId).filter(Boolean)));
        let idToCode = {};
        if (idSet.length > 0) {
          // 批量查找所有课程信息
          const courseRes = await listCourse({ pageNum: 1, pageSize: 9999 });
          if (courseRes.rows) {
            courseRes.rows.forEach(c => {
              idToCode[c.courseId] = c.courseCode;
            });
          }
        }
        // 为每条数据补充courseCode
        list.forEach(item => {
          item.courseCode = idToCode[item.courseId] || '';
        });
        this.requirementList = list;
        this.total = response.total;
        this.loading = false;
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
        requirementId: null,
        courseCode: null,
        skillName: null,
        description: null,
        requiredText: null,
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
      this.ids = selection.map(item => item.requirementId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加课程能力要求"
    },
    /** 修改按钮操作 */
    async handleUpdate(row) {
      this.reset()
      // 通过courseCode查ID
      const code = row.courseCode || this.form.courseCode;
      const res = await listCourse({ courseCode: code, pageNum: 1, pageSize: 1 });
      if (res.rows && res.rows.length > 0) {
        const courseId = res.rows[0].courseId;
        getRequirement(row.requirementId).then(response => {
          this.form = response.data
          this.form.courseCode = code;
          this.open = true
          this.title = "修改课程能力要求"
        })
      } else {
        this.$modal.msgError("未找到该课程编号对应的课程");
      }
    },
    /** 提交按钮 */
    async submitForm() {
      this.$refs["form"].validate(async valid => {
        if (valid) {
          // 通过courseCode查ID
          let courseId = null;
          if (this.form.courseCode) {
            const res = await listCourse({ courseCode: this.form.courseCode, pageNum: 1, pageSize: 1 });
            if (res.rows && res.rows.length > 0) {
              courseId = res.rows[0].courseId;
            }
          }
          if (courseId) {
            this.form.courseId = courseId;
          }
          if (this.form.requirementId != null) {
            updateRequirement(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addRequirement(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    async handleDelete(row) {
      // 通过courseCode查ID
      const code = row.courseCode;
      const res = await listCourse({ courseCode: code, pageNum: 1, pageSize: 1 });
      if (res.rows && res.rows.length > 0) {
        const courseId = res.rows[0].courseId;
        this.$modal.confirm('是否确认删除课程编号为"' + code + '"的数据项？').then(function() {
          return delRequirement(row.requirementId)
        }).then(() => {
          this.getList()
          this.$modal.msgSuccess("删除成功")
        }).catch(() => {})
      } else {
        this.$modal.msgError("未找到该课程编号对应的课程");
      }
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/requirement/export', {
        ...this.queryParams
      }, `requirement_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
