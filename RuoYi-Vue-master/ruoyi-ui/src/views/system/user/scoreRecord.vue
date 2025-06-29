<template>
  <div class="app-container">
    <el-card>
      <div slot="header">个人成绩记录</div>
      <el-table :data="scores" v-loading="loading" style="width:100%">
        <el-table-column prop="scoreId" label="ID" width="70"/>
        <el-table-column prop="courseName" label="课程" width="120"/>
        <el-table-column prop="paperId" label="试卷ID" width="100"/>
        <el-table-column prop="score" label="分数" width="80"/>
        <el-table-column prop="submitTime" label="提交时间" />
      </el-table>
    </el-card>
  </div>
</template>

<script>
import { getScoreByUserId } from "@/api/system/score";
export default {
  name: "UserScoreRecord",
  data() {
    return {
      loading: true,
      scores: []
    };
  },
  created() {
    this.fetchData();
  },
  methods: {
    fetchData() {
      const userId = this.$route.query.userId || this.$store.getters.id;
      if (!userId) {
        this.loading = false;
        return;
      }
      getScoreByUserId(userId).then(res => {
        this.scores = res.data || res.rows || [];
        if (res.rows) this.scores = res.rows;
        this.loading = false;
      }).catch(() => { this.loading=false; });
    }
  }
};
</script>
