package com.youku.ypush;

public class JniTest {
	static {
		System.loadLibrary("ijkffmpeg");
		System.loadLibrary("ypush");
	}

	public static native String GetStr(String path);

}
