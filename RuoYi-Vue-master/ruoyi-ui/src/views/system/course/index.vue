<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="课程名称" prop="courseName">
        <el-input
          v-model="queryParams.courseName"
          placeholder="请输入课程名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="课程编号" prop="courseCode">
        <el-input
          v-model="queryParams.courseCode"
          placeholder="请输入课程编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="课程分类" prop="courseCategory">
        <el-input
          v-model="queryParams.courseCategory"
          placeholder="请输入课程分类"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="授课教师ID" prop="teacherId">
        <el-input
          v-model="queryParams.teacherId"
          placeholder="请输入授课教师ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="课程学分" prop="credit">
        <el-input
          v-model="queryParams.credit"
          placeholder="请输入课程学分"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="课程学时" prop="hours">
        <el-input
          v-model="queryParams.hours"
          placeholder="请输入课程学时"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>

      <el-form-item label="课程的图片" prop="courseImg">
        <el-input
          v-model="queryParams.courseImg"
          placeholder="请输入课程的图片"
          clearable
          @keyup.enter.native="handleQuery"
        />

      </el-form-item>

      <el-form-item label="课程状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择课程状态" clearable>
          <el-option
            v-for="dict in dict.type.char"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>

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
          v-hasPermi="['system:course:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:course:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>


    <el-table v-loading="loading" :data="courseList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="课程ID" align="center" prop="courseId" />
      <el-table-column label="课程名称" align="center" prop="courseName" />
      <el-table-column label="课程编号" align="center" prop="courseCode" />
      <el-table-column label="课程分类" align="center" prop="courseCategory" />
      <el-table-column label="授课教师ID" align="center" prop="teacherId" />
      <el-table-column label="课程学分" align="center" prop="credit" />
      <el-table-column label="课程学时" align="center" prop="hours" />
      <el-table-column label="课程描述" align="center" prop="courseDesc" />
      <el-table-column label="课程状态" align="center" prop="status" />
      <el-table-column label="课程的图片" align="center" prop="courseImg" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:course:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:course:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <el-row :gutter="20" v-loading="loading">
      <el-col :md="6" :lg="6" :xl="6" v-for="course in courseList" :key="course.courseId" style="margin-bottom: 20px;">
        <el-card :body-style="{ padding: '0px' }" class="course-card" @click.native="handleCourseClick(course)">
          <el-image :src="baseUrl + course.courseImg" lazy class="course-image" fit="cover">
            <div slot="error" class="image-slot">
              <i class="el-icon-picture-outline"></i>
            </div>
          </el-image>
          <div style="padding: 14px;">
            <div class="course-body">
              <h4 class="course-name">{{ course.courseName }}</h4>
              <p class="course-category">{{ course.courseCategory }}</p>
              <p class="course-desc">{{ course.courseDesc }}</p>
            </div>
            <div class="bottom clearfix">
              <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(course)" v-hasPermi="['system:course:edit']">修改</el-button>
              <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(course)" v-hasPermi="['system:course:remove']">删除</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <div v-if="courseList.length === 0 && !loading" style="text-align: center; color: #909399;">
      暂无课程
    </div>


    <el-row :gutter="20" v-loading="loading">
      <el-col :md="6" :lg="6" :xl="6" v-for="course in courseList" :key="course.courseId" style="margin-bottom: 20px;">
        <el-card :body-style="{ padding: '0px' }" class="course-card" @click.native="handleCourseClick(course)">
          <el-image :src="baseUrl + course.courseImg" lazy class="course-image" fit="cover">
            <div slot="error" class="image-slot">
              <i class="el-icon-picture-outline"></i>
            </div>
          </el-image>
          <div style="padding: 14px;">
            <div class="course-body">
              <h4 class="course-name">{{ course.courseName }}</h4>
              <p class="course-category">{{ course.courseCategory }}</p>
              <p class="course-desc">{{ course.courseDesc }}</p>
            </div>
            <div class="bottom clearfix">
              <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(course)" v-hasPermi="['system:course:edit']">修改</el-button>
              <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(course)" v-hasPermi="['system:course:remove']">删除</el-button>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
    <div v-if="courseList.length === 0 && !loading" style="text-align: center; color: #909399;">
      暂无课程
    </div>


    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改课程信息对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="课程名称" prop="courseName">
          <el-input v-model="form.courseName" placeholder="请输入课程名称" />
        </el-form-item>
        <el-form-item label="课程编号" prop="courseCode">
          <el-input v-model="form.courseCode" placeholder="请输入课程编号" />
        </el-form-item>
        <el-form-item label="课程分类" prop="courseCategory">
          <el-input v-model="form.courseCategory" placeholder="请输入课程分类" />
        </el-form-item>
        <el-form-item label="授课教师ID" prop="teacherId">
          <el-input v-model="form.teacherId" placeholder="请输入授课教师ID" />
        </el-form-item>
        <el-form-item label="课程学分" prop="credit">
          <el-input v-model="form.credit" placeholder="请输入课程学分" />
        </el-form-item>
        <el-form-item label="课程学时" prop="hours">
          <el-input v-model="form.hours" placeholder="请输入课程学时" />
        </el-form-item>
        <el-form-item label="课程描述" prop="courseDesc">
          <el-input v-model="form.courseDesc" type="textarea" placeholder="请输入内容" />
        </el-form-item>


        <el-form-item label="课程的图片" prop="courseImg">
          <el-input v-model="form.courseImg" placeholder="请输入课程的图片" />
        </el-form-item>

        <el-form-item label="课程的图片" prop="courseImg">
          <el-input v-model="form.courseImg" placeholder="请输入课程的图片" />

        </el-form-item>
        <el-form-item label="课程状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio
              v-for="dict in dict.type.char"
              :key="dict.value"
              :label="dict.value"
            >{{dict.label}}</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="课程的图片" prop="courseImg">
          <image-upload v-model="form.courseImg" :action="uploadUrl" :headers="uploadHeaders" />

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
import { listCourse, getCourse, delCourse, addCourse, updateCourse } from "@/api/system/course"
import { getToken } from "@/utils/auth";

export default {
  name: "Course",
  dicts: ['char'],
  data() {
    return {
      // 遮罩层
      loading: true,
      baseUrl: process.env.VUE_APP_BASE_API,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 课程信息表格数据
      courseList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 图片上传地址
      uploadUrl: "/system/course/upload",
      // 上传请求头
      uploadHeaders: {
        Authorization: "Bearer " + getToken()
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        courseName: null,
        courseCode: null,
        courseCategory: null,
        teacherId: null,
        credit: null,
        hours: null,
        courseDesc: null,
        status: null,
        courseImg: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        courseName: [
          { required: true, message: "课程名称不能为空", trigger: "blur" }
        ],
        courseCode: [
          { required: true, message: "课程编号不能为空", trigger: "blur" }
        ],
        courseCategory: [
          { required: true, message: "课程分类不能为空", trigger: "blur" }
        ],
        teacherId: [
          { required: true, message: "授课教师ID不能为空", trigger: "blur" }
        ],
        credit: [
          { required: true, message: "课程学分不能为空", trigger: "blur" }
        ],
        hours: [
          { required: true, message: "课程学时不能为空", trigger: "blur" }
        ],
        createTime: [
          { required: true, message: "创建时间不能为空", trigger: "blur" }
        ],
        courseImg: [
          { required: true, message: "课程的图片不能为空", trigger: "blur" }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询课程信息列表 */
    getList() {
      this.loading = true
      listCourse(this.queryParams).then(response => {
        this.courseList = response.rows
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
        courseId: null,
        courseName: null,
        courseCode: null,
        courseCategory: null,
        teacherId: null,
        credit: null,
        hours: null,
        courseDesc: null,
        createTime: null,
        updateTime: null,
        status: null,
        courseImg: null
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
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加课程信息"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const courseId = row.courseId
      getCourse(courseId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改课程信息"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.courseId != null) {
            updateCourse(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addCourse(this.form).then(response => {
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
      const courseIds = row.courseId
      this.$modal.confirm('是否确认删除课程名称为"' + row.courseName + '"的数据项？').then(function() {
        return delCourse(courseIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/course/export', {
        ...this.queryParams
      }, `course_${new Date().getTime()}.xlsx`)
    },
    /** 跳转到课程详情页 */
    handleCourseClick(course) {
      this.$router.push({ path: '/system/course-detail/' + course.courseId });
    }
  }
}
</script>

<style scoped>
.course-card {
  cursor: pointer;
}

.course-image {
  width: 100%;
  height: 160px;
  display: block;
}

.image-slot {
  display: flex;
  justify-content: center;
  align-items: center;
  width: 100%;
  height: 100%;
  background: #f5f7fa;
  color: #909399;
  font-size: 30px;
}

.course-body {
  height: 110px; /* Fixed height for the text content area */
  overflow: hidden;
}

.course-name {
  margin-top: 0;
  margin-bottom: 8px;
  font-size: 16px;
  font-weight: 500;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.course-category {
  font-size: 13px;
  color: #999;
  margin-bottom: 10px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
}

.course-desc {
  font-size: 13px;
  color: #666;
  line-height: 1.5;
  /* Multi-line clamp */
  overflow: hidden;
  text-overflow: ellipsis;
  display: -webkit-box;
  -webkit-line-clamp: 2; /* Limit to 2 lines */
  -webkit-box-orient: vertical;
}

.bottom {
  margin-top: 13px;
  line-height: 12px;
  display: flex;
  justify-content: flex-end;
  align-items: center;
}

.clearfix:before,
.clearfix:after {
    display: table;
    content: "";
}

.clearfix:after {
    clear: both
}
</style>
