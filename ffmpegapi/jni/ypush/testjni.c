#include "stdio.h"
#include <string.h>
#include <jni.h>
#include <libavformat/avformat.h>

jstring JNICALL Java_com_youku_ypush_JniTest_GetStr(JNIEnv* env, jobject thiz,
		jstring inpath) {

	const char *i_path = NULL;
	i_path = (*env)->GetStringUTFChars(env, inpath, NULL );

	int rcode = testAvformat(i_path);
	jstring str = NULL;
	if (rcode > -1) {
		str = (*env)->NewStringUTF(env, "load ffmpeg success!");

	} else {
		str = (*env)->NewStringUTF(env, "load ffmpeg fail!");
	}
	return str;
}

int testAvformat(const char *filepath) {

	AVFormatContext *pFormatCtx;
	av_register_all();
	avformat_network_init();
	pFormatCtx = avformat_alloc_context();
	if (avformat_open_input(&pFormatCtx, filepath, NULL, NULL ) != 0) {
		return -1;
	}
	if (avformat_find_stream_info(pFormatCtx, NULL ) < 0) {
		return -2;
	}
	return 0;
}

