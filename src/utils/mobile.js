/**
 * 简单的移动端检测工具
 */

import { BREAKPOINTS, getScreenBreakpoint, getDeviceType as getDeviceTypeFromUtils } from './device'

// 检测是否为移动设备
export function isMobile() {
  return window.innerWidth <= BREAKPOINTS.MOBILE
}

// 检测是否为平板设备 
export function isTablet() {
  return window.innerWidth > BREAKPOINTS.MOBILE && window.innerWidth <= BREAKPOINTS.TABLET
}

// 检测是否为桌面设备
export function isDesktop() {
  return window.innerWidth > BREAKPOINTS.TABLET
}

export function isSmallMobile() {
  return window.innerWidth <= BREAKPOINTS.SMALL_MOBILE
}

export function isMiniMobile() {
  return window.innerWidth <= BREAKPOINTS.MINI_MOBILE
}

export function getDeviceType() {
  return getDeviceTypeFromUtils()
}

export function getResponsiveClass() {
  const breakpoint = getScreenBreakpoint()
  return {
    'is-mini-mobile': breakpoint === 'mini-mobile',
    'is-small-mobile': breakpoint === 'small-mobile',
    'is-large-mobile': breakpoint === 'large-mobile',
    'is-mobile': breakpoint === 'mobile',
    'is-tablet': breakpoint === 'tablet', 
    'is-desktop': breakpoint === 'desktop'
  }
}