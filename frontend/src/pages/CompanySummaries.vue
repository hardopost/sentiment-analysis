<template>
  <div class="max-w-screen-xl mx-auto px-4 py-6 space-y-8">
    <h1 class="text-2xl font-bold text-center mb-4">Top Companies by Sector (Outlook 2025 and Beyound)</h1>
        <div class="flex gap-4 mb-6 justify-center">
      <button
        @click="loadMarket('stockholm')"
        :class="[
        'px-4 py-2 rounded-lg transition border',
        selectedMarket === 'stockholm'
        ? 'bg-black text-white font-semibold border-black'
        : 'bg-white text-black border-gray-300 hover:bg-gray-100 hover:border-black'
        ]"
        > Stockholm
      </button>

      <button
        @click="loadMarket('baltic')"
        :class="[
        'px-4 py-2 rounded-lg transition border',
        selectedMarket === 'baltic'
        ? 'bg-black text-white font-semibold border-black'
        : 'bg-white text-black border-gray-300 hover:bg-gray-100 hover:border-black'
        ]"
      > Baltic
      </button>
  </div>

    <!-- 🔍 Custom Company Section -->
  <div class="border rounded-xl shadow p-4 space-y-4 border border-gray-100">
    <div class="flex justify-between items-center">
      <h2 class="text-xl font-semibold">Custom Companies</h2>
      <button @click="showSearch = !showSearch" class="text-blue-600 hover:underline">
        {{ showSearch ? 'Cancel' : 'Add Company' }}
      </button>
    </div>

    <div v-if="showSearch" class="relative">
      <input
        v-model="searchQuery"
        class="border px-4 py-2 w-full rounded"
        placeholder="Search company name..."
      />
      <ul
        v-if="filteredCompanies.length"
        class="absolute z-50 bg-white w-full border max-h-48 overflow-y-auto mt-1 rounded"
      >
        <li
          v-for="name in filteredCompanies"
          :key="name"
          class="px-4 py-2 hover:bg-gray-100 cursor-pointer"
          @click="addCompany(name)"
        >
          {{ name }}
        </li>
      </ul>
    </div>

  <div v-if="customCompanies.length" class="space-y-4">
    <div
      v-for="company in customCompanies"
      :key="company.companyName"
      class="border rounded-xl shadow-md overflow-hidden border border-gray-100"
    >
      <div class="bg-gray-50 px-4 py-2 flex justify-between items-center">
        <div class="font-medium">
          {{ company.companyName }} ({{ company.capitalization }})
        </div>
                <button
          class="text-red-600 hover:underline text-sm"
          @click="removeCompany(company.companyName)"
        >
        Remove ✕
        </button>
        <button class="text-gray-600 hover:underline" @click="toggle(company.companyName)">
          {{ expanded[company.companyName] ? 'Collapse ▲' : 'Read summary ▼' }}
        </button>

      </div>
      <div class="px-4 pt-4">
        <CompanyCategoryChart 
          :data="company.categoryCounts"
          @bar-click="(payload) => fetchAndShowStatements({ category: payload.category, sentiment: payload.sentiment as 'positive' | 'neutral' | 'negative' }, company.companyName)"
        />
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
        <div v-if="company.positiveDrivers?.length">
          <p class="font-semibold">Positive Drivers:</p>
          <div class="flex flex-wrap gap-2 mt-1">
            <span
              v-for="(driver, i) in company.positiveDrivers"
              :key="'pos-' + i"
              class="bg-green-100 text-green-800 px-3 py-1 rounded-full text-sm"
            >
              {{ driver }}
            </span>
          </div>
        </div>
        <div v-if="company.negativeDrivers?.length">
          <p class="font-semibold">Negative Drivers:</p>
          <div class="flex flex-wrap gap-2 mt-1">
            <span
              v-for="(driver, i) in company.negativeDrivers"
              :key="'neg-' + i"
              class="bg-red-100 text-red-800 px-3 py-1 rounded-full text-sm"
            >
              {{ driver }}
            </span>
          </div>
        </div>
      </div>
    </div>
  </div>
</div>


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
    <CompanyCategoryChart 
    :data="company.categoryCounts"
    @bar-click="(payload) => fetchAndShowStatements({ category: payload.category, sentiment: payload.sentiment as 'positive' | 'neutral' | 'negative' }, company.companyName)"
    />
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
      <!-- ✅ Positive Drivers -->
  <div v-if="company.positiveDrivers?.length">
    <p class="font-semibold">Positive Drivers:</p>
    <div class="flex flex-wrap gap-2 mt-1">
      <span
        v-for="(driver, i) in company.positiveDrivers"
        :key="'pos-' + i"
        class="bg-green-100 text-green-800 px-3 py-1 rounded-full text-sm"
      >
        {{ driver }}
      </span>
    </div>
  </div>

  <!-- ✅ Negative Drivers -->
  <div v-if="company.negativeDrivers?.length">
    <p class="font-semibold">Negative Drivers:</p>
    <div class="flex flex-wrap gap-2 mt-1">
      <span
        v-for="(driver, i) in company.negativeDrivers"
        :key="'neg-' + i"
        class="bg-red-100 text-red-800 px-3 py-1 rounded-full text-sm"
      >
        {{ driver }}
      </span>
    </div>
  </div>
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
import { ref, onMounted, computed } from 'vue'
import api from '../services/api'
import CompanyCategoryChart from '../components/charts/CompanyCategoryChart.vue'
import StatementModal from '../components/charts/StatementModal.vue'

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
  positiveDrivers: string[];
  negativeDrivers: string[];
  sectorRank: number;
  rankRationale: string;
  categoryCounts: SentimentData[];
}

const selectedMarket = ref<'stockholm' | 'baltic'>('stockholm')
const companies = ref<CompanySummary[]>([])
const expanded = ref<Record<string, boolean>>({})

async function loadMarket(market: 'stockholm' | 'baltic') {
  selectedMarket.value = market
  const res = await api.get<CompanySummary[]>('/api/company-summaries', {
    params: { market }
  })
  companies.value = res.data
  res.data.forEach(c => (expanded.value[c.companyName] = false))
}

const showSearch = ref(false)
const searchQuery = ref('')
const allCompanies = ref<string[]>([])
const customCompanies = ref<CompanySummary[]>([])

const filteredCompanies = computed(() =>
  allCompanies.value
    .filter(name => name.toLowerCase().includes(searchQuery.value.toLowerCase()))
    .filter(name => !customCompanies.value.some(c => c.companyName === name))
)

async function loadAllCompanyNames() {
  const res = await api.get<string[]>('/api/company-names')
  allCompanies.value = res.data
}

async function addCompany(name: string) {
  const res = await api.get<CompanySummary>('/api/company-summary', {
    params: { companyName: name }
  })
  customCompanies.value.push(res.data)
  expanded.value[res.data.companyName] = false
  searchQuery.value = ''
  showSearch.value = false
}

function removeCompany(name: string) {
  customCompanies.value = customCompanies.value.filter(c => c.companyName !== name)
  delete expanded.value[name]
}

// Load data from backend
onMounted(async () => {
  loadMarket(selectedMarket.value)
  loadAllCompanyNames()
})

// Toggle collapse per company
function toggle(name: string) {
  expanded.value[name] = !expanded.value[name]
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

async function fetchAndShowStatements(
  payload: { category: string; sentiment: 'positive' | 'neutral' | 'negative' },
  companyName: string
) {
  console.log('BAR CLICK RECEIVED:', payload, companyName)
  try {
    const res = await api.post('/api/company-statements', {
      type: 'company',
      companyName: companyName,
      category: payload.category,
      sentiment: payload.sentiment

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