<template>
  <div class="max-w-screen-lg mx-auto px-4 py-6">
    <h1 class="text-2xl font-bold text-center mb-6">Validation Overview</h1>

    <div v-if="!data">Loading...</div>

    <div v-else>
      <ValidationChart :data="categoryCounts" />
    </div>
<!-- 📊 Report Summary -->
<div v-if="metadata" class="mt-10 space-y-8">

  <div>
    <h2 class="text-xl font-semibold mb-2">Report Type Counts</h2>
    <table class="w-full table-auto border border-gray-300 text-sm">
      <thead class="bg-gray-100">
        <tr>
          <th class="border px-4 py-2 text-left">Report Type</th>
          <th class="border px-4 py-2 text-right">Count</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(count, type) in metadata.reportTypeCounts" :key="type">
          <td class="border px-4 py-2">{{ type }}</td>
          <td class="border px-4 py-2 text-right">{{ count }}</td>
        </tr>
      </tbody>
    </table>
  </div>

  <div>
    <h2 class="text-xl font-semibold mb-2">Fiscal Year Counts</h2>
    <table class="w-full table-auto border border-gray-300 text-sm">
      <thead class="bg-gray-100">
        <tr>
          <th class="border px-4 py-2 text-left">Fiscal Year</th>
          <th class="border px-4 py-2 text-right">Count</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="(count, year) in metadata.fiscalYearCounts" :key="year">
          <td class="border px-4 py-2">{{ year }}</td>
          <td class="border px-4 py-2 text-right">{{ count }}</td>
        </tr>
      </tbody>
    </table>
  </div>

  <div>
    <h2 class="text-xl font-semibold mb-2">Market Distribution</h2>
    <table class="w-full table-auto border border-gray-300 text-sm">
      <tbody>
        <tr>
          <td class="border px-4 py-2 font-medium">Total Reports</td>
          <td class="border px-4 py-2 text-right">{{ metadata.totalReports }}</td>
        </tr>
        <tr>
          <td class="border px-4 py-2 font-medium">Stockholm Reports</td>
          <td class="border px-4 py-2 text-right">{{ metadata.marketCounts?.stockholm ?? 0 }}</td>
        </tr>
        <tr>
          <td class="border px-4 py-2 font-medium">Baltic Reports</td>
          <td class="border px-4 py-2 text-right">{{ metadata.marketCounts?.baltic ?? 0 }}</td>
        </tr>
      </tbody>
    </table>
  </div>

  <div v-if="statementStats" class="mt-10">
  <h2 class="text-xl font-semibold mb-4">Extracted Statements</h2>
  <table class="w-full table-auto border border-gray-300 text-sm">
    <tbody>
      <tr>
        <td class="border px-4 py-2 font-medium">Total Statements</td>
        <td class="border px-4 py-2 text-right">{{ statementStats.totalStatements }}</td>
      </tr>
      <tr>
        <td class="border px-4 py-2">Company Statements</td>
        <td class="border px-4 py-2 text-right">{{ statementStats.companyStatements }}</td>
      </tr>
      <tr>
        <td class="border px-4 py-2">Sector Statements</td>
        <td class="border px-4 py-2 text-right">{{ statementStats.sectorStatements }}</td>
      </tr>
      <tr>
        <td class="border px-4 py-2">Company Statements (Stockholm)</td>
        <td class="border px-4 py-2 text-right">{{ statementStats.companyStatementsStockholm }}</td>
      </tr>
      <tr>
        <td class="border px-4 py-2">Company Statements (Baltic)</td>
        <td class="border px-4 py-2 text-right">{{ statementStats.companyStatementsBaltic }}</td>
      </tr>
      <tr>
        <td class="border px-4 py-2">Sector Statements (Stockholm)</td>
        <td class="border px-4 py-2 text-right">{{ statementStats.sectorStatementsStockholm }}</td>
      </tr>
      <tr>
        <td class="border px-4 py-2">Sector Statements (Baltic)</td>
        <td class="border px-4 py-2 text-right">{{ statementStats.sectorStatementsBaltic }}</td>
      </tr>
    </tbody>
  </table>
</div>


</div>


  </div>
</template>

<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import api from '../services/api'
import ValidationChart from '../components/charts/ValidationChart.vue'

interface ValidationStatsDTO {
  matchFoundTrue: number
  matchFoundFalse: number
  typeValidTrue: number
  typeValidFalse: number
  categoryValidTrue: number
  categoryValidFalse: number
  sentimentValidTrue: number
  sentimentValidFalse: number
  sectorValidTrue: number
  sectorValidFalse: number
  esgYesFinancial: number
  esgYesNonfinancial: number
  esgNo: number
}

const data = ref<ValidationStatsDTO | null>(null)

onMounted(async () => {
  const res = await api.get<ValidationStatsDTO>('/api/info')
  data.value = res.data
})

interface ReportsStatsDTO {
  totalReports: number
  reportTypeCounts: Record<string, number>
  fiscalYearCounts: Record<string, number>
  marketCounts: Record<string, number>
}

interface InfoPageStatsDTO {
  validationStats: ValidationStatsDTO
  reportsStats: ReportsStatsDTO
}

const validation = ref<ValidationStatsDTO | null>(null)
const metadata = ref<ReportsStatsDTO | null>(null)

interface StatementStatsDTO {
  totalStatements: number
  companyStatements: number
  sectorStatements: number
  companyStatementsStockholm: number
  companyStatementsBaltic: number
  sectorStatementsStockholm: number
  sectorStatementsBaltic: number
}

interface InfoPageStatsDTO {
  validationStats: ValidationStatsDTO
  reportsStats: ReportsStatsDTO
  statementStats: StatementStatsDTO
}

const statementStats = ref<StatementStatsDTO | null>(null)

onMounted(async () => {
  const res = await api.get<InfoPageStatsDTO>('/api/info')
  validation.value = res.data.validationStats
  metadata.value = res.data.reportsStats
  statementStats.value = res.data.statementStats
})


const categoryCounts = computed(() => {
  if (!validation.value) return []

  return [
    {
      category: 'Match Found',
      trueCount: validation.value.matchFoundTrue,
      falseCount: validation.value.matchFoundFalse
    },
    {
      category: 'Type Valid',
      trueCount: validation.value.typeValidTrue,
      falseCount: validation.value.typeValidFalse
    },
    {
      category: 'Category Valid',
      trueCount: validation.value.categoryValidTrue,
      falseCount: validation.value.categoryValidFalse
    },
    {
      category: 'Sentiment Valid',
      trueCount: validation.value.sentimentValidTrue,
      falseCount: validation.value.sentimentValidFalse
    },
    {
      category: 'Sector Valid',
      trueCount: validation.value.sectorValidTrue,
      falseCount: validation.value.sectorValidFalse
    },
    {
      category: 'ESG Classification',
      yesFinancialCount: validation.value.esgYesFinancial,
      yesNonfinancialCount: validation.value.esgYesNonfinancial,
      noCount: validation.value.esgNo
    }
  ]
})
</script>
