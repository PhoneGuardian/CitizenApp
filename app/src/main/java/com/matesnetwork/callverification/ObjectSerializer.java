package com.matesnetwork.callverification;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class ObjectSerializer {

	// private static final Log log = LogFactory.getLog(ObjectSerializer.class);

	public static String serialize(Serializable obj) {
		if (obj == null)
			return "";
		try {
			ByteArrayOutputStream serialObj = new ByteArrayOutputStream();
			ObjectOutputStream objStream = new ObjectOutputStream(serialObj);
			objStream.writeObject(obj);
			objStream.close();
			return encodeBytes(serialObj.toByteArray());
		} catch (Exception e) {
			return "";
		}
	}

	public static Object deserialize(String str) {
		if (str == null || str.length() == 0)
			return null;
		try {
			ByteArrayInputStream serialObj = new ByteArrayInputStream(
					decodeBytes(str));
			ObjectInputStream objStream = new ObjectInputStream(serialObj);
			return objStream.readObject();
		} catch (Exception e) {
			return "";
		}
	}

	public static String encodeBytes(byte[] bytes) {
		StringBuffer strBuf = new StringBuffer();

		for (int i = 0; i < bytes.length; i++) {
			strBuf.append((char) (((bytes[i] >> 4) & 0xF) + ((int) 'a')));
			strBuf.append((char) (((bytes[i]) & 0xF) + ((int) 'a')));
		}

		return strBuf.toString();
	}

	public static byte[] decodeBytes(String str) {
		byte[] bytes = new byte[str.length() / 2];
		for (int i = 0; i < str.length(); i += 2) {
			char c = str.charAt(i);
			bytes[i / 2] = (byte) ((c - 'a') << 4);
			c = str.charAt(i + 1);
			bytes[i / 2] += (c - 'a');
		}
		return bytes;
	}

}
