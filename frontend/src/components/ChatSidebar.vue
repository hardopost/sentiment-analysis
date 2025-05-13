<template>
  <!-- Toggle tab -->
  <div
    class="fixed top-1/2 right-0 transform -translate-y-1/2 bg-blue-600 text-white px-3 py-1 rounded-xl cursor-pointer z-50 rotate-90"
    @click="emit('update:open', true)"
    v-if="!props.open"
  >
    AI Chat ⇩
  </div>

  <!-- Chat sidebar -->
  <div
    class="fixed top-[96px] bottom-6 right-0 w-[30%] bg-white border border-gray-300 rounded-2xl shadow-lg
           transition-transform duration-300 z-40 flex flex-col"
    :class="props.open ? 'translate-x-0' : 'translate-x-full'"
  >
    <!-- Header -->
  <!-- Header -->
  <div class="relative px-4 py-4 border-b border-gray-300">
    <!-- Centered Title -->
    <h2 class="text-lg font-semibold text-center">Chat</h2>

    <!-- Close Button (absolutely positioned to right) -->
    <button
      @click="emit('update:open', false)"
      class="absolute right-4 top-1/2 transform -translate-y-1/2 text-gray-600 hover:text-red-500 text-xl"
    >
      ✕
    </button>
  </div>

    <!-- Messages -->
    <div class="flex flex-col-reverse overflow-y-auto h-full gap-y-4 px-4 py-2">
      <div
    v-for="(msg, index) in messages"
    :key="index"
    :class="[
      'max-w-[75%] px-4 py-3 rounded-lg text-sm shadow whitespace-pre-wrap',
      msg.from === 'user' ? 'self-end bg-gray-200 text-black' : 'self-start bg-blue-100 text-black'
    ]"
  >
    <div v-if="msg.from === 'ai'" v-html="renderMarkdown(msg.text)" />
    <div v-else>{{ msg.text }}</div>
      </div>
    </div>

   <!-- Input -->
<div class="flex border border-gray-300 p-1 rounded-lg">
  <textarea
    v-model="userMessage"
    @keyup.enter.exact.prevent="sendMessage"
    placeholder="Ask a question..."
    rows="1"
    class="flex-1 rounded-l px-3 py-2 resize-none focus:outline-none overflow-hidden"
    @input="autoResize"
  > </textarea>
  <button
    @click="sendMessage"
    class="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 self-end h-10"
  >
    Send
  </button>
</div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue'
import api from '../services/api'
import { marked } from 'marked'
import DOMPurify from 'dompurify'


const props = defineProps<{ open: boolean }>()
const emit = defineEmits(['update:open'])

const userMessage = ref('')
const messages = ref<{ from: 'user' | 'ai', text: string }[]>([])


async function sendMessage() {
  if (!userMessage.value.trim()) return

  const messageToSend = userMessage.value
  userMessage.value = ''
   // Insert user message at the beginning of array
  messages.value.unshift({ from: 'user', text: messageToSend })

  // ✅ Start time
  const start = performance.now()

  try {
    const response = await api.post('/api/chat', {
      message: messageToSend
    })

    // ✅ End time
    const end = performance.now()
    const elapsed = (end - start).toFixed(0)

    // Insert AI response below user message (i.e., above it in array)
    messages.value.unshift({ from: 'ai', text: `${response.data}\n\n_⏱️ Responded in ${elapsed} ms_` })
  } catch (err) {
    messages.value.unshift({ from: 'ai', text: 'Error: failed to get response from server.' })
  }
}

function renderMarkdown(markdown: string): string {
  return DOMPurify.sanitize(marked.parse(markdown) as string) 
}

function autoResize(event: Event) {
  const textarea = event.target as HTMLTextAreaElement
  textarea.style.height = 'auto'
  textarea.style.height = `${textarea.scrollHeight}px`
}

</script>