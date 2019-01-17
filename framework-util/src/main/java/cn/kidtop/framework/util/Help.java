package cn.kidtop.framework.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Pattern;

/**
 * <div>类简介</div>
 * convert<br/>
 * 当指定对象是null,数字是0，字符串是空时返回指定值<br/>
 */
public class Help {

    /**
     * 转化\\u开头的十六进制字符串表示的unicode码为字符串
     *
     * @param src
     */
    private static Pattern FOUR_HEX_REGEX = null;

    /**
     * 如果src!=null && src.intValue()!=0，返回src，否则返回dst中第一个符合前述条件的参数
     *
     * @param <T> t
     * @param src 返回的符合条件元素
     * @param dst 传入参数
     * @return T 返回传入类型
     */
    @SuppressWarnings("unchecked")
    public static <T> T convert(T src, T... dst) {
        int i = 0, l = dst.length;
        while (i < l && (BeanUtils.isEmpty(src) ||
                ((src instanceof Long || src instanceof Integer || src instanceof AtomicInteger || src instanceof AtomicLong || src instanceof BigInteger) && ((Number) src).intValue() == 0) ||
                (src instanceof Double && ((Double) src).compareTo(0.0) == 0) ||
                (src instanceof Float && ((Float) src).compareTo(0.0f) == 0) ||
                (src instanceof BigDecimal && ((BigDecimal) src).compareTo(BigDecimal.ZERO) == 0) ||
                (src instanceof Collection && ((Collection<T>) src).isEmpty()) ||
                (src instanceof Map && ((Map<Object, Object>) src).isEmpty()))) {
            src = dst[i++];
        }
        return src;
    }

    /**
     * 如果isNotEmpty(src)==true，返回src，否则返回dst中第一个符合前述条件的参数
     *
     * @param src d
     * @param dst d
     * @return String
     */
    public static String convert(String src, String... dst) {
        int i = 0, l = dst.length;
        while (i < l && StringUtils.isEmpty(src)) {
            src = dst[i++];
        }
        return src;
    }

    public static int convert(String src, int v) {
        if (StringUtils.isNotEmpty(src)) {
            return Integer.parseInt(src);
        } else {
            return v;
        }
    }

    public static long convert(String src, long v) {
        if (StringUtils.isNotEmpty(src)) {
            return Long.parseLong(src);
        } else {
            return v;
        }
    }

    public static double convert(String src, double v) {
        if (StringUtils.isNotEmpty(src)) {
            return Double.parseDouble(src);
        } else {
            return v;
        }
    }

    public static float convert(String src, float v) {
        if (StringUtils.isNotEmpty(src)) {
            return Float.parseFloat(src);
        } else {
            return v;
        }
    }

    public static byte convert(String src, byte v) {
        if (StringUtils.isNotEmpty(src)) {
            return Byte.parseByte(src);
        } else {
            return v;
        }
    }

    public static Short convert(String src, short v) {
        if (StringUtils.isNotEmpty(src)) {
            return Short.parseShort(src);
        } else {
            return v;
        }
    }

    public static BigInteger convert(String src, BigInteger v) {
        if (StringUtils.isNotEmpty(src)) {
            return new BigInteger(src);
        } else {
            return v;
        }
    }

    public static BigDecimal convert(String src, BigDecimal v) {
        if (StringUtils.isNotEmpty(src)) {
            return new BigDecimal(src);
        } else {
            return v;
        }
    }

    public static boolean convert(String src, boolean v) {
        if (StringUtils.isNotEmpty(src)) {
            return Boolean.parseBoolean(src);
        } else {
            return v;
        }
    }

    public static String convertUnicodeString(String src) {
        if (FOUR_HEX_REGEX == null) {
            FOUR_HEX_REGEX = Pattern.compile("^[0-9a-fA-F]4$");
        }
        StringBuilder builder = new StringBuilder();
        for (int i = 0, l = src.length(); i < l; ) {
            char c = src.charAt(i);
            if (c == '\\' && src.charAt(i + 1) == 'u') {
                int offset = i + 2;
                int end = offset + 4;
                if (offset < l) {
                    if (end <= l) {
                        String sub = src.substring(offset, end);
                        if (FOUR_HEX_REGEX.matcher(sub).matches()) {
                            builder.append((char) Integer.parseInt(sub, 16));
                        } else {
                            builder.append("\\u");
                            builder.append(sub);
                        }
                        i = end;
                    } else {
                        builder.append("\\u");
                        for (; offset < l; offset++) {
                            builder.append(src.charAt(offset));
                        }
                        return builder.toString();
                    }
                } else {
                    builder.append("\\u");
                    return builder.toString();
                }
            } else {
                builder.append(c);
                i++;
            }
        }
        return builder.toString();
    }

    public static String convertStringToUnicode(String src, String charPrefix) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0, l = src.length(); i < l; i++) {
            char c = src.charAt(i);
            builder.append(charPrefix);
            String unicode = Integer.toHexString((int) c);
            switch (unicode.length()) {
                case 1:
                    builder.append("000");
                    break;
                case 2:
                    builder.append("00");
                    break;
                case 3:
                    builder.append("0");
                    break;
            }
            builder.append(unicode);
        }
        return builder.toString();
    }
}
