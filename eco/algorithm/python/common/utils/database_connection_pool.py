# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
"""
Description:
创建数据库连接池
数据库连接池为单例模式，保证连接池只被创建一次，从数据库连接池中获取连接，进而与数据库进行交互
Create: 2024/6/5 9:26
"""
import os
import threading
import urllib

from sqlalchemy import create_engine
from sqlalchemy.exc import SQLAlchemyError, OperationalError

from fitframework.api.logging import plugin_logger as logger
from fitframework.api.decorators import fit


class DatabaseConnectionPool:
    _instance = None
    _initialized = False
    conn_pool = None
    _lock = threading.Lock()

    def __new__(cls):
        if not cls._instance:
            with cls._lock:
                if not cls._instance:
                    cls._instance = cls._initialize_conn_pool()
        return cls._instance

    @property
    def initialized(self):
        return self._initialized

    @staticmethod
    def get_db_conn():
        """获取数据库连接"""
        conn = None
        db_conn_pool = DatabaseConnectionPool()
        if not db_conn_pool:
            return conn
        if not DatabaseConnectionPool._initialized:
            logger.error("Initial database connection pool failed. Please reinitialize.", exc_info=True)
        try:
            conn = DatabaseConnectionPool.conn_pool.connect()
        except SQLAlchemyError as e:
            logger.error("Connection error: %s", e, exc_info=True)
        return conn

    @classmethod
    def _initialize_conn_pool(cls):
        """初始化数据库连接引擎"""
        # 从环境变量中获取数据库相关配置信息
        db_user = os.environ.get("postgres.username")
        db_password = urllib.parse.quote(decrypt(os.environ.get("postgres.password")))  # 对数据库密码进行解密和转义
        db_server = os.environ.get("postgres.db.server")
        db_name = os.environ.get("postgres.db.name")
        ssl_mode = os.environ.get("ssl_mode")
        db_dsn = f"postgresql://{db_user}:{db_password}@{db_server}/{db_name}?sslmode={ssl_mode}"
        cls._instance = super(DatabaseConnectionPool, cls).__new__(cls)
        try:
            DatabaseConnectionPool.conn_pool = create_engine(
                db_dsn,
                pool_size=20,  # 连接池中的连接数
                max_overflow=5,  # 超出连接池大小时允许的最大连接数
                pool_timeout=60000  # 等待时间
            )
            DatabaseConnectionPool.conn_pool.connect().close()  # 执行 ping 操作检查数据库是否可用
        except (SQLAlchemyError, OperationalError) as e:
            DatabaseConnectionPool.conn_pool = None
            cls._instance = None
            logger.error(f"Failed to initialize conn_pool: %s", e, exc_info=True)
        if DatabaseConnectionPool.conn_pool:
            DatabaseConnectionPool._initialized = True
        return cls._instance


@fit("com.huawei.fit.security.decrypt")
def decrypt(cipher: str) -> str:
    """fit框架对加密后内容进行解密。"""
    pass
