package com.cloudin.commons.langs;

import org.apache.commons.codec.binary.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import java.nio.charset.Charset;
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
	 * @return {@link java.security.interfaces.RSAPrivateKey}
	 *
	 * @throws java.security.spec.InvalidKeySpecException
	 */
	public RSAPrivateKey loadPriKeyFromBase64(String priKeyBase64Str) throws InvalidKeySpecException {
		byte[] priKeyBytes = Base64.decodeBase64(priKeyBase64Str);
		return loadPKCS8PriKeyFromBytes(priKeyBytes);
	}
	
	/**
	 * 从byte数组加载rsa PKCS#8格式 私钥
	 *
	 * @param priKeyBytes 私钥字节数组
	 *
	 * @return {@link java.security.interfaces.RSAPrivateKey}
	 *
	 * @throws java.security.spec.InvalidKeySpecException
	 */
	public RSAPrivateKey loadPKCS8PriKeyFromBytes(byte[] priKeyBytes) throws InvalidKeySpecException {
		PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(priKeyBytes);
		return (RSAPrivateKey) keyFactory.generatePrivate(keySpec);
	}
	
	/**
	 * 从base64字符串中加载rsa公钥
	 *
	 * @param pubKeyBase64Str base64编码的rsa公钥字符串
	 *
	 * @return {@link java.security.interfaces.RSAPublicKey}
	 *
	 * @throws java.security.spec.InvalidKeySpecException
	 */
	public RSAPublicKey loadPubKeyFromBase64(String pubKeyBase64Str) throws InvalidKeySpecException {
		byte[] pubKeyBytes = Base64.decodeBase64(pubKeyBase64Str);
		return loadPubKeyFromBytes(pubKeyBytes);
	}
	
	/**
	 * 从byte数组加载rsa公钥
	 *
	 * @param pubKeyBytes 公钥字节数组
	 *
	 * @return {@link java.security.interfaces.RSAPublicKey}
	 *
	 * @throws java.security.spec.InvalidKeySpecException
	 */
	public RSAPublicKey loadPubKeyFromBytes(byte[] pubKeyBytes) throws InvalidKeySpecException {
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
	public String decrypt(RSAPrivateKey rsaPrivateKey, String cipherTxtBase64)
		throws IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, InvalidKeyException {
		byte[] res = decrypt(rsaPrivateKey, Base64.decodeBase64(cipherTxtBase64.getBytes(Charsets.UTF_8)));
		if (res == null) {
			return null;
		}
		return new String(res, Charsets.UTF_8);
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
	public String decrypt(RSAPrivateKey rsaPrivateKey, String cipherTxtBase64, Charset cipherTxtCharset,
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
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * rsa加密
	 *
	 * @param rsaPublicKey rsa 公钥
	 * @param plainText    明文
	 *
	 * @return UTF-8 编码的base64密文
	 *
	 * @throws java.security.InvalidKeyException
	 * @throws javax.crypto.NoSuchPaddingException
	 * @throws javax.crypto.BadPaddingException
	 * @throws javax.crypto.IllegalBlockSizeException
	 */
	public String encrypt(RSAPublicKey rsaPublicKey, String plainText)
		throws InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
		byte[] res = encrypt(rsaPublicKey, plainText.getBytes(Charsets.UTF_8));
		if (res == null) {
			return null;
		}
		return new String(Base64.encodeBase64(res), Charsets.UTF_8);
	}
	
	/**
	 * rsa加密
	 *
	 * @param rsaPublicKey     rsa 公钥
	 * @param plainText        明文
	 * @param plainTxtCharset  明文编码
	 * @param cipherTxtCharset 密文编码
	 *
	 * @return cipherTxtCharset 编码的base64密文
	 *
	 * @throws java.security.InvalidKeyException
	 * @throws javax.crypto.NoSuchPaddingException
	 * @throws javax.crypto.BadPaddingException
	 * @throws javax.crypto.IllegalBlockSizeException
	 */
	public String encrypt(RSAPublicKey rsaPublicKey, String plainText, Charset plainTxtCharset,
		Charset cipherTxtCharset)
		throws InvalidKeyException, NoSuchPaddingException, BadPaddingException, IllegalBlockSizeException {
		byte[] res = encrypt(rsaPublicKey, plainText.getBytes(plainTxtCharset));
		if (res == null) {
			return null;
		}
		return new String(Base64.encodeBase64(res), cipherTxtCharset);
	}
	
	/**
	 * rsa公钥加密
	 *
	 * @param rsaPublicKey rsa公钥
	 * @param plainText    明文
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
			e.printStackTrace();
		}
		return null;
	}
	
	public static void main(String[] args) throws InvalidKeySpecException, InvalidKeyException, SignatureException {
		String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJy+dhFO0w8CemAS" + "m6H6ttPh++k7p3W95ZqoHeaFtYaJPqVZd+w7xYplU2l3VLnjNmpzmxbUIDCt1cBt" + "ezwwE+tEm33ynIGICfUDcR1vnYARGey+aoAcIExS8SsSyZarA7gtu0ZqHC2zpayP" + "oOB6akyrt+wSxI2yCFzCKsyF6N9TAgMBAAECgYALjeKoLfZS8ezRbi24yAF4Jv1p" + "2PLkNAZb2y6xEoQqEa3jhG/ZoyJRrFx0ts4xEbWuLS1uhl6FGBtcnvVpn2WoYYzG" + "K6LR9y/TLfoUGHB3p8ZGwaiTST07+Y3rEDxVx4x6l711qJUFeUSI5DlgE6Xa6bCy" + "c9Yli6UN7yK4WGqwUQJBANCU1BF5xuhavk4GUevNnFl49FYtirOkdyzQCi/joy0K" + "ClloX/f53d/ZuSTCZB9Q8aKLbVxtDf1Z30o3jSxI/isCQQDAYMNmP/K9sEENa/T6" + "v0xcpY1r7g7M+vJpRlk1LBxNKCUMOga3F6hcMgDWRJVrXvTJf8oXH/Glz/Mm1tGZ" + "KLd5AkEAmtOOozY4vqr1DifE0Xs7LnMJM5r0lSpkyB/ZH/kiW8FTM1C1w/V2i74q" + "ny2oclJ8OsKyN+Q/eSsoAwg/Q3sfJwJATqwDnKAQM9SNdeCtSZHLt6OYRwgOk3MY" + "iUbUmnMUi9Ub+XkNX/jTyAhG4Vkgxc0KTBDvYy+UEdwNstds1baZ+QJBAIPYZjX8" + "e/9Ijs1W7sFM86yixP+MozyHK3o9cD0bayRn1gzU7uDZns4ONhBh0Jca5SwRqA8g" + "GyfZfejIWahwLTg=";
		String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCcvnYRTtMPAnpgEpuh+rbT4fvp" + "O6d1veWaqB3mhbWGiT6lWXfsO8WKZVNpd1S54zZqc5sW1CAwrdXAbXs8MBPrRJt9" + "8pyBiAn1A3Edb52AERnsvmqAHCBMUvErEsmWqwO4LbtGahwts6Wsj6DgempMq7fs" + "EsSNsghcwirMhejfUwIDAQAB";
		
		System.out.println(privateKey);
		System.out.println(publicKey);
		
		String plainText = "hello world";
		
		RSAPrivateKey rsaPrivateKey = RsaUtils.INSTANCE.loadPriKeyFromBase64(privateKey);
		RSAPublicKey rsaPublicKey = RsaUtils.INSTANCE.loadPubKeyFromBase64(publicKey);
		
		try {
			String cipherTxt = RsaUtils.INSTANCE.encrypt(rsaPublicKey, plainText);
			System.out.println(cipherTxt);
			System.out.println(RsaUtils.INSTANCE.decrypt(rsaPrivateKey, cipherTxt));
		} catch (NoSuchPaddingException e) {
			e.printStackTrace();
		} catch (BadPaddingException e) {
			e.printStackTrace();
		} catch (IllegalBlockSizeException e) {
			e.printStackTrace();
		}
	}
	
}
