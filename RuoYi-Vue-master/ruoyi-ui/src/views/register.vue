<template>
  <div class="register">
    <el-form ref="registerForm" :model="registerForm" :rules="registerRules" class="register-form">
      <h3 class="title">{{title}}</h3>
      <el-form-item prop="username">
        <el-input v-model="registerForm.username" type="text" auto-complete="off" placeholder="账号">
          <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>
      <el-form-item prop="password">
        <el-input
          v-model="registerForm.password"
          :type="passwordType"
          auto-complete="off"
          placeholder="密码"
          @keyup.enter.native="handleRegister"
        >
          <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
          <svg-icon slot="suffix" :icon-class="passwordType === 'password' ? 'eye' : 'eye-open'" @click="showPwd" class="el-input__icon input-icon" style="cursor: pointer;" />
        </el-input>
      </el-form-item>
      <el-form-item prop="confirmPassword">
        <el-input
          v-model="registerForm.confirmPassword"
          :type="confirmPasswordType"
          auto-complete="off"
          placeholder="确认密码"
          @keyup.enter.native="handleRegister"
        >
          <svg-icon slot="prefix" icon-class="password" class="el-input__icon input-icon" />
          <svg-icon slot="suffix" :icon-class="confirmPasswordType === 'password' ? 'eye' : 'eye-open'" @click="showConfirmPwd" class="el-input__icon input-icon" style="cursor: pointer;" />
        </el-input>
      </el-form-item>
      <el-form-item prop="nickName">
        <el-input v-model="registerForm.nickName" type="text" auto-complete="off" placeholder="您的真实姓名">
          <svg-icon slot="prefix" icon-class="user" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>
      <el-form-item prop="email">
        <el-input v-model="registerForm.email" type="text" auto-complete="off" placeholder="邮箱地址">
          <svg-icon slot="prefix" icon-class="email" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>
      <el-form-item prop="phonenumber">
        <el-input v-model="registerForm.phonenumber" type="text" auto-complete="off" placeholder="手机号码">
          <svg-icon slot="prefix" icon-class="phone" class="el-input__icon input-icon" />
        </el-input>
      </el-form-item>
      <el-form-item prop="sex">
        <el-radio-group v-model="registerForm.sex" style="width:100%; text-align: center;">
            <el-radio label="0">男</el-radio>
            <el-radio label="1">女</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item prop="roleId">
        <el-select v-model="registerForm.roleId" placeholder="请选择角色" style="width: 100%">
          <el-option
            v-for="role in roleOptions"
            :key="role.roleId"
            :label="role.roleName"
            :value="role.roleId">
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item prop="code" v-if="captchaEnabled">
        <el-input
          v-model="registerForm.code"
          auto-complete="off"
          placeholder="验证码"
          style="width: 63%"
          @keyup.enter.native="handleRegister"
        >
          <svg-icon slot="prefix" icon-class="validCode" class="el-input__icon input-icon" />
        </el-input>
        <div class="register-code">
          <img :src="codeUrl" @click="getCode" class="register-code-img"/>
        </div>
      </el-form-item>
      <el-form-item style="width:100%;">
        <el-button
          :loading="loading"
          size="medium"
          type="primary"
          style="width:100%;"
          @click.native.prevent="handleRegister"
        >
          <span v-if="!loading">注 册</span>
          <span v-else>注 册 中...</span>
        </el-button>
        <div style="float: right;">
          <router-link class="link-type" :to="'/login'">使用已有账户登录</router-link>
        </div>
      </el-form-item>
    </el-form>
    <!--  底部  -->
    <div class="el-register-footer">
      <span>Copyright © 2018-2025 ruoyi.vip All Rights Reserved.</span>
    </div>
  </div>
</template>

<script>
import { getCodeImg, register, getRegisterRoles } from "@/api/login"

export default {
  name: "Register",
  data() {
    const equalToPassword = (rule, value, callback) => {
      if (this.registerForm.password !== value) {
        callback(new Error("两次输入的密码不一致"))
      } else {
        callback()
      }
    }
    return {
      title: process.env.VUE_APP_TITLE,
      codeUrl: "",
      passwordType: 'password',
      confirmPasswordType: 'password',
      registerForm: {
        username: "",
        password: "",
        confirmPassword: "",
        nickName: "",
        email: "",
        phonenumber: "",
        sex: "0",
        roleId: "",
        code: "",
        uuid: ""
      },
      roleOptions: [],
      registerRules: {
        username: [
          { required: true, trigger: "blur", message: "请输入您的账号" },
          { min: 2, max: 20, message: '用户账号长度必须介于 2 和 20 之间', trigger: 'blur' }
        ],
        password: [
          { required: true, trigger: "blur", message: "请输入您的密码" },
          { min: 5, max: 20, message: "用户密码长度必须介于 5 和 20 之间", trigger: "blur" },
          { pattern: /^[^<>"'|\\\\]+$/, message: "不能包含非法字符：< > \" ' \\ |", trigger: "blur" }
        ],
        confirmPassword: [
          { required: true, trigger: "blur", message: "请再次输入您的密码" },
          { required: true, validator: equalToPassword, trigger: "blur" }
        ],
        nickName: [
          { required: true, trigger: "blur", message: "请输入您的真实姓名" }
        ],
        email: [
          { required: true, message: "邮箱地址不能为空", trigger: "blur" },
          { type: "email", message: "请输入正确的邮箱地址", trigger: ["blur", "change"] }
        ],
        phonenumber: [
          { required: true, message: "手机号码不能为空", trigger: "blur" },
          { pattern: /^1[3-9]\d{9}$/, message: "请输入正确的手机号码", trigger: "blur" }
        ],
        roleId: [
          { required: true, trigger: "change", message: "请选择角色" }
        ],
        code: [{ required: true, trigger: "change", message: "请输入验证码" }]
      },
      loading: false,
      captchaEnabled: true
    }
  },
  created() {
    this.getCode()
    this.getRoles()
  },
  methods: {
    showPwd() {
      this.passwordType = this.passwordType === 'password' ? 'text' : 'password';
    },
    showConfirmPwd() {
      this.confirmPasswordType = this.confirmPasswordType === 'password' ? 'text' : 'password';
    },
    getCode() {
      getCodeImg().then(res => {
        this.captchaEnabled = res.captchaEnabled === undefined ? true : res.captchaEnabled
        if (this.captchaEnabled) {
          this.codeUrl = "data:image/gif;base64," + res.img
          this.registerForm.uuid = res.uuid
        }
      })
    },
    getRoles() {
      getRegisterRoles().then(res => {
        // 兼容后端返回格式
        if (Array.isArray(res)) {
          this.roleOptions = res
        } else if (res && Array.isArray(res.data)) {
          this.roleOptions = res.data
        } else {
          this.roleOptions = []
        }
      }).catch(() => {
        this.$message.error("获取角色列表失败")
      })
    },
    handleRegister() {
      this.$refs.registerForm.validate(valid => {
        if (valid) {
          this.loading = true
          register(this.registerForm).then(res => {
            const username = this.registerForm.username
            this.$alert("<font color='red'>恭喜你，您的账号 " + username + " 注册成功！</font>", '系统提示', {
              dangerouslyUseHTMLString: true,
              type: 'success'
            }).then(() => {
              this.$router.push("/login")
            }).catch(() => {})
          }).catch(() => {
            this.loading = false
            if (this.captchaEnabled) {
              this.getCode()
            }
          })
        }
      })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss">
.register {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100%;
  background-image: url("../assets/images/login-background.jpg");
  background-size: cover;
}
.title {
  margin: 0px auto 30px auto;
  text-align: center;
  color: #707070;
}

.register-form {
  border-radius: 6px;
  background: #ffffff;
  width: 400px;
  padding: 25px 25px 5px 25px;
  .el-input {
    height: 38px;
    input {
      height: 38px;
    }
  }
  .input-icon {
    height: 39px;
    width: 14px;
    margin-left: 2px;
  }
}
.register-tip {
  font-size: 13px;
  text-align: center;
  color: #bfbfbf;
}
.register-code {
  width: 33%;
  height: 38px;
  float: right;
  img {
    cursor: pointer;
    vertical-align: middle;
  }
}
.el-register-footer {
  height: 40px;
  line-height: 40px;
  position: fixed;
  bottom: 0;
  width: 100%;
  text-align: center;
  color: #fff;
  font-family: Arial;
  font-size: 12px;
  letter-spacing: 1px;
}
.register-code-img {
  height: 38px;
}
</style>
