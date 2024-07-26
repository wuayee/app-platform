/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2023-2023. All rights reserved.
 */

package com.huawei.fit.ohscript.script.parser.codereader;

import com.huawei.fitframework.log.Logger;
import com.huawei.fitframework.util.StringUtils;

import lombok.SneakyThrows;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * 读入ohFile源码
 *
 * @since 1.0
 */
public class OhFileReader implements CodeReader {
    private static final Logger LOG = Logger.get(OhFileReader.class);

    private final BufferedReader bufferedReader;

    private boolean closed = false;

    /**
     * 构造函数
     *
     * @param filePath oh文件路径
     * @throws IOException 文件读取异常
     */
    public OhFileReader(String filePath) throws IOException {
        FileReader fileReader = new FileReader(filePath);
        bufferedReader = new BufferedReader(fileReader);
    }

    @SneakyThrows
    @Override
    public String readLine() {
        if (this.closed) {
            return null;
        }
        String line = this.bufferedReader.readLine();
        if (line == null) {
            this.bufferedReader.close();
            this.closed = true;
            return "$";
        } else {
            while (StringUtils.equals(line.trim(), StringUtils.EMPTY)) {
                line = this.readLine();
            }
            if (line.endsWith("{")) {
                line += this.readLine();
            }
            return line;
        }
    }

    @Override
    public void close() {
        if (this.closed) {
            return;
        }
        try {
            this.bufferedReader.close();
        } catch (IOException e) {
            LOG.error("close file error.", e);
        }
        this.closed = true;
    }
}
