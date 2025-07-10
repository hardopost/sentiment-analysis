<template>
  <div class="w-full h-[400px]">
    <v-chart ref="chartRef" :option="chartOptions" autoresize class="w-full h-full" />
  </div>
</template>

<script setup lang="ts">
  import { computed, onMounted, nextTick, ref } from 'vue'
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

  const customOrder = [
  'Revenue Growth Expectations',
  'Profitability & Margin Trends',
  'Operational Expansion & Investments',
  'Product & Service Innovation',
  'Market Share & Competitive Position',
  'Company-Specific Product Demand & Consumer Trends',
  'Financial Stability & Liquidity',
  'Company-Specific Risks & Challenges'
  ]
  
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

  const sortedData = computed(() => {
    return [...props.data].sort((a, b) => {
      return customOrder.indexOf(b.category) - customOrder.indexOf(a.category)
    })
  })

  const chartRef = ref<InstanceType<typeof VChart> | null>(null)

  const emit = defineEmits<{
    (e: 'bar-click', payload: { category: string; sentiment: string }): void
  }>()

  const handleClick = (params: any) => {
    console.log('CLICKED BAR:', params)
    emit('bar-click', {
      category: params.name,
      sentiment: params.seriesName.toLowerCase()
    })
  }

  onMounted(() => {
  nextTick(() => {
    const chart = chartRef.value?.chart
    if (chart) {
      chart.on('click', handleClick)
      console.log('✅ Chart click listener attached')
    } else {
      console.warn('⚠️ chartRef is not defined or chart instance not available')
    }
  })
})
  
  // Build ECharts option dynamically
  const chartOptions = computed(() => {
    const categories = sortedData.value.map(d => d.category)
  
    return {
      tooltip: { trigger: 'axis' },
      legend: { data: ['Negative', 'Neutral', 'Positive'] },
      grid: { left: '5%', right: '7%', bottom: '3%', containLabel: true },
      xAxis: { type: 'value', axisLabel: {fontSize: 14 } },
      yAxis: { type: 'category', data: categories, axisLabel: {
    fontSize: 14, // ← Increase this to make category labels bigger
    color: '#333'
  } },
      series: [
        {
          name: 'Negative',
          type: 'bar',
          color: 'rgb(175,54,60)',
          stack: 'total',
          data: sortedData.value.map(d => d.negativeCount)
        },
        {
          name: 'Neutral',
          type: 'bar',
          color: 'rgb(192,192,192)',
          stack: 'total',
          data: sortedData.value.map(d => d.neutralCount)
        },
        {
          name: 'Positive',
          type: 'bar',
          color: 'rgb(70,147,73)',
          stack: 'total',
          data: sortedData.value.map(d => d.positiveCount),
          label: {
            show: true,
            position: 'right',
            formatter: (params: any) => {
              const total = sortedData.value[params.dataIndex].positiveCount +
                    sortedData.value[params.dataIndex].neutralCount +
                    sortedData.value[params.dataIndex].negativeCount;
              return total.toString()
            },
          fontSize: 14,
          color: '#000'
  }
        }
      ]
    }
  })

</script>