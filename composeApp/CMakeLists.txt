cmake_minimum_required(VERSION 3.10.2)

# Определите путь к исходному коду
set(SOURCE_DIR ${CMAKE_SOURCE_DIR}/funcC)

# Добавьте библиотеку sharedSecret
add_library(sharedSecret SHARED ${SOURCE_DIR}/sharedSecret.c)

# Определите путь к библиотеке liboqs
set(LIBOQS_DIR ${CMAKE_SOURCE_DIR}/libs/liboqs)

# Укажите директории для заголовочных файлов и библиотек
include_directories(${LIBOQS_DIR}/include)
link_directories(${LIBOQS_DIR}/libs)

# Определите пути к библиотекам для каждой архитектуры
set(armeabi-v7a_lib ${LIBOQS_DIR}/libs/armeabi-v7a/liboqs.a)
set(arm64-v8a_lib ${LIBOQS_DIR}/libs/arm64-v8a/liboqs.a)
set(x86_lib ${LIBOQS_DIR}/libs/x86/liboqs.a)
set(x86_64_lib ${LIBOQS_DIR}/libs/x86_64/liboqs.a)

find_library(log-lib log)

# Свяжите библиотеки с вашим исполняемым файлом
target_link_libraries(sharedSecret
        $<$<STREQUAL:${ANDROID_ABI},armeabi-v7a>:${armeabi-v7a_lib}>
        $<$<STREQUAL:${ANDROID_ABI},arm64-v8a>:${arm64-v8a_lib}>
        $<$<STREQUAL:${ANDROID_ABI},x86>:${x86_lib}>
        $<$<STREQUAL:${ANDROID_ABI},x86_64>:${x86_64_lib}>
        ${log-lib}
)

# Добавьте библиотеку wolfsslNative
add_library(wolfsslNative SHARED
        ${SOURCE_DIR}/encupsChachaMessage.c
        ${SOURCE_DIR}/decupsChachaMessage.c
        ${SOURCE_DIR}/encupsChachaFile.c
        ${SOURCE_DIR}/decupsChachaFile.c
)

# Определите путь к библиотеке wolfssl
set(WOLFSSL_LIBS_DIR ${CMAKE_SOURCE_DIR}/libs/wolfssl/libs)

include_directories(${CMAKE_SOURCE_DIR}/libs/wolfssl/include) # Предполагается, что заголовочные файлы находятся здесь

add_library(wolfssl STATIC IMPORTED)
set_target_properties(wolfssl PROPERTIES IMPORTED_LOCATION ${WOLFSSL_LIBS_DIR}/${ANDROID_ABI}/libwolfssl.a)

# Свяжите библиотеки с вашим исполняемым файлом
target_link_libraries(wolfsslNative
        wolfssl
        ${log-lib}
)
