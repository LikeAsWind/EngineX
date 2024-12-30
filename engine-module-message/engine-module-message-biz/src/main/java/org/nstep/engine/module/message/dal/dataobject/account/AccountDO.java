package org.nstep.engine.module.message.dal.dataobject.account;

import com.baomidou.mybatisplus.annotation.KeySequence;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import org.nstep.engine.framework.mybatis.core.dataobject.BaseDO;

/**
 * 渠道配置信息 DO
 *
 * @author engine
 */
@TableName("channel_account")
@KeySequence("channel_account_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 账号名称
     */
    private String name;
    /**
     * 消息发送渠道：10.Email 20.短信 30.钉钉机器人 40.微信服务号 50.push通知栏 60.飞书机器人
     */
    private Integer sendChannel;
    /**
     * 渠道code配置
     */
    private String accountConfig;

}