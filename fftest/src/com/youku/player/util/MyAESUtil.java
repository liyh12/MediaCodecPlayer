package com.youku.player.util;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class MyAESUtil {

	// 密钥算法
	private static final String ALGORITHM = "AES/ECB/NoPadding";

	public static byte[] encrypt(String input, String key) {
		byte[] crypted = null;
		try {
			SecretKeySpec skey = new SecretKeySpec(key.getBytes(), "AES");
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			int blockSize = cipher.getBlockSize();
			byte[] dataBytes = input.getBytes();
			int plaintextLength = dataBytes.length;
			if (plaintextLength % blockSize != 0) {
				plaintextLength = plaintextLength
						+ (blockSize - (plaintextLength % blockSize));
			}
			byte[] plaintext = new byte[plaintextLength];
			for(int i=0;i<plaintextLength;i++){
				plaintext[i]=0x20;
			}
			System.arraycopy(dataBytes, 0, plaintext, 0, dataBytes.length);
			cipher.init(Cipher.ENCRYPT_MODE, skey);
			crypted = cipher.doFinal(plaintext);
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return crypted;
	}
}
