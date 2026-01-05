/**
 * 论坛相关工具函数
 */
import { getCategories } from '@/api/forum'

/**
 * 获取并格式化分类列表
 * @param {Object} options - 配置选项
 * @param {boolean} options.includeAll - 是否包含"全部"选项
 * @param {boolean} options.filterDisabled - 是否过滤已禁用的分类
 * @returns {Promise<Array>} 格式化后的分类列表
 */
export async function fetchAndFormatCategories(options = {}) {
  const { includeAll = true, filterDisabled = true } = options

  try {
    const response = await getCategories()
    if (response.code === 200) {
      let fetched = Array.isArray(response.data) ? response.data : []

      // 过滤已禁用的分类
      if (filterDisabled) {
        fetched = fetched.filter(item => item.status !== '1')
      }

      // 格式化数据
      const formatted = fetched.map(item => ({
        id: String(item.categoryId || item.id),
        name: item.categoryName || item.name,
        description: item.description || '',
        status: item.status
      }))

      // 构建分类映射
      const categoryMap = formatted.reduce((acc, cur) => {
        acc[cur.id] = cur.name
        return acc
      }, {})

      // 是否包含"全部"选项
      const result = includeAll
        ? [{ id: 'all', name: '全部' }, ...formatted]
        : formatted

      return {
        categories: result,
        categoryMap
      }
    }
    return {
      categories: includeAll ? [{ id: 'all', name: '全部' }] : [],
      categoryMap: {}
    }
  } catch (error) {
    // 404错误可能是后端未启动，静默处理
    if (error.response && error.response.status === 404) {
      console.warn('后端服务可能未启动，分类列表加载失败')
    } else {
      console.error('获取分类列表失败:', error)
    }
    return {
      categories: includeAll ? [{ id: 'all', name: '全部' }] : [],
      categoryMap: {}
    }
  }
}

