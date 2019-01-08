package com.static4u.netcar.utils;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ZipUtils {

    /**
     * 使用zip进行解压缩
     * @param compressedStr 压缩后的文本
     * @return 解压后的字符串
     */
    public static final String unzip(String compressedStr) {
        if (compressedStr == null) {
            return null;
        }

        ByteArrayOutputStream out = null;
        ByteArrayInputStream in = null;
        ZipInputStream zin = null;
        String decompressed = null;
        try {

            byte[] compressed = Base64.decode(compressedStr.getBytes(), Base64.DEFAULT);
            out = new ByteArrayOutputStream();
            in = new ByteArrayInputStream(compressed);
            zin = new ZipInputStream(in);
            ZipEntry entry = null;
            while ((entry = zin.getNextEntry()) != null) {
                if (entry.getName().equals("a")) {
                    byte[] buffer = new byte[1024];
                    int offset = -1;
                    while ((offset = zin.read(buffer)) != -1) {
                        out.write(buffer, 0, offset);
                    }
                    decompressed = out.toString();
                    break;
                }
            }


        } catch (IOException e) {
            decompressed = null;
        } finally {
            if (zin != null) {
                try {
                    zin.close();
                } catch (IOException e) {
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                }
            }
        }
        return decompressed;
    }
}