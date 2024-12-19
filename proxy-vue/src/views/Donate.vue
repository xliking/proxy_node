<template>
  <div class="donate-key-container">
    <div class="content-box">
      <p class="description">
        本捐赠的 Key 仅用于维持 IP 检测的使用。若您支持，可在
        <a href="https://dashboard.ipregistry.co/signin" target="_blank" rel="noopener noreferrer">IPRegistry</a>
        注册账号，获取 Key。您也可以发送邮件至
        <a href="mailto:linux@xlike.email">linux@xlike.email</a>
        ，或者在下方的输入框中输入 Key 并发送即可，非常感谢您的支持！
        <br>
        <span class="note">
           <a href="https://github.com/xliking/proxy_node"  target="_blank" rel="noopener noreferrer">原项目在此处，需要的可以查看</a>
        </span>
      </p>

      <form @submit.prevent="submitKey" class="key-form">
        <input
            type="text"
            v-model="key"
            placeholder="请输入您的 IPRegistry Key"
            required
            class="key-input"
        />
        <button type="submit" class="submit-button">
          发送 Key
        </button>
      </form>

      <div v-if="message" :class="['message', messageType]">
        <span class="icon">
          <svg v-if="messageType === 'success'" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
            <path fill="currentColor"
                  d="M9 16.17l-3.88-3.88a1 1 0 00-1.41 1.41l4.59 4.59a1 1 0 001.41 0l10-10a1 1 0 10-1.41-1.41L9 16.17z"/>
          </svg>
          <svg v-else xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24">
            <path fill="currentColor"
                  d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm5 13l-1.41 1.41L12 13.41l-3.59 3.59L7 15l3.59-3.59L7 7.83 8.41 6.42 12 10l3.59-3.58L17 7.83l-3.59 3.59L17 15z"/>
          </svg>
        </span>
        {{ message }}
      </div>
    </div>
  </div>
</template>

<script>
import axios from "../axios.js";

export default {
  name: 'DonateKeyPage',
  data() {
    return {
      key: '',
      message: '',
      messageType: '', // 'success' 或 'error'
    };
  },
  methods: {
    async submitKey() {
      const trimmedKey = this.key.trim();
      if (!trimmedKey) {
        this.message = 'Key 不能为空。';
        this.messageType = 'error';
        return;
      }

      try {
        // 替换为您的后端 API 端点
        const response = await axios.post('/email/sendKey', {
          key: trimmedKey
        });

        if (response.code === 0) {
          this.message = response.data;
          this.messageType = 'success';
          this.key = '';
        } else {
          this.message = response.msg;
          this.messageType = 'error';
        }
      } catch (error) {
        console.error(error);
        this.message = '发送失败，请检查网络连接或稍后再试。';
        this.messageType = 'error';
      }
    },
  },
};
</script>

<style scoped>
.donate-key-container {
  display: flex;
  justify-content: center;
  align-items: center;
  height: 100vh;
  background: linear-gradient(135deg, #e0eafc, #cfdef3);
  padding: 20px;
}

.content-box {
  background-color: #ffffff;
  padding: 40px 30px;
  border-radius: 12px;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
  max-width: 500px;
  width: 100%;
  text-align: center;
}

.description {
  font-size: 1rem;
  color: #555555;
  margin-bottom: 30px;
  line-height: 1.6;
}

.description a {
  color: #4a90e2;
  text-decoration: none;
}

.description a:hover {
  text-decoration: underline;
}

.key-form {
  display: flex;
  flex-direction: column;
  gap: 15px;
}

.key-input {
  padding: 12px 15px;
  border: 1px solid #ccc;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.3s;
}

.key-input:focus {
  border-color: #4a90e2;
  outline: none;
}

.submit-button {
  padding: 12px 15px;
  background-color: #4a90e2;
  color: #ffffff;
  border: none;
  border-radius: 8px;
  font-size: 1rem;
  cursor: pointer;
  transition: background-color 0.3s, transform 0.2s;
}

.submit-button:hover {
  background-color: #357ABD;
  transform: translateY(-2px);
}

.message {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 10px;
  margin-top: 20px;
  padding: 15px 20px;
  border-radius: 8px;
  font-size: 0.95rem;
}

.success {
  background-color: #d4edda;
  color: #155724;
}

.error {
  background-color: #f8d7da;
  color: #721c24;
}

.message .icon {
  width: 20px;
  height: 20px;
}

.message svg {
  width: 100%;
  height: 100%;
}
</style>
