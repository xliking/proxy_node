<template>
  <div class="home-container">
    <div class="cards-container">
      <NavBox title="获取节点" to="/Proxy">
        <template #icon>
          <i class="fa fa-location-arrow"></i>
        </template>
      </NavBox>
      <NavBox title="文本转化" to="/Text">
        <template #icon>
          <i class="fa fa-pencil"></i>
        </template>
      </NavBox>
      <NavBox title="IP检测" to="/DetectionIp">
        <template #icon>
          <i class="fas fa-chart-line"></i>
        </template>
      </NavBox>
      <NavBox title="key捐赠" to="/Donate">
        <template #icon>
          <i class="fa fa-heart"></i>
        </template>
      </NavBox>
    </div>
  </div>
</template>

<script>
import NavBox from '../components/NavBox.vue';

export default {
  name: 'Home',
  components: {
    NavBox,
  },
};
</script>

<style scoped>
.home-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 60px 20px;
  min-height: 100vh;
  background-color: #f5f7fa;
}

.home-title {
  font-size: 2.5rem;
  margin-bottom: 40px;
  color: #333333;
}

.cards-container {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 30px;
  width: 100%;
  max-width: 1000px;
}

@media (max-width: 768px) {
  .home-title {
    font-size: 2rem;
    margin-bottom: 30px;
  }

  .cards-container {
    gap: 20px;
  }
}
</style>
