<template>
  <div class="cm-task-editor-container">
    <div class="btn" :disabled="disabled" @click="!disabled ? open = true : ''">
      <slot>
        <el-button type="primary" plain icon="el-icon-plus" size="mini">新增巡更任务</el-button>
      </slot>
    </div>
    <el-dialog :title="title" :visible.sync="open" width="70vw" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="120px">

        <el-form-item label="任务名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入" />
        </el-form-item>
        <el-form-item label="监控点" prop="monitor">
          <!-- <el-input readonly :value="form.monitors ? form.monitors.map(({ name }) => name).join('、') : ''"
            placeholder="请输入" /> -->
            <el-select v-model="form.monitors" multiple placeholder="请选择" style="width: 100%;">
              <el-option 
              v-for="item in baseDataList"
              :key="item.id"
              :label="item.name" 
              :value="item.id"></el-option>
            </el-select>
        </el-form-item>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="开始时间" prop="startTime">
              <el-date-picker style="width: 100%;" v-model="form.startTime" type="datetime" placeholder="选择日期时间">
              </el-date-picker>
            </el-form-item>
          </el-col>
          
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="时间间隔" prop="timeInterval">
              <el-input placeholder="请输入" type="number" v-model="form.timeInterval" class="input-with-select">
                <el-select slot="append" v-model="form.unit" placeholder="请选择">
                  <el-option label="分" value="1"></el-option>
                  <el-option label="时" value="2"></el-option>
                  <el-option label="天" value="3"></el-option>
                  <el-option label="月" value="4"></el-option>
                </el-select>
              </el-input>
            </el-form-item>
          </el-col>
        </el-row>

        <el-form-item label="巡更结果接收人" prop="receivers">
          <el-select style="width: 100%;" v-model="form.receivers" multiple filterable remote reserve-keyword
            placeholder="请输入关键词" :remote-method="searchReceivers" :loading="receiversLoading">
            <el-option v-for="item in options" :key="item.value" :label="item.label" :value="item.value">
            </el-option>
          </el-select>
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

      },
      receiversLoading: false,
      options: [
        {
          label: "张三",
          value: "1",
        },
        {
          label: "李四",
          value: "2",
        }
        ,
        {
          label: "王五",
          value: "3",
        }
      ],
      baseDataList: [
        {
          id: 1,
          name: "一厂后门",
          ip: "192.168.55.66"
        },
        {
          id: 2,
          name: "一厂前门",
          ip: "192.168.55.67"
        },
        {
          id: 3,
          name: "一厂实验室",
          ip: "192.168.55.68"
        },
        {
          id: 4,
          name: "一厂吸烟室",
          ip: "192.168.55.69"
        }
      ],
    }
  },
  watch: {
    open(newVal) {
      if (!newVal) {
        this.form = {};
        return;
      }
      const info = this.info;
      if (info.id || info.id === 0) {
        this.form = this.lodash.cloneDeep(info || {});
        return;
      }
      this.form = {
        startTime: this.moment().format('YYYY-MM-DD HH:mm'),
        unit: "1",
        ...this.lodash.cloneDeep(info || {})
      };
    }
  },
  methods: {
    submitForm() {
      this.$refs.form.validate().then(() => {
        this.open = false;
        this.$emit("success");
      })
    },
    searchReceivers() {

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