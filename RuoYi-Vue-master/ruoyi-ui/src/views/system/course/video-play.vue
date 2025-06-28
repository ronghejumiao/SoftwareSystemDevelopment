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
      videoElement: null,
      isUpdating: false, // 防止重复更新
      lastUpdateTime: 0, // 上次更新时间
      isDestroyed: false // 组件销毁标记
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
    // 设置销毁标记
    this.isDestroyed = true;
    
    // 组件销毁时清理所有资源
    this.cleanupCurrentVideo();
    window.removeEventListener('beforeunload', this.handleBeforeUnload);
    
    // 重置所有状态
    this.isUpdating = false;
    this.lastUpdateTime = 0;
  },
  watch: {
    '$route.params.videoId'(newId, oldId) {
      if (newId && newId !== oldId) {
        console.log('[路由变化] 从视频', oldId, '切换到视频', newId);
        
        // 立即清理当前定时器和事件监听器
        this.cleanupCurrentVideo();
        
        // 重置状态
        this.resetVideoState();
        
        // 获取新视频详情
        this.getVideoDetails(newId);
      }
    }
  },
  methods: {
    // 清理当前视频的定时器和事件监听器
    cleanupCurrentVideo() {
      console.log('[清理视频资源] 开始清理当前视频');
      
      // 清理定时器
      if (this.updateInterval) {
        clearInterval(this.updateInterval);
        this.updateInterval = null;
        console.log('[清理视频资源] 定时器已清理');
      }
      
      // 清理视频元素事件监听器
      if (this.videoElement) {
        try {
          this.videoElement.removeEventListener('play', this.onVideoPlay);
          this.videoElement.removeEventListener('pause', this.onVideoPause);
          this.videoElement.removeEventListener('loadedmetadata', this.onLoadedMetadata);
          // 注意：ended事件在模板中已绑定，不需要手动清理
          console.log('[清理视频资源] 事件监听器已清理');
        } catch (error) {
          console.warn('[清理视频资源] 清理事件监听器时出错:', error);
        }
        this.videoElement = null;
      }
      
      // 保存当前视频进度（如果有）
      if (this.videoRecord) {
        try {
          this.updateWatchRecord(true);
          console.log('[清理视频资源] 已保存视频进度');
        } catch (error) {
          console.warn('[清理视频资源] 保存视频进度时出错:', error);
        }
      }
      
      // 重置更新状态
      this.isUpdating = false;
      this.lastUpdateTime = 0;
      
      console.log('[清理视频资源] 清理完成');
    },
    
    // 重置视频状态
    resetVideoState() {
      this.videoDetail = null;
      this.videoRecord = null;
      this.learningRecordId = null;
      this.currentIndex = -1;
    },
    
    // 视频元数据加载完成事件处理
    onLoadedMetadata() {
      if (this.videoRecord && this.videoElement) {
        this.videoRecord.totalDuration = Math.round(this.videoElement.duration);
        // 跳转到上次观看位置
        if (this.videoRecord.watchedDuration > 0 && this.videoRecord.watchedDuration < this.videoElement.duration) {
          this.videoElement.currentTime = this.videoRecord.watchedDuration;
        }
      }
    },

    getVideoDetails(videoId) {
      getVideoresource(videoId).then(res => {
        this.videoDetail = res.data;
        if (this.videoDetail && this.videoDetail.courseId) {
          listVideoresource({ courseId: this.videoDetail.courseId, pageSize: 999 }).then(listRes => {
            this.videoList = listRes.rows || [];
            this.currentIndex = this.videoList.findIndex(v => v.videoId == videoId);
          });
          // 开始获取/创建学习记录
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
      // 检查组件是否已被销毁
      if (this.$isDestroyed || this.isDestroyed) {
        console.log('[视频播放结束] 组件已销毁，跳过处理');
        return;
      }
      
      console.log('[视频播放结束] 开始处理自动跳转');
      
      // 先保存当前视频的观看进度
      if (this.videoRecord) {
        // 确保视频记录标记为完全观看
        if (this.videoElement && this.videoElement.duration) {
          this.videoRecord.watchedDuration = this.videoElement.duration;
          this.videoRecord.completionRate = 100;
        }
        this.updateWatchRecord(true);
      }
      
      // 检查是否还有下一个视频
      if (this.currentIndex < this.videoList.length - 1) {
        console.log('[视频播放结束] 准备跳转到下一个视频');
        
        // 延迟跳转，确保当前视频的状态已保存
        setTimeout(() => {
          // 再次检查组件是否还存在（防止组件已被销毁）
          if (this.$el && !this.$isDestroyed) {
            const nextVideo = this.videoList[this.currentIndex + 1];
            console.log('[视频播放结束] 跳转到下一个视频:', nextVideo.videoId);
            this.$router.push({ 
              name: "VideoPlay", 
              params: { videoId: nextVideo.videoId } 
            });
          }
        }, 1500); // 增加延迟时间，确保状态保存完成
      } else {
        console.log('[视频播放结束] 已是最后一个视频，不进行跳转');
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
        
        // 等待DOM更新完成后再设置视频元素
        this.$nextTick(() => {
          this.videoElement = this.$refs.videoPlayer;
          if (this.videoElement) {
            // Set current time of video if it was previously watched
            if (this.videoRecord && this.videoRecord.watchedDuration > 0) {
              const seekTime = this.videoRecord.watchedDuration;
              if (isFinite(seekTime)) {
                this.videoElement.currentTime = seekTime;
              }
            }
            // 初始化事件监听器
            this.initVideoEvents();
            // 如果视频已在播放，立即启动定时器
            if (!this.videoElement.paused) {
              this.onVideoPlay();
            }
          }
        });
      } catch(e) {
        console.error("Failed to get or create video record", e);
      }
    },
    updateWatchRecord(forceUpdate = false) {
      // 检查组件是否已被销毁
      if (this.$isDestroyed || this.isDestroyed) {
        console.log('[视频进度刷新] 组件已销毁，跳过更新');
        return;
      }
      
      // 兜底日志
      if (!this.videoRecord) {
        console.warn('videoRecord为空，无法刷新');
        return;
      }
      
      // 尝试获取视频元素
      if (!this.videoElement) {
        this.videoElement = this.$refs.videoPlayer;
        if (!this.videoElement) {
          console.warn('videoElement为空，无法刷新 - 可能是视频还未加载或组件已销毁');
          return;
        }
      }
      
      // 检查视频元素是否有效
      if (!this.videoElement.duration || this.videoElement.duration === Infinity || this.videoElement.duration === NaN) {
        console.warn('视频元素无效，duration:', this.videoElement.duration);
        return;
      }
      
      // 防抖机制：如果不是强制更新，且距离上次更新不足3秒，则跳过
      const now = Date.now();
      if (!forceUpdate && now - this.lastUpdateTime < 3000) {
        return;
      }
      
      // 如果正在更新中，跳过本次更新
      if (this.isUpdating && !forceUpdate) {
        return;
      }
      
      // 关键日志
      console.log('[视频进度刷新]', {
        recordId: this.videoRecord.recordId,
        watchedDuration: this.videoElement.currentTime,
        totalDuration: this.videoElement.duration
      });
      
      const currentTime = Math.round(this.videoElement.currentTime);
      const totalDuration = Math.round(this.videoElement.duration);
      
      // 更新记录数据
      this.videoRecord.watchedDuration = currentTime;
      if (totalDuration > 0) {
        this.videoRecord.totalDuration = totalDuration;
        this.videoRecord.completionRate = Math.round((currentTime / totalDuration) * 100);
      }
      this.videoRecord.lastWatchTime = new Date();
      
      // 只有有recordId才发送
      if (this.videoRecord.recordId) {
        this.isUpdating = true;
        this.lastUpdateTime = now;
        
        // 添加防重复提交标记
        updateVideoRecordApi(this.videoRecord, { headers: { repeatSubmit: false } }).then(() => {
          this.$root.$emit('videoRecordUpdated');
          this.isUpdating = false;
        }).catch(error => {
          console.warn('更新视频记录失败:', error);
          this.isUpdating = false;
        });
      } else {
        console.warn('recordId为空，未发送更新');
      }
    },

    handleBeforeUnload(event) {
      // 页面卸载前同步更新进度
      this.updateWatchRecord(true);
    },
    
    // Video Player Event Handlers
    onVideoPlay() {
      // 清理之前的定时器
      if (this.updateInterval) {
        clearInterval(this.updateInterval);
      }
      // 每10秒刷新一次，减少请求频率
      this.updateInterval = setInterval(() => this.updateWatchRecord(), 10000);
    },
    
    onVideoPause() {
      // 暂停时立即保存进度并清理定时器
      clearInterval(this.updateInterval);
      this.updateInterval = null;
      this.updateWatchRecord(true);
    },

    initVideoEvents() {
      if (!this.videoElement) {
        console.warn('[初始化视频事件] videoElement为空');
        return;
      }
      
      console.log('[初始化视频事件] 开始添加事件监听器');
      
      // 移除之前的事件监听器（如果存在）
      try {
        this.videoElement.removeEventListener('play', this.onVideoPlay);
        this.videoElement.removeEventListener('pause', this.onVideoPause);
        this.videoElement.removeEventListener('loadedmetadata', this.onLoadedMetadata);
        // 注意：ended事件在模板中已绑定，不需要手动添加
      } catch (error) {
        console.warn('[初始化视频事件] 清理旧事件监听器时出错:', error);
      }
      
      // 添加新的事件监听器
      try {
        this.videoElement.addEventListener('play', this.onVideoPlay);
        this.videoElement.addEventListener('pause', this.onVideoPause);
        this.videoElement.addEventListener('loadedmetadata', this.onLoadedMetadata);
        // 注意：ended事件在模板中已绑定，不需要手动添加
        console.log('[初始化视频事件] 事件监听器添加成功');
      } catch (error) {
        console.error('[初始化视频事件] 添加事件监听器时出错:', error);
      }
    }
  },
  mounted() {
    // 只在组件首次挂载时添加beforeunload事件监听器
    window.addEventListener('beforeunload', this.handleBeforeUnload);
  },
  beforeDestroy() {
    // 组件销毁时清理所有资源
    this.cleanupCurrentVideo();
    window.removeEventListener('beforeunload', this.handleBeforeUnload);
    
    // 重置所有状态
    this.isUpdating = false;
    this.lastUpdateTime = 0;
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