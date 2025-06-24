<template>
  <div class="course-detail-container">
    <!-- 顶部大图+课程信息 -->
    <div class="course-hero" :style="courseHeroStyle">
      <div class="course-hero-mask">
        <div class="course-hero-info">
          <div class="course-title">{{ course.courseName }}</div>
          <div class="course-meta">
            <span><i class="el-icon-collection"></i> 编号：{{ course.courseCode }}</span>
            <span><i class="el-icon-user"></i> 教师：{{ course.teacherName || '未知' }}</span>
            <span><i class="el-icon-notebook-2"></i> 分类：{{ course.courseCategory }}</span>
            <span><i class="el-icon-star-on"></i> 学分：{{ course.credit }}</span>
            <span><i class="el-icon-time"></i> 学时：{{ course.hours }}</span>
          </div>
        </div>
      </div>
    </div>
    <el-tabs v-model="activeTab" class="block-tabs">
      <el-tab-pane label="课程能力要求" name="requirements">
        <div class="block-title"><i class="el-icon-medal"></i> 课程能力要求</div>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button
              type="primary"
              plain
              icon="el-icon-plus"
              size="mini"
              @click="handleAddRequirement"
            >新增能力要求</el-button>
          </el-col>
        </el-row>
        <div v-if="requirementList.length === 0" style="text-align: center; color: #909399;">暂无能力要求</div>
        <div v-for="item in requirementList" :key="item.requirementId" class="requirement-block">
          <div>
            <div class="requirement-header">
              <i class="el-icon-s-flag requirement-icon"></i>
              <span class="requirement-title">{{ item.skillName }}</span>
              <div class="requirement-actions">
                <el-button size="mini" type="text" icon="el-icon-edit" @click="handleEditRequirement(item)">编辑</el-button>
                <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDeleteRequirement(item)">删除</el-button>
              </div>
            </div>


            <div class="requirement-content">
              <div class="requirement-desc"><b>课程描述：</b>{{ item.description }}</div>
              <div class="requirement-required"><b>课堂要求：</b>{{ item.requiredText }}</div>
            </div>
          </div>
        </div>
        <el-dialog :title="requirementDialogTitle" :visible.sync="requirementDialogVisible" width="500px" append-to-body>
          <el-form ref="requirementForm" :model="requirementForm" :rules="requirementRules" label-width="100px">
            <el-form-item label="课程编号" prop="courseCode">
              <el-input v-model="requirementForm.courseCode" :disabled="true" />
            </el-form-item>
            <el-form-item label="能力名称" prop="skillName">
              <el-input v-model="requirementForm.skillName" placeholder="请输入能力名称" />
            </el-form-item>
            <el-form-item label="课程描述" prop="description">
              <el-input v-model="requirementForm.description" placeholder="请输入课程描述" maxlength="100" show-word-limit />
            </el-form-item>
            <el-form-item label="课堂要求描述" prop="requiredText">
              <el-input v-model="requirementForm.requiredText" placeholder="请输入课堂要求描述" />
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button type="primary" @click="submitRequirementForm">确 定</el-button>
            <el-button @click="requirementDialogVisible = false">取 消</el-button>
          </div>
        </el-dialog>
      </el-tab-pane>
        <el-tab-pane label="学习资源" name="resources">
        <div class="block-title"><i class="el-icon-folder"></i> 学习资源</div>
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
      </el-tab-pane>

      <el-tab-pane label="学习任务" name="tasks">
        <course-task v-if="course.courseId" :course-id="course.courseId" @switch-tab="handleSwitchTab" />
        <div v-else style="text-align: center; padding: 40px; color: #909399;">加载中...</div>
      </el-tab-pane>
      <el-tab-pane label="答题" name="quiz">
        <course-quiz v-if="course.courseId" :course-id="course.courseId" />
        <div v-else style="text-align: center; padding: 40px; color: #909399;">
          加载中...
        </div>
      </el-tab-pane>
      <el-tab-pane label="视频学习" name="videos">
        <div class="block-title"><i class="el-icon-video-camera"></i> 视频学习</div>
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button
              type="primary"
              plain
              icon="el-icon-plus"
              size="mini"
              @click="handleAddVideo"
            >新增视频</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="info"
              plain
              icon="el-icon-view"
              size="mini"
              @click="goToVideoList"
            >查看全部</el-button>
          </el-col>
        </el-row>
        <div v-if="Object.keys(groupedVideos).length === 0" style="text-align: center; color: #909399;">
          暂无视频资源
        </div>
        <el-card class="box-card" v-for="(videos, chapterName) in groupedVideos" :key="chapterName" style="margin-bottom: 20px;">
          <div slot="header" class="clearfix">
            <span>{{ chapterName }}</span>
          </div>
          <div v-for="video in videos" :key="video.videoId" class="video-item">
            <div class="video-info" @click="handleVideoPreview(video)">
              <div class="video-thumbnail">
                <img v-if="video.thumbnail" :src="baseUrl + video.thumbnail" alt="视频封面">
                <i v-else class="el-icon-video-camera"></i>
              </div>
              <div class="video-details">
                <div class="video-name">{{ video.description }}</div>
                <div class="video-meta">
                  <span>章节：{{ video.videoName }}</span>
                  <span>大小：{{ video.fileSize }}MB</span>
                </div>
              </div>
            </div>
            <div class="video-actions">
              <el-button size="mini" type="text" icon="el-icon-edit" @click.stop="handleEditVideo(video)">修改</el-button>
              <el-button size="mini" type="text" icon="el-icon-delete" @click.stop="handleDeleteVideo(video)">删除</el-button>
            </div>
          </div>
        </el-card>
      </el-tab-pane>
    </el-tabs>





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

        </el-tab-pane>
        <el-tab-pane label="学习任务" name="tasks">
        <div class="block-title"><i class="el-icon-s-operation"></i> 学习任务</div>
          <p>这里是学习任务内容区域。</p>
        </el-tab-pane>
        <el-tab-pane label="答题" name="quiz">
        <div class="block-title"><i class="el-icon-edit-outline"></i> 答题</div>
          <p>这里是答题内容区域。</p>
        </el-tab-pane>
        <el-tab-pane label="视频学习" name="videos">
          <div class="block-title"><i class="el-icon-video-camera"></i> 视频学习</div>
          <el-row :gutter="10" class="mb8">
            <el-col :span="1.5">
              <el-button
                type="primary"
                plain
                icon="el-icon-plus"
                size="mini"
                @click="handleAddVideo"
              >新增视频</el-button>
            </el-col>
            <el-col :span="1.5">
              <el-button
                type="info"
                plain
                icon="el-icon-view"
                size="mini"
                @click="goToVideoList"
              >查看全部</el-button>
            </el-col>
          </el-row>
          <div v-if="Object.keys(groupedVideos).length === 0" style="text-align: center; color: #909399;">
            暂无视频资源
          </div>
          <el-card class="box-card" v-for="(videos, chapterName) in groupedVideos" :key="chapterName" style="margin-bottom: 20px;">
            <div slot="header" class="clearfix">
              <span>{{ chapterName }}</span>
            </div>
            <div v-for="video in videos" :key="video.videoId" class="video-item">
              <div class="video-info" @click="handleVideoPreview(video)">
                <div class="video-thumbnail">
                  <img v-if="video.thumbnail" :src="baseUrl + video.thumbnail" alt="视频封面">
                  <i v-else class="el-icon-video-camera"></i>
                </div>
                <div class="video-details">
                  <div class="video-name">{{ video.description }}</div>
                  <div class="video-meta">
                    <span>章节：{{ video.videoName }}</span>
                    <span>大小：{{ video.fileSize }}MB</span>
                  </div>
                </div>
              </div>
              <div class="video-actions">
                <el-button size="mini" type="text" icon="el-icon-edit" @click.stop="handleEditVideo(video)">修改</el-button>
                <el-button size="mini" type="text" icon="el-icon-delete" @click.stop="handleDeleteVideo(video)">删除</el-button>
              </div>
            </div>
          </el-card>
        </el-tab-pane>
      </el-tabs>
  </div>
</template>

<script>
import { getCourse } from "@/api/system/course";
import { listResource, getResource, delResource, addResource, updateResource } from "@/api/system/resource";
import { getToken } from "@/utils/auth";
import FileUpload from '@/components/FileUpload';
import { listRequirement, addRequirement, updateRequirement, delRequirement } from '@/api/system/requirement';
import { listCourse } from '@/api/system/course';
import CourseQuiz from './quiz.vue';
import CourseTask from './task.vue';
import { listVideoresource, getVideoresource, delVideoresource } from "@/api/system/videoresource";

export default {
  name: "CourseDetail",
  components: { FileUpload, CourseQuiz, CourseTask },
  data() {
    return {
      activeTab: 'requirements',
      course: {},
      baseUrl: process.env.VUE_APP_BASE_API,
      // 课程图片背景样式
      get courseHeroStyle() {
        if (this.course.courseImg) {
          let imgPath = this.course.courseImg;
          if (!/^https?:\/\//.test(imgPath)) {
            if (!imgPath.startsWith('/')) {
              imgPath = '/' + imgPath;
            }
            imgPath = this.baseUrl + imgPath;
          }
          return {
            backgroundImage: `url('${imgPath}')`,
            backgroundPosition: 'center center',
            backgroundSize: 'cover',
            backgroundRepeat: 'no-repeat'
          };
        } else {
          return {
            background: 'linear-gradient(120deg, #e6eaf3 0%, #f8fafc 100%)'
          };
        }
      },
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
      uploadUrl:  "/system/resource/upload",
      uploadHeaders: {
        Authorization: "Bearer " + getToken()
      },
      uploadedFiles: [],
      // 课程能力要求相关
      requirementList: [],
      requirementDialogVisible: false,
      requirementDialogTitle: '',
      requirementForm: {},
      requirementRules: {
        courseCode: [
          { required: true, message: '课程编号不能为空', trigger: 'blur' }
        ],
        skillName: [
          { required: true, message: '能力名称不能为空', trigger: 'blur' }
        ],
        description: [
          { required: true, message: '课程描述不能为空', trigger: 'blur' },
          { max: 100, message: '课程描述不能超过100个字符', trigger: 'blur' }
        ],
        requiredText: [
          { required: true, message: '课堂要求描述不能为空', trigger: 'blur' }
        ]
      },
      // 视频相关
      videoList: [],
      groupedVideos: {},
      uploaderId: JSON.parse(localStorage.getItem('userInfo') || '{}').userId || '',
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
      this.getRequirementList();
      this.getVideoList(courseId);
    }
  },
  methods: {
    getCourseDetails(courseId) {
      getCourse(courseId).then(response => {
        this.course = response.data;
      });
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
    handlePreview(resource) {
      if (resource.resourcePath) {
        window.open(process.env.VUE_APP_BASE_API + resource.resourcePath);
      }
    },
    reset() {
      const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
      const uploaderId = userInfo.userId || '';
      this.form = {
        resourceId: null,
        courseId: this.course.courseId,
        resourceName: null,
        resourceType: null,
        resourcePath: null,
        fileSize: null,
        uploaderId: uploaderId,
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
                uploaderId: this.uploaderId,
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
    },


    // 课程能力要求相关
    getRequirementList() {
      if (!this.course.courseId) return;
      listRequirement({ courseId: this.course.courseId, pageNum: 1, pageSize: 999 }).then(async res => {
        const list = res.rows || [];
        // 批量查找所有courseId对应的courseCode
        const idSet = Array.from(new Set(list.map(item => item.courseId).filter(Boolean)));
        let idToCode = {};
        if (idSet.length > 0) {
          const courseRes = await listCourse({ pageNum: 1, pageSize: 9999 });
          if (courseRes.rows) {
            courseRes.rows.forEach(c => {
              idToCode[c.courseId] = c.courseCode;
            });
          }
        }
        list.forEach(item => {
          item.courseCode = idToCode[item.courseId] || '';
        });
        this.requirementList = list;
      });
    },
    handleAddRequirement() {
      this.requirementForm = {
        requirementId: null,
        courseCode: this.course.courseCode,
        skillName: '',
        description: '',
        requiredText: ''
      };
      this.requirementDialogTitle = '新增课程能力要求';
      this.requirementDialogVisible = true;
    },
    handleEditRequirement(item) {
      this.requirementForm = { ...item };
      this.requirementDialogTitle = '编辑课程能力要求';
      this.requirementDialogVisible = true;
    },
    async submitRequirementForm() {
      this.$refs.requirementForm.validate(async valid => {
        if (valid) {
          // 通过courseCode查ID
          let courseId = null;
          if (this.requirementForm.courseCode) {
            const res = await listCourse({ courseCode: this.requirementForm.courseCode, pageNum: 1, pageSize: 1 });
            if (res.rows && res.rows.length > 0) {
              courseId = res.rows[0].courseId;
            }
          }
          if (courseId) {
            this.requirementForm.courseId = courseId;
          }
          if (this.requirementForm.requirementId) {
            updateRequirement(this.requirementForm).then(() => {
              this.$modal.msgSuccess('修改成功');
              this.requirementDialogVisible = false;
              this.getRequirementList();
            });
          } else {
            addRequirement(this.requirementForm).then(() => {
              this.$modal.msgSuccess('新增成功');
              this.requirementDialogVisible = false;
              this.getRequirementList();
            });
          }
        }
      });
    },
    handleDeleteRequirement(item) {
      this.$modal.confirm('是否确认删除该能力要求？').then(() => {
        delRequirement(item.requirementId).then(() => {
          this.$modal.msgSuccess('删除成功');
          this.getRequirementList();
        });
      });
    },
    // 视频相关方法
    getVideoList(courseId) {
      listVideoresource({ courseId: courseId, pageSize: 999 }).then(response => {
        this.videoList = response.rows;
        // 按章节分组
        this.groupedVideos = {};
        this.videoList.forEach(video => {
          const chapter = video.videoName || '未分类';
          if (!this.groupedVideos[chapter]) {
            this.groupedVideos[chapter] = [];
          }
          this.groupedVideos[chapter].push(video);
        });
      });
    },
    handleAddVideo() {
      this.$router.push({ 
        name: 'VideoAdd',
        query: { 
          courseId: this.course.courseId,
          courseCode: this.course.courseCode
        }
      });
    },
    goToVideoList() {
      this.$router.push({ 
        name: 'VideoList',
        query: { 
          courseId: this.course.courseId,
          courseCode: this.course.courseCode
        }
      });
    },
    handleVideoPreview(video) {
      // 推荐跳转到大屏播放页
      this.$router.push({ 
        name: 'VideoPlay',
        params: { videoId: video.videoId }
      });
    },
    handleEditVideo(video) {
      this.$router.push({ 
        name: 'VideoEdit',
        query: { 
          videoId: video.videoId,
          courseId: this.course.courseId
        }
      });
    },
    handleDeleteVideo(video) {
      const videoId = video.videoId;
      this.$modal.confirm('是否确认删除该视频？').then(function() {
        return delVideoresource(videoId);
      }).then(() => {
        this.getVideoList(this.course.courseId);
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
  },
  watch: {
    'course.courseId'(val) {
      if (val) this.getRequirementList();
    },

    handleSwitchTab(tabName) {
      this.activeTab = tabName;
    },


  }
};
</script>

<style scoped>
.course-detail-container {
  max-width: 1000px;
  margin: 0 auto;
  padding-bottom: 40px;
}
.course-hero {
  width: 100%;
  height: 200px;
  background-size: cover;
  background-position: center;
  border-radius: 12px;
  position: relative;
  margin-bottom: 24px;
  box-shadow: 0 4px 24px rgba(0,0,0,0.08);
}
.course-hero-mask {
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg,rgba(0,0,0,0.45) 30%,rgba(0,0,0,0.10) 100%);
  border-radius: 12px;
  position: absolute;
  left: 0; top: 0;
  display: flex;
  align-items: flex-end;
}
.course-hero-info {
  color: #fff;
  padding: 24px 36px 18px 36px;
}
.course-title {
  font-size: 22px;
  font-weight: 600;
  margin-bottom: 10px;
  letter-spacing: 1px;
}
.course-meta {
  font-size: 13px;
  display: flex;
  gap: 24px;
  flex-wrap: wrap;
}
.course-meta i {
  margin-right: 4px;
  color: #1890ff;
}
.block-tabs {
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
  padding: 18px 24px 4px 24px;
  margin-bottom: 24px;
}
.block-title {
  font-size: 17px;
  font-weight: 600;
  color: #1890ff;
  margin-bottom: 12px;
  display: flex;
  align-items: center;
  gap: 8px;
}
.mb8 {
  margin-bottom: 8px;
}
.requirement-block {
  background: #f8fafd;
  border-radius: 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.04);
  padding: 18px 24px 12px 24px;
  margin-bottom: 18px;
  position: relative;
  transition: box-shadow 0.2s;
}
.requirement-block:hover {
  box-shadow: 0 6px 24px rgba(24,144,255,0.13);
}
.requirement-main {
  display: flex;
  align-items: flex-start;
  justify-content: space-between;
}
.requirement-img {
  width: 80px;
  height: 80px;
  object-fit: cover;
  border-radius: 10px;
  margin-left: 24px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
}
.requirement-header {
  display: flex;
  align-items: center;
  margin-bottom: 8px;
}
.requirement-icon {
  font-size: 20px;
  color: #1890ff;
  margin-right: 8px;
}
.requirement-title {
  font-size: 15px;
  font-weight: 600;
  color: #222;
  flex: 1;
}
.requirement-actions {
  display: flex;
  gap: 6px;
}
.requirement-content {
  font-size: 13px;
  color: #444;
  line-height: 1.7;
}
.requirement-desc {
  margin-bottom: 6px;
}
.requirement-required {
  margin-bottom: 0;
}
.resource-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 10px 0;
  font-size: 13px;
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
  color: #1890ff;
}
.resource-name i {
  margin-right: 6px;
}
.actions {
  display: flex;
  gap: 6px;
}

.video-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 12px;
  border-bottom: 1px solid #EBEEF5;
  transition: background-color 0.2s;
}
.video-item:last-child {
  border-bottom: none;
}
.video-item:hover {
  background-color: #f5f7fa;
}
.video-info {
  display: flex;
  align-items: center;
  cursor: pointer;
  flex: 1;
}
.video-thumbnail {
  width: 120px;
  height: 68px;
  margin-right: 16px;
  background: #f5f7fa;
  border-radius: 4px;
  overflow: hidden;
  display: flex;
  align-items: center;
  justify-content: center;
}
.video-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.video-thumbnail i {
  font-size: 24px;
  color: #909399;
}
.video-details {
  flex: 1;
}
.video-name {
  font-size: 14px;
  color: #303133;
  margin-bottom: 8px;
}
.video-meta {
  font-size: 13px;
  color: #909399;
}
.video-meta span {
  margin-right: 16px;
}
.video-actions {
  display: flex;
  gap: 8px;
}
</style>