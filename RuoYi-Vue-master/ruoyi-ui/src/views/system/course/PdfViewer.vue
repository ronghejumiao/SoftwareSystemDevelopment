<template>
  <div class="pdf-viewer-container">
    <el-button @click="$router.back()" style="margin-bottom:10px;">返回</el-button>
    <div class="pdf-toolbar">
      <el-input v-model="searchText" placeholder="高亮关键词" style="width:200px;margin-right:10px;" @keyup.enter="highlightText"/>
      <el-button @click="highlightText">高亮</el-button>
    </div>
    <pdf
      ref="pdfRef"
      :src="pdfUrl"
      :page="page"
      @progress="onProgress"
      @loaded="onLoaded"
      style="width:100%;height:80vh;"
    />
    <div class="pdf-pagination">
      <el-button @click="prevPage" :disabled="page<=1">上一页</el-button>
      <span>第 {{page}} / {{numPages}} 页</span>
      <el-button @click="nextPage" :disabled="page>=numPages">下一页</el-button>
    </div>
  </div>
</template>

<script>
import pdf from 'vue-pdf';

export default {
  name: 'PdfViewer',
  components: { pdf },
  data() {
    return {
      pdfUrl: '',
      page: 1,
      numPages: 1,
      searchText: '',
      nodeName: ''
    };
  },
  mounted() {
    this.pdfUrl = process.env.VUE_APP_BASE_API + '/common/download/resource?resource=' + this.$route.query.file;
    this.nodeName = this.$route.query.node || '';
    if (this.nodeName) {
      this.searchText = this.nodeName;
      // 等pdf加载后自动高亮
      setTimeout(this.highlightText, 1000);
    }
  },
  methods: {
    onProgress(e) {},
    onLoaded(pdf) {
      this.numPages = pdf.numPages;
    },
    prevPage() {
      if (this.page > 1) this.page--;
    },
    nextPage() {
      if (this.page < this.numPages) this.page++;
    },
    highlightText() {
      // 简单高亮实现：查找文本并滚动到所在页
      // vue-pdf 暂不支持原生高亮，建议用pdf.js原生API
      // 这里只做定位到包含关键词的页面
      const pdfComponent = this.$refs.pdfRef;
      if (!pdfComponent || !this.searchText) return;
      for (let i = 1; i <= this.numPages; i++) {
        pdfComponent.pdf.getPage(i).then(page => {
          page.getTextContent().then(tc => {
            const text = tc.items.map(item => item.str).join('');
            if (text.includes(this.searchText)) {
              this.page = i;
            }
          });
        });
      }
    }
  }
};
</script>

<style scoped>
.pdf-viewer-container { background: #fff; padding: 20px; border-radius: 8px; }
.pdf-toolbar { margin-bottom: 10px; }
.pdf-pagination { margin-top: 10px; text-align: center; }
</style> 