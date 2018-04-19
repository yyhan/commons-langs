package com.cloudin.commons.langs.security;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

/**
 * rsa加解密、密钥解析工具类
 *
 * @author 小天
 * @version 1.0.0, 2017/10/18 0018 09:57
 */
public enum RsaUtils {
	
	/**
	 * 单例
	 */
	INSTANCE;
	
	private Logger logger = LoggerFactory.getLogger(getClass());
	
	public KeyFactory keyFactory;
	
	RsaUtils() {
		try {
			keyFactory = KeyFactory.getInstance("RSA");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
	
	public static final String MD5_WITH_RSA         = "MD5withRSA";
	public static final String SHA1_WITH_RSA        = "SHA1WithRSA";
	public static final String SHA256_WITH_RSA      = "SHA256WithRSA";
	public static final String SHA512_WITH_RSA      = "SHA512withRSA";
	public static final String RSA_ECB_PKCS1PADDING = "RSA/ECB/PKCS1Padding";
	
	/**
	 * 从base64字符串中加载 rsa PKCS#8格式 私钥
	 *
	 * @param priKeyBase64Str base64编码的rsa私钥字符串
	 *
	 * @return {@link RSAPrivateKey}
	 *
	 * @throws InvalidKeySpecException
	 */
	public RSAPrivateKey loadPKCS8PriKeyFromBase64(String priKeyBase64Str) throws InvalidKeySpecException {
		byte[] priKeyBytes = Base64.decodeBase64(priKeyBase64Str);
		return loadPKCS8PriKey(priKeyBytes);
	}
	
	/**
	 * 从byte数组加载rsa PKCS#8格式 私钥
	 *
	 * @param priKeyBytes 私钥字节数组
	 *
	 * @return {@link RSAPrivateKey}
	 *
	 * @throws InvalidKeySpecException
	 */
	public RSAPrivateKey loadPKCS8PriKey(byte[] priKeyBytes) throws InvalidKeySpecException {
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(priKeyBytes);
		return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
	}
	
	/**
	 * 从base64字符串中加载rsa公钥
	 *
	 * @param pubKeyBase64Str base64编码的rsa公钥字符串
	 *
	 * @return {@link RSAPublicKey}
	 *
	 * @throws InvalidKeySpecException
	 */
	public RSAPublicKey loadX509PubKeyFromBase64(String pubKeyBase64Str) throws InvalidKeySpecException {
		byte[] pubKeyBytes = Base64.decodeBase64(pubKeyBase64Str);
		return loadX509PubKey(pubKeyBytes);
	}
	
	/**
	 * 从byte数组加载rsa公钥
	 *
	 * @param pubKeyBytes 公钥字节数组
	 *
	 * @return {@link RSAPublicKey}
	 *
	 * @throws InvalidKeySpecException
	 */
	public RSAPublicKey loadX509PubKey(byte[] pubKeyBytes) throws InvalidKeySpecException {
		X509EncodedKeySpec keySpec = new X509EncodedKeySpec(pubKeyBytes);
		return (RSAPublicKey) keyFactory.generatePublic(keySpec);
	}
	
	/**
	 * rsa解密
	 *
	 * @param rsaPrivateKey   rsa私钥
	 * @param cipherTxtBase64 base64密文
	 *
	 * @return UTF-8 编码的明文
	 */
	public String decryptBase64String(RSAPrivateKey rsaPrivateKey, String cipherTxtBase64)
		throws IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, InvalidKeyException {
		return decryptBase64String(rsaPrivateKey, cipherTxtBase64, StandardCharsets.UTF_8, StandardCharsets.UTF_8);
	}
	
	/**
	 * rsa解密
	 *
	 * @param rsaPrivateKey    rsa私钥
	 * @param cipherTxtBase64  base64密文
	 * @param cipherTxtCharset 密文编码
	 * @param plainTxtCharset  解密后的明文编码
	 *
	 * @return plainTxtCharset 编码的明文
	 */
	public String decryptBase64String(RSAPrivateKey rsaPrivateKey, String cipherTxtBase64, Charset cipherTxtCharset,
		Charset plainTxtCharset)
		throws IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, InvalidKeyException {
		byte[] res = decrypt(rsaPrivateKey, Base64.decodeBase64(cipherTxtBase64.getBytes(cipherTxtCharset)));
		if (res == null) {
			return null;
		}
		return new String(res, plainTxtCharset);
	}
	
	/**
	 * rsa私钥解密
	 *
	 * @param rsaPrivateKey rsa私钥
	 * @param cipherTxt     明文
	 *
	 * @return 解密结果
	 */
	public byte[] decrypt(RSAPrivateKey rsaPrivateKey, byte[] cipherTxt)
		throws InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
		try {
			Cipher cipher = Cipher.getInstance(RSA_ECB_PKCS1PADDING);
			cipher.init(Cipher.DECRYPT_MODE, rsaPrivateKey);
			return cipher.doFinal(cipherTxt);
		} catch (NoSuchAlgorithmException e) {
			// 一般rsa算法都存在，忽略该异常
			logger.error("", e);
		}
		return null;
	}
	
	/**
	 * rsa加密
	 *
	 * @param rsaPublicKey rsa 公钥
	 * @param plainText    明文
	 *
	 * @return 加密结果
	 *
	 * @throws InvalidKeyException
	 * @throws NoSuchPaddingException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public byte[] encrypt(RSAPublicKey rsaPublicKey, String plainText)
		throws InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
		byte[] res = encrypt(rsaPublicKey, plainText.getBytes(StandardCharsets.UTF_8));
		if (res == null) {
			return null;
		}
		return res;
	}
	
	/**
	 * rsa加密
	 *
	 * @param rsaPublicKey rsa 公钥
	 * @param plainText    明文字节数组
	 *
	 * @return cipherTxtCharset 编码的base64密文
	 *
	 * @throws InvalidKeyException
	 * @throws NoSuchPaddingException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public String encryptToBase64(RSAPublicKey rsaPublicKey, byte[] plainText)
		throws InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
		return encryptToBase64(rsaPublicKey, plainText, StandardCharsets.UTF_8);
	}
	
	/**
	 * rsa加密
	 *
	 * @param rsaPublicKey rsa 公钥
	 * @param plainText    明文
	 *
	 * @return cipherTxtCharset 编码的base64密文
	 *
	 * @throws InvalidKeyException
	 * @throws NoSuchPaddingException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public String encryptToBase64(RSAPublicKey rsaPublicKey, String plainText)
		throws InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
		return encryptToBase64(rsaPublicKey, plainText, StandardCharsets.UTF_8, StandardCharsets.UTF_8);
	}
	
	/**
	 * rsa加密
	 *
	 * @param rsaPublicKey     rsa 公钥
	 * @param plainText        明文
	 * @param plainTxtCharset  明文编码
	 * @param cipherTxtCharset 加密结果转字符串的编码
	 *
	 * @return cipherTxtCharset 编码的base64密文
	 *
	 * @throws InvalidKeyException
	 * @throws NoSuchPaddingException
	 * @throws BadPaddingException
	 * @throws IllegalBlockSizeException
	 */
	public String encryptToBase64(RSAPublicKey rsaPublicKey, String plainText, Charset plainTxtCharset,
		Charset cipherTxtCharset)
		throws InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
		return encryptToBase64(rsaPublicKey, plainText.getBytes(plainTxtCharset), cipherTxtCharset);
	}
	
	/**
	 * rsa公钥加密
	 *
	 * @param rsaPublicKey     rsa公钥
	 * @param plainText        明文
	 * @param cipherTxtCharset 加密结果转字符串的编码
	 *
	 * @return 加密结果
	 */
	public String encryptToBase64(RSAPublicKey rsaPublicKey, byte[] plainText, Charset cipherTxtCharset)
		throws IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, InvalidKeyException {
		byte[] res = encrypt(rsaPublicKey, plainText);
		if (res == null) {
			return null;
		}
		return new String(Base64.encodeBase64(res), cipherTxtCharset);
	}
	
	/**
	 * rsa公钥加密
	 *
	 * @param rsaPublicKey rsa公钥
	 * @param plainText    明文字节数组
	 *
	 * @return 加密结果
	 */
	public byte[] encrypt(RSAPublicKey rsaPublicKey, byte[] plainText)
		throws InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
		try {
			Cipher cipher = Cipher.getInstance(RSA_ECB_PKCS1PADDING);
			cipher.init(Cipher.ENCRYPT_MODE, rsaPublicKey);
			return cipher.doFinal(plainText);
		} catch (NoSuchAlgorithmException e) {
			// 一般rsa算法都存在，忽略该异常
			logger.error("", e);
		}
		return null;
	}
}
