/*
 * jnirefhelp.h
 *
 *  Created on: 2016-6-17
 *      Author: youku
 */

#ifndef JNIREFHELP_H_
#define JNIREFHELP_H_
#include "jni.h"

jint jniGetInt(JNIEnv* env,jobject obj,const char* field);
jlong jniGetLong(JNIEnv* env,jobject obj,const char* field);
jstring jniGetString(JNIEnv* env,jobject obj,const char* field);
jdouble jniGetDouble(JNIEnv* env,jobject obj,const char* field);
jfloat jniGetFloat(JNIEnv* env,jobject obj,const char* field);

jobject jniGetObject(JNIEnv* env,jobject obj,const char* field,const char* clzname);

void jniSetInt(JNIEnv* env,jobject obj,const char* field,int value);
void jniSetLong(JNIEnv* env,jobject obj,const char* field,long value);
void jniSetString(JNIEnv* env,jobject obj,const char* field,const char* value);
void jniSetDouble(JNIEnv* env,jobject obj,const char* field,double value);
void jniSetFloat(JNIEnv* env,jobject obj,const char* field,float value);

void jniSetObject(JNIEnv* env,jobject obj,const char* field,const char* clzname,jobject value);

#endif /* JNIREFHELP_H_ */
