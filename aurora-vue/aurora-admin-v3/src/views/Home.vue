<template>
  <AppPage :show-footer="true">
    <div class="home-content">
      <!-- 统计卡片 -->
      <n-grid x-gap="16" y-gap="16" :cols="4" class="stats-grid">
        <n-gi>
          <n-card class="stat-card stat-card-green">
            <div class="stat-content">
              <div class="stat-icon">
                <svg viewBox="0 0 640 512" xmlns="http://www.w3.org/2000/svg">
                  <path fill="currentColor" d="M96 224c35.3 0 64-28.7 64-64s-28.7-64-64-64s-64 28.7-64 64s28.7 64 64 64zm448 0c35.3 0 64-28.7 64-64s-28.7-64-64-64s-64 28.7-64 64s28.7 64 64 64zm32 32h-64c-17.6 0-33.5 7.1-45.1 18.6c40.3 22.1 68.9 62 75.1 109.4h66c17.7 0 32-14.3 32-32v-32c0-35.3-28.7-64-64-64zm-256 0c61.9 0 112-50.1 112-112S381.9 32 320 32S208 82.1 208 144s50.1 112 112 112zm76.8 32h-8.3c-20.8 10-43.9 16-68.5 16s-47.6-6-68.5-16h-8.3C179.6 288 128 339.6 128 403.2V432c0 26.5 21.5 48 48 48h288c26.5 0 48-21.5 48-48v-28.8c0-63.6-51.6-115.2-115.2-115.2zm-223.7-13.4C161.5 263.1 145.6 256 128 256H64c-35.3 0-64 28.7-64 64v32c0 17.7 14.3 32 32 32h65.9c6.3-47.4 34.9-87.3 75.2-109.4z" />
                </svg>
              </div>
              <div class="stat-info">
                <div class="stat-label">访问量</div>
                <div class="stat-value">
                  <NumberAnimation :from="0" :to="viewsCount" />
                </div>
              </div>
            </div>
          </n-card>
        </n-gi>

        <n-gi>
          <n-card class="stat-card stat-card-blue">
            <div class="stat-content">
              <div class="stat-icon">
                <svg viewBox="0 0 448 512" xmlns="http://www.w3.org/2000/svg">
                  <path fill="currentColor" d="M383.9 308.3l23.9-62.6c4-10.5-3.7-21.7-15-21.7h-58.5c11-18.9 17.8-40.6 17.8-64v-.3c39.2-7.8 64-19.1 64-31.7c0-13.3-27.3-25.1-70.1-33c-9.2-32.8-27-65.8-40.6-82.8c-9.5-11.9-25.9-15.6-39.5-8.8l-27.6 13.8c-9 4.5-19.6 4.5-28.6 0L182.1 3.4c-13.6-6.8-30-3.1-39.5 8.8c-13.5 17-31.4 50-40.6 82.8c-42.7 7.9-70 19.7-70 33c0 12.6 24.8 23.9 64 31.7v.3c0 23.4 6.8 45.1 17.8 64H56.3c-11.5 0-19.2 11.7-14.7 22.3l25.8 60.2C27.3 329.8 0 372.7 0 422.4v44.8C0 491.9 20.1 512 44.8 512h358.4c24.7 0 44.8-20.1 44.8-44.8v-44.8c0-48.4-25.8-90.4-64.1-114.1z" />
                </svg>
              </div>
              <div class="stat-info">
                <div class="stat-label">用户量</div>
                <div class="stat-value">
                  <NumberAnimation :from="0" :to="userCount" />
                </div>
              </div>
            </div>
          </n-card>
        </n-gi>

        <n-gi>
          <n-card class="stat-card stat-card-pink">
            <div class="stat-content">
              <div class="stat-icon">
                <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg">
                  <path fill="currentColor" d="M19 3H5c-1.1 0-2 .9-2 2v14c0 1.1.9 2 2 2h14c1.1 0 2-.9 2-2V5c0-1.1-.9-2-2-2zm-5 14H7v-2h7v2zm3-4H7v-2h10v2zm0-4H7V7h10v2z" />
                </svg>
              </div>
              <div class="stat-info">
                <div class="stat-label">文章量</div>
                <div class="stat-value">
                  <NumberAnimation :from="0" :to="articleCount" />
                </div>
              </div>
            </div>
          </n-card>
        </n-gi>

        <n-gi>
          <n-card class="stat-card stat-card-purple">
            <div class="stat-content">
              <div class="stat-icon">
                <svg viewBox="0 0 1024 1024" xmlns="http://www.w3.org/2000/svg">
                  <path fill="currentColor" d="M924.3 338.4a447.57 447.57 0 0 0-96.1-143.3a443.09 443.09 0 0 0-143-96.3A443.91 443.91 0 0 0 512 64h-2c-60.5.3-119 12.3-174.1 35.9a444.08 444.08 0 0 0-141.7 96.5a445 445 0 0 0-95 142.8A449.89 449.89 0 0 0 65 514.1c.3 69.4 16.9 138.3 47.9 199.9v152c0 25.4 20.6 46 45.9 46h151.8a447.72 447.72 0 0 0 199.5 48h2.1c59.8 0 117.7-11.6 172.3-34.3A443.2 443.2 0 0 0 827 830.5c41.2-40.9 73.6-88.7 96.3-142c23.5-55.2 35.5-113.9 35.8-174.5c.2-60.9-11.6-120-34.8-175.6z" />
                </svg>
              </div>
              <div class="stat-info">
                <div class="stat-label">留言量</div>
                <div class="stat-value">
                  <NumberAnimation :from="0" :to="messageCount" />
                </div>
              </div>
            </div>
          </n-card>
        </n-gi>
      </n-grid>

      <!-- 图表区域 -->
      <n-grid x-gap="16" y-gap="16" :cols="2" class="charts-grid">
        <!-- 一周访问量 -->
        <n-gi :span="2">
          <n-card class="chart-card" title="访问趋势">
            <template #header-extra>
              <n-tag type="success" size="small">近7天</n-tag>
            </template>
            <div id="visits_per_week" class="chart-container-350"></div>
          </n-card>
        </n-gi>

        <!-- 热门文章 -->
        <n-gi>
          <n-card class="chart-card hot-article-card" title="热门文章">
            <template #header-extra>
              <n-tag type="warning" size="small" round>
                <template #icon>
                  <n-icon><svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path fill="currentColor" d="M17 3H7c-1.1 0-2 .9-2 2v16l7-3 7 3V5c0-1.1-.9-2-2-2z"/></svg></n-icon>
                </template>
                TOP 5
              </n-tag>
            </template>
            <div class="hot-articles-list">
              <div
                v-for="(item, index) in topArticles"
                :key="item.id"
                class="hot-article-item"
                :class="`rank-${index + 1}`"
              >
                <div class="article-rank-badge">
                  <span v-if="index < 3" class="rank-icon">
                    <svg v-if="index === 0" viewBox="0 0 24 24"><path fill="currentColor" d="M12 2l3.09 6.26L22 9.27l-5 4.87 1.18 6.88L12 17.77l-6.18 3.25L7 14.14 2 9.27l6.91-1.01L12 2z"/></svg>
                    <svg v-else-if="index === 1" viewBox="0 0 24 24"><path fill="currentColor" d="M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z"/></svg>
                    <svg v-else viewBox="0 0 24 24"><path fill="currentColor" d="M12 15.4l-3.76 2.27 1-4.28-3.32-2.88 4.38-.38L12 6.1l1.71 4.04 4.38.38-3.32 2.88 1 4.28z"/></svg>
                  </span>
                  <span v-else class="rank-number">{{ index + 1 }}</span>
                </div>
                <div class="article-content">
                  <div class="article-title">{{ item.articleTitle }}</div>
                  <div class="article-stats">
                    <div class="stat-item">
                      <n-icon size="14" color="#18A058">
                        <svg viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg"><path fill="currentColor" d="M12 4.5C7 4.5 2.73 7.61 1 12c1.73 4.39 6 7.5 11 7.5s9.27-3.11 11-7.5c-1.73-4.39-6-7.5-11-7.5zM12 17c-2.76 0-5-2.24-5-5s2.24-5 5-5 5 2.24 5 5-2.24 5-5 5zm0-8c-1.66 0-3 1.34-3 3s1.34 3 3 3 3-1.34 3-3-1.34-3-3-3z"/></svg>
                      </n-icon>
                      <span>{{ formatNumber(item.viewsCount) }}</span>
                    </div>
                    <div class="article-progress" :style="{ width: getProgressWidth(item.viewsCount) }"></div>
                  </div>
                </div>
              </div>
            </div>
          </n-card>
        </n-gi>

        <!-- 文章分类统计 -->
        <n-gi>
          <n-card class="chart-card" title="文章分类">
            <template #header-extra>
              <n-tag type="info" size="small">{{ categoryCount }} 个分类</n-tag>
            </template>
            <div id="article_category" class="chart-container-350"></div>
          </n-card>
        </n-gi>

        <!-- 用户地域分布 -->
        <n-gi>
          <n-card class="chart-card" title="用户地域 Top 10">
            <template #header-extra>
              <n-radio-group v-model:value="userAreaType" size="small">
                <n-radio-button :value="1">用户</n-radio-button>
                <n-radio-button :value="2">游客</n-radio-button>
              </n-radio-group>
            </template>
            <div id="user-area" class="chart-container-350"></div>
          </n-card>
        </n-gi>

        <!-- 文章标签 -->
        <n-gi>
          <n-card class="chart-card tag-card" title="文章标签">
            <template #header-extra>
              <n-tag type="primary" size="small">{{ tagList.length }} 个标签</n-tag>
            </template>
            <div id="article-tags" class="tag-cloud-container">
              <TagCloud v-if="tagList.length > 0" :data="tagList" />
            </div>
          </n-card>
        </n-gi>
      </n-grid>
    </div>
  </AppPage>
</template>

<script setup>
import { ref, onMounted, watch, onBeforeUnmount } from 'vue'
import { useUserStore } from '@/store/modules/user'
import { getHomeCardData, getUserArea } from './api'
import AppPage from '@/components/common/AppPage.vue'
import NumberAnimation from '@/components/common/NumberAnimation.vue'
import TagCloud from '@/components/common/TagCloud.vue'
import { Line, Column, Pie, Bar } from '@antv/g2plot'

const userStore = useUserStore()

// 数据
const viewsCount = ref(0)
const userCount = ref(0)
const articleCount = ref(0)
const messageCount = ref(0)
const tagList = ref([])
const userAreaType = ref(1)
const categoryCount = ref(0)
const topArticles = ref([]) // 热门文章列表
let userAreaChart = null // 用户地域图表实例

// 获取首页数据
const fetchHomeData = async () => {
  try {
    const res = await getHomeCardData()
    const data = res.data

    viewsCount.value = data.viewsCount || 0
    userCount.value = data.userCount || 0
    articleCount.value = data.articleCount || 0
    messageCount.value = data.messageCount || 0

    // 一周访问量
    if (data.uniqueViewDTOs && data.uniqueViewDTOs.length > 0) {
      initVisitWeek(data.uniqueViewDTOs)
    }

    // 文章浏览量排行
    if (data.articleRankDTOs && data.articleRankDTOs.length > 0) {
      initArticleVisits(data.articleRankDTOs)
    }

    // 文章分类统计
    if (data.categoryDTOs && data.categoryDTOs.length > 0) {
      categoryCount.value = data.categoryDTOs.length
      initArticleCategory(data.categoryDTOs)
    }

    // 文章标签
    if (data.tagDTOs && data.tagDTOs.length > 0) {
      tagList.value = data.tagDTOs.map(tag => ({
        name: tag.tagName,
        id: tag.id
      }))
    }
  } catch (error) {
    console.error('获取首页数据失败:', error)
  }
}

// 获取用户地域分布
const fetchUserArea = async () => {
  try {
    const res = await getUserArea({ type: userAreaType.value })

    if (res && res.data && res.data.length > 0) {
      initUserArea(res.data)
    }
  } catch (error) {
    console.error('获取用户地域分布失败:', error)
  }
}

// 初始化一周访问量图表
const initVisitWeek = (data) => {
  const container = document.getElementById('visits_per_week')
  if (!container) return

  const line = new Line(container, {
    data,
    xField: 'day',
    yField: 'viewsCount',
    smooth: true,
    color: ['#18A058'],
    area: {
      style: {
        fill: 'l(270) 0:#ffffff 0.5:#18A058 1:#18A058',
        fillOpacity: 0.2
      }
    },
    lineStyle: {
      lineWidth: 3
    },
    point: {
      size: 4,
      style: {
        fill: '#fff',
        stroke: '#18A058',
        lineWidth: 2
      }
    },
    xAxis: {
      label: {
        style: {
          fill: '#666'
        }
      },
      line: {
        style: {
          stroke: '#e8e8e8'
        }
      }
    },
    yAxis: {
      label: {
        style: {
          fill: '#666'
        }
      },
      grid: {
        line: {
          style: {
            stroke: '#e8e8e8',
            lineDash: [4, 4]
          }
        }
      }
    }
  })
  line.render()
}

// 初始化文章浏览量排行
const initArticleVisits = (data) => {
  // 只取前5条
  topArticles.value = data.slice(0, 5).map(item => ({
    ...item,
    id: item.id || Math.random()
  }))
}

// 初始化文章分类统计
const initArticleCategory = (data) => {
  const container = document.getElementById('article_category')
  if (!container) return

  const pie = new Pie(container, {
    data,
    angleField: 'articleCount',
    colorField: 'categoryName',
    radius: 0.85,
    innerRadius: 0.5,
    color: ['#18A058', '#2080F0', '#F0A020', '#D03050', '#722ED1', '#13C2C2'],
    label: {
      type: 'inner',
      offset: '-50%',
      content: '{value}',
      style: {
        textAlign: 'center',
        fontSize: 14,
        fill: '#fff'
      }
    },
    statistic: {
      title: {
        offsetY: -8,
        content: '文章总数',
        style: {
          fontSize: '14px',
          color: '#666'
        }
      },
      content: {
        offsetY: 4,
        style: {
          fontSize: '20px',
          color: '#333',
          fontWeight: 'bold'
        }
      }
    },
    legend: {
      position: 'bottom',
      layout: {
        justifyContent: 'center',
        alignItems: 'center'
      }
    }
  })
  pie.render()
}

// 初始化用户地域分布 - 热力柱状图
const initUserArea = (data) => {
  const container = document.getElementById('user-area')
  if (!container) return

  // 销毁旧实例
  if (userAreaChart) {
    userAreaChart.destroy()
  }

  // 转换数据格式，取 Top 10
  const chartData = data
    .map(item => {
      const name = item.name || item.provinceName || item.areaName || item.regionName || '未知'
      const value = item.value || item.count || item.userCount || 0
      return { name, value }
    })
    .sort((a, b) => b.value - a.value)
    .slice(0, 10)

  if (chartData.length === 0) {
    console.warn('没有有效的地域数据')
    return
  }

  // 计算最大值用于颜色映射
  const maxValue = Math.max(...chartData.map(item => item.value))

  userAreaChart = new Bar(container, {
    data: chartData,
    xField: 'value',
    yField: 'name',
    seriesField: 'value',
    color: ({ value }) => {
      // 根据值的大小返回不同深度的蓝色
      const ratio = value / maxValue
      if (ratio > 0.8) return '#0050b3'
      if (ratio > 0.6) return '#1890ff'
      if (ratio > 0.4) return '#40a9ff'
      if (ratio > 0.2) return '#69c0ff'
      return '#91d5ff'
    },
    legend: false,
    barStyle: {
      radius: [0, 4, 4, 0]
    },
    tooltip: {
      formatter: (datum) => {
        return { name: datum.name, value: datum.value + ' 人' }
      }
    },
    label: {
      position: 'right',
      style: {
        fill: '#666',
        fontSize: 12
      },
      formatter: ({ value }) => value
    },
    xAxis: {
      label: {
        style: {
          fill: '#999'
        }
      },
      grid: {
        line: {
          style: {
            stroke: '#f0f0f0'
          }
        }
      }
    },
    yAxis: {
      label: {
        style: {
          fill: '#666'
        }
      }
    },
    responsive: true,
    appendPadding: [10, 20, 0, 0]
  })

  userAreaChart.render()
}

// 格式化数字显示
const formatNumber = (num) => {
  if (num >= 10000) {
    return (num / 10000).toFixed(1) + 'w'
  } else if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'k'
  }
  return num.toString()
}

// 计算进度条宽度（基于最大浏览量）
const getProgressWidth = (views) => {
  if (!topArticles.value || topArticles.value.length === 0) return '0%'
  const maxViews = Math.max(...topArticles.value.map(item => item.viewsCount || 0))
  if (maxViews === 0) return '0%'
  return Math.max((views / maxViews) * 100, 15) + '%'
}

// 监听用户类型变化
watch(userAreaType, () => {
  fetchUserArea()
})

onMounted(() => {
  fetchHomeData()
  fetchUserArea()

  // 监听窗口大小变化
  window.addEventListener('resize', handleResize)
})

// 组件卸载时清理
onBeforeUnmount(() => {
  window.removeEventListener('resize', handleResize)
  if (userAreaChart) {
    userAreaChart.destroy()
  }
})

// 处理窗口大小变化
const handleResize = () => {
  if (userAreaChart) {
    userAreaChart.changeSize()
  }
}
</script>

<style scoped>
.home-content {
  padding: 0;
}

.stats-grid {
  margin-bottom: 16px;
}

.charts-grid {
  margin-top: 16px;
}

/* 统计卡片样式 */
.stat-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
  overflow: hidden;
}

.stat-card:hover {
  transform: translateY(-4px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
}

.stat-card :deep(.n-card__content) {
  padding: 20px;
}

.stat-content {
  display: flex;
  align-items: center;
  gap: 16px;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
}

.stat-icon svg {
  width: 28px;
  height: 28px;
  fill: white;
}

.stat-card-green .stat-icon {
  background: linear-gradient(135deg, #52C41A 0%, #73D13D 100%);
}

.stat-card-blue .stat-icon {
  background: linear-gradient(135deg, #1890FF 0%, #40A9FF 100%);
}

.stat-card-pink .stat-icon {
  background: linear-gradient(135deg, #EB2F96 0%, #F759AB 100%);
}

.stat-card-purple .stat-icon {
  background: linear-gradient(135deg, #722ED1 0%, #9254DE 100%);
}

.stat-info {
  flex: 1;
  min-width: 0;
}

.stat-label {
  font-size: 13px;
  color: #666;
  margin-bottom: 8px;
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #333;
  margin-bottom: 6px;
  line-height: 1;
}

.stat-trend {
  font-size: 12px;
  color: #52C41A;
  font-weight: 500;
}

.trend-text {
  color: #999;
  font-weight: 400;
  margin-left: 4px;
}

/* 图表卡片样式 */
.chart-card {
  border-radius: 12px;
  border: none;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: all 0.3s ease;
}

.chart-card:hover {
  box-shadow: 0 4px 16px rgba(0, 0, 0, 0.1);
}

.chart-card :deep(.n-card__header) {
  padding: 16px 20px;
  border-bottom: 1px solid #f0f0f0;
}

.chart-card :deep(.n-card__content) {
  padding: 20px;
}

.chart-container {
  height: 280px;
  width: 100%;
}

.chart-container-350 {
  height: 350px;
  width: 100%;
}

#user-area {
  min-height: 350px !important;
  width: 100% !important;
  height: 350px !important;
}

/* 确保地图容器有正确的样式 */
#user-area canvas {
  width: 100% !important;
  height: 100% !important;
}

.tag-cloud-container {
  height: 350px;
  width: 100%;
  display: flex;
  align-items: center;
  justify-content: center;
}

#article-tags {
  height: 350px !important;
}

/* 热门文章卡片特殊样式 */
.hot-article-card {
  background: linear-gradient(135deg, #ffffff 0%, #fafbfc 100%);
}

/* 热门文章列表样式 */
.hot-articles-list {
  display: flex;
  flex-direction: column;
  gap: 10px;
  height: 350px;
  overflow-y: auto;
  padding-right: 6px;
}

/* 自定义滚动条 */
.hot-articles-list::-webkit-scrollbar {
  width: 4px;
}

.hot-articles-list::-webkit-scrollbar-track {
  background: #f1f1f1;
  border-radius: 4px;
}

.hot-articles-list::-webkit-scrollbar-thumb {
  background: #c1c1c1;
  border-radius: 4px;
}

.hot-articles-list::-webkit-scrollbar-thumb:hover {
  background: #a8a8a8;
}

.hot-article-item {
  display: flex;
  align-items: center;
  gap: 14px;
  padding: 14px 16px;
  background: white;
  border-radius: 12px;
  border: 1px solid #f0f0f0;
  transition: all 0.3s cubic-bezier(0.4, 0, 0.2, 1);
  position: relative;
  overflow: hidden;
}

.hot-article-item::before {
  content: '';
  position: absolute;
  left: 0;
  top: 0;
  bottom: 0;
  width: 3px;
  background: #e0e0e0;
  transition: all 0.3s ease;
}

.hot-article-item:hover {
  border-color: #1890ff;
  box-shadow: 0 4px 12px rgba(24, 144, 255, 0.1);
  transform: translateY(-2px);
}

.hot-article-item:hover::before {
  background: #1890ff;
}

/* 不同排名的左侧边框颜色 */
.hot-article-item.rank-1::before {
  background: linear-gradient(180deg, #FFD700 0%, #FFA500 100%);
}

.hot-article-item.rank-2::before {
  background: linear-gradient(180deg, #C0C0C0 0%, #A8A8A8 100%);
}

.hot-article-item.rank-3::before {
  background: linear-gradient(180deg, #CD7F32 0%, #B87332 100%);
}

/* 排名徽章 */
.article-rank-badge {
  width: 36px;
  height: 36px;
  display: flex;
  align-items: center;
  justify-content: center;
  flex-shrink: 0;
  position: relative;
}

.rank-icon {
  width: 28px;
  height: 28px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.rank-icon svg {
  width: 100%;
  height: 100%;
}

.rank-1 .rank-icon {
  color: #FFD700;
  filter: drop-shadow(0 2px 4px rgba(255, 215, 0, 0.3));
}

.rank-2 .rank-icon {
  color: #C0C0C0;
  filter: drop-shadow(0 2px 4px rgba(192, 192, 192, 0.3));
}

.rank-3 .rank-icon {
  color: #CD7F32;
  filter: drop-shadow(0 2px 4px rgba(205, 127, 50, 0.3));
}

.rank-number {
  width: 28px;
  height: 28px;
  border-radius: 8px;
  background: #f5f5f5;
  color: #999;
  font-size: 14px;
  font-weight: 700;
  display: flex;
  align-items: center;
  justify-content: center;
}

/* 文章内容 */
.article-content {
  flex: 1;
  min-width: 0;
  display: flex;
  flex-direction: column;
  gap: 8px;
}

.article-title {
  font-size: 14px;
  font-weight: 500;
  color: #333;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
  line-height: 1.4;
}

.hot-article-item.rank-1 .article-title {
  font-weight: 600;
  color: #1890ff;
}

.article-stats {
  display: flex;
  align-items: center;
  gap: 10px;
  position: relative;
}

.stat-item {
  display: flex;
  align-items: center;
  gap: 4px;
  font-size: 12px;
  color: #666;
  font-weight: 500;
  z-index: 1;
  background: white;
  padding: 2px 6px;
  border-radius: 4px;
}

.article-progress {
  position: absolute;
  left: 0;
  right: 0;
  height: 4px;
  background: linear-gradient(90deg, #1890ff 0%, #40A9FF 100%);
  border-radius: 2px;
  transition: width 0.6s cubic-bezier(0.4, 0, 0.2, 1);
  opacity: 0.15;
  top: 50%;
  transform: translateY(-50%);
}

.hot-article-item.rank-1 .article-progress {
  background: linear-gradient(90deg, #FFD700 0%, #FFA500 100%);
  opacity: 0.25;
}

.hot-article-item.rank-2 .article-progress {
  background: linear-gradient(90deg, #C0C0C0 0%, #A8A8A8 100%);
  opacity: 0.2;
}

.hot-article-item.rank-3 .article-progress {
  background: linear-gradient(90deg, #CD7F32 0%, #B87332 100%);
  opacity: 0.2;
}

/* 响应式 */
@media (max-width: 1400px) {
  .stats-grid {
    grid-template-columns: repeat(2, 1fr) !important;
  }
}

@media (max-width: 768px) {
  .stats-grid {
    grid-template-columns: 1fr !important;
  }

  .charts-grid {
    grid-template-columns: 1fr !important;
  }

  .chart-container {
    height: 240px;
  }

  .chart-container-350 {
    height: 280px;
  }

  .hot-articles-list {
    height: 280px;
  }

  .tag-cloud-container {
    height: 280px;
  }

  #user-area {
    height: 280px !important;
  }

  #article-tags {
    height: 280px !important;
  }

  .stat-value {
    font-size: 24px;
  }
}
</style>