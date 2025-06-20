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
      <el-form-item label="资源ID，关联learning_resource表" prop="resourceId">
        <el-input
          v-model="queryParams.resourceId"
          placeholder="请输入资源ID，关联learning_resource表"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="视频总时长" prop="totalDuration">
        <el-input
          v-model="queryParams.totalDuration"
          placeholder="请输入视频总时长"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="观看时长" prop="watchedDuration">
        <el-input
          v-model="queryParams.watchedDuration"
          placeholder="请输入观看时长"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="完成率" prop="completionRate">
        <el-input
          v-model="queryParams.completionRate"
          placeholder="请输入完成率"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="最后观看时间" prop="lastWatchTime">
        <el-date-picker clearable
          v-model="queryParams.lastWatchTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择最后观看时间">
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
          v-hasPermi="['system:videoLearningRecord:add']"
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
          v-hasPermi="['system:videoLearningRecord:edit']"
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
          v-hasPermi="['system:videoLearningRecord:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:videoLearningRecord:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="videoLearningRecordList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="记录ID，主键，自增" align="center" prop="recordId" />
      <el-table-column label="学习记录ID，关联learning_record表" align="center" prop="learningRecordId" />
      <el-table-column label="资源ID，关联learning_resource表" align="center" prop="resourceId" />
      <el-table-column label="视频总时长" align="center" prop="totalDuration" />
      <el-table-column label="观看时长" align="center" prop="watchedDuration" />
      <el-table-column label="跳过片段" align="center" prop="skipSegments" />
      <el-table-column label="重复观看片段" align="center" prop="repeatSegments" />
      <el-table-column label="完成率" align="center" prop="completionRate" />
      <el-table-column label="最后观看时间" align="center" prop="lastWatchTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.lastWatchTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:videoLearningRecord:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:videoLearningRecord:remove']"
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

    <!-- 添加或修改视频学习记录，记录学生观看视频的行为数据对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="学习记录ID，关联learning_record表" prop="learningRecordId">
          <el-input v-model="form.learningRecordId" placeholder="请输入学习记录ID，关联learning_record表" />
        </el-form-item>
        <el-form-item label="资源ID，关联learning_resource表" prop="resourceId">
          <el-input v-model="form.resourceId" placeholder="请输入资源ID，关联learning_resource表" />
        </el-form-item>
        <el-form-item label="视频总时长" prop="totalDuration">
          <el-input v-model="form.totalDuration" placeholder="请输入视频总时长" />
        </el-form-item>
        <el-form-item label="观看时长" prop="watchedDuration">
          <el-input v-model="form.watchedDuration" placeholder="请输入观看时长" />
        </el-form-item>
        <el-form-item label="跳过片段" prop="skipSegments">
          <el-input v-model="form.skipSegments" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="重复观看片段" prop="repeatSegments">
          <el-input v-model="form.repeatSegments" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="完成率" prop="completionRate">
          <el-input v-model="form.completionRate" placeholder="请输入完成率" />
        </el-form-item>
        <el-form-item label="最后观看时间" prop="lastWatchTime">
          <el-date-picker clearable
            v-model="form.lastWatchTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择最后观看时间">
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
import { listVideoLearningRecord, getVideoLearningRecord, delVideoLearningRecord, addVideoLearningRecord, updateVideoLearningRecord } from "@/api/system/videoLearningRecord"

export default {
  name: "VideoLearningRecord",
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
      // 视频学习记录，记录学生观看视频的行为数据表格数据
      videoLearningRecordList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        learningRecordId: null,
        resourceId: null,
        totalDuration: null,
        watchedDuration: null,
        skipSegments: null,
        repeatSegments: null,
        completionRate: null,
        lastWatchTime: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        learningRecordId: [
          { required: true, message: "学习记录ID，关联learning_record表不能为空", trigger: "blur" }
        ],
        resourceId: [
          { required: true, message: "资源ID，关联learning_resource表不能为空", trigger: "blur" }
        ],
        totalDuration: [
          { required: true, message: "视频总时长不能为空", trigger: "blur" }
        ],
        watchedDuration: [
          { required: true, message: "观看时长不能为空", trigger: "blur" }
        ],
        completionRate: [
          { required: true, message: "完成率不能为空", trigger: "blur" }
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
    /** 查询视频学习记录，记录学生观看视频的行为数据列表 */
    getList() {
      this.loading = true
      listVideoLearningRecord(this.queryParams).then(response => {
        this.videoLearningRecordList = response.rows
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
        recordId: null,
        learningRecordId: null,
        resourceId: null,
        totalDuration: null,
        watchedDuration: null,
        skipSegments: null,
        repeatSegments: null,
        completionRate: null,
        lastWatchTime: null,
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
      this.ids = selection.map(item => item.recordId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加视频学习记录，记录学生观看视频的行为数据"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const recordId = row.recordId || this.ids
      getVideoLearningRecord(recordId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改视频学习记录，记录学生观看视频的行为数据"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.recordId != null) {
            updateVideoLearningRecord(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addVideoLearningRecord(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除视频学习记录，记录学生观看视频的行为数据编号为"' + recordIds + '"的数据项？').then(function() {
        return delVideoLearningRecord(recordIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/videoLearningRecord/export', {
        ...this.queryParams
      }, `videoLearningRecord_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
