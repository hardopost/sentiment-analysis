<template>
  <div class="max-w-screen-xl mx-auto px-4 py-6 space-y-8">
    <h1 class="text-2xl font-bold text-center mb-4">Top Companies by Sector (Outlook 2025 and Beyound)</h1>

    <div v-for="(companies, sector) in groupedBySector" :key="sector" class="space-y-6">
      <!-- Sector Heading -->
      <h2 class="text-xl font-semibold text-gray-800 border-b pb-1">{{ sector }}</h2>

      <!-- Companies for this sector -->
<div v-for="company in companies" :key="company.companyName" class="border rounded-xl shadow-md overflow-hidden border border-gray-100">
  <div class="bg-gray-50 px-4 py-2 flex justify-between items-center">
    <div class="font-medium">
      {{ company.sectorRank }}. {{ company.companyName }} ({{ company.capitalization }})
    </div>
    <button class="text-gray-600 hover:underline" @click="toggle(company.companyName)">
      {{ expanded[company.companyName] ? 'Collapse ▲' : 'Read summary ▼' }}
    </button>
  </div>
  <div class="px-4 pt-4">
    <CompanyCategoryChart :data="company.categoryCounts" />
  </div>
  <div v-show="expanded[company.companyName]" class="px-4 pb-6 pt-2 text-gray-800 space-y-4">
    <div>
      <p class="font-semibold">Summary:</p>
      <p class="whitespace-pre-line">{{ company.summary }}</p>
    </div>
    <div>
      <p class="font-semibold">Ranking Rationale:</p>
      <p class="whitespace-pre-line">{{ company.rankRationale }}</p>
    </div>
  </div>
</div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import api from '../services/api'
import CompanyCategoryChart from '../components/charts/CompanyCategoryChart.vue'

interface SentimentData {
  category: string;
  positiveCount: number;
  neutralCount: number;
  negativeCount: number;
}

interface CompanySummary {
  companyName: string;
  sector: string;
  capitalization: string;
  summary: string;
  sectorRank: number;
  rankRationale: string;
  categoryCounts: SentimentData[];
}

const companies = ref<CompanySummary[]>([])
const expanded = ref<Record<string, boolean>>({})

// Load data from backend
onMounted(async () => {
  const res = await api.get<CompanySummary[]>('/api/company-summaries')
  companies.value = res.data
  res.data.forEach(c => (expanded.value[c.companyName] = false))
})

// Toggle collapse per company
function toggle(name: string) {
  expanded.value[name] = !expanded.value[name]
}

// Group and sort companies by sector + rank
const groupedBySector = computed(() => {
  const map: Record<string, CompanySummary[]> = {}

  for (const company of companies.value) {
    if (!map[company.sector]) map[company.sector] = []
    map[company.sector].push(company)
  }

  for (const sector in map) {
    map[sector].sort((a, b) => a.sectorRank - b.sectorRank)
  }

  return map
})
</script>