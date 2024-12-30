package org.nstep.engine.framework.xxljob.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 * XXL 执行器对象
 * <p>
 * 该类用于表示 XXL-JOB 执行器的配置信息，包括执行器的 ID、应用名称、标题、地址类型、地址列表等。
 * 该类用于管理执行器的配置信息，并支持获取执行器地址列表。
 */
@Data  // 自动生成 getter、setter、toString、equals、hashCode 方法
@Builder  // 支持构建者模式
@Accessors(chain = true)  // 支持链式调用
@AllArgsConstructor  // 生成包含所有字段的构造函数
@NoArgsConstructor  // 生成无参构造函数
public class XxlJobGroup {

    private int id;  // 执行器 ID
    private String appname;  // 执行器应用名称
    private String title;  // 执行器标题
    private int addressType;  // 执行器地址类型：0=自动注册，1=手动录入
    private String addressList;  // 执行器地址列表，多个地址用逗号分隔（手动录入）
    private Date updateTime;  // 最后更新时间

    // registry list
    private List<String> registryList;  // 执行器地址列表（系统注册）

    /**
     * 获取执行器的注册地址列表
     * <p>
     * 如果手动录入了执行器地址列表（addressList），则将其拆分为一个字符串列表返回。
     * 否则，返回系统注册的执行器地址列表。
     *
     * @return 执行器地址列表
     */
    public List<String> getRegistryList() {
        // 如果手动录入了地址列表，拆分地址并返回
        if (addressList != null && !addressList.trim().isEmpty()) {
            registryList = new ArrayList<String>(Arrays.asList(addressList.split(",")));
        }
        return registryList;  // 返回执行器地址列表
    }
}
