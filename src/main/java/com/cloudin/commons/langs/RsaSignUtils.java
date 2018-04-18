package com.cloudin.commons.langs;

import org.apache.commons.codec.binary.Base64;

import java.nio.charset.Charset;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;

/**
 * RSA 签名工具类
 *
 * @author 小天
 * @version 1.0.0, 2017/10/18 0018 09:57
 */
public enum RsaSignUtils {
	
	NONEwithRSA("NONEwithRSA"),
	MD5withRSA("MD5withRSA"),
	SHA1withRSA("SHA1withRSA"),
	SHA256withRSA("SHA256withRSA"),
	SHA512withRSA("SHA512withRSA");
	
	/**
	 * rsa签名算法标准名称
	 */
	public String algorithm;
	
	RsaSignUtils(String algorithm) {
		this.algorithm = algorithm;
	}
	
	/**
	 * 使用指定的算法对文本进行rsa签名
	 *
	 * @param privateKey base64编码的rsa私钥字符串
	 * @param plainTxt   待签名文本
	 * @param charset    签名结果编码 {@link java.nio.charset.Charset}
	 *
	 * @return 签名结果base64字符串
	 *
	 * @throws java.security.spec.InvalidKeySpecException
	 * @throws java.security.InvalidKeyException
	 * @throws java.security.SignatureException
	 */
	public String signToBase64(String privateKey, String plainTxt, Charset charset)
		throws InvalidKeySpecException, InvalidKeyException, SignatureException {
		RSAPrivateKey rsaPrivateKey = RsaUtils.INSTANCE.loadPriKeyFromBase64(privateKey);
		byte[] res = sign(rsaPrivateKey, plainTxt.getBytes(charset));
		if (res == null) {
			return null;
		}
		return new String(Base64.encodeBase64(res), charset);
	}
	
	/**
	 * 使用指定的算法对文本进行rsa签名
	 *
	 * @param privateKey base64编码的rsa私钥字符串
	 * @param plainTxt   待签名文本
	 *
	 * @return 签名结果base64字符串
	 *
	 * @throws java.security.spec.InvalidKeySpecException
	 * @throws java.security.InvalidKeyException
	 * @throws java.security.SignatureException
	 */
	public String signToBase64(String privateKey, String plainTxt)
		throws InvalidKeySpecException, InvalidKeyException, SignatureException {
		return signToBase64(privateKey, plainTxt, Charsets.UTF_8);
	}
	
	/**
	 * 使用指定的算法对文本进行rsa签名
	 *
	 * @param privateKey rsa 私钥
	 * @param plainTxt   utf8 编码的待签名文本
	 *
	 * @return 签名结果base64字符串
	 *
	 * @throws java.security.spec.InvalidKeySpecException
	 * @throws java.security.InvalidKeyException
	 * @throws java.security.SignatureException
	 */
	public String signToBase64(RSAPrivateKey privateKey, String plainTxt)
		throws InvalidKeySpecException, InvalidKeyException, SignatureException {
		return signToBase64(privateKey, plainTxt, Charsets.UTF_8);
	}
	
	/**
	 * 使用指定的算法对文本进行rsa签名
	 *
	 * @param privateKey rsa 私钥
	 * @param plainTxt   待签名文本
	 * @param charset    签名结果编码 {@link java.nio.charset.Charset}
	 *
	 * @return 签名结果base64字符串
	 *
	 * @throws java.security.spec.InvalidKeySpecException
	 * @throws java.security.InvalidKeyException
	 * @throws java.security.SignatureException
	 */
	public String signToBase64(RSAPrivateKey privateKey, String plainTxt, Charset charset)
		throws InvalidKeySpecException, InvalidKeyException, SignatureException {
		byte[] res = sign(privateKey, plainTxt.getBytes(charset));
		if (res == null) {
			return null;
		}
		return new String(Base64.encodeBase64(res), charset);
	}
	
	/**
	 * rsa签名
	 *
	 * @param privateKey    rsa 私钥
	 * @param plainTxtBytes 待签名文本字节数组
	 *
	 * @return 签名结果
	 *
	 * @throws java.security.spec.InvalidKeySpecException
	 * @throws java.security.InvalidKeyException
	 * @throws java.security.SignatureException
	 */
	public byte[] sign(RSAPrivateKey privateKey, byte[] plainTxtBytes)
		throws InvalidKeySpecException, InvalidKeyException, SignatureException {
		try {
			Signature signature = Signature.getInstance(algorithm);
			signature.initSign(privateKey);
			signature.update(plainTxtBytes);
			return signature.sign();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * 验证签名
	 *
	 * @param pubKeyBytes        rsa 公钥字符串
	 * @param plainTxt           待验证签名的明文
	 * @param signatureBase64Str base64编码的签名字符串
	 *
	 * @return 验签成功 或 失败
	 *
	 * @throws java.security.spec.InvalidKeySpecException
	 * @throws java.security.InvalidKeyException
	 * @throws java.security.SignatureException
	 */
	public boolean verify(byte[] pubKeyBytes, String plainTxt, String signatureBase64Str)
		throws InvalidKeySpecException, InvalidKeyException, SignatureException {
		RSAPublicKey rsaPublicKey = RsaUtils.INSTANCE.loadPubKeyFromBytes(pubKeyBytes);
		return verify(rsaPublicKey, plainTxt, signatureBase64Str, Charsets.UTF_8);
	}
	
	/**
	 * 验证签名
	 *
	 * @param publicKeyStr       rsa 公钥字符串
	 * @param plainTxt           待验证签名的明文
	 * @param signatureBase64Str base64编码的签名字符串
	 *
	 * @return 验签成功 或 失败
	 *
	 * @throws java.security.spec.InvalidKeySpecException
	 * @throws java.security.InvalidKeyException
	 * @throws java.security.SignatureException
	 */
	public boolean verify(String publicKeyStr, String plainTxt, String signatureBase64Str)
		throws InvalidKeySpecException, InvalidKeyException, SignatureException {
		RSAPublicKey rsaPublicKey = RsaUtils.INSTANCE.loadPubKeyFromBase64(publicKeyStr);
		return verify(rsaPublicKey, plainTxt, signatureBase64Str, Charsets.UTF_8);
	}
	
	/**
	 * 验证签名
	 *
	 * @param rsaPublicKey       rsa 公钥
	 * @param plainTxt           待验证签名的明文
	 * @param signatureBase64Str base64编码的签名字符串
	 *
	 * @return 验签成功 或 失败
	 *
	 * @throws java.security.spec.InvalidKeySpecException
	 * @throws java.security.InvalidKeyException
	 * @throws java.security.SignatureException
	 */
	public boolean verify(RSAPublicKey rsaPublicKey, String plainTxt, String signatureBase64Str)
		throws InvalidKeySpecException, InvalidKeyException, SignatureException {
		return verify(rsaPublicKey, plainTxt, signatureBase64Str, Charsets.UTF_8);
	}
	
	/**
	 * 验证签名
	 *
	 * @param rsaPublicKey       rsa 公钥
	 * @param plainTxt           待验证签名的明文
	 * @param signatureBase64Str base64编码的签名字符串
	 * @param plainTxtCharset    明文编码
	 *
	 * @return 验签成功 或 失败
	 *
	 * @throws java.security.spec.InvalidKeySpecException
	 * @throws java.security.InvalidKeyException
	 * @throws java.security.SignatureException
	 */
	public boolean verify(RSAPublicKey rsaPublicKey, String plainTxt, String signatureBase64Str,
		Charset plainTxtCharset) throws InvalidKeySpecException, InvalidKeyException, SignatureException {
		return verify(rsaPublicKey, plainTxt.getBytes(plainTxtCharset), Base64.decodeBase64(signatureBase64Str));
	}
	
	/**
	 * 验证签名
	 *
	 * @param rsaPublicKey   rsa 公钥
	 * @param plainTxtBytes  明文字节数组
	 * @param signatureBytes 签名字节数组
	 *
	 * @return 验签成功 或 失败
	 *
	 * @throws java.security.spec.InvalidKeySpecException
	 * @throws java.security.InvalidKeyException
	 * @throws java.security.SignatureException
	 */
	public boolean verify(RSAPublicKey rsaPublicKey, byte[] plainTxtBytes, byte[] signatureBytes)
		throws InvalidKeySpecException, InvalidKeyException, SignatureException {
		try {
			Signature signature = Signature.getInstance(algorithm);
			signature.initVerify(rsaPublicKey);
			signature.update(plainTxtBytes);
			return signature.verify(signatureBytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public static void main(String[] args) throws InvalidKeySpecException, InvalidKeyException, SignatureException {
		String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAJy+dhFO0w8CemAS" + "m6H6ttPh++k7p3W95ZqoHeaFtYaJPqVZd+w7xYplU2l3VLnjNmpzmxbUIDCt1cBt" + "ezwwE+tEm33ynIGICfUDcR1vnYARGey+aoAcIExS8SsSyZarA7gtu0ZqHC2zpayP" + "oOB6akyrt+wSxI2yCFzCKsyF6N9TAgMBAAECgYALjeKoLfZS8ezRbi24yAF4Jv1p" + "2PLkNAZb2y6xEoQqEa3jhG/ZoyJRrFx0ts4xEbWuLS1uhl6FGBtcnvVpn2WoYYzG" + "K6LR9y/TLfoUGHB3p8ZGwaiTST07+Y3rEDxVx4x6l711qJUFeUSI5DlgE6Xa6bCy" + "c9Yli6UN7yK4WGqwUQJBANCU1BF5xuhavk4GUevNnFl49FYtirOkdyzQCi/joy0K" + "ClloX/f53d/ZuSTCZB9Q8aKLbVxtDf1Z30o3jSxI/isCQQDAYMNmP/K9sEENa/T6" + "v0xcpY1r7g7M+vJpRlk1LBxNKCUMOga3F6hcMgDWRJVrXvTJf8oXH/Glz/Mm1tGZ" + "KLd5AkEAmtOOozY4vqr1DifE0Xs7LnMJM5r0lSpkyB/ZH/kiW8FTM1C1w/V2i74q" + "ny2oclJ8OsKyN+Q/eSsoAwg/Q3sfJwJATqwDnKAQM9SNdeCtSZHLt6OYRwgOk3MY" + "iUbUmnMUi9Ub+XkNX/jTyAhG4Vkgxc0KTBDvYy+UEdwNstds1baZ+QJBAIPYZjX8" + "e/9Ijs1W7sFM86yixP+MozyHK3o9cD0bayRn1gzU7uDZns4ONhBh0Jca5SwRqA8g" + "GyfZfejIWahwLTg=";
		String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCcvnYRTtMPAnpgEpuh+rbT4fvp" + "O6d1veWaqB3mhbWGiT6lWXfsO8WKZVNpd1S54zZqc5sW1CAwrdXAbXs8MBPrRJt9" + "8pyBiAn1A3Edb52AERnsvmqAHCBMUvErEsmWqwO4LbtGahwts6Wsj6DgempMq7fs" + "EsSNsghcwirMhejfUwIDAQAB";
		
		
		
		String plainText = "hello world";
		String signature = "";
		boolean verifyRes = false;
		
		signature = RsaSignUtils.SHA256withRSA.signToBase64(privateKey, plainText);
		System.out.println(signature);
		verifyRes = RsaSignUtils.SHA256withRSA.verify(publicKey, plainText, signature);
		System.out.println(verifyRes);
		
		plainText = "{\"method\":\"sms.code.send\",\"token\":\"\",\"version\":\"1.0.0\",\"sign\":\"\",\"body\":{\"mobile\":\"18839000327\",\"type\":1}}";
		
		signature = RsaSignUtils.SHA256withRSA.signToBase64(privateKey, plainText);
		System.out.println(signature);
		verifyRes = RsaSignUtils.SHA256withRSA.verify(publicKey, plainText, signature);
		System.out.println(verifyRes);
		
		// {"body":{"signature":"bde768b9f930f5581d5e4b71a3e50fa012a94abe7687ac94f1377c81e44f679f"},"code":0,"message":"success","success":true}
		
		
		plainText = "{\"method\":\"sms.code.send\",\"token\":\"\",\"version\":\"1.0.0\",\"sign\":\"\",\"body\":{\"mobile\":\"18839000327\",\"type\":1}}";
		
		signature = RsaSignUtils.SHA256withRSA.signToBase64(privateKey, plainText);
		System.out.println(signature);
		verifyRes = RsaSignUtils.SHA256withRSA.verify(publicKey, plainText, signature);
		System.out.println(verifyRes);
	}
	
}
