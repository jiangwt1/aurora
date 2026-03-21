// 生成占位符图片工具
export function getPlaceholder(width = 300, height = 200, text = '暂无图片') {
  // 使用SVG data URI生成占位符
  const svg = `
    <svg width="${width}" height="${height}" xmlns="http://www.w3.org/2000/svg">
      <rect width="${width}" height="${height}" fill="#f0f0f0"/>
      <text x="50%" y="50%" font-size="16" fill="#999" text-anchor="middle" dominant-baseline="middle" font-family="Arial">${text}</text>
    </svg>
  `
  return `data:image/svg+xml;charset=utf-8,${encodeURIComponent(svg)}`
}

export function getAvatarPlaceholder(text = 'U', size = 40) {
  // 生成头像占位符
  const colors = ['#40c9c6', '#36a3eb', '#f56c6c', '#e6a23c', '#67c23a', '#909399']
  const colorIndex = text.charCodeAt(0) % colors.length
  const bgColor = colors[colorIndex]

  const svg = `
    <svg width="${size}" height="${size}" xmlns="http://www.w3.org/2000/svg">
      <rect width="${size}" height="${size}" fill="${bgColor}"/>
      <text x="50%" y="50%" font-size="${size * 0.5}" fill="white" text-anchor="middle" dominant-baseline="middle" font-family="Arial">${text.charAt(0).toUpperCase()}</text>
    </svg>
  `
  return `data:image/svg+xml;charset=utf-8,${encodeURIComponent(svg)}`
}
