# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. -. All rights reserved.
from urllib.parse import quote_plus
import psycopg2
from fel_langchain.langchain_registers import register_function_tools, register_api_tools
from langchain_community.utilities.sql_database import SQLDatabase
from langchain_experimental.tools import PythonREPLTool
from langchain_google_community import GoogleSearchRun, GoogleSearchAPIWrapper
from langchain_community.tools.sql_database.tool import (
    InfoSQLDatabaseTool,
    ListSQLDatabaseTool,
    QuerySQLCheckerTool,
    QuerySQLDataBaseTool,
)
from langchain_core.tools import BaseTool
from langchain_openai import ChatOpenAI


# 从app_engine加密传输敏感信息
def get_db(sql_url: str, sql_table: str, sql_name: str, sql_pwd: str) -> SQLDatabase:
    return SQLDatabase.from_uri(
        "postgresql+psycopg2://%s:%s@%s/%s" % (quote_plus(sql_name), quote_plus(sql_pwd), sql_url,
                                               quote_plus(sql_table)))


def _get_python_repl_tool() -> PythonREPLTool:
    import builtins
    from langchain_experimental.utilities import PythonREPL
    from RestrictedPython.Guards import safe_builtins

    def safer_import(name, globals=None, locals=None, fromlist=(), level=0):
        white_list = {'asyncio', 'json', 'numpy', 'typing'}
        if name not in white_list:
            raise NameError(f'model {name} is not valid.')
        return __import__(name, globals, locals, fromlist, level)

    builtins = {
        **safe_builtins,
        'print': getattr(builtins, 'print'),
        '__name__': getattr(builtins, '__name__'),
        'setattr': setattr,
        '__import__': safer_import
    }
    return PythonREPLTool(python_repl=PythonREPL(globals=builtins))


def langchain_sql_query(kwargs) -> BaseTool:
    db = get_db(kwargs.get("sql_url"), kwargs.get("sql_table"), kwargs.get("sql_name"), kwargs.get("sql_pwd"))

    query_sql_database_tool_description = (
        "Input to this tool is a detailed and correct SQL query, output is a "
        "result from the database. If the query is not correct, an error message "
        "will be returned. If an error is returned, rewrite the query, check the "
        "query, and try again. If you encounter an issue with Unknown column "
        "'xxxx' in 'field list', use sql_db_schema "
        "to query the correct table fields."
    )

    query_sql_database_tool = QuerySQLDataBaseTool(
        db=db, description=query_sql_database_tool_description
    )
    return query_sql_database_tool


def langchain_sql_info(kwargs) -> BaseTool:
    db = get_db(kwargs.get("sql_url"), kwargs.get("sql_table"), kwargs.get("sql_name"), kwargs.get("sql_pwd"))

    info_sql_database_tool_description = (
        "Input to this tool is a comma-separated list of tables, output is the "
        "schema and sample rows for those tables. "
        "Be sure that the tables actually exist by calling "
        "sql_db_list_tables first! "
        "Example Input: table1, table2, table3"
    )
    info_sql_database_tool = InfoSQLDatabaseTool(
        db=db, description=info_sql_database_tool_description
    )
    return info_sql_database_tool


def langchain_sql_list(kwargs) -> BaseTool:
    db = get_db(kwargs.get("sql_url"), kwargs.get("sql_table"), kwargs.get("sql_name"), kwargs.get("sql_pwd"))

    list_sql_database_tool = ListSQLDatabaseTool(db=db)
    return list_sql_database_tool


def langchain_sql_checker(kwargs) -> BaseTool:
    api_key = kwargs.get("api_key") or "EMPTY"
    model_name = kwargs.get("model_name")
    api_base = kwargs.get("api_base")
    temperature = kwargs.get("temperature") or 0

    db = get_db(kwargs.get("sql_url"), kwargs.get("sql_table"), kwargs.get("sql_name"), kwargs.get("sql_pwd"))
    llm = ChatOpenAI(model_name=model_name, openai_api_base=api_base, openai_api_key=api_key, temperature=temperature)


    query_sql_checker_tool_description = (
        "Use this tool to double check if your query is correct before executing "
        "it. Always use this tool before executing a query with "
        "sql_db_query!"
    )
    query_sql_checker_tool = QuerySQLCheckerTool(
        db=db, llm=llm, description=query_sql_checker_tool_description
    )
    return query_sql_checker_tool


# function tools
function_tools = [
    _get_python_repl_tool(),
]

register_function_tools(function_tools)

api_tools = [
    (lambda dict_args: GoogleSearchRun(api_wrapper=GoogleSearchAPIWrapper(**dict_args)),
     ["google_api_key", "google_cse_id", "k", "siterestrict"], "google_search"),
    (langchain_sql_query, ["sql_url", "sql_table", "sql_name", "sql_pwd"], "sql_db_query"),
    (langchain_sql_info, ["sql_url", "sql_table", "sql_name", "sql_pwd"], "sql_db_schema"),
    (langchain_sql_list, ["sql_url", "sql_table", "sql_name", "sql_pwd"], "sql_db_list_tables"),
    (langchain_sql_checker,
     ["model_name", "api_key", "api_base", "sql_url", "sql_table", "sql_name", "sql_pwd", "temperature"],
     "sql_db_query_checker")
]
# api tools
for tool in api_tools:
    register_api_tools(tool[0], tool[1], tool[2])

if __name__ == "__main__":
    import time
    from fel_langchain.langchain_schema_helper import dump_schema

    current_timestamp = time.strftime('%Y%m%d%H%M%S')
    dump_schema(function_tools, f"./tool_schema-{str(current_timestamp)}.json")