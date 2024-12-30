package org.nstep.engine.module.message.service.account;

import jakarta.annotation.Resource;
import org.nstep.engine.framework.common.pojo.PageResult;
import org.nstep.engine.framework.common.util.object.BeanUtils;
import org.nstep.engine.module.message.controller.admin.account.vo.AccountPageReqVO;
import org.nstep.engine.module.message.controller.admin.account.vo.AccountSaveReqVO;
import org.nstep.engine.module.message.dal.dataobject.account.AccountDO;
import org.nstep.engine.module.message.dal.mysql.account.AccountMapper;
import org.nstep.engine.module.message.enums.ErrorCodeConstants;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import static org.nstep.engine.framework.common.exception.util.ServiceExceptionUtil.exception;


/**
 * 渠道配置信息 Service 实现类
 * <p>
 * 该类实现了 AccountService 接口，提供了关于渠道配置信息（Account）的增删改查功能。包括创建、更新、删除、查询单个和分页查询等操作。
 * 它依赖于 AccountMapper 进行数据库操作，并在服务方法中封装了业务逻辑。
 *
 * @author engine
 */
@Service
@Validated
public class AccountServiceImpl implements AccountService {

    @Resource
    private AccountMapper accountMapper;

    /**
     * 创建渠道配置信息
     * <p>
     * 该方法接收一个创建请求对象（AccountSaveReqVO），将其转换为 AccountDO 对象后，插入到数据库中。
     * 插入成功后返回创建的渠道配置的ID。
     *
     * @param createReqVO 渠道配置信息的创建请求对象
     * @return 返回创建的渠道配置信息ID
     */
    @Override
    public Long createAccount(AccountSaveReqVO createReqVO) {
        // 将创建请求对象转换为数据库实体对象
        AccountDO account = BeanUtils.toBean(createReqVO, AccountDO.class);
        // 插入数据库
        accountMapper.insert(account);
        // 返回插入后的ID
        return account.getId();
    }

    /**
     * 更新渠道配置信息
     * <p>
     * 该方法接收一个更新请求对象（AccountSaveReqVO），首先通过ID验证该渠道配置信息是否存在，
     * 然后将请求对象转换为数据库实体对象并执行更新操作。
     *
     * @param updateReqVO 渠道配置信息的更新请求对象
     */
    @Override
    public void updateAccount(AccountSaveReqVO updateReqVO) {
        // 校验渠道配置信息是否存在
        validateAccountExists(updateReqVO.getId());
        // 将更新请求对象转换为数据库实体对象
        AccountDO updateObj = BeanUtils.toBean(updateReqVO, AccountDO.class);
        // 执行更新操作
        accountMapper.updateById(updateObj);
    }

    /**
     * 删除渠道配置信息
     * <p>
     * 该方法接收一个ID作为参数，首先验证该渠道配置信息是否存在，然后执行删除操作。
     *
     * @param id 渠道配置信息的ID
     */
    @Override
    public void deleteAccount(Long id) {
        // 校验渠道配置信息是否存在
        validateAccountExists(id);
        // 执行删除操作
        accountMapper.deleteById(id);
    }

    /**
     * 校验渠道配置信息是否存在
     * <p>
     * 该方法根据ID查询数据库，若查询结果为空，则抛出异常表示该渠道配置信息不存在。
     *
     * @param id 渠道配置信息的ID
     */
    private void validateAccountExists(Long id) {
        if (accountMapper.selectById(id) == null) {
            // 抛出异常，表示渠道配置信息不存在
            throw exception(ErrorCodeConstants.ACCOUNT_NOT_EXISTS);
        }
    }

    /**
     * 获取渠道配置信息
     * <p>
     * 该方法根据ID查询渠道配置信息，若存在则返回对应的 AccountDO 对象。
     *
     * @param id 渠道配置信息的ID
     * @return 返回查询到的渠道配置信息
     */
    @Override
    public AccountDO getAccount(Long id) {
        return accountMapper.selectById(id);
    }

    /**
     * 获取渠道配置信息分页列表
     * <p>
     * 该方法根据分页请求对象（AccountPageReqVO）查询渠道配置信息的分页数据。
     *
     * @param pageReqVO 分页请求对象
     * @return 返回分页结果
     */
    @Override
    public PageResult<AccountDO> getAccountPage(AccountPageReqVO pageReqVO) {
        return accountMapper.selectPage(pageReqVO);
    }

}
