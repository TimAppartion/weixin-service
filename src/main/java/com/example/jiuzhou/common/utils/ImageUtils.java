package com.example.jiuzhou.common.utils;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.*;

/**
 * @author Appartion
 * @data 2022/10/20
 * 一入代码深似海，从此生活是路人
 */
public class ImageUtils {

    /**
     *  -> base64
     * @param imgFile
     * @return
     * @throws IOException
     */
    public static String getImageStr(String imgFile) throws IOException {
        InputStream inputStream = null;
        byte[] data = null;

        inputStream = new FileInputStream(imgFile);
        data = new byte[inputStream.available()];
        inputStream.read(data);
        inputStream.close();

        // 加密
        BASE64Encoder encoder = new BASE64Encoder();
        return encoder.encode(data);
    }
    /**
     * base64 ->
     * @param imgStr
     * @param path
     * @return
     * @throws IOException
     */
    public static boolean generateImage(String imgStr, String path) throws IOException {
        if (imgStr == null){
            return false;
        }
        BASE64Decoder decoder = new BASE64Decoder();

        //解密
        byte[] b = decoder.decodeBuffer(imgStr);
        // 处理数据
        for (int i = 0; i < b.length; ++i) {
            if (b[i] < 0) {
                b[i] += 256;
            }
        }
        OutputStream out = new FileOutputStream(path);
        out.write(b);
        out.flush();
        out.close();
        return true;
    }
}
