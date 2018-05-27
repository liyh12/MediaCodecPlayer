#include "ffextractor.h"

int avinterrupt_callback(void *param) {
//	FFMediaExtractor *ffextractor = (FFMediaExtractor*) param;
//	if (ffextractor->timeout == -1) {
//		return 1;
//	}
//	if (ffextractor->readcnt == 0) {
//		ffextractor->time0 = time(NULL );
//		ffextractor->readcnt = 1;
//	} else {
//		ffextractor->time1 = time(NULL );
//		if (difftime(ffextractor->time1, ffextractor->time0)
//				> ffextractor->timeout) {
//			return 1;
//		}
//	}
	return 0;
}
//设置数据源
void ffextractor_setDataSource(FFMediaExtractor * ffexe, const char *filepath) {
	ffexe->datapath = filepath;
	ffexe->pFormatCtx = avformat_alloc_context();
	ffexe->pFormatCtx->interrupt_callback.callback = avinterrupt_callback;
	ffexe->pFormatCtx->interrupt_callback.opaque = ffexe;
}
int ffextractor_prepare(FFMediaExtractor * ffexe) {

	av_register_all();
	avformat_network_init();
//	AVDictionary *avdic=NULL;
//	char option_key[]="skip_initial_bytes";
//	char option_value[]="34";
//	av_dict_set(&avdic,option_key,option_value,0);

//	avio_open(&ffexe->pFormatCtx->pb, ffexe->datapath, AVIO_FLAG_READ);
//	avio_skip(ffexe->pFormatCtx->pb, 34);
	if (avformat_open_input(&(ffexe->pFormatCtx), ffexe->datapath, NULL, NULL )
			!= 0) {
		printf("Couldn't open input stream.\n");
		return -1;
	}
	if (avformat_find_stream_info(ffexe->pFormatCtx, NULL ) < 0) {
		printf("Couldn't find stream information.\n");
		return -2;
	}
	ffexe->videoindex = -1;
	ffexe->audioinx = -1;
	int i;
	for (i = 0; i < ffexe->pFormatCtx->nb_streams; i++) {
		if (ffexe->pFormatCtx->streams[i]->codec->codec_type
				== AVMEDIA_TYPE_VIDEO) {
			ffexe->videoindex = i;
		}
		if (ffexe->pFormatCtx->streams[i]->codec->codec_type
				== AVMEDIA_TYPE_AUDIO) {
			ffexe->audioinx = i;
		}
	}
	return 0;
}

//释放
void ffextractor_release(FFMediaExtractor * ffexe) {
	if (ffexe->hasReadPacket == 1) {
		av_packet_unref(ffexe->packet);
	}
	avformat_close_input(&(ffexe->pFormatCtx));
	free(ffexe);
}
