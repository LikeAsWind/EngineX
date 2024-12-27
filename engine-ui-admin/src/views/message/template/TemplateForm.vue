<template>
  <Dialog :title="dialogTitle" v-model="dialogVisible">
    <el-form
        ref="formRef"
        :model="formData"
        :rules="formRules"
        label-width="100px"
        v-loading="formLoading"
    >
                  <el-form-item label="标题" prop="name">
                    <el-input v-model="formData.name" placeholder="请输入标题"/>
                  </el-form-item>
                  <el-form-item label="当前消息状态：0.新建 20.停用 30.启用 40.等待发送 50.发送中 60.发送成功 70.发送失败" prop="msgStatus">
                    <el-radio-group v-model="formData.msgStatus">
                          <el-radio value="1">请选择字典生成</el-radio>
                    </el-radio-group>
                  </el-form-item>
                  <el-form-item label="推送类型：10.实时 20.定时" prop="pushType">
                    <el-select v-model="formData.pushType" placeholder="请选择推送类型：10.实时 20.定时">
                          <el-option label="请选择字典生成" value=""/>
                    </el-select>
                  </el-form-item>
                  <el-form-item label="定时任务Id (xxl-job-admin返回)" prop="cronTaskId">
                    <el-input v-model="formData.cronTaskId" placeholder="请输入定时任务Id (xxl-job-admin返回)"/>
                  </el-form-item>
                  <el-form-item label="定时发送人群的文件路径" prop="cronCrowdPath">
                    <el-input v-model="formData.cronCrowdPath" placeholder="请输入定时发送人群的文件路径"/>
                  </el-form-item>
                  <el-form-item label="期望发送时间：0:立即发送 定时任务以及周期任务:cron表达式" prop="expectPushTime">
                    <el-date-picker
                        v-model="formData.expectPushTime"
                        type="date"
                        value-format="x"
                        placeholder="选择期望发送时间：0:立即发送 定时任务以及周期任务:cron表达式"
                    />
                  </el-form-item>
                  <el-form-item label="消息发送渠道：10.Email 20.短信 30.钉钉机器人 40.微信服务号 50.push通知栏 60.飞书机器人" prop="sendChannel">
                    <el-input v-model="formData.sendChannel" placeholder="请输入消息发送渠道：10.Email 20.短信 30.钉钉机器人 40.微信服务号 50.push通知栏 60.飞书机器人"/>
                  </el-form-item>
                  <el-form-item label="消息内容 占位符用${var}表示" prop="msgContent">
                    <Editor v-model="formData.msgContent" height="150px"/>
                  </el-form-item>
                  <el-form-item label="发送账号 一个渠道下可存在多个账号" prop="sendAccount">
                    <el-input v-model="formData.sendAccount" placeholder="请输入发送账号 一个渠道下可存在多个账号"/>
                  </el-form-item>
                  <el-form-item label="10.通知类消息 20.营销类消息 30.验证码类消息" prop="msgType">
                    <el-select v-model="formData.msgType" placeholder="请选择10.通知类消息 20.营销类消息 30.验证码类消息">
                          <el-option label="请选择字典生成" value=""/>
                    </el-select>
                  </el-form-item>
                  <el-form-item label="当前消息审核状态： 10.待审核 20.审核成功 30.被拒绝" prop="auditStatus">
                    <el-radio-group v-model="formData.auditStatus">
                          <el-radio value="1">请选择字典生成</el-radio>
                    </el-radio-group>
                  </el-form-item>
                  <el-form-item label="当前定时模板使用用户id" prop="currentId">
                    <el-input v-model="formData.currentId" placeholder="请输入当前定时模板使用用户id"/>
                  </el-form-item>
    </el-form>
          <template #footer>
      <el-button @click="submitForm" type="primary" :disabled="formLoading">确 定</el-button>
      <el-button @click="dialogVisible = false">取 消</el-button>
    </template>
  </Dialog>
</template>
<script setup lang="ts">
  import {TemplateApi, TemplateVO} from '@/api/'
  import {handleTree} from '@/utils/tree'
            
  /** 消息模板信息 表单 */
  defineOptions({name: 'TemplateForm'})

  const {t} = useI18n() // 国际化
  const message = useMessage() // 消息弹窗

  const dialogVisible = ref(false) // 弹窗的是否展示
  const dialogTitle = ref('') // 弹窗的标题
  const formLoading = ref(false) // 表单的加载中：1）修改时的数据加载；2）提交的按钮禁用
  const formType = ref('') // 表单的类型：create - 新增；update - 修改
  const formData = ref({
                      id: undefined,
                      name: undefined,
                      msgStatus: undefined,
                      pushType: undefined,
                      cronTaskId: undefined,
                      cronCrowdPath: undefined,
                      expectPushTime: undefined,
                      sendChannel: undefined,
                      msgContent: undefined,
                      sendAccount: undefined,
                      msgType: undefined,
                      auditStatus: undefined,
                      currentId: undefined,
  })
  const formRules = reactive({
                  name: [{
              required: true, message: '标题不能为空', trigger: 'blur' }],
                  msgStatus: [{
              required: true, message: '当前消息状态：0.新建 20.停用 30.启用 40.等待发送 50.发送中 60.发送成功 70.发送失败不能为空', trigger: 'blur' }],
                  pushType: [{
              required: true, message: '推送类型：10.实时 20.定时不能为空', trigger: 'change'
 }],
                  sendChannel: [{
              required: true, message: '消息发送渠道：10.Email 20.短信 30.钉钉机器人 40.微信服务号 50.push通知栏 60.飞书机器人不能为空', trigger: 'blur' }],
                  msgContent: [{
              required: true, message: '消息内容 占位符用${var}表示不能为空', trigger: 'blur' }],
                  sendAccount: [{
              required: true, message: '发送账号 一个渠道下可存在多个账号不能为空', trigger: 'blur' }],
                  msgType: [{
              required: true, message: '10.通知类消息 20.营销类消息 30.验证码类消息不能为空', trigger: 'change'
 }],
                  auditStatus: [{
              required: true, message: '当前消息审核状态： 10.待审核 20.审核成功 30.被拒绝不能为空', trigger: 'blur' }],
  })
  const formRef = ref() // 表单 Ref
            
  /** 打开弹窗 */
  const open = async (type: string, id?: number) => {
    dialogVisible.value = true
    dialogTitle.value = t('action.' + type)
    formType.value = type
    resetForm()
    // 修改时，设置数据
    if (id) {
      formLoading.value = true
      try {
        formData.value = await TemplateApi.getTemplate(id)
      } finally {
        formLoading.value = false
      }
    }
        }
  defineExpose({open}) // 提供 open 方法，用于打开弹窗

  /** 提交表单 */
  const emit = defineEmits(['success']) // 定义 success 事件，用于操作成功后的回调
  const submitForm = async () => {
    // 校验表单
    await formRef.value.validate()
          // 提交请求
    formLoading.value = true
    try {
      const data = formData.value as unknown as TemplateVO
              if (formType.value === 'create') {
        await TemplateApi.createTemplate(data)
        message.success(t('common.createSuccess'))
      } else {
        await TemplateApi.updateTemplate(data)
        message.success(t('common.updateSuccess'))
      }
      dialogVisible.value = false
      // 发送操作成功的事件
      emit('success')
    } finally {
      formLoading.value = false
    }
  }

  /** 重置表单 */
  const resetForm = () => {
    formData.value = {
                        id: undefined,
                        name: undefined,
                        msgStatus: undefined,
                        pushType: undefined,
                        cronTaskId: undefined,
                        cronCrowdPath: undefined,
                        expectPushTime: undefined,
                        sendChannel: undefined,
                        msgContent: undefined,
                        sendAccount: undefined,
                        msgType: undefined,
                        auditStatus: undefined,
                        currentId: undefined,
    }
    formRef.value?.resetFields()
  }
      </script>