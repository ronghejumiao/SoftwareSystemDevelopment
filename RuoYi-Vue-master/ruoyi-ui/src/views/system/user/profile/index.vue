<template>
  <div class="profile-container">
    <el-row :gutter="30">
      <!-- Left Card: User Info -->
      <el-col :span="8" :xs="24">
        <el-card class="profile-card user-card">
          <div class="user-info-section">
            <div class="user-avatar">
              <userAvatar :user="user" />
            </div>
            <h2 class="user-name">{{ user.nickName }}</h2>
            <p class="user-role">{{ roleGroup }}</p>
            <ul class="user-details">
              <li><i class="el-icon-user"></i>用户名<div class="detail-value">{{ user.userName }}</div></li>
              <li><i class="el-icon-phone-outline"></i>手机号码 <div class="detail-value">{{ user.phonenumber }}</div></li>
              <li><i class="el-icon-message"></i>用户邮箱 <div class="detail-value">{{ user.email }}</div></li>
              <li><i class="el-icon-date"></i>创建日期 <div class="detail-value">{{ user.createTime }}</div></li>

            </ul>
          </div>
        </el-card>
      </el-col>

      <!-- Right Card: Actions -->
      <el-col :span="16" :xs="24">
        <el-card class="profile-card">
          <div slot="header" class="clearfix">
            <span>基本资料</span>
          </div>
          <el-tabs v-model="activeTab" class="profile-tabs">
            <el-tab-pane label="基本资料" name="userinfo">
              <userInfo :user="user" />
              <div style="margin-top: 20px;">
                <el-button type="primary" size="mini" icon="el-icon-video-camera" @click="goto('videos')">我的视频记录</el-button>
                <el-button type="warning" size="mini" icon="el-icon-medal-1" @click="goto('scores')" style="margin-left: 8px;">我的成绩</el-button>
                <el-button type="success" size="mini" icon="el-icon-document" @click="goto('tasks')" style="margin-left: 8px;">我的任务提交</el-button>
              </div>
            </el-tab-pane>
            <el-tab-pane label="修改密码" name="resetPwd">
              <resetPwd :user="user" />
            </el-tab-pane>
          </el-tabs>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import userAvatar from "./userAvatar"
import userInfo from "./userInfo"
import resetPwd from "./resetPwd"
import { getUserProfile } from "@/api/system/user"

export default {
  name: "Profile",
  components: { userAvatar, userInfo, resetPwd },
  data() {
    return {
      user: {},
      roleGroup: {},
      postGroup: {},
      activeTab: "userinfo"
    }
  },
  created() {
    this.getUser()
  },
  methods: {
    getUser() {
      getUserProfile().then(response => {
        this.user = response.data
        this.roleGroup = response.roleGroup
        this.postGroup = response.postGroup
      })
    },
    goto(type) {
      const map = {
        videos: '/user/videos',
        scores: '/user/scores',
        tasks: '/user/tasks'
      };
      this.$router.push(map[type]);
    }
  }
}
</script>

<style lang="scss" scoped>
.profile-container {
  background-color: #f4f6f8;
  padding: 30px;
  height: 100px;
}

.clearfix:before,
.clearfix:after {
  display: table;
  content: "";
}
.clearfix:after {
  clear: both
}

.profile-card {
  border-radius: 12px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.05);
  border: none;
  margin-bottom: 30px;
}

.user-card {
  .user-info-section {
    text-align: center;
    padding: 20px 0;
  }
  .user-avatar {
    margin-bottom: 20px;
    // 使用 ::v-deep 来影响子组件样式，放大头像
    ::v-deep .user-info-head {
      width: 120px;
      height: 120px;
      border-radius: 50%;
    }
  }
  .user-name {
    font-size: 24px;
    font-weight: 500;
    margin: 0 0 5px;
  }
  .user-role {
    font-size: 14px;
    color: #909399;
    margin-bottom: 25px;
  }
  .user-details {
    list-style: none;
    padding: 0;
    margin: 0;
    text-align: left;

    li {
      font-size: 14px;
      padding: 15px 10px;
      border-bottom: 1px solid #f0f3f5;
      display: flex;
      align-items: center;

      &:last-child {
        border-bottom: none;
      }

      i {
        font-size: 16px;
        margin-right: 10px;
        color: #909399;
      }

      .detail-value {
        margin-left: auto;
        color: #303133;
        font-weight: 500;
      }
    }
  }
}

.profile-tabs {
  .el-tab-pane {
    padding: 10px;
  }
}
</style>
