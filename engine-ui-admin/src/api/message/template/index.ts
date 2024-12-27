import request from '@/config/axios'

// 消息模板信息 VO
export interface TemplateVO {
  id: number // 编号
  name: string // 标题
  msgStatus: number // 当前消息状态：0.新建 20.停用 30.启用 40.等待发送 50.发送中 60.发送成功 70.发送失败
  pushType: number // 推送类型：10.实时 20.定时
  cronTaskId: number // 定时任务Id (xxl-job-admin返回)
  cronCrowdPath: string // 定时发送人群的文件路径
  expectPushTime: string // 期望发送时间：0:立即发送 定时任务以及周期任务:cron表达式
  sendChannel: number // 消息发送渠道：10.Email 20.短信 30.钉钉机器人 40.微信服务号 50.push通知栏 60.飞书机器人
  msgContent: string // 消息内容 占位符用${var}表示
  sendAccount: number // 发送账号 一个渠道下可存在多个账号
  msgType: number // 10.通知类消息 20.营销类消息 30.验证码类消息
  auditStatus: number // 当前消息审核状态： 10.待审核 20.审核成功 30.被拒绝
  currentId: number // 当前定时模板使用用户id
}

// 消息模板信息 API
export const TemplateApi = {
  // 查询消息模板信息分页
  getTemplatePage: async (params: any) => {
    return await request.get({ url: `/message/template/page`, params })
  },

  // 查询消息模板信息详情
  getTemplate: async (id: number) => {
    return await request.get({ url: `/message/template/get?id=` + id })
  },

  // 新增消息模板信息
  createTemplate: async (data: TemplateVO) => {
    return await request.post({ url: `/message/template/create`, data })
  },

  // 修改消息模板信息
  updateTemplate: async (data: TemplateVO) => {
    return await request.put({ url: `/message/template/update`, data })
  },

  // 删除消息模板信息
  deleteTemplate: async (id: number) => {
    return await request.delete({ url: `/message/template/delete?id=` + id })
  },

  // 导出消息模板信息 Excel
  exportTemplate: async (params) => {
    return await request.download({ url: `/message/template/export-excel`, params })
  }
}
