<template>
  <div class="nav-card" @click="navigate">
    <div class="icon">
      <slot name="icon"></slot>
    </div>
    <div class="title">{{ title }}</div>
  </div>
</template>

<script>
export default {
  name: 'NavCard',
  props: {
    title: {
      type: String,
      required: true,
    },
    to: {
      type: String,
      required: true,
    },
  },
  methods: {
    navigate() {
      this.$router.push(this.to);
    },
  },
};
</script>

<style scoped>
.nav-card {
  background-color: #ffffff;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  padding: 30px 20px;
  text-align: center;
  cursor: pointer;
  transition: transform 0.3s, box-shadow 0.3s;
  display: flex;
  flex-direction: column;
  align-items: center;
}

.nav-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 10px 15px rgba(0, 0, 0, 0.2);
}

.icon {
  font-size: 3rem;
  color: #4a90e2;
  margin-bottom: 15px;
}

.title {
  font-size: 1.2rem;
  font-weight: 600;
  color: #333333;
}

@media (max-width: 768px) {
  .nav-card {
    padding: 20px 15px;
  }

  .icon {
    font-size: 2.5rem;
  }

  .title {
    font-size: 1rem;
  }
}
</style>
