<template>
  <div class="container">
    <!-- 左侧面板 -->
    <div class="left-panel">
      <h2>批量IP检测</h2>
      <textarea
          v-model="inputData"
          @keydown="handleKeydown"
          placeholder="请输入IP,暂不支持域名，可以,号或者换行都可以  ctrl+enter 发送请求"
          class="input-box"
      ></textarea>
      <button @click="sendRequest" class="send-button">获取结果</button>
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
            prop="type"
            label="类型"
            width="80">
        </el-table-column>
        <el-table-column
            prop="companyName"
            label="所属企业"
            min-width="180">
        </el-table-column>
        <el-table-column
            prop="countryAddress"
            label="国家区域"
            width="150">
        </el-table-column>

        <el-table-column
            prop="isAbuser"
            label="是否滥用"
            width="100"
            align="center"
        >
          <template v-slot="scope">
            <el-tooltip content="表示 IP 地址是否是已知的滥用来源（例如垃圾邮件、收割者、注册机器人）" placement="top">
              <el-tag :type="scope.row.isAbuser ? 'danger' : 'success'">
                {{ scope.row.isAbuser ? '是' : '否' }}
              </el-tag>
            </el-tooltip>
          </template>
        </el-table-column>

        <el-table-column
            prop="isCloudProvider"
            label="是否托管"
            width="100"
            align="center"
        >
          <template v-slot="scope">
            <el-tooltip
                content="表示 IP 地址是否用于托管目的（例如来自 Akamai、Cloudflare、Google Cloud Platform、Amazon EC2 等的节点）"
                placement="top">
              <el-tag :type="scope.row.isCloudProvider ? 'danger' : 'success'">
                {{ scope.row.isCloudProvider ? '是' : '否' }}
              </el-tag>
            </el-tooltip>
          </template>
        </el-table-column>

        <!-- 是否已知代理 -->
        <el-table-column
            prop="isProxy"
            label="是否已知代理"
            width="120"
            align="center"
        >
          <template v-slot="scope">
            <el-tooltip
                content="表示 IP 地址是否为已知代理。它包括 HTTP/HTTPS/SSL/SOCKS/CONNECT 和透明代理"
                placement="top">
              <el-tag :type="scope.row.isProxy ? 'danger' : 'success'">
                {{ scope.row.isProxy ? '是' : '否' }}
              </el-tag>
            </el-tooltip>
          </template>
        </el-table-column>

        <!-- 是否中继 -->
        <el-table-column
            prop="isRelay"
            label="是否中继"
            width="100"
            align="center"
        >
          <template v-slot="scope">
            <el-tooltip
                content="表示 IP 地址是否为已知中继。中继 IP 地址并非设计用于绕过地理控制，而是将多个用户集中到同一个 IP 后面"
                placement="top">
              <el-tag :type="scope.row.isRelay ? 'danger' : 'success'">
                {{ scope.row.isRelay ? '是' : '否' }}
              </el-tag>
            </el-tooltip>
          </template>
        </el-table-column>

        <!-- 是否VPN -->
        <el-table-column
            prop="isVpn"
            label="是否VPN"
            width="100"
            align="center"
        >
          <template v-slot="scope">
            <el-tooltip
                content="表示此 IP 地址被虚拟专用网络 (VPN) 使用"
                placement="top">
              <el-tag :type="scope.row.isVpn ? 'danger' : 'success'">
                {{ scope.row.isVpn ? '是' : '否' }}
              </el-tag>
            </el-tooltip>
          </template>
        </el-table-column>

      </el-table>
    </div>


  </div>
</template>

<script setup>
import {ref, onMounted} from 'vue';
import axios from '@/axios';
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


const handleKeydown = (event) => {
  if (event.ctrlKey && event.key === 'Enter') {
    sendRequest();
  }
};

// 获取当前IP地址
const getClientIP = async () => {
  try {
    // 获取客户端的公共IP
    const ipResponse = await axios.get('https://api.ipify.org?format=json');
    const ip = ipResponse.ip;
    if (!ip) {
      ElMessage.error('未能获取到IP地址，请手动输入');
      return;
    }

    // 请求后端API以获取IP相关信息
    const ipInfoResponse = await axios.post('/ip', {query: ip});

    // 假设响应数据是一个数组，包含 ip, type, companyName, countryAddress, isAbuser, isCloudProvider, isProxy, isRelay, isVpn 等字段
    const newData = ipInfoResponse.data.map((item) => ({
      ip: item.ip || '未知',
      type: item.type || '未知',
      companyName: item.companyName || '未知',
      countryAddress: item.countryAddress || '未知',
      isAbuser: item.isAbuser ?? 'N/A',
      isCloudProvider: item.isCloudProvider ?? 'N/A',
      isProxy: item.isProxy ?? 'N/A',
      isRelay: item.isRelay ?? 'N/A',
      isVpn: item.isVpn ?? 'N/A',
    }));


    // 将新数据添加到现有数据的顶部
    responses.value = [...newData, ...responses.value];
    inputData.value = '';
    ElMessage.success('请求成功！');
  } catch (error) {
    console.error('请求失败:', error);

    // 根据错误类型进行不同的处理
    if (error.response) {
      // 服务器返回了一个状态码，但状态码超出了2xx的范围
      ElMessage.error(`请求失败：${error.response.status} ${error.response.statusText}`);
    } else if (error.request) {
      // 请求已经发出，但没有收到响应
      ElMessage.error('请求失败：未收到服务器响应');
    } else {
      // 在设置请求时发生了一些事情，触发了一个错误
      ElMessage.error(`请求失败：${error.message}`);
    }

    // 在顶部插入一条错误信息
    responses.value.unshift({
      ip: 'N/A',
      type: 'N/A',
      companyName: '请求失败',
      countryAddress: '未知',
      isAbuser: 'N/A',
      isCloudProvider: 'N/A',
      isProxy: 'N/A',
      isRelay: 'N/A',
      isVpn: 'N/A',
    });
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
    const ipInfoResponse = await axios.post('/ip', {
      query: dataToSend,
    });

    // 假设响应数据是一个数组，包含 ip, country, address, riskLevel 等字段
    const newData = ipInfoResponse.data.map((item) => ({
      ip: item.ip || '未知',
      type: item.type || '未知',
      companyName: item.companyName || '未知',
      countryAddress: item.countryAddress || '未知',
      isAbuser: item.isAbuser ?? 'N/A',
      isCloudProvider: item.isCloudProvider ?? 'N/A',
      isProxy: item.isProxy ?? 'N/A',
      isRelay: item.isRelay ?? 'N/A',
      isVpn: item.isVpn ?? 'N/A',
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
      type: 'N/A',
      companyName: '请求失败',
      countryAddress: '未知',
      isAbuser: 'N/A',
      isCloudProvider: 'N/A',
      isProxy: 'N/A',
      isRelay: 'N/A',
      isVpn: 'N/A',
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
