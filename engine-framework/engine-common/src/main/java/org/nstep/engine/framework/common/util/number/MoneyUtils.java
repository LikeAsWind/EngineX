package org.nstep.engine.framework.common.util.number;

import cn.hutool.core.math.Money;
import cn.hutool.core.util.NumberUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 金额工具类
 * <p>
 * 该工具类提供了一些常见的金额计算和转换功能，如百分比计算、金额相乘、单位转换等。
 * 主要用于处理金额的计算和转换，确保金额在处理过程中精度的正确性。
 */
public class MoneyUtils {

    /**
     * 百分比对应的 BigDecimal 对象
     * <p>
     * 该常量用于百分比计算时，将百分比值除以 100 进行转换。
     */
    public static final BigDecimal PERCENT_100 = BigDecimal.valueOf(100);

    /**
     * 金额的小数位数
     * <p>
     * 该常量指定了金额计算时的精度，即保留两位小数。
     */
    private static final int PRICE_SCALE = 2;

    /**
     * 计算百分比金额，四舍五入
     * <p>
     * 该方法根据给定的金额和百分比，计算出百分比金额，并返回整数结果。
     *
     * @param price 金额（单位：分）
     * @param rate  百分比（例如 56.77% 则传入 56.77）
     * @return 百分比金额（单位：分）
     */
    public static Integer calculateRatePrice(Integer price, Double rate) {
        return calculateRatePrice(price, rate, 0, RoundingMode.HALF_UP).intValue();
    }

    /**
     * 计算百分比金额，向下取整
     * <p>
     * 该方法根据给定的金额和百分比，计算出百分比金额，并向下取整返回整数结果。
     *
     * @param price 金额（单位：分）
     * @param rate  百分比（例如 56.77% 则传入 56.77）
     * @return 百分比金额（单位：分）
     */
    public static Integer calculateRatePriceFloor(Integer price, Double rate) {
        return calculateRatePrice(price, rate, 0, RoundingMode.FLOOR).intValue();
    }

    /**
     * 计算百分比金额
     * <p>
     * 该方法根据给定的金额、数量和百分比，计算出商品总价。百分比以分为单位，支持折扣计算。
     *
     * @param price   单价（单位：分）
     * @param count   数量
     * @param percent 折扣（单位：分），例如 60.2% 则传入 6020
     * @return 商品总价（单位：分）
     */
    public static Integer calculator(Integer price, Integer count, Integer percent) {
        price = price * count;
        if (percent == null) {
            return price;
        }
        return MoneyUtils.calculateRatePriceFloor(price, (double) (percent / 100));
    }

    /**
     * 计算百分比金额
     * <p>
     * 该方法根据给定的金额、百分比、保留小数位数和舍入模式，计算百分比金额。
     *
     * @param price        金额（单位：分）
     * @param rate         百分比（例如 56.77% 则传入 56.77）
     * @param scale        保留的小数位数
     * @param roundingMode 舍入模式（例如四舍五入、向下取整等）
     * @return 计算后的百分比金额
     */
    public static BigDecimal calculateRatePrice(Number price, Number rate, int scale, RoundingMode roundingMode) {
        return NumberUtil.toBigDecimal(price).multiply(NumberUtil.toBigDecimal(rate)) // 乘以
                .divide(BigDecimal.valueOf(100), scale, roundingMode); // 除以 100
    }

    /**
     * 分转元
     * <p>
     * 该方法将单位为分的金额转换为单位为元的金额。
     *
     * @param fen 金额（单位：分）
     * @return 转换后的金额（单位：元）
     */
    public static BigDecimal fenToYuan(int fen) {
        return new Money(0, fen).getAmount();
    }

    /**
     * 分转元（字符串）
     * <p>
     * 该方法将单位为分的金额转换为单位为元的金额，并以字符串形式返回。
     * 例如，fen 为 1 时，返回 "0.01"。
     *
     * @param fen 金额（单位：分）
     * @return 转换后的金额（单位：元）字符串
     */
    public static String fenToYuanStr(int fen) {
        return new Money(0, fen).toString();
    }

    /**
     * 金额相乘，默认进行四舍五入
     * <p>
     * 该方法将两个金额相乘，默认使用四舍五入方式保留两位小数。
     *
     * @param price 金额（单位：元）
     * @param count 数量
     * @return 相乘后的结果（单位：元）
     */
    public static BigDecimal priceMultiply(BigDecimal price, BigDecimal count) {
        if (price == null || count == null) {
            return null;
        }
        return price.multiply(count).setScale(PRICE_SCALE, RoundingMode.HALF_UP);
    }

    /**
     * 金额相乘（百分比），默认进行四舍五入
     * <p>
     * 该方法将金额与百分比相乘，默认使用四舍五入方式保留两位小数。
     *
     * @param price   金额（单位：元）
     * @param percent 百分比（例如 60% 则传入 60）
     * @return 相乘后的结果（单位：元）
     */
    public static BigDecimal priceMultiplyPercent(BigDecimal price, BigDecimal percent) {
        if (price == null || percent == null) {
            return null;
        }
        return price.multiply(percent).divide(PERCENT_100, PRICE_SCALE, RoundingMode.HALF_UP);
    }

}
