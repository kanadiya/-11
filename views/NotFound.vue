<template>
  <div class="not-found">
    <div class="not-found-content">
      <div class="error-code">404</div>
      <div class="error-message">页面未找到</div>
      <div class="error-description">抱歉，您访问的页面不存在或已被删除</div>
      <div class="actions">
        <el-button type="primary" @click="goHome">返回首页</el-button>
        <el-button @click="goBack">返回上一页</el-button>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'NotFound',
  methods: {
    goHome() {
      this.$router.push('/');
    },
    goBack() {
      this.$router.go(-1);
    }
  }
}
</script>

<style scoped>
.not-found {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.not-found-content {
  text-align: center;
  color: white;
  padding: 2rem;
}

.error-code {
  font-size: 8rem;
  font-weight: bold;
  margin-bottom: 1rem;
  text-shadow: 2px 2px 4px rgba(0, 0, 0, 0.3);
}

.error-message {
  font-size: 2rem;
  margin-bottom: 1rem;
  font-weight: 500;
}

.error-description {
  font-size: 1.1rem;
  margin-bottom: 2rem;
  opacity: 0.9;
}

.actions {
  display: flex;
  gap: 1rem;
  justify-content: center;
}

.actions .el-button {
  min-width: 120px;
}
</style> 