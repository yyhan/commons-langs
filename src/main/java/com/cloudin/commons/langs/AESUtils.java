package com.cloudin.commons.langs;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * AES加密解密工具包，默认使用 "AES/ECB/PKCS5Padding" 算法
 *
 * @author 小天
 * @version 1.0.0, 2017/10/25 0025 15:51
 */
public class AESUtils {
	
	private static Logger logger = LoggerFactory.getLogger(AESUtils.class);
	
	private static final String ALGORITHM  = "AES";
	private static final int    KEY_SIZE   = 128;
	private static final int    CACHE_SIZE = 1024;
	
	/**
	 * IV(Initialization Value)是一个初始值，对于CBC模式来说，它必须是随机选取并且需要保密的
	 * 而且它的长度和密码分组相同(比如：对于AES 128为128位，即长度为16的byte类型数组)
	 */
	public static final byte[] DEFAULT_IV = "0123456789abcdef".getBytes();
	
	public static final String AES_CBC_NOPADDING    = "AES/CBC/NoPadding";
	public static final String AES_CBC_PKCS5PADDING = "AES/CBC/PKCS5Padding";
	public static final String AES_ECB_NOPADDING    = "AES/ECB/NoPadding";
	public static final String AES_ECB_PKCS5PADDING = "AES/ECB/PKCS5Padding";
	
	/**
	 * 对指定文本进行AES加密，并返回加密后的base64编码文本
	 * <p>
	 * AES 算法： AES/ECB/PKCS5Padding 128位
	 *
	 * @param aesPwd   aes密钥(UTF-8)
	 * @param plainTxt 待加密文本(UTF-8)
	 *
	 * @return 解密后的字节数组
	 *
	 * @throws java.security.InvalidAlgorithmParameterException
	 * @throws java.security.InvalidKeyException
	 * @throws javax.crypto.BadPaddingException
	 * @throws javax.crypto.IllegalBlockSizeException
	 */
	public static String encryptToBase64(String aesPwd, String plainTxt)
		throws IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
		byte[] res = encrypt(aesPwd, plainTxt);
		if (res == null) {
			return null;
		} else {
			return Base64.encodeBase64String(res);
		}
	}
	
	/**
	 * 对指定文本进行AES加密，并返回加密后的字节数组
	 * <p>
	 * AES 算法： AES/ECB/PKCS5Padding 128位
	 *
	 * @param aesPwd   aes密钥(UTF-8)
	 * @param plainTxt 待加密文本(UTF-8)
	 *
	 * @return 解密后的字节数组
	 *
	 * @throws java.security.InvalidAlgorithmParameterException
	 * @throws java.security.InvalidKeyException
	 * @throws javax.crypto.BadPaddingException
	 * @throws javax.crypto.IllegalBlockSizeException
	 */
	public static byte[] encrypt(String aesPwd, String plainTxt)
		throws IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
		return encrypt(aesPwd.getBytes(Charsets.UTF_8), plainTxt.getBytes(Charsets.UTF_8));
	}
	
	/**
	 * 对指定文本进行AES加密，并返回加密后的字节数组
	 * <p>
	 * AES 算法： AES/ECB/PKCS5Padding 128位
	 *
	 * @param aesPwdBytes   aes密钥字节数组
	 * @param plainTxtBytes 待加密文本的字节数组
	 *
	 * @return 解密后的字节数组
	 *
	 * @throws java.security.InvalidAlgorithmParameterException
	 * @throws java.security.InvalidKeyException
	 * @throws javax.crypto.BadPaddingException
	 * @throws javax.crypto.IllegalBlockSizeException
	 */
	public static byte[] encrypt(byte[] aesPwdBytes, byte[] plainTxtBytes)
		throws IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
		return encrypt(aesPwdBytes, null, plainTxtBytes, AES_ECB_PKCS5PADDING);
	}
	
	/**
	 * 对指定文本进行AES加密，并返回加密后的字节数组
	 *
	 * @param aesPwdBytes         aes密钥字节数组
	 * @param aesIVBytes          aes向量字节数组
	 * @param plainTxtBytes       待加密文本的字节数组
	 * @param algorithmAndPadding 算法和填充方式
	 *
	 * @return 解密后的字节数组
	 *
	 * @throws java.security.InvalidAlgorithmParameterException
	 * @throws java.security.InvalidKeyException
	 * @throws javax.crypto.BadPaddingException
	 * @throws javax.crypto.IllegalBlockSizeException
	 */
	public static byte[] encrypt(byte[] aesPwdBytes, byte[] aesIVBytes, byte[] plainTxtBytes,
		String algorithmAndPadding)
		throws InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		try {
			Cipher cp = Cipher.getInstance(algorithmAndPadding);
			SecretKeySpec keySpec = new SecretKeySpec(aesPwdBytes, ALGORITHM);
			if (aesIVBytes != null) {
				IvParameterSpec ivpSpec = new IvParameterSpec(aesIVBytes);
				cp.init(Cipher.ENCRYPT_MODE, keySpec, ivpSpec);
			} else {
				cp.init(Cipher.ENCRYPT_MODE, keySpec);
			}
			return cp.doFinal(plainTxtBytes);
		} catch (NoSuchAlgorithmException e) {
			// 该异常可以忽略，java都提供AES算法
			logger.error("", e);
		} catch (NoSuchPaddingException e) {
			// 该异常可以忽略，java提供的AES算法支持“NoPadding”补码方式
			logger.error("", e);
		}
		return null;
	}
	
	/**
	 * 对指定内容使用指定秘钥进行AES解密，并返回解密后的字节数组
	 * <p>
	 * AES 算法： AES/ECB/PKCS5Padding 128位
	 *
	 * @param aesPwd             aes密钥(UTF-8)
	 * @param encryptedTxtBase64 base64编码的加密文本(UTF-8)
	 *
	 * @return 解密后的明文，采用指定的字符集编码
	 *
	 * @throws javax.crypto.IllegalBlockSizeException
	 * @throws javax.crypto.BadPaddingException
	 * @throws java.security.InvalidAlgorithmParameterException
	 * @throws java.security.InvalidKeyException
	 */
	public static String decryptBase64(String aesPwd, String encryptedTxtBase64)
		throws IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
		return decryptBase64(aesPwd, encryptedTxtBase64, Charsets.UTF_8);
	}
	
	/**
	 * 对指定内容使用指定秘钥进行AES解密，并返回解密后的字节数组
	 * <p>
	 * AES 算法： AES/ECB/PKCS5Padding 128位
	 *
	 * @param aesPwd             aes密钥
	 * @param encryptedTxtBase64 base64编码的加密文本
	 * @param charset            编码字符集
	 *
	 * @return 解密后的明文，采用指定的字符集编码
	 *
	 * @throws javax.crypto.IllegalBlockSizeException
	 * @throws javax.crypto.BadPaddingException
	 * @throws java.security.InvalidAlgorithmParameterException
	 * @throws java.security.InvalidKeyException
	 */
	public static String decryptBase64(String aesPwd, String encryptedTxtBase64, Charset charset)
		throws IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
		byte[] res = decrypt(aesPwd.getBytes(charset), Base64.decodeBase64(encryptedTxtBase64.getBytes(charset)));
		if (res == null) {
			return null;
		} else {
			return new String(res, charset);
		}
	}
	
	/**
	 * 对指定内容使用指定秘钥进行AES解密，并返回解密后的字节数组
	 * <p>
	 * AES 算法： AES/ECB/PKCS5Padding 128位
	 *
	 * @param aesPwdBytes       aes密钥字节数组
	 * @param encryptedTxtBytes 待解密文本的字节数组
	 *
	 * @return 解密后的字节数组
	 *
	 * @throws java.security.InvalidAlgorithmParameterException
	 * @throws java.security.InvalidKeyException
	 * @throws javax.crypto.BadPaddingException
	 * @throws javax.crypto.IllegalBlockSizeException
	 */
	public static byte[] decrypt(byte[] aesPwdBytes, byte[] encryptedTxtBytes)
		throws InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		return decrypt(aesPwdBytes, null, encryptedTxtBytes, AES_ECB_PKCS5PADDING);
	}
	
	/**
	 * 对指定内容使用指定秘钥进行AES解密，并返回解密后的字节数组
	 *
	 * @param aesPwdBytes         aes密钥字节数组
	 * @param aesIVBytes          aes向量字节数组
	 * @param encryptedTxtBytes   待解密文本的字节数组
	 * @param algorithmAndPadding 算法和填充方式
	 *
	 * @return 解密后的字节数组
	 *
	 * @throws java.security.InvalidAlgorithmParameterException
	 * @throws java.security.InvalidKeyException
	 * @throws javax.crypto.BadPaddingException
	 * @throws javax.crypto.IllegalBlockSizeException
	 */
	public static byte[] decrypt(byte[] aesPwdBytes, byte[] aesIVBytes, byte[] encryptedTxtBytes,
		String algorithmAndPadding)
		throws InvalidAlgorithmParameterException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
		try {
			Cipher cp = Cipher.getInstance(algorithmAndPadding);
			SecretKeySpec keySpec = new SecretKeySpec(aesPwdBytes, ALGORITHM);
			if (aesIVBytes != null) {
				IvParameterSpec ivpSpec = new IvParameterSpec(aesIVBytes);
				cp.init(Cipher.DECRYPT_MODE, keySpec, ivpSpec);
			} else {
				cp.init(Cipher.DECRYPT_MODE, keySpec);
			}
			return cp.doFinal(encryptedTxtBytes);
		} catch (NoSuchAlgorithmException e) {
			// 该异常可以忽略，java都提供AES算法
			logger.error("", e);
		} catch (NoSuchPaddingException e) {
			// 该异常可以忽略
			logger.error("", e);
		}
		return null;
	}
	
	public static void main(String[] args) {
		String aesPwd = "0123456789abcdef";
		String plainTxt = "0123456789abcdef 中国";
		try {
			String encryptedTxt = AESUtils.encryptToBase64(aesPwd, plainTxt);
			System.out.println(encryptedTxt);
			String decryptRes = AESUtils.decryptBase64(aesPwd, encryptedTxt);
			System.out.println(decryptRes);
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			e.printStackTrace();
		}
	}
}