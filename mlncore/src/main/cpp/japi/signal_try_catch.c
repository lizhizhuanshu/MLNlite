//
// Created by XiongFangyu on 8/20/21.
//

#include "signal_try_catch.h"
//lizhi 增加全局定义
jmp_buf *_g_jmp_buf;

void sig_handler(int signal) {
    longjmp(_g_jmp_buf, signal);
}