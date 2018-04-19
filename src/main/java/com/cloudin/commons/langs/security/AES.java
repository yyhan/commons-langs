package com.cloudin.commons.langs.security;

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
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * AES 加解密工具类。
 * <p>
 * IV(Initialization Value)是一个初始值，对于CBC模式来说，它必须是随机选取并且需要保密的
 * 而且它的长度和密码分组相同(比如：对于AES 128为128位，即长度为16的byte类型数组)
 *
 * @author 小天
 * @version 1.0.0, 2018/3/28 0028 13:51
 */
public enum AES {
	
	AES_CBC_NOPADDING("AES", "CBC", "NoPadding"),
	AES_CBC_PKCS5PADDING("AES", "CBC", "PKCS5Padding"),
	AES_ECB_NOPADDING("AES", "ECB", "NoPadding"),
	AES_ECB_PKCS5PADDING("AES", "ECB", "PKCS5Padding");
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public String algorithm;
	
	public String cipherMode;
	public String paddingMode;
	
	public Charset defaultCharset = StandardCharsets.UTF_8;
	
	public static final String CIPHER_MODE_CBC = "CBC";
	public static final String CIPHER_MODE_ECB = "ECB";
	
	public String algorithmAndPadding;
	
	AES(String algorithm, String cipherMode, String paddingMode) {
		this.algorithm = algorithm;
		this.cipherMode = cipherMode;
		this.paddingMode = paddingMode;
		this.algorithmAndPadding = algorithm + "/" + cipherMode + "/" + paddingMode;
	}
	
	/**
	 *
	 * @param keyBytes
	 * @param ivBytes
	 * @return
	 * @throws IllegalBlockSizeException
	 * @throws BadPaddingException
	 * @throws InvalidAlgorithmParameterException
	 * @throws InvalidKeyException
	 */
	public byte[] encrypt(byte[] keyBytes, byte[] ivBytes)
		throws IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
		
		return encrypt(keyBytes, null, ivBytes);
	}
	
	/**
	 * 对指定内容使用指定秘钥进行 AES 加密，并返回加密后的字节数组
	 *
	 * @param keyBytes      aes密钥字节数组
	 * @param ivBytes       aes向量字节数组（只在 CBC 模式下有用）
	 * @param plainTxtBytes 待加密文本的字节数组
	 *
	 * @return 加密后的字节数组
	 *
	 * @throws InvalidAlgorithmParameterException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public byte[] encrypt(byte[] keyBytes, byte[] ivBytes, byte[] plainTxtBytes)
		throws IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
		
		try {
			Cipher cp = Cipher.getInstance(algorithmAndPadding);
			SecretKeySpec keySpec = new SecretKeySpec(keyBytes, algorithm);
			if(cipherMode.equals(CIPHER_MODE_ECB)) {
				cp.init(Cipher.ENCRYPT_MODE, keySpec);
			} else {
				IvParameterSpec ivpSpec = new IvParameterSpec(ivBytes);
				cp.init(Cipher.ENCRYPT_MODE, keySpec, ivpSpec);
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
	
	public byte[] encrypt(String key, String plainTxt)
		throws IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
		
		return encrypt(key, null, plainTxt, defaultCharset);
	}
	
	/**
	 * 对指定内容使用指定秘钥进行 AES 加密，并返回加密后的字节数组
	 *
	 * @param key      aes密钥
	 * @param iv       aes向量（只在 CBC 模式下有用）
	 * @param plainTxt 待加密的文本
	 *
	 * @return 加密后的字节数组
	 *
	 * @throws InvalidAlgorithmParameterException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public byte[] encrypt(String key, String iv, String plainTxt)
		throws IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
		
		return encrypt(key, iv, plainTxt, defaultCharset);
	}

	
	/**
	 * 对指定内容使用指定秘钥进行 AES 加密，并将加密结果转换为 base64 编码后返回
	 *
	 * @param key      aes密钥
	 * @param iv       aes向量（只在 CBC 模式下有用）
	 * @param plainTxt 待加密的文本
	 *
	 * @return 加密结果转换为 base64 编码后的字符串
	 *
	 * @throws InvalidAlgorithmParameterException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public String encryptToBase64(String key, String iv, String plainTxt)
		throws IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
		byte[] bytes = encrypt(key, iv, plainTxt, defaultCharset);
		if(bytes == null) {
			return null;
		}
		return Base64.encodeBase64String(bytes);
	}
	
	/**
	 * 对指定内容使用指定秘钥进行 AES 加密，并返回加密后的字节数组
	 *
	 * @param key      aes密钥
	 * @param iv       aes向量（只在 CBC 模式下有用）
	 * @param plainTxt 待加密的文本
	 * @param charset  密钥、向量、带加密文本的编码字符集
	 *
	 * @return 加密后的字节数组
	 *
	 * @throws InvalidAlgorithmParameterException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public byte[] encrypt(String key, String iv, String plainTxt, Charset charset)
		throws IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
		
		return encrypt(key.getBytes(charset), iv == null ? null : iv.getBytes(charset), plainTxt.getBytes(charset));
	}
	
	/**
	 * 对指定内容使用指定秘钥进行 AES 解密，并返回解密后的字节数组
	 *
	 * @param keyBytes          aes密钥字节数组
	 * @param ivBytes           aes向量字节数组（只在 CBC 模式下有用）
	 * @param encryptedTxtBytes 待解密文本的字节数组
	 *
	 * @return 解密后的字节数组
	 *
	 * @throws InvalidAlgorithmParameterException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public byte[] decrypt(byte[] keyBytes, byte[] ivBytes, byte[] encryptedTxtBytes)
		throws IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
		try {
			Cipher cp = Cipher.getInstance(algorithmAndPadding);
			SecretKeySpec keySpec = new SecretKeySpec(keyBytes, algorithm);
			if(cipherMode.equals(CIPHER_MODE_ECB)) {
				cp.init(Cipher.DECRYPT_MODE, keySpec);
			} else {
				IvParameterSpec ivpSpec = new IvParameterSpec(ivBytes);
				cp.init(Cipher.DECRYPT_MODE, keySpec, ivpSpec);
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
	
	/**
	 * 对指定内容使用指定秘钥进行 AES 解密，并返回解密后的字节数组
	 *
	 * @param key          aes密钥
	 * @param iv           aes向量（只在 CBC 模式下有用）
	 * @param encryptedTxt 待解密的文本
	 *
	 * @return 解密后的字节数组
	 *
	 * @throws InvalidAlgorithmParameterException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public byte[] decrypt(String key, String iv, String encryptedTxt)
		throws IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
		return decrypt(key, iv, encryptedTxt, defaultCharset);
	}
	
	/**
	 * 对指定内容使用指定秘钥进行 AES 解密，并返回解密后的字节数组
	 *
	 * @param key          aes密钥
	 * @param iv           aes向量（只在 CBC 模式下有用）
	 * @param encryptedTxt 待解密的文本
	 * @param charset      密钥、向量、带解密文本的编码字符集
	 *
	 * @return 解密后的字节数组
	 *
	 * @throws InvalidAlgorithmParameterException
	 * @throws InvalidKeyException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public byte[] decrypt(String key, String iv, String encryptedTxt, Charset charset)
		throws IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
		return decrypt(key.getBytes(charset), iv.getBytes(charset), encryptedTxt.getBytes(charset));
	}
	
	public String decryptToString(byte[] keyBytes, byte[] ivBytes, byte[] encryptedTxtBytes, Charset charset)
		throws IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
		
		byte[] bytes = decrypt(keyBytes, ivBytes, encryptedTxtBytes);
		if(bytes == null) {
			return null;
		}
		return new String(bytes, charset);
	}
	
	public String decryptToString(byte[] keyBytes, byte[] ivBytes, byte[] encryptedTxtBytes)
		throws IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
		return decryptToString(keyBytes, ivBytes, encryptedTxtBytes, defaultCharset);
	}
	
	public String decryptToString(String key, String iv, String encryptedTxt)
		throws IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
		return decryptToString(key, iv, encryptedTxt, defaultCharset);
	}
	
	public String decryptToString(String key, String iv, String encryptedTxt, Charset charset)
		throws IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException, InvalidKeyException {
		return decryptToString(key.getBytes(charset), iv.getBytes(charset), encryptedTxt.getBytes(charset));
	}
}
