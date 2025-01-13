<template>
  <div class="container">
    <!-- 左侧输入区域 -->
    <div class="left-panel">
      <div class="form-container">
        <div class="title">
          <h2>节点数据获取工具</h2>
          <p>支持vless协议和vmess协议</p>
        </div>

        <el-form :model="form" :rules="rules" ref="formRef">
          <el-form-item prop="url">
            <el-input
                v-model="form.url"
                :prefix-icon="Link"
                placeholder="请输入单个URL或多个URL(每行一个)"
                type="textarea"
                rows="4"
                class="custom-input">
            </el-input>
          </el-form-item>

          <el-form-item prop="username">
            <el-input
                v-model="form.username"
                :prefix-icon="User"
                placeholder="请输入账号，默认admin"
                class="custom-input">
            </el-input>
          </el-form-item>

          <el-form-item prop="password">
            <el-input
                v-model="form.password"
                :prefix-icon="Lock"
                type="password"
                placeholder="请输入密码，默认admin"
                class="custom-input">
            </el-input>
          </el-form-item>

          <el-form-item>
            <el-button type="primary" @click="handleSubmit" class="submit-button" :loading="loading">
              获取数据
            </el-button>
          </el-form-item>
        </el-form>

        <div class="connection-status" :class="{ connected: isConnected }">
          WebSocket状态: {{ isConnected ? '已连接' : '未连接' }}
        </div>
      </div>
    </div>

    <!-- 右侧结果显示区域 -->
    <div class="right-panel">
      <div class="result-container">
        <div class="result-header">
          <h3>数据结果</h3>
          <el-button v-if="resultList.length" type="primary" link @click="handleCopyAll" class="copy-button">
            <el-icon><DocumentCopy/></el-icon>
            一键复制
          </el-button>
        </div>

        <div class="result-content" v-if="resultList.length">
          <div class="result-scroll">
            <div v-for="(item, index) in resultList" :key="index" class="result-item">
              <pre>{{ formatResult(item) }}</pre>
            </div>
          </div>
        </div>

        <div v-else class="empty-result">
          <el-empty description="暂无数据" />
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import {ref, reactive, onMounted, onUnmounted} from 'vue'
import {User, Lock, Link, DocumentCopy} from '@element-plus/icons-vue'
import {ElMessage} from 'element-plus'

const formRef = ref(null)
const loading = ref(false)
const resultList = ref([])
const isConnected = ref(false)
let ws = null

const form = reactive({
  url: '',
  username: '',
  password: ''
})

const rules = {
  url: [{required: true, message: '请输入URL地址', trigger: 'blur'}]
}

// WebSocket连接函数
const connectWebSocket = () => {
  // 根据实际部署修改WebSocket地址
  ws = new WebSocket('ws://127.0.0.1:9988/ws/proxy')

  ws.onopen = () => {
    isConnected.value = true
    ElMessage.success('WebSocket连接成功')
  }

  ws.onclose = () => {
    isConnected.value = false
    ElMessage.warning('WebSocket连接已断开')
  }

  ws.onerror = (error) => {
    console.error('WebSocket错误：', error)
    ElMessage.error('WebSocket连接错误')
    isConnected.value = false
  }

  ws.onmessage = (event) => {
    try {
      // 添加新的结果到列表
      resultList.value.push(event.data)
    } catch (error) {
      console.error('处理消息错误：', error)
    }
  }
}

// 重连函数
const reconnect = () => {
  if (!isConnected.value && ws) {
    connectWebSocket()
  }
}

const handleSubmit = async () => {
  if (!formRef.value) return
  try {
    await formRef.value.validate()

    if (!isConnected.value) {
      ElMessage.error('WebSocket未连接，请等待重连')
      reconnect()
      return
    }

    loading.value = true
    resultList.value = [] // 清空之前的结果

    // 处理多行URL
    const urlList = form.url.split('\n').filter(url => url.trim() !== '')

    // 发送数据到WebSocket
    ws.send(JSON.stringify({
      url: urlList.join(','),
      username: form.username || 'admin',
      password: form.password || 'admin'
    }))

  } catch (error) {
    ElMessage.error(error.message || '提交失败')
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
    navigator.clipboard.writeText(text).then(() => {
      ElMessage.success('复制成功')
    }).catch(() => {
      ElMessage.error('复制失败')
    })
  } else {
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

// 组件挂载时连接WebSocket
onMounted(() => {
  connectWebSocket()
})

// 组件卸载时关闭WebSocket
onUnmounted(() => {
  if (ws) {
    ws.close()
  }
})
</script>

<style lang="scss" scoped>
.container {
  display: flex;
  min-height: 100vh;
  background: #f5f7fa;
  overflow: hidden;
}

.left-panel {
  width: 500px;
  min-width: 500px;
  padding: 40px;
  background: white;
  box-shadow: 4px 0 8px rgba(0, 0, 0, 0.05);
  overflow-y: auto;

  .form-container {
    max-width: 420px;
    margin: 0 auto;
  }
}

.right-panel {
  flex: 1;
  padding: 20px 40px;
  background: #f5f7fa;
  min-width: 0;
  height: 100vh;
  overflow: hidden;

  .result-container {
    height: calc(100vh - 40px);
    background: white;
    border-radius: 12px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.05);
    padding: 24px;
    display: flex;
    flex-direction: column;
  }
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

.connection-status {
  text-align: center;
  padding: 12px;
  margin-top: 20px;
  border-radius: 8px;
  background: #fef0f0;
  color: #f56c6c;
  font-size: 14px;

  &.connected {
    background: #f0f9eb;
    color: #67c23a;
  }
}

.result-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
  padding-bottom: 16px;
  border-bottom: 1px solid #ebeef5;

  .header-left {
    display: flex;
    align-items: center;
    gap: 12px;

    h3 {
      font-size: 22px;
      color: #303133;
      margin: 0;
      font-weight: 600;
    }

    .result-count {
      color: #909399;
      font-size: 14px;
      background: #f5f7fa;
      padding: 4px 8px;
      border-radius: 4px;
    }
  }

  .copy-button {
    display: flex;
    align-items: center;
    gap: 4px;
    font-size: 14px;
    padding: 8px 16px;

    &:hover {
      background: #ecf5ff;
      border-radius: 4px;
    }
  }
}

.result-content {
  flex: 1;
  min-height: 0;
  position: relative;
  margin: -8px;
  padding: 8px;
}

.result-scroll {
  height: calc(100vh - 140px);
  overflow-y: auto;
  overflow-x: hidden;
  padding-right: 12px;

  &::-webkit-scrollbar {
    width: 8px;
    height: 8px;
  }

  &::-webkit-scrollbar-track {
    background: #f5f7fa;
    border-radius: 4px;
  }

  &::-webkit-scrollbar-thumb {
    background: #dcdfe6;
    border-radius: 4px;

    &:hover {
      background: #c0c4cc;
    }
  }
}

.result-item {
  padding: 16px;
  background: #f8f9fb;
  border-radius: 8px;
  margin-bottom: 12px;
  transition: all 0.3s ease;
  border: 1px solid transparent;

  &:hover {
    background: #fff;
    border-color: #e4e7ed;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.04);
  }

  &:last-child {
    margin-bottom: 0;
  }

  .result-text {
    font-family: Monaco, Consolas, Courier, monospace;
    font-size: 14px;
    line-height: 1.5;
    color: #606266;
    white-space: pre-wrap;
    word-wrap: break-word;
    word-break: break-all;
    overflow-wrap: break-word;
    max-width: 100%;
  }
}

.empty-result {
  height: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #909399;
}

</style>
