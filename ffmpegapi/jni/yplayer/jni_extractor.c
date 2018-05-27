#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <time.h>
#include <jni.h>
#include "ffextractor.h"
#include "jnirefhelp.h"

FFMediaExtractor *getExtractor(JNIEnv* env, jobject obj) {
	jclass cls = (*env)->GetObjectClass(env, obj);
	jfieldID fid = (*env)->GetFieldID(env, cls, "mNativePointer", "J");
	jlong value = (*env)->GetLongField(env, obj, fid);
	intptr_t pt = (intptr_t) value;
	FFMediaExtractor *ffextractor = (FFMediaExtractor*) pt;
	return ffextractor;
}

void Java_com_youku_yplayer_FFExtractor_stopread(JNIEnv* env, jobject obj) {
	FFMediaExtractor *ffextractor = getExtractor(env, obj);
	ffextractor->timeout = -1;
}
jlong Java_com_youku_yplayer_FFExtractor_init(JNIEnv *env, jobject obj) {
	FFMediaExtractor *ffextractor = (FFMediaExtractor *) malloc(
			sizeof(FFMediaExtractor));
	ffextractor->hasReadPacket = 0;
	ffextractor->vtimebase = 0;
	ffextractor->atimebase = 0;
	ffextractor->timeout = 10;
	return (intptr_t) ffextractor;
}

void Java_com_youku_yplayer_FFExtractor_setDataSource(JNIEnv* env, jobject obj,
		jstring inpath) {
	FFMediaExtractor *ffextractor = getExtractor(env, obj);
	const char *liyhpath = (*env)->GetStringUTFChars(env, inpath, NULL );
	ffextractor_setDataSource(ffextractor, liyhpath);
}
int Java_com_youku_yplayer_FFExtractor_prepare(JNIEnv* env, jobject obj,
		jstring inpath) {
	FFMediaExtractor *ffextractor = getExtractor(env, obj);
	ffextractor->readcnt = 0;
	int result = ffextractor_prepare(ffextractor);
	if (result == 0) {
		jniSetLong(env, obj, "bitRate", ffextractor->pFormatCtx->bit_rate);
		jniSetLong(env, obj, "duration",
				(ffextractor->pFormatCtx->duration * 1000) / AV_TIME_BASE);
		jniSetInt(env, obj, "streamNum", ffextractor->pFormatCtx->nb_streams);
		int videoinx = ffextractor->videoindex;
		int audioinx = ffextractor->audioinx;
		jniSetInt(env, obj, "streamVideoInx", videoinx);
		jniSetInt(env, obj, "streamAudioInx", audioinx);
		if (videoinx > -1) {
			jniSetInt(env, obj, "width",
					ffextractor->pFormatCtx->streams[videoinx]->codec->width);
			jniSetInt(env, obj, "height",
					ffextractor->pFormatCtx->streams[videoinx]->codec->height);

			jniSetDouble(env, obj, "frameRate",
					av_q2d(
							ffextractor->pFormatCtx->streams[videoinx]->avg_frame_rate));

			if (ffextractor->pFormatCtx->streams[videoinx]->codec->codec_id
					== AV_CODEC_ID_H264) {
				jniSetInt(env, obj, "videoCodecId", 1);
			}
//			jniSetString(env, obj, "videoCodecType",
//					ffextractor->pFormatCtx->video_codec->name);
			jniSetDouble(env, obj, "vTimeBase",
					av_q2d(
							ffextractor->pFormatCtx->streams[videoinx]->time_base));
			ffextractor->vtimebase = av_q2d(
					ffextractor->pFormatCtx->streams[videoinx]->time_base);
		}
		if (audioinx > -1) {
			jniSetInt(env, obj, "sampleRate",
					ffextractor->pFormatCtx->streams[audioinx]->codec->sample_rate);
			jniSetInt(env, obj, "audioChannels",
					ffextractor->pFormatCtx->streams[audioinx]->codec->channels);

			if (ffextractor->pFormatCtx->streams[audioinx]->codec->codec_id
					== AV_CODEC_ID_AAC) {
				jniSetInt(env, obj, "audioCodecId", 1);
			}
			jniSetDouble(env, obj, "aTimeBase",
					av_q2d(
							ffextractor->pFormatCtx->streams[audioinx]->time_base));
			ffextractor->atimebase = av_q2d(
					ffextractor->pFormatCtx->streams[audioinx]->time_base);
		}
		ffextractor->packet = (AVPacket *) av_malloc(sizeof(AVPacket));
		char meta[1024] = "";
		AVDictionaryEntry *m = NULL;
		while ((m = av_dict_get(ffextractor->pFormatCtx->metadata, "", m,
				AV_DICT_IGNORE_SUFFIX)) != NULL ) {
			strcat(meta, m->key);
			strcat(meta, "\t:");
			strcat(meta, m->value);
			strcat(meta, "\r\n");
		}
		jniSetString(env, obj, "metaData", meta);
	}
	return result;
}
jint Java_com_youku_yplayer_FFExtractor_seekfile(JNIEnv* env, jobject obj,
		jlong ts) {
	FFMediaExtractor *ffextractor = getExtractor(env, obj);

	ts = ts * (AV_TIME_BASE / 1000);
	if (ts < 0) {
		ts = 0;
	}
	if (ts > ffextractor->pFormatCtx->duration) {
		ts = ffextractor->pFormatCtx->duration;
	}

	int ret = avformat_seek_file(ffextractor->pFormatCtx, -1, 0, ts,
			ffextractor->pFormatCtx->duration, 0);
	avio_flush(ffextractor->pFormatCtx->pb);
	avformat_flush(ffextractor->pFormatCtx);
	return ret;
}
jint Java_com_youku_yplayer_FFExtractor_seekframe(JNIEnv* env, jobject obj,
		jlong ts) {
	FFMediaExtractor *ffextractor = getExtractor(env, obj);

	ts = ts * (AV_TIME_BASE / 1000);
	if (ts < 0) {
		ts = 0;
	}
	if (ts > ffextractor->pFormatCtx->duration) {
		ts = ffextractor->pFormatCtx->duration;
	}

	int ret = av_seek_frame(ffextractor->pFormatCtx, -1, ts, 0);
	avio_flush(ffextractor->pFormatCtx->pb);
	avformat_flush(ffextractor->pFormatCtx);
	return ret;
}

int Java_com_youku_yplayer_FFExtractor_hasError(JNIEnv* env, jobject obj) {
	FFMediaExtractor *ffextractor = getExtractor(env, obj);
	return ffextractor->pFormatCtx->pb->error;
}
int Java_com_youku_yplayer_FFExtractor_getBufferPos(JNIEnv* env, jobject obj) {
	FFMediaExtractor *ffextractor = getExtractor(env, obj);

	return ffextractor->pFormatCtx->pb->pos;
}

int Java_com_youku_yplayer_FFExtractor_getFileLength(JNIEnv* env, jobject obj) {
	FFMediaExtractor *ffextractor = getExtractor(env, obj);
	return avio_size(ffextractor->pFormatCtx->pb);
}

void Java_com_youku_yplayer_FFExtractor_release(JNIEnv* env, jobject obj,
		jstring inpath) {
	FFMediaExtractor *ffextractor = getExtractor(env, obj);
	ffextractor_release(ffextractor);
}
void Java_com_youku_yplayer_FFExtractor_getRead2(JNIEnv* env, jobject obj,
		jobject avpacket) {
	FFMediaExtractor *ffextractor = getExtractor(env, obj);
	jobject bytebuffer;
	bytebuffer = (*env)->NewDirectByteBuffer(env, ffextractor->packet->data,
			ffextractor->packet->size);
	jniSetObject(env, avpacket, "data", "Ljava/nio/ByteBuffer;", bytebuffer);
	jniSetInt(env, avpacket, "dataSize", ffextractor->packet->size);
	if (ffextractor->packet->stream_index == ffextractor->audioinx) {
		jniSetLong(env, avpacket, "dts",
				ffextractor->packet->dts * ffextractor->atimebase * 1000);
		jniSetLong(env, avpacket, "pts",
				ffextractor->packet->pts * ffextractor->atimebase * 1000);
	} else if (ffextractor->packet->stream_index == ffextractor->videoindex) {
		jniSetLong(env, avpacket, "dts",
				ffextractor->packet->dts * ffextractor->vtimebase * 1000);
		jniSetLong(env, avpacket, "pts",
				ffextractor->packet->pts * ffextractor->vtimebase * 1000);
	}
	//jniSetLong(env, avpacket, "duration", ffextractor->packet->duration);
	jniSetInt(env, avpacket, "streamInx", ffextractor->packet->stream_index);
	//jniSetLong(env, avpacket, "pos", ffextractor->packet->pos);
	jniSetInt(env, avpacket, "flags", ffextractor->packet->flags);
}
jint Java_com_youku_yplayer_FFExtractor_prereadNext(JNIEnv* env, jobject obj) {
	//TODO http://blog.csdn.net/leixiaohua1020/article/details/12678577
	// AVERROR(EAGAIN) AVERROR(ENOMEM) AVERROR_EOF
	//区分具体错误原因
	FFMediaExtractor *ffextractor = getExtractor(env, obj);
	if (ffextractor->hasReadPacket == 0) {
		ffextractor->hasReadPacket = 1;
	} else if (ffextractor->hasReadPacket == 1) {
		av_packet_unref(ffextractor->packet);
	}
	ffextractor->readcnt = 0;
	int readr = av_read_frame(ffextractor->pFormatCtx, ffextractor->packet);
	if (readr >= 0) {
		ffextractor->readcnt = 0;
		return ffextractor->packet->stream_index;
	} else if (readr == AVERROR_EOF ) {
		return -1;
	} else if (readr == AVERROR(EAGAIN)) {
		return -2;
	} else if (readr == AVERROR(ENOMEM)) {
		return -3;
	} else {
		return readr;
	}
}

void Java_com_youku_yplayer_FFExtractor_getRead(JNIEnv* env, jobject obj,
		jobject avpacket, jbyteArray jba) {
	FFMediaExtractor *ffextractor = getExtractor(env, obj);
	(*env)->SetByteArrayRegion(env, jba, 0, ffextractor->packet->size,
			(const jbyte*) ffextractor->packet->data);
	jniSetInt(env, avpacket, "dataSize", ffextractor->packet->size);
	if (ffextractor->packet->stream_index == ffextractor->audioinx) {
		jniSetLong(env, avpacket, "dts",
				ffextractor->packet->dts * ffextractor->atimebase * 1000);
		jniSetLong(env, avpacket, "pts",
				ffextractor->packet->pts * ffextractor->atimebase * 1000);
	} else if (ffextractor->packet->stream_index == ffextractor->videoindex) {
		jniSetLong(env, avpacket, "dts",
				ffextractor->packet->dts * ffextractor->vtimebase * 1000);
		jniSetLong(env, avpacket, "pts",
				ffextractor->packet->pts * ffextractor->vtimebase * 1000);
	}
	//jniSetLong(env, avpacket, "duration", ffextractor->packet->duration);
	jniSetInt(env, avpacket, "streamInx", ffextractor->packet->stream_index);
	//jniSetLong(env, avpacket, "pos", ffextractor->packet->pos);
	jniSetInt(env, avpacket, "flags", ffextractor->packet->flags);
}

jobject Java_com_youku_yplayer_FFExtractor_getExtraData(JNIEnv* env,
		jobject obj, int inx) {
	FFMediaExtractor *ffextractor = getExtractor(env, obj);
	jobject bytebuffer;
	if (ffextractor->pFormatCtx->streams[inx]->codec->extradata_size > 0) {
		bytebuffer = (*env)->NewDirectByteBuffer(env,
				ffextractor->pFormatCtx->streams[inx]->codec->extradata,
				(ffextractor->pFormatCtx->streams[inx]->codec->extradata_size)
						+ 1);
	} else {
		return NULL ;
	}
	return bytebuffer;
}

jbyteArray Java_com_youku_yplayer_FFExtractor__aestest2(JNIEnv* env, jobject obj,
		jbyteArray txt, jbyteArray key, jbyteArray ret) {
	jclass keyclass = (*env)->FindClass(env,
			"javax/crypto/spec/SecretKeySpec");
	jmethodID newkeymethod = (*env)-> GetMethodID(env, keyclass, "<init>",
	               "([BLjava/lang/String;)V" );
	jobject secretkey= (*env)->NewObject(env,keyclass,newkeymethod,key,(*env)-> NewStringUTF(env, "AES"));

	jclass cipherclass=(*env)->FindClass(env,
			"javax/crypto/Cipher");
	jmethodID newciphermethod = (*env)-> GetStaticMethodID(env,cipherclass,"getInstance","(Ljava/lang/String;)Ljavax/crypto/Cipher;");
	jmethodID initkeymethod = (*env)-> GetMethodID(env,cipherclass,"init","(ILjava/security/Key;)V");
	jmethodID encmethod = (*env)-> GetMethodID(env,cipherclass,"doFinal","([B)[B");
	jobject cipher=(*env)->CallStaticObjectMethod(env,cipherclass,newciphermethod,(*env)-> NewStringUTF(env, "AES/ECB/NoPadding"));
	(*env)->CallVoidMethod(env,cipher,initkeymethod,1,secretkey);
	jbyteArray result=(*env)->CallObjectMethod(env,cipher,encmethod,txt);
	return result;
}
jbyteArray Java_com_youku_yplayer_FFExtractor_native_aestest2(JNIEnv* env, jobject obj,
		jbyteArray txt, jbyteArray key, jbyteArray ret) {

	jclass keyclass = (*env)->FindClass(env,
			"javax/crypto/spec/SecretKeySpec");
	jmethodID newkeymethod = (*env)-> GetMethodID(env, keyclass, "<init>",
	               "([BLjava/lang/String;)V" );
	jobject secretkey= (*env)->NewObject(env,keyclass,newkeymethod,key,(*env)-> NewStringUTF(env, "AES"));

	jclass cipherclass=(*env)->FindClass(env,
			"javax/crypto/Cipher");
	jmethodID newciphermethod = (*env)-> GetStaticMethodID(env,cipherclass,"getInstance","(Ljava/lang/String;)Ljavax/crypto/Cipher;");
	jmethodID initkeymethod = (*env)-> GetMethodID(env,cipherclass,"init","(ILjava/security/Key;)V");
	jmethodID encmethod = (*env)-> GetMethodID(env,cipherclass,"doFinal","([B)[B");
	jobject cipher=(*env)->CallStaticObjectMethod(env,cipherclass,newciphermethod,(*env)-> NewStringUTF(env, "AES/ECB/NoPadding"));
	(*env)->CallVoidMethod(env,cipher,initkeymethod,1,secretkey);
	jbyteArray result=(*env)->CallObjectMethod(env,cipher,encmethod,txt);
	return result;
}

