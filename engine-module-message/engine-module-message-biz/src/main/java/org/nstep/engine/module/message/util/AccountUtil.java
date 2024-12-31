package org.nstep.engine.module.message.util;


import com.alibaba.fastjson.JSON;
import jakarta.annotation.Resource;
import me.chanjar.weixin.common.redis.RedisTemplateWxRedisOps;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.api.impl.WxMpServiceImpl;
import me.chanjar.weixin.mp.config.impl.WxMpRedisConfigImpl;
import org.nstep.engine.module.message.constant.WeChatConstants;
import org.nstep.engine.module.message.dal.dataobject.account.AccountDO;
import org.nstep.engine.module.message.dal.mysql.account.AccountMapper;
import org.nstep.engine.module.message.dto.weChat.WeChatServiceAccountConfig;
import org.nstep.engine.module.message.enums.ErrorCodeConstants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static org.nstep.engine.framework.common.exception.util.ServiceExceptionUtil.exception;


/**
 * 账户工具类，用于获取和管理账户相关的配置信息。
 * 该类提供了从数据库获取账户信息、初始化微信服务号账号配置等功能。
 */
@Configuration
public class AccountUtil {

    /**
     * 账户数据访问对象，用于与数据库交互获取账户信息。
     */
    @Resource
    private AccountMapper accountMapper;

    /**
     * 字符串Redis模板，用于操作Redis中的数据。
     */
    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 微信服务号账号集合，用于缓存微信服务号账号配置信息。
     * 键为账户数据对象，值为微信服务号服务对象。
     */
    private final ConcurrentMap<AccountDO, WxMpService> weChatServiceAccountMap = new ConcurrentHashMap<>();

    /**
     * 根据ID获取消息渠道配置信息。
     * 如果请求的是微信服务号账号配置类，则会封装成WxMpService对象并存入账号集合。
     *
     * @param id    账户ID
     * @param clazz 目标类类型，用于将账户配置信息反序列化为指定类型的对象
     * @param <T>   目标类类型
     * @return 反序列化后的账户配置信息对象
     */
    @SuppressWarnings("unchecked")
    public <T> T getAccount(Integer id, Class<T> clazz) {
        // 根据ID从数据库中查询账户信息
        AccountDO channelAccount = accountMapper.selectById(id);
        // 如果账户信息为空，则抛出异常
        if (Objects.isNull(channelAccount)) {
            throw exception(ErrorCodeConstants.CHANNEL_CODE_EMPTY);
        }
        // 如果请求的是微信服务号账号配置类，则初始化WxMpService对象并返回
        if (clazz.equals(WxMpService.class)) {
            // 使用ConcurrentHashMapUtils的computeIfAbsent方法来确保线程安全地获取或创建WxMpService对象
            WxMpService wxMpService = ConcurrentHashMapUtils.computeIfAbsent(weChatServiceAccountMap, channelAccount, channelAccount1 ->
                    initOfficialAccountService(JSON.parseObject(channelAccount.getAccountConfig(), WeChatServiceAccountConfig.class)));
            return (T) wxMpService;
        }
        // 否则，将账户配置信息反序列化为指定类型的对象并返回
        return JSON.parseObject(channelAccount.getAccountConfig(), clazz);
    }

    /**
     * 初始化微信服务号账号配置。
     * 使用Redis存储access_token，并设置微信服务号的配置信息。
     *
     * @param officialAccount 微信服务号账号配置对象
     * @return 初始化后的微信服务号服务对象
     */
    public WxMpService initOfficialAccountService(WeChatServiceAccountConfig officialAccount) {
        // 创建微信服务号服务对象
        WxMpService wxMpService = new WxMpServiceImpl();
        // 创建微信服务号Redis配置对象
        WxMpRedisConfigImpl config = new WxMpRedisConfigImpl(redisTemplateWxRedisOps(), WeChatConstants.WECHAT_SERVICE_ACCOUNT_ACCESS_TOKEN_PREFIX);
        // 设置微信服务号的配置信息
        config.setAppId(officialAccount.getAppId());
        config.setSecret(officialAccount.getSecret());
        config.setToken(officialAccount.getToken());
        config.useStableAccessToken(true);
        // 设置微信服务号服务对象的配置存储
        wxMpService.setWxMpConfigStorage(config);
        return wxMpService;
    }

    /**
     * 创建RedisTemplateWxRedisOps对象，用于操作微信服务号相关的Redis数据。
     *
     * @return RedisTemplateWxRedisOps对象
     */
    @Bean
    public RedisTemplateWxRedisOps redisTemplateWxRedisOps() {
        return new RedisTemplateWxRedisOps(stringRedisTemplate);
    }
}