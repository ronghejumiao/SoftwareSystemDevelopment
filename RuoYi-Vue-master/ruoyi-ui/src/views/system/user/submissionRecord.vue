<template>
  <div class="app-container">
    <el-card>
      <div slot="header">我的任务提交记录</div>
      <el-table :data="submissions" v-loading="loading" style="width:100%">
        <el-table-column prop="submissionId" label="ID" width="70"/>
        <el-table-column prop="taskId" label="任务ID" width="100"/>
        <el-table-column prop="courseId" label="课程ID" width="100"/>
        <el-table-column prop="status" label="状态" width="100"/>
        <el-table-column prop="score" label="评分" width="80"/>
        <el-table-column prop="createTime" label="提交时间" />
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { listSubmission } from "@/api/system/submission";
export default {
  name: "UserSubmissionRecord",
  data() {
    return {
      loading: true,
      submissions: []
    };
  },
  created() {
    this.fetchData();
    // 监听任务提交记录更新事件，实现实时同步
    this.$root.$on('submissionRecordUpdated', this.fetchData);
  },
  beforeDestroy() {
    // 移除监听，防止内存泄漏
    this.$root.$off('submissionRecordUpdated', this.fetchData);
  },
  methods: {
    fetchData() {
      const userId = this.$route.query.userId || this.$store.getters.id;
      if (!userId) {
        this.loading = false;
        return;
      }
      listSubmission({ userId, pageNum: 1, pageSize: 100 }).then(res => {
        this.submissions = res.rows || [];
        this.loading = false;
      }).catch(() => { this.loading=false; });
    }
  }
};
</script> 