/**
 * 设备检测工具函数
 */

/**
 * 检测是否为移动设备
 * @returns {boolean}
 */
export function isMobile() {
  const userAgent = navigator.userAgent;
  const mobileRegex = /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i;
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
  return window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth;
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
  return getScreenWidth() <= 768;
}

/**
 * 检测是否为平板屏幕尺寸
 * @returns {boolean}
 */
export function isTabletScreen() {
  const width = getScreenWidth();
  return width > 768 && width <= 1024;
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
      orientation: getOrientation()
    });
  };

  window.addEventListener('resize', handleResize);
  window.addEventListener('orientationchange', handleResize);

  return () => {
    window.removeEventListener('resize', handleResize);
    window.removeEventListener('orientationchange', handleResize);
  };
} 