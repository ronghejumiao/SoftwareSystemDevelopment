<template>
  <div class="video-play-container">
    <!-- 
    视频播放组件 - 修复版本
    修复问题：
    1. 视频续播功能被误判为跳过片段的问题
    2. 重复观看片段被重复添加的问题
    3. 添加了智能去重机制和时间间隔检查
    
    新增功能：
    1. 初始续播标记，避免续播跳转被误判
    2. 智能片段去重，避免重复添加相同片段
    3. 时间间隔检查，避免短时间内重复添加片段
    -->
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
      isDestroyed: false, // 组件销毁标记
      // 用于跟踪观看行为
      watchedSegments: [], // 已观看的片段 [start, end]
      skipSegments: [], // 跳过的片段 [start, end]
      repeatSegments: [], // 重复观看的片段 [start, end]
      lastSeekTime: 0, // 上次跳转的时间
      isSeeking: false, // 是否正在跳转
      currentWatchStart: 0, // 当前观看片段的开始时间
      isWatching: false, // 是否正在观看
      isRevisitingSkippedSegments: false, // 新增：检测是否重新观看跳过片段
      isInitialSeek: false, // 新增：标记是否为初始续播跳转
      initialSeekTime: 0, // 新增：记录初始跳转时间
      lastSegmentAddTime: 0, // 新增：记录上次添加片段的时间
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
          this.videoElement.removeEventListener('seeking', this.onVideoSeek);
          this.videoElement.removeEventListener('seeked', this.onVideoSeek);
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
          this.updateWatchRecord(true, true);
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
      // 重置跟踪状态
      this.watchedSegments = [];
      this.skipSegments = [];
      this.repeatSegments = [];
      this.lastSeekTime = 0;
      this.isSeeking = false;
      this.currentWatchStart = 0;
      this.isWatching = false;
      this.isRevisitingSkippedSegments = false;
      this.isInitialSeek = false;
      this.initialSeekTime = 0;
      this.lastSegmentAddTime = 0;
    },
    
    // 视频元数据加载完成事件处理
    onLoadedMetadata() {
      if (this.videoRecord && this.videoElement) {
        this.videoRecord.totalDuration = Math.round(this.videoElement.duration);
        // 跳转到上次观看位置
        if (this.videoRecord.watchedDuration > 0 && this.videoRecord.watchedDuration < this.videoElement.duration) {
          // 标记为初始续播跳转，避免被误判为跳过片段
          this.isInitialSeek = true;
          this.initialSeekTime = this.videoRecord.watchedDuration;
          
          this.videoElement.currentTime = this.videoRecord.watchedDuration;
          this.lastSeekTime = this.videoRecord.watchedDuration;
          this.currentWatchStart = this.videoRecord.watchedDuration;
          
          console.log('[视频续播] 跳转到上次观看位置:', this.videoRecord.watchedDuration);
        } else {
          this.lastSeekTime = 0;
          this.currentWatchStart = 0;
          this.isInitialSeek = false;
          this.initialSeekTime = 0;
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
        this.updateWatchRecord(true, true);
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
      
      // 先尝试获取已存在的记录
      try {
        const existingRecords = await listVideoRecords({
          learningRecordId: this.learningRecordId,
          resourceId: this.videoDetail.videoId,
          pageSize: 1
        });
        
        if (existingRecords.rows && existingRecords.rows.length > 0) {
          // 找到已存在的记录，使用它
          this.videoRecord = existingRecords.rows[0];
          console.log('[获取已存在记录]', this.videoRecord);
          
          // 初始化跳过片段和重复观看片段状态
          if (this.videoRecord.skipSegments && this.videoRecord.skipSegments.trim()) {
            this.skipSegments = this.videoRecord.skipSegments.split(',').filter(s => s.trim());
            console.log('[初始化跳过片段]', this.skipSegments);
          }
          if (this.videoRecord.repeatSegments && this.videoRecord.repeatSegments.trim()) {
            this.repeatSegments = this.videoRecord.repeatSegments.split(',').filter(s => s.trim());
            console.log('[初始化重复观看片段]', this.repeatSegments);
          }
        } else {
          // 没有找到记录，创建新记录
          const payload = {
            learningRecordId: this.learningRecordId,
            resourceId: this.videoDetail.videoId,
            userId: this.$store.getters.id,
            totalDuration: Math.round(this.videoDetail.duration || 0),
            watchedDuration: 0,
            completionRate: 0,
            skipSegments: '',
            repeatSegments: '',
            // lastWatchTime 由后端自动生成
          };
          
          const response = await addVideoRecord(payload);
          this.videoRecord = response.data;
          console.log('[创建新记录]', this.videoRecord);
        }
        
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
    updateWatchRecord(forceUpdate = false, isEnd = false) {
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
      
      const currentTime = Math.round(this.videoElement.currentTime);
      const totalDuration = Math.round(this.videoElement.duration);
      
      // 更新记录数据
      this.videoRecord.watchedDuration = currentTime;
      if (totalDuration > 0) {
        this.videoRecord.totalDuration = totalDuration;
        this.videoRecord.completionRate = Math.round((currentTime / totalDuration) * 100);
      }
      
      // 更新跳过片段和重复观看片段
      // 先处理跳过片段
      console.log('[片段处理] 开始处理跳过片段，原始数量:', this.skipSegments.length);
      if (this.skipSegments.length > 0) {
        // 使用智能跳过片段管理器
        this.smartSkipSegmentManager();
        
        // 去重并排序
        const uniqueSkipSegments = this.removeDuplicateSegments(this.skipSegments);
        this.videoRecord.skipSegments = uniqueSkipSegments.join(',');
        console.log('[保存跳过片段] 最终数量:', uniqueSkipSegments.length, '内容:', this.videoRecord.skipSegments);
      } else {
        // 确保空字符串被保存
        this.videoRecord.skipSegments = '';
        console.log('[保存跳过片段] 无跳过片段');
      }
      
      // 再处理重复观看片段
      console.log('[片段处理] 开始处理重复观看片段，原始数量:', this.repeatSegments.length);
      if (this.repeatSegments.length > 0) {
        // 使用智能重复观看片段管理器
        this.smartRepeatSegmentManager();
        
        // 去重并排序
        const uniqueRepeatSegments = this.removeDuplicateSegments(this.repeatSegments);
        this.videoRecord.repeatSegments = uniqueRepeatSegments.join(',');
        console.log('[保存重复观看片段] 最终数量:', uniqueRepeatSegments.length, '内容:', this.videoRecord.repeatSegments);
      } else {
        // 确保空字符串被保存
        this.videoRecord.repeatSegments = '';
        console.log('[保存重复观看片段] 无重复观看片段');
      }
      
      // 关键日志
      console.log('[视频进度刷新]', {
        recordId: this.videoRecord.recordId,
        watchedDuration: currentTime,
        totalDuration: totalDuration,
        skipSegments: this.videoRecord.skipSegments,
        repeatSegments: this.videoRecord.repeatSegments
      });
      
      // 只有有recordId才发送
      if (this.videoRecord.recordId) {
        this.isUpdating = true;
        this.lastUpdateTime = now;
        
        // 传递 isEnd 参数
        const data = { ...this.videoRecord };
        if (isEnd) {
          if (!data.params) data.params = {};
          data.params.isEnd = true;
        }
        updateVideoRecordApi(data, { headers: { repeatSubmit: false } }).then(() => {
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
      this.updateWatchRecord(true, true);
    },
    
    // 处理视频跳转事件
    onVideoSeek() {
      if (!this.videoElement || this.isInitialSeek) {
        return;
      }
      
      const currentTime = this.videoElement.currentTime;
      const timeDiff = currentTime - this.lastSeekTime;
      const now = Date.now();
      
      // 如果是初始续播跳转，跳过检测
      if (this.isInitialSeek && Math.abs(currentTime - this.initialSeekTime) < 1) {
        console.log('[视频续播] 跳过初始续播跳转检测');
        this.isInitialSeek = false; // 清除标记
        this.lastSeekTime = currentTime;
        this.currentWatchStart = currentTime;
        return;
      }
      
      // 避免在短时间内重复添加相同的片段（5秒内）
      if (now - this.lastSegmentAddTime < 5000) {
        console.log('[视频跳转] 距离上次添加片段时间过短，跳过检测');
        this.lastSeekTime = currentTime;
        this.currentWatchStart = currentTime;
        return;
      }
      
      // 如果时间倒流（当前时间小于上次时间），说明是重复观看
      if (timeDiff < -2) {
        const repeatStart = currentTime;
        const repeatEnd = this.lastSeekTime;
        
        // 检查这个重复观看片段是否已经记录过
        const repeatSegment = `${repeatStart}-${repeatEnd}`;
        const isAlreadyRepeated = this.repeatSegments.some(segment => {
          const [start, end] = segment.split('-').map(Number);
          return Math.abs(start - repeatStart) < 3 && Math.abs(end - repeatEnd) < 3;
        });
        
        if (!isAlreadyRepeated) {
          this.repeatSegments.push(repeatSegment);
          this.lastSegmentAddTime = now;
          console.log('[重复观看片段]', repeatSegment);
          this.debugStatus();
        }
      }
      // 如果时间大幅前进（超过5秒），说明是跳过片段
      else if (timeDiff > 5) {
        const skipStart = this.lastSeekTime;
        const skipEnd = currentTime;
        
        // 检查这个跳过片段是否已经被观看覆盖
        const isWatched = this.watchedSegments.some(segment => {
          const [watchStart, watchEnd] = segment.split('-').map(Number);
          // 如果观看片段与跳过片段有重叠，说明这部分内容已经被观看
          const overlapStart = Math.max(skipStart, watchStart);
          const overlapEnd = Math.min(skipEnd, watchEnd);
          // 只有当重叠时间超过跳过片段总时长的80%时才认为是已观看
          const skipDuration = skipEnd - skipStart;
          const overlapDuration = overlapEnd - overlapStart;
          return overlapDuration > skipDuration * 0.8; // 重叠超过80%认为是已观看
        });
        
        // 只有未被观看的部分才记录为跳过片段
        if (!isWatched) {
          // 检查这个跳过片段是否已经记录过
          const skipSegment = `${skipStart}-${skipEnd}`;
          const isAlreadySkipped = this.skipSegments.some(segment => {
            const [start, end] = segment.split('-').map(Number);
            return Math.abs(start - skipStart) < 3 && Math.abs(end - skipEnd) < 3;
          });
          
          if (!isAlreadySkipped) {
            this.skipSegments.push(skipSegment);
            this.lastSegmentAddTime = now;
            console.log('[跳过片段]', skipSegment);
            this.debugStatus();
          }
        } else {
          console.log('[跳过片段] 片段已被观看，跳过记录:', `${skipStart}-${skipEnd}`);
        }
      }
      
      this.lastSeekTime = currentTime;
      this.currentWatchStart = currentTime;
    },
    
    // 新增：检测是否重新观看跳过片段
    checkRevisitingSkippedSegments(currentTime) {
      if (this.skipSegments.length === 0) {
        return false;
      }
      
      // 检查当前时间是否在之前的跳过片段范围内
      for (const skipSegment of this.skipSegments) {
        const [skipStart, skipEnd] = skipSegment.split('-').map(Number);
        
        // 如果当前时间在跳过片段范围内，且距离跳过片段结束时间不超过10秒
        if (currentTime >= skipStart && currentTime <= skipEnd + 10) {
          const revisitSegment = `${skipStart}-${skipEnd}`;
          
          // 检查是否已经记录过这个重新观看片段
          const isAlreadyRevisited = this.repeatSegments.some(segment => {
            const [start, end] = segment.split('-').map(Number);
            return Math.abs(start - skipStart) < 3 && Math.abs(end - skipEnd) < 3;
          });
          
          // 检查这个片段是否已经被观看覆盖（避免将观看片段误判为重复观看）
          const isWatched = this.watchedSegments.some(segment => {
            const [watchStart, watchEnd] = segment.split('-').map(Number);
            // 如果观看片段完全覆盖了跳过片段，说明这不是重复观看
            return watchStart <= skipStart && watchEnd >= skipEnd;
          });
          
          if (!isAlreadyRevisited && !isWatched) {
            this.repeatSegments.push(revisitSegment);
            console.log('[重新观看跳过片段]', revisitSegment);
            return true; // 找到重新观看的片段
          } else {
            if (isWatched) {
              console.log('[重新观看跳过片段] 片段已被观看覆盖，跳过添加:', revisitSegment);
            } else {
              console.log('[重新观看跳过片段] 已存在，跳过添加:', revisitSegment);
            }
          }
        }
      }
      
      return false;
    },

    // 处理视频播放事件
    onVideoPlay() {
      // 清理之前的定时器
      if (this.updateInterval) {
        clearInterval(this.updateInterval);
      }
      // 每10秒刷新一次，减少请求频率
      this.updateInterval = setInterval(() => this.updateWatchRecord(), 10000);
      
      // 记录开始观看时间
      if (this.videoElement) {
        this.currentWatchStart = this.videoElement.currentTime;
        this.lastSeekTime = this.currentWatchStart;
        this.isWatching = true;
        
        // 清除初始续播标记
        if (this.isInitialSeek) {
          console.log('[视频播放] 清除初始续播标记');
          this.isInitialSeek = false;
        }
        
        // 检查是否在重新观看跳过片段
        this.checkRevisitingSkippedSegments(this.currentWatchStart);
      }
    },
    
    // 处理视频暂停事件
    onVideoPause() {
      // 暂停时立即保存进度并清理定时器
      clearInterval(this.updateInterval);
      this.updateInterval = null;
      this.updateWatchRecord(true);
      
      // 记录观看片段
      if (this.videoElement && this.isWatching) {
        const currentTime = this.videoElement.currentTime;
        
        // 确保时间顺序正确
        const startTime = Math.min(this.currentWatchStart, currentTime);
        const endTime = Math.max(this.currentWatchStart, currentTime);
        
        // 只有当观看时长超过1秒时才记录
        if (endTime - startTime > 1) {
          const watchSegment = `${startTime}-${endTime}`;
          
          // 检查是否已经记录过这个观看片段
          const isAlreadyWatched = this.watchedSegments.some(segment => {
            const [start, end] = segment.split('-').map(Number);
            return Math.abs(start - startTime) < 3 && Math.abs(end - endTime) < 3;
          });
          
          if (!isAlreadyWatched) {
            // 首次观看，记录为观看片段
            this.watchedSegments.push(watchSegment);
            console.log('[观看片段]', watchSegment);
            
            // 检查并更新跳过片段（移除被观看覆盖的部分）
            this.updateSkipSegmentsAfterWatching(startTime, endTime);
            
            this.debugStatus();
          }
        }
        
        this.isWatching = false;
      }
    },

    // 新增：根据观看片段更新跳过片段
    updateSkipSegmentsAfterWatching(watchStart, watchEnd) {
      if (this.skipSegments.length === 0) {
        return; // 没有跳过片段，无需更新
      }
      
      console.log('[更新跳过片段] 检查观看片段:', `${watchStart}-${watchEnd}`);
      
      const updatedSkipSegments = [];
      let hasChanges = false;
      
      for (const skipSegment of this.skipSegments) {
        const [skipStart, skipEnd] = skipSegment.split('-').map(Number);
        
        // 检查观看片段是否与跳过片段有重叠
        const overlapStart = Math.max(watchStart, skipStart);
        const overlapEnd = Math.min(watchEnd, skipEnd);
        
        if (overlapStart < overlapEnd) {
          // 有重叠，需要更新跳过片段
          hasChanges = true;
          
          // 计算新的跳过片段
          const newSkipSegments = [];
          
          // 如果跳过片段的开始部分没有被观看，保留这部分
          if (skipStart < overlapStart) {
            newSkipSegments.push(`${skipStart}-${overlapStart}`);
          }
          
          // 如果跳过片段的结束部分没有被观看，保留这部分
          if (overlapEnd < skipEnd) {
            newSkipSegments.push(`${overlapEnd}-${skipEnd}`);
          }
          
          // 只有当新的跳过片段时长超过2秒时才保留
          newSkipSegments.forEach(segment => {
            const [start, end] = segment.split('-').map(Number);
            if (end - start > 2) {
              updatedSkipSegments.push(segment);
            }
          });
          
          console.log('[更新跳过片段] 原片段:', skipSegment, '-> 新片段:', newSkipSegments);
        } else {
          // 没有重叠，保持原样
          updatedSkipSegments.push(skipSegment);
        }
      }
      
      if (hasChanges) {
        this.skipSegments = updatedSkipSegments;
        console.log('[更新跳过片段] 最终跳过片段:', this.skipSegments);
        
        // 更新重复观看片段，移除已被观看覆盖的部分
        this.updateRepeatSegmentsAfterWatching(watchStart, watchEnd);
      }
    },

    // 新增：根据观看片段更新重复观看片段
    updateRepeatSegmentsAfterWatching(watchStart, watchEnd) {
      if (this.repeatSegments.length === 0) {
        return; // 没有重复观看片段，无需更新
      }
      
      console.log('[更新重复观看片段] 检查观看片段:', `${watchStart}-${watchEnd}`);
      
      const updatedRepeatSegments = [];
      let hasChanges = false;
      
      for (const repeatSegment of this.repeatSegments) {
        const [repeatStart, repeatEnd] = repeatSegment.split('-').map(Number);
        
        // 检查观看片段是否与重复观看片段有重叠
        const overlapStart = Math.max(watchStart, repeatStart);
        const overlapEnd = Math.min(watchEnd, repeatEnd);
        
        if (overlapStart < overlapEnd) {
          // 有重叠，需要更新重复观看片段
          hasChanges = true;
          
          // 计算新的重复观看片段
          const newRepeatSegments = [];
          
          // 如果重复观看片段的开始部分没有被观看，保留这部分
          if (repeatStart < overlapStart) {
            newRepeatSegments.push(`${repeatStart}-${overlapStart}`);
          }
          
          // 如果重复观看片段的结束部分没有被观看，保留这部分
          if (overlapEnd < repeatEnd) {
            newRepeatSegments.push(`${overlapEnd}-${repeatEnd}`);
          }
          
          // 只有当新的重复观看片段时长超过2秒时才保留
          newRepeatSegments.forEach(segment => {
            const [start, end] = segment.split('-').map(Number);
            if (end - start > 2) {
              updatedRepeatSegments.push(segment);
            }
          });
          
          console.log('[更新重复观看片段] 原片段:', repeatSegment, '-> 新片段:', newRepeatSegments);
        } else {
          // 没有重叠，保持原样
          updatedRepeatSegments.push(repeatSegment);
        }
      }
      
      if (hasChanges) {
        this.repeatSegments = updatedRepeatSegments;
        console.log('[更新重复观看片段] 最终重复观看片段:', this.repeatSegments);
      }
    },

    // 新增：智能合并相邻的跳过片段
    mergeAdjacentSkipSegments() {
      if (this.skipSegments.length <= 1) {
        return;
      }
      
      // 按开始时间排序
      const sortedSegments = [...this.skipSegments].sort((a, b) => {
        const startA = parseFloat(a.split('-')[0]);
        const startB = parseFloat(b.split('-')[0]);
        return startA - startB;
      });
      
      const mergedSegments = [];
      let currentSegment = sortedSegments[0];
      
      for (let i = 1; i < sortedSegments.length; i++) {
        const [currentStart, currentEnd] = currentSegment.split('-').map(Number);
        const [nextStart, nextEnd] = sortedSegments[i].split('-').map(Number);
        
        // 如果两个片段间隔小于3秒，则合并
        if (nextStart - currentEnd <= 3) {
          currentSegment = `${currentStart}-${Math.max(currentEnd, nextEnd)}`;
        } else {
          mergedSegments.push(currentSegment);
          currentSegment = sortedSegments[i];
        }
      }
      
      mergedSegments.push(currentSegment);
      
      if (mergedSegments.length !== this.skipSegments.length) {
        this.skipSegments = mergedSegments;
        console.log('[合并跳过片段] 合并后:', this.skipSegments);
      }
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
        this.videoElement.removeEventListener('seeking', this.onVideoSeek);
        this.videoElement.removeEventListener('seeked', this.onVideoSeek);
        // 注意：ended事件在模板中已绑定，不需要手动添加
      } catch (error) {
        console.warn('[初始化视频事件] 清理旧事件监听器时出错:', error);
      }
      
      // 添加新的事件监听器
      try {
        this.videoElement.addEventListener('play', this.onVideoPlay);
        this.videoElement.addEventListener('pause', this.onVideoPause);
        this.videoElement.addEventListener('loadedmetadata', this.onLoadedMetadata);
        this.videoElement.addEventListener('seeking', this.onVideoSeek);
        this.videoElement.addEventListener('seeked', this.onVideoSeek);
        // 注意：ended事件在模板中已绑定，不需要手动添加
        console.log('[初始化视频事件] 事件监听器添加成功');
      } catch (error) {
        console.error('[初始化视频事件] 添加事件监听器时出错:', error);
      }
    },

    // 调试方法：查看当前状态
    debugStatus() {
      const behaviorStats = this.getLearningBehaviorStats();
      console.log('[调试状态]', {
        watchedSegments: this.watchedSegments,
        skipSegments: this.skipSegments,
        repeatSegments: this.repeatSegments,
        currentWatchStart: this.currentWatchStart,
        lastSeekTime: this.lastSeekTime,
        isWatching: this.isWatching,
        isInitialSeek: this.isInitialSeek,
        initialSeekTime: this.initialSeekTime,
        lastSegmentAddTime: this.lastSegmentAddTime,
        videoRecord: this.videoRecord,
        behaviorStats: behaviorStats
      });
    },

    // 新增：智能跳过片段管理器
    smartSkipSegmentManager() {
      // 1. 合并相邻的跳过片段
      this.mergeAdjacentSkipSegments();
      
      // 2. 过滤掉过短的跳过片段（小于3秒）
      this.skipSegments = this.skipSegments.filter(segment => {
        const [start, end] = segment.split('-').map(Number);
        return end - start >= 3;
      });
      
      // 3. 检查是否有完全被观看覆盖的跳过片段
      this.removeFullyWatchedSkipSegments();
      
      console.log('[智能跳过片段管理] 处理完成:', this.skipSegments);
    },

    // 新增：移除完全被观看覆盖的跳过片段
    removeFullyWatchedSkipSegments() {
      if (this.skipSegments.length === 0 || this.watchedSegments.length === 0) {
        return;
      }
      
      const remainingSkipSegments = [];
      
      for (const skipSegment of this.skipSegments) {
        const [skipStart, skipEnd] = skipSegment.split('-').map(Number);
        let isFullyCovered = false;
        
        // 检查是否被观看片段完全覆盖
        for (const watchSegment of this.watchedSegments) {
          const [watchStart, watchEnd] = watchSegment.split('-').map(Number);
          
          // 如果观看片段完全覆盖了跳过片段，且观看时间晚于跳过时间
          // 这里我们放宽条件，只有当观看片段的时间范围完全包含跳过片段时才移除
          if (watchStart <= skipStart && watchEnd >= skipEnd) {
            // 检查观看是否发生在跳过之后（允许5秒的误差）
            const skipTime = skipStart; // 跳过发生的时间
            const watchTime = watchStart; // 观看开始的时间
            
            // 只有当观看发生在跳过之后较长时间才移除（超过30秒）
            if (watchTime - skipTime > 30) {
              isFullyCovered = true;
              console.log('[移除完全覆盖的跳过片段]', skipSegment, '被', watchSegment, '完全覆盖，观看时间晚于跳过时间');
              break;
            } else {
              console.log('[保留跳过片段]', skipSegment, '虽然被观看覆盖，但观看时间接近跳过时间，保留记录');
            }
          }
        }
        
        if (!isFullyCovered) {
          remainingSkipSegments.push(skipSegment);
        }
      }
      
      if (remainingSkipSegments.length !== this.skipSegments.length) {
        this.skipSegments = remainingSkipSegments;
        console.log('[移除完全覆盖的跳过片段] 剩余:', this.skipSegments);
      }
    },

    // 新增：获取学习行为统计
    getLearningBehaviorStats() {
      const stats = {
        totalSkipTime: 0,
        totalRepeatTime: 0,
        totalWatchedTime: 0,
        skipSegmentsCount: this.skipSegments.length,
        repeatSegmentsCount: this.repeatSegments.length,
        watchedSegmentsCount: this.watchedSegments.length
      };
      
      // 计算跳过总时长
      this.skipSegments.forEach(segment => {
        const [start, end] = segment.split('-').map(Number);
        stats.totalSkipTime += (end - start);
      });
      
      // 计算重复观看总时长
      this.repeatSegments.forEach(segment => {
        const [start, end] = segment.split('-').map(Number);
        stats.totalRepeatTime += (end - start);
      });
      
      // 计算观看总时长
      this.watchedSegments.forEach(segment => {
        const [start, end] = segment.split('-').map(Number);
        stats.totalWatchedTime += (end - start);
      });
      
      return stats;
    },

    // 新增：智能去重函数
    removeDuplicateSegments(segments) {
      if (!segments || segments.length === 0) {
        return [];
      }
      
      // 按开始时间排序
      const sortedSegments = [...segments].sort((a, b) => {
        const startA = parseFloat(a.split('-')[0]);
        const startB = parseFloat(b.split('-')[0]);
        return startA - startB;
      });
      
      const uniqueSegments = [];
      
      for (const segment of sortedSegments) {
        const [start, end] = segment.split('-').map(Number);
        
        // 检查是否与已存在的片段有重叠（允许3秒的误差）
        const hasOverlap = uniqueSegments.some(existingSegment => {
          const [existingStart, existingEnd] = existingSegment.split('-').map(Number);
          
          // 检查时间范围是否重叠
          const overlapStart = Math.max(start, existingStart);
          const overlapEnd = Math.min(end, existingEnd);
          
          // 如果重叠时间超过3秒，认为是重复片段
          return overlapEnd - overlapStart > 3;
        });
        
        if (!hasOverlap) {
          uniqueSegments.push(segment);
        } else {
          console.log('[智能去重] 移除重复片段:', segment);
        }
      }
      
      return uniqueSegments;
    },

    // 新增：智能重复观看片段管理器
    smartRepeatSegmentManager() {
      // 1. 合并相邻的重复观看片段
      this.mergeAdjacentRepeatSegments();
      
      // 2. 过滤掉过短的重复观看片段（小于3秒）
      this.repeatSegments = this.repeatSegments.filter(segment => {
        const [start, end] = segment.split('-').map(Number);
        return end - start >= 3;
      });
      
      // 3. 检查是否有完全被观看覆盖的重复观看片段
      this.removeFullyWatchedRepeatSegments();
      
      console.log('[智能重复观看片段管理] 处理完成:', this.repeatSegments);
    },

    // 新增：智能合并相邻的重复观看片段
    mergeAdjacentRepeatSegments() {
      if (this.repeatSegments.length <= 1) {
        return;
      }
      
      // 按开始时间排序
      const sortedSegments = [...this.repeatSegments].sort((a, b) => {
        const startA = parseFloat(a.split('-')[0]);
        const startB = parseFloat(b.split('-')[0]);
        return startA - startB;
      });
      
      const mergedSegments = [];
      let currentSegment = sortedSegments[0];
      
      for (let i = 1; i < sortedSegments.length; i++) {
        const [currentStart, currentEnd] = currentSegment.split('-').map(Number);
        const [nextStart, nextEnd] = sortedSegments[i].split('-').map(Number);
        
        // 如果两个片段间隔小于3秒，则合并
        if (nextStart - currentEnd <= 3) {
          currentSegment = `${currentStart}-${Math.max(currentEnd, nextEnd)}`;
        } else {
          mergedSegments.push(currentSegment);
          currentSegment = sortedSegments[i];
        }
      }
      
      mergedSegments.push(currentSegment);
      
      if (mergedSegments.length !== this.repeatSegments.length) {
        this.repeatSegments = mergedSegments;
        console.log('[合并重复观看片段] 合并后:', this.repeatSegments);
      }
    },

    // 新增：移除完全被观看覆盖的重复观看片段
    removeFullyWatchedRepeatSegments() {
      if (this.repeatSegments.length === 0 || this.watchedSegments.length === 0) {
        return;
      }
      
      const remainingRepeatSegments = [];
      
      for (const repeatSegment of this.repeatSegments) {
        const [repeatStart, repeatEnd] = repeatSegment.split('-').map(Number);
        let isFullyCovered = false;
        
        // 检查是否被观看片段完全覆盖
        for (const watchSegment of this.watchedSegments) {
          const [watchStart, watchEnd] = watchSegment.split('-').map(Number);
          
          // 如果观看片段完全覆盖了重复观看片段，且观看时间晚于重复观看时间
          if (watchStart <= repeatStart && watchEnd >= repeatEnd) {
            // 检查观看是否发生在重复观看之后（允许5秒的误差）
            const repeatTime = repeatStart; // 重复观看发生的时间
            const watchTime = watchStart; // 观看开始的时间
            
            // 只有当观看发生在重复观看之后较长时间才移除（超过30秒）
            if (watchTime - repeatTime > 30) {
              isFullyCovered = true;
              console.log('[移除完全覆盖的重复观看片段]', repeatSegment, '被', watchSegment, '完全覆盖，观看时间晚于重复观看时间');
              break;
            } else {
              console.log('[保留重复观看片段]', repeatSegment, '虽然被观看覆盖，但观看时间接近重复观看时间，保留记录');
            }
          }
        }
        
        if (!isFullyCovered) {
          remainingRepeatSegments.push(repeatSegment);
        }
      }
      
      if (remainingRepeatSegments.length !== this.repeatSegments.length) {
        this.repeatSegments = remainingRepeatSegments;
        console.log('[移除完全覆盖的重复观看片段] 剩余:', this.repeatSegments);
      }
    },
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