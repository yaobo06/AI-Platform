// 模拟数据存储
let posts = [
  {
    postId: 1,
    categoryId: 1,
    userId: 1,
    title: '分享：如何使用ChatGPT提高编程效率',
    content: '本文将分享一些使用ChatGPT辅助编程的实用技巧。\n\n1. 代码优化：可以让ChatGPT帮助优化代码结构和性能。\n2. 问题排查：遇到bug时，可以将错误信息和相关代码提供给ChatGPT进行分析。\n3. 文档生成：可以让ChatGPT帮助生成代码注释和文档。\n\n希望这些技巧对大家有帮助！',
    contentType: '1',
    viewCount: 1200,
    replyCount: 2,
    likeCount: 88,
    isTop: '0',
    isEssence: '0',
    status: '0',
    lastReplyTime: '2024-03-20 11:30:00',
    lastReplyUserId: 1,
    createBy: '张三',
    createTime: '2024-03-20 10:00:00',
    updateBy: '',
    updateTime: null,
    remark: '',
    categoryName: '技术交流',
    userName: 'zhangsan',
    nickName: '张三',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    lastReplyUserName: '张三'
  },
  {
    postId: 2,
    categoryId: 2,
    userId: 2,
    title: '求助：Stable Diffusion安装问题',
    content: '在Windows上安装Stable Diffusion遇到了问题，总是提示缺少依赖包。\n\n错误信息：\n- CUDA版本不匹配\n- Python环境冲突\n- 模型文件下载失败\n\n请问有经验的朋友能帮忙解决吗？',
    contentType: '1',
    viewCount: 800,
    replyCount: 1,
    likeCount: 45,
    isTop: '0',
    isEssence: '0',
    status: '0',
    lastReplyTime: '2024-03-19 15:20:00',
    lastReplyUserId: 3,
    createBy: '李四',
    createTime: '2024-03-19 14:00:00',
    updateBy: '',
    updateTime: null,
    remark: '',
    categoryName: '产品讨论',
    userName: 'lisi',
    nickName: '李四',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    lastReplyUserName: '王五'
  },
  {
    postId: 3,
    categoryId: 3,
    userId: 3,
    title: '讨论：AI大模型的发展趋势',
    content: '让我们一起探讨一下AI大模型的未来发展方向。\n\n目前的发展现状：\n1. GPT-4已经展现出强大的多模态能力\n2. Claude在代码生成方面表现优异\n3. Gemini在推理能力上有独特优势\n\n未来可能的发展方向：\n- 更强的推理能力\n- 更低的训练成本\n- 更好的多模态融合\n- 更强的个性化定制',
    contentType: '1',
    viewCount: 2000,
    replyCount: 3,
    likeCount: 120,
    isTop: '1',
    isEssence: '1',
    status: '0',
    lastReplyTime: '2024-03-18 16:45:00',
    lastReplyUserId: 1,
    createBy: '王五',
    createTime: '2024-03-18 09:00:00',
    updateBy: '',
    updateTime: null,
    remark: '',
    categoryName: '闲聊灌水',
    userName: 'wangwu',
    nickName: '王五',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    lastReplyUserName: '张三'
  }
]

let replies = [
  {
    replyId: 1,
    postId: 1,
    userId: 2,
    parentId: 0,
    content: '非常实用的分享，感谢楼主！',
    likeCount: 5,
    status: '0',
    createBy: '李四',
    createTime: '2024-03-20 10:30:00',
    updateBy: '',
    updateTime: null,
    remark: '',
    userName: 'lisi',
    nickName: '李四',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    children: []
  },
  {
    replyId: 2,
    postId: 1,
    userId: 3,
    parentId: 0,
    content: '建议可以多分享一些具体的案例。',
    likeCount: 3,
    status: '0',
    createBy: '王五',
    createTime: '2024-03-20 11:15:00',
    updateBy: '',
    updateTime: null,
    remark: '',
    userName: 'wangwu',
    nickName: '王五',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    children: [
      {
        replyId: 3,
        postId: 1,
        userId: 1,
        parentId: 2,
        content: '好的，我后续会补充一些实际案例。',
        likeCount: 2,
        status: '0',
        createBy: '张三',
        createTime: '2024-03-20 11:30:00',
        updateBy: '',
        updateTime: null,
        remark: '',
        userName: 'zhangsan',
        nickName: '张三',
        avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
        children: []
      }
    ]
  },
  {
    replyId: 4,
    postId: 2,
    userId: 3,
    parentId: 0,
    content: '建议检查CUDA版本是否与Stable Diffusion要求匹配，可以先卸载重装CUDA。',
    likeCount: 8,
    status: '0',
    createBy: '王五',
    createTime: '2024-03-19 15:20:00',
    updateBy: '',
    updateTime: null,
    remark: '',
    userName: 'wangwu',
    nickName: '王五',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    children: []
  },
  {
    replyId: 5,
    postId: 3,
    userId: 1,
    parentId: 0,
    content: '我认为未来AI大模型会更加注重推理能力和逻辑思维，而不是简单的文本生成。',
    likeCount: 12,
    status: '0',
    createBy: '张三',
    createTime: '2024-03-18 14:30:00',
    updateBy: '',
    updateTime: null,
    remark: '',
    userName: 'zhangsan',
    nickName: '张三',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    children: []
  },
  {
    replyId: 6,
    postId: 3,
    userId: 2,
    parentId: 0,
    content: '同意楼主的观点，多模态融合是未来的重要发展方向。',
    likeCount: 7,
    status: '0',
    createBy: '李四',
    createTime: '2024-03-18 15:45:00',
    updateBy: '',
    updateTime: null,
    remark: '',
    userName: 'lisi',
    nickName: '李四',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    children: []
  },
  {
    replyId: 7,
    postId: 3,
    userId: 1,
    parentId: 0,
    content: '另外，我认为个性化定制也是一个重要趋势，每个用户都能有自己的专属AI助手。',
    likeCount: 15,
    status: '0',
    createBy: '张三',
    createTime: '2024-03-18 16:45:00',
    updateBy: '',
    updateTime: null,
    remark: '',
    userName: 'zhangsan',
    nickName: '张三',
    avatar: 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    children: []
  }
]

let likes = [
  { likeId: 1, userId: 2, targetType: '1', targetId: 1, createTime: '2024-03-20 10:35:00' },
  { likeId: 2, userId: 3, targetType: '1', targetId: 1, createTime: '2024-03-20 11:20:00' },
  { likeId: 3, userId: 1, targetType: '1', targetId: 2, createTime: '2024-03-19 15:25:00' },
  { likeId: 4, userId: 2, targetType: '1', targetId: 3, createTime: '2024-03-18 14:35:00' },
  { likeId: 5, userId: 3, targetType: '1', targetId: 3, createTime: '2024-03-18 15:50:00' }
]

let categories = [
  { categoryId: 1, categoryName: '技术交流', categoryDesc: '技术问题讨论、经验分享', categoryIcon: 'el-icon-cpu', sortOrder: 1, status: '0' },
  { categoryId: 2, categoryName: '产品讨论', categoryDesc: '产品功能建议、使用反馈', categoryIcon: 'el-icon-shopping-bag-1', sortOrder: 2, status: '0' },
  { categoryId: 3, categoryName: '闲聊灌水', categoryDesc: '轻松话题、日常交流', categoryIcon: 'el-icon-chat-dot-round', sortOrder: 3, status: '0' },
  { categoryId: 4, categoryName: '公告通知', categoryDesc: '官方公告、重要通知', categoryIcon: 'el-icon-bell', sortOrder: 0, status: '0' }
]

// 获取帖子列表
export function getPosts(query = {}) {
  let filteredPosts = [...posts]
  
  if (query.categoryId) {
    filteredPosts = filteredPosts.filter(post => post.categoryId === query.categoryId)
  }
  
  if (query.title) {
    filteredPosts = filteredPosts.filter(post => 
      post.title.toLowerCase().includes(query.title.toLowerCase())
    )
  }
  
  if (query.userId) {
    filteredPosts = filteredPosts.filter(post => post.userId === query.userId)
  }
  
  // 按置顶、精华、创建时间排序
  filteredPosts.sort((a, b) => {
    if (a.isTop !== b.isTop) return b.isTop - a.isTop
    if (a.isEssence !== b.isEssence) return b.isEssence - a.isEssence
    return new Date(b.createTime) - new Date(a.createTime)
  })
  
  return filteredPosts
}

// 获取帖子详情
export function getPost(postId) {
  return posts.find(post => post.postId === parseInt(postId))
}

// 创建帖子
export function createPost(postData) {
  const newPost = {
    postId: Math.max(...posts.map(p => p.postId)) + 1,
    categoryId: postData.categoryId,
    userId: postData.userId || 1,
    title: postData.title,
    content: postData.content,
    contentType: postData.contentType || '1',
    viewCount: 0,
    replyCount: 0,
    likeCount: 0,
    isTop: '0',
    isEssence: '0',
    status: '0',
    lastReplyTime: null,
    lastReplyUserId: null,
    createBy: postData.createBy || '当前用户',
    createTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
    updateBy: '',
    updateTime: null,
    remark: '',
    categoryName: categories.find(c => c.categoryId === postData.categoryId)?.categoryName || '',
    userName: postData.userName || 'currentuser',
    nickName: postData.nickName || '当前用户',
    avatar: postData.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    lastReplyUserName: null
  }
  
  posts.push(newPost)
  return newPost
}

// 更新帖子
export function updatePost(postId, postData) {
  const index = posts.findIndex(post => post.postId === parseInt(postId))
  if (index !== -1) {
    posts[index] = { ...posts[index], ...postData, updateTime: new Date().toISOString().slice(0, 19).replace('T', ' ') }
    return posts[index]
  }
  return null
}

// 删除帖子
export function deletePost(postId) {
  const index = posts.findIndex(post => post.postId === parseInt(postId))
  if (index !== -1) {
    posts.splice(index, 1)
    // 同时删除相关回复
    replies = replies.filter(reply => reply.postId !== parseInt(postId))
    return true
  }
  return false
}

// 增加浏览次数
export function increaseViewCount(postId) {
  const post = posts.find(p => p.postId === parseInt(postId))
  if (post) {
    post.viewCount++
    return true
  }
  return false
}

// 获取回复列表
export function getReplies(postId) {
  return replies.filter(reply => reply.postId === parseInt(postId) && reply.parentId === 0)
}

// 创建回复
export function createReply(postId, replyData) {
  const newReply = {
    replyId: Math.max(...replies.map(r => r.replyId)) + 1,
    postId: parseInt(postId),
    userId: replyData.userId || 1,
    parentId: replyData.parentId || 0,
    content: replyData.content,
    likeCount: 0,
    status: '0',
    createBy: replyData.createBy || '当前用户',
    createTime: new Date().toISOString().slice(0, 19).replace('T', ' '),
    updateBy: '',
    updateTime: null,
    remark: '',
    userName: replyData.userName || 'currentuser',
    nickName: replyData.nickName || '当前用户',
    avatar: replyData.avatar || 'https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png',
    children: []
  }
  
  replies.push(newReply)
  
  // 更新帖子回复数和最后回复信息
  const post = posts.find(p => p.postId === parseInt(postId))
  if (post) {
    post.replyCount++
    post.lastReplyTime = newReply.createTime
    post.lastReplyUserId = newReply.userId
    post.lastReplyUserName = newReply.nickName
  }
  
  return newReply
}

// 点赞帖子
export function likePost(postId, userId) {
  const existingLike = likes.find(like => 
    like.userId === userId && like.targetType === '1' && like.targetId === parseInt(postId)
  )
  
  if (existingLike) {
    return false // 已经点赞过了
  }
  
  const newLike = {
    likeId: Math.max(...likes.map(l => l.likeId)) + 1,
    userId: userId,
    targetType: '1',
    targetId: parseInt(postId),
    createTime: new Date().toISOString().slice(0, 19).replace('T', ' ')
  }
  
  likes.push(newLike)
  
  // 更新帖子点赞数
  const post = posts.find(p => p.postId === parseInt(postId))
  if (post) {
    post.likeCount++
  }
  
  return true
}

// 取消点赞帖子
export function unlikePost(postId, userId) {
  const index = likes.findIndex(like => 
    like.userId === userId && like.targetType === '1' && like.targetId === parseInt(postId)
  )
  
  if (index !== -1) {
    likes.splice(index, 1)
    
    // 更新帖子点赞数
    const post = posts.find(p => p.postId === parseInt(postId))
    if (post && post.likeCount > 0) {
      post.likeCount--
    }
    
    return true
  }
  
  return false
}

// 检查用户是否点赞帖子
export function isLikedByUser(postId, userId) {
  return likes.some(like => 
    like.userId === userId && like.targetType === '1' && like.targetId === parseInt(postId)
  )
}

// 获取分类列表
export function getCategories() {
  return categories
}

// 获取统计数据
export function getStats() {
  return {
    totalPosts: posts.length,
    totalUsers: Math.max(...posts.map(p => p.userId)),
    totalComments: replies.length
  }
}
