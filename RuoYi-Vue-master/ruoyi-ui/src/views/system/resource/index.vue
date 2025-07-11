<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="课程ID" prop="courseId">
        <el-input
          v-model="queryParams.courseId"
          placeholder="请输入课程ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="资源名称" prop="resourceName">
        <el-input
          v-model="queryParams.resourceName"
          placeholder="请输入资源名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="存储路径" prop="resourcePath">
        <el-input
          v-model="queryParams.resourcePath"
          placeholder="请输入存储路径"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="上传者ID" prop="uploaderId">
        <el-input
          v-model="queryParams.uploaderId"
          placeholder="请输入上传者ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="上传时间" prop="uploadTime">
        <el-date-picker clearable
          v-model="queryParams.uploadTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择上传时间">
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
          v-hasPermi="['system:resource:add']"
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
          v-hasPermi="['system:resource:edit']"
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
          v-hasPermi="['system:resource:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:resource:export']"
        >导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-cpu"
          size="mini"
          @click="handleBatchAnalyze"
        >批量AI分析</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="resourceList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="资源ID" align="center" prop="resourceId" />
      <el-table-column label="课程ID" align="center" prop="courseId" />
      <el-table-column label="资源名称" align="center" prop="resourceName">
        <template slot-scope="scope">
          <el-link type="primary" @click="handleDownloadAndSubmit(scope.row)">{{ scope.row.resourceName }}</el-link>
        </template>
      </el-table-column>
      <el-table-column label="资源类型" align="center" prop="resourceType" />
      <el-table-column label="文件名称" align="center" prop="resourcePath">
        <template slot-scope="scope">
          <span>{{ getFileName(scope.row.resourcePath) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="上传者ID" align="center" prop="uploaderId" />
      <el-table-column label="上传时间" align="center" prop="uploadTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.uploadTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:resource:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:resource:remove']"
          >删除</el-button>
          <el-button
            v-if="scope.row.resourceType && scope.row.resourceType.toLowerCase() === 'ppt'"
            size="mini"
            type="text"
            icon="el-icon-download"
            @click="handleDownloadAndSubmit(scope.row)"
          >下载</el-button>
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

    <!-- 添加或修改学习资源对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="课程编号" prop="courseCode">
          <el-input v-model="form.courseCode" placeholder="请输入课程编号" @blur="handleCourseCodeBlur" :disabled="isUpdate" />
        </el-form-item>
        <el-form-item label="资源名称" prop="resourceName">
          <el-input v-model="form.resourceName" placeholder="请输入资源名称" />
        </el-form-item>
        <el-form-item label="上传资源" prop="resourcePath">
          <file-upload
            v-model="form.resourcePath"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :file-size="3072"
            :limit="isUpdate ? 1 : 10"
            :accept="allowedFileTypes"
            @upload-completed="handleResourceUploadSuccess"
            @before-upload="beforeUpload"
          />
          <div slot="tip" class="el-upload__tip">
            支持的文件类型：PPT、PDF、DOC、DOCX、TXT等文档格式，不支持视频文件
          </div>
        </el-form-item>
        <el-form-item label="上传者ID" prop="uploaderId">
          <el-input v-model="form.uploaderId" placeholder="请输入上传者ID" />
        </el-form-item>
        <el-form-item label="内容摘要">
          <el-input
            type="textarea"
            v-model="form.contentSummary"
            :rows="4"
            readonly
            placeholder="暂无AI分析内容"
          />
          <el-button
            size="mini"
            type="primary"
            icon="el-icon-cpu"
            style="margin-top: 8px;"
            @click="handleAnalyzeResource(form.resourceId)"
          >
            AI分析
          </el-button>
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
import { listResource, getResource, delResource, addResource, updateResource } from "@/api/system/resource"
import { getToken } from "@/utils/auth"
import { listCourse, getCourse } from "@/api/system/course"
import { addSubmission } from '@/api/system/submission'
import { listTask } from '@/api/system/task'
import { getLearningRecordByUserAndCourse } from '@/api/system/learningRecord'
import { getResourceAnalysis, analyzeResource, batchAnalyzeResource } from "@/api/system/learningResource"
import { listAllResource } from "@/api/system/resource"

export default {
  name: "Resource",
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
      // 学习资源表格数据
      resourceList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否为修改
      isUpdate: false,
      // 上传的文件列表
      uploadedFiles: [],
      // 文件上传地址
      uploadUrl: "/system/resource/upload",
      // 上传请求头
      uploadHeaders: {
        Authorization: "Bearer " + getToken()
      },
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        courseId: null,
        resourceName: null,
        resourceType: null,
        resourcePath: null,
        fileSize: null,
        uploaderId: null,
        uploadTime: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        courseCode: [
          { required: true, message: "课程编号不能为空", trigger: "blur" }
        ],
        courseId: [
          { required: true, message: "请输入有效的课程编号", trigger: "change" }
        ],
        resourceName: [
          { required: true, message: "资源名称不能为空", trigger: "blur" }
        ],
        resourcePath: [
          { required: true, message: "请上传资源文件", trigger: "blur" }
        ]
      },
      allowedFileTypes: '.ppt,.pdf,.doc,.docx,.txt'
    }
  },
  created() {
    console.log('[DEBUG] Resource页面created钩子执行');
    this.getList();
    // 确保courseId有效，优先用queryParams.courseId，否则用第一页资源的courseId
    setTimeout(() => {
      let courseId = this.queryParams.courseId;
      if (!courseId && this.resourceList.length > 0) {
        courseId = this.resourceList[0].courseId;
      }
      if (courseId) {
        listAllResource({ courseId }).then(res => {
          console.log('[DEBUG] allList资源数:', res.data?.length);
          (res.data || []).forEach(resource => {
            getResourceAnalysis(resource.resourceId).catch(() => {
              analyzeResource(resource.resourceId)
            })
          })
        })
      } else {
        console.warn('[DEBUG] 未能获取有效的courseId，无法初始化全部资源analysis');
      }
    }, 1000);
  },
  methods: {
    getFileName(path) {
      console.log('[DEBUG] getFileName调用', path);
      if (path) {
        const lastSlash = path.lastIndexOf('/');
        if (lastSlash !== -1) {
            return path.substring(lastSlash + 1);
        }
        return path;
      }
      return '';
    },
    /** 查询学习资源列表 */
    getList(pagination) {
      console.log('[DEBUG] getList方法调用', pagination);
      if (pagination) {
        // 兼容分页组件的事件参数
        if (pagination.page) this.queryParams.pageNum = pagination.page;
        if (pagination.limit) this.queryParams.pageSize = pagination.limit;
      }
      this.loading = true
      listResource(this.queryParams).then(response => {
        // 过滤掉video类型的资源，因为视频资源现在单独管理
        this.resourceList = (response.rows || []).filter(resource => resource.resourceType !== 'video');

        this.total = response.total || 0; // 必须用后端返回的total

        this.loading = false
        console.log('[DEBUG] getList响应', response);
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
        resourceId: null,
        courseId: null,
        courseCode: null,
        resourceName: null,
        resourceType: null,
        resourcePath: null,
        fileSize: null,
        uploaderId: null,
        uploadTime: null
      };
      this.uploadedFiles = [];
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
      this.ids = selection.map(item => item.resourceId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.isUpdate = false;
      this.open = true;
      this.title = "添加学习资源";
    },
    handleCourseCodeBlur() {
      if (this.form.courseCode) {
        listCourse({ courseCode: this.form.courseCode, pageNum: 1, pageSize: 1 }).then(response => {
          if (response.rows && response.rows.length > 0) {
            this.form.courseId = response.rows[0].courseId;
            this.$refs.form.validateField('courseId');
          } else {
            this.form.courseId = null;
            this.$modal.msgError("未找到该课程编号对应的课程");
          }
        });
      } else {
        this.form.courseId = null;
      }
    },
    handleResourceUploadSuccess(res, file) {
      this.uploadedFiles.push({
        path: res.fileName,
        size: file.size,
        type: res.fileName.substring(res.fileName.lastIndexOf(".") + 1),
        name: file.name.lastIndexOf('.') !== -1 ? file.name.substring(0, file.name.lastIndexOf('.')) : file.name
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      this.isUpdate = true;
      const resourceId = row.resourceId || this.ids
      getResource(resourceId).then(response => {
        this.form = response.data
        if (this.form.courseId) {
          getCourse(this.form.courseId).then(courseResponse => {
            this.$set(this.form, 'courseCode', courseResponse.data.courseCode);
          });
        }
        // 查询内容摘要
        if (this.form.resourceId) {
          getResourceAnalysis(this.form.resourceId).then(res => {
            this.form.contentSummary = res.data?.contentSummary || ''
          }).catch(() => {
            this.form.contentSummary = ''
            this.$message.warning('暂无AI内容摘要')
          })
        }
        this.open = true
        this.title = "修改学习资源"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.resourceId != null) {
            if (this.uploadedFiles.length > 0) {
              const newFile = this.uploadedFiles[0];
              this.form.resourcePath = newFile.path;
              this.form.resourceType = newFile.type;
              this.form.fileSize = newFile.size;
            }
            updateResource(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            if (this.uploadedFiles.length === 0) {
              this.$modal.msgError("请上传资源文件");
              return;
            }
            const promises = this.uploadedFiles.map(file => {
              const resourceData = {
                ...this.form,
                resourcePath: file.path,
                resourceType: file.type,
                fileSize: file.size,
                uploadTime: new Date(),
                resourceName: this.form.resourceName ? `${this.form.resourceName} - ${file.name}` : file.name,
              };
              delete resourceData.resourceId;
              return addResource(resourceData);
            });

            Promise.all(promises).then(() => {
              this.$modal.msgSuccess(`成功新增 ${this.uploadedFiles.length} 个资源`);
              this.open = false;
              this.getList();
            }).catch(() => {
              this.$modal.msgError("新增资源失败");
            });
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const resourceIds = row.resourceId || this.ids
      this.$modal.confirm('是否确认删除学习资源编号为"' + resourceIds + '"的数据项？').then(function() {
        return delResource(resourceIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/resource/export', {
        ...this.queryParams
      }, `resource_${new Date().getTime()}.xlsx`)
    },
    getResourceList(courseId) {
      this.loading = true;
      listResource({ courseId: courseId, pageSize: 999 }).then(response => {
        this.resourceList = response.rows;
        this.loading = false;
      }).catch(() => {
        this.resourceList = [];
        this.loading = false;
      });
    },
    handleSwitchTab(tabName) {
      this.activeTab = tabName;
    },
    async handleDownloadAndSubmit(resource) {
      console.log('[DEBUG] handleDownloadAndSubmit被调用，resource:', resource);
      let userId = this.$store.getters.id;
      if (!userId) {
        console.log('[DEBUG] userId为空，尝试获取用户信息');
        await this.$store.dispatch('user/getInfo');
        userId = this.$store.getters.id;
        console.log('[DEBUG] 获取后userId:', userId);
      }
      const courseId = resource.courseId;
      if (!userId || !courseId) {
        this.$modal.msgError('无法获取用户或课程信息，userId:' + userId + ', courseId:' + courseId);
        console.error('[DEBUG] userId或courseId为空，终止提交');
        return;
      }
      let record = await getLearningRecordByUserAndCourse(userId, courseId);
      console.log('[DEBUG] 查询学习记录:', record);
      if (!record) {
        await this.$store.dispatch('user/getInfo');
        await getLearningRecordByUserAndCourse(userId, courseId);
        record = await getLearningRecordByUserAndCourse(userId, courseId);
        console.log('[DEBUG] 再次查询学习记录:', record);
      }
      if (!record) {
        this.$modal.msgError('未找到学习记录，无法提交');
        console.error('[DEBUG] 未找到学习记录，终止提交');
        return;
      }
      const taskRes = await listTask({ courseId, submitMethod: '资料阅读', resourceId: resource.resourceId, pageSize: 10 });
      console.log('[DEBUG] 查询资料阅读任务:', taskRes);
      const task = (taskRes.rows || taskRes.data || []).find(t => t.resourceId === resource.resourceId && t.submitMethod === '资料阅读');
      console.log('[DEBUG] 匹配到的任务:', task);
      if (task) {
        try {
          const now = new Date();
          const submissionData = {
            recordId: record.recordId,
            taskId: task.taskId,
            userId,
            submissionContent: '已阅读资料',
            submissionFile: resource.resourcePath,
            submissionTime: now,
            createTime: now,
            isGraded: '1',
            gradeScore: 100,
            gradeComment: '资料阅读自动满分',
            status: '1',
            delFlag: '0'
          };
          console.log('[DEBUG] 提交任务参数:', submissionData);
          const res = await addSubmission(submissionData);
          console.log('[DEBUG] addSubmission响应:', res);
          this.$modal.msgSuccess('已自动记录任务完成，得分100');
          this.$root && this.$root.$emit && this.$root.$emit('submissionRecordUpdated');
        } catch (e) {
          console.error('[DEBUG] 自动提交任务失败', e);
          this.$modal.msgError('自动提交任务失败，请重试');
        }
      } else {
        console.warn('[DEBUG] 未找到资料阅读任务，不提交，仅下载');
      }
      let url = resource.resourcePath;
      if (!/^https?:\/{2}/.test(url)) {
        url = process.env.VUE_APP_BASE_API + url;
      }
      window.open(url, '_blank');
    },
    beforeUpload(file) {
      const isVideo = file.type.toLowerCase().startsWith('video');
      if (isVideo) {
        this.$modal.msgError('不支持上传视频文件');
        return false;
      }
      return true;
    },
    // 手动触发AI分析
    handleAnalyzeResource(resourceId) {
      this.$modal.loading('AI分析中...')
      analyzeResource(resourceId).then(() => {
        this.$modal.closeLoading()
        this.$message.success('AI分析已触发，请稍后刷新')
        // 分析后自动刷新内容摘要
        getResourceAnalysis(resourceId).then(res => {
          this.form.contentSummary = res.data?.contentSummary || ''
        }).catch(() => {
          this.form.contentSummary = ''
          this.$message.warning('暂无AI内容摘要')
        })
      }).catch(() => {
        this.$modal.closeLoading()
      })
    },
    handleBatchAnalyze() {
      this.$confirm('确定要批量分析所有待分析的学习资源吗？', '批量AI分析', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message.info('已触发批量分析，任务将按队列排队执行');
        batchAnalyzeResource().then(res => {
          this.$modal.msgSuccess(res.msg || '批量分析已触发');
        }).catch(err => {
          this.$modal.msgError('批量分析触发失败: ' + (err.message || '未知错误'));
        });
      });
    }
  },
  computed: {
    groupedResources() {
      if (!this.resourceList) return {};
      const groups = {};
      this.resourceList.forEach(resource => {
        const resourceName = resource.resourceName || '';
        const parts = resourceName.split(' - ');
        const groupName = parts.length > 1 ? parts[0].trim() : '其他';
        const displayName = parts.length > 1 ? parts.slice(1).join(' - ').trim() : resourceName;
        if (!groups[groupName]) {
          groups[groupName] = [];
        }
        groups[groupName].push({
          ...resource,
          displayName: displayName
        });
      });
      return groups;
    }
  }
}
</script>
