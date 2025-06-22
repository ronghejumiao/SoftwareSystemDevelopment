<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>{{ course.courseName || '课程详情' }}</span>
      </div>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="学习资源" name="resources">
          <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
              <el-button
                type="primary"
                plain
                icon="el-icon-plus"
                size="mini"
                @click="handleAdd"
                v-hasPermi="['system:resource:add']"
              >新增资源</el-button>
            </el-col>
          </el-row>

          <div v-if="Object.keys(groupedResources).length === 0" style="text-align: center; color: #909399;">
            暂无学习资源
          </div>
          <el-card class="box-card" v-for="(resources, groupName) in groupedResources" :key="groupName" style="margin-bottom: 20px;">
            <div slot="header" class="clearfix">
              <span>{{ groupName }}</span>
            </div>
            <div v-for="resource in resources" :key="resource.resourceId" class="resource-item">
              <span class="resource-name" @click="handlePreview(resource)">
                <i class="el-icon-document" /> {{ resource.displayName }}
              </span>
              <div class="actions">
                <el-button size="mini" type="text" @click.stop="handleUpdate(resource)" v-hasPermi="['system:resource:edit']">修改</el-button>
                <el-button size="mini" type="text" @click.stop="handleDelete(resource)" v-hasPermi="['system:resource:remove']">删除</el-button>
              </div>
            </div>
          </el-card>
        </el-tab-pane>
        <el-tab-pane label="课程能力要求" name="requirements">
          <p>这里是课程能力要求内容区域。</p>
        </el-tab-pane>
        <el-tab-pane label="学习任务" name="tasks">
          <p>这里是学习任务内容区域。</p>
        </el-tab-pane>
        <el-tab-pane label="答题" name="quiz">
          <p>这里是答题内容区域。</p>
        </el-tab-pane>
      </el-tabs>
    </el-card>

    <!-- 添加或修改学习资源对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="资源名称" prop="resourceName">
          <el-input v-model="form.resourceName" placeholder="请输入资源名称 (作为分组依据)" />
        </el-form-item>
        <el-form-item label="上传资源" prop="resourcePath">
          <file-upload v-model="form.resourcePath" :action="uploadUrl" :headers="uploadHeaders" :file-size="3072" :limit="isUpdate ? 1 : 10" @upload-completed="handleResourceUploadSuccess" />
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
import { getCourse } from "@/api/system/course";
import { listResource, getResource, delResource, addResource, updateResource } from "@/api/system/resource";
import { getToken } from "@/utils/auth";
import FileUpload from '@/components/FileUpload';

export default {
  name: "CourseDetail",
  components: { FileUpload },
  data() {
    return {
      activeTab: 'resources',
      course: {},
      // 学习资源相关
      resourceList: [],
      open: false,
      title: "",
      isUpdate: false,
      form: {},
      rules: {
        resourceName: [
          { required: true, message: "资源名称不能为空", trigger: "blur" }
        ],
      },
      uploadUrl: process.env.VUE_APP_BASE_API + "/system/resource/upload",
      uploadHeaders: {
        Authorization: "Bearer " + getToken()
      },
      uploadedFiles: [],
    };
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
  },
  created() {
    const courseId = this.$route.params.courseId;
    if (courseId) {
      this.getCourseDetails(courseId);
      this.getResourceList(courseId);
    }
  },
  methods: {
    getCourseDetails(courseId) {
      getCourse(courseId).then(response => {
        this.course = response.data;
      });
    },
    getResourceList(courseId) {
      listResource({ courseId: courseId, pageSize: 999 }).then(response => {
        this.resourceList = response.rows;
      });
    },
    handlePreview(resource) {
      if (resource.resourcePath) {
        window.open(process.env.VUE_APP_BASE_API + resource.resourcePath);
      }
    },
    reset() {
      this.form = {
        resourceId: null,
        courseId: this.course.courseId,
        resourceName: null,
        resourceType: null,
        resourcePath: null,
        fileSize: null,
        uploaderId: null,
        uploadTime: null,
      };
      this.uploadedFiles = [];
      this.resetForm("form");
    },
    handleAdd() {
      this.reset();
      this.isUpdate = false;
      this.open = true;
      this.title = "添加学习资源";
    },
    handleUpdate(resource) {
      this.reset();
      this.isUpdate = true;
      getResource(resource.resourceId).then(response => {
        // 分离分组名称和显示名称
        const resourceName = response.data.resourceName || '';
        const parts = resourceName.split(' - ');
        if (parts.length > 1) {
          response.data.resourceName = parts[0].trim();
        }
        this.form = response.data;
        this.open = true;
        this.title = "修改学习资源";
      });
    },
    handleDelete(resource) {
      const resourceIds = resource.resourceId;
      const resourceName = resource.displayName || resource.resourceName;
      this.$modal.confirm('是否确认删除学习资源【' + resourceName + '】？').then(function() {
        return delResource(resourceIds);
      }).then(() => {
        this.getResourceList(this.course.courseId);
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    handleResourceUploadSuccess(res, file) {
      this.uploadedFiles.push({
        path: res.fileName,
        size: file.size,
        type: res.fileName.substring(res.fileName.lastIndexOf(".") + 1),
        name: file.name
      });
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.resourceId != null) { // 更新模式
            if (this.uploadedFiles.length > 0) {
              const newFile = this.uploadedFiles[0];
              this.form.resourcePath = newFile.path;
              this.form.resourceType = newFile.type;
              this.form.fileSize = newFile.size;
              this.form.resourceName = `${this.form.resourceName} - ${newFile.name}`;
            }
            updateResource(this.form).then(() => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getResourceList(this.course.courseId);
            });
          } else { // 新增模式
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
                resourceName: `${this.form.resourceName} - ${file.name}`,
              };
              return addResource(resourceData);
            });

            Promise.all(promises).then(() => {
              this.$modal.msgSuccess(`成功新增 ${this.uploadedFiles.length} 个资源`);
              this.open = false;
              this.getResourceList(this.course.courseId);
            }).catch(() => {
              this.$modal.msgError("新增资源失败");
            });
          }
        }
      });
    },
    cancel() {
      this.open = false;
      this.reset();
    }
  }
};
</script>

<style scoped>
.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both
}
.mb8 {
  margin-bottom: 8px;
}
.resource-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px 0;
  font-size: 14px;
  border-bottom: 1px solid #EBEEF5;
}
.resource-item:last-child {
  border-bottom: none;
}
.resource-name {
  cursor: pointer;
  color: #303133;
  transition: color 0.2s;
}
.resource-name:hover {
  color: #409EFF;
}
.resource-name i {
  margin-right: 8px;
}
</style> 