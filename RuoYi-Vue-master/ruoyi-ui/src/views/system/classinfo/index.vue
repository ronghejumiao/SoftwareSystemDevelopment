<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="班级名称" prop="className">
        <el-input
          v-model="queryParams.className"
          placeholder="请输入班级名称"
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
          v-hasPermi="['system:classinfo:add']"
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
          v-hasPermi="['system:classinfo:edit']"
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
          v-hasPermi="['system:classinfo:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:classinfo:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="classinfoList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="班级ID" align="center" prop="classId" />
      <el-table-column label="班级名称" align="center" prop="className" />
      <el-table-column label="课程ID" align="center" prop="courseId" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:classinfo:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:classinfo:remove']"
          >删除</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
            v-hasPermi="['system:classinfo:query']"
          >详情</el-button>
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

    <!-- 添加或修改班级信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="班级名称" prop="className">
          <el-input v-model="form.className" placeholder="请输入班级名称" />
        </el-form-item>
        <el-form-item label="课程id" prop="courseId">
          <el-input v-model="form.courseId" placeholder="请输入课程id" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 班级信息详细 -->
    <el-dialog :title="title" :visible.sync="detailOpen" width="700px" append-to-body>
      <el-form ref="form" :model="form" label-width="100px">
        <el-form-item label="班级名称：">{{ form.className }}</el-form-item>
      </el-form>
      <el-divider content-position="center">关联课程信息</el-divider>
      <div v-if="form.course">
        <el-form ref="courseForm" :model="form.course" label-width="100px">
          <el-row>
            <el-col :span="12">
              <el-form-item label="课程名称：">{{ form.course.courseName }}</el-form-item>
              <el-form-item label="学分：">{{ form.course.credit }}</el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="课程编码：">{{ form.course.courseCode }}</el-form-item>
              <el-form-item label="学时：">{{ form.course.hours }}</el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </div>
      <div v-else class="no-course-tip">
        <p>暂无关联课程信息</p>
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listClassinfo, getClassinfo, delClassinfo, addClassinfo, updateClassinfo, getDetail } from "@/api/system/classinfo"

export default {
  name: "Classinfo",
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
      // 班级信息，存储班级的基本信息表格数据
      classinfoList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        className: null,
        courseId: null
      },
      // 表单参数
      form: {
        course: {} // 初始化课程对象
      },
      // 表单校验
      rules: {
        className: [
          { required: true, message: "班级名称不能为空", trigger: "blur" }
        ],
        courseId: [
          { required: true, message: "课程ID，关联course表不能为空", trigger: "blur" }
        ],
        createTime: [
          { required: true, message: "创建时间不能为空", trigger: "blur" }
        ],
      },
      // 详情对话框
      detailOpen: false,
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询班级信息，存储班级的基本信息列表 */
    getList() {
      this.loading = true
      listClassinfo(this.queryParams).then(response => {
        this.classinfoList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    /**
     * 表单重置
     */
    reset() {
      this.form = {
        classId: null,
        className: null,
        courseId: null,
        createTime: null,
        updateTime: null,
        course: null // 重置时清空课程信息
      };
      this.resetForm("form");
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
      this.ids = selection.map(item => item.classId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加班级信息，存储班级的基本信息"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const classId = row.classId || this.ids
      getClassinfo(classId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改班级信息，存储班级的基本信息"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.classId != null) {
            updateClassinfo(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addClassinfo(this.form).then(response => {
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
      const classIds = row.classId || this.ids
      this.$modal.confirm('是否确认删除班级信息，存储班级的基本信息编号为"' + classIds + '"的数据项？').then(function() {
        return delClassinfo(classIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/classinfo/export', {
        ...this.queryParams
      }, `classinfo_${new Date().getTime()}.xlsx`)
    },
    /** 详细按钮操作 */
    handleDetail(row) {
      this.reset();
      const classId = row.classId || this.ids
      getDetail(classId).then(response => {
        this.form = response.data;
        this.detailOpen = true;
        this.title = "班级详情";
      });
    }
  }
}
</script>
