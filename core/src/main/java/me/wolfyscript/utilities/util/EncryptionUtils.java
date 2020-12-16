package me.wolfyscript.utilities.util;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.handler.codec.base64.Base64;

import java.nio.charset.StandardCharsets;
import java.util.Random;

public class EncryptionUtils {

    /**
     * Encodes String with Base64
     *
     * @param str the String to be encoded
     * @return the Base64 encoded String
     */
    public static String getBase64EncodedString(String str) {
        ByteBuf byteBuf = null;
        ByteBuf encodedByteBuf = null;
        String var3;
        try {
            byteBuf = Unpooled.wrappedBuffer(str.getBytes(StandardCharsets.UTF_8));
            encodedByteBuf = Base64.encode(byteBuf);
            var3 = encodedByteBuf.toString(StandardCharsets.UTF_8);
        } finally {
            if (byteBuf != null) {
                byteBuf.release();
                if (encodedByteBuf != null) {
                    encodedByteBuf.release();
                }
            }
        }
        return var3;
    }

    public static String getCode() {
        Random random = new Random();
        String alphabet = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        final int x = alphabet.length();
        StringBuilder sB = new StringBuilder();
        for (int i = 0; i < 16; i++) {
            sB.append(alphabet.charAt(random.nextInt(x)));
        }
        return sB.toString();
    }
}
