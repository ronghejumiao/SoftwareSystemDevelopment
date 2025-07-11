<template>
  <div class="pdf-kg-container">
    <el-button @click="goBackCourseDetail" style="margin-bottom:10px;">返回课程详情</el-button>
    <div style="display:flex;gap:30px;align-items:flex-start;">
      <div style="flex:1;min-width:400px;">
        <KnowledgeGraph :courseId="courseId" :fileName="fileName" :graphData="graphData" />
      </div>
      <div style="flex:1;min-width:400px;">
        <pdf v-if="pdfBlobUrl" :src="pdfBlobUrl" style="width:100%;height:800px;" @rendered="highlightNodeName" />
        <div v-else>正在加载PDF...</div>
      </div>
    </div>
  </div>
</template>

<script>
import KnowledgeGraph from './KnowledgeGraph.vue';
import pdf from 'vue-pdf';
import { getToken } from '@/utils/auth';
import request from '@/utils/request';
import { listResource } from '@/api/system/resource';
import * as echarts from 'echarts';

export default {
  name: 'PdfKnowledgeGraph',
  components: { KnowledgeGraph, pdf },
  data() {
    return {
      courseId: this.$route.query.courseId,
      fileName: this.$route.query.fileName, // 这里只是文件名
      nodeName: this.$route.query.node,
      loading: false,
      graphData: { nodes: [], edges: [] },
      pdfBlobUrl: ''
    };
  },
  mounted() {
    this.loadResourcePathAndPdf();
    this.fetchGraph();
  },
  watch: {
    pdfBlobUrl(newVal) {
      if (newVal && this.nodeName) {
        this.$nextTick(() => {
          setTimeout(this.highlightNodeName, 1000); // 等待PDF渲染
        });
      }
    }
  },
  methods: {
    goBackCourseDetail() {
      this.$router.push({
        path: '/system/course/detail',
        query: { courseId: this.courseId, tab: 'resources' }
      });
    },
    // 1. 查找 resourcePath 并加载 PDF
    async loadResourcePathAndPdf() {
      try {
        this.loading = true;
        // 查询后端，获取完整 resourcePath
        const res = await listResource({ courseId: this.courseId, pageSize: 999 });
        console.log('resourceList:', res);
        const resourceList = (res.rows || []).filter(r => r.resourceType && r.resourceType.toLowerCase() === 'pdf');
        // 用 fileName 精确匹配
        let match = resourceList.find(r => r.resourcePath && r.resourcePath.endsWith('/' + this.fileName));
        if (!match && this.fileName) {
          match = resourceList.find(r => r.resourceName === this.fileName);
        }
        let resourcePath = match ? match.resourcePath : '';
        console.log('匹配到的resourcePath:', resourcePath);
        if (!resourcePath) {
          this.$message.error('未找到对应PDF资源路径');
          return;
        }
        // 去掉/profile/前缀，规范化斜杠
        if (resourcePath.startsWith('/profile/')) {
          resourcePath = resourcePath.replace(/^\/profile\/+/, '/');
        }
        resourcePath = resourcePath.replace(/\/+/g, '/');
        if (!resourcePath.startsWith('/')) resourcePath = '/' + resourcePath;
        // 请求 PDF
        const token = getToken();
        console.log('最终请求PDF的resourcePath:', resourcePath);
        const pdfRes = await request({
          url: '/common/download/resource?resource=' + encodeURIComponent(resourcePath),
          method: 'get',
          responseType: 'blob',
          headers: { Authorization: 'Bearer ' + token }
        });
        this.pdfBlobUrl = URL.createObjectURL(pdfRes);
      } catch (e) {
        this.$message.error('PDF加载失败');
      } finally {
        this.loading = false;
      }
    },
    // 2. 知识图谱相关
    async fetchGraph() {
      try {
        const res = await request({
          url: '/system/kg/graph',
          method: 'post',
          data: {
            courseId: this.courseId,
            pdfNames: [this.fileName],
            fileName: this.fileName
          }
        });
        this.graphData = res || { nodes: [], edges: [] };
      } catch (e) {
        this.$message.error('知识图谱加载失败');
      }
    },
    highlightNodeName() {
      if (!this.nodeName) return;
      // 查找所有PDF文本层
      const textLayers = document.querySelectorAll('.textLayer');
      textLayers.forEach(layer => {
        layer.innerHTML = layer.innerHTML.replace(
          new RegExp(this.nodeName, 'g'),
          `<span style="background: yellow; color: red;">${this.nodeName}</span>`
        );
      });
    }
  }
}
</script>

<style scoped>
.pdf-kg-container { background: #fff; padding: 20px; border-radius: 8px; }
</style> 