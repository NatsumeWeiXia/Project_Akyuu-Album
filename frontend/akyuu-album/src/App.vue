<script setup lang="ts">
import { RouterLink, RouterView } from 'vue-router'
</script>

<template>
  <header>
    <div class="navbar">
      <div class="navbar-brand">
        <RouterLink to="/">Akyuu Album</RouterLink>
      </div>
      <nav class="navbar-nav">
        <RouterLink to="/albums/public">公共相册</RouterLink>
        <RouterLink to="/albums/mine">我的相册</RouterLink>
      </nav>
      <div class="navbar-auth">
        <RouterLink to="/login">登录</RouterLink>
        <RouterLink to="/register">注册</RouterLink>
      </div>
    </div>
  </header>

  <RouterView />
</template>

<style scoped>
header {
  line-height: 1.5;
  max-height: 100vh;
}

.navbar {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 1rem 2rem;
  background-color: #409eff;
  color: white;
}

.navbar-brand a {
  font-size: 1.5rem;
  font-weight: bold;
  color: white;
  text-decoration: none;
}

.navbar-nav {
  display: flex;
  gap: 1.5rem;
}

.navbar-nav a {
  color: white;
  text-decoration: none;
  padding: 0.5rem 1rem;
  border-radius: 4px;
  transition: background-color 0.3s;
}

.navbar-nav a:hover,
.navbar-nav a.router-link-exact-active {
  background-color: rgba(255, 255, 255, 0.2);
}

.navbar-auth {
  display: flex;
  gap: 1rem;
}

.navbar-auth a {
  color: white;
  text-decoration: none;
  padding: 0.5rem 1rem;
  border: 1px solid rgba(255, 255, 255, 0.5);
  border-radius: 4px;
  transition: all 0.3s;
}

.navbar-auth a:hover {
  background-color: rgba(255, 255, 255, 0.2);
}
</style>
