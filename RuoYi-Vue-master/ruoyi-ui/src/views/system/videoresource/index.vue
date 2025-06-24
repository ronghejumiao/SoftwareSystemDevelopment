<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="课程编号" prop="courseCode">
        <el-input
          v-model="queryParams.courseCode"
          placeholder="请输入课程编号"
          clearable
          @keyup.enter.native="handleCourseCodeChange"
          @clear="handleCourseCodeChange"
        />
      </el-form-item>
      <el-form-item label="视频名称" prop="videoName">
        <el-input
          v-model="queryParams.videoName"
          placeholder="请输入视频名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="视频大小" prop="fileSize">
        <el-input
          v-model="queryParams.fileSize"
          placeholder="请输入视频大小"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="视频时长" prop="duration">
        <el-input
          v-model="queryParams.duration"
          placeholder="请输入视频时长"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="上传用户 ID" prop="suploaderId">
        <el-input
          v-model="queryParams.suploaderId"
          placeholder="请输入上传用户 ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="上传时间" prop="uploadTime">
        <el-date-picker clearable
          v-model="queryParams.uploadTime"
          type="date"
          value-format="yyyy-MM-dd"
          placeholder="请选择上传时间">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:videoresource:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:videoresource:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:videoresource:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:videoresource:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="videoresourceList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="视频资源" align="center" prop="videoId" />
      <el-table-column label="所属课程" align="center" prop="courseName" />
      <el-table-column label="视频章节" align="center" prop="videoName" />
      <el-table-column label="视频类型" align="center" prop="videoType" />
      <el-table-column label="视频存储路径" align="center" prop="videoPath" width="100">
        <template slot-scope="scope">
          <video v-if="scope.row.videoPath" :src="baseApi + scope.row.videoPath" width="80" height="50" controls />
        </template>
      </el-table-column>
      <el-table-column label="视频大小" align="center" prop="fileSize" />
      <el-table-column label="视频名称" align="center" prop="description" />
      <el-table-column label="上传用户 ID" align="center" prop="suploaderId" />
      <el-table-column label="上传时间" align="center" prop="uploadTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.uploadTime, '{y}-{m}-{d}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="视频封面路径" align="center" prop="thumbnail" width="100">
        <template slot-scope="scope">
          <img v-if="scope.row.thumbnail" :src="baseApi + scope.row.thumbnail" width="50" height="50" />
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:videoresource:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:videoresource:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>
    
    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改视频学习资源对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="课程编号" prop="courseCode">
          <el-input 
            v-model="form.courseCode" 
            placeholder="请输入课程编号"
            @blur="handleFormCourseCodeChange"
          />
        </el-form-item>
        <el-form-item label="视频章节" prop="videoName">
          <el-input v-model="form.videoName" placeholder="请输入视频章节" />
        </el-form-item>
        <el-form-item label="视频文件" prop="videoPath">
          <el-upload
            :action="upload.videoUrl"
            :headers="upload.headers"
            :on-success="handleVideoSuccess"
            :on-error="handleError"
            :before-upload="beforeVideoUpload"
            :file-list="videoList"
            :show-file-list="true"
            accept=".mp4"
            multiple>
            <el-button size="small" type="primary">点击上传视频</el-button>
            <div slot="tip" class="el-upload__tip">只能上传mp4格式视频文件</div>
          </el-upload>
          <video
            v-if="form.videoPath"
            :src="baseApi + form.videoPath"
            style="margin-top: 10px; width: 100%; max-width: 400px; height: 220px; border:1px solid #eee;"
            controls
          />
        </el-form-item>
        <el-form-item label="视频封面" prop="thumbnail">
          <el-upload
            class="avatar-uploader"
            :action="upload.imageUrl"
            :headers="upload.headers"
            :show-file-list="false"
            :on-success="handleThumbnailSuccess"
            :on-error="handleError"
            :before-upload="beforeThumbnailUpload">
            <img v-if="imageUrl" :src="imageUrl" class="avatar">
            <el-button v-if="imageUrl" @click="removeThumbnail" type="danger" size="mini">删除</el-button>
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
            <div slot="tip" class="el-upload__tip">建议上传16:9比例的图片</div>
          </el-upload>
        </el-form-item>
        <el-form-item label="上传用户ID" prop="suploaderId">
          <el-input v-model="form.suploaderId" placeholder="请输入上传用户ID" />
        </el-form-item>
        <el-form-item label="视频名称" prop="description">
          <el-input v-model="form.description" type="textarea" placeholder="请输入视频名称" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listVideoresource, getVideoresource, delVideoresource, addVideoresource, updateVideoresource } from "@/api/system/videoresource"
import { listCourse } from "@/api/system/course"
import { getToken } from "@/utils/auth"

export default {
  name: "Videoresource",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 视频学习资源表格数据
      videoresourceList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 课程选项列表
      courseOptions: [],
      // 课程编号到ID的映射
      courseCodeToId: {},
      // 课程ID到编号的映射
      courseIdToCode: {},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        courseId: null,
        courseCode: null,
        videoName: null,
        videoType: null,
        videoPath: null,
        fileSize: null,
        duration: null,
        suploaderId: null,
        uploadTime: null,
        description: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        courseCode: [
          { required: true, message: "所属课程不能为空", trigger: "change" }
        ],
        videoName: [
          { required: true, message: "视频名称不能为空", trigger: "blur" }
        ],
        videoType: [
          { required: true, message: "视频类型不能为空", trigger: "change" }
        ],
        videoPath: [
          { required: true, message: "视频存储路径不能为空", trigger: "blur" }
        ],
        fileSize: [
          { required: true, message: "视频大小不能为空", trigger: "blur" }
        ],
        suploaderId: [
          { required: true, message: "上传用户 ID不能为空", trigger: "blur" }
        ],
      },
      // 上传参数
      upload: {
        // 上传地址
        videoUrl: process.env.VUE_APP_BASE_API + "/system/videoresource/uploadVideo",
        imageUrl: process.env.VUE_APP_BASE_API + "/system/videoresource/uploadThumbnail",
        // 设置上传的请求头部
        headers: {
          Authorization: "Bearer " + getToken()
        }
      },
      videoList: [],
      imageUrl: '',
      // 基础API地址
      baseApi: process.env.VUE_APP_BASE_API
    }
  },
  created() {
    this.getCourseOptions()
  },
  methods: {
    /** 查询课程列表 */
    getCourseOptions() {
      listCourse().then(response => {
        this.courseOptions = response.rows;
        // 建立课程编号和ID的映射关系
        this.courseOptions.forEach(course => {
          this.courseCodeToId[course.courseCode] = course.courseId;
          this.courseIdToCode[course.courseId] = course.courseCode;
        });
        this.getList()
      });
    },
    /** 处理查询表单课程编号变化 */
    handleCourseCodeChange() {
      const courseCode = this.queryParams.courseCode;
      if (courseCode) {
        this.queryParams.courseId = this.courseCodeToId[courseCode];
        if (!this.queryParams.courseId) {
          this.$modal.msgError("课程编号不存在");
          this.queryParams.courseId = null;
        }
      } else {
        this.queryParams.courseId = null;
      }
      this.handleQuery()
    },
    /** 处理表单课程编号变化 */
    handleFormCourseCodeChange() {
      const courseCode = this.form.courseCode;
      if (courseCode) {
        this.form.courseId = this.courseCodeToId[courseCode];
        if (!this.form.courseId) {
          this.$modal.msgError("课程编号不存在");
          this.form.courseId = null;
        }
      } else {
        this.form.courseId = null;
      }
    },
    /** 查询视频学习资源列表 */
    getList() {
      this.loading = true
      // 发送查询请求时使用courseId
      const queryParams = { ...this.queryParams };
      delete queryParams.courseCode; // 删除courseCode，只传courseId给后端
      listVideoresource(queryParams).then(response => {
        this.videoresourceList = response.rows.map(item => {
          // 根据courseId查找对应的课程信息
          const courseCode = this.courseIdToCode[item.courseId];
          const course = this.courseOptions.find(c => c.courseCode === courseCode);
          return {
            ...item,
            courseCode: courseCode,
            courseName: course ? course.courseName : '未知课程'
          };
        });
        this.total = response.total
        this.loading = false
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        videoId: null,
        courseId: null,
        courseCode: null,
        videoName: null,
        videoType: null,
        videoPath: null,
        fileSize: null,
        duration: null,
        suploaderId: null,
        uploadTime: null,
        thumbnail: null,
        description: null
      };
      this.resetForm("form");
      this.imageUrl = '';
      this.videoList = [];
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.videoId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加视频学习资源";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const videoId = row.videoId || this.ids;
      getVideoresource(videoId).then(response => {
        const data = response.data;
        this.form = data;
        this.form.courseCode = this.courseIdToCode[data.courseId];
        // 只赋值相对路径
        if (data.thumbnail) {
          this.imageUrl = process.env.VUE_APP_BASE_API + data.thumbnail;
        } else {
          this.imageUrl = '';
        }
        // 只赋值相对路径
        if (data.videoPath) {
          this.form.videoPath = data.videoPath;
        }
        this.open = true;
        this.title = "修改视频学习资源";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          this.form.videoType = "MP4";
          if (!this.form.courseId) {
            this.$modal.msgError("课程编号不存在");
            return;
          }
          // 提交时确保使用courseId
          const submitForm = { ...this.form };
          delete submitForm.courseCode; // 删除courseCode，只传courseId给后端
          
          if (submitForm.videoId != null) {
            updateVideoresource(submitForm).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addVideoresource(submitForm).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const videoIds = row.videoId || this.ids
      this.$modal.confirm('是否确认删除视频学习资源编号为"' + videoIds + '"的数据项？').then(function() {
        return delVideoresource(videoIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/videoresource/export', {
        ...this.queryParams
      }, `videoresource_${new Date().getTime()}.xlsx`)
    },
    // 视频上传成功回调
    handleVideoSuccess(response, file, fileList) {
      if (response.code === 200) {
        // 只存相对路径
        this.form.videoPath = response.videoUrl;
        this.form.fileSize = response.fileSize;
        this.form.duration = response.duration;
        this.form.uploadTime = response.uploadTime;
        // 自动赋值首帧为封面
        if (response.coverImage) {
          this.form.thumbnail = response.coverImage; // 只存相对路径
          this.imageUrl = process.env.VUE_APP_BASE_API + response.coverImage; // 预览用
        }
        this.$modal.msgSuccess("视频上传成功");
      } else {
        this.$modal.msgError(response.msg);
      }
    },
    // 封面上传成功回调
    handleThumbnailSuccess(response, file) {
      if (response.code === 200) {
        this.imageUrl = process.env.VUE_APP_BASE_API + response.thumbnailUrl;
        this.form.thumbnail = response.thumbnailUrl; // 只存相对路径
        this.$modal.msgSuccess("封面上传成功");
      } else {
        this.$modal.msgError(response.msg);
      }
    },
    // 上传失败回调
    handleError(err) {
      this.$modal.msgError("上传失败");
    },
    // 视频上传前的校验
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
    // 封面图片上传前的校验
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
      this.imageUrl = '';
      this.form.thumbnail = '';
    },
  }
}
</script>

<style>
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
</style>
