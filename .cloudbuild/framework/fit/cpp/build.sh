#!/bin/bash

set -eu

FIT_PATH="../../../../framework/fit"
FIT_CPP_PATH="$FIT_PATH/cpp"

cd $FIT_CPP_PATH
sh prepare.sh
cd -

./build_cpp_registry.sh a3000_pgsql build_ssl:true,build_libpq:true,build_scc:true,build_libcurl:true $FIT_PATH debug x86_64