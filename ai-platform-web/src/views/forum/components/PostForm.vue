<template>
  <el-dialog
    title="发布新帖子"
    :visible.sync="dialogVisible"
    width="50%"
    :close-on-click-modal="false"
    custom-class="post-form-dialog"
    @close="handleClose"
  >
    <el-form :model="postForm" :rules="postRules" ref="postForm" label-position="top">
      <el-form-item label="标题" prop="title">
        <el-input
          v-model="postForm.title"
          placeholder="请输入帖子标题"
          maxlength="50"
          show-word-limit
        ></el-input>
      </el-form-item>
      <el-form-item label="内容" prop="content">
        <el-input
          type="textarea"
          v-model="postForm.content"
          :rows="8"
          placeholder="请输入帖子内容"
          maxlength="2000"
          show-word-limit
        ></el-input>
      </el-form-item>
      <el-form-item label="分类" prop="category">
        <el-select v-model="postForm.category" placeholder="请选择分类" class="category-select">
          <el-option
            v-for="cat in categories.filter(c => c.id !== 'all')"
            :key="cat.id"
            :label="cat.name"
            :value="cat.id"
          >
            <span class="category-icon" :class="getCategoryIcon(cat.id)"></span>
            {{ cat.name }}
          </el-option>
        </el-select>
      </el-form-item>
      <el-form-item label="标签" prop="tags">
        <el-select
          v-model="postForm.tags"
          multiple
          filterable
          allow-create
          default-first-option
          placeholder="请选择或输入标签"
          class="tag-select"
        >
          <el-option
            v-for="tag in commonTags"
            :key="tag"
            :label="tag"
            :value="tag"
          ></el-option>
        </el-select>
        <div class="tag-tips">最多可添加5个标签，每个标签不超过10个字符</div>
      </el-form-item>
    </el-form>
    <template #footer>
      <div class="dialog-footer">
        <el-button @click="handleClose">取消</el-button>
        <el-button type="primary" @click="handleSubmit" :loading="submitting">
          {{ submitting ? '发布中...' : '发布' }}
        </el-button>
      </div>
    </template>
  </el-dialog>
</template>

<script>
export default {
  name: 'PostForm',
  props: {
    value: {
      type: Boolean,
      required: true
    },
    categories: {
      type: Array,
      required: true
    }
  },
  data() {
    return {
      postForm: {
        title: '',
        content: '',
        category: '',
        tags: []
      },
      postRules: {
        title: [
          { required: true, message: '请输入标题', trigger: 'blur' },
          { min: 3, max: 50, message: '长度在 3 到 50 个字符', trigger: 'blur' }
        ],
        content: [
          { required: true, message: '请输入内容', trigger: 'blur' },
          { min: 10, max: 2000, message: '长度在 10 到 2000 个字符', trigger: 'blur' }
        ],
        category: [
          { required: true, message: '请选择分类', trigger: 'change' }
        ],
        tags: [
          { type: 'array', max: 5, message: '最多添加5个标签', trigger: 'change' }
        ]
      },
      submitting: false,
      commonTags: [
        'ChatGPT',
        'Stable Diffusion',
        'Midjourney',
        'Claude',
        'Gemini',
        'AI绘画',
        '深度学习',
        '机器学习',
        'NLP',
        '计算机视觉'
      ]
    }
  },
  computed: {
    dialogVisible: {
      get() {
        return this.value
      },
      set(val) {
        this.$emit('input', val)
      }
    }
  },
  methods: {
    getCategoryIcon(categoryId) {
      const icons = {
        '1': 'el-icon-share',
        '2': 'el-icon-question',
        '3': 'el-icon-chat-dot-round',
        '4': 'el-icon-news'
      }
      const key = String(categoryId)
      return icons[key] || 'el-icon-folder'
    },
    handleClose() {
      this.dialogVisible = false
      this.$refs.postForm.resetFields()
      this.postForm.tags = []
      this.submitting = false
    },
    async handleSubmit() {
      try {
        await this.$refs.postForm.validate()
        this.submitting = true
        await this.$emit('submit', { ...this.postForm })
        this.handleClose()
      } catch (error) {
        // 表单验证失败
        return false
      } finally {
        this.submitting = false
      }
    }
  }
}
</script>

<style scoped lang="scss">
:deep(.post-form-dialog) {
  border-radius: 8px;
  overflow: hidden;

  .el-dialog__header {
    background: #f8fafc;
    margin: 0;
    padding: 20px 24px;
    border-bottom: 1px solid #e5e7eb;

    .el-dialog__title {
      font-size: 18px;
      font-weight: 600;
      color: #1f2937;
    }
  }

  .el-dialog__body {
    padding: 24px;
  }

  .el-dialog__footer {
    padding: 16px 24px;
    border-top: 1px solid #e5e7eb;
    background: #f8fafc;
  }
}

.el-form {
  .el-form-item__label {
    font-weight: 500;
    color: #374151;
    padding-bottom: 8px;
  }

  .el-input__inner,
  .el-textarea__inner {
    border-color: #d1d5db;
    border-radius: 6px;

    &:hover,
    &:focus {
      border-color: #1e40af;
    }
  }

  .el-input__count {
    background: transparent;
    color: #6b7280;
  }
}

.category-select {
  width: 100%;

  :deep(.el-select__tags) {
    background-color: transparent;
  }
}

.category-icon {
  margin-right: 8px;
  color: #6b7280;
}

.tag-select {
  width: 100%;

  :deep(.el-select__tags) {
    background-color: transparent;
  }
}

.tag-tips {
  margin-top: 4px;
  font-size: 12px;
  color: #6b7280;
}

.dialog-footer {
  text-align: right;

  .el-button {
    padding: 9px 20px;
    font-size: 14px;
    border-radius: 6px;

    &--default {
      border-color: #d1d5db;
      color: #374151;

      &:hover {
        border-color: #9ca3af;
        color: #1f2937;
      }
    }

    &--primary {
      background-color: #1e40af;
      border-color: #1e40af;

      &:hover {
        background-color: #1e3a8a;
        border-color: #1e3a8a;
      }
    }
  }
}
</style> 