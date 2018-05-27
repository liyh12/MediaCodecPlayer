/*
 * jnirefhelp.c
 *
 *  Created on: 2016-6-17
 *      Author: youku
 */

#include "jnirefhelp.h"

jint jniGetInt(JNIEnv* env,jobject obj,const char* field){
	jclass cls = (*env)->GetObjectClass(env, obj);
	jfieldID fid = (*env)->GetFieldID(env, cls, field, "I");
	jlong value = (*env)->GetIntField(env, obj, fid);
	return value;
}
jlong jniGetLong(JNIEnv* env,jobject obj,const char* field){
	jclass cls = (*env)->GetObjectClass(env, obj);
	jfieldID fid = (*env)->GetFieldID(env, cls, field, "J");
	jlong value = (*env)->GetLongField(env, obj, fid);
	return value;
}

jstring jniGetString(JNIEnv* env,jobject obj,const char* field){
	jclass cls = (*env)->GetObjectClass(env, obj);
	jfieldID fid = (*env)->GetFieldID(env, cls, field, "Ljava/lang/String;");
	jstring value = (*env)->GetObjectField(env, obj, fid);
	return value;
}
jdouble jniGetDouble(JNIEnv* env,jobject obj,const char* field){
	jclass cls = (*env)->GetObjectClass(env, obj);
	jfieldID fid = (*env)->GetFieldID(env, cls, field, "D");
	jdouble value = (*env)->GetDoubleField(env, obj, fid);
	return value;
}
jfloat jniGetFloat(JNIEnv* env,jobject obj,const char* field){
	jclass cls = (*env)->GetObjectClass(env, obj);
	jfieldID fid = (*env)->GetFieldID(env, cls, field, "F");
	jfloat value = (*env)->GetFloatField(env, obj, fid);
	return value;
}
jobject jniGetObject(JNIEnv* env,jobject obj,const char* field,const char* clzname){
	jclass cls = (*env)->GetObjectClass(env, obj);
	jfieldID fid = (*env)->GetFieldID(env, cls, field, clzname);
	jobject value = (*env)->GetObjectField(env,obj,fid);
	return value;
}




void jniSetInt(JNIEnv* env,jobject obj,const char* field,int value){
	jclass cls = (*env)->GetObjectClass(env, obj);
	jfieldID fid = (*env)->GetFieldID(env, cls, field, "I");
	(*env)->SetIntField(env,obj,fid,value);
}
void jniSetLong(JNIEnv* env,jobject obj,const char* field,long value){
	jclass cls = (*env)->GetObjectClass(env, obj);
	jfieldID fid = (*env)->GetFieldID(env, cls, field, "J");
	(*env)->SetLongField(env,obj,fid,value);
}
void jniSetString(JNIEnv* env,jobject obj,const char* field,const char* value){
	jclass cls = (*env)->GetObjectClass(env, obj);
	jfieldID fid = (*env)->GetFieldID(env, cls, field, "Ljava/lang/String;");
	jstring jstr=(*env)->NewStringUTF(env,value);
	(*env)->SetObjectField(env,obj,fid,jstr);
}
void jniSetDouble(JNIEnv* env,jobject obj,const char* field,double value){
	jclass cls = (*env)->GetObjectClass(env, obj);
	jfieldID fid = (*env)->GetFieldID(env, cls, field, "D");
	(*env)->SetDoubleField(env,obj,fid,value);
}
void jniSetFloat(JNIEnv* env,jobject obj,const char* field,float value){
	jclass cls = (*env)->GetObjectClass(env, obj);
	jfieldID fid = (*env)->GetFieldID(env, cls, field, "F");
	(*env)->SetFloatField(env,obj,fid,value);
}
void jniSetObject(JNIEnv* env,jobject obj,const char* field,const char* clzname,jobject value){
	jclass cls = (*env)->GetObjectClass(env, obj);
	jfieldID fid = (*env)->GetFieldID(env, cls, field, clzname);
	(*env)->SetObjectField(env,obj,fid,value);
}


