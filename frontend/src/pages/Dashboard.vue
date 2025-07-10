<template>
  <div class="max-w-screen-2xl mx-auto px-4 py-6">
    <h1 class="text-2xl font-bold mb-6 text-center">Aggregated Overview</h1>

    <div v-if="loading">Loading data...</div>
    <div v-else-if="error">Error loading data: {{ error }}</div>

    <div v-else-if="sentiments" class="space-y-10">
      <!-- Company Charts Row -->
      <div class="flex flex-col lg:flex-row gap-6 justify-center">
        <div class="flex-1 border border-gray-200 rounded-xl shadow p-4">
          <h2 class="text-center font-semibold mb-2">Companies - Stockholm</h2>
          <CompanyCategoryChart :data="sentiments.companies.stockholm" />
        </div>
        <div class="flex-1 border border-gray-200 rounded-xl shadow p-4">
          <h2 class="text-center font-semibold mb-2">Companies - Baltic</h2>
          <CompanyCategoryChart :data="sentiments.companies.baltic" />
        </div>
      </div>

      <!-- Sector Charts Row -->
      <div class="flex flex-col lg:flex-row gap-6 justify-center">
        <div class="flex-1 border border-gray-200 rounded-xl shadow p-4">
          <h2 class="text-center font-semibold mb-2">Sectors - Stockholm</h2>
          <SectorCategoryChart :data="sentiments.sectors.stockholm" />
        </div>
        <div class="flex-1 border border-gray-200 rounded-xl shadow p-4">
          <h2 class="text-center font-semibold mb-2">Sectors - Baltic</h2>
          <SectorCategoryChart :data="sentiments.sectors.baltic" />
        </div>
      </div>
    </div>
  </div>
</template>
  
  <script lang="ts" setup>
  import { ref, onMounted } from 'vue';
  import api from '../services/api';
  import CompanyCategoryChart from '../components/charts/CompanyCategoryChart.vue'
  import SectorCategoryChart from '../components/charts/SectorCategoryChart.vue'
  
  // Define the TypeScript interface for your API data
  interface SentimentData {
    category: string; // example: 'Revenue Growth Expectations'
    positiveCount: number;
    neutralCount: number;
    negativeCount: number;
  }

type MarketKey = 'stockholm' | 'baltic';

interface SentimentSummary {
  companies: Record<MarketKey, SentimentData[]>;
  sectors: Record<MarketKey, SentimentData[]>;
}
  
  // Reactive variables 
  // ref() creates a reactive variable. If you change the value inside ref, Vue will automatically react and update the UI. 
  // In TypeScript, ref<Type>() automatically knows the type it holds.
  const sentiments = ref<SentimentSummary | null>(null); // One object, not an array; 
  const loading = ref(true); // loading.value will start as true
  const error = ref<string | null>(null);
  
  // Fetch data on component mount
  // In Vue 3 Composition API, onMounted() is the lifecycle hook that runs after the component is added to the DOM (loaded on screen). It's where you usually fetch data, start animations, set timers, etc.
  onMounted(async () => {
    try {
      const response = await api.get<SentimentSummary>('/api/sentiments'); 
      // Update the ref with typed data
      sentiments.value = response.data;
    } catch (err: any) {
      error.value = err.message || 'Unknown error';
    } finally {
      loading.value = false;
    }
  });
  </script>