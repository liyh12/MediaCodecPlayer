/*
 * ffextractor.h
 *
 *  Created on: 2016-6-15
 *      Author: youku
 */

#ifndef FFEXTRACTOR_H_
#define FFEXTRACTOR_H_
#include <stdio.h>
#include <jni.h>
#include <time.h>
#include <libavcodec/avcodec.h>
#include <libavformat/avformat.h>
#include <libavfilter/avfilter.h>
#include <libswscale/swscale.h>
#include <libavutil/imgutils.h>
#include <libavutil/mathematics.h>

typedef struct FFMediaExtractor {
	const char *datapath;
	AVFormatContext *pFormatCtx;
	int audioinx, videoindex;
	double vtimebase,atimebase;
	AVPacket *packet;
	int hasReadPacket;
	int timeout;
	int readcnt;
	time_t time0;
	time_t time1;
} FFMediaExtractor;

//设置数据源
void ffextractor_setDataSource(FFMediaExtractor * ffexe, const char *filepath);
int ffextractor_prepare(FFMediaExtractor * ffexe);
//读取下一帧数据
AVPacket *ffextractor_readNext(FFMediaExtractor * ffexe);
//获取当前帧
AVPacket *ffextractor_getCurrentPacket(FFMediaExtractor * ffexe);
//释放
void ffextractor_release(FFMediaExtractor * ffexe);

#endif /* FFEXTRACTOR_H_ */
