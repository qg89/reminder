package com.q.reminder.reminder;

import org.springframework.util.StringUtils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class AESUtils {

    private static final String KEY_ALGORITHM = "AES";
    /**
     * 默认的加密算法
     */
    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";

    private static final String KEY_EMPTY_ERROR = "没有传入AES加密的加密秘钥";

    /**
     * 随机生成密钥
     *
     * @return
     */
    public static String getAESRandomKey() {
        SecureRandom random = new SecureRandom();
        long randomKey = random.nextLong();
        return String.valueOf(randomKey);
    }

    public static void main(String[] args) throws Exception {
        System.out.println(decrypt("YAU7VUQTQlBTP/D7DYdMI5A5u1zqyh85JI/hVpPjrLzCpZnihi+gruSUQTzq8hl9p8mY2czOCrvHMrNNn5hE2869zSbitJStXAKwXQKs6yzl2JhuHSDinw5IyD2v7td4WFtaEQNW8VYwfi1CT17S4/Zf44Yht4Y0Pm/gaRPgSokkAa2s5qe1tu3m1oF4nCFiWsPOwe2hWdR6b6hI5L1Dca0CZCVTVW4dBM+ceuRmoEHR68ILnwSrc+jk7STgvjU+aQ3O/dFxmN7D2qLattxa5wEJjr9Xg5hxczIrC75/SBfst0IOmzqpQdgOyplnL6VGc7KZmcqxKunLwVhdNZqpCQ==", "b9dde9d3d0a82d37"));
//        System.out.println(encrypt("LVSHFFFV5KF778384", "4313405756912339671"));
//        String string = new DateTime().toString("yyyy-MM-dd");
//        Holiday holiday = HolidayUtil.getHoliday(new DateTime().toString("yyyy-MM-dd"));
//        System.out.println(holiday.getName());
    }

    /**
     * 生成其他语言用AesKey
     * Base64格式
     * @param key
     * @return
     */
    public static String getGeneratorKey(String key) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(key.getBytes());
            kgen.init(128, random);
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            return byte2Base64(enCodeFormat);
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    /**
     * AES 加密操作
     *
     * @param content 待加密内容
     * @param key     加密密钥
     * @return 返回Base64转码后的加密数据
     */
    public static String encrypt(String content, String key) {
        try {
            // 创建密码器
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            byte[] byteContent = content.getBytes("utf-8");
            // 初始化为加密模式的密码器
            cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(key));
            // 加密
            byte[] result = cipher.doFinal(byteContent);
            //通过Base64转码返回
            return byte2Base64(result);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    public static String encryptAES256ECB(String key, String data) {
        if (StringUtils.isEmpty(key) || StringUtils.isEmpty(data)) {
            System.out.println(KEY_EMPTY_ERROR);
            throw new RuntimeException(KEY_EMPTY_ERROR);
        }
        try {
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec);
            byte[] res = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return org.apache.commons.codec.binary.Base64.encodeBase64String(res);
        } catch (Exception e) {
            System.out.println("encryptAES256ECB failed {},{}");
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * AES 解密操作
     *
     * @param content
     * @param key
     * @return
     */
    public static String decrypt(String content, String key) throws Exception{
        //实例化
        Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
        //使用密钥初始化，设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(key));
        //执行操作
        byte[] result = cipher.doFinal(base642Byte(content.replaceAll(" +","+")));
        return new String(result, StandardCharsets.UTF_8);
    }

    public static String decryptAES256ECB(String key, String data) {
        try {
            if (StringUtils.isEmpty(key) || (StringUtils.isEmpty(data) && org.apache.commons.codec.binary.Base64.decodeBase64(data).length == 0)) {
                System.out.println(KEY_EMPTY_ERROR);
                throw new RuntimeException(KEY_EMPTY_ERROR);
            }
            SecretKeySpec secretKeySpec = new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), KEY_ALGORITHM);
            Cipher cipher = Cipher.getInstance(DEFAULT_CIPHER_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec);
            byte[] res = cipher.doFinal(org.apache.commons.codec.binary.Base64.decodeBase64(data));
            return new String(res, StandardCharsets.UTF_8);
        } catch (Exception e) {
            System.out.println("decryptAES256ECB failed {},{}");
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * 字节数组转Base64编码
     *
     * @param bytes
     * @return
     */
    public static String byte2Base64(byte[] bytes) {
        Base64.Encoder encoder = Base64.getEncoder();
        return new String(encoder.encode(bytes));
    }

    /**
     * Base64编码转字节数组
     *
     * @param base64Key
     * @return
     */
    public static byte[] base642Byte(String base64Key) {
        Base64.Decoder decoder = Base64.getDecoder();
        return decoder.decode(base64Key);
    }

    /**
     * 生成加密秘钥
     *
     * @return
     */
    private static SecretKeySpec getSecretKey(final String key) {
        //返回生成指定算法密钥生成器的 KeyGenerator 对象
        try {
            KeyGenerator kg = KeyGenerator.getInstance(KEY_ALGORITHM);
            // 此类提供加密的强随机数生成器 (RNG)，该实现在windows上每次生成的key都相同，但是在部分linux或solaris系统上则不同。
            // SecureRandom random = new SecureRandom(key.getBytes());
            // 指定算法名称，不同的系统上生成的key是相同的。
            SecureRandom random = SecureRandom.getInstance("SHA1PRNG");
            random.setSeed(key.getBytes());
            //AES 要求密钥长度为 128
            kg.init(128, random);
            //生成一个密钥
            SecretKey secretKey = kg.generateKey();

            // 转换为AES专用密钥
            return new SecretKeySpec(secretKey.getEncoded(), KEY_ALGORITHM);
        } catch (NoSuchAlgorithmException ex) {
            ex.printStackTrace();
        }
        return null;
    }
}
