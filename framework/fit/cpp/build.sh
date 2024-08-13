#!/bin/bash

set -eu

script_file=$(readlink -f "$0")
script_dir=$(dirname "${script_file:?}")
current_dir="${script_dir}"
cpp_dir="${current_dir}"

echo "script_dir=${script_dir}"
echo "cpp_dir=${cpp_dir}"

build_options=${1:-"
    build_src:true,
    build_test:false,
    build_grpc:true,
    build_odbc:true,
    build_libpq:true,
    build_librdkafka:true,
    build_kms:true,
    build_ssl:true,
    install_third_party:false,
    build_type:debug,
    build_libcurl:true
    "}
fit_build_dir=${2:-"${cpp_dir}/build-tmp"}

build_src=$([[ "${build_options}" =~ "build_src:false" ]] && echo false || echo true)
build_test=$([[ "${build_options}" =~ "build_test:true" ]] && echo true || echo false)
build_grpc=$([[ "${build_options}" =~ "build_grpc:true" ]] && echo true || echo false)
build_librdkafka=$([[ "${build_options}" =~ "build_librdkafka:true" ]] && echo true || echo false)
build_odbc=$([[ "${build_options}" =~ "build_odbc:true" ]] && echo true || echo false)
build_libpq=$([[ "${build_options}" =~ "build_libpq:true" ]] && echo true || echo false)
build_kms=$([[ "${build_options}" =~ "build_kms:true" ]] && echo true || echo false)
build_ssl=$([[ "${build_options}" =~ "build_ssl:true" ]] && echo true || echo false)
build_type=$([[ "${build_options}" =~ "build_type:release" ]] && echo release || echo debug)
cmake_build_type=$([ "${build_type}" == "release" ] && echo Release || echo Debug)
cmake_build_test=$([ "${build_test}" == true ] && echo ON || echo OFF)
install_third_party=$([[ "${build_options}" =~ "install_third_party:true" ]] && echo true || echo false)
build_libcurl=$([[ "${build_options}" =~ "build_scc:true" ]] && echo true || echo false)
ext_cmake_args=()

echo "build_options=${build_options}"
echo "build_type=${build_type}"
echo "build_test=${build_test}"
echo "build_grpc=${build_grpc}"
echo "build_libpq=${build_libpq}"
echo "build_librdkafka=${build_librdkafka}"
echo "cmake_build_test=${cmake_build_test}"
echo "cmake_build_type=${cmake_build_type}"
cat /etc/os-release


third_party_output_dir=${cpp_dir}/third_party/output
mkdir -p ${third_party_output_dir}

if [ "${build_grpc}" == true ]; then
    cd ${cpp_dir}/third_party/grpc || exit
    rm -rf cmake/build

    # 这里需要和对应版本的行数匹配
    sed -i '3645c\if(gRPC_INSTALL AND NOT gRPC_USE_PROTO_LITE)' CMakeLists.txt
    mkdir -p cmake/build
    pushd cmake/build
    cmake -DCMAKE_BUILD_TYPE=Release -DBUILD_SHARED_LIBS=ON -DgRPC_INSTALL=ON \
        -DgRPC_BUILD_TESTS=OFF -DBUILD_TESTING=OFF -DRE2_BUILD_TESTING=OFF \
        -DgRPC_SSL_PROVIDER=package \
        -DgRPC_USE_PROTO_LITE=ON -DCMAKE_INSTALL_PREFIX=${cpp_dir}/third_party/grpc/install ../..
    make -j$(nproc)
    make install
    popd
    cp -arv ${cpp_dir}/third_party/grpc/install/* ${third_party_output_dir}
    if [ "${install_third_party}" == true ]; then
        cp -arv ${cpp_dir}/third_party/grpc/install/* /usr/local/
        ldconfig /usr/local/lib
    fi
    ext_cmake_args+=(-DFIT_ENABLE_GRPC=ON -DFIT_ENABLE_PROTOBUF=ON)
fi

########odbc##########
if [ "${build_odbc}" == true ]; then
    cd ${cpp_dir}/third_party/odbc
    tar -xf odbc.zip
    cp -arv ${cpp_dir}/third_party/odbc/* ${third_party_output_dir}
    if [ "${install_third_party}" == true ]; then
        cp -arv ${cpp_dir}/third_party/odbc/* /usr/local/
        ldconfig /usr/local/lib
    fi
    ext_cmake_args+=(-DFIT_ENABLE_ODBC=ON)
fi

########libpq##########
if [ "${build_libpq}" == true ]; then
    cd "${cpp_dir}/third_party/pgsql"
    rm -rf install
    mkdir install
    ./configure --disable-rpath --quiet --without-readline --with-openssl --prefix="$(pwd)/install" CFLAGS="-O3 -fPIC -fstack-protector-strong -Wl,-z,relro,-z,now,-z,noexecstack -s"
    # build only libpq & its dependencies in pgsql
    make -C src/include install -j$(nproc)
    make -C src/common install -j$(nproc)
    make -C src/port install -j$(nproc)
    make -C src/interfaces/libpq install -j$(nproc)
    cp -arv "${cpp_dir}"/third_party/pgsql/install/* "${third_party_output_dir}"
    if [ "${install_third_party}" == true ]; then
        cp -arv "${cpp_dir}"/third_party/pgsql/install/* /usr/local
        ldconfig /usr/local/lib
    fi
    ext_cmake_args+=(-DFIT_ENABLE_PGSQL=ON)
fi

#########librdkafka#########
if [ "${build_librdkafka}" == true ]; then
    cd ${cpp_dir}/third_party/librdkafka
    rm -rf build
    mkdir -p build && cd build || exit
    cmake -DCMAKE_BUILD_TYPE=Release -DWITH_BUNDLED_SSL=OFF -DWITH_SSL=OFF -DRDKAFKA_BUILD_TESTS=OFF \
        -DCMAKE_INSTALL_PREFIX=${cpp_dir}/third_party/librdkafka/install ..
    cmake --build . --config Release -- -j$(nproc)
    make install
    cp -arv ${cpp_dir}/third_party/librdkafka/install/* ${third_party_output_dir}
    if [ "${install_third_party}" == true ]; then
        cp -ar ${cpp_dir}/third_party/librdkafka/install/* /usr/local/
        ldconfig /usr/local/lib
    fi
    ext_cmake_args+=(-DFIT_ENABLE_LIBRDKAFKA=ON)
fi

#########libcurl#########
if [ "${build_libcurl}" == true ]; then
    cd ${cpp_dir}/third_party/curl
    rm -rf build
    mkdir -p build && cd build || exit

    cmake -DCURL_USE_OPENSSL=ON \
    -DBUILD_SHARED_LIBS=ON -DBUILD_TESTING=OFF -DENABLE_DEBUG=Release \
    -DCMAKE_INSTALL_PREFIX=${cpp_dir}/third_party/curl/install \
    -DCMAKE_BUILD_TYPE=Release ..
    make install

    if [ -d "${cpp_dir}/third_party/curl/install/lib64" ]; then
        mv ${cpp_dir}/third_party/curl/install/lib64 ${cpp_dir}/third_party/curl/install/lib
    fi

    cp -arv ${cpp_dir}/third_party/curl/install/* ${third_party_output_dir}
    if [ "${install_third_party}" == true ]; then
        cp -ar ${cpp_dir}/third_party/curl/install/* /usr/local/
        ldconfig /usr/local/lib
    fi
    ext_cmake_args+=(-DFIT_ENABLE_LIBCURL=ON)
fi

########kms##########
if [ "${build_kms}" == true ]; then
    bash ${cpp_dir}/src/script/generate_binary_file.sh "${fit_build_dir}/bin" ${cpp_dir}/src/script
    ext_cmake_args+=(-DFIT_ENABLE_KMS=ON)
fi

########ssl##########
if [ "${build_ssl}" == true ]; then
    ssl_install_dir=${cpp_dir}/third_party/openssl/install
    rm -rf ${ssl_install_dir}/lib
    mkdir -p ${ssl_install_dir}/lib
    find /usr -name libssl.so.1.1 | xargs -I {} cp -adv {} ${ssl_install_dir}/lib
    find /usr -name libcrypto.so.1.1 | xargs -I {} cp -adv {} ${ssl_install_dir}/lib
fi

########ssl-scc##########
sec_component_path="/usr/local/seccomponent/"
export CPLUS_INCLUDE_PATH=${sec_component_path}
########compile##########
if [ "${build_src}" == true ]; then
    cd "${cpp_dir}"
    mkdir -p "${fit_build_dir}"
    cd "${fit_build_dir}" || exit

    defaultCmakeFlags=(
        -DCMAKE_BUILD_TYPE="${cmake_build_type}"
        -DFIT_BUILD_TESTS="${cmake_build_test}"
        -DFIT_THIRD_PARTY_OUTPUT_DIR="${third_party_output_dir}"
        -DGRPC_CPP_PLUGIN="${third_party_output_dir}"/bin/grpc_cpp_plugin
        -DSEC_COMPONENT_PATH="${sec_component_path}/lib"
        ${ext_cmake_args[*]}
    )

    old_path=${PATH:-}
    old_ld_path=${LD_LIBRARY_PATH:-}
    export PATH="${third_party_output_dir}"/bin:${PATH}
    export LD_LIBRARY_PATH="${third_party_output_dir}"/lib:"${third_party_output_dir}"/lib64:${LD_LIBRARY_PATH:-}

    cmake -B . "${defaultCmakeFlags[@]}" ..
    export PATH="${old_path}"
    export LD_LIBRARY_PATH="${old_ld_path}"
    cmake --build . -- -j$(nproc)
    if [ "${build_test}" == true ]; then
        cov_start_time=`date +%s`
        cmake --build . --target cov > "${current_dir}"/cov.txt || (cat "${current_dir}"/cov.txt)
        cov_end_time=`date +%s`
        cov_cost_time=$[ $cov_end_time - $cov_start_time ]
        echo "cov_cost_time=${cov_cost_time}"
        echo "cov_cost_time=${cov_cost_time}" >> "${current_dir}"/cov.txt
    fi
fi
