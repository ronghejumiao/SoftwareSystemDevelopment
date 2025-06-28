<template>
  <div class="app-container">
    <!-- 统计卡片区域 -->
    <el-row :gutter="20" class="mb20">
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <i class="el-icon-s-data" style="color: #409EFF;"></i>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ total }}</div>
              <div class="stat-label">总成绩记录</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <i class="el-icon-s-opportunity" style="color: #67C23A;"></i>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ validCount }}</div>
              <div class="stat-label">有效成绩</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <i class="el-icon-s-marketing" style="color: #E6A23C;"></i>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ averageScore }}</div>
              <div class="stat-label">平均分</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card" shadow="hover">
          <div class="stat-content">
            <div class="stat-icon">
              <i class="el-icon-s-flag" style="color: #F56C6C;"></i>
            </div>
            <div class="stat-info">
              <div class="stat-number">{{ todayCount }}</div>
              <div class="stat-label">今日新增</div>
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
          <el-col :span="8" v-if="!isStudent">
            <el-form-item label="成绩ID" prop="scoreId">
        <el-input
          v-model="queryParams.scoreId"
                placeholder="请输入成绩ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="学习记录ID" prop="learningRecordId">
        <el-input
          v-model="queryParams.learningRecordId"
                placeholder="请输入学习记录ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="任务ID" prop="taskId">
        <el-input
          v-model="queryParams.taskId"
                placeholder="请输入任务ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="8">
            <el-form-item label="试卷ID" prop="paperId">
        <el-input
          v-model="queryParams.paperId"
                placeholder="请输入试卷ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
          </el-col>
          <el-col :span="8" v-if="!isStudent">
            <el-form-item label="得分范围" prop="score">
              <el-input-number
          v-model="queryParams.score"
                :min="0"
                :max="100"
                placeholder="请输入得分"
                style="width: 100%"
        />
      </el-form-item>
          </el-col>
          <el-col :span="8" v-if="!isStudent">
            <el-form-item label="成绩状态" prop="scoreStatus">
              <el-select v-model="queryParams.scoreStatus" placeholder="请选择成绩状态" clearable style="width: 100%">
          <el-option
            v-for="dict in dict.type.score_status"
            :key="dict.value"
            :label="dict.label"
            :value="dict.value"
          />
        </el-select>
      </el-form-item>
          </el-col>
          <el-col :span="8" v-if="!isStudent && queryParams.scoreStatus === undefined">
            <el-form-item label="提交时间" prop="submitTime">
              <el-date-picker
          v-model="queryParams.submitTime"
          type="date"
          value-format="yyyy-MM-dd"
                placeholder="请选择提交时间"
                style="width: 100%"
                clearable
              />
      </el-form-item>
          </el-col>
          <el-col :span="isStudent ? 16 : 8">
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
          </el-col>
        </el-row>
    </el-form>
    </el-card>

    <!-- 操作按钮区域 -->
    <el-card class="operation-card" shadow="never" v-if="!isStudent">
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:score:add']"
          >新增成绩</el-button>
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
    </el-card>

    <!-- 数据表格区域 -->
    <el-card class="table-card" shadow="never">
      <el-table 
        v-loading="loading" 
        :data="scoreList" 
        @selection-change="handleSelectionChange"
        stripe
        border
        highlight-current-row
        class="score-table"
      >
      <el-table-column type="selection" width="55" align="center" v-if="!isStudent"/>
        <el-table-column label="成绩ID" align="center" prop="scoreId" width="80" />
        <el-table-column label="学习记录ID" align="center" prop="learningRecordId" width="120" />
        <el-table-column label="任务ID" align="center" prop="taskId" width="100" />
        <el-table-column label="试卷ID" align="center" prop="paperId" width="100" />
        <el-table-column label="得分" align="center" prop="score" width="100">
          <template slot-scope="scope">
            <el-tag 
              :type="getScoreTagType(scope.row.score)"
              size="medium"
              effect="dark"
            >
              {{ scope.row.score }}分
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column label="成绩描述" align="center" prop="scoreDesc" min-width="200" show-overflow-tooltip />
        <el-table-column label="成绩状态" align="center" prop="scoreStatus" width="100">
        <template slot-scope="scope">
            <el-tag 
              :type="scope.row.scoreStatus === '1' ? 'success' : 'danger'"
              size="medium"
            >
          <dict-tag :options="dict.type.score_status" :value="scope.row.scoreStatus"/>
            </el-tag>
        </template>
      </el-table-column>
        <el-table-column label="提交时间" align="center" prop="submitTime" width="180">
        <template slot-scope="scope">
            <span>{{ parseTime(scope.row.submitTime, '{y}-{m}-{d} {h}:{i}') }}</span>
        </template>
      </el-table-column>
        <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="150" v-if="!isStudent">
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
    </el-card>

    <!-- 添加或修改成绩管理对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="学习记录ID" prop="learningRecordId">
              <el-input v-model="form.learningRecordId" placeholder="请输入学习记录ID" />
        </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="任务ID" prop="taskId">
              <el-input v-model="form.taskId" placeholder="请输入任务ID" />
        </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="试卷ID" prop="paperId">
              <el-input v-model="form.paperId" placeholder="请输入试卷ID" />
        </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="得分" prop="score">
              <el-input-number 
                v-model="form.score" 
                :min="0" 
                :max="100" 
                placeholder="请输入得分(0-100)"
                style="width: 100%"
              />
        </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="成绩描述" prop="scoreDesc">
          <el-input 
            v-model="form.scoreDesc" 
            type="textarea" 
            :rows="3"
            placeholder="请输入成绩描述" 
          />
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="成绩状态" prop="scoreStatus">
              <el-select v-model="form.scoreStatus" placeholder="请选择成绩状态" style="width: 100%">
            <el-option
              v-for="dict in dict.type.score_status"
              :key="dict.value"
              :label="dict.label"
              :value="parseInt(dict.value)"
            ></el-option>
          </el-select>
        </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="提交时间" prop="submitTime">
              <el-date-picker
            v-model="form.submitTime"
                type="datetime"
                value-format="yyyy-MM-dd HH:mm:ss"
                placeholder="请选择提交时间"
                style="width: 100%"
                clearable
              />
        </el-form-item>
          </el-col>
        </el-row>
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
import { isStudent, isTeacher, isAdmin } from "@/utils/roles"

export default {
  name: "Score",
  dicts: ['score_status'],
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
      // 成绩管理表格数据
      scoreList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        scoreId: null,
        learningRecordId: null,
        userId: null,
        taskId: null,
        paperId: null,
        score: null,
        scoreDesc: null,
        scoreStatus: null,
        submitTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        learningRecordId: [
          { required: true, message: "学习记录ID不能为空", trigger: "blur" }
        ],
        score: [
          { required: true, message: "得分不能为空", trigger: "blur" }
        ],
        scoreDesc: [
          { required: true, message: "成绩描述不能为空", trigger: "blur" }
        ],
        scoreStatus: [
          { required: true, message: "成绩状态不能为空", trigger: "change" }
        ],
      },
      // 是否为学生
      isStudent: false,
    }
  },
  computed: {
    // 有效成绩数量
    validCount() {
      return this.scoreList.filter(item => item.scoreStatus === '1').length
    },
    // 平均分
    averageScore() {
      const validScores = this.scoreList.filter(item => item.scoreStatus === '1' && item.score)
      if (validScores.length === 0) return 0
      const total = validScores.reduce((sum, item) => sum + parseFloat(item.score), 0)
      return (total / validScores.length).toFixed(1)
    },
    // 今日新增数量
    todayCount() {
      const today = new Date().toISOString().split('T')[0]
      return this.scoreList.filter(item => {
        const submitDate = this.parseTime(item.submitTime, '{y}-{m}-{d}')
        return submitDate === today
      }).length
    }
  },
  created() {
    // 检查是否为学生角色
    this.isStudent = isStudent()
    
    // 自动读取 query 参数
    if (this.$route.query.userId) {
      this.queryParams.userId = this.$route.query.userId
    }
    if (this.$route.query.learningRecordId) {
      this.queryParams.learningRecordId = this.$route.query.learningRecordId
    }
    this.getList()
  },
  methods: {
    /** 查询成绩管理列表 */
    getList() {
      this.loading = true
      listScore(this.queryParams).then(response => {
        this.scoreList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    // 切换搜索显示
    toggleSearch() {
      this.showSearch = !this.showSearch
    },
    // 获取成绩标签类型
    getScoreTagType(score) {
      if (!score) return 'info'
      const numScore = parseFloat(score)
      if (numScore >= 90) return 'success'
      if (numScore >= 80) return 'warning'
      if (numScore >= 60) return 'primary'
      return 'danger'
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
        scoreStatus: null,
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
      this.title = "添加成绩管理"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const scoreId = row.scoreId || this.ids
      getScore(scoreId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改成绩管理"
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
      this.$modal.confirm('是否确认删除成绩管理编号为"' + scoreIds + '"的数据项？').then(function() {
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

<style lang="scss" scoped>
.app-container {
  padding: 20px;
  background-color: #f5f7fa;
  min-height: calc(100vh - 84px);
}

.mb20 {
  margin-bottom: 20px;
}

.mb8 {
  margin-bottom: 8px;
}

// 统计卡片样式
.stat-card {
  .stat-content {
    display: flex;
    align-items: center;
    padding: 10px 0;
    
    .stat-icon {
      font-size: 48px;
      margin-right: 20px;
      width: 60px;
      text-align: center;
    }
    
    .stat-info {
      flex: 1;
      
      .stat-number {
        font-size: 28px;
        font-weight: bold;
        color: #303133;
        line-height: 1;
        margin-bottom: 5px;
      }
      
      .stat-label {
        font-size: 14px;
        color: #909399;
      }
    }
  }
}

// 搜索卡片样式
.search-card {
  margin-bottom: 20px;
  
  .search-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
    
    .search-title {
      font-size: 16px;
      font-weight: 500;
      color: #303133;
      
      i {
        margin-right: 8px;
        color: #409EFF;
      }
    }
  }
  
  .search-form {
    .el-form-item {
      margin-bottom: 18px;
    }
  }
}

// 操作卡片样式
.operation-card {
  margin-bottom: 20px;
  padding: 15px 20px;
}

// 表格卡片样式
.table-card {
  .score-table {
    .el-table__header-wrapper {
      th {
        background-color: #fafafa;
        color: #606266;
        font-weight: 500;
      }
    }
    
    .el-table__body-wrapper {
      tr:hover {
        background-color: #f5f7fa;
      }
    }
  }
}

// 响应式设计
@media (max-width: 768px) {
  .app-container {
    padding: 10px;
  }
  
  .stat-card .stat-content {
    flex-direction: column;
    text-align: center;
    
    .stat-icon {
      margin-right: 0;
      margin-bottom: 10px;
    }
  }
  
  .search-form .el-col {
    margin-bottom: 10px;
  }
}
</style>
