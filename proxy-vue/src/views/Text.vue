<template>
  <div class="app-container">
    <header class="header">
      <h1>文本转换工具</h1>
    </header>
    <main class="main-content">
      <section class="input-section">
        <label for="inputText">输入文本：</label>
        <textarea
            id="inputText"
            v-model="inputText"
            placeholder="在此输入带有逗号或换行符的文本"
        ></textarea>
        <div class="buttons">
          <button @click="convertToNewline">
            <i class="fas fa-arrow-down"></i> 逗号转换为换行符
          </button>
          <button @click="convertToComma">
            <i class="fas fa-arrow-up"></i> 换行符转换为逗号
          </button>
        </div>
      </section>

      <section class="preview-section">
        <label for="previewText">预览：</label>
        <textarea
            id="previewText"
            :value="outputText"
            readonly
            placeholder="转换后的文本将显示在这里"
        ></textarea>
        <div class="copy-container">
          <button class="copy-button" @click="copyToClipboard">
            <i class="fas fa-copy"></i> 一键复制
          </button>
          <transition name="fade">
            <div v-if="copySuccess" class="copy-success">
              <i class="fas fa-check-circle"></i> 文本已成功复制到剪贴板！
            </div>
          </transition>
        </div>
      </section>
    </main>
    <footer class="footer">
      <p>&copy; 2024 xlike文本符转换工具</p>
    </footer>
  </div>
</template>

<script>
export default {
  name: 'CommaSplitter',
  data() {
    return {
      inputText: '',
      outputText: '',
      copySuccess: false,
    };
  },
  methods: {
    convertToNewline() {
      this.outputText = this.inputText
          .split(',')
          .map(item => item.trim())
          .filter(item => item)
          .join('\n');
    },
    convertToComma() {
      this.outputText = this.inputText
          .split(/\r?\n/)
          .map(item => item.trim())
          .filter(item => item)
          .join(',');
    },
    async copyToClipboard() {
      if (!this.outputText) return;
      try {
        await navigator.clipboard.writeText(this.outputText);
        this.showCopySuccess();
      } catch (err) {
        // 备用方案：使用 document.execCommand
        const textarea = document.createElement('textarea');
        textarea.value = this.outputText;
        document.body.appendChild(textarea);
        textarea.select();
        try {
          document.execCommand('copy');
          this.showCopySuccess();
        } catch (err) {
          alert('复制失败，请手动复制。');
        }
        document.body.removeChild(textarea);
      }
    },
    showCopySuccess() {
      this.copySuccess = true;
      setTimeout(() => {
        this.copySuccess = false;
      }, 3000);
    },
  },
};
</script>

<style scoped>
/* 引入字体图标库 Font Awesome */
@import url('https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css');

/* 全局样式重置 */
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

html, body, #app, .app-container {
  height: 100%;
  width: 100%;
  font-family: 'Roboto', sans-serif;
}

/* 容器设置为全屏 */
.app-container {
  display: flex;
  flex-direction: column;
  background: linear-gradient(135deg, #667eea, #764ba2);
  color: #ffffff;
  height: 100vh; /* 确保填满视口高度 */
}

/* 头部样式 */
.header {
  background: rgba(0, 0, 0, 0.5);
  padding: 20px;
  text-align: center;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
}

.header h1 {
  font-size: 2.5rem;
  font-weight: 700;
  letter-spacing: 2px;
}

/* 主内容区布局 */
.main-content {
  flex: 1;
  display: flex;
  padding: 20px;
  gap: 20px;
  max-width: 100%;
  margin: 0 auto;
  width: 100%;
  overflow: hidden;
}

/* 输入和预览部分样式 */
.input-section,
.preview-section {
  flex: 1;
  background: rgba(255, 255, 255, 0.1);
  padding: 20px;
  border-radius: 15px;
  backdrop-filter: blur(10px);
  box-shadow: 0 8px 32px rgba(0, 0, 0, 0.2);
  display: flex;
  flex-direction: column;
  height: 100%;
}

.input-section label,
.preview-section label {
  margin-bottom: 10px;
  font-size: 1.2rem;
  font-weight: 600;
}

textarea {
  flex: 1; /* 让 textarea 填满可用高度 */
  width: 100%;
  padding: 15px;
  border: none;
  border-radius: 10px;
  resize: none;
  font-size: 1.1rem;
  font-family: 'Courier New', Courier, monospace;
  background: rgba(255, 255, 255, 0.2);
  color: #ffffff;
  outline: none;
  margin-bottom: 15px;
  overflow: auto;
}

textarea::placeholder {
  color: #e0e0e0;
}

textarea:focus {
  background: rgba(255, 255, 255, 0.3);
}

/* 按钮样式 */
.buttons {
  display: flex;
  gap: 15px;
}

.buttons button,
.copy-button {
  flex: 1;
  padding: 10px 15px; /* 调整按钮高度 */
  background: #ff7e5f;
  border: none;
  border-radius: 10px;
  color: #ffffff;
  font-size: 0.9rem; /* 调整字体大小 */
  font-weight: 600;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  transition: background 0.3s, transform 0.2s;
}

.buttons button:hover {
  background: #feb47b;
  transform: translateY(-2px);
}

.copy-container {
  display: flex;
  flex-direction: column;
  align-items: flex-end;
}

.copy-button {
  background: #43cea2;
  padding: 8px 12px; /* 调整复制按钮的高度和宽度 */
  font-size: 0.9rem; /* 调整字体大小 */
  margin-top: 10px;
  width: fit-content; /* 让按钮根据内容自动调整宽度 */
}

.copy-button:hover {
  background: #185a9d;
  transform: translateY(-2px);
}

/* 复制成功提示 */
.copy-success {
  margin-top: 10px;
  padding: 10px 15px;
  background: rgba(0, 0, 0, 0.3);
  border-radius: 8px;
  display: flex;
  align-items: center;
  gap: 10px;
  font-size: 1rem;
  color: #28a745;
  animation: fadeIn 0.5s;
}

.copy-success i {
  color: #28a745;
}

/* 页脚样式 */
.footer {
  background: rgba(0, 0, 0, 0.2);
  text-align: center;
  padding: 15px;
  font-size: 0.9rem;
}

/* 动画效果 */
@keyframes fadeIn {
  from { opacity: 0; }
  to { opacity: 1; }
}

.fade-enter-active, .fade-leave-active {
  transition: opacity 0.5s;
}
.fade-enter-from, .fade-leave-to {
  opacity: 0;
}

/* 响应式设计 */
@media (max-width: 992px) {
  .main-content {
    flex-direction: column;
    padding: 10px;
    gap: 10px;
  }

  .header h1 {
    font-size: 2rem;
  }
}

@media (max-width: 576px) {
  .header h1 {
    font-size: 1.8rem;
  }

  .buttons button,
  .copy-button {
    font-size: 0.8rem;
    padding: 8px 10px;
  }
}
</style>
