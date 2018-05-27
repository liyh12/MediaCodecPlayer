package com.youku.player.util;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.View;

public class ScreenUtil {
	private static int h, w;

	public static void init(Activity activity) {
		DisplayMetrics dm = new DisplayMetrics();
		activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
		setH(dm.heightPixels);
		setW(dm.widthPixels);
	}
	/*
	 * 获取控件宽 
	 */
	public static int getWidth(View view) {
		return (view.getWidth());
	}

	/*
	 * 获取控件高
	 */
	public static int getHeight(View view) {
		return (view.getHeight());
	}

	public static int getH() {
		return h;
	}

	public static void setH(int h) {
		ScreenUtil.h = h;
	}

	public static int getW() {
		return w;
	}

	public static void setW(int w) {
		ScreenUtil.w = w;
	}
	
	/**
	 * 得到设备屏幕的宽度
	 */
	public static int getScreenWidth(Context context) {
		return context.getResources().getDisplayMetrics().widthPixels;
	}

	/**
	 * 得到设备屏幕的高度
	 */
	public static int getScreenHeight(Context context) {
		return context.getResources().getDisplayMetrics().heightPixels;
	}

	/**
	 * 得到设备的密度
	 */
	public static float getScreenDensity(Context context) {
		return context.getResources().getDisplayMetrics().density;
	}

	/**
	 * 把密度转换为像素
	 */
	public static int dip2px(Context context, float px) {
		final float scale = getScreenDensity(context);
		return (int) (px * scale + 0.5);
	}
}
