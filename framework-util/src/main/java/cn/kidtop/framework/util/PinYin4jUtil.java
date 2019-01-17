package cn.kidtop.framework.util;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.HanyuPinyinVCharType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PinYin4jUtil {

    /**
     * 将汉字转换为全拼
     * 包括重音字
     * 例如：重音：全拼：zhongyin,chongyin，首字母：zy,cy
     *
     * @param str
     * @return 字符为空，则返回null
     * @author fushun
     * @version
     * @creation 2016年2月22日
     */
    public static HashMap<pinYinEnum, String> getAllPingYin(String str) {
        if (StringUtils.isEmpty(str)) {
            return null;
        }
        char[] strChars = str.toCharArray();
        int strCharsLength = strChars.length;
        HanyuPinyinOutputFormat hanyuPinyinOutputFormat = new HanyuPinyinOutputFormat();

        hanyuPinyinOutputFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        hanyuPinyinOutputFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        hanyuPinyinOutputFormat.setVCharType(HanyuPinyinVCharType.WITH_V);
        ArrayList<String[]> strPinYinList = new ArrayList<String[]>();


        try {
            for (int i = 0; i < strCharsLength; i++) {
                // 判断是否为汉字字符
                if (java.lang.Character.toString(strChars[i]).matches("[\\u4E00-\\u9FA5]+")) {
                    String[] strPinYins = PinyinHelper.toHanyuPinyinStringArray(strChars[i], hanyuPinyinOutputFormat);
                    strPinYinList.add(strPinYins);
                } else {
                    strPinYinList.add(new String[]{java.lang.Character.toString(strChars[i])});
                }
            }
        } catch (BadHanyuPinyinOutputFormatCombination e1) {
            e1.printStackTrace();
        }
        return handleStrPinYinList(strPinYinList);
    }

    /**
     * 处理汉子拼音为字符串，
     * 包括重音字
     * 例如：重音：全拼：zhongyin,chongyin，首字母：zy,cy
     *
     * @param strPinYinList
     * @return
     * @author fushun
     * @version
     * @creation 2016年2月22日
     */
    private static HashMap<pinYinEnum, String> handleStrPinYinList(ArrayList<String[]> strPinYinList) {
        HashMap<pinYinEnum, String> pinYinMap = new HashMap<PinYin4jUtil.pinYinEnum, String>();

        List<String> allPinYinList = new ArrayList<String>();
        List<String> firstCharPinYinList = new ArrayList<String>();
        int strPinYinListSize = strPinYinList.size();
        boolean strPinYinListAdd = false;//第一层数组的index是否已改变
        for (int j = 0; j < strPinYinListSize; j++) {
            String oldAllPinYinStr = "";//上一次拼接的拼音      重音拼接的时候，第二个重音字会拼接到第一个重音字上
            String oldFirstCharPinYinStr = "";
            if (allPinYinList.size() > 0) {
                oldAllPinYinStr = allPinYinList.get(allPinYinList.size() - 1);
                oldFirstCharPinYinStr = firstCharPinYinList.get(firstCharPinYinList.size() - 1);
            }

            for (String str : strPinYinList.get(j)) {
                //全拼音总数
                int allPinYinListSize = allPinYinList.size();

                //
                for (int i = 0; i < allPinYinListSize && strPinYinListAdd == false; i++) {
                    allPinYinList.set(i, allPinYinList.get(i) + str);
                    firstCharPinYinList.set(i, firstCharPinYinList.get(i) + str.charAt(0));
                }
                //存在重音字
                if (strPinYinList.get(j).length > 1 || allPinYinListSize == 0) {
                    if (allPinYinList.size() > 0) {
                        allPinYinList.add(oldAllPinYinStr + str);
                        firstCharPinYinList.add(oldFirstCharPinYinStr + str.charAt(0));
                    }
                    if (allPinYinList.size() == 0) {
                        allPinYinList.add(str);
                    }
                    if (firstCharPinYinList.size() == 0) {
                        firstCharPinYinList.add(java.lang.Character.toString(str.charAt(0)));
                    }
                }
                strPinYinListAdd = true;
            }
            strPinYinListAdd = false;
        }

        //使用,号把重音的拼音连接起来
        String allPinYinStr = "";
        String firstCharPinYinStr = "";
        for (int i = 0; i < allPinYinList.size(); i++) {
            if (i > 0) {
                allPinYinStr += ",";
                firstCharPinYinStr += ",";
            }
            allPinYinStr += allPinYinList.get(i);
            firstCharPinYinStr += firstCharPinYinList.get(i);
        }
        pinYinMap.put(PinYin4jUtil.pinYinEnum.All_PIN_YIN, allPinYinStr);
        pinYinMap.put(PinYin4jUtil.pinYinEnum.FIRST_CHAR_PIN_YIN, firstCharPinYinStr);

        return pinYinMap;
    }

    public static void main(String[] args) {
        System.out.println(getAllPingYin("重音重音"));
    }


    public enum pinYinEnum {
        All_PIN_YIN, FIRST_CHAR_PIN_YIN
    }
}
