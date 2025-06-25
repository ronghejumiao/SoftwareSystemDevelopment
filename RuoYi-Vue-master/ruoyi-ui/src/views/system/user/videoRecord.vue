<template>
  <div class="app-container">
    <el-card>
      <div slot="header">我的视频观看记录</div>
      <el-table :data="records" v-loading="loading" style="width:100%">
        <el-table-column prop="recordId" label="记录ID" width="80"/>
        <el-table-column prop="resourceId" label="视频ID" width="100"/>
        <el-table-column prop="totalDuration" label="总时长(秒)" width="120"/>
        <el-table-column prop="watchedDuration" label="已观看(秒)" width="120"/>
        <el-table-column prop="completionRate" label="完成率(%)" width="100"/>
        <el-table-column prop="lastWatchTime" label="最后观看时间" />
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { listRecord } from "@/api/system/videoLearningRecord";
export default {
  name: "UserVideoRecord",
  data() {
    return {
      loading: true,
      records: []
    };
  },
  created() {
    this.fetchData();
    // 监听观看记录更新事件
    this.$root.$on('videoRecordUpdated', this.fetchData);
  },
  beforeDestroy() {
    this.$root.$off('videoRecordUpdated', this.fetchData);
  },
  methods: {
    fetchData() {
      const userId = this.$route.query.userId || this.$store.getters.id;
      if (!userId) {
        this.loading = false;
        return;
      }
      listRecord({ userId: userId, pageNum: 1, pageSize: 100 }).then(res => {
        this.records = res.rows || [];
        this.loading = false;
      }).catch(() => { this.loading=false; });
    }
  }
};
</script> 