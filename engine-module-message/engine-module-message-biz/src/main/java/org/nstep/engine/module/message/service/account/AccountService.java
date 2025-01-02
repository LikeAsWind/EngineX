package org.nstep.engine.module.message.service.account;


import jakarta.validation.Valid;
import org.nstep.engine.framework.common.pojo.PageResult;
import org.nstep.engine.module.message.controller.admin.account.vo.AccountPageReqVO;
import org.nstep.engine.module.message.controller.admin.account.vo.AccountRespVO;
import org.nstep.engine.module.message.controller.admin.account.vo.AccountSaveReqVO;
import org.nstep.engine.module.message.dal.dataobject.account.AccountDO;

import java.util.List;


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

    /**
     * 获取当前用户的所有渠道账号
     *
     * @param sendChannel 渠道类型，表示需要查询的渠道类别。不同的渠道类型对应不同的配置。
     * @return 返回一个包含所有符合条件的渠道账号的列表，类型为 {@link AccountRespVO}，该类包含了渠道账号的详细信息。
     */
    List<AccountRespVO> list4CurrUser(Integer sendChannel);

}