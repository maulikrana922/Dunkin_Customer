package com.dunkin.customer.Utils;

import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

/**
 * Created by mital on 9/11/17.
 */

public class CO123QA {



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
