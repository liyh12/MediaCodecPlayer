/*
 * jni_ffextractor.c
 *
 *  Created on: 2016-12-13
 *      Author: liyh
 */
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <jni.h>
#include "ffextractor.h"
#include "jnirefhelp.h"

static const char *classPathName = "com/example/hellojni/HelloJni";

FFMediaExtractor *getExtractor(JNIEnv* env, jobject obj) {
	jclass cls = (*env)->GetObjectClass(env, obj);
	jfieldID fid = (*env)->GetFieldID(env, cls, "mNativePointer", "J");
	jlong value = (*env)->GetLongField(env, obj, fid);
	intptr_t pt = (intptr_t) value;
	FFMediaExtractor *ffextractor = (FFMediaExtractor*) pt;
	return ffextractor;
}
jlong FFExtractor_init(JNIEnv *env, jobject obj) {
	FFMediaExtractor *ffextractor = (FFMediaExtractor *) malloc(
			sizeof(FFMediaExtractor));
	ffextractor->hasReadPacket = 0;
	ffextractor->vtimebase = 0;
	ffextractor->atimebase = 0;
	ffextractor->timeout = 10;
	return (intptr_t) ffextractor;
}

void FFExtractor_setDataSource(JNIEnv* env, jobject obj, jstring inpath) {
	FFMediaExtractor *ffextractor = getExtractor(env, obj);
	const char *liyhpath = (*env)->GetStringUTFChars(env, inpath, NULL );
	ffextractor_setDataSource(ffextractor, liyhpath);
}

static JNINativeMethod methods[] = {
		{ "_init", "()J", (void*) FFExtractor_init },
		{ "_init", "()J", (void*) FFExtractor_init },
		{ "_init", "()J", (void*) FFExtractor_init },
		{ "_init", "()J", (void*) FFExtractor_init },
		{ "_init", "()J", (void*) FFExtractor_init },
		{ "_init", "()J", (void*) FFExtractor_init },
		{ "_init", "()J", (void*) FFExtractor_init },
		{ "_init", "()J", (void*) FFExtractor_init },
		{ "_init", "()J", (void*) FFExtractor_init },
		{ "_init", "()J", (void*) FFExtractor_init },
};



jint JNI_OnLoad(JavaVM* vm, void* reserved) {
	JNIEnv* env = NULL;
	jclass clazz;
	//获取JNI环境对象
	if ((*vm)->GetEnv(vm, (void**) &env, JNI_VERSION_1_4) != JNI_OK) {
		LOGE("ERROR: GetEnv failed\n");
		return JNI_ERR;
	}
	//注册本地方法.Load 目标类
	clazz = (*env)->FindClass(env, classPathName);
	if (clazz == NULL ) {
		LOGE("Native registration unable to find class '%s'", classPathName);
		return JNI_ERR;
	}
	//注册本地native方法
	if ((*env)->RegisterNatives(env, clazz, methods, NELEM(methods)) < 0) {
		LOGE("ERROR: MediaPlayer native registration failed\n");
		return JNI_ERR;
	}
	/* success -- return valid version number */
	return JNI_VERSION_1_4;
}

