<template>
  <div class="max-w-screen-xl mx-auto px-4 py-6 space-y-8">
    <h1 class="text-2xl font-bold text-center mb-4">Sector Outlooks for 2025 and Beyond</h1>

    <div
      v-for="(block, index) in sortedSummaries"
      :key="block.sector"
      class="border rounded-xl shadow-lg transition-all duration-300 border border-gray-300 overflow-hidden"
    >
      <!-- Header -->
      <div class="bg-gray-100 px-4 py-3 flex justify-between items-center">
        <div class="text-xl font-semibold">{{ block.ranking }}. {{ block.sector }} ({{ block.tone }})</div>
        <button class="text-grey-600 hover:underline" @click="toggle(index)">
          {{ expanded[index] ? 'Collapse ▲' : 'Read summary ▼' }}
        </button>
      </div>

      <!-- Chart -->
      <div class="px-4 pt-4">
        <SectorCategoryChart :data="block.categoryCounts" />
      </div>

      <!-- Texts -->
      <div
        class="px-4 pb-6 pt-2 text-gray-800 space-y-4 transition-all"
        v-show="expanded[index]"
      >
        <div>
          <p class="font-semibold">Summary:</p>
          <p class="whitespace-pre-line">{{ block.summary }}</p>
        </div>
        <div>
          <p class="font-semibold">Sentiment Rationale:</p>
          <p class="whitespace-pre-line">{{ block.toneRationale }}</p>
        </div>
        <div>
          <p class="font-semibold">Ranking Rationale:</p>
          <p class="whitespace-pre-line">{{ block.rankingRationale }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import api from '../services/api'
import SectorCategoryChart from '../components/charts/SectorCategoryChart.vue'

interface SentimentData {
  category: string;
  positiveCount: number;
  neutralCount: number;
  negativeCount: number;
}

interface SectorSummaryBlock {
  sector: string;
  ranking: number;
  tone: string;
  summary: string;
  rankingRationale: string;
  toneRationale: string;
  categoryCounts: SentimentData[];
}

const summaries = ref<SectorSummaryBlock[]>([])
const expanded = ref<boolean[]>([])

onMounted(async () => {
  const res = await api.get<SectorSummaryBlock[]>('/api/sector-summaries')
  summaries.value = res.data
  expanded.value = Array(res.data.length).fill(false)
})

// Computed sorting by ranking
const sortedSummaries = computed(() =>
  [...summaries.value].sort((a, b) => a.ranking - b.ranking)
)

function toggle(index: number) {
  expanded.value[index] = !expanded.value[index]
}
</script>