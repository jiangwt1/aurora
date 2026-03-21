<template>
  <div class="login-container" :style="{ backgroundImage: `url(${backgroundImage})` }">
    <div class="login-card">
      <h2 class="login-title">登录</h2>
      <el-form status-icon :model="loginForm" :rules="rules" ref="ruleForm" class="login-form">
        <el-form-item prop="username">
          <el-input
            v-model="loginForm.username"
            placeholder="账号"
            @keyup.enter.native="login">
          </el-input>
        </el-form-item>
        <el-form-item prop="password">
          <el-input
            v-model="loginForm.password"
            type="password"
            show-password
            placeholder="密码"
            @keyup.enter.native="login">
          </el-input>
        </el-form-item>
        <el-button type="primary" class="login-button" @click="login" :loading="loading">
          登录
        </el-button>
      </el-form>
    </div>
  </div>
</template>

<script>
import { generaMenu } from '@/assets/js/menu'
export default {
  data: function () {
    return {
      loginForm: {
        username: '',
        password: ''
      },
      rules: {
        username: [{ required: true, message: '用户名不能为空', trigger: 'blur' }],
        password: [{ required: true, message: '密码不能为空', trigger: 'blur' }]
      },
      loading: false,
      backgroundImage: 'https://minio.gysy.ltd/aurora/photos/e3331491f8f9324fbfa2cffd374daa45.jpg'
    }
  },
  created() {
    this.getSystemConfig()
  },
  methods: {
    getSystemConfig() {
      this.axios.get('/api/admin/system/config').then(({ data }) => {
        if (data.data && data.data.loginBackgroundImage) {
          this.backgroundImage = data.data.loginBackgroundImage
        }
      }).catch(() => {
        // 获取失败使用默认背景
      })
    },
    login() {
      this.$refs.ruleForm.validate((valid) => {
        if (valid) {
          this.loading = true
          const that = this
          let param = new URLSearchParams()
          param.append('username', that.loginForm.username)
          param.append('password', that.loginForm.password)
          that.axios.post('/api/users/login', param).then(({ data }) => {
            if (data.flag) {
              that.$store.commit('login', data.data)
              generaMenu()
              that.$message.success('登录成功')
              that.$router.push({ path: '/' })
            } else {
              that.$message.error(data.message)
            }
          }).finally(() => {
            this.loading = false
          })
        } else {
          return false
        }
      })
    }
  }
}
</script>

<style scoped>
.login-container {
  position: fixed;
  top: 0;
  bottom: 0;
  right: 0;
  left: 0;
  background-position: center center;
  background-size: cover;
  background-repeat: no-repeat;
  display: flex;
  justify-content: flex-end;
  align-items: center;
  padding-right: 8%;
}

.login-card {
  background: rgba(255, 255, 255, 0.92);
  backdrop-filter: blur(12px);
  border-radius: 16px;
  padding: 45px 40px;
  width: 360px;
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.12);
  animation: slideIn 0.5s ease;
}

@keyframes slideIn {
  from {
    opacity: 0;
    transform: translateX(40px);
  }
  to {
    opacity: 1;
    transform: translateX(0);
  }
}

.login-title {
  color: #2c3e50;
  font-weight: 600;
  font-size: 26px;
  text-align: center;
  margin: 0 0 35px 0;
}

.login-form {
  margin-top: 0;
}

.login-form >>> .el-form-item {
  margin-bottom: 22px;
}

.login-form >>> .el-input__wrapper {
  background: #f5f7fa;
  border: 1px solid #e4e7ed;
  border-radius: 8px;
  padding: 1px 15px;
  box-shadow: none;
  transition: all 0.2s;
}

.login-form >>> .el-input__wrapper:hover {
  border-color: #c0c4cc;
  background: #fff;
}

.login-form >>> .el-input.is-focus .el-input__wrapper {
  border-color: #5e72e4;
  background: #fff;
  box-shadow: 0 0 0 2px rgba(94, 114, 228, 0.1);
}

.login-form >>> .el-input__inner {
  color: #303133;
  font-size: 14px;
  height: 44px;
  line-height: 44px;
}

.login-form >>> .el-input__inner::placeholder {
  color: #a8abb2;
}

.login-button {
  width: 100%;
  height: 46px;
  margin-top: 12px;
  border-radius: 8px;
  font-size: 15px;
  font-weight: 500;
  background: #5e72e4;
  border-color: #5e72e4;
  transition: all 0.2s;
}

.login-button:hover {
  background: #4c63d2;
  border-color: #4c63d2;
  transform: translateY(-1px);
  box-shadow: 0 4px 12px rgba(94, 114, 228, 0.3);
}

.login-button:active {
  transform: translateY(0);
}

/* 响应式 */
@media (max-width: 768px) {
  .login-container {
    justify-content: center;
    padding-right: 0;
  }
  
  .login-card {
    width: 90%;
    max-width: 360px;
    padding: 40px 30px;
  }
}
</style>
