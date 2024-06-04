/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
 * Description: DataBus Python SDK的`databus.memory_io`模块实现
 * Create Date: 2024-04-23
 */
#define PY_SSIZE_T_CLEAN

#include <Python.h>
#include <stdio.h>
#include <sys/shm.h>
#include "memory_write.h"

// databus.memory_io.read函数注释文档
static const char * const DATABUS_MEMORY_IO_READ_DOC = \
"读取大小为`size`, 位于`memory_id对应的内存地址 + offset`的共享内存.\n"
"\n"
"Args:\n"
"    args:\n"
"       1. memory_id (int): 共享内存id.\n"
"       2. size (int): 读取大小.\n"
"    kwargs:\n"
"       3. offset (int): 读取偏移量, 默认为0.\n"
"\n"
"Return:\n"
"    以bytes类型返回内存内容\n"
"\n"
"Raises:\n"
"     IOError: 内存附加/读取/分离错误";

// databus.memory_io.write函数注释文档
static const char * const DATABUS_MEMORY_IO_WRITE_DOC = \
"对位于`memory_id对应的内存地址 + offset`的共享内存写入`contents`.\n"
"\n"
"Args:\n"
"    args:\n"
"       1. memory_id (int): 共享内存id.\n"
"       2. contents (bytes): 写入内容.\n"
"    kwargs:\n"
"       3. offset (int): 写入位置偏移量, 默认为0.\n"
"\n"
"Return:\n"
"    以int类型返回写入长度\n"
"\n"
"Raises:\n"
"     IOError: 内存附加/读取/分离错误";

/*
 * databus.memory_io.read函数
 *
 * Python对应函数原型:
 *     def read(memory_id: int, size: int, offset: int = 0) -> bytes: pass
 *
 * 描述:
 *     读取key为`memory_id`的共享内存,以`offset`为起始,读取`size`字节,作为bytes类型返回
 */
static PyObject*
DataBus_MemoryIo_Read(PyObject* self, PyObject* args, PyObject* keywords)
{
    int sharedMemoryId = -1;
    long readSize = 0;
    Py_ssize_t readOffset = 0;

    static char* keywordList[] = { "memory_id", "size", "offset", NULL };

    // Python内置解析参数函数
    if (!PyArg_ParseTupleAndKeywords(
        args, keywords, "il|n",
        keywordList,
        // memory_id, size, offset
        &sharedMemoryId, &readSize, &readOffset)) {
        return PyErr_Format(PyExc_ValueError, "invalid input parameter");
    }
    // 获取共享内存指针
    void* sharedMemoryBuffer = shmat(sharedMemoryId, NULL, SHM_RDONLY);
    if (sharedMemoryBuffer == (void*) -1) {
        // 获取到的指针无效时抛出`IOError`(Python实际抛`OSError`)
        // 见[alias IOError to OSError](https://peps.python.org/pep-3151/#step-1-coalesce-exception-types)
        return PyErr_Format(PyExc_IOError, "fail to attach the shared memory, reason: %s", strerror(errno));
    }
    PyObject* bytes = PyBytes_FromStringAndSize(
        (const char*) sharedMemoryBuffer + readOffset, readSize);
    if (bytes == NULL || shmdt(sharedMemoryBuffer) == -1) {
        // 读取或分离失败同样抛`IOError`(实际抛`OSError`,见上)
        return PyErr_Format(PyExc_IOError, "fail to %s the shared memory",
                            (bytes == NULL) ? "read" : "detach");
    }

    return bytes;
}

/*
 * databus.memory_io.write函数
 *
 * Python对应函数原型:
 *     def write(memory_id: int, contents: bytes, offset: int = 0) -> int: pass
 *
 * 描述:
 *     读取key为`memory_id`的共享内存,从`offset`为偏移起始,写入`contents`内容,返回成功写入的长度
 */
static PyObject*
DataBus_MemoryIo_Write(PyObject* self, PyObject* args, PyObject* keywords)
{
    int sharedMemoryId = -1;
    const char* contents = NULL;
    Py_ssize_t contentLength = 0;
    Py_ssize_t readOffset = 0;
    static char* keywordList[] = { "memory_id", "contents", "offset", NULL };

    // Python内置解析参数函数
    if (!PyArg_ParseTupleAndKeywords(
        args, keywords, "iy#|n",
        keywordList,
        // memory_id, size, offset
        &sharedMemoryId, &contents, &contentLength, &readOffset)) {
        return PyErr_Format(PyExc_ValueError, "invalid input parameter");
    }
    // 获取共享内存指针
    void* sharedMemoryBuffer = shmat(sharedMemoryId, NULL, 0);
    if (sharedMemoryBuffer == (void*) -1) {
        // 获取到的指针无效时抛出`IOError`(Python实际抛`OSError`)
        // 见[alias IOError to OSError](https://peps.python.org/pep-3151/#step-1-coalesce-exception-types)
        return PyErr_Format(PyExc_IOError, "fail to attach the shared memory, reason: %s", strerror(errno));
    }
    WriteToSharedBuffer((const unsigned char*) contents, contentLength, sharedMemoryBuffer + readOffset);
    if (shmdt(sharedMemoryBuffer) == -1) {
        // 读取或分离失败同样抛`IOError`(实际抛`OSError`,见上)
        return PyErr_Format(PyExc_IOError, "fail to detach the shared memory");
    }
    return PyLong_FromSsize_t(contentLength);
}

// 声明databus.memory_io模块的所有方法
static PyMethodDef g_dataBus_MemoryIo_Methods[] = {
    {
        "read",
        // Python方法声明只接受两个参数的函数,需要强转一下
        (PyCFunction)(void(*)(void))DataBus_MemoryIo_Read,
        METH_VARARGS | METH_KEYWORDS,
        DATABUS_MEMORY_IO_READ_DOC
    },
    {
        "write",
        // Python方法声明只接受两个参数的函数,需要强转一下
        (PyCFunction)(void(*)(void))DataBus_MemoryIo_Write,
        METH_VARARGS | METH_KEYWORDS,
        DATABUS_MEMORY_IO_WRITE_DOC
    },
    {NULL, NULL, 0, NULL}
};

// 声明databus.memory_io
static struct PyModuleDef g_dataBus_MemoryIo_Module = {
    PyModuleDef_HEAD_INIT,
    // 模块名称
    "memory_io",
    // 模块描述
    "Python interface for manipulating memory, part of the DataBus Python SDK",
    // 模块静态大小
    -1,
    // 模块内方法
    g_dataBus_MemoryIo_Methods
};

// 初始化databus.memory_io
#define DATABUS_DECL_MODULE(moduleName) PyInit_##moduleName(void)

PyMODINIT_FUNC DATABUS_DECL_MODULE(memory_io)
{
    return PyModule_Create(&g_dataBus_MemoryIo_Module);
}
