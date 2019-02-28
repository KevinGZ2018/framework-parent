package com.fushun.framework.util.util;

public class SkuNumUtil {

    /**
     * sku+1
     * 转换sku为16位
     *
     * @param sku
     * @return
     */
    public static String getLastSku(String sku, int skuLength) {
        Integer num = Integer.parseInt(sku, 16);
        num++;
        sku = Integer.toHexString(num);
        int len = skuLength - sku.length();
        for (int i = len; i > 0; i--) {
            sku = "0" + sku;
        }

        return sku;
    }


    public static void main(String[] args) {
        String sku = getLastSku("01", 5);
        System.out.println(sku);
    }
}
