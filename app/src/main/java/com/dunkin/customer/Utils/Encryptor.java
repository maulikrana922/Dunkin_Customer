package com.dunkin.customer.Utils;

import android.content.Context;

import com.dunkin.customer.R;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

public class Encryptor {
    public static String encrypt(Context context, String Data) throws Exception {
        Key key = generateKey(context);
        Cipher c = Cipher.getInstance(context.getString(R.string.stral));
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        return new BASE64Encoder().encode(encVal);
    }

    public static String decrypt(Context context, String encryptedData) throws Exception {
        Key key = generateKey(context);
        Cipher c = Cipher.getInstance(context.getString(R.string.stral));
        c.init(Cipher.DECRYPT_MODE, key);
        byte[] decodedValue = new BASE64Decoder().decodeBuffer(encryptedData);
        byte[] decValue = c.doFinal(decodedValue);
        return new String(decValue);
    }

    private static Key generateKey(Context context) throws Exception {
        String keyValue = context.getString(R.string.strp);
        Key key = new SecretKeySpec(keyValue.getBytes(), context.getString(R.string.stral));
        return key;
    }
}
