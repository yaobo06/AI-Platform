/**
 * 设备检测工具函数
 */

export const BREAKPOINTS = Object.freeze({
  MINI_MOBILE: 320,
  SMALL_MOBILE: 375,
  LARGE_MOBILE: 414,
  MOBILE: 768,
  TABLET: 1024
});

/**
 * 检测是否为移动设备
 * @returns {boolean}
 */
export function isMobile() {
  if (navigator.userAgentData && typeof navigator.userAgentData.mobile === 'boolean') {
    return navigator.userAgentData.mobile;
  }

  const userAgent = navigator.userAgent || navigator.vendor || window.opera;
  const mobileRegex = /Android|webOS|iPhone|iPod|BlackBerry|IEMobile|Opera Mini|Mobile Safari/i;
  return mobileRegex.test(userAgent);
}

/**
 * 检测是否为平板设备
 * @returns {boolean}
 */
export function isTablet() {
  const userAgent = navigator.userAgent;
  const tabletRegex = /iPad|Android(?!.*Mobile)/i;
  return tabletRegex.test(userAgent);
}

/**
 * 检测是否为桌面设备
 * @returns {boolean}
 */
export function isDesktop() {
  return !isMobile() && !isTablet();
}

/**
 * 获取屏幕宽度
 * @returns {number}
 */
export function getScreenWidth() {
  return Math.round(window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth);
}

/**
 * 获取屏幕高度
 * @returns {number}
 */
export function getScreenHeight() {
  return window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight;
}

/**
 * 检测是否为移动端屏幕尺寸
 * @returns {boolean}
 */
export function isMobileScreen() {
  if (window.matchMedia) {
    return window.matchMedia(`(max-width: ${BREAKPOINTS.MOBILE}px)`).matches;
  }
  return getScreenWidth() <= BREAKPOINTS.MOBILE;
}

/**
 * 检测是否为平板屏幕尺寸
 * @returns {boolean}
 */
export function isTabletScreen() {
  const width = getScreenWidth();
  return width > BREAKPOINTS.MOBILE && width <= BREAKPOINTS.TABLET;
}

/**
 * 获取设备类型
 * @returns {string} 'mobile' | 'tablet' | 'desktop'
 */
export function getDeviceType() {
  if (isMobile() || isMobileScreen()) {
    return 'mobile';
  } else if (isTablet() || isTabletScreen()) {
    return 'tablet';
  } else {
    return 'desktop';
  }
}

/**
 * 获取当前屏幕断点
 * @returns {string}
 */
export function getScreenBreakpoint() {
  const width = getScreenWidth();

  if (width <= BREAKPOINTS.MINI_MOBILE) {
    return 'mini-mobile';
  }

  if (width <= BREAKPOINTS.SMALL_MOBILE) {
    return 'small-mobile';
  }

  if (width <= BREAKPOINTS.LARGE_MOBILE) {
    return 'large-mobile';
  }

  if (width <= BREAKPOINTS.MOBILE) {
    return 'mobile';
  }

  if (width <= BREAKPOINTS.TABLET) {
    return 'tablet';
  }

  return 'desktop';
}

/**
 * 检测是否支持触摸
 * @returns {boolean}
 */
export function isTouchDevice() {
  return 'ontouchstart' in window || navigator.maxTouchPoints > 0 || navigator.msMaxTouchPoints > 0;
}

/**
 * 检测屏幕方向
 * @returns {string} 'portrait' | 'landscape'
 */
export function getOrientation() {
  const width = getScreenWidth();
  const height = getScreenHeight();
  return width > height ? 'landscape' : 'portrait';
}

/**
 * 添加屏幕尺寸变化监听器
 * @param {Function} callback 回调函数
 * @returns {Function} 移除监听器的函数
 */
export function addResizeListener(callback) {
  const handleResize = () => {
    callback({
      width: getScreenWidth(),
      height: getScreenHeight(),
      deviceType: getDeviceType(),
      orientation: getOrientation(),
      breakpoint: getScreenBreakpoint()
    });
  };

  handleResize();

  const resizeObserver = window.matchMedia ? window.matchMedia('(orientation: portrait)') : null;

  window.addEventListener('resize', handleResize);
  window.addEventListener('orientationchange', handleResize);
  if (resizeObserver && typeof resizeObserver.addEventListener === 'function') {
    resizeObserver.addEventListener('change', handleResize);
  }

  return () => {
    window.removeEventListener('resize', handleResize);
    window.removeEventListener('orientationchange', handleResize);
    if (resizeObserver && typeof resizeObserver.removeEventListener === 'function') {
      resizeObserver.removeEventListener('change', handleResize);
    }
  };
} 