package org.nstep.engine.module.system.service.mail;

import jakarta.validation.Valid;
import org.nstep.engine.framework.common.pojo.PageResult;
import org.nstep.engine.module.system.controller.admin.mail.vo.account.MailAccountPageReqVO;
import org.nstep.engine.module.system.controller.admin.mail.vo.account.MailAccountSaveReqVO;
import org.nstep.engine.module.system.dal.dataobject.mail.MailAccountDO;

import java.util.List;

/**
 * 邮箱账号 Service 接口
 *
 * @since 2022-03-21
 */
public interface MailAccountService {

    /**
     * 创建邮箱账号
     *
     * @param createReqVO 邮箱账号信息
     * @return 编号
     */
    Long createMailAccount(@Valid MailAccountSaveReqVO createReqVO);

    /**
     * 修改邮箱账号
     *
     * @param updateReqVO 邮箱账号信息
     */
    void updateMailAccount(@Valid MailAccountSaveReqVO updateReqVO);

    /**
     * 删除邮箱账号
     *
     * @param id 编号
     */
    void deleteMailAccount(Long id);

    /**
     * 获取邮箱账号信息
     *
     * @param id 编号
     * @return 邮箱账号信息
     */
    MailAccountDO getMailAccount(Long id);

    /**
     * 从缓存中获取邮箱账号
     *
     * @param id 编号
     * @return 邮箱账号
     */
    MailAccountDO getMailAccountFromCache(Long id);

    /**
     * 获取邮箱账号分页信息
     *
     * @param pageReqVO 邮箱账号分页参数
     * @return 邮箱账号分页信息
     */
    PageResult<MailAccountDO> getMailAccountPage(MailAccountPageReqVO pageReqVO);

    /**
     * 获取邮箱数组信息
     *
     * @return 邮箱账号信息数组
     */
    List<MailAccountDO> getMailAccountList();

}
