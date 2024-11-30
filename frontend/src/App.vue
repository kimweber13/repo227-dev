<script setup lang="ts">
import { RouterView } from 'vue-router';
import { Close, Header, HeaderNav, HeaderNavItem, Toast, Toasts } from "agnostic-vue";
import { activeToasts } from "@/ts/toasts";

import 'agnostic-vue/dist/common.min.css';
import "agnostic-vue/dist/index.css";
</script>

<template>
  <div id="app">
    <Header isHeaderContentStart>
      <template v-slot:headernav>
        <HeaderNav>
          <HeaderNavItem>
            <RouterLink to="/">Home</RouterLink>
          </HeaderNavItem>
          <HeaderNavItem>
            <RouterLink to="/about">About</RouterLink>
          </HeaderNavItem>
          <HeaderNavItem>
            <RouterLink to="/assignees">Assignees</RouterLink>
          </HeaderNavItem>
          <HeaderNavItem>
            <RouterLink to="/todos">ToDos</RouterLink>
          </HeaderNavItem>
        </HeaderNav>
      </template>
    </Header>

    <div class="main">
      <RouterView/>
    </div>

    <Toasts vertical-position="top" horizontal-position="end">
      <template v-for="toast of activeToasts" :key="toast.key">
        <Toast :type="toast.type" class="alert alert-border-left alert-info">
          <div class="flex-fill flex flex-column">
            <div class="flex">
              <h3 class="flex-fill">{{ toast.title }}</h3>
              <Close @click="toast.close()"/>
            </div>
            <div class="flex">
              <font-awesome-icon :icon="toast.icon" size="xl" class="mie8 pbs2"></font-awesome-icon>
              <div class="flex-fill">
                {{ toast.message }}
              </div>
            </div>
          </div>
        </Toast>
        <div class="mbe14"/>
      </template>
    </Toasts>
  </div>
</template>

<style scoped>
body {
  background-color: #2e2e2e; /* Dark grey background */
  color: #ffffff; /* White text */
}

.main {
  padding: 20px;
  background-color: #2e2e2e; /* Dark grey background */
  min-height: 100vh;
}

header {
  background-color: #1f1f1f; /* Slightly darker header */
  color: white;
}

header a {
  color: white;
  text-decoration: none;
  padding: 10px 15px;
}

header a:hover {
  background-color: #3a3a3a; /* Darker hover effect */
}

.toasts-container {
  position: fixed;
  top: 20px;
  right: 20px;
}

.toast {
  background-color: #4a4a4a; /* Lighter grey for toast */
  border-left: 4px solid #3498db;
  box-shadow: 0px 0px 10px rgba(0,0,0,0.1);
}

.toast h3 {
  margin: 0;
}

.toast .close-button {
  cursor: pointer;
}
</style>
