package com.duangframework.utils;

import com.duangframework.exception.Exceptions;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


/**
 * 支持SHA-1/MD5消息摘要的工具类.
 * 
 * 返回ByteSource，可进一步被编码为Hex, Base64或UrlSafeBase64
 * 
 */
public class Digests {

	private static final String SHA1 = "SHA-1";
	private static final String MD5 = "MD5";
	private final static String[] hexDigits = { "0", "1", "2", "3", "4", "5", "6", "7", "8", "9", "a", "b", "c", "d", "e", "f" };
	private static SecureRandom random = new SecureRandom();

	/**
	 * 对输入字符串进行sha1散列.
	 */
	public static byte[] sha1(byte[] input) {
		return digest(input, SHA1, null, 1);
	}

	public static byte[] sha1(byte[] input, byte[] salt) {
		return digest(input, SHA1, salt, 1);
	}

	public static byte[] sha1(byte[] input, byte[] salt, int iterations) {
		return digest(input, SHA1, salt, iterations);
	}

	/**
	 * 对字符串进行散列, 支持md5与sha1算法.
	 */
	private static byte[] digest(byte[] input, String algorithm, byte[] salt, int iterations) {
		try {
			MessageDigest digest = MessageDigest.getInstance(algorithm);

			if (salt != null) {
				digest.update(salt);
			}

			byte[] result = digest.digest(input);

			for (int i = 1; i < iterations; i++) {
				digest.reset();
				result = digest.digest(result);
			}
			return result;
		} catch (GeneralSecurityException e) {
			throw Exceptions.unchecked(e);
		}
	}

	/**
	 * 生成随机的Byte[]作为salt.
	 * 
	 * @param numBytes
	 *            byte数组的大小
	 */
	public static byte[] generateSalt(int numBytes) {
		if(numBytes <= 0) {
			throw new RuntimeException("numBytes argument must be a positive integer (1 or larger)");
		}

		byte[] bytes = new byte[numBytes];
		random.nextBytes(bytes);
		return bytes;
	}

	/**
	 * 对文件进行md5散列.
	 */
	public static byte[] md5(InputStream input) throws IOException {
		return digest(input, MD5);
	}

	/**
	 * 进行md5散列.并返回字符串
	 */
	public static String md5toString(InputStream input) throws Exception {
		StringBuilder resultSb = new StringBuilder();
		byte[] bytes = md5(input);
		for (byte b : bytes) {
			resultSb.append(byteToHexString(b));
		}
		return resultSb.toString();
	}
	
	/**
	 * 进行sha1散列.并返回字符串
	 */
	public static String sha1toString(InputStream input) throws Exception {
		StringBuilder resultSb = new StringBuilder();
		byte[] bytes = sha1(input);
		for (byte b : bytes) {
			resultSb.append(byteToHexString(b));
		}
		return resultSb.toString();
	}

	/**
	 * 转换byte到16进制
	 * 
	 * @param b
	 *            要转换的byte
	 * @return 16进制格式
	 */
	private static String byteToHexString(byte b) {
		int n = b;
		if (n < 0) {
			n = 256 + n;
		}
		int d1 = n / 16;
		int d2 = n % 16;
		return hexDigits[d1] + hexDigits[d2];
	}

	/**
	 * 对文件进行sha1散列.
	 */
	public static byte[] sha1(InputStream input) throws IOException {
		return digest(input, SHA1);
	}

	private static byte[] digest(InputStream input, String algorithm) throws IOException {
		try {
			MessageDigest messageDigest = MessageDigest.getInstance(algorithm);
			int bufferLength = 8 * 1024;
			byte[] buffer = new byte[bufferLength];
			int read = input.read(buffer, 0, bufferLength);

			while (read > -1) {
				messageDigest.update(buffer, 0, read);
				read = input.read(buffer, 0, bufferLength);
			}

			return messageDigest.digest();
		} catch (GeneralSecurityException e) {
			throw new RuntimeException(e);
		}
	}


	/**
	 * 传入文本内容，返回 SHA-256 串
	 *
	 * @param strText
	 * @return
	 */
	public static String sha256(final String strText) {
//		return DigestUtils.sha256(strText).toString();
		return SHA(strText, "SHA-256");
	}

	/**
	 * 传入文本内容，返回 SHA-512 串
	 *
	 * @param strText
	 * @return
	 */
	public static String sha512(final String strText) {
//		return DigestUtils.sha512(strText).toString();
		return SHA(strText, "SHA-512");
	}


		/**
         * 字符串 SHA 加密
         *
         * @param strText
         * @param strType
         * @return
         */
	private static String SHA(final String strText, final String strType) {
		// 返回值
		String strResult = null;

		// 是否是有效字符串
		if (strText != null && strText.length() > 0) {
			try {
				// SHA 加密开始
				// 创建加密对象 并傳入加密類型
				MessageDigest messageDigest = MessageDigest.getInstance(strType);
				// 传入要加密的字符串
				messageDigest.update(strText.getBytes());
				// 得到 byte 類型结果
				byte byteBuffer[] = messageDigest.digest();

				// 將 byte 轉換爲 string
				StringBuffer strHexString = new StringBuffer();
				// 遍歷 byte buffer
				for (int i = 0; i < byteBuffer.length; i++) {
					String hex = Integer.toHexString(0xff & byteBuffer[i]);
					if (hex.length() == 1) {
						strHexString.append('0');
					}
					strHexString.append(hex);
				}
				// 得到返回結果
				strResult = strHexString.toString();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
		}
		return strResult;
	}
}
