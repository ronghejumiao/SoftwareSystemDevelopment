<template>
  <div class="video-play-container">
    <el-card v-if="videoDetail" class="video-card">
      <div class="video-player">
        <video
          v-if="videoDetail.videoPath"
          :src="baseUrl + videoDetail.videoPath"
          controls
          autoplay
          @ended="handleEnded"
          style="width: 100%; max-height: 70vh; background: #000;"
        ></video>
        <div v-else class="no-video">暂无视频资源</div>
      </div>
      <div class="video-info">
        <h2>{{ videoDetail.description || '未命名视频' }}</h2>
        <div class="video-meta">
          <span>章节：{{ videoDetail.videoName || '-' }}</span>
          <span>大小：{{ videoDetail.fileSize ? videoDetail.fileSize + 'MB' : '-' }}</span>
        </div>
        <el-button type="primary" icon="el-icon-back" @click="goBack">返回课程</el-button>
        <el-button
          type="info"
          icon="el-icon-arrow-left"
          :disabled="currentIndex <= 0"
          @click="playPrev"
          style="margin-left: 12px;"
        >上一集</el-button>
        <el-button
          type="info"
          icon="el-icon-arrow-right"
          :disabled="currentIndex >= videoList.length - 1"
          @click="playNext"
          style="margin-left: 8px;"
        >下一集</el-button>
      </div>
      <div class="video-list-panel" v-if="videoList.length > 1">
        <div class="video-list-title">全部视频</div>
        <ul class="video-list-ul">
          <li
            v-for="(video, idx) in videoList"
            :key="video.videoId"
            :class="{ active: idx === currentIndex }"
            @click="selectVideo(idx)"
          >
            {{ idx + 1 }}. {{ video.description || video.videoName || '未命名' }}
          </li>
        </ul>
      </div>
    </el-card>
    <el-empty v-else description="未找到视频信息" />
  </div>
</template>

<script>
import { getVideoresource, listVideoresource } from "@/api/system/videoresource";
export default {
  name: "VideoPlay",
  data() {
    return {
      videoDetail: null,
      baseUrl: process.env.VUE_APP_BASE_API,
      videoList: [],
      currentIndex: -1
    };
  },
  created() {
    const videoId = this.$route.params.videoId;
    if (videoId) {
      getVideoresource(videoId).then(res => {
        this.videoDetail = res.data;
        // 获取同课程下所有视频
        if (this.videoDetail && this.videoDetail.courseId) {
          listVideoresource({ courseId: this.videoDetail.courseId, pageSize: 999 }).then(listRes => {
            this.videoList = listRes.rows || [];
            this.currentIndex = this.videoList.findIndex(v => v.videoId == videoId);
          });
        }
      });
    }
  },
  watch: {
    '$route.params.videoId'(newId) {
      // 监听路由变化，切换视频
      if (newId) {
        getVideoresource(newId).then(res => {
          this.videoDetail = res.data;
          if (this.videoList.length > 0) {
            this.currentIndex = this.videoList.findIndex(v => v.videoId == newId);
          }
        });
      }
    }
  },
  methods: {
    goBack() {
      if (this.videoDetail && this.videoDetail.courseId) {
        this.$router.push({
          name: 'CourseDetail',
          params: { courseId: this.videoDetail.courseId },
          query: { tab: 'videos' }
        });
      } else {
        this.$router.push({ name: 'CourseDetail' });
      }
    },
    playPrev() {
      if (this.currentIndex > 0) {
        const prevVideo = this.videoList[this.currentIndex - 1];
        this.$router.push({ name: "VideoPlay", params: { videoId: prevVideo.videoId } });
      }
    },
    playNext() {
      if (this.currentIndex < this.videoList.length - 1) {
        const nextVideo = this.videoList[this.currentIndex + 1];
        this.$router.push({ name: "VideoPlay", params: { videoId: nextVideo.videoId } });
      }
    },
    handleEnded() {
      // 视频播放结束自动播放下一个
      if (this.currentIndex < this.videoList.length - 1) {
        this.playNext();
      }
    },
    selectVideo(index) {
      const video = this.videoList[index];
      this.$router.push({ name: "VideoPlay", params: { videoId: video.videoId } });
    }
  }
};
</script>

<style scoped>
.video-play-container {
  max-width: 900px;
  margin: 0 auto;
  padding: 32px 0;
}
.video-card {
  padding: 24px;
}
.video-player {
  margin-bottom: 24px;
  background: #000;
  border-radius: 8px;
  overflow: hidden;
}
.no-video {
  color: #909399;
  text-align: center;
  padding: 60px 0;
  background: #f5f7fa;
}
.video-info {
  padding: 0 8px;
}
.video-info h2 {
  font-size: 22px;
  font-weight: 600;
  margin-bottom: 12px;
}
.video-meta {
  color: #909399;
  font-size: 14px;
  margin-bottom: 18px;
  display: flex;
  gap: 24px;
}
.video-list-panel {
  margin-top: 32px;
  background: #f8fafd;
  border-radius: 8px;
  padding: 16px 20px;
}
.video-list-title {
  font-weight: 600;
  color: #1890ff;
  margin-bottom: 10px;
}
.video-list-ul {
  list-style: none;
  padding: 0;
  margin: 0;
}
.video-list-ul li {
  padding: 8px 0;
  cursor: pointer;
  color: #333;
  border-bottom: 1px solid #f0f0f0;
  transition: background 0.2s;
}
.video-list-ul li:last-child {
  border-bottom: none;
}
.video-list-ul li.active {
  color: #1890ff;
  font-weight: bold;
  background: #e6f7ff;
}
</style> 