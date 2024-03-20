#!/bin/bash
# Copyright (c) Huawei Technologies Co., Ltd. 2024. All rights reserved.
# Description: databus core prepare third-party source

# Description: download git-mm if not exits
# User input: y/n for download or not
# Returns: 0 for success, other nums for fail
function ensure_git_mm() {
    if ! command -v "git-mm" &> /dev/null; then
        echo -e "no git-mm found!\nDownload now? [y/N]"
        read -r need_git_mm
        if [[ ${need_git_mm} == "y" || ${need_git_mm} == "Y" ]]; then
            check_git_mm_environment
            install_git_mm
            rm -rf "$HOME/.git-mm/logs" "$HOME/.git-mm/last-check-update.txt"
        else
            return 1
        fi
    fi
}

# Description: download third-party source
# Arguments:
#     $1: third_party.mm.xml directory
#     $2: lib path. download all if blank
#     $3: tmp folder for git mm
#     $4: git mm xml filename
# Returns: 0 for success, other nums for fail
function prepare_third_party() {
    working_dir=${1:-$(readlink -f "$(dirname "$0")/../third_party")}
    tmp_folder=${3:-".dummy_git_mm"}
    xml_filename=${4:-"third_party.mm.xml"}
    ensure_git_mm || return 1
    if [ ! -f "${working_dir}/${xml_filename}" ]; then
        echo "no such file: ${working_dir}/${xml_filename}"
        return 1
    fi
    cd "${working_dir}" || return 2
    clean_temp_files
    prepare_dummy_git "${working_dir}"
    cd "${working_dir}" || return 2
    git mm init -u "${tmp_folder}/" -m "${xml_filename}" -b main
    if [ $# -eq 1 ]; then
        git mm sync -d --force-sync || clean_temp_files
    else
        git mm sync -d --force-sync "$2" || clean_temp_files
    fi
    clean_temp_files
}

function prepare_dummy_git() {
    working_dir=$1
    xml_filename=${2:-"third_party.mm.xml"}
    tmp_folder=${3:-".dummy_git_mm"}
    # git mm needs a dummy git url for reading manifest files
    cd "$1" || return 1
    rm -rf "${tmp_folder}" && mkdir -p "${tmp_folder}"
    cp "${xml_filename}" "${tmp_folder}/"
    cd "${tmp_folder}" || return 1
    git init . -b main && git config user.email "git_mm" && git config user.name "git_mm"
    git add "${xml_filename}" && git commit -m "dummy git record for git mm"
}

function clean_temp_files() {
    # clean dummy_git
    rm -rf "${tmp_folder}"
    # clean git-mm
    rm -rf .mm
}

# Description: check environment for git-mm and decide which version to install
# Returns: 0 for success, other nums for fail
function check_git_mm_environment() {
    if ! command -v "uname" &> /dev/null; then
        echo "[ERROR] I could not find 'uname' tool, please check it."
        return 1
    fi
    if ! command -v "curl" &> /dev/null; then
        echo "[ERROR] I could not find 'curl' tool, please check it."
        return 1
    fi

    OS=$(uname -o | tr '[:upper:]' '[:lower:]')
    ARCH=$(uname -m | tr '[:upper:]' '[:lower:]')
    ARCH_NAME=""
    FILE_NAME="git-mm"
    echo "[INFO] verify the package that need to download."

    case ${OS} in
        "gnu/linux" | "linux-gnu")
            case "${ARCH}" in
                "x86_64") ARCH_NAME="linux64";;
                "aarch64") ARCH_NAME="arm64";;
                *) ARCH_NAME="linux32";;
            esac;;
        "msys")
            FILE_NAME="git-mm.exe"
            case "${ARCH}" in
                "x86_64") ARCH_NAME="win64";;
                *) ARCH_NAME="win32";;
            esac;;
        *)
            if test "x${ARCH}" = "xx86_64"; then
                ARCH_NAME="darwin64"
            fi;;
    esac
    if [[ -z ${ARCH_NAME} ]]; then
        echo "[ERROR] git-mm does not support your system."
        return 1
    fi
    mkdir -p "$HOME/bin"

    # Check that $HOME/bin is in PATH
    if [[ ":$PATH:" != *":$HOME/bin:"* ]]; then
      # Add export to .bashrc
      echo "export PATH=""$HOME"/bin:"$PATH""" >> ~/.bashrc
      # Update the current PATH
      export PATH="$HOME/bin:$PATH"
      echo "[INFO] $HOME/bin has been added to PATH."
    fi
}

# Description: download and install git-mm v3.1.0
# Returns: 0 for success, other nums for fail
function install_git_mm() {
    # default to download and install v3.1.0
    local url_prefix="https://cmc-szver-artifactory.cmc.tools.huawei.com/artifactory/cmc-software-release/CodeHub/git-mm/3.1.0"
    local bin_path="$HOME/bin"
    URL="${url_prefix}/${ARCH_NAME}/${FILE_NAME}"
    echo "[INFO] curl -k ${URL} -o ${FILE_NAME}"

    curl -k "${URL}" -o "${FILE_NAME}" || ( echo "[ERROR] Download git-mm package failed" && return 1 )
    echo "[INFO] Installing git-mm to ${bin_path}"
    (mv "${FILE_NAME}" "${bin_path}/" && chmod 755 "${bin_path}/${FILE_NAME}") || echo "[ERROR] could install git-mm to ${bin_path}"
    echo "[INFO] git mm version"
    if git mm version; then
        echo "[INFO] Congratulate, git-mm is installed successfully."
    else
        echo "[ERROR] could install git-mm to ${bin_path}."
        return 1
    fi
}
