<template>
    <nav class="bg-white border-b border-gray-400 py-4 sticky top-0 z-50 shadow-sm">
      <div class="max-w-screen-xl mx-auto px-4 flex items-center justify-between">
        <div class="text-2xl font-bold text-gray-800">
          Sentiment Analysis of OMX Nasdaq Stockholm and Baltic companies
        </div>
        <div class="flex gap-6 text-lg">
          <RouterLink
            to="/"
            class="text-gray-700 hover:text-blue-600 font-medium"
            active-class="text-blue-600 font-semibold"
          >
            Overview
          </RouterLink>
          <RouterLink
            to="/sectors"
            class="text-gray-700 hover:text-blue-600 font-medium"
            active-class="text-blue-600 font-semibold"
          >
            Sectors
          </RouterLink>
          <RouterLink
            to="/companies"
            class="text-gray-700 hover:text-blue-600 font-medium"
            active-class="text-blue-600 font-semibold"
          >
            Companies
          </RouterLink>
          <RouterLink
            to="/info"
            class="text-gray-700 hover:text-blue-600 font-medium"
            active-class="text-blue-600 font-semibold"
          >
            Info
          </RouterLink>
          
        </div>
      </div>
    </nav>
  </template>
