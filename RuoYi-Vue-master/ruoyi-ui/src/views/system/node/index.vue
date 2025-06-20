<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="课程ID，关联course表" prop="courseId">
        <el-input
          v-model="queryParams.courseId"
          placeholder="请输入课程ID，关联course表"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="节点名称" prop="nodeName">
        <el-input
          v-model="queryParams.nodeName"
          placeholder="请输入节点名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="节点层级" prop="nodeLevel">
        <el-input
          v-model="queryParams.nodeLevel"
          placeholder="请输入节点层级"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="父节点ID，自关联" prop="parentId">
        <el-input
          v-model="queryParams.parentId"
          placeholder="请输入父节点ID，自关联"
          clearable
          @keyup.enter.native="handleQuery"
        />
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
          v-hasPermi="['system:node:add']"
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
          v-hasPermi="['system:node:edit']"
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
          v-hasPermi="['system:node:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:node:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="nodeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="节点ID，主键，自增" align="center" prop="nodeId" />
      <el-table-column label="课程ID，关联course表" align="center" prop="courseId" />
      <el-table-column label="节点名称" align="center" prop="nodeName" />
      <el-table-column label="节点描述" align="center" prop="nodeDesc" />
      <el-table-column label="节点层级" align="center" prop="nodeLevel" />
      <el-table-column label="父节点ID，自关联" align="center" prop="parentId" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:node:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:node:remove']"
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

    <!-- 添加或修改知识图谱节点，存储课程的知识图谱结构对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="课程ID，关联course表" prop="courseId">
          <el-input v-model="form.courseId" placeholder="请输入课程ID，关联course表" />
        </el-form-item>
        <el-form-item label="节点名称" prop="nodeName">
          <el-input v-model="form.nodeName" placeholder="请输入节点名称" />
        </el-form-item>
        <el-form-item label="节点描述" prop="nodeDesc">
          <el-input v-model="form.nodeDesc" type="textarea" placeholder="请输入内容" />
        </el-form-item>
        <el-form-item label="节点层级" prop="nodeLevel">
          <el-input v-model="form.nodeLevel" placeholder="请输入节点层级" />
        </el-form-item>
        <el-form-item label="父节点ID，自关联" prop="parentId">
          <el-input v-model="form.parentId" placeholder="请输入父节点ID，自关联" />
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
import { listNode, getNode, delNode, addNode, updateNode } from "@/api/system/node"

export default {
  name: "Node",
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
      // 知识图谱节点，存储课程的知识图谱结构表格数据
      nodeList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        courseId: null,
        nodeName: null,
        nodeDesc: null,
        nodeLevel: null,
        parentId: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        courseId: [
          { required: true, message: "课程ID，关联course表不能为空", trigger: "blur" }
        ],
        nodeName: [
          { required: true, message: "节点名称不能为空", trigger: "blur" }
        ],
        nodeLevel: [
          { required: true, message: "节点层级不能为空", trigger: "blur" }
        ],
        createTime: [
          { required: true, message: "创建时间不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询知识图谱节点，存储课程的知识图谱结构列表 */
    getList() {
      this.loading = true
      listNode(this.queryParams).then(response => {
        this.nodeList = response.rows
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
        nodeId: null,
        courseId: null,
        nodeName: null,
        nodeDesc: null,
        nodeLevel: null,
        parentId: null,
        createTime: null,
        updateTime: null
      }
      this.resetForm("form")
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
      this.ids = selection.map(item => item.nodeId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加知识图谱节点，存储课程的知识图谱结构"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const nodeId = row.nodeId || this.ids
      getNode(nodeId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改知识图谱节点，存储课程的知识图谱结构"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.nodeId != null) {
            updateNode(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addNode(this.form).then(response => {
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
      const nodeIds = row.nodeId || this.ids
      this.$modal.confirm('是否确认删除知识图谱节点，存储课程的知识图谱结构编号为"' + nodeIds + '"的数据项？').then(function() {
        return delNode(nodeIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/node/export', {
        ...this.queryParams
      }, `node_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
