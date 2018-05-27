LOCAL_PATH := $(call my-dir)

#先预编译ffmpeg共享库
include $(CLEAR_VARS)

ifeq ($(TARGET_ARCH), arm64)
	MY_APP_FFMPEG_OUTPUT_PATH := $(realpath $(LOCAL_PATH)/ffmpeg-arm64/output)
else ifeq ($(TARGET_ARCH), x86)
	MY_APP_FFMPEG_OUTPUT_PATH := $(realpath $(LOCAL_PATH)/ffmpeg-x86/output)
else ifeq ($(TARGET_ARCH), x86_64)
	MY_APP_FFMPEG_OUTPUT_PATH := $(realpath $(LOCAL_PATH)/ffmpeg-x86_64/output)
else
	MY_APP_FFMPEG_OUTPUT_PATH := $(realpath $(LOCAL_PATH)/ffmpeg-armv7a/output)
endif

LOCAL_MODULE := ffmpeg
LOCAL_SRC_FILES := $(MY_APP_FFMPEG_OUTPUT_PATH)/libijkffmpeg.so
include $(PREBUILT_SHARED_LIBRARY)

#再编译yplayer内的文件
include $(CLEAR_VARS)
LOCAL_LDLIBS := -llog -landroid
LOCAL_C_INCLUDES := $(LOCAL_PATH)/yplayer
LOCAL_C_INCLUDES += $(MY_APP_FFMPEG_OUTPUT_PATH)/include
LOCAL_SHARED_LIBRARIES := \
    ffmpeg

LOCAL_SRC_FILES += $(realpath $(LOCAL_PATH))/yplayer/jnirefhelp.c
LOCAL_SRC_FILES += $(realpath $(LOCAL_PATH))/yplayer/ffextractor.c
#jni入口
LOCAL_SRC_FILES += $(realpath $(LOCAL_PATH))/yplayer/jni_extractor.c


LOCAL_MODULE := yplayer
include $(BUILD_SHARED_LIBRARY)

#再编译ypush内的文件
include $(CLEAR_VARS)
LOCAL_C_INCLUDES := $(LOCAL_PATH)/ypush
LOCAL_C_INCLUDES += $(MY_APP_FFMPEG_OUTPUT_PATH)/include
LOCAL_SHARED_LIBRARIES := \
    ffmpeg
    
LOCAL_SRC_FILES += $(realpath $(LOCAL_PATH))/ypush/testjni.c
LOCAL_MODULE := ypush
include $(BUILD_SHARED_LIBRARY)

