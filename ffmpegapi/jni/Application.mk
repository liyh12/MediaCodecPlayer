APP_ABI := armeabi-v7a arm64-v8a x86 x86_64
#APP_ABI := armeabi-v7a
APP_CFLAGS := -O3 -Wall -pipe \
    -ffast-math \
    -fstrict-aliasing -Werror=strict-aliasing \
    -Wno-psabi -Wa,--noexecstack \
    -DANDROID -DNDEBUG