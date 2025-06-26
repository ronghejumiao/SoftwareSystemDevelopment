<template>
  <div class="app-container">
    <div class="course-info">
      <div class="course-title">{{ course.courseName }}</div>
      <div class="course-meta">
        <span><i class="el-icon-collection"></i> 编号：{{ course.courseCode }}</span>
        <span><i class="el-icon-user"></i> 教师：{{ course.teacherName || '未知' }}</span>
      </div>
    </div>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
        >新增视频</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-back"
          size="mini"
          @click="goBack"
        >返回课程</el-button>
      </el-col>
    </el-row>

    <div v-if="Object.keys(groupedVideos).length === 0" style="text-align: center; color: #909399; margin-top: 100px;">
      暂无视频资源
    </div>

    <el-card v-for="(videos, chapterName) in groupedVideos" :key="chapterName" class="chapter-card">
      <div slot="header" class="chapter-header">
        <span class="chapter-title">{{ chapterName }}</span>
        <span class="video-count">{{ videos.length }}个视频</span>
      </div>
      <div class="video-grid">
        <div
          v-for="video in videos"
          :key="video.videoId"
          class="video-item"
          :class="{ 'active-video': video.videoId == activeVideoId }"
          @click="goToVideo(video)"
        >
          <div class="video-thumbnail">
            <img v-if="video.thumbnail" :src="baseUrl + video.thumbnail" alt="视频封面">
            <i v-else class="el-icon-video-camera"></i>
          </div>
          <div class="video-info">
            <div class="video-name">{{ video.description }}</div>
            <div class="video-meta">
              <span>章节：{{ video.videoName }}</span>
              <span>大小：{{ video.fileSize }}MB</span>
            </div>
          </div>
          <div class="video-actions">
            <el-button size="mini" type="text" icon="el-icon-edit" @click.stop="handleUpdate(video)">修改</el-button>
            <el-button size="mini" type="text" icon="el-icon-delete" @click.stop="handleDelete(video)">删除</el-button>
          </div>
        </div>
      </div>
    </el-card>

    <!-- 视频预览对话框 -->
    <el-dialog :visible.sync="previewVisible" width="800px" class="video-preview-dialog">
      <div class="video-player">
        <video v-if="currentVideo" :src="baseUrl + currentVideo.videoPath" controls></video>
      </div>
      <div class="video-info-panel" v-if="currentVideo">
        <h3>{{ currentVideo.description }}</h3>
        <p>章节：{{ currentVideo.videoName }}</p>
        <p>大小：{{ currentVideo.fileSize }}MB</p>
      </div>
    </el-dialog>

    <!-- 新增/编辑弹窗 -->
    <el-dialog :visible.sync="open" width="800px" class="video-form-dialog">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="课程编号" prop="courseCode">
          <el-input v-model="form.courseCode" disabled></el-input>
        </el-form-item>
        <el-form-item label="视频名称" prop="videoName">
          <el-input v-model="form.videoName"></el-input>
        </el-form-item>
        <el-form-item label="视频类型" prop="videoType">
          <el-select v-model="form.videoType">
            <el-option value="MP4"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="视频存储路径" prop="videoPath">
          <el-input v-model="form.videoPath"></el-input>
        </el-form-item>
        <el-form-item label="视频大小" prop="fileSize">
          <el-input v-model="form.fileSize"></el-input>
        </el-form-item>
        <el-form-item label="视频时长" prop="duration">
          <el-input v-model="form.duration"></el-input>
        </el-form-item>
        <el-form-item label="上传用户 ID" prop="suploaderId">
          <el-input v-model="form.suploaderId"></el-input>
        </el-form-item>
        <el-form-item label="上传时间" prop="uploadTime">
          <el-input v-model="form.uploadTime"></el-input>
        </el-form-item>
        <el-form-item label="封面图片" prop="thumbnail">
          <el-input v-model="imageUrl"></el-input>
          <el-button @click="removeThumbnail">移除封面</el-button>
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description"></el-input>
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button @click="open = false">取消</el-button>
        <el-button type="primary" @click="submitForm">提交</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listVideoresource, getVideoresource, delVideoresource, addVideoresource, updateVideoresource } from "@/api/system/videoresource";
import { listCourse } from "@/api/system/course";
import { getToken } from "@/utils/auth";

export default {
  name: "VideoList",
  data() {
    return {
      baseUrl: process.env.VUE_APP_BASE_API,
      course: {},
      videoList: [],
      groupedVideos: {},
      previewVisible: false,
      currentVideo: null,
      activeVideoId: null,
      // 新增/编辑弹窗相关
      open: false,
      title: '',
      form: {},
      rules: {
        courseCode: [
          { required: true, message: "所属课程不能为空", trigger: "change" }
        ],
        videoName: [
          { required: true, message: "视频名称不能为空", trigger: "blur" }
        ],
        videoType: [
          { required: true, message: "视频类型不能为空", trigger: "change" }
        ],
        videoPath: [
          { required: true, message: "视频存储路径不能为空", trigger: "blur" }
        ],
        fileSize: [
          { required: true, message: "视频大小不能为空", trigger: "blur" }
        ],
        suploaderId: [
          { required: true, message: "上传用户 ID不能为空", trigger: "blur" }
        ],
      },
      upload: {
        videoUrl: process.env.VUE_APP_BASE_API + "/system/videoresource/uploadVideo",
        imageUrl: process.env.VUE_APP_BASE_API + "/system/videoresource/uploadThumbnail",
        headers: {
          Authorization: "Bearer " + getToken()
        }
      },
      videoFileList: [],
      imageUrl: '',
      courseOptions: [],
      courseCodeToId: {},
      courseIdToCode: {},
      // 多选相关
      ids: [],
      single: true,
      multiple: true,
    };
  },
  created() {
    const { courseId, videoId } = this.$route.query;
    if (courseId) {
      this.getCourseInfo(courseId);
      this.getVideoList(courseId);
    }
    if (videoId) {
      this.activeVideoId = videoId;
    }
    this.getCourseOptions();
  },
  mounted() {
    this.$nextTick(() => {
      if (this.activeVideoId) {
        const el = this.$el.querySelector('.active-video');
        if (el) el.scrollIntoView({ behavior: 'smooth', block: 'center' });
      }
    });
  },
  methods: {
    getCourseInfo(courseId) {
      listCourse().then(response => {
        const course = response.rows.find(c => c.courseId == courseId);
        if (course) this.course = course;
      });
    },
    getCourseOptions() {
      listCourse().then(response => {
        this.courseOptions = response.rows;
        this.courseOptions.forEach(course => {
          this.courseCodeToId[course.courseCode] = course.courseId;
          this.courseIdToCode[course.courseId] = course.courseCode;
        });
      });
    },
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
    // 新增
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加视频学习资源";
      if (this.course && this.course.courseCode) {
        this.form.courseCode = this.course.courseCode;
        this.form.courseId = this.course.courseId;
      }
    },
    // 修改
    handleUpdate(video) {
      this.reset();
      getVideoresource(video.videoId).then(response => {
        const data = response.data;
        this.form = data;
        this.form.courseCode = this.courseIdToCode[data.courseId];
        if (data.thumbnail) {
          this.imageUrl = process.env.VUE_APP_BASE_API + data.thumbnail;
        } else {
          this.imageUrl = '';
        }
        if (data.videoPath) {
          this.form.videoPath = data.videoPath;
        }
        this.open = true;
        this.title = "修改视频学习资源";
      });
    },
    // 删除
    handleDelete(video) {
      const videoIds = video.videoId || this.ids;
      this.$modal.confirm('是否确认删除视频学习资源编号为"' + videoIds + '"的数据项？').then(function() {
        return delVideoresource(videoIds);
      }).then(() => {
        this.getVideoList(this.course.courseId);
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    // 多选
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.videoId);
      this.single = selection.length!==1;
      this.multiple = !selection.length;
    },
    // 表单重置
    reset() {
      this.form = {
        videoId: null,
        courseId: this.course.courseId || null,
        courseCode: this.course.courseCode || null,
        videoName: null,
        videoType: null,
        videoPath: null,
        fileSize: null,
        duration: null,
        suploaderId: null,
        uploadTime: null,
        thumbnail: null,
        description: null
      };
      this.imageUrl = '';
      this.videoFileList = [];
    },
    // 提交
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.form.videoType = "MP4";
          if (!this.form.courseId) {
            this.$modal.msgError("课程编号不存在");
            return;
          }
          const submitForm = { ...this.form };
          delete submitForm.courseCode;
          if (submitForm.videoId != null) {
            updateVideoresource(submitForm).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getVideoList(this.course.courseId);
            });
          } else {
            addVideoresource(submitForm).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getVideoList(this.course.courseId);
            });
          }
        }
      });
    },
    // 上传
    handleVideoSuccess(response, file, fileList) {
      if (response.code === 200) {
        this.form.videoPath = response.videoUrl;
        this.form.fileSize = response.fileSize;
        this.form.duration = response.duration;
        this.form.uploadTime = response.uploadTime;
        if (response.coverImage) {
          this.form.thumbnail = response.coverImage;
          this.imageUrl = process.env.VUE_APP_BASE_API + response.coverImage;
        }
        this.$modal.msgSuccess("视频上传成功");
      } else {
        this.$modal.msgError(response.msg);
      }
    },
    handleThumbnailSuccess(response, file) {
      if (response.code === 200) {
        this.imageUrl = process.env.VUE_APP_BASE_API + response.thumbnailUrl;
        this.form.thumbnail = response.thumbnailUrl;
        this.$modal.msgSuccess("封面上传成功");
      } else {
        this.$modal.msgError(response.msg);
      }
    },
    handleError(err) {
      this.$modal.msgError("上传失败");
    },
    beforeVideoUpload(file) {
      const isMP4 = file.type === 'video/mp4';
      const isLt3G = file.size / 1024 / 1024 / 1024 < 3;
      if (!isMP4) {
        this.$message.error('只能上传MP4格式的视频文件!');
        return false;
      }
      if (!isLt3G) {
        this.$message.error('视频大小不能超过 3GB!');
        return false;
      }
      return true;
    },
    beforeThumbnailUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png';
      const isLt2M = file.size / 1024 / 1024 < 2;
      if (!isJPG) {
        this.$message.error('上传封面图片只能是 JPG/PNG 格式!');
        return false;
      }
      if (!isLt2M) {
        this.$message.error('上传封面图片大小不能超过 2MB!');
        return false;
      }
      return true;
    },
    removeThumbnail() {
      this.imageUrl = '';
      this.form.thumbnail = '';
    },
    // 预览
    handlePreview(video) {
      this.currentVideo = video;
      this.previewVisible = true;
    },
    goBack() {
      this.$router.push({
        name: 'CourseDetailPage',
        params: { courseId: this.course.courseId },
        query: { tab: 'videos' }
      });
    },
    goToVideo(row) {
      this.$router.push({ name: "VideoPlay", params: { videoId: row.videoId } });
    },
  }
};
</script>

<style scoped>
.course-info {
  background: #fff;
  padding: 24px;
  border-radius: 8px;
  margin-bottom: 24px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.04);
}

.course-title {
  font-size: 20px;
  font-weight: 600;
  color: #303133;
  margin-bottom: 12px;
}

.course-meta {
  color: #606266;
  font-size: 14px;
}

.course-meta span {
  margin-right: 24px;
}

.course-meta i {
  margin-right: 4px;
  color: #409EFF;
}

.chapter-card {
  margin-bottom: 24px;
}

.chapter-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.chapter-title {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
}

.video-count {
  font-size: 13px;
  color: #909399;
}

.video-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
  padding: 10px 0;
}

.video-item {
  background: #f8f9fa;
  border-radius: 8px;
  overflow: hidden;
  transition: all 0.3s;
  cursor: pointer;
}

.video-item:hover {
  transform: translateY(-2px);
  box-shadow: 0 4px 12px rgba(0,0,0,0.1);
}

.video-thumbnail {
  width: 100%;
  height: 168px;
  background: #eee;
  position: relative;
  overflow: hidden;
}

.video-thumbnail img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.video-thumbnail i {
  position: absolute;
  top: 50%;
  left: 50%;
  transform: translate(-50%, -50%);
  font-size: 40px;
  color: #909399;
}

.video-info {
  padding: 12px;
}

.video-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 8px;
  line-height: 1.4;
}

.video-meta {
  font-size: 13px;
  color: #909399;
}

.video-meta span {
  display: block;
  margin-bottom: 4px;
}

.video-actions {
  padding: 8px 12px;
  border-top: 1px solid #ebeef5;
  display: flex;
  justify-content: flex-end;
  gap: 8px;
  background: #fff;
}

.video-preview-dialog {
  .video-player {
    width: 100%;
    background: #000;
    video {
      width: 100%;
      max-height: 450px;
    }
  }
  .video-info-panel {
    padding: 16px 0;
    h3 {
      margin: 0 0 12px 0;
      font-size: 16px;
      color: #303133;
    }
    p {
      margin: 8px 0;
      color: #606266;
      font-size: 14px;
    }
  }
}

.active-video {
  border: 2px solid #409EFF;
  box-shadow: 0 0 8px #409EFF33;
}

.video-form-dialog {
  .el-form {
    padding: 20px;
  }
}
</style> 