<template>
  <div class="knowledge-graph-container">
    <el-alert v-if="polling" title="知识图谱正在初始化，请稍后..." type="info" show-icon style="margin-bottom: 10px;"/>
    <el-progress v-if="polling" :percentage="progressPercent" status="active" style="margin-bottom: 10px;"/>
    <div class="kg-toolbar">
      <el-button type="primary" @click="fetchGraph" :loading="loading || polling">刷新知识图谱</el-button>
      <el-button @click="goBackCourseDetail" style="margin-bottom:10px;">返回课程详情</el-button>
    </div>
    <div ref="graphChart" class="kg-graph" style="height: 500px;"></div>
  </div>
</template>

<script>
import request from '@/utils/request';
import * as echarts from 'echarts';
import { listResource } from '@/api/system/resource'

export default {
  name: 'KnowledgeGraph',
  props: {
    courseId: { type: [String, Number], required: true },
    fileName: { type: String, default: '' }
  },
  data() {
    return {
      loading: false,
      chart: null,
      nodes: [],
      edges: [],
      resourceList: [],
      polling: false,      // 是否正在轮询
      pollingTimer: null,  // 轮询定时器
      pollingCount: 0,     // 轮询次数
      maxPolling: 15,       // 最大轮询次数（如30秒/2秒一次）
      progressPercent: 0
    };
  },
  mounted() {
    this.loadResourceListAndGraph();
  },
  watch: {
    courseId: {
      immediate: true,
      handler(newVal) {
        if (newVal) {
          this.loadResourceListAndGraph();
        }
      }
    },
    fileName(newVal) {
      this.fetchGraph();
    },
    polling(val) {
      if (val) {
        this.doPolling();
      } else if (this.pollingTimer) {
        clearTimeout(this.pollingTimer);
        this.pollingTimer = null;
      }
    }
  },
  methods: {
    async loadResourceListAndGraph() {
      try {
        const res = await listResource({ courseId: this.courseId, pageSize: 999 });
        this.resourceList = (res.rows || []).filter(r => r.resourceType && r.resourceType.toLowerCase() === 'pdf');
       
        console.log(this.resourceList.map(r => r.resourcePath)); // 应该是非空数组
        console.log('resourceList:', this.resourceList);
        await this.fetchGraph();
      } catch (e) {
        this.$message.error('资源加载失败');
      }
    },
    async fetchGraph(isPolling = false) {
      if (this.loading) return;
      this.loading = true;
      try {
        const pdfNames = this.resourceList.map(r => r.resourcePath);
        if (!pdfNames.length) {
          this.$message.error('没有可用的PDF资源，无法查询知识图谱');
          this.loading = false;
          return;
        }
        const data = {
          courseId: this.courseId,
          pdfNames
        };
        if (this.fileName) data.fileName = this.fileName;
        const res = await request({
          url: '/system/kg/graph',
          method: 'post',
          data
        });
        // 控制台输出后端返回数据
        console.log('知识图谱后端返回：', res);
        this.nodes = (res.nodes || []).map(n => {
          let match = this.resourceList.find(r => r.fileName === n.fileName);
          if (!match && n.name) {
            match = this.resourceList.find(r => r.fileName === n.name || r.resourceName === n.name);
          }
          let resourcePath = match ? match.resourcePath : n.fileName;
          // 去掉/profile/前缀，并规范化斜杠
          if (resourcePath && resourcePath.startsWith('/profile/')) {
            resourcePath = resourcePath.replace(/^\/profile\/+/, '/');
          }
          resourcePath = resourcePath.replace(/\/+/g, '/'); // 替换多余斜杠
          return {
            ...n,
            resourcePath
          };
        });
        this.edges = JSON.parse(JSON.stringify(res.edges || []));
        this.renderGraph();
        this.polling = false;
        this.pollingCount = 0;
        this.progressPercent = 100;
        if (this.pollingTimer) {
          clearTimeout(this.pollingTimer);
          this.pollingTimer = null;
        }
      } catch (e) {
        // 只要有数据就渲染
        if (this.nodes && this.nodes.length && this.edges && this.edges.length) {
          this.renderGraph();
          this.loading = false;
          return;
        }
        // 只要是"正在初始化"或"数据正在处理"，都自动轮询
        const msg = (e && e.message) ? e.message : '';
        if (msg.indexOf('数据正在处理，请勿重复提交') !== -1 || msg.indexOf('知识图谱正在初始化') !== -1) {
          this.showPollingTip();
          this.startPolling();
          this.loading = false;
          return;
        }
        this.$message.error(msg || '知识图谱加载失败');
      } finally {
        this.loading = false;
      }
    },
    initChart() {
      if (!this.chart) {
        this.chart = echarts.init(this.$refs.graphChart);
        window.addEventListener('resize', () => this.chart.resize());
      }
    },
    renderGraph() {
      try {
        this.initChart();
        const option = {
          title: { text: this.fileName ? '章节知识图谱' : '课程知识图谱', left: 'center' },
          tooltip: { trigger: 'item' },
          series: [{
            type: 'graph',
            layout: 'force',
            roam: true,
            data: this.nodes.map(n => ({
              id: (n.fileName ? n.fileName + '_' : '') + n.name,
              name: n.name,
              category: n.fileName || '知识点',
              symbolSize: 50,
              ...n,
              resourcePath: n.resourcePath,
              label: {
                show: true,
                formatter: n.name
              }
            })),
            links: this.edges.map(e => ({
              source: (e.fileName ? e.fileName + '_' : '') + e.source,
              target: (e.fileName ? e.fileName + '_' : '') + e.target,
              label: { show: true, formatter: e.type }
            })),
            categories: Array.from(new Set(this.nodes.map(n => n.fileName || '知识点'))).map(f => ({ name: f })),
            force: { repulsion: 200, edgeLength: 120 }
          }]
        };
        console.log('ECharts data:', this.nodes.map(n => ({
          id: (n.fileName ? n.fileName + '_' : '') + n.name,
          name: n.name,
          category: n.fileName || '知识点',
          symbolSize: 50,
          ...n,
          resourcePath: n.resourcePath
        })));
        this.chart.setOption(option);
        this.chart.off('click');
        this.chart.on('click', params => {
          // 1. 获取当前节点的 fileName 或 category
          const fileName = params.data.fileName || params.data.category;
          // 2. 在 resourceList 里查找对应的 resourcePath
          let match = this.resourceList.find(r => r.fileName === fileName);
          if (!match && fileName) {
            // 兜底用 name 匹配
            match = this.resourceList.find(r => r.resourceName === fileName);
          }
          let resourcePath = match ? match.resourcePath : fileName;
          // 3. 去掉/profile/前缀，规范化斜杠
          if (resourcePath && resourcePath.startsWith('/profile/')) {
            resourcePath = resourcePath.replace(/^\/profile\/+/, '/');
          }
          resourcePath = resourcePath.replace(/\/+/g, '/');
          // 4. 跳转
          if (resourcePath) {
            this.$router.push({
              name: 'PdfKnowledgeGraph',
              query: {
                courseId: this.courseId,
                fileName: resourcePath,
                node: params.data.name
              }
            });
            console.log('resourcePath:', resourcePath, 'node:', params.data.name);
          } else {
            this.$message.error('未找到对应PDF资源路径，无法跳转');
          }
        });
      } catch (e) {
        console.error('ECharts 渲染异常', e);
        this.$message.error('知识图谱渲染失败');
      }
    },
    showPollingTip() {
      if (!this.polling) {
        this.$message.info('知识图谱正在初始化，请稍后刷新');
      }
    },
    startPolling() {
      if (this.polling) return;
      this.polling = true;
      this.pollingCount = 0;
      this.progressPercent = 0;
      this.doPolling();
    },
    doPolling() {
      if (this.pollingCount >= this.maxPolling) {
        this.polling = false;
        this.pollingCount = 0;
        this.progressPercent = 0;
        this.$message.error('知识图谱初始化超时，请稍后手动刷新');
        return;
      }
      this.pollingCount++;
      this.progressPercent = Math.floor((this.pollingCount / this.maxPolling) * 100);
      this.pollingTimer = setTimeout(() => {
        this.fetchGraph(true);
      }, 2000);
    },
    goBackCourseDetail() {
      // 跳转到课程详情页，带上 courseId
      this.$router.push({
        name: 'CourseDetail', // 这里用你的课程详情页的路由name
        query: { courseId: this.courseId }
      });
    }
  },
  beforeDestroy() {
    if (this.pollingTimer) {
      clearTimeout(this.pollingTimer);
    }
  }
};
</script>

<style scoped>
.knowledge-graph-container { margin: 20px 0; }
.kg-toolbar { margin-bottom: 10px; }
.kg-graph { width: 100%; min-height: 400px; background: #fff; border-radius: 8px; }
</style>
