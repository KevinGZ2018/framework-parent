package cn.kidtop.framework.util;

import cn.kidtop.framework.constant.SystemConstant;

import java.time.Instant;
import java.util.Date;

public class TokenUtil {
    public static String generate(String userId, String moblePhone, String deviceType, String systemType, Date lastLoginTime) {
        Instant instant = Instant.ofEpochMilli(lastLoginTime.getTime());
//		int i = instant.toString().length();
        //加密token
        String token = instant + "," + deviceType + "," + moblePhone + "," + userId;
        try {
            token = DESUtil.encrypt(token, SystemConstant.DES_KEY);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return token;

    }

    public static String[] parseUserIdFromToken(String token) {
        try {
            token = DESUtil.decrypt(token, SystemConstant.DES_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] tokens = token.split(",");
        return tokens;
    }
}
