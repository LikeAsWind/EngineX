package org.nstep.engine.module.message.service.account;


import jakarta.validation.Valid;
import org.nstep.engine.framework.common.pojo.PageResult;
import org.nstep.engine.module.message.controller.admin.account.vo.AccountPageReqVO;
import org.nstep.engine.module.message.controller.admin.account.vo.AccountSaveReqVO;
import org.nstep.engine.module.message.dal.dataobject.account.AccountDO;


/**
 * 渠道配置信息 Service 接口
 *
 * @author engine
 */
public interface AccountService {

    /**
     * 创建渠道配置信息
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createAccount(@Valid AccountSaveReqVO createReqVO);

    /**
     * 更新渠道配置信息
     *
     * @param updateReqVO 更新信息
     */
    void updateAccount(@Valid AccountSaveReqVO updateReqVO);

    /**
     * 删除渠道配置信息
     *
     * @param id 编号
     */
    void deleteAccount(Long id);

    /**
     * 获得渠道配置信息
     *
     * @param id 编号
     * @return 渠道配置信息
     */
    AccountDO getAccount(Long id);

    /**
     * 获得渠道配置信息分页
     *
     * @param pageReqVO 分页查询
     * @return 渠道配置信息分页
     */
    PageResult<AccountDO> getAccountPage(AccountPageReqVO pageReqVO);

}