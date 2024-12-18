<template>
  <div class="login-container">
    <div class="content-box">
      <div class="title">
        <h2>节点数据获取工具</h2>
        <p>目前只支持vless协议 和 vmess协议，其他后续更新</p>
      </div>

      <el-form :model="form" :rules="rules" ref="formRef">
        <!-- URL输入框 -->
        <el-form-item prop="url">
          <el-input
              v-model="form.url"
              :prefix-icon="Link"
              placeholder="请输入单个URL或多个URL(每行一个)，示例：https://localhost:8080"
              type="textarea"
              rows="4"
              class="custom-input">
          </el-input>
        </el-form-item>

        <!-- 账号输入框 -->
        <el-form-item prop="username">
          <el-input
              v-model="form.username"
              :prefix-icon="User"
              placeholder="请输入账号，默认admin"
              class="custom-input">
          </el-input>
        </el-form-item>

        <!-- 密码输入框 -->
        <el-form-item prop="password">
          <el-input
              v-model="form.password"
              :prefix-icon="Lock"
              type="password"
              placeholder="请输入密码，默认admin"
              class="custom-input">
          </el-input>
        </el-form-item>

        <!-- 提交按钮 -->
        <el-form-item>
          <el-button type="primary" @click="handleSubmit" class="submit-button" :loading="loading">
            获取数据
          </el-button>
        </el-form-item>
      </el-form>

      <!-- 结果展示区域 -->
      <div v-if="resultList.length" class="result-section">
        <div class="result-header">
          <h3>数据结果</h3>
          <el-button type="primary" link @click="handleCopyAll" class="copy-button">
            <el-icon>
              <DocumentCopy/>
            </el-icon>
            一键复制
          </el-button>
        </div>

        <el-card class="result-card" shadow="hover">
          <div class="result-scroll">
            <div v-for="(item, index) in resultList" :key="index" class="result-item">
              <pre>{{ formatResult(item) }}</pre>
            </div>
          </div>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, reactive} from 'vue'
import {User, Lock, Link, DocumentCopy} from '@element-plus/icons-vue'
import {ElMessage} from 'element-plus'
import axios from '@/axios';

const formRef = ref(null)
const loading = ref(false)
const resultList = ref([])

const form = reactive({
  url: '',
  username: '',
  password: ''
})

const rules = {
  url: [{required: true, message: '请输入URL地址', trigger: 'blur'}]
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()
    loading.value = true
    const urlList = form.url.split('\n').filter(url => url.trim() !== '')
    const urlParam = urlList.join(',')
    const response = await axios.post("/api", {
      url: urlParam,
      username: form.username,
      password: form.password
    })

    if (response.code === 1) {
      ElMessage.error(response.msg)
      return
    }
    resultList.value = response.data
    ElMessage.success('数据获取成功')
  } catch (error) {
    ElMessage.error(error.message || '获取数据失败')
  } finally {
    loading.value = false
  }
}



const formatResult = (item) => {
  if (typeof item === 'string') {
    return item
  }
  return JSON.stringify(item, null, 2)
}

const handleCopyAll = () => {
  const text = resultList.value.map(item => formatResult(item)).join('\n')
  if (navigator.clipboard && navigator.clipboard.writeText) {
    // 优先使用 navigator.clipboard API
    navigator.clipboard.writeText(text).then(() => {
      ElMessage.success('复制成功')
    }).catch(() => {
      ElMessage.error('复制失败')
    })
  } else {
    // 兼容非 HTTPS 环境的解决方案
    const textarea = document.createElement('textarea')
    textarea.value = text
    document.body.appendChild(textarea)
    textarea.select()
    try {
      document.execCommand('copy')
      ElMessage.success('复制成功')
    } catch (err) {
      ElMessage.error('复制失败')
    }
    document.body.removeChild(textarea)
  }
}

</script>

<style lang="scss" scoped>
.login-container {
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  padding: 20px;

  .content-box {
    width: 800px;
    padding: 40px;
    background: rgba(255, 255, 255, 0.95);
    border-radius: 16px;
    box-shadow: 0 8px 32px rgba(0, 0, 0, 0.1);
    backdrop-filter: blur(10px);
    transition: transform 0.3s ease;

    &:hover {
      transform: translateY(-5px);
    }

    .title {
      text-align: center;
      margin-bottom: 40px;

      h2 {
        font-size: 28px;
        color: #303133;
        margin-bottom: 8px;
        font-weight: 600;
      }

      p {
        color: #909399;
        font-size: 16px;
      }
    }

    .custom-input {
      :deep(.el-input__wrapper) {
        padding: 12px;
        border-radius: 8px;
        background: #f5f7fa;
        box-shadow: none;
        border: 2px solid transparent;
        transition: all 0.3s ease;

        &:hover, &.is-focus {
          border-color: #409eff;
          background: white;
        }
      }
    }

    .submit-button {
      width: 100%;
      padding: 12px;
      font-size: 16px;
      border-radius: 8px;
      height: 45px;
      background: linear-gradient(135deg, #409eff 0%, #007fff 100%);
      border: none;
      transition: all 0.3s ease;

      &:hover {
        transform: translateY(-2px);
        box-shadow: 0 4px 12px rgba(64, 158, 255, 0.4);
      }
    }

    .result-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .result-scroll {
      max-height: 250px;
      overflow: auto;
    }

    .result-section {
      margin-top: 30px;
    }
  }
}
</style>
