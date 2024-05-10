/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2020-2021. All rights reserved.
 *
 * Description  : Provides definition for ODBC data source.
 * Author       : liangjishi 00298979
 * Date         : 2021/07/01
 */

#ifndef FIT_ODBC_DATA_SOURCE_HPP
#define FIT_ODBC_DATA_SOURCE_HPP

#include "types.hpp"

#include <fit/stl/string.hpp>

namespace Fit {
namespace Odbc {
/**
 * 为数据库连接提供工厂。
 * <p>首先需要在环境上安装unixODBC库，并按照对应的数据库ODBC驱动。</p>
 * <p>在ODBC及驱动安装完成后，需要在/etc/odbc.ini中配置数据库服务端信息。</p>
 * <p>以MySQL数据库为例，需要在/etc/odbc.ini中进行以下配置。</p>
 * <example>
 * [test-connector]<br/>
 * Description=MySQL connection to test_perf database<br/>
 * Driver=MySQL<br/>
 * Database=test<br/>
 * Server=localhost<br/>
 * Port=3306<br/>
 * Socket=/var/lib/mysql/mysql.sock<br/>
 * </example>
 */
class DataSource {
public:
    /**
     * 释放工厂占用的所有资源。
     */
    virtual ~DataSource() = default;

    /**
     * 获取一个数据库连接。
     *
     * @return 表示数据库连接的实例。
     * @throws SqlException 获取数据库连接失败。
     */
    virtual ConnectionUptr GetConnection() = 0;

    /**
     * 创建一个工厂的新实例。
     *
     * @param server 表示待连接到的数据库服务器的字符串。
     * @param username 表示连接时使用的用户名的字符串。
     * @param password 表示连接时使用的密码的字符串。
     * @return 表示指向数据库连接工厂的新实例的指针。
     */
    static DataSourceUptr Create(::Fit::string server, ::Fit::string username, ::Fit::string password);
};
}
}

#endif // FIT_ODBC_DATA_SOURCE_HPP
