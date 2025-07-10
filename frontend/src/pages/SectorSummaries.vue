<template>
  <div class="max-w-screen-xl mx-auto px-4 py-6 space-y-8">
    <h1 class="text-2xl font-bold text-center mb-4">Sector Outlooks for 2025 and Beyond</h1>
    <div class="flex gap-4 mb-6 justify-center">
           <button
  @click="loadMarket('stockholm')"
  :class="[
    'px-4 py-2 rounded-lg transition border',
    selectedMarket === 'stockholm'
      ? 'bg-black text-white font-semibold border-black'
      : 'bg-white text-black border-gray-300 hover:bg-gray-100 hover:border-black'
  ]"
>
  Stockholm
</button>

<button
  @click="loadMarket('baltic')"
  :class="[
    'px-4 py-2 rounded-lg transition border',
    selectedMarket === 'baltic'
      ? 'bg-black text-white font-semibold border-black'
      : 'bg-white text-black border-gray-300 hover:bg-gray-100 hover:border-black'
  ]"
>
  Baltic
</button>
    </div>
    <div
      v-for="(block, index) in sortedSummaries"
      :key="block.sector"
      class="border rounded-xl shadow-lg transition-all duration-300 border border-gray-300 overflow-hidden"
    >
      <!-- Header -->
      <div class="bg-gray-100 px-4 py-3 flex justify-between items-center">
        <div class="text-xl font-semibold">{{ block.ranking }}. {{ block.sector }} ({{ block.sentiment }})</div>
        <button class="text-grey-600 hover:underline" @click="toggle(index)">
          {{ expanded[index] ? 'Collapse ▲' : 'Read summary ▼' }}
        </button>
      </div>

      <!-- Chart -->
      <div class="px-4 pt-4"> 
        <SectorCategoryChart 
        :data="block.categoryCounts"
        @bar-click="(payload) => fetchAndShowStatements({ category: payload.category, sentiment: payload.sentiment as 'positive' | 'neutral' | 'negative' }, block.sector)"
        />
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
    <!-- Modal always outside the v-for loop -->
    <StatementModal
      v-if="selectedBar"
      :visible="showModal"
      :sentiment="selectedBar.sentiment"
      :category="selectedBar.category"
      :statements="statementList"
      @close="showModal = false"
    />

  </div>
</template>

<script setup lang="ts">
import { ref, computed, onMounted } from 'vue'
import api from '../services/api'
import SectorCategoryChart from '../components/charts/SectorCategoryChart.vue'
import StatementModal from '../components/charts/StatementModal.vue'

interface SentimentData {
  category: string;
  positiveCount: number;
  neutralCount: number;
  negativeCount: number;
}

interface SectorSummaryBlock {
  sector: string;
  ranking: number;
  sentiment: string;
  summary: string;
  rankingRationale: string;
  toneRationale: string;
  categoryCounts: SentimentData[];
}

const selectedMarket = ref<'stockholm' | 'baltic'>('stockholm')

async function loadMarket(market: 'stockholm' | 'baltic') {
  selectedMarket.value = market
  const res = await api.get<SectorSummaryBlock[]>('/api/sector-summaries', {
    params: { market }
  })
  summaries.value = res.data
  expanded.value = Array(res.data.length).fill(false)
}

const summaries = ref<SectorSummaryBlock[]>([])
const expanded = ref<boolean[]>([])

onMounted(() => {
  loadMarket(selectedMarket.value)
})

// Computed sorting by ranking
const sortedSummaries = computed(() =>
  [...summaries.value].sort((a, b) => a.ranking - b.ranking)
)

function toggle(index: number) {
  expanded.value[index] = !expanded.value[index]
}

const showModal = ref(false)
const statementList = ref<{ companyName: string; content: string }[]>([])
const selectedBar = ref<{ category: string; sentiment: 'positive' | 'neutral' | 'negative' } | null>(null)

function showModalWithStatements(
  statements: { companyName: string; content: string }[],
  category: string,
  sentiment: 'positive' | 'neutral' | 'negative'
) {
  statementList.value = statements
  selectedBar.value = { category, sentiment }
  showModal.value = true
}

async function fetchAndShowStatements(
  payload: { category: string; sentiment: 'positive' | 'neutral' | 'negative' },
  sector: string
) {
  console.log('BAR CLICK RECEIVED:', payload, sector, selectedMarket.value)
  try {
    const res = await api.post('/api/sector-statements', {
      type: 'sector',
      sector: sector,
      category: payload.category,
      sentiment: payload.sentiment,
      market: selectedMarket.value

  })
    showModalWithStatements(res.data, payload.category, payload.sentiment)
  } catch (e) {
    console.warn('API failed, using fallback.')
    showModalWithStatements(
  [
    { companyName: 'Swedbank', content: 'Swedbank sees stable growth in interest income.' },
    { companyName: 'SEB', content: 'SEB plans new AI-driven tools for customer onboarding.' }
  ],
      payload.category,
      payload.sentiment
    )
  }
}

</script>