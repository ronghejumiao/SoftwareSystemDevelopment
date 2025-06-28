<template>
  <div class="app-container">
    <div class="course-info">
      <div class="course-title">{{ course.courseName }}</div>
      <div class="course-meta">
        <span><i class="el-icon-collection"></i> 编号：{{ course.courseCode }}</span>
        <span><i class="el-icon-user"></i> 教师：{{ course.teacherName || '未知' }}</span>
      </div>
    </div>

    <el-row :gutter="10" class="mb8" v-if="isTeacherOrAdmin">
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
          <div class="video-actions" v-if="isTeacherOrAdmin">
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
  </div>
</template>

<script>
import { getCourse } from "@/api/system/course";
import { listVideoresource, getVideoresource, delVideoresource } from "@/api/system/videoresource";

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
      activeVideoId: null
    };
  },
  computed: {
    isTeacherOrAdmin() {
      const roles = this.$store.getters.roles || [];
      return roles.includes('admin') || roles.includes('teacher');
    }
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
      getCourse(courseId).then(response => {
        this.course = response.data;
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
    handleAdd() {
      this.$router.push({ 
        name: 'VideoAdd',
        query: { 
          courseId: this.course.courseId,
          courseCode: this.course.courseCode
        }
      });
    },
    handleUpdate(video) {
      this.$router.push({ 
        name: 'VideoEdit',
        query: { 
          videoId: video.videoId,
          courseId: this.course.courseId
        }
      });
    },
    handleDelete(video) {
      const videoId = video.videoId;
      this.$modal.confirm('是否确认删除该视频？').then(function() {
        return delVideoresource(videoId);
      }).then(() => {
        this.getVideoList(this.course.courseId);
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
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
</style> 