<template>
  <div class="w-full h-[400px]">
    <v-chart ref="chartRef" :option="chartOptions" autoresize class="w-full h-full" />
  </div>
</template>

<script setup lang="ts">
import { computed, ref, onMounted, nextTick } from 'vue'
import { use } from 'echarts/core'
import VChart from 'vue-echarts'
import { BarChart } from 'echarts/charts'
import { CanvasRenderer } from 'echarts/renderers'
import { TitleComponent, TooltipComponent, GridComponent, LegendComponent } from 'echarts/components'

use([
  CanvasRenderer,
  BarChart,
  TitleComponent,
  TooltipComponent,
  GridComponent,
  LegendComponent
])

const props = defineProps<{
  data: {
    category: string
    trueCount?: number
    falseCount?: number
    yesFinancialCount?: number
    yesNonfinancialCount?: number
    noCount?: number
  }[]
}>()

const chartRef = ref<InstanceType<typeof VChart> | null>(null)

onMounted(() => {
  nextTick(() => {
    if (!chartRef.value?.chart) {
      console.warn('⚠️ Chart not initialized')
    }
  })
})

const chartOptions = computed(() => {
  const categories = props.data.map(d => d.category)

  const trueCounts = props.data.map(d => d.trueCount)
  const falseCounts = props.data.map(d => d.falseCount)
  const yesFinancial = props.data.map(d => d.yesFinancialCount)
  const yesNonfinancial = props.data.map(d => d.yesNonfinancialCount)
  const noCounts = props.data.map(d => d.noCount)

  return {
    title: {
      text: 'Validation 309 Statements from 10 Reports',
      left: 'center',
      top: 10,
      textStyle: { fontSize: 18 }
    },
    tooltip: { trigger: 'axis', axisPointer: { type: 'shadow' } },
    legend: { top: 40 },
    grid: { left: '5%', right: '5%', bottom: '10%', containLabel: true },
    xAxis: { type: 'category', data: categories },
    yAxis: { type: 'value' },
    series: [
      {
        name: 'Yes',
        type: 'bar',
        stack: 'validation',
        data: trueCounts,
        itemStyle: { color: '#4b9cdb' },
        label: { show: true, position: 'inside', fontSize: 12 }
      },
      {
        name: 'No',
        type: 'bar',
        stack: 'validation',
        data: falseCounts,
        itemStyle: { color: '#d9534f' },
        label: { show: true, position: 'inside', fontSize: 12 }
      },
      {
        name: 'ESG (with Fin info)',
        type: 'bar',
        stack: 'validation',
        data: yesFinancial,
        itemStyle: { color: '#5cb85c' },
        label: { show: true, position: 'inside', fontSize: 12 }
      },
      {
        name: 'ESG (without Fin info)',
        type: 'bar',
        stack: 'validation',
        data: yesNonfinancial,
        itemStyle: { color: '#f0ad4e' },
        label: { show: true, position: 'inside', fontSize: 12 }
      },
      {
        name: 'Not ESG',
        type: 'bar',
        stack: 'validation',
        data: noCounts,
        itemStyle: { color: '#999999' },
        label: { show: true, position: 'inside', fontSize: 12 }
      }
    ]
  }
})
</script>
