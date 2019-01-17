package cn.kidtop.framework.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class UUIDUtil {

    public static String[] chars = new String[]{"a", "b", "c", "d", "e", "f",
            "g", "h", "i", "j", "k", "l", "m", "n", "o", "p", "q", "r", "s",
            "t", "u", "v", "w", "x", "y", "z", "0", "1", "2", "3", "4", "5",
            "6", "7", "8", "9", "A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z"};

    /**
     * 8位短uuid
     *
     * @return
     * @author zhoup
     */
    public static String generateShortUuid() {
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        for (int i = 0; i < 8; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 0x3E]);
        }
        return shortBuffer.toString();

    }

    /**
     * 根据传入num生成不同位数uuid`
     *
     * @param num
     * @return
     * @author zhoup
     */
    public static String generateUuidFromNum(int num) {
        StringBuffer shortBuffer = new StringBuffer();
        String uuid = UUID.randomUUID().toString().replace("-", "");
        int index = 8;
        if (num > 0) {
            index = num;
        }
        for (int i = 0; i < index; i++) {
            String str = uuid.substring(i * 4, i * 4 + 4);
            int x = Integer.parseInt(str, 16);
            shortBuffer.append(chars[x % 0x3E]);
        }
        return shortBuffer.toString();
    }

    public static UUID getUUID() {
        return UUID.randomUUID();
    }

    @SuppressWarnings("rawtypes")
    public static String createShuffle(boolean numberFlag, int length) {
        List list = null;
        if (numberFlag) {
            String[] beforeShuffleForNumber = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9"};
            list = Arrays.asList(beforeShuffleForNumber);
        } else {
            String[] beforeShuffle = new String[]{"0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "A", "B", "C", "D",
                    "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X",
                    "Y", "Z"};
            list = Arrays.asList(beforeShuffle);
        }
        Collections.shuffle(list);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            sb.append(list.get(i));
        }
        String afterShuffle = sb.toString();
        String result = "";
        int intEnd = 5 + 4;
        if (length < 4) {
            result = afterShuffle.substring(5, intEnd);
        } else {
            intEnd = 3 + length;
            result = afterShuffle.substring(3, intEnd);
        }

        return result;
    }

    public static String createRandom(boolean numberFlag, int length) {
        String retStr = "";
        String strTable = numberFlag ? "1234567890" : "1234567890abcdefghijkmnpqrstuvwxyz";
        int len = strTable.length();
        boolean bDone = true;
        do {
            retStr = "";
            int count = 0;
            for (int i = 0; i < length; i++) {
                double dblR = Math.random() * len;
                int intR = (int) Math.floor(dblR);
                char c = strTable.charAt(intR);
                if (('0' <= c) && (c <= '9')) {
                    count++;
                }
                retStr += strTable.charAt(intR);
            }
            if (count >= 2) {
                bDone = false;
            }
        } while (bDone);
        return retStr;
    }

}
