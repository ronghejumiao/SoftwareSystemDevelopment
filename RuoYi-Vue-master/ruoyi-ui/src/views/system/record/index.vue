<template>
  <div class="app-container">
    <!-- 统计卡片区域 -->
    <el-row :gutter="20" class="mb20">
      <el-col :span="8">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <i class="el-icon-s-data" style="color: #409EFF;"></i>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ total }}</div>
              <div class="stat-label">学习记录总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <i class="el-icon-date" style="color: #67C23A;"></i>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ todayCount }}</div>
              <div class="stat-label">今日新增</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8" v-if="isTeacherOrAdmin">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <i class="el-icon-user" style="color: #E6A23C;"></i>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ uniqueUserCount }}</div>
              <div class="stat-label">学生总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 搜索表单区域 -->
    <el-card class="search-card" shadow="never">
      <div slot="header" class="search-header">
        <span class="search-title">
          <i class="el-icon-search"></i>
          搜索条件
        </span>
        <el-button type="text" @click="toggleSearch">
          {{ showSearch ? '收起' : '展开' }}
          <i :class="showSearch ? 'el-icon-arrow-up' : 'el-icon-arrow-down'"></i>
        </el-button>
      </div>
      <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px" class="search-form">
        <el-row :gutter="20">
          <el-col :span="8" v-if="isTeacherOrAdmin">
            <el-form-item label="用户名" prop="userName">
              <el-input
                v-model="queryParams.userName"
                placeholder="请输入用户名"
                clearable
                @keyup.enter.native="handleQuery"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="课程代码" prop="courseCode">
              <el-input
                v-model="queryParams.courseCode"
                placeholder="请输入课程代码"
                clearable
                @keyup.enter.native="handleQuery"
              />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="记录时间" prop="joinTime">
              <el-date-picker clearable
                v-model="queryParams.joinTime"
                type="date"
                value-format="yyyy-MM-dd"
                placeholder="请选择时间"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20" justify="end">
          <el-col :span="8">
          </el-col>
          <el-col :span="8" :offset="8" style="text-align: right;">
            <el-form-item>
              <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
              <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
    </el-card>

    <!-- 操作按钮区域 -->
    <el-card class="operation-card" shadow="never">
      <el-row :gutter="10" class="mb8">
        <el-col :span="1.5" v-if="isTeacherOrAdmin">
          <el-button
            type="primary"
            plain
            icon="el-icon-plus"
            size="mini"
            @click="handleAdd"
          >新增</el-button>
        </el-col>
        <el-col :span="1.5" v-if="isTeacherOrAdmin">
          <el-button
            type="success"
            plain
            icon="el-icon-edit"
            size="mini"
            :disabled="single"
            @click="handleUpdate"
          >修改</el-button>
        </el-col>
        <el-col :span="1.5" v-if="isTeacherOrAdmin">
          <el-button
            type="danger"
            plain
            icon="el-icon-delete"
            size="mini"
            :disabled="multiple"
            @click="handleDelete"
          >删除</el-button>
        </el-col>
        <el-col :span="1.5">
          <el-button
            type="warning"
            plain
            icon="el-icon-download"
            size="mini"
            @click="handleExport"
          >导出</el-button>
        </el-col>
        <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
      </el-row>
    </el-card>

    <!-- 数据表格区域 -->
    <el-card class="table-card" shadow="never">
      <el-table
        v-loading="loading"
        :data="learningRecordList"
        @selection-change="handleSelectionChange"
        stripe
        border
        highlight-current-row
        class="learning-record-table"
      >
        <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="记录ID" align="center" prop="recordId" width="80" v-if="$store.getters.roles.includes('admin')" />
        <el-table-column label="用户名" align="center" prop="userName" width="120" v-if="isTeacherOrAdmin" />
        <el-table-column label="真实姓名" align="center" prop="nickName" width="120" v-if="isTeacherOrAdmin" />
        <el-table-column label="课程ID" align="center" prop="courseId" width="100" />
        <el-table-column label="课程名称" align="center" prop="courseName" width="150" />
        <el-table-column label="课程代码" align="center" prop="courseCode" width="120" />
        <el-table-column label="记录时间" align="center" prop="joinTime" width="120">
          <template slot-scope="scope">
            <span>{{ parseTime(scope.row.joinTime, '{y}-{m}-{d}') }}</span>
          </template>
        </el-table-column>
        <el-table-column label="总成绩" align="center" prop="totalScore" width="100" />

        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="320">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="info"
              icon="el-icon-video-camera"
              @click="goToVideoLearningRecord(scope.row)"
            >视频学习详情</el-button>
            <el-button
              size="mini"
              type="info"
              icon="el-icon-s-flag"
              @click="goToScoreRecord(scope.row)"
            >成绩详情</el-button>
            <el-button
              size="mini"
              type="info"
              icon="el-icon-document"
              @click="goToSubmissionRecord(scope.row)"
            >任务提交详情</el-button>


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
    </el-card>

    <!-- 添加或修改学习记录对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="用户ID" prop="userId">
          <el-input v-model="form.userId" placeholder="请输入用户ID" />
        </el-form-item>
        <el-form-item label="课程ID" prop="courseId">
          <el-input v-model="form.courseId" placeholder="请输入课程ID" />
        </el-form-item>
        <el-form-item label="记录时间" prop="joinTime">
          <el-date-picker clearable
            v-model="form.joinTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择时间"
            style="width: 100%"
          />
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
import { listLearningRecord, getLearningRecord, delLearningRecord, addLearningRecord, updateLearningRecord } from "@/api/system/learningRecord"

export default {
  name: "LearningRecord",
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
      // 学习记录表格数据
      learningRecordList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        userId: null,
        courseId: null,
        joinTime: null,
        courseProgress: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        userId: [
          { required: true, message: "用户ID不能为空", trigger: "blur" }
        ],
        courseId: [
          { required: true, message: "课程ID不能为空", trigger: "blur" }
        ],
        joinTime: [
          { required: true, message: "选课时间不能为空", trigger: "blur" }
        ],
      }
    }
  },
  computed: {
    // 今日新增数量
    todayCount() {
      const today = new Date().toISOString().split('T')[0]
      return this.learningRecordList.filter(item => {
        const joinDate = this.parseTime(item.joinTime, '{y}-{m}-{d}')
        return joinDate === today
      }).length
    },
    // 用户总数
    uniqueUserCount() {
      const userSet = new Set(this.learningRecordList.map(item => item.userId))
      return userSet.size
    },
    isTeacherOrAdmin() {
      return this.$store.getters.roles.includes('admin') || this.$store.getters.roles.includes('teacher');
    },
    currentUserName() {
      return this.$store.getters.name;
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询学习记录列表 */
    getList() {
      if (!this.isTeacherOrAdmin) {
        this.queryParams.userName = this.currentUserName;
      }
      this.loading = true
      listLearningRecord(this.queryParams).then(response => {
        this.learningRecordList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 切换搜索显示
    toggleSearch() {
      this.showSearch = !this.showSearch
    },
    // 跳转到视频学习记录页面
    goToVideoLearningRecord(row) {
      this.$router.push({
        path: '/user/videos',
        query: { userId: row.userId, learningRecordId: row.recordId }
      })
    },
    // 跳转到成绩记录页面
    goToScoreRecord(row) {
      this.$router.push({
        path: '/user/scores',
        query: { userId: row.userId, learningRecordId: row.recordId }
      })
    },
    // 跳转到任务提交记录页面
    goToSubmissionRecord(row) {
      this.$router.push({
        path: '/user/tasks',
        query: { userId: row.userId, recordId: row.recordId }
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
        recordId: null,
        userId: null,
        courseId: null,
        joinTime: null,
        courseProgress: null
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
      this.ids = selection.map(item => item.recordId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加学习记录"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const recordId = row.recordId || this.ids
      getLearningRecord(recordId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改学习记录"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.recordId != null) {
            updateLearningRecord(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addLearningRecord(this.form).then(response => {
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
      const recordIds = row.recordId || this.ids
      this.$modal.confirm('是否确认删除学习记录编号为"' + recordIds + '"的数据项？').then(function() {
        return delLearningRecord(recordIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/record/export', {
        ...this.queryParams
      }, `learningRecord_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
