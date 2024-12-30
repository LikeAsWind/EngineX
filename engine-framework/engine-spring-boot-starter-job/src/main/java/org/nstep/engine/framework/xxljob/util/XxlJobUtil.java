package org.nstep.engine.framework.xxljob.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.alibaba.fastjson.JSON;
import com.xxl.job.core.biz.model.ReturnT;
import lombok.extern.slf4j.Slf4j;
import org.nstep.engine.framework.xxljob.constant.XxlJobDataConstants;
import org.nstep.engine.framework.xxljob.domain.XxlJobGroup;
import org.nstep.engine.framework.xxljob.domain.XxlJobInfo;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * xxl接口封装工具类
 * <p>
 * 该类封装了与 XXL-Job 调度中心交互的常用接口，包括任务的增、删、改、查等操作。
 * </p>
 * <p>
 * 提供了对任务调度执行器的管理功能，如创建、修改、启动、停止任务等。
 * </p>
 * <p>
 * 本工具类通过调用调度中心的接口，简化了任务调度的操作流程。
 * </p>
 */
@Component
@Slf4j
public class XxlJobUtil {

    @Value("${xxl.job.admin.addresses}")
    private String adminAddresses;

    @Value("${xxl.job.executor.appname}")
    private String appname;

    @Value("${xxl.job.executor.jobHandlerName}")
    private String title;

    /**
     * 获取执行器id
     * <p>
     * 该方法根据执行器名称和标题从调度中心获取对应的执行器ID。
     * 如果执行器不存在，则会创建新的执行器并返回其ID。
     * </p>
     *
     * @return 执行器ID
     */
    public Integer getGroupId() {
        Integer id = fetchGroupIdFromCenter();
        if (id == null) {
            log.info("执行器ID不存在，正在创建新的执行器...");
            String result = saveGroup();
            ReturnT<?> returnT = JSON.parseObject(result, ReturnT.class);
            if (ReturnT.SUCCESS_CODE == returnT.getCode()) {
                id = fetchGroupIdFromCenter();
            } else {
                log.error("创建执行器失败，返回错误: {}", result);
                throw new RuntimeException("创建执行器失败");
            }
        }
        return id;
    }

    private Integer fetchGroupIdFromCenter() {
        Map<String, Object> param = new HashMap<>();
        param.put("appname", appname);
        param.put("title", title);
        String result = doPost(XxlJobDataConstants.GET_GROUP_ID, param);
        Map<String, Object> map = JSON.parseObject(result, Map.class);
        List<XxlJobGroup> data = (List<XxlJobGroup>) map.get("data");
        return CollectionUtil.isEmpty(data) ? null : data.get(0).getId();
    }

    /**
     * 新建任务
     * <p>
     * 该方法用于创建一个新的任务，提交到调度中心。
     * </p>
     *
     * @param xxlJobInfo 新任务的信息
     * @return 返回任务ID
     */
    public String add(XxlJobInfo xxlJobInfo) {
        return executeJobRequest(XxlJobDataConstants.ADD_URL, xxlJobInfo);
    }

    /**
     * 修改任务
     * <p>
     * 该方法用于修改已存在的任务信息，提交到调度中心。
     * </p>
     *
     * @param xxlJobInfo 需要修改的任务信息
     * @return 返回操作结果
     */
    public String update(XxlJobInfo xxlJobInfo) {
        xxlJobInfo.setJobGroup(getGroupId());
        return executeJobRequest(XxlJobDataConstants.UPDATE_URL, xxlJobInfo);
    }

    /**
     * 删除任务
     * <p>
     * 该方法用于删除指定的任务，提交到调度中心。
     * </p>
     *
     * @param id 任务ID
     * @return 返回操作结果
     */
    public String remove(int id) {
        return executeJobRequest(XxlJobDataConstants.REMOVE_URL, Map.of("id", id));
    }

    /**
     * 停止任务
     * <p>
     * 该方法用于停止指定的任务，提交到调度中心。
     * </p>
     *
     * @param id 任务ID
     * @return 返回操作结果
     */
    public String stop(int id) {
        return executeJobRequest(XxlJobDataConstants.STOP_URL, Map.of("id", id));
    }

    /**
     * 启动任务
     * <p>
     * 该方法用于启动指定的任务，提交到调度中心。
     * </p>
     *
     * @param id 任务ID
     * @return 返回操作结果
     */
    public String start(int id) {
        return executeJobRequest(XxlJobDataConstants.START_URL, Map.of("id", id));
    }

    /**
     * 创建执行器
     * <p>
     * 该方法用于在调度中心创建新的执行器。
     * </p>
     *
     * @return 返回创建结果
     */
    public String saveGroup() {
        XxlJobGroup xxlJobGroup = XxlJobGroup.builder()
                .appname(appname)
                .title(title)
                .addressType(XxlJobDataConstants.XXL_GROUP_TYPE_AUTO)
                .build();
        return executeJobRequest(XxlJobDataConstants.SAVE_GROUP_URL, xxlJobGroup);
    }

    /**
     * 调用任务调度中心接口
     * <p>
     * 该方法用于通过HTTP POST请求与调度中心进行通信。
     * </p>
     *
     * @param url   调度中心接口的URL
     * @param param 请求参数
     * @return 返回接口响应结果
     */
    private String doPost(String url, Map<String, Object> param) {
        try {
            HttpResponse response = HttpRequest.post(adminAddresses + url).form(param).execute();
            return response.body();
        } catch (Exception e) {
            log.error("请求失败，URL: {}, 参数: {}, 错误: {}", url, param, e.getMessage());
            throw new RuntimeException("请求调度中心失败", e);
        }
    }

    /**
     * 执行任务请求的通用方法
     * <p>
     * 该方法用于封装任务的增、删、改操作。
     * </p>
     *
     * @param url        调度中心接口的URL
     * @param xxlJobInfo 任务信息
     * @return 返回操作结果
     */
    private String executeJobRequest(String url, Object xxlJobInfo) {
        Map<String, Object> params = JSON.parseObject(JSON.toJSONString(xxlJobInfo), Map.class);
        return doPost(url, params);
    }
}
