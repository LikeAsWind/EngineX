package org.nstep.engine.framework.common.core;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Key Value 的键值对
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class KeyValue<K, V> implements Serializable {

    /**
     * 键
     */
    private K key;
    /**
     * 值
     */
    private V value;

}
