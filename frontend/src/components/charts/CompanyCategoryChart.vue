<template>
  <div class="w-full h-[400px]">
    <v-chart :option="chartOptions" autoresize class="w-full h-full" />
  </div>
</template>

<script setup lang="ts">
  import { computed } from 'vue'
  import { use } from 'echarts/core'
  import VChart from 'vue-echarts'
  import { CanvasRenderer } from 'echarts/renderers'
  import { BarChart } from 'echarts/charts'
  import { TitleComponent, TooltipComponent, GridComponent, LegendComponent } from 'echarts/components'
  
  // Register the required components
  use([
    CanvasRenderer,
    BarChart,
    TitleComponent,
    TooltipComponent,
    GridComponent,
    LegendComponent
  ])
  
  // Props
  interface SentimentData {
    category: string;
    negativeCount: number;
    neutralCount: number;
    positiveCount: number;
  }
  
  const props = defineProps<{
    data: SentimentData[];
  }>()
  
  // Build ECharts option dynamically
  const chartOptions = computed(() => {
    const categories = props.data.map(d => d.category)
  
    return {
      tooltip: { trigger: 'axis' },
      legend: { data: ['Negative', 'Neutral', 'Positive'] },
      grid: { left: '5%', right: '4%', bottom: '3%', containLabel: true },
      xAxis: { type: 'value' },
      yAxis: { type: 'category', data: categories },
      series: [
        {
          name: 'Negative',
          type: 'bar',
          color: 'rgb(175,54,60)',
          stack: 'total',
          data: props.data.map(d => d.negativeCount)
        },
        {
          name: 'Neutral',
          type: 'bar',
          color: 'rgb(192,192,192)',
          stack: 'total',
          data: props.data.map(d => d.neutralCount)
        },
        {
          name: 'Positive',
          type: 'bar',
          color: 'rgb(70,147,73)',
          stack: 'total',
          data: props.data.map(d => d.positiveCount)
        }
      ]
    }
  })

</script>