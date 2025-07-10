<template>
  <Teleport to="body">
    <div
      v-if="visible"
      class="fixed inset-0 z-50 flex items-center justify-center bg-black/30"
    >
      <div
        class="bg-white rounded-xl shadow-xl max-w-3xl w-full max-h-[80vh] overflow-hidden p-0 relative"
      >
        <!-- Sticky Header -->
        <div class="sticky top-0 bg-white z-10 px-6 py-4 border-b flex justify-between items-center">
          <h2 class="text-xl font-semibold text-gray-800">
            {{ sentimentLabel }} statements for <span class="font-bold">{{ category }}</span>
          </h2>
          <button
            @click="emit('close')"
            class="text-2xl font-bold text-gray-500 hover:text-red-500"
          >
            &times;
          </button>
        </div>

        <!-- Scrollable List -->
        <div class="px-6 py-4 overflow-y-auto max-h-[calc(80vh-72px)]">
          <ul class="space-y-4">
            <li
              v-for="(stmt, idx) in statements"
              :key="idx"
              class="border-b pb-2"
            >
              <div class="text-sm text-gray-600 font-semibold">
                {{ stmt.companyName }}
              </div>
              <div class="text-base text-gray-800 mt-1">
                {{ stmt.content }}
              </div>
            </li>
          </ul>
        </div>
      </div>
    </div>
  </Teleport>
</template>

<script setup lang="ts">
import { computed } from 'vue'

const props = defineProps<{
  visible: boolean
  sentiment: string
  category: string
  statements: {
    companyName: string
    content: string
  }[]
}>()

const emit = defineEmits<{
  (e: 'close'): void
}>()

const sentimentLabel = computed(() =>
  props.sentiment.charAt(0).toUpperCase() + props.sentiment.slice(1)
)
</script>



<style scoped>
::-webkit-scrollbar {
  width: 8px;
}
::-webkit-scrollbar-thumb {
  background: #ccc;
  border-radius: 4px;
}
</style>