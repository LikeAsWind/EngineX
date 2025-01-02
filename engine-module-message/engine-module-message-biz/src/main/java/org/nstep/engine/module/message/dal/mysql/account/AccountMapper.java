package org.nstep.engine.module.message.dal.mysql.account;


import org.apache.ibatis.annotations.Mapper;
import org.nstep.engine.framework.common.pojo.PageResult;
import org.nstep.engine.framework.mybatis.core.mapper.BaseMapperX;
import org.nstep.engine.framework.mybatis.core.query.LambdaQueryWrapperX;
import org.nstep.engine.framework.security.core.util.SecurityFrameworkUtils;
import org.nstep.engine.module.message.controller.admin.account.vo.AccountPageReqVO;
import org.nstep.engine.module.message.dal.dataobject.account.AccountDO;


/**
 * 渠道配置信息 Mapper
 * <p>
 * 该接口用于操作与渠道配置相关的数据，包括查询、分页查询等操作。继承自 BaseMapperX，提供基本的数据库操作方法。
 * </p>
 *
 * @author engine
 */
@Mapper
public interface AccountMapper extends BaseMapperX<AccountDO> {

    /**
     * 分页查询渠道配置信息
     * <p>
     * 该方法根据传入的请求对象（AccountPageReqVO）进行分页查询，并根据提供的条件进行过滤。
     * 查询条件包括名称、发送渠道、账户配置、创建时间等字段。结果按 ID 降序排列。
     * </p>
     *
     * @param reqVO 分页请求对象，包含查询条件
     * @return 返回分页结果，包含匹配条件的渠道配置信息
     */
    default PageResult<AccountDO> selectPage(AccountPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<AccountDO>()
                .eq(AccountDO::getCreator, SecurityFrameworkUtils.getLoginUserId())
                // 如果名称字段不为空，则进行模糊查询
                .likeIfPresent(AccountDO::getName, reqVO.getName())
                // 如果发送渠道字段不为空，则进行精确匹配
                .eqIfPresent(AccountDO::getSendChannel, reqVO.getSendChannel())
                // 如果账户配置字段不为空，则进行精确匹配
                .eqIfPresent(AccountDO::getAccountConfig, reqVO.getAccountConfig())
                // 如果创建时间字段不为空，则进行时间范围查询
                .betweenIfPresent(AccountDO::getCreateTime, reqVO.getCreateTime())
                // 按 ID 降序排列结果
                .orderByDesc(AccountDO::getUpdater));
    }

}
