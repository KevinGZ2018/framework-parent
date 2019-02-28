package com.fushun.framework.util.util;

import com.fushun.framework.util.exception.exception.BusinessException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 二维码生成工具类
 *
 * @author Cloud
 * @data 2016-12-15
 * QRCode
 */

public class QRCodeUtil {

    //二维码颜色  
    private static final int BLACK = 0xFF000000;
    //二维码颜色  
    private static final int WHITE = 0xFFFFFFFF;

    /**
     * <span style="font-size:18px;font-weight:blod;">ZXing 方式生成二维码</span>
     *
     * @param text       <a href="javascript:void();">二维码内容</a>
     * @param width      二维码宽
     * @param height     二维码高
     * @param outPutPath 二维码生成保存路径
     * @param imageType  二维码生成格式  "JPEG"
     */
    public static void zxingCodeCreate(String text, int width, int height, String outPutPath, String imageType) {
        try {
            BufferedImage image = createQRcode(text, width, height);
            File outPutImage = new File(outPutPath);
            //如果图片不存在创建图片  
            if (!outPutImage.exists())
                outPutImage.createNewFile();
            //5、将二维码写入图片 
            ImageIO.write(image, imageType, outPutImage);
        } catch (IOException e) {
            throw new BusinessException(e, BusinessException.Enum.CREATED_QRCODE_ERROR_EXCEPTION);
        }
    }

    /**
     * 生成二维码
     *
     * @param text
     * @param width
     * @param height
     * @param imageType "JPEG"
     * @return 返回二维码的图片的字节
     * @date: 2017-11-09 18:58:26
     * @author:wangfushun
     * @version 1.0
     */
    public static byte[] zxingCodeCreate(String text, int width, int height, String imageType) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
//    	ImageInputStream imageInputStream=
        BufferedImage image = createQRcode(text, width, height);
        try {
            ImageIO.write(image, imageType, outputStream);
            outputStream.close();
            return outputStream.toByteArray(); //得到一个新的btye[],
        } catch (IOException e) {
            throw new BusinessException(e, BusinessException.Enum.CREATED_QRCODE_ERROR_EXCEPTION);
        }
    }

    /**
     * 生成二维码 对象
     *
     * @param text
     * @param width
     * @param height
     * @return
     * @date: 2017-11-09 16:32:51
     * @author:wangfushun
     * @version 1.0
     */
    private static BufferedImage createQRcode(String text, int width, int height) {
        Map<EncodeHintType, String> his = new HashMap<EncodeHintType, String>();
        //设置编码字符集  
        his.put(EncodeHintType.CHARACTER_SET, "utf-8");
        try {
            //1、生成二维码  
            BitMatrix encode = new MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, his);

            //2、获取二维码宽高  
            int codeWidth = encode.getWidth();
            int codeHeight = encode.getHeight();

            //3、将二维码放入缓冲流  
            BufferedImage image = new BufferedImage(codeWidth, codeHeight, BufferedImage.TYPE_INT_RGB);
            for (int i = 0; i < codeWidth; i++) {
                for (int j = 0; j < codeHeight; j++) {
                    //4、循环将二维码内容定入图片  
                    image.setRGB(i, j, encode.get(i, j) ? BLACK : WHITE);
                }
            }
            return image;
        } catch (WriterException e) {
            throw new BusinessException(e, BusinessException.Enum.CREATED_QRCODE_ERROR_EXCEPTION);
        }

    }

}