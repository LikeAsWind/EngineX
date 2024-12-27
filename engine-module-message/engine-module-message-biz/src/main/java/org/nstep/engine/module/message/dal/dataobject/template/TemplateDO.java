package org.nstep.engine.module.message.dal.dataobject.template;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.nstep.engine.framework.mybatis.core.dataobject.BaseDO;

/**
 * 消息模板信息 DO
 *
 * @author engine
 */
@TableName("message_template")
@KeySequence("message_template_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TemplateDO extends BaseDO {

    /**
     * 编号
     */
    @TableId
    private Long id;
    /**
     * 标题
     */
    private String name;
    /**
     * 当前消息状态：0.新建 20.停用 30.启用 40.等待发送 50.发送中 60.发送成功 70.发送失败
     */
    private Integer msgStatus;
    /**
     * 推送类型：10.实时 20.定时
     */
    private Integer pushType;
    /**
     * 定时任务Id (xxl-job-admin返回)
     */
    private Long cronTaskId;
    /**
     * 定时发送人群的文件路径
     */
    private String cronCrowdPath;
    /**
     * 期望发送时间：0:立即发送 定时任务以及周期任务:cron表达式
     */
    private String expectPushTime;
    /**
     * 消息发送渠道：10.Email 20.短信 30.钉钉机器人 40.微信服务号 50.push通知栏 60.飞书机器人
     */
    private Integer sendChannel;
    /**
     * 消息内容 占位符用${var}表示
     */
    private String msgContent;
    /**
     * 发送账号 一个渠道下可存在多个账号
     */
    private Integer sendAccount;
    /**
     * 10.通知类消息 20.营销类消息 30.验证码类消息
     */
    private Integer msgType;
    /**
     * 当前消息审核状态： 10.待审核 20.审核成功 30.被拒绝
     */
    private Integer auditStatus;
    /**
     * 当前定时模板使用用户id
     */
    private Long currentId;

}