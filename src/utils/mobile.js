/**
 * 简单的移动端检测工具
 */

// 检测是否为移动设备
export function isMobile() {
  return window.innerWidth <= 768
}

// 检测是否为平板设备 
export function isTablet() {
  return window.innerWidth > 768 && window.innerWidth <= 1024
}

// 检测是否为桌面设备
export function isDesktop() {
  return window.innerWidth > 1024
}

// 获取设备类型
export function getDeviceType() {
  if (isMobile()) return 'mobile'
  if (isTablet()) return 'tablet'
  return 'desktop'
}

// 简单的响应式类名
export function getResponsiveClass() {
  const deviceType = getDeviceType()
  return {
    'is-mobile': deviceType === 'mobile',
    'is-tablet': deviceType === 'tablet', 
    'is-desktop': deviceType === 'desktop'
  }
} 