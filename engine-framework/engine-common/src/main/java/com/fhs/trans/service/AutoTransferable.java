package com.fhs.trans.service;

import com.fhs.core.trans.vo.VO;

import java.util.ArrayList;
import java.util.List;


public interface AutoTransferable<V extends VO> {


    /**
     * 根据 ids 查询
     *
     * @param ids 编号数组
     * @return 数据列表
     */
    default List<V> selectByIds(List<?> ids) {
        return new ArrayList<>();
    }

    /**
     * 获取 db 中所有的数据
     *
     * @return db 中所有的数据
     */
    default List<V> select() {
        // 方法实现的具体逻辑，此处仅返回一个空列表
        return new ArrayList<>();
    }


    /**
     * 根据 id 获取 vo
     *
     * @param primaryValue id
     * @return vo
     */
    V selectById(Object primaryValue);


}
