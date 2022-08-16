package com.dunkin.customer.Utils;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * Created by mital on 9/11/17.
 */

public class CO123QA {

    public static String compress(String string) throws IOException {
        ByteArrayOutputStream os = new ByteArrayOutputStream(string.length());
        GZIPOutputStream gos = new GZIPOutputStream(os);
        gos.write(string.getBytes());
        gos.close();
        byte[] compressed = os.toByteArray();
        os.close();
        return base64Compress(compressed);
    }

    private static String base64Compress(byte[] data) {
        return Base64.encodeToString(data, Base64.NO_WRAP);
    }





    private static byte[] baseGet(String data) {
        return Base64.decode(data, Base64.NO_WRAP);
    }

    public static String getData(String base64) throws IOException {
        byte[] compressed = baseGet(base64);
        final int BUFFER_SIZE = 32;
        ByteArrayInputStream is = new ByteArrayInputStream(compressed);
        GZIPInputStream gis = new GZIPInputStream(is, BUFFER_SIZE);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] data = new byte[BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = gis.read(data)) != -1) {
            baos.write(data, 0, bytesRead);
        }
        gis.close();
        return baos.toString("UTF-8");
    }


}
