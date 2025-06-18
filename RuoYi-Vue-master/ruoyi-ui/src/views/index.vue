<template>
  <div class="app-container home">
    <!-- 轮播图 -->
    <el-carousel
      height="400px"
      indicator-position="none"
      arrow="never"
      motion-blur
      type="card"
      :autoplay="true"
      v-on="{
        mouseenter: $event => $event.stopPropagation(),
        mouseleave: $event => $event.stopPropagation()
      }"
    >
      <el-carousel-item v-for="(item, index) in carouselImages" :key="index">
        <img :src="item.src" :alt="item.alt" class="carousel-image">
      </el-carousel-item>
    </el-carousel>
    <el-divider />


    <!-- 新增内容容器，添加padding -->
    <div class="content-container px-4 md:px-8 py-8">


      <!-- 平台核心优势模块 -->
      <h1 class="text-center my-6">AI教育技术优势</h1>
      <el-divider />
      <h2 class="text-center text-gray-600 mb-8">深度融合人工智能技术，构建智能化教育生态</h2>

      <!-- 优势模块1：图片在左，文字在右 -->
      <div class="advantage-section mb-16">
        <div class="advantage-container">
          <div class="advantage-image">
            <img :src="require('@/assets/images/advantage1.png')" alt="智能推荐" class="w-full h-full object-cover rounded-lg shadow-lg">
          </div>
          <div class="advantage-content">
            <div class="advantage-icon">
              <i class="el-icon-star-on"></i>
            </div>
            <h3 class="advantage-title">个性化学习推荐</h3>
            <p class="advantage-description">基于大模型分析学习行为，精准推荐适配课程与知识点，学习效率提升40%+</p>
            <ul class="advantage-features">
              <li>智能分析学习轨迹</li>
              <li>精准匹配学习需求</li>
              <li>动态调整推荐策略</li>
            </ul>
          </div>
        </div>
      </div>

      <!-- 优势模块2：图片在右，文字在左 -->
      <div class="advantage-section mb-16">
        <div class="advantage-container reverse">
          <div class="advantage-content">
            <div class="advantage-icon">
              <i class="el-icon-connection"></i>
            </div>
            <h3 class="advantage-title">知识图谱构建</h3>
            <p class="advantage-description">自动生成课程知识网络，可视化展示知识点关联，点击直达对应学习内容</p>
            <ul class="advantage-features">
              <li>自动构建知识网络</li>
              <li>可视化关联展示</li>
              <li>智能路径推荐</li>
            </ul>
          </div>
          <div class="advantage-image">
            <img :src="require('@/assets/images/advantage2.png')" alt="知识图谱" class="w-full h-full object-cover rounded-lg shadow-lg">
          </div>
        </div>
      </div>

      <!-- 优势模块3：图片在左，文字在右 -->
      <div class="advantage-section mb-16">
        <div class="advantage-container">
          <div class="advantage-image">
            <img :src="require('@/assets/images/advantage3.png')" alt="智能批改" class="w-full h-full object-cover rounded-lg shadow-lg">
          </div>
          <div class="advantage-content">
            <div class="advantage-icon">
              <i class="el-icon-edit-outline"></i>
            </div>
            <h3 class="advantage-title">AI智能批改系统</h3>
            <p class="advantage-description">支持报告、论文、简答题智能评分，自动标注错误，教师批改效率提升80%</p>
            <ul class="advantage-features">
              <li>多题型智能评分</li>
              <li>自动错误标注</li>
              <li>批改效率大幅提升</li>
            </ul>
          </div>
        </div>
      </div>

      <el-divider />

      <!-- 合作高校模块 -->
      <h1 class="text-center my-6 mt-16">合作高校</h1>
      <el-divider />
      <h2 class="text-center text-gray-600 mb-8">携手800+高校共建智能教育生态</h2>

      <div class="university-grid">
        <div v-for="(uni, index) in universityLogos" :key="index" class="university-item">
          <div class="university-logo-container">
            <img :src="uni.src" :alt="uni.alt" class="university-logo">
          </div>
        </div>
      </div>

      <el-divider />

      <!-- 课程资源模块 -->
      <h1 class="text-center my-6 mt-16">精品课程资源</h1>
      <el-divider />
      <h2 class="text-center text-gray-600 mb-8">收录5000+优质课程，覆盖全学科学习需求</h2>

      <div class="course-carousel-container">
        <el-carousel height="300px" indicator-position="outside" arrow="always" class="mt-8" type="card" motion-blur>
          <el-carousel-item v-for="(course, index) in courseCarousel" :key="index" class="course-carousel-item">
            <div class="course-image-container">
              <img :src="course.src" :alt="course.alt" class="course-image">
            </div>
          </el-carousel-item>
        </el-carousel>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: "Index",
  data() {
    return {
      version: "3.9.0",
      carouselImages: [
        { src: require('@/assets/images/mainConcept1.png'), alt: "主要概念图一" },
        { src: require('@/assets/images/mainConcept2.png'), alt: "主要概念图二" },
        { src: require('@/assets/images/mainConcept3.png'), alt: "主要概念图三" }
      ],

      universityLogos: [
        { src: require('@/assets/images/university1.png'), alt: "高校LOGO1" },
        { src: require('@/assets/images/university2.png'), alt: "高校LOGO2" },
        { src: require('@/assets/images/university3.png'), alt: "高校LOGO3" },
        { src: require('@/assets/images/university4.png'), alt: "高校LOGO4" },
        { src: require('@/assets/images/university5.png'), alt: "高校LOGO5" },
        { src: require('@/assets/images/university6.png'), alt: "高校LOGO6" },
        { src: require('@/assets/images/university7.png'), alt: "高校LOGO7" },
        { src: require('@/assets/images/university8.png'), alt: "高校LOGO8" },
        { src: require('@/assets/images/university9.png'), alt: "高校LOGO9" },
        { src: require('@/assets/images/university10.png'), alt: "高校LOGO10" },
        { src: require('@/assets/images/university11.png'), alt: "高校LOGO11" },
        { src: require('@/assets/images/university12.png'), alt: "高校LOGO12" },
      ],

      courseCarousel: [
        { src: require('@/assets/images/courseConcept1.png'), alt: "课程轮播图1" },
        { src: require('@/assets/images/courseConcept2.png'), alt: "课程轮播图2" },
        { src: require('@/assets/images/courseConcept3.png'), alt: "课程轮播图3" },
        { src: require('@/assets/images/courseConcept4.png'), alt: "课程轮播图4" },
        { src: require('@/assets/images/courseConcept5.png'), alt: "课程轮播图5" },
        { src: require('@/assets/images/courseConcept6.png'), alt: "课程轮播图6" },
        { src: require('@/assets/images/courseConcept7.png'), alt: "课程轮播图7" },
      ]


    }
  },
  methods: {
    goTarget(href) {
      window.open(href, "_blank")
    }
  }
}
</script>

<style scoped lang="scss">
.app-container{
  padding: 0;
}


.home {
  blockquote {
    margin: 0 0 20px;
    font-size: 17.5px;
    border-left: 5px solid #eee;
  }
  hr {
    margin-top: 20px;
    margin-bottom: 20px;
    border: 0;
    border-top: 1px solid #eee;
  }
  .col-item {
    margin-bottom: 20px;
  }

  ul {
    padding: 0;
    margin: 0;
  }

  font-family: "open sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
  font-size: 13px;
  color: #676a6c;
  overflow-x: hidden;

  ul {
    list-style-type: none;
  }

  h4 {
    margin-top: 0px;
  }

  h2 {
    margin-top: 10px;
    font-size: 26px;
    font-weight: 100;
  }

  p {
    margin-top: 10px;
    b {
      font-weight: 700;
    }
  }

  .update-log {
    ol {
      display: block;
      list-style-type: decimal;
      margin-block-start: 1em;
      margin-block-end: 1em;
      margin-inline-start: 0;
      margin-inline-end: 0;
      padding-inline-start: 40px;
    }
  }

  /* 轮播图样式 */
  .carousel-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
  }

  /* 移除最大宽度限制 */
  .el-carousel {
    max-width: none;
    margin: 0;
  }

  // 新增内容容器样式
  .content-container {
    // 移动端左右边距16px，桌面端32px，上下边距32px
    padding-top: 1rem;
    padding-left: 10rem;
    padding-right: 10rem;
  }

  // 原有模块样式
  h1 {
    font-size: 28px;
    font-weight: 600;
  }

  h2 {
    font-size: 20px;
    font-weight: 400;
    color: #676a6c;
  }

  .text-center {
    text-align: center;
  }

  .my-6 {
    margin: 24px 0;
  }

  .mt-8 {
    margin-top: 32px;
  }

  // 响应式网格适配
  @media (max-width: 768px) {
    .grid {
      grid-template-columns: 1fr;
    }
  }

  // 优势模块样式
  .advantage-section {
    margin-bottom: 4rem;
  }

  .advantage-container {
    display: flex;
    align-items: center;
    gap: 3rem;
    max-width: 1200px;
    margin: 0 auto;
    padding: 2rem;
    background: linear-gradient(135deg, #f8fafc 0%, #e2e8f0 100%);
    border-radius: 16px;
    box-shadow: 0 10px 25px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease, box-shadow 0.3s ease;

    &:hover {
      transform: translateY(-5px);
      box-shadow: 0 20px 40px rgba(0, 0, 0, 0.15);
    }

    &.reverse {
      flex-direction: row-reverse;
    }
  }

  .advantage-image {
    flex: 1;
    height: 300px;
    overflow: hidden;
    border-radius: 12px;
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: 0;
      right: 0;
      bottom: 0;
      background: linear-gradient(45deg, rgba(59, 130, 246, 0.1), rgba(147, 51, 234, 0.1));
      z-index: 1;
      opacity: 0;
      transition: opacity 0.3s ease;
    }

    &:hover::before {
      opacity: 1;
    }

    img {
      width: 100%;
      height: 100%;
      object-fit: cover;
      object-position: center;
      transition: transform 0.3s ease;
      border-radius: 12px;
    }

    &:hover img {
      transform: scale(1.05);
    }
  }

  .advantage-content {
    flex: 1;
    padding: 1rem;
  }

  .advantage-icon {
    width: 60px;
    height: 60px;
    background: linear-gradient(135deg, #3b82f6, #8b5cf6);
    border-radius: 50%;
    display: flex;
    align-items: center;
    justify-content: center;
    margin-bottom: 1.5rem;
    box-shadow: 0 8px 20px rgba(59, 130, 246, 0.3);

    i {
      font-size: 24px;
      color: white;
    }
  }

  .advantage-title {
    font-size: 24px;
    font-weight: 600;
    color: #1e293b;
    margin-bottom: 1rem;
    position: relative;

    &::after {
      content: '';
      position: absolute;
      bottom: -8px;
      left: 0;
      width: 40px;
      height: 3px;
      background: linear-gradient(90deg, #3b82f6, #8b5cf6);
      border-radius: 2px;
    }
  }

  .advantage-description {
    font-size: 16px;
    line-height: 1.6;
    color: #64748b;
    margin-bottom: 1.5rem;
  }

  .advantage-features {
    list-style: none;
    padding: 0;
    margin: 0;

    li {
      position: relative;
      padding-left: 1.5rem;
      margin-bottom: 0.75rem;
      color: #475569;
      font-size: 14px;

      &::before {
        content: '✓';
        position: absolute;
        left: 0;
        top: 0;
        color: #10b981;
        font-weight: bold;
        font-size: 16px;
      }
    }
  }

  // 响应式适配
  @media (max-width: 768px) {
    .advantage-container {
      flex-direction: column !important;
      gap: 1.5rem;
      padding: 1rem;
    }

    .advantage-image {
      height: 200px;
      width: 100%;
      min-height: 200px;
    }

    .advantage-content {
      padding: 0.5rem;
    }

    .advantage-title {
      font-size: 20px;
    }

    .advantage-description {
      font-size: 14px;
    }
  }

  // 合作高校模块样式
  .university-grid {
    display: grid;
    grid-template-columns: repeat(4, 1fr);
    gap: 2rem;
    max-width: 1200px;
    margin: 0 auto;
    padding: 2rem 0;
  }

  .university-item {
    display: flex;
    justify-content: center;
    align-items: center;
  }

  .university-logo-container {
    width: 100%;
    height: 120px;
    background: white;
    border-radius: 12px;
    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
    display: flex;
    align-items: center;
    justify-content: center;
    padding: 1rem;
    transition: all 0.3s ease;
    border: 2px solid transparent;
    position: relative;
    overflow: hidden;

    &::before {
      content: '';
      position: absolute;
      top: 0;
      left: -100%;
      width: 100%;
      height: 100%;
      background: linear-gradient(90deg, transparent, rgba(59, 130, 246, 0.1), transparent);
      transition: left 0.5s ease;
    }

    &:hover {
      transform: translateY(-4px);
      box-shadow: 0 12px 30px rgba(59, 130, 246, 0.2);
      border-color: #3b82f6;
      background: linear-gradient(135deg, #ffffff 0%, #f8fafc 100%);

      &::before {
        left: 100%;
      }
    }
  }

  .university-logo {
    max-width: 100%;
    max-height: 100%;
    width: auto;
    height: auto;
    object-fit: contain;
    transition: all 0.3s ease;
    position: relative;
    z-index: 2;

    &:hover {
      transform: scale(1.08);
    }
  }

  // 响应式布局
  @media (max-width: 1024px) {
    .university-grid {
      grid-template-columns: repeat(3, 1fr);
      gap: 1.5rem;
    }
  }

  @media (max-width: 768px) {
    .university-grid {
      grid-template-columns: repeat(2, 1fr);
      gap: 1rem;
      padding: 1rem 0;
    }

    .university-logo-container {
      height: 100px;
      padding: 0.75rem;
    }
  }

  @media (max-width: 480px) {
    .university-grid {
      grid-template-columns: repeat(2, 1fr);
      gap: 0.75rem;
    }

    .university-logo-container {
      height: 80px;
      padding: 0.5rem;
    }
  }

  // 课程轮播图样式
  .course-carousel-container {
    max-width: 1200px;
    margin: 0 auto;
    padding: 0 2rem;
  }

  .course-carousel-item {
    display: flex;
    align-items: center;
    justify-content: center;
  }

  .course-image-container {
    width: 100%;
    height: 100%;
    overflow: hidden;
    border-radius: 12px;
    position: relative;
    display: flex;
    align-items: center;
    justify-content: center;
    background: #f8fafc;
  }

  .course-image {
    width: 100%;
    height: 100%;
    object-fit: cover;
    object-position: center;
    border-radius: 12px;
    transition: transform 0.3s ease;
  }

  .course-image-container:hover .course-image {
    transform: scale(1.05);
  }

  // 轮播图卡片样式优化
  .el-carousel__item {
    border-radius: 12px;
    overflow: hidden;
  }

  // 响应式适配
  @media (max-width: 768px) {
    .course-carousel-container {
      padding: 0 1rem;
    }
  }
}
</style>
