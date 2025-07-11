<template>
  <div>
    <!-- 悬浮按钮 -->
    <el-button
      v-if="!openSidebar"
      class="ai-fab"
      circle
      @click="handleOpenSidebar"
      :style="{boxShadow: '0 4px 12px rgba(0,0,0,0.2)'}"
    >
      <svg width="28" height="28" viewBox="0 0 1024 1024"><path d="M512 64C264.6 64 64 240.6 64 464c0 99.2 47.2 189.2 124.8 254.4-8.8 32.8-29.6 90.4-61.6 130.4-6.4 8-0.8 19.2 9.2 19.2 2.4 0 4.8-0.8 7.2-2.4 66.4-44.8 120.8-92.8 146.4-116.8 38.4 12.8 79.2 20 122 20 247.4 0 448-176.6 448-400S759.4 64 512 64z" fill="#409eff"/></svg>
    </el-button>

    <!-- 自定义右侧抽屉 -->
    <transition name="ai-drawer-fade">
      <div v-if="openSidebar" class="ai-drawer-mask" @click.self="closeSidebar">
        <div
          class="ai-drawer-panel"
          :style="{ width: drawerWidth + 'px' }"
          tabindex="-1"
          @keydown.esc="closeSidebar"
        >
          <!-- 标题栏 -->
          <div class="ai-drawer-header">
            <span class="ai-drawer-title">
              <svg width="22" height="22" viewBox="0 0 1024 1024" style="vertical-align:middle;margin-right:6px;"><path d="M512 64C264.6 64 64 240.6 64 464c0 99.2 47.2 189.2 124.8 254.4-8.8 32.8-29.6 90.4-61.6 130.4-6.4 8-0.8 19.2 9.2 19.2 2.4 0 4.8-0.8 7.2-2.4 66.4-44.8 120.8-92.8 146.4-116.8 38.4 12.8 79.2 20 122 20 247.4 0 448-176.6 448-400S759.4 64 512 64z" fill="#409eff"/></svg>
              AI智能对话
            </span>
            <span class="ai-drawer-close" @click="closeSidebar">×</span>
          </div>
          <div class="ai-chat-container">
            <div class="ai-chat-messages">
              <div
                v-for="(msg, idx) in messages"
                :key="idx"
                :class="['ai-chat-message', msg.role]"
              >
                <img
                  :src="msg.role === 'user' ? userAvatar : aiAvatar"
                  class="ai-chat-avatar"
                />
                <div class="ai-chat-bubble">
                  <span v-if="msg.loading"><i class="el-icon-loading" style="margin-right:4px;"></i>AI思考中...</span>
                  <span v-else>{{ msg.content }}</span>
                  <div v-if="msg.thinking" class="ai-chat-thinking">{{ msg.thinking }}</div>
                </div>
              </div>
            </div>
            <!-- 清空历史按钮 -->
            <div style="text-align:right;padding:0 16px 8px 0;">
              <el-button size="mini" @click="clearHistory" icon="el-icon-delete">清空历史</el-button>
            </div>
            <!-- 附件预览区 -->
            <div v-if="attachments.length > 0" class="ai-attachment-preview">
              <div v-for="(file, idx) in attachments" :key="file.uid || file.name" class="ai-attachment-item">
                <img v-if="file.isImage" :src="file.url" class="ai-attachment-thumb" />
                <span v-else class="ai-attachment-file">{{ file.name }}</span>
                <span class="ai-attachment-remove" @click="removeAttachment(idx)">×</span>
              </div>
            </div>
            <div class="ai-chat-input">
              <el-input
                v-model="input"
                placeholder="请输入你的问题..."
                @keyup.enter.native="send"
                clearable
              />
              <el-upload
                ref="uploadBtn"
                class="ai-upload-btn"
                :show-file-list="false"
                :multiple="true"
                :before-upload="beforeUpload"
                :on-change="onFileChange"
                :on-remove="onRemove"
                :auto-upload="false"
                :action="''"
                accept="image/*,.pdf,.ppt,.pptx,.doc,.docx"
              >
                <el-button>上传附件</el-button>
              </el-upload>
              <el-button type="primary" @click="send" :loading="loading">发送</el-button>
            </div>
          </div>
          <!-- 拖拽条 -->
          <div
            class="ai-drawer-resizer"
            @mousedown="startResize"
            @touchstart.prevent="startResize"
          ></div>
        </div>
      </div>
    </transition>
  </div>
</template>

<script>
import service from '@/utils/request'
export default {
  name: 'AIDialog',
  props: {
    studentId: { type: [String, Number], required: true },
    courseId: { type: [String, Number], default: "" }
  },
  data() {
    return {
      openSidebar: false,
      input: '',
      loading: false,
      messages: [],
      userAvatar: require('@/assets/images/profile.jpg'),
      aiAvatar: require('@/assets/images/AIAgent.png'),
      studentSkill: null,
      drawerWidth: 400,
      resizing: false,
      minWidth: 350,
      maxWidth: 600,
      startX: 0,
      startWidth: 400,
      attachments: [] // 附件列表
    }
  },
  methods: {
    handleOpenSidebar() {
      this.openSidebar = true
      this.loadHistory()
      // 首次展开且无历史时，自动输出AI问候语
      if (!this.messages || this.messages.length === 0) {
        const greetMsg = { role: 'ai', content: '你好，我是AI助手，有什么可以帮您？' };
        this.messages.push(greetMsg);
        this.saveHistory();
      }
      this.$nextTick(() => {
        const panel = document.querySelector('.ai-drawer-panel')
        if(panel) panel.focus()
      })
    },
    closeSidebar() {
      this.openSidebar = false
    },
    async loadStudentSkill() {
      // 获取学生能力值
      try {
        const res = await fetch(`/system/skill/student/${this.studentId}/course/${this.courseId}`)
        const data = await res.json()
        if(data && data.code === 200) {
          this.studentSkill = data.data
        } else {
          this.studentSkill = null
        }
      } catch (e) {
        this.studentSkill = null
      }
    },
    getHistoryKey() {
      return `ai_chat_history_${this.studentId}_${this.courseId || "all"}`;
    },
    loadHistory() {
      const key = this.getHistoryKey();
      const raw = sessionStorage.getItem(key);
      if(raw) {
        this.messages = JSON.parse(raw);
      }
    },
    saveHistory() {
      const key = this.getHistoryKey();
      sessionStorage.setItem(key, JSON.stringify(this.messages));
    },
    beforeUpload(file) {
      const maxSize = 10 * 1024 * 1024; // 10MB
      if (file.size > maxSize) {
        this.$message.error('单个文件不能超过10MB');
        return false;
      }
      return false; // 阻止el-upload自动上传，仍走自定义流程
    },
    onFileChange(file, fileList) {
      // 只保留最新的fileList，且彻底移除已删除的文件
      // fileList为el-upload维护的最新列表
      this.attachments = fileList.map(f => {
        const isImage = f.raw && f.raw.type && f.raw.type.startsWith('image/');
        let url = '';
        if (isImage) {
          url = URL.createObjectURL(f.raw);
        }
        return {
          uid: f.uid,
          name: f.name,
          raw: f.raw,
          isImage,
          url
        };
      });
    },
    onRemove(file, fileList) {
      // fileList为el-upload最新的文件列表
      this.attachments = fileList.map(f => {
        const isImage = f.raw && f.raw.type && f.raw.type.startsWith('image/');
        let url = '';
        if (isImage) {
          url = URL.createObjectURL(f.raw);
        }
        return {
          uid: f.uid,
          name: f.name,
          raw: f.raw,
          isImage,
          url
        };
      });
    },
    removeAttachment(idx) {
      // 只通过el-upload的remove方法删除，保证同步
      const upload = this.$refs.uploadBtn;
      if (upload && upload.uploadFiles && this.attachments[idx]) {
        const file = upload.uploadFiles.find(f => f.uid === this.attachments[idx].uid);
        if (file) upload.handleRemove(file);
      }
    },
    clearHistory() {
      this.messages = [];
      // 立即生成AI问候语
      const greetMsg = { role: 'ai', content: '你好，我是AI助手，有什么可以帮您？' };
      this.messages.push(greetMsg);
      this.saveHistory();
    },
    async send() {
      if (!this.input.trim() && this.attachments.length === 0) return;
      // 先保存当前输入和附件
      const currentInput = this.input;
      const currentAttachments = this.attachments.slice();
      const userMsg = { role: 'user', content: currentInput, attachments: currentAttachments.map(f => f.name) };
      this.messages.push(userMsg);
      this.saveHistory();
      const aiThinkingMsg = { role: 'ai', content: '', loading: true };
      this.messages.push(aiThinkingMsg);
      this.saveHistory();
      // 立即清空输入和附件
      this.input = '';
      this.attachments = [];
      this.loading = true;
      if(!this.studentSkill) await this.loadStudentSkill();
      const context = [];
      let count = 0;
      for(let i = this.messages.length - 3; i >= 0 && count < 4; i--) {
        context.unshift(this.messages[i]);
        count++;
      }
      const skillStr = this.studentSkill ? JSON.stringify(this.studentSkill) : '[]';
      const contextStr = context.map(m => (m.role === 'user' ? '用户: ' : 'AI: ') + m.content).join('\n');
      const prompt = `学生能力值: ${skillStr}\n历史对话: ${contextStr}\n用户问题: ${currentInput}`;
      try {
        if (currentAttachments.length > 0) {
          // 有附件，走混合理解接口
          const form = new FormData();
          // 图片
          currentAttachments.filter(f => f.isImage).forEach(f => form.append('images', f.raw));
          // 文件
          currentAttachments.filter(f => !f.isImage).forEach(f => form.append('files', f.raw));
          form.append('prompt', currentInput);
          const res = await service.post('/api/ai/mixed-understand', form, {
            headers: { 'Content-Type': 'multipart/form-data' }
          });
          // 调试：打印res和res.data
          console.log('[AIDialog] /api/ai/mixed-understand 响应:', res);
          console.log('[AIDialog] /api/ai/mixed-understand 响应data:', res.data);
          // 兼容res.data为字符串或对象
          let reply = '';
          if (res) {
            if (typeof res.data === 'string') {
              reply = res.data;
            } else if (res.data && typeof res.data === 'object') {
              reply = res.data.data || res.data.reply || res.data.msg || (typeof res.data === 'string' ? res.data : undefined);
            } else if (typeof res === 'string') {
              reply = res;
            } else if (typeof res === 'object') {
              reply = res.data || res.reply || res.msg;
            }
          }
          if (!reply) reply = 'AI未响应';
          // 只替换最后一个 loading 的 ai 消息
          for (let i = this.messages.length - 1; i >= 0; i--) {
            if (this.messages[i].loading) {
              this.$set(this.messages, i, { role: 'ai', content: reply });
              break;
            }
          }
          this.attachments = [];
          this.saveHistory();
        } else {
          // 无附件，走原有文本接口
          const validHistory = this.messages
            .filter(m => (m.role === 'user' || m.role === 'ai') && m.content && !m.loading)
            .slice(-4);
          console.log('[AIDialog] 请求参数:', {
            studentId: this.studentId,
            courseId: this.courseId,
            userInput: currentInput,
            history: validHistory
          });
          const res = await service.post('/api/ai/chat', {
            studentId: this.studentId,
            courseId: this.courseId,
            userInput: currentInput,
            history: validHistory
          });
          console.log('[AIDialog] axios响应内容:', res);
          // 兼容全局axios封装后的返回结构
          let reply, thinking;
          if (res && res.data) {
            reply = res.data.data || res.data.reply || res.data.msg || (typeof res.data === 'string' ? res.data : undefined);
            thinking = res.data.thinking;
          } else if (res) {
            reply = res.data || res.reply || res.msg;
            thinking = res.thinking;
          }
          if (!reply) reply = 'AI未响应';
          // 只替换最后一个 loading 的 ai 消息
          for (let i = this.messages.length - 1; i >= 0; i--) {
            if (this.messages[i].loading) {
              this.$set(this.messages, i, { role: 'ai', content: reply, thinking });
              break;
            }
          }
          this.saveHistory();
        }
      } catch (e) {
        console.error('[AIDialog] axios异常:', e);
        for (let i = this.messages.length - 1; i >= 0; i--) {
          if (this.messages[i].loading) {
            this.$set(this.messages, i, { role: 'ai', content: '请求失败，请稍后重试。' });
            break;
          }
        }
        this.saveHistory();
      }
      this.loading = false;
    },
    startResize(e) {
      this.resizing = true
      this.startX = e.touches ? e.touches[0].clientX : e.clientX
      this.startWidth = this.drawerWidth
      document.addEventListener('mousemove', this.onResizing)
      document.addEventListener('mouseup', this.stopResize)
      document.addEventListener('touchmove', this.onResizing)
      document.addEventListener('touchend', this.stopResize)
    },
    onResizing(e) {
      if (!this.resizing) return
      const clientX = e.touches ? e.touches[0].clientX : e.clientX
      let newWidth = this.startWidth + (this.startX - clientX)
      if (newWidth < this.minWidth) newWidth = this.minWidth
      if (newWidth > this.maxWidth) newWidth = this.maxWidth
      this.drawerWidth = newWidth
    },
    stopResize() {
      this.resizing = false
      document.removeEventListener('mousemove', this.onResizing)
      document.removeEventListener('mouseup', this.stopResize)
      document.removeEventListener('touchmove', this.onResizing)
      document.removeEventListener('touchend', this.stopResize)
    }
  },
  mounted() {
    this.loadHistory()
  }
}
</script>

<style scoped>
.ai-fab {
  position: fixed;
  right: 32px;
  bottom: 32px;
  z-index: 9999;
  width: 56px;
  height: 56px;
  background: #fff;
  color: #409eff;
  font-size: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
}
/* 右侧抽屉动画 */
.ai-drawer-fade-enter-active, .ai-drawer-fade-leave-active {
  transition: opacity 0.2s;
}
.ai-drawer-fade-enter, .ai-drawer-fade-leave-to {
  opacity: 0;
}
.ai-drawer-mask {
  position: fixed;
  z-index: 2000;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background: transparent;
}
.ai-drawer-panel {
  position: absolute;
  right: 0;
  top: 0;
  height: 100vh;
  background: #fff;
  box-shadow: 0 0 24px rgba(0,0,0,0.3);
  display: flex;
  flex-direction: column;
  outline: none;
  animation: ai-drawer-slide-in 0.25s;
  min-width: 350px;
  max-width: 600px;
  transition: width 0.15s;
}
@keyframes ai-drawer-slide-in {
  from { transform: translateX(100%); }
  to { transform: translateX(0); }
}
.ai-drawer-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  height: 54px;
  padding: 0 20px 0 18px;
  border-bottom: 1px solid #f0f0f0;
  font-size: 18px;
  font-weight: 500;
  background: #f8fafd;
  user-select: none;
}
.ai-drawer-title {
  display: flex;
  align-items: center;
  color: #409eff;
  font-size: 18px;
  font-weight: 600;
}
.ai-drawer-close {
  font-size: 26px;
  color: #bbb;
  cursor: pointer;
  margin-left: 12px;
  transition: color 0.2s;
}
.ai-drawer-close:hover {
  color: #ff4d4f;
}
.ai-drawer-resizer {
  position: absolute;
  left: 0;
  top: 0;
  width: 6px;
  height: 100%;
  cursor: ew-resize;
  background: transparent;
  z-index: 10;
}
.ai-chat-container {
  display: flex;
  flex-direction: column;
  flex: 1 1 0%;
  min-height: 0;
  padding-top: 0;
}
.ai-chat-messages {
  flex: 1 1 0%;
  min-height: 0;
  overflow-y: auto;
  padding: 16px;
  background: #f7f8fa;
}
.ai-chat-message {
  display: flex;
  align-items: flex-end;
  margin-bottom: 12px;
}
.ai-chat-message.user {
  flex-direction: row-reverse;
}
.ai-chat-avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  margin: 0 8px;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
}
.ai-chat-bubble {
  max-width: 70%;
  padding: 10px 16px;
  border-radius: 18px;
  background: #fff;
  box-shadow: 0 2px 8px rgba(0,0,0,0.08);
  word-break: break-all;
  position: relative;
}
.ai-chat-message.user .ai-chat-bubble {
  background: #409eff;
  color: #fff;
}
.ai-chat-thinking {
  color: #888;
  font-size: 13px;
  margin-top: 4px;
}
.ai-chat-input {
  display: flex;
  gap: 8px;
  padding: 12px;
  background: #fff;
  border-top: 1px solid #eee;
}
.ai-attachment-preview {
  display: flex;
  flex-wrap: wrap;
  gap: 8px;
  padding: 8px 12px 0 12px;
}
.ai-attachment-item {
  display: flex;
  align-items: center;
  background: #f5f5f5;
  border-radius: 4px;
  padding: 2px 8px;
  position: relative;
}
.ai-attachment-thumb {
  width: 40px;
  height: 40px;
  object-fit: cover;
  border-radius: 2px;
  margin-right: 6px;
}
.ai-attachment-file {
  font-size: 14px;
  color: #555;
  margin-right: 6px;
}
.ai-attachment-remove {
  color: #bbb;
  cursor: pointer;
  font-size: 18px;
  margin-left: 2px;
  transition: color 0.2s;
}
.ai-attachment-remove:hover {
  color: #ff4d4f;
}
.ai-upload-btn {
  margin-right: 4px;
}
</style> 