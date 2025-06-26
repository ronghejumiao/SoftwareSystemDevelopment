<template>
  <div class="video-play-container">
    <el-card v-if="videoDetail" class="video-card">
      <div class="video-player">
        <video
          v-if="videoDetail.videoPath"
          ref="videoPlayer"
          :src="baseUrl + videoDetail.videoPath"
          controls
          autoplay
          @play="onVideoPlay"
          @pause="onVideoPause"
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
import { getLearningRecordByUserAndCourse, addLearningRecord } from "@/api/system/learningRecord";
import { addRecord as addVideoRecord, updateRecord as updateVideoRecordApi, listRecord as listVideoRecords } from "@/api/system/videoLearningRecord";

export default {
  name: "VideoPlay",
  data() {
    return {
      videoDetail: null,
      baseUrl: process.env.VUE_APP_BASE_API,
      videoList: [],
      currentIndex: -1,
      learningRecordId: null,
      videoRecord: null, // Stores the video learning record from the DB
      updateInterval: null, // To hold the setInterval ID
      videoElement: null
    };
  },
  created() {
    const videoId = this.$route.params.videoId;
    if (videoId) {
      this.getVideoDetails(videoId);
    }
    window.addEventListener('beforeunload', this.handleBeforeUnload);
  },
  beforeDestroy() {
    // Component is being destroyed, save final progress
    this.updateWatchRecord(true);
    // Clean up interval and event listener
    if (this.updateInterval) {
      clearInterval(this.updateInterval);
    }
    window.removeEventListener('beforeunload', this.handleBeforeUnload);
  },
  watch: {
    '$route.params.videoId'(newId, oldId) {
      if (newId && newId !== oldId) {
        // Before fetching new video, save progress of the old one
        this.updateWatchRecord(true);
        if (this.updateInterval) {
          clearInterval(this.updateInterval);
          this.updateInterval = null;
        }
        // Reset state for the new video
        this.videoDetail = null;
        this.videoRecord = null;
        this.learningRecordId = null;
        this.videoElement = null;
        this.getVideoDetails(newId);
      }
    }
  },
  methods: {
    getVideoDetails(videoId) {
      getVideoresource(videoId).then(res => {
        this.videoDetail = res.data;
        if (this.videoDetail && this.videoDetail.courseId) {
          listVideoresource({ courseId: this.videoDetail.courseId, pageSize: 999 }).then(listRes => {
            this.videoList = listRes.rows || [];
            this.currentIndex = this.videoList.findIndex(v => v.videoId == videoId);
          });
          // Start the process of getting/creating a learning record
          this.getOrCreateVideoRecord();
          }
        });
  },
    goBack() {
      if (this.videoDetail && this.videoDetail.courseId) {
        this.$router.push({
          name: 'CourseDetailDyn',
          params: { courseId: this.videoDetail.courseId },
          query: { tab: 'videos' }
        });
      } else {
        this.$router.push({ name: 'CourseDetail' });
      }
    },
    playPrev() {
      // 切换前彻底清理定时器和状态，防止重复提交
      if (this.updateInterval) {
        clearInterval(this.updateInterval);
        this.updateInterval = null;
      }
      this.videoElement = null;
      this.videoRecord = null;
      this.learningRecordId = null;
      // 切换到上一个视频
      if (this.currentIndex > 0) {
        const prevVideo = this.videoList[this.currentIndex - 1];
        this.$router.push({ name: "VideoPlay", params: { videoId: prevVideo.videoId } });
      }
    },
    playNext() {
      // 切换前彻底清理定时器和状态，防止重复提交
      if (this.updateInterval) {
        clearInterval(this.updateInterval);
        this.updateInterval = null;
      }
      this.videoElement = null;
      this.videoRecord = null;
      this.learningRecordId = null;
      // 切换到下一个视频
      if (this.currentIndex < this.videoList.length - 1) {
        const nextVideo = this.videoList[this.currentIndex + 1];
        this.$router.push({ name: "VideoPlay", params: { videoId: nextVideo.videoId } });
      }
    },
    handleEnded() {
      // 结束时先清理定时器，防止重复提交
      if (this.updateInterval) {
        clearInterval(this.updateInterval);
        this.updateInterval = null;
      }
      // Mark as fully watched and save progress
      if (this.videoElement) {
        this.videoElement.currentTime = this.videoElement.duration;
      }
      this.updateWatchRecord(true);
      // 自动切换下一个视频前，清理状态
      this.videoElement = null;
      this.videoRecord = null;
      this.learningRecordId = null;
      // Automatically play the next video
      if (this.currentIndex < this.videoList.length - 1) {
        this.playNext();
      }
    },
    selectVideo(index) {
      const video = this.videoList[index];
      this.$router.push({ name: "VideoPlay", params: { videoId: video.videoId } });
    },
    async prepareLearningRecord() {
      if (this.learningRecordId) return; // Already prepared
      if (!this.videoDetail || !this.videoDetail.courseId) return;

      const userId = this.$store.getters.id;
      if (!userId) return;

      try {
        let rec = await getLearningRecordByUserAndCourse(userId, this.videoDetail.courseId);
        if (!rec) {
          const newRecord = await addLearningRecord({ userId, courseId: this.videoDetail.courseId });
          // The creation might not return the full object, so we query again.
          // This depends on the backend implementation of addLearningRecord.
          // For now, let's assume we need to re-query.
          rec = await getLearningRecordByUserAndCourse(userId, this.videoDetail.courseId);
        }
        if (rec) {
          this.learningRecordId = rec.recordId;
        }
      } catch (error) {
        console.error("Error preparing learning record:", error);
      }
    },
    async getOrCreateVideoRecord() {
      await this.prepareLearningRecord();
      if (!this.learningRecordId) {
        console.error("Could not obtain learningRecordId. Video tracking disabled.");
        return;
      }
      const payload = {
        learningRecordId: this.learningRecordId,
        resourceId: this.videoDetail.videoId,
        userId: this.$store.getters.id,
        totalDuration: Math.round(this.videoDetail.duration || this.videoElement?.duration || 0),
        watchedDuration: 0,
        completionRate: 0,
        lastWatchTime: new Date()
      };
      try {
        // Use addRecord, which now acts as get-or-create and returns the full record object
        const response = await addVideoRecord(payload);
        this.videoRecord = response.data;
        // Set current time of video if it was previously watched
        if (this.videoElement && this.videoRecord && this.videoRecord.watchedDuration > 0) {
            const seekTime = this.videoRecord.watchedDuration;
            if (isFinite(seekTime)) {
               this.videoElement.currentTime = seekTime;
            }
        }
        // 立即刷新 videoElement
        this.videoElement = this.$refs.videoPlayer;
        this.onVideoPlay();
      } catch(e) {
         console.error("Failed to get or create video record", e);
      }
    },
    updateWatchRecord(forceUpdate = false) {
      // 兜底日志
      if (!this.videoRecord) {
        console.warn('videoRecord为空，无法刷新');
        return;
      }
      // 兜底赋值
      if (!this.videoElement) {
        this.videoElement = this.$refs.videoPlayer;
        // 再兜底一次
        if (!this.videoElement) {
          // 尝试延迟获取
          setTimeout(() => {
            this.videoElement = this.$refs.videoPlayer;
          }, 500);
          console.error('videoElement为空，无法刷新');
          return;
        }
      }
      // 关键日志
      console.log('[视频进度刷新]', {
        recordId: this.videoRecord.recordId,
        watchedDuration: this.videoElement.currentTime,
        totalDuration: this.videoElement.duration
      });
      const currentTime = Math.round(this.videoElement.currentTime);
      const totalDuration = Math.round(this.videoElement.duration);
      // 只要在播放中，每5秒都更新
      this.videoRecord.watchedDuration = currentTime;
      if (totalDuration > 0) {
        this.videoRecord.totalDuration = totalDuration;
        this.videoRecord.completionRate = Math.round((currentTime / totalDuration) * 100);
      }
      // 每次都用当前时间
      this.videoRecord.lastWatchTime = new Date();
      // 只有有recordId才发送
      if (this.videoRecord.recordId) {
        updateVideoRecordApi(this.videoRecord).then(() => {
          this.$root.$emit('videoRecordUpdated');
        });
      } else {
        console.warn('recordId为空，未发送更新');
      }
    },

    handleBeforeUnload(event) {
        // Synchronously update progress before the page unloads.
        // Note: Modern browsers may limit or ignore async operations here.
        this.updateWatchRecord(true);
    },
    
    // Video Player Event Handlers
    onVideoPlay() {
      // 兜底赋值
      this.videoElement = this.$refs.videoPlayer;
      if (!this.videoElement) {
        setTimeout(() => {
          this.videoElement = this.$refs.videoPlayer;
        }, 500);
      }
      if (this.updateInterval) clearInterval(this.updateInterval);
      // 每5秒刷新一次
      this.updateInterval = setInterval(() => this.updateWatchRecord(), 5000);
    },
    onVideoPause() {
      // When paused, save progress immediately
      clearInterval(this.updateInterval);
      this.updateWatchRecord(true);
    },

    initVideoEvents() {
        if (!this.videoElement) return;
        this.videoElement.addEventListener('play', this.onVideoPlay);
        this.videoElement.addEventListener('pause', this.onVideoPause);
        this.videoElement.addEventListener('loadedmetadata', () => {
             // When metadata is loaded, we can get duration
             if (this.videoRecord) {
                 this.videoRecord.totalDuration = Math.round(this.videoElement.duration);
                 // Seek to last watched position
                if (this.videoRecord.watchedDuration > 0 && this.videoRecord.watchedDuration < this.videoElement.duration) {
                    this.videoElement.currentTime = this.videoRecord.watchedDuration;
                }
             }
        });
    }
  },
  mounted() {
    this.$nextTick(() => {
      this.videoElement = this.$refs.videoPlayer;
      if (this.videoElement) {
        this.initVideoEvents();
        // 如果视频已在播放，立即启动定时器
        if (!this.videoElement.paused) {
          this.onVideoPlay();
        }
      }
    });
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