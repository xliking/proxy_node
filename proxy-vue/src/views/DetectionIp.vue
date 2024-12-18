<template>
  <div class="container">
    <!-- 左侧面板 -->
    <div class="left-panel">
      <h2>发送请求</h2>
      <textarea
          v-model="inputData"
          @keyup.enter="sendRequest"
          placeholder="请输入内容..."
          class="input-box"
      ></textarea>
      <button @click="sendRequest" class="send-button">请求</button>
    </div>

    <!-- 中间分隔线 -->
    <div class="separator"></div>

    <!-- 右侧面板 -->
    <div class="right-panel">
      <h2>响应数据</h2>
      <el-table
          :data="responses"
          border
          style="width: 100%;"
          :max-height="tableHeight"
          stripe
          size="medium"
      >
        <el-table-column
            type="index"
            label="序号"
            width="80">
        </el-table-column>
        <el-table-column
            prop="ip"
            label="IP"
            width="150">
        </el-table-column>
        <el-table-column
            prop="country"
            label="国家"
            width="120">
        </el-table-column>
        <el-table-column
            prop="address"
            label="地址"
            min-width="200">
        </el-table-column>
        <el-table-column
            prop="riskLevel"
            label="IP风险度"
            width="150">
          <template #default="scope">
            <el-rate v-model="scope.row.riskLevel" :disabled="true" :allow-half="true"></el-rate>
          </template>
        </el-table-column>
        <el-table-column
            prop="column6"
            label="列6"
            width="100">
        </el-table-column>
        <el-table-column
            prop="column7"
            label="列7"
            width="100">
        </el-table-column>
        <el-table-column
            prop="column8"
            label="列8"
            width="100">
        </el-table-column>
      </el-table>
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue';
import axios from 'axios';
import {
  ElTable,
  ElTableColumn,
  ElRate,
  ElMessage,
} from 'element-plus';
import 'element-plus/dist/index.css';

// 输入数据
const inputData = ref('');

// 响应数据，初始化为空数组
const responses = ref([]);

// 动态计算表格高度以适应窗口大小
const tableHeight = ref('calc(100vh - 150px)');

const updateTableHeight = () => {
  const windowHeight = window.innerHeight;
  tableHeight.value = `${windowHeight - 150}px`;
};

// 获取当前IP地址
const getClientIP = async () => {
  try {
    const response = await axios.get('https://api.ipify.org?format=json');
    return response.data.ip;
  } catch (error) {
    console.error('获取IP地址失败:', error);
    ElMessage.error('获取IP地址失败，请手动输入');
    return null;
  }
};

// 发送请求函数
const sendRequest = async () => {
  const dataToSend = inputData.value.trim();
  if (!dataToSend) {
    ElMessage.warning('请输入内容！');
    return;
  }

  try {
    const response = await axios.post('/ip', {
      query: dataToSend,
    });

    // 假设响应数据是一个数组，包含 ip, country, address, riskLevel 等字段
    const newData = response.data.map((item) => ({
      ip: item.ip || '未知',
      country: item.country || '未知',
      address: item.address || '未知',
      riskLevel: item.riskLevel || 0,
      column6: item.column6 || 'N/A',
      column7: item.column7 || 'N/A',
      column8: item.column8 || 'N/A',
    }));

    // 将新数据添加到现有数据的顶部
    responses.value = [...newData, ...responses.value];
    inputData.value = '';
    ElMessage.success('请求成功！');
  } catch (error) {
    console.error('请求失败:', error);
    ElMessage.error('请求失败，请稍后再试。');
    // 在顶部插入一条错误信息
    responses.value.unshift({
      ip: 'N/A',
      country: 'N/A',
      address: '请求失败',
      riskLevel: 0,
      column6: 'N/A',
      column7: 'N/A',
      column8: 'N/A',
    });
  }
};

// 在组件挂载时和窗口大小变化时更新表格高度，并获取IP
onMounted(async () => {
  updateTableHeight();
  window.addEventListener('resize', updateTableHeight);

  const clientIP = await getClientIP();
  if (clientIP) {
    inputData.value = clientIP;
    await sendRequest();
  }
});

</script>

<style scoped>
* {
  box-sizing: border-box;
}

body,
html,
#app {
  height: 100%;
  margin: 0;
  font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
  background-color: #f0f2f5;
}

.container {
  display: flex;
  height: 100vh;
  width: 100vw;
}

/* 左侧面板样式 */
.left-panel {
  width: 35%;
  background-color: #ffffff;
  padding: 40px;
  box-shadow: 2px 0 5px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
}

.left-panel h2 {
  margin-bottom: 20px;
  color: #333333;
}

.input-box {
  flex: 1;
  padding: 12px 20px;
  margin-bottom: 20px;
  border: 1px solid #dcdcdc;
  border-radius: 8px;
  font-size: 16px;
  resize: vertical;
  min-height: 100px;
  transition: border-color 0.3s;
}

.input-box:focus {
  border-color: #409eff;
  outline: none;
}

.send-button {
  padding: 12px 20px;
  background-color: #409eff;
  color: #ffffff;
  border: none;
  border-radius: 8px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
  align-self: flex-start;
}

.send-button:hover {
  background-color: #66b1ff;
}

/* 中间分隔线 */
.separator {
  width: 1px;
  background-color: #e0e0e0;
}

/* 右侧面板样式 */
.right-panel {
  width: 65%;
  background-color: #f9fafb;
  padding: 40px;
  overflow-y: auto;
}

.right-panel h2 {
  margin-bottom: 20px;
  color: #333333;
}

/* 表格样式 */
.el-table {
  background-color: #ffffff;
  border-radius: 8px;
  overflow: hidden;
}

.el-table th {
  background-color: #409eff;
  color: #ffffff;
  font-weight: bold;
}

.el-rate {
  font-size: 16px;
}

/* 滚动条样式（可选，增强美观） */
.right-panel::-webkit-scrollbar {
  width: 8px;
}

.right-panel::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.right-panel::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.right-panel::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}
</style>
