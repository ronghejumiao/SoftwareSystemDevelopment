<template>
  <div class="app-container">
    <el-card>
      <div slot="header" class="clearfix">
        <span>{{ course.courseName || '课程详情' }}</span>
      </div>
      <el-tabs v-model="activeTab">
        <el-tab-pane label="学习资源" name="resources">
          <p>这里是学习资源内容区域。</p>
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
  </div>
</template>

<script>
import { getCourse } from "@/api/system/course";

export default {
  name: "CourseDetail",
  data() {
    return {
      activeTab: 'resources',
      course: {},
    };
  },
  created() {
    const courseId = this.$route.params.courseId;
    if (courseId) {
      this.getCourseDetails(courseId);
    }
  },
  methods: {
    getCourseDetails(courseId) {
      getCourse(courseId).then(response => {
        this.course = response.data;
      });
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
</style> 