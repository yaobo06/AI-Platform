<template>
  <div class="cm-task-editor-container">
    <div class="btn" :disabled="disabled" @click="!disabled ? open = true : ''">
      <slot>
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
        >新增巡更任务</el-button>
      </slot>
    </div>
    <el-dialog :title="title" :visible.sync="open" width="70vw" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="任务名称" prop="name">
          <el-input readonly v-model="form.name" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="监控点" prop="monitor">
          <el-input readonly :value="form.monitors ? form.monitors.map(({name}) => name).join('、') : ''" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-input readonly v-model="form.startTime" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="时间间隔" prop="timeInterval">
          <el-input placeholder="请输入" type="number" v-model="form.timeInterval" class="input-with-select">
            <el-select slot="append" v-model="form.unit" placeholder="请选择" style="width: 5em;">
              <el-option label="分" value="1"></el-option>
              <el-option label="时" value="2"></el-option>
              <el-option label="天" value="3"></el-option>
              <el-option label="月" value="4"></el-option>
            </el-select>
          </el-input>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="open = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
export default {
  name: "CmTaskEditor",
  props: {
    title: {
      type: String,
      default: "新增巡更任务"
    },
    info: {
      type: Object,
      default: () => ({})
    },
    disabled: {
      type: Boolean,
      default: false
    }
  },
  data() {
    return {
      open: false,
      form: {},
      rules: {
        
      }
    }
  },
  watch: {
    open(newVal) {
      if(!newVal) {
        this.form = {};
        return;
      }
      this.form = {
        startTime: this.moment().format('YYYY-MM-DD HH:mm'),
        unit: "1",
        ...this.lodash.cloneDeep(this.info || {})
      };
    }
  },
  methods: {
    submitForm() {
      this.$refs.form.validate().then(() => {
        this.open = false;
        this.$emit("success");
      })
    }
  }
};
</script>
<style lang="scss" scoped>
.cm-task-editor-container {
  height: 100%;
  .btn {
    &[disabled] {
      opacity: 0.6;
      cursor: not-allowed;
    }
  }
}

</style>