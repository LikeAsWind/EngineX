<template>
  <ContentWrap>
    <!-- 搜索工作栏 -->
    <el-form
        class="-mb-15px"
        :model="queryParams"
        ref="queryFormRef"
        :inline="true"
        label-width="68px"
    >
                  <el-form-item label="标题" prop="name">
                    <el-input
                        v-model="queryParams.name"
                        placeholder="请输入标题"
                        clearable
                        @keyup.enter="handleQuery"
                        class="!w-240px"
                    />
                  </el-form-item>
                  <el-form-item label="当前消息状态：0.新建 20.停用 30.启用 40.等待发送 50.发送中 60.发送成功 70.发送失败" prop="msgStatus">
                    <el-select
                        v-model="queryParams.msgStatus"
                        placeholder="请选择当前消息状态：0.新建 20.停用 30.启用 40.等待发送 50.发送中 60.发送成功 70.发送失败"
                        clearable
                        class="!w-240px"
                    >
                          <el-option label="请选择字典生成" value=""/>
                    </el-select>
                  </el-form-item>
                  <el-form-item label="推送类型：10.实时 20.定时" prop="pushType">
                    <el-select
                        v-model="queryParams.pushType"
                        placeholder="请选择推送类型：10.实时 20.定时"
                        clearable
                        class="!w-240px"
                    >
                          <el-option label="请选择字典生成" value=""/>
                    </el-select>
                  </el-form-item>
                  <el-form-item label="定时任务Id (xxl-job-admin返回)" prop="cronTaskId">
                    <el-input
                        v-model="queryParams.cronTaskId"
                        placeholder="请输入定时任务Id (xxl-job-admin返回)"
                        clearable
                        @keyup.enter="handleQuery"
                        class="!w-240px"
                    />
                  </el-form-item>
                  <el-form-item label="定时发送人群的文件路径" prop="cronCrowdPath">
                    <el-input
                        v-model="queryParams.cronCrowdPath"
                        placeholder="请输入定时发送人群的文件路径"
                        clearable
                        @keyup.enter="handleQuery"
                        class="!w-240px"
                    />
                  </el-form-item>
                      <el-form-item label="期望发送时间：0:立即发送 定时任务以及周期任务:cron表达式" prop="expectPushTime">
                        <el-date-picker
                            v-model="queryParams.expectPushTime"
                            value-format="YYYY-MM-DD HH:mm:ss"
                            type="daterange"
                            start-placeholder="开始日期"
                            end-placeholder="结束日期"
                            :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
                            class="!w-220px"
                        />
                      </el-form-item>
                  <el-form-item label="消息发送渠道：10.Email 20.短信 30.钉钉机器人 40.微信服务号 50.push通知栏 60.飞书机器人" prop="sendChannel">
                    <el-input
                        v-model="queryParams.sendChannel"
                        placeholder="请输入消息发送渠道：10.Email 20.短信 30.钉钉机器人 40.微信服务号 50.push通知栏 60.飞书机器人"
                        clearable
                        @keyup.enter="handleQuery"
                        class="!w-240px"
                    />
                  </el-form-item>
                  <el-form-item label="发送账号 一个渠道下可存在多个账号" prop="sendAccount">
                    <el-input
                        v-model="queryParams.sendAccount"
                        placeholder="请输入发送账号 一个渠道下可存在多个账号"
                        clearable
                        @keyup.enter="handleQuery"
                        class="!w-240px"
                    />
                  </el-form-item>
                      <el-form-item label="创建时间" prop="createTime">
                        <el-date-picker
                            v-model="queryParams.createTime"
                            value-format="YYYY-MM-DD HH:mm:ss"
                            type="daterange"
                            start-placeholder="开始日期"
                            end-placeholder="结束日期"
                            :default-time="[new Date('1 00:00:00'), new Date('1 23:59:59')]"
                            class="!w-220px"
                        />
                      </el-form-item>
                  <el-form-item label="10.通知类消息 20.营销类消息 30.验证码类消息" prop="msgType">
                    <el-select
                        v-model="queryParams.msgType"
                        placeholder="请选择10.通知类消息 20.营销类消息 30.验证码类消息"
                        clearable
                        class="!w-240px"
                    >
                          <el-option label="请选择字典生成" value=""/>
                    </el-select>
                  </el-form-item>
                  <el-form-item label="当前消息审核状态： 10.待审核 20.审核成功 30.被拒绝" prop="auditStatus">
                    <el-select
                        v-model="queryParams.auditStatus"
                        placeholder="请选择当前消息审核状态： 10.待审核 20.审核成功 30.被拒绝"
                        clearable
                        class="!w-240px"
                    >
                          <el-option label="请选择字典生成" value=""/>
                    </el-select>
                  </el-form-item>
                  <el-form-item label="当前定时模板使用用户id" prop="currentId">
                    <el-input
                        v-model="queryParams.currentId"
                        placeholder="请输入当前定时模板使用用户id"
                        clearable
                        @keyup.enter="handleQuery"
                        class="!w-240px"
                    />
                  </el-form-item>
      <el-form-item>
        <el-button @click="handleQuery">
          <Icon icon="ep:search" class="mr-5px"/>
          搜索
        </el-button>
        <el-button @click="resetQuery">
          <Icon icon="ep:refresh" class="mr-5px"/>
          重置
        </el-button>
        <el-button
            type="primary"
            plain
            @click="openForm('create')"
            v-hasPermi="['message:template:create']"
        >
          <Icon icon="ep:plus" class="mr-5px"/>
          新增
        </el-button>
        <el-button
            type="success"
            plain
            @click="handleExport"
            :loading="exportLoading"
            v-hasPermi="['message:template:export']"
        >
          <Icon icon="ep:download" class="mr-5px"/>
          导出
        </el-button>
                </el-form-item>
    </el-form>
  </ContentWrap>

  <!-- 列表 -->
  <ContentWrap>
            <el-table v-loading="loading" :data="list" :stripe="true" :show-overflow-tooltip="true">
                      <el-table-column label="编号" align="center" prop="id"/>
                <el-table-column label="标题" align="center" prop="name"/>
                <el-table-column label="当前消息状态：0.新建 20.停用 30.启用 40.等待发送 50.发送中 60.发送成功 70.发送失败" align="center" prop="msgStatus"/>
                <el-table-column label="推送类型：10.实时 20.定时" align="center" prop="pushType"/>
                <el-table-column label="定时任务Id (xxl-job-admin返回)" align="center" prop="cronTaskId"/>
                <el-table-column label="定时发送人群的文件路径" align="center" prop="cronCrowdPath"/>
                <el-table-column label="期望发送时间：0:立即发送 定时任务以及周期任务:cron表达式" align="center" prop="expectPushTime"/>
                <el-table-column label="消息发送渠道：10.Email 20.短信 30.钉钉机器人 40.微信服务号 50.push通知栏 60.飞书机器人" align="center" prop="sendChannel"/>
                <el-table-column label="消息内容 占位符用${var}表示" align="center" prop="msgContent"/>
                <el-table-column label="发送账号 一个渠道下可存在多个账号" align="center" prop="sendAccount"/>
                <el-table-column
                    label="创建时间"
                    align="center"
                    prop="createTime"
                    width="180px"
                />
                <el-table-column label="10.通知类消息 20.营销类消息 30.验证码类消息" align="center" prop="msgType"/>
                <el-table-column label="当前消息审核状态： 10.待审核 20.审核成功 30.被拒绝" align="center" prop="auditStatus"/>
                <el-table-column label="当前定时模板使用用户id" align="center" prop="currentId"/>
    <el-table-column label="操作" align="center" min-width="120px">
      <template #default="scope">
        <el-button
            link
            type="primary"
            @click="openForm('update', scope.row.id)"
            v-hasPermi="['message:template:update']"
        >
          编辑
        </el-button>
        <el-button
            link
            type="danger"
            @click="handleDelete(scope.row.id)"
            v-hasPermi="['message:template:delete']"
        >
          删除
        </el-button>
      </template>
    </el-table-column>
  </el-table>
    <!-- 分页 -->
    <Pagination
        :total="total"
        v-model:page="queryParams.pageNo"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
    />
  </ContentWrap>

  <!-- 表单弹窗：添加/修改 -->
  <TemplateForm ref="formRef" @success="getList"/>
    </template>

<script setup lang="ts">
  import {handleTree} from '@/utils/tree'
  import download from '@/utils/download'
  import {TemplateApi, TemplateVO} from '@/api/'
            
  /** 消息模板信息 列表 */
  defineOptions({name: 'Template'})

  const message = useMessage() // 消息弹窗
  const {t} = useI18n() // 国际化

  const loading = ref(true) // 列表的加载中
  const list = ref<TemplateVO[]>([]) // 列表的数据
            const total = ref(0) // 列表的总页数
  const queryParams = reactive({
              pageNo: 1,
        pageSize: 10,
                      name: undefined,
                      msgStatus: undefined,
                      pushType: undefined,
                      cronTaskId: undefined,
                      cronCrowdPath: undefined,
                      expectPushTime: [],
                      sendChannel: undefined,
                      msgContent: undefined,
                      sendAccount: undefined,
                      createTime: [],
                      msgType: undefined,
                      auditStatus: undefined,
                      currentId: undefined,
  })
  const queryFormRef = ref() // 搜索的表单
  const exportLoading = ref(false) // 导出的加载中

  /** 查询列表 */
  const getList = async () => {
    loading.value = true
    try {
                  const data = await TemplateApi.getTemplatePage(queryParams)
          list.value = data.list
          total.value = data.total
    } finally {
      loading.value = false
    }
  }

  /** 搜索按钮操作 */
  const handleQuery = () => {
    queryParams.pageNo = 1
    getList()
  }

  /** 重置按钮操作 */
  const resetQuery = () => {
    queryFormRef.value.resetFields()
    handleQuery()
  }

  /** 添加/修改操作 */
  const formRef = ref()
  const openForm = (type: string, id?: number) => {
    formRef.value.open(type, id)
  }

  /** 删除按钮操作 */
  const handleDelete = async (id: number) => {
    try {
      // 删除的二次确认
      await message.delConfirm()
      // 发起删除
      await TemplateApi.deleteTemplate(id)
      message.success(t('common.delSuccess'))
      // 刷新列表
      await getList()
    } catch {
    }
  }

  /** 导出按钮操作 */
  const handleExport = async () => {
    try {
      // 导出的二次确认
      await message.exportConfirm()
      // 发起导出
      exportLoading.value = true
      const data = await TemplateApi.exportTemplate(queryParams)
      download.excel(data, '消息模板信息.xls')
    } catch {
    } finally {
      exportLoading.value = false
    }
  }
            
  /** 初始化 **/
  onMounted(() => {
    getList()
  })
</script>