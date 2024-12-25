package org.nstep.engine.module.system.enums.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 性别的枚举值
 * <p>
 * 该枚举类定义了性别的常见值，包括男性、女性和未知性别。
 * - MALE: 男性，值为 1
 * - FEMALE: 女性，值为 2
 * - UNKNOWN: 未知性别，值为 0
 */
@Getter
@AllArgsConstructor
public enum SexEnum {

    /**
     * 男
     * <p>
     * 代表男性，值为 1。
     */
    MALE(1),

    /**
     * 女
     * <p>
     * 代表女性，值为 2。
     */
    FEMALE(2),

    /**
     * 未知
     * <p>
     * 代表性别未知，值为 0。
     */
    UNKNOWN(0);

    /**
     * 性别值
     * <p>
     * 存储性别的具体值，1 代表男性，2 代表女性，0 代表未知。
     */
    private final Integer sex;

}
