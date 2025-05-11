<template>
  <TopNav />
  <div class="flex transition-all duration-300">
    <div
      :class="chatOpen ? 'w-[70%]' : 'w-full'"
      class="transition-all duration-300"
    >
      <router-view />
    </div>
    <ChatSidebar v-model:open="chatOpen" />
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import TopNav from './components/TopNav.vue'
import ChatSidebar from './components/ChatSidebar.vue'

const chatOpen = ref(false)
</script>

