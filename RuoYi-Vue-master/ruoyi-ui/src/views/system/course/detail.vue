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
        <!-- 课程概述区域 -->
        <div class="course-overview-section">
          <div class="block-title">
            <i class="el-icon-document"></i> 课程概述
          </div>
          <div class="course-overview-content">
            <div class="overview-card">
              <div class="overview-text">{{ course.courseDesc || '暂无课程简介' }}</div>
            </div>
          </div>
        </div>

        <!-- 学生能力图谱区域 -->
        <div class="student-skill-section">
          <div class="block-title">
            <i class="el-icon-medal"></i> 我的能力掌握情况
            <span class="skill-overview" v-if="studentSkills.length > 0">
              能力掌握度：{{ averageSkillScore }}%
            </span>
          </div>

          <!-- 能力图谱 -->
          <div v-if="studentSkills.length > 0" class="skill-radar-container">
            <div class="radar-chart-wrapper">
              <div ref="radarChart" class="radar-chart"></div>
            </div>

            <!-- 能力详情列表 -->
            <div class="skill-details-list">
              <div class="skill-detail-item" v-for="skill in studentSkills" :key="skill.requirementId">
                <div class="skill-info">
                  <div class="skill-name">{{ skill.skillName }}</div>
                  <div class="skill-score">
                    <span class="score-value">{{ skill.skillScore }}</span>
                    <span class="score-unit">分</span>
                  </div>
                </div>
                <div class="skill-status">
                  <el-tag :type="getSkillStatusType(skill.skillScore)" size="small">
                    {{ getSkillStatusText(skill.skillScore) }}
                  </el-tag>
                </div>
                <div class="skill-description">{{ skill.description }}</div>
              </div>
            </div>
          </div>

          <!-- AI课程智能推荐区域 -->
          <div class="ai-recommend-section" v-if="aiRecommendations.length > 0">
            <div class="block-title" style="margin-top: 24px; display: flex; align-items: center; gap: 12px;">
              <i class="el-icon-magic-stick"></i> AI课程智能推荐
              <el-button
                type="primary"
                size="mini"
                :loading="aiRecommendLoading"
                :disabled="aiRecommendLoading"
                @click="handleChangeAIRecommend"
                style="margin-left: 8px;"
              >换一批</el-button>
            </div>
            <div class="recommend-scroll-wrapper">
              <transition-group name="fade-recommend" tag="div" class="recommend-scroll-inner">
                <div class="recommend-card" v-for="item in aiRecommendations" :key="item.id + '-' + (item.segmentId || '')" @click="handleRecommendCardClick(item)" style="cursor:pointer;">
                  <div class="recommend-title">{{ item.name }}</div>
                  <div class="recommend-summary">{{ item.summary }}</div>
                </div>
              </transition-group>
            </div>
          </div>

          <div v-else class="no-skill-data">
            <i class="el-icon-warning"></i>
            <span>暂无能力数据，请联系教师初始化</span>
          </div>
        </div>

        <!-- 课程能力要求管理区域 -->
        <div class="course-requirements-section">
          <div class="block-title" style="display:flex;align-items:center;justify-content:space-between;">
            <span><i class="el-icon-s-flag"></i> 课程能力要求</span>
            <el-button
              type="primary"
              plain
              icon="el-icon-plus"
              size="mini"
              @click="handleAddRequirement"
            >新增能力要求</el-button>
          </div>

          <div v-if="requirementList.length === 0" style="text-align: center; color: #909399;">暂无能力要求</div>
          <div class="ability-list-container">
            <div class="ability-list-grid">
              <div class="ability-card" v-for="item in requirementList" :key="item.requirementId">
                <div class="ability-header">
                  <i class="el-icon-s-flag ability-icon"></i>
                  <span class="ability-title">{{ item.skillName }}</span>
                </div>
                <div class="ability-body">
                  <div class="ability-desc" :title="item.description"><b>课程描述：</b>
                    <span class="ability-desc-text">{{ item.description }}</span>
                  </div>
                  <div class="ability-required" :title="item.requiredText"><b>课堂要求：</b>
                    <span class="ability-required-text">{{ item.requiredText }}</span>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
        <el-dialog :title="requirementDialogTitle" :visible.sync="requirementDialogVisible" width="500px" append-to-body>
          <el-form ref="requirementForm" :model="requirementForm" :rules="requirementRules" label-width="100px">
            <el-form-item label="课程ID" prop="courseId">
              <el-input v-model="requirementForm.courseId" :disabled="true" />
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
        <el-row :gutter="10" class="mb8" v-if="isTeacherOrAdmin">
          <el-col :span="1.5">
            <el-button
              type="primary"
              plain
              icon="el-icon-plus"
              size="mini"
              @click="handleAdd"
            >新增资源</el-button>
          </el-col>
        </el-row>
        <div v-if="Object.keys(groupedResources).length === 0" style="text-align: center; color: #909399;">
          暂无学习资源
        </div>
        <el-collapse v-model="activeCollapseNames">
          <el-collapse-item v-for="(resources, groupName) in sortedGroupedResources" :key="groupName" :title="groupName" :name="groupName">
            <div v-for="resource in resources" :key="resource.resourceId" class="resource-item">
              <el-link type="primary" class="resource-name" @click="handleDownloadAndSubmit(resource)">
                <i class="el-icon-document" /> {{ resource.displayName }}
              </el-link>
              <div class="actions" v-if="isTeacherOrAdmin">
                <el-button size="mini" type="text" @click.stop="handleUpdate(resource)" v-hasPermi="['system:resource:edit']">修改</el-button>
                <el-button size="mini" type="text" @click.stop="handleDelete(resource)" v-hasPermi="['system:resource:remove']">删除</el-button>
              </div>
            </div>
          </el-collapse-item>
        </el-collapse>
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
        <!-- 知识图谱区域 -->
        <div style="margin-top: 30px;">
          <KnowledgeGraph v-if="activeTab==='resources' && course.courseId" :courseId="course.courseId" />
        </div>
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
        <el-row :gutter="10" class="mb8" v-if="isTeacherOrAdmin">
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
            <div class="video-actions" v-if="isTeacherOrAdmin">
              <el-button size="mini" type="text" icon="el-icon-edit" @click.stop="handleEditVideo(video)">修改</el-button>
              <el-button size="mini" type="text" icon="el-icon-delete" @click.stop="handleDeleteVideo(video)">删除</el-button>
            </div>
          </div>
        </el-card>

        <!-- 添加或修改视频学习资源对话框 -->
        <el-dialog :title="videoDialogTitle" :visible.sync="videoDialogVisible" width="600px" append-to-body>
          <el-form ref="videoForm" :model="videoForm" :rules="videoRules" label-width="100px">
            <el-form-item label="课程编号" prop="courseCode">
              <el-input
                v-model="videoForm.courseCode"
                placeholder="请输入课程编号"
                :disabled="true"
              />
            </el-form-item>
            <el-form-item label="视频章节" prop="videoName">
              <el-input v-model="videoForm.videoName" placeholder="请输入视频章节" />
            </el-form-item>
            <el-form-item label="视频文件" prop="videoPath">
              <el-upload
                :action="videoUpload.videoUrl"
                :headers="videoUpload.headers"
                :on-success="handleVideoSuccess"
                :on-error="handleVideoError"
                :before-upload="beforeVideoUpload"
                :file-list="videoFileList"
                :show-file-list="true"
                accept=".mp4"
                :limit="1">
                <el-button size="small" type="primary">点击上传视频</el-button>
                <div slot="tip" class="el-upload__tip">只能上传mp4格式视频文件，且不超过3GB</div>
              </el-upload>
              <video
                v-if="videoForm.videoPath"
                :src="baseUrl + videoForm.videoPath"
                style="margin-top: 10px; width: 100%; max-width: 400px; height: 220px; border:1px solid #eee;"
                controls
              />
            </el-form-item>
            <el-form-item label="视频封面" prop="thumbnail">
              <el-upload
                class="avatar-uploader"
                :action="videoUpload.imageUrl"
                :headers="videoUpload.headers"
                :show-file-list="false"
                :on-success="handleThumbnailSuccess"
                :on-error="handleVideoError"
                :before-upload="beforeThumbnailUpload">
                <img v-if="videoImageUrl" :src="videoImageUrl" class="avatar">
                <el-button v-if="videoImageUrl" @click="removeThumbnail" type="danger" size="mini">删除</el-button>
                <i v-else class="el-icon-plus avatar-uploader-icon"></i>
                <div slot="tip" class="el-upload__tip">建议上传16:9比例的图片</div>
              </el-upload>
            </el-form-item>
            <el-form-item label="上传用户ID" prop="suploaderId">
              <el-input v-model="videoForm.suploaderId" placeholder="请输入上传用户ID" :disabled="true" />
            </el-form-item>
            <el-form-item label="视频名称" prop="description">
              <el-input v-model="videoForm.description" type="textarea" placeholder="请输入视频名称" />
            </el-form-item>
          </el-form>
          <div slot="footer" class="dialog-footer">
            <el-button type="primary" @click="submitVideoForm">确 定</el-button>
            <el-button @click="cancelVideoForm">取 消</el-button>
          </div>
        </el-dialog>
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
import { getStudentSkillByStudentAndCourse, initStudentCourseSkills, addStudentSkill } from '@/api/system/studentSkill';
import CourseQuiz from './quiz.vue';
import CourseTask from './task.vue';
import { listVideoresource, getVideoresource, delVideoresource, addVideoresource, updateVideoresource } from "@/api/system/videoresource";

import { addSubmission } from '@/api/system/submission';
import { listTask } from '@/api/system/task';
import { getLearningRecordByUserAndCourse } from '@/api/system/learningRecord';
import * as echarts from 'echarts';

import { notificationState } from '@/utils/notificationControl';

import KnowledgeGraph from './KnowledgeGraph.vue';
import { recommendResource } from '@/api/system/learningResource'


export default {
  name: "CourseDetailPage",
  components: { FileUpload, CourseQuiz, CourseTask, KnowledgeGraph },
  data() {
    return {
      activeTab: 'requirements',
      course: {},
      baseUrl: process.env.VUE_APP_BASE_API,
      loading: false,
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
        courseId: [
          { required: true, message: '课程ID不能为空', trigger: 'blur' }
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

      // 学生能力相关
      studentSkills: [],
      radarChart: null,
      studentId: null, // 将在created中初始化
      videoDialogTitle: '',
      videoDialogVisible: false,
      videoForm: {},
      videoRules: {
        courseCode: [
          { required: true, message: '课程编号不能为空', trigger: 'blur' }
        ],
        videoName: [
          { required: true, message: '视频章节不能为空', trigger: 'blur' }
        ],
        videoPath: [
          { required: true, message: '视频文件不能为空', trigger: 'blur' }
        ],
        suploaderId: [
          { required: true, message: '上传用户ID不能为空', trigger: 'blur' }
        ],
        description: [
          { required: true, message: '视频名称不能为空', trigger: 'blur' }
        ]
      },
      videoFileList: [],
      videoImageUrl: '',
      videoUpload: {
        videoUrl: process.env.VUE_APP_BASE_API + "/system/videoresource/uploadVideo",
        imageUrl: process.env.VUE_APP_BASE_API + "/system/videoresource/uploadThumbnail",
        headers: {
          Authorization: "Bearer " + getToken()
        }
      },

      activeCollapseNames: [], // 添加折叠面板的激活状态数组
      aiRecommendations: [],
      aiRecommendLoading: false,
      courseId: null, // 新增，统一存储courseId

    };
  },
  computed: {
    // 课程图片背景样式
    courseHeroStyle() {
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
    },

    // 计算平均能力分数
    averageSkillScore() {
      if (!this.studentSkills || this.studentSkills.length === 0) return 0;
      const total = this.studentSkills.reduce((sum, skill) => sum + parseFloat(skill.skillScore || 0), 0);
      return Math.round(total / this.studentSkills.length);
    },

    // 判断是否为教师或管理员
    isTeacherOrAdmin() {
      const roles = this.$store.getters.roles || [];
      return roles.includes('admin') || roles.includes('teacher');
    },

    // 判断是否为学生
    isStudent() {
      const roles = this.$store.getters.roles || [];
      return roles.includes('student');
    },

    sortedGroupedResources() {
      const groups = this.groupedResources;

      // 中文数字映射
      const chineseNumbers = {
        '一': 1, '二': 2, '三': 3, '四': 4, '五': 5,
        '六': 6, '七': 7, '八': 8, '九': 9, '十': 10
      };

      // 处理章节标题，将中文数字转换为阿拉伯数字
      const processTitle = (title) => {
        // 提取章节号
        const match = title.match(/第(.*?)章/);
        if (match) {
          const chineseNum = match[1];
          if (chineseNumbers[chineseNum]) {
            // 替换中文数字为阿拉伯数字
            return title.replace(/第(.*?)章/, `第${chineseNumbers[chineseNum]}章`);
          }
        }
        return title;
      };

      // 对分组名称进行排序
      const sortedKeys = Object.keys(groups).sort((a, b) => {
        const processedA = processTitle(a);
        const processedB = processTitle(b);

        // 提取数字部分
        const numA = processedA.match(/第(\d+)章/);
        const numB = processedB.match(/第(\d+)章/);

        // 如果都有章节号，按章节号排序
        if (numA && numB) {
          const chapterA = parseInt(numA[1]);
          const chapterB = parseInt(numB[1]);
          if (chapterA !== chapterB) {
            return chapterA - chapterB;
          }
        }

        // 如果章节号相同或者没有章节号，按完整字符串排序
        return processedA.localeCompare(processedB, 'zh-CN');
      });

      // 返回排序后的对象
      const sortedGroups = {};
      sortedKeys.forEach(key => {
        sortedGroups[key] = groups[key];
      });

      return sortedGroups;
    }
  },

  async created() {
    // 初始化用户信息
    await this.initUserInfo();

    // 获取课程ID
    const courseId = this.$route.params.courseId || this.$route.query.courseId;
    if (!courseId) {
      this.$message.error('未找到课程ID参数');
      return;
    }
    this.courseId = courseId;

    // 检查studentId
    if (!this.studentId) {
      this.$message.error('未找到学生ID参数');
      return;
    }

    // 只有都有效才调用
    await this.getCourseDetails(this.courseId);
    await this.getResourceList(this.courseId);
    await this.getVideoList(this.courseId);

    // 检查tab参数
    const tab = this.$route.query.tab;
    if (tab && ['requirements', 'resources', 'tasks', 'quiz', 'videos'].includes(tab)) {
      this.activeTab = tab;
    }


    // 默认展开所有折叠面板
    this.$nextTick(() => {
      this.activeCollapseNames = Object.keys(this.groupedResources);
    });

    // 只有都有效才调用
    if (this.studentId && this.courseId) {
      await this.checkAndInitStudentSkill();
      await this.loadAIRecommendations();
    }

  },

  beforeDestroy() {
    // 清理雷达图实例
    if (this.radarChart) {
      this.radarChart.dispose();
      this.radarChart = null;
    }

    // 移除窗口大小变化监听器
    window.removeEventListener('resize', this.handleResize);
  },

  destroyed() {
    notificationState.isErrorNotificationsEnabled = true; // 离开页面时恢复弹窗
  },
  methods: {
    // 初始化用户信息
    async initUserInfo() {
      try {
        // 尝试从store获取用户信息
        if (this.$store && this.$store.dispatch) {
          await this.$store.dispatch('user/getInfo');
          this.studentId = this.$store.getters.id;
          this.uploaderId = this.$store.getters.id;
        }

        // 如果store中没有，尝试从localStorage获取
        if (!this.studentId) {
          const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
          this.studentId = userInfo.userId || '';
          this.uploaderId = userInfo.userId || '';
        }

        console.log('[DEBUG] 初始化用户信息完成，studentId:', this.studentId);
      } catch (error) {
        console.error('初始化用户信息失败:', error);
        // 从localStorage获取备用信息
        const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
        this.studentId = userInfo.userId || '';
        this.uploaderId = userInfo.userId || '';
      }
    },

    getCourseDetails(courseId) {
      if (!courseId) {
        this.$message.error('未找到课程ID参数');
        return;
      }
      this.loading = true;
      getCourse(courseId).then(response => {
        this.course = response.data || {};
        this.loading = false;
        this.getRequirementList();
      }).catch(error => {
        console.error('获取课程详情失败', error);
        this.$message.error('获取课程详情失败，请刷新页面重试');
        this.loading = false;
      });
    },
    getResourceList(courseId) {
      this.loading = true;
      listResource({ courseId: courseId, pageSize: 999 }).then(response => {
        // 过滤掉video类型的资源，因为视频资源现在单独管理
        this.resourceList = (response.rows || []).filter(resource => resource.resourceType !== 'video');
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
    async handleDownloadAndSubmit(resource) {
      function formatDate(date) {
        if (!date) return '';
        const pad = n => n < 10 ? '0' + n : n;
        return date.getFullYear() + '-' +
          pad(date.getMonth() + 1) + '-' +
          pad(date.getDate()) + ' ' +
          pad(date.getHours()) + ':' +
          pad(date.getMinutes()) + ':' +
          pad(date.getSeconds());
      }
      console.log('[DEBUG] handleDownloadAndSubmit被调用，resource:', resource);
      // 获取用户ID
      let userId = this.uploaderId || (this.$store && this.$store.getters && this.$store.getters.id);
      if (!userId) {
        console.log('[DEBUG] userId为空，尝试获取用户信息');
        if (this.$store && this.$store.dispatch) {
          await this.$store.dispatch('user/getInfo');
          userId = this.$store.getters.id;
        }
        if (!userId) {
          const userInfo = JSON.parse(localStorage.getItem('userInfo') || '{}');
          userId = userInfo.userId;
        }
        console.log('[DEBUG] 获取后userId:', userId);
      }
      const courseId = resource.courseId || (this.course && this.course.courseId);
      if (!userId || !courseId) {
        this.$modal && this.$modal.msgError && this.$modal.msgError('无法获取用户或课程信息，userId:' + userId + ', courseId:' + courseId);
        console.error('[DEBUG] userId或courseId为空，终止提交');
        return;
      }
      let record = await getLearningRecordByUserAndCourse(userId, courseId);
      console.log('[DEBUG] 查询学习记录:', record);
      if (!record) {
        if (this.$store && this.$store.dispatch) {
          await this.$store.dispatch('user/getInfo');
        }
        record = await getLearningRecordByUserAndCourse(userId, courseId);
        console.log('[DEBUG] 再次查询学习记录:', record);
      }
      if (!record) {
        this.$modal && this.$modal.msgError && this.$modal.msgError('未找到学习记录，无法提交');
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
            submissionTime: formatDate(now),
            createTime: formatDate(now),
            isGraded: '1',
            gradeScore: 100,
            gradeComment: '资料阅读自动满分',
            status: '1',
            delFlag: '0'
          };
          console.log('[DEBUG] 提交任务参数:', submissionData);
          const res = await addSubmission(submissionData);
          console.log('[DEBUG] addSubmission响应:', res);
          this.$root && this.$root.$emit && this.$root.$emit('submissionRecordUpdated');
        } catch (e) {
          console.error('[DEBUG] 自动提交任务失败', e);
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
      listRequirement({ courseId: this.course.courseId, pageSize: 999 }).then(response => {
        this.requirementList = response.rows || [];
        // 获取学生能力数据
        this.getStudentSkills();
      }).catch(() => {
        this.requirementList = [];
      });
    },

    // 获取学生能力数据
    async getStudentSkills() {
      if (!this.studentId || !this.course.courseId) {
        console.log('[DEBUG] 缺少必要参数，studentId:', this.studentId, 'courseId:', this.course.courseId);
        return;
      }

      try {
        console.log('[DEBUG] 开始获取学生能力数据，studentId:', this.studentId, 'courseId:', this.course.courseId);
        const response = await getStudentSkillByStudentAndCourse(this.studentId, this.course.courseId);
        this.studentSkills = response.data || [];
        console.log('[DEBUG] 获取到的学生能力数据:', this.studentSkills);

        // 检查是否需要初始化
        if (this.studentSkills.length === 0 && this.requirementList.length > 0) {
          console.log('[DEBUG] 学生能力数据为空，开始初始化');
          await this.initStudentSkills();
        } else if (this.studentSkills.length > 0 && this.requirementList.length > 0) {
          // 检查是否有缺失的能力要求
          const existingRequirementIds = this.studentSkills.map(skill => skill.requirementId);
          const missingRequirements = this.requirementList.filter(req =>
            !existingRequirementIds.includes(req.requirementId)
          );

          if (missingRequirements.length > 0) {
            console.log('[DEBUG] 发现缺失的能力要求:', missingRequirements);
            await this.initMissingSkills(missingRequirements);
          }
        }

        // 绘制雷达图
        this.$nextTick(() => {
          this.initRadarChart();
        });
      } catch (error) {
        console.error('获取学生能力数据失败:', error);
        this.studentSkills = [];

        // 如果获取失败，尝试初始化
        if (this.requirementList.length > 0) {
          console.log('[DEBUG] 获取失败，尝试初始化学生能力数据');
          await this.initStudentSkills();
        }
      }
    },

    // 初始化学生能力数据
    async initStudentSkills() {
      if (!this.studentId || !this.course.courseId) {
        console.log('[DEBUG] 初始化失败，缺少必要参数');
        return;
      }

      try {
        console.log('[DEBUG] 开始初始化学生能力数据');
        await initStudentCourseSkills(this.studentId, this.course.courseId);
        console.log('[DEBUG] 初始化成功，重新获取学生能力数据');

        // 重新获取学生能力数据
        const response = await getStudentSkillByStudentAndCourse(this.studentId, this.course.courseId);
        this.studentSkills = response.data || [];

        // 重新绘制雷达图
        this.$nextTick(() => {
          this.initRadarChart();
        });

        this.$message.success('学生能力数据初始化成功');
        console.log('[DEBUG] 初始化后的学生能力数据:', this.studentSkills);
      } catch (error) {
        console.error('初始化学生能力数据失败:', error);
        this.$message.error('初始化学生能力数据失败: ' + (error.message || '未知错误'));
      }
    },

    // 初始化缺失的能力记录
    async initMissingSkills(missingRequirements) {
      if (!this.studentId || missingRequirements.length === 0) return;

      try {
        console.log('[DEBUG] 开始初始化缺失的能力记录:', missingRequirements);

        // 为每个缺失的能力要求创建学生能力记录
        const promises = missingRequirements.map(requirement => {
          const skillData = {
            studentId: this.studentId,
            requirementId: requirement.requirementId,
            skillScore: 0, // 初始分数为0
            updateTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
            updateReason: '系统自动初始化'
          };

          return addStudentSkill(skillData);
        });

        await Promise.all(promises);
        console.log('[DEBUG] 缺失能力记录初始化成功');

        // 重新获取学生能力数据
        const response = await getStudentSkillByStudentAndCourse(this.studentId, this.course.courseId);
        this.studentSkills = response.data || [];

        // 重新绘制雷达图
        this.$nextTick(() => {
          this.initRadarChart();
        });

        this.$message.success(`成功初始化 ${missingRequirements.length} 个能力记录`);
      } catch (error) {
        console.error('初始化缺失能力记录失败:', error);
        this.$message.error('初始化缺失能力记录失败');
      }
    },

    // 初始化雷达图
    initRadarChart() {
      if (!this.studentSkills || this.studentSkills.length === 0) return;

      const chartDom = this.$refs.radarChart;
      if (!chartDom) return;

      // 销毁之前的图表
      if (this.radarChart) {
        this.radarChart.dispose();
      }

      this.radarChart = echarts.init(chartDom);

      // 准备数据
      const indicators = this.studentSkills.map(skill => ({
        name: skill.skillName,
        max: 100
      }));

      const values = this.studentSkills.map(skill => parseFloat(skill.skillScore || 0));

      // 调试信息
      console.log('[DEBUG] 雷达图数据:', {
        indicators: indicators,
        values: values,
        studentSkills: this.studentSkills
      });

      const option = {
        title: {
          text: '能力掌握情况',
          left: 'center',
          textStyle: {
            fontSize: 16,
            fontWeight: 'bold'
          }
        },
        tooltip: {
          trigger: 'item',
          backgroundColor: 'rgba(255, 255, 255, 0.95)',
          borderColor: '#409eff',
          borderWidth: 1,
          textStyle: {
            color: '#333'
          },
          confine: true,
          formatter: (params) => {
            console.log('[DEBUG] Tooltip被触发，params:', params);

            // 对于雷达图，使用params.name来找到对应的技能数据
            const skillName = params.name;
            const skill = this.studentSkills.find(s => s.skillName === skillName);

            if (!skill) {
              console.log('[DEBUG] 未找到对应技能数据，name:', skillName);
              return `${skillName || '未知能力'}<br/>得分：${params.value}分`;
            }

            console.log('[DEBUG] 找到技能数据:', skill);

            return `<div style="padding: 8px;">
              <div style="font-weight: bold; color: #409eff; margin-bottom: 6px; font-size: 14px;">${skill.skillName}</div>
              <div style="margin-bottom: 6px; font-size: 13px;">得分：<span style="color: #67c23a; font-weight: bold;">${skill.skillScore}分</span></div>
              <div style="color: #606266; font-size: 12px; line-height: 1.4;">描述：${skill.description || '暂无描述'}</div>
            </div>`;
          }
        },
        radar: {
          indicator: indicators,
          radius: '65%',
          center: ['50%', '55%'],
          splitNumber: 5,
          axisName: {
            color: '#333',
            fontSize: 12
          },
          splitLine: {
            lineStyle: {
              color: ['#ddd']
            }
          },
          splitArea: {
            show: false
          },
          axisLine: {
            lineStyle: {
              color: '#ddd'
            }
          }
        },
        series: [{
          name: '能力得分',
          type: 'radar',
          data: [{
            value: values,
            name: '当前得分',
            areaStyle: {
              color: 'rgba(64, 158, 255, 0.3)'
            },
            lineStyle: {
              color: '#409eff',
              width: 2
            },
            itemStyle: {
              color: '#409eff'
            }
          }],
          emphasis: {
            lineStyle: {
              width: 3
            },
            itemStyle: {
              color: '#409eff',
              borderWidth: 2,
              borderColor: '#fff'
            }
          }
        }]
      };

      this.radarChart.setOption(option);

      // 监听窗口大小变化
      this.handleResize = () => {
        if (this.radarChart) {
          this.radarChart.resize();
        }
      };
      window.addEventListener('resize', this.handleResize);
    },

    // 获取能力状态类型
    getSkillStatusType(score) {
      const numScore = parseFloat(score || 0);
      if (numScore >= 80) return 'success';
      if (numScore >= 60) return 'warning';
      return 'danger';
    },

    // 获取能力状态文本
    getSkillStatusText(score) {
      const numScore = parseFloat(score || 0);
      if (numScore >= 80) return '优秀';
      if (numScore >= 60) return '良好';
      return '待提升';
    },
    handleAddRequirement() {
      this.requirementForm = {
        courseId: this.course.courseId,
        skillName: '',
        description: '',
        requiredText: ''
      };
      this.requirementDialogTitle = '新增能力要求';
      this.requirementDialogVisible = true;
    },
    handleEditRequirement(requirement) {
      this.requirementForm = { ...requirement };
      this.requirementDialogTitle = '修改能力要求';
      this.requirementDialogVisible = true;
    },
    handleDeleteRequirement(requirement) {
      const requirementId = requirement.requirementId;
      this.$modal.confirm('是否确认删除该能力要求？').then(function() {
        return delRequirement(requirementId);
      }).then(() => {
        this.getRequirementList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    submitRequirementForm() {
      this.requirementForm.courseId = this.course.courseId;
      this.$refs.requirementForm.validate(valid => {
        if (valid) {
          if (this.requirementForm.requirementId) {
            updateRequirement(this.requirementForm).then(() => {
              this.$modal.msgSuccess("修改成功");
              this.requirementDialogVisible = false;
              this.getRequirementList();
            });
          } else {
            addRequirement(this.requirementForm).then(() => {
              this.$modal.msgSuccess("新增成功");
              this.requirementDialogVisible = false;
              this.getRequirementList();
            });
          }
        }
      });
    },
    // 视频相关方法
    getVideoList(courseId) {
      listVideoresource({ courseId: courseId, pageSize: 999 }).then(response => {
        this.videoList = response.rows || [];

        // 按章节分组视频
        this.groupedVideos = {};
        this.videoList.forEach(video => {
          const chapter = video.videoName || '未分类';
          if (!this.groupedVideos[chapter]) {
            this.groupedVideos[chapter] = [];
          }
          this.groupedVideos[chapter].push(video);
        });
      }).catch(() => {
        this.videoList = [];
        this.groupedVideos = {};
      });
    },
    handleAddVideo() {
      this.videoForm = {
        courseCode: this.course.courseCode,
        videoName: '',
        videoPath: '',
        thumbnail: '',
        suploaderId: this.uploaderId,
        description: ''
      };
      this.videoFileList = [];
      this.videoImageUrl = '';
      this.videoDialogTitle = '新增视频';
      this.videoDialogVisible = true;
    },
    handleEditVideo(video) {
      this.videoForm = {
        videoId: video.videoId,
        courseCode: this.course.courseCode,
        videoName: video.videoName,
        videoPath: video.videoPath,
        thumbnail: video.thumbnail,
        suploaderId: video.suploaderId,
        description: video.description
      };
      // 设置图片URL用于显示
      if (video.thumbnail) {
        this.videoImageUrl = this.baseUrl + video.thumbnail;
      }
      this.videoDialogTitle = '修改视频';
      this.videoDialogVisible = true;
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
    handleVideoPreview(video) {
      this.$router.push({
        name: 'VideoPlay',
        params: { videoId: video.videoId },
        query: { courseId: this.course.courseId }
      });
    },
    goToVideoList() {
      this.$router.push({
        name: 'VideoList',
        query: { courseId: this.course.courseId }
      });
    },
    handleSwitchTab(tabName) {
      if (tabName && ['requirements', 'resources', 'tasks', 'quiz', 'videos'].includes(tabName)) {
      this.activeTab = tabName;
      }
    },
    handleVideoSuccess(res, file) {
      if (res.code === 200) {
        this.videoForm.videoPath = res.videoUrl;
        this.videoForm.fileSize = res.fileSize;
        this.videoForm.duration = res.duration;
        this.videoForm.uploadTime = res.uploadTime;

        // 自动赋值首帧为封面
        if (res.coverImage) {
          this.videoForm.thumbnail = res.coverImage;
          this.videoImageUrl = this.baseUrl + res.coverImage;
        }

        this.videoFileList = [{
          name: file.name,
          url: this.baseUrl + res.videoUrl
        }];
        this.$message.success('视频上传成功');
      } else {
        this.$message.error(res.msg || '视频上传失败');
      }
    },
    handleVideoError(err, file) {
      console.error('视频上传失败:', err);
      this.$message.error('上传失败');
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
    handleThumbnailSuccess(res, file) {
      if (res.code === 200) {
        this.videoForm.thumbnail = res.thumbnailUrl;
        this.videoImageUrl = this.baseUrl + res.thumbnailUrl;
        this.$message.success('封面上传成功');
      } else {
        this.$message.error(res.msg || '封面上传失败');
      }
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
      this.videoForm.thumbnail = '';
      this.videoImageUrl = '';
    },
    submitVideoForm() {
      this.$refs.videoForm.validate((valid) => {
        if (valid) {
          // 设置课程ID和视频类型
          this.videoForm.courseId = this.course.courseId;
          this.videoForm.videoType = "MP4";

          if (this.videoForm.videoId) {
            // 修改视频
            updateVideoresource(this.videoForm).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.videoDialogVisible = false;
              this.getVideoList(this.course.courseId);
            });
          } else {
            // 新增视频
            addVideoresource(this.videoForm).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.videoDialogVisible = false;
              this.getVideoList(this.course.courseId);
            });
          }
        }
      });
    },
    cancelVideoForm() {
      this.videoDialogVisible = false;
      this.videoForm = {};
      this.videoFileList = [];
      this.videoImageUrl = '';
      // 重置表单验证
      this.$nextTick(() => {
        if (this.$refs.videoForm) {
          this.$refs.videoForm.clearValidate();
        }
      });
    },
    async checkAndInitStudentSkill() {
      if (!this.studentId || !this.courseId) return;
      const res = await getStudentSkillByStudentAndCourse(this.studentId, this.courseId)
      if (!res.data || res.data.length === 0) {
        await initStudentCourseSkills(this.studentId, this.courseId)
      }
    },
    async loadAIRecommendations(forceRefresh = false) {
      if (!this.studentId || !this.courseId) return;
      this.aiRecommendLoading = true;
      try {
        const res = await recommendResource(this.studentId, this.courseId, forceRefresh)
        if (res.data && Array.isArray(res.data)) {
          // 先清空，触发动画
          this.aiRecommendations = [];
          await this.$nextTick();
          this.aiRecommendations = await Promise.all(res.data.map(async (item) => {
            let name = '', summary = ''
            if (item.resourceType === 'ppt') {
              const resource = this.resourceList.find(r => r.resourceId === item.id)
              name = resource ? resource.resourceName : '资料资源'
              summary = resource ? resource.contentSummary : ''
            } else if (item.resourceType === 'video') {
              const video = this.videoList.find(v => v.videoId === item.id)
              name = video ? video.videoName : '视频资源'
              if (video && video.segments && item.segmentId) {
                const seg = video.segments.find(s => s.segmentId === item.segmentId)
                summary = seg ? seg.contentSummary : ''
              } else {
                summary = video && video.summary ? video.summary : ''
              }
            }
            return { ...item, name, summary }
          }))
        }
      } finally {
        this.aiRecommendLoading = false;
      }
    },
    handleChangeAIRecommend() {
      this.loadAIRecommendations(true);
    },
    handleRecommendCardClick(item) {
      if (item.resourceType === 'ppt' || item.resourceType === 'pdf') {
        // 跳转到学习资源tab
        this.activeTab = 'resources';
        this.$nextTick(() => {
          // 可选：滚动到资源区域
          const el = document.querySelector('.block-title i.el-icon-folder');
          if (el) el.scrollIntoView({ behavior: 'smooth', block: 'center' });
        });
      } else if (item.resourceType === 'video') {
        // 跳转到视频学习tab并自动跳转到视频播放
        this.activeTab = 'videos';
        this.$nextTick(() => {
          // 跳转到视频播放页面
          this.$router.push({
            name: 'VideoPlay',
            params: { videoId: item.id },
            query: { courseId: this.courseId }
          });
        });
      }
    },
  },
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
.ability-list-container {
  max-height: 400px;
  overflow-y: auto;
}
.ability-list-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 20px;
  margin-top: 10px;
}
.ability-card {
  border-radius: 12px;
  transition: box-shadow 0.25s, transform 0.18s;
  min-height: 180px;
  background: #fff;
  box-shadow: 0 4px 16px rgba(64,158,255,0.10), 0 1.5px 6px rgba(0,0,0,0.06);
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  width: 100%;
  margin-bottom: 0;
  padding: 18px 20px 14px 20px;
  box-sizing: border-box;
}
.ability-card:hover {
  box-shadow: 0 8px 32px rgba(64,158,255,0.18), 0 4px 16px rgba(0,0,0,0.10);
  transform: translateY(-4px) scale(1.025);
  z-index: 2;
}
.ability-header {
  display: flex;
  align-items: center;
  font-size: 18px;
  font-weight: bold;
  margin-bottom: 8px;
}
.ability-icon {
  color: #409EFF;
  font-size: 22px;
  margin-right: 8px;
}
.ability-title {
  flex: 1;
  word-break: break-all;
}
.ability-body {
  font-size: 14px;
  color: #606266;
}
.ability-desc, .ability-required {
  margin-bottom: 6px;
  word-break: break-all;
}
.ability-desc-text, .ability-required-text {
  display: inline-block;
  vertical-align: top;
  white-space: pre-line;
  word-break: break-all;
}
.ability-required {
  color: #909399;
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
  padding: 12px 0;
  border-bottom: 1px solid #EBEEF5;
  transition: background-color 0.2s;
}
.video-item:last-child {
  border-bottom: none;
}
.video-item:hover {
  background-color: #f8f9fa;
}
.video-info {
  display: flex;
  align-items: center;
  flex: 1;
  cursor: pointer;
  padding: 8px;
  border-radius: 6px;
  transition: background-color 0.2s;
}
.video-info:hover {
  background-color: #f0f2f5;
}
.video-thumbnail {
  width: 80px;
  height: 60px;
  margin-right: 12px;
  border-radius: 4px;
  overflow: hidden;
  background-color: #f5f5f5;
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
  color: #c0c4cc;
}
.video-details {
  flex: 1;
}
.video-name {
  font-size: 14px;
  font-weight: 500;
  color: #303133;
  margin-bottom: 4px;
  line-height: 1.4;
}
.video-meta {
  font-size: 12px;
  color: #909399;
  display: flex;
  gap: 12px;
}
.video-actions {
  display: flex;
  gap: 8px;
}
.video-actions .el-button {
  padding: 4px 8px;
  font-size: 12px;
}
.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}
.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}
.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}
.avatar {
  width: 178px;
  height: 178px;
  display: block;
}

/* 课程概述区域样式 */
.course-overview-section {
  margin-bottom: 30px;
}

.course-overview-content {
  margin-top: 16px;
}

.overview-card {
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  border: 1px solid #e4e7ed;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
}

.overview-text {
  font-size: 15px;
  line-height: 1.6;
  color: #606266;
  white-space: pre-wrap;
  word-break: break-word;
}

/* 学生能力图谱区域样式 */
.student-skill-section {
  margin-bottom: 30px;
}

.skill-overview {
  margin-left: 15px;
  font-size: 14px;
  color: #67c23a;
  font-weight: 500;
}

.skill-radar-container {
  display: flex;
  gap: 30px;
  margin-top: 20px;
  align-items: flex-start;
  min-height: 440px;
}

.radar-chart-wrapper {
  flex: 1;
  height: 400px;
  background: #fff;
  border-radius: 12px;
  padding: 20px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  border: 1px solid #e4e7ed;
}

.radar-chart {
  width: 100%;
  height: 360px;
}

.skill-details-list {
  flex: 1;
  max-width: 400px;
  max-height: 400px;
  overflow-y: auto;
  padding-right: 8px;
}

/* 自定义滚动条样式 */
.skill-details-list::-webkit-scrollbar {
  width: 6px;
}

.skill-details-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 3px;
}

.skill-details-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 3px;
}

.skill-details-list::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.skill-detail-item {
  background: #fff;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 16px;
  margin-bottom: 12px;
  box-shadow: 0 2px 8px rgba(0, 0, 0, 0.05);
  transition: all 0.3s ease;
}

.skill-detail-item:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
  transform: translateY(-2px);
}

.skill-info {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}

.skill-name {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  flex: 1;
}

.skill-score {
  display: flex;
  align-items: baseline;
  margin-left: 12px;
}

.score-value {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
}

.score-unit {
  font-size: 12px;
  color: #909399;
  margin-left: 2px;
}

.skill-status {
  margin-bottom: 8px;
}

.skill-description {
  font-size: 13px;
  color: #606266;
  line-height: 1.5;
  word-break: break-word;
}

.no-skill-data {
  text-align: center;
  padding: 40px 20px;
  color: #909399;
  background: #f8f9fa;
  border-radius: 8px;
  border: 1px dashed #d9d9d9;
}

.no-skill-data i {
  font-size: 24px;
  margin-bottom: 8px;
  display: block;
}

/* 课程能力要求管理区域样式 */
.course-requirements-section {
  margin-top: 30px;
}

.ability-list-container {
  max-height: 400px;
  overflow-y: auto;
  margin-top: 10px;
  padding-right: 8px;
  border-radius: 8px;
}

/* 自定义滚动条样式 */
.ability-list-container::-webkit-scrollbar {
  width: 8px;
}

.ability-list-container::-webkit-scrollbar-track {
  background: #f5f5f5;
  border-radius: 4px;
}

.ability-list-container::-webkit-scrollbar-thumb {
  background: #c0c4cc;
  border-radius: 4px;
  transition: background 0.3s ease;
}

.ability-list-container::-webkit-scrollbar-thumb:hover {
  background: #909399;
}

.ability-list-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(320px, 1fr));
  gap: 20px;
  padding-bottom: 10px;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .skill-radar-container {
    flex-direction: column;
    gap: 20px;
    min-height: auto;
  }

  .radar-chart-wrapper {
    height: 300px;
  }

  .radar-chart {
    height: 260px;
  }

  .skill-details-list {
    max-width: 100%;
    max-height: 300px;
  }

  .ability-list-container {
    max-height: 300px;
  }

  .skill-info {
    flex-direction: column;
    align-items: flex-start;
    gap: 8px;
  }

  .skill-score {
    margin-left: 0;
  }
}

@media (max-width: 480px) {
  .course-overview-section,
  .student-skill-section,
  .course-requirements-section {
    margin-bottom: 20px;
  }

  .overview-card {
    padding: 15px;
  }

  .overview-text {
    font-size: 14px;
  }

  .radar-chart-wrapper {
    padding: 15px;
    height: 250px;
  }

  .radar-chart {
    height: 220px;
  }

  .skill-details-list {
    max-height: 250px;
  }

  .ability-list-container {
    max-height: 250px;
  }

  .skill-detail-item {
    padding: 12px;
  }

  .skill-name {
    font-size: 14px;
  }

  .score-value {
    font-size: 18px;
  }

  .ability-list-grid {
    grid-template-columns: 1fr;
    gap: 15px;
  }

  .ability-card {
    padding: 15px;
  }
}


/* 添加折叠面板样式 */
.el-collapse {
  border: none;
  margin-bottom: 20px;
}

.el-collapse-item {
  background: #fff;
  border-radius: 8px;
  margin-bottom: 12px;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
}

.el-collapse-item:last-child {
  margin-bottom: 0;
}

.el-collapse-item__header {
  font-size: 16px;
  font-weight: 600;
  color: #303133;
  background: #f8f9fa;
  border-radius: 8px;
  padding: 0 20px;
  height: 50px;
  line-height: 50px;
  border-bottom: none;
}

.el-collapse-item__content {
  padding: 15px 20px;
}

.el-collapse-item__wrap {
  border-bottom: none;
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
.recommend-scroll-wrapper {
  padding: 8px 0 16px 0;
}
.recommend-scroll-inner {
  display: flex;
  overflow-x: auto;
}
.recommend-card {
  min-width: 220px;
  max-width: 260px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.06);
  margin-right: 16px;
  padding: 16px 18px 12px 18px;
  display: flex;
  flex-direction: column;
  justify-content: center;
}
.recommend-title {
  font-weight: bold;
  font-size: 16px;
  margin-bottom: 8px;
  color: #333;
}
.recommend-summary {
  font-size: 13px;
  color: #666;
  white-space: normal;
  word-break: break-all;
}
.fade-recommend-enter-active, .fade-recommend-leave-active {
  transition: opacity 0.4s;
}
.fade-recommend-enter, .fade-recommend-leave-to {
  opacity: 0;
}
</style>
