#!/bin/bash

set -eu

script_file=$(readlink -f "$0")
script_dir=$(dirname "${script_file:?}")
current_dir="${script_dir}"
cpp_dir="${current_dir}"
cpp_third_party_dir="${cpp_dir}/third_party"

arch_type=$(uname -m)

case "${arch_type}" in
    x86_64)
        cmc_arch_dir=x86
        ;;
    aarch64)
        cmc_arch_dir=arm
        ;;
esac

bash "${cpp_third_party_dir}"/clone.mm.sh

###############准备python环境############
version=$(lsb_release -rs)
if [ $version = "18.04" ]; then
    cat > /etc/apt/sources.list << EOF
    deb http://mirrors.tools.huawei.com/ubuntu/ bionic main multiverse restricted universe
    deb http://mirrors.tools.huawei.com/ubuntu/ bionic-backports main multiverse restricted universe
    deb http://mirrors.tools.huawei.com/ubuntu/ bionic-proposed main multiverse restricted universe
    deb http://mirrors.tools.huawei.com/ubuntu/ bionic-security main multiverse restricted universe
    deb http://mirrors.tools.huawei.com/ubuntu/ bionic-updates main multiverse restricted universe
    deb-src http://mirrors.tools.huawei.com/ubuntu/ bionic main multiverse restricted universe
    deb-src http://mirrors.tools.huawei.com/ubuntu/ bionic-backports main multiverse restricted universe
    deb-src http://mirrors.tools.huawei.com/ubuntu/ bionic-proposed main multiverse restricted universe
    deb-src http://mirrors.tools.huawei.com/ubuntu/ bionic-security main multiverse restricted universe
    deb-src http://mirrors.tools.huawei.com/ubuntu/ bionic-updates main multiverse restricted universe
EOF
fi
trusted_source_command="--index-url https://cmc-cd-mirror.rnd.huawei.com/pypi/simple/ --extra-index-url https://cmc.centralrepo.rnd.huawei.com/artifactory/product_pypi/simple --trusted-host cmc-cd-mirror.rnd.huawei.com --trusted-host cmc.centralrepo.rnd.huawei.com"
/usr/bin/python3 -m pip install --upgrade pip $trusted_source_command
/usr/bin/python3 -m pip install hw-uniai-his-decrypt==1.5.5 $trusted_source_command
/usr/bin/python3 -m pip install pyinstaller $trusted_source_command
/usr/bin/python3 -m pip install pybase64 $trusted_source_command
pyinstaller --version
python3 -V

if [ -d "${current_dir}/build" ]; then
    rm -rf ${current_dir}/build
fi
mkdir ${current_dir}/build
mkdir ${current_dir}/build/bin
bash "${current_dir}/src/script"/generate_binary_file.sh "build/bin" ${current_dir}/src/script

curl -k https://cmc-lfg-artifactory.cmc.tools.huawei.com/artifactory/cbu-common-general/seccomponent/1.1.8/seccomponent-1.1.8-release.x86_64.rpm -o seccomponent-1.1.8-release.x86_64.rpm
rpm -ivh seccomponent-1.1.8-release.x86_64.rpm --force --nodeps