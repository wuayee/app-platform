# TODO 添加使用说明
COMMON_STRUCT_FULL_DIR="hakuna/fit_common_struct"
THIRD_PARTY_FULL_DIR="hakuna/third_party"
PY_PATH="python3"

_COMMON_STRUCT_REQUIREMENTS="common_requirements.txt"
_2ND_PARTY_PYPI_URI="http://language.cloudartifact.dgg.dragon.tools.huawei.com/artifactory/api/pypi/product_pypi/simple/"
_2ND_PARTY_PYPI_HOST="language.cloudartifact.dgg.dragon.tools.huawei.com"
_THIRD_PARTY_REQUIREMENTS="third_party_requirements.txt"
_3RD_PARTY_PYPI_URI="http://mirrors.tools.huawei.com/pypi/simple"
_3RD_PARTY_PYPI_HOST="mirrors.tools.huawei.com"


function download_dependencies {
  local requirements_full_dir="$1"
  local requirements_filename="$2"
  local pypi_uri="$3"
  local pypi_host="$4"
  echo -e "\n---------------------------- downloading dependencies from $requirements_filename... ----------------------------"

  # 注意此处的--no-binary: protobuf不能以python源码方式直接安装，需要编译安装
  awk '{print}' "$requirements_full_dir"/*"$requirements_filename" | sort -u > ".all_requirements.txt"
  $PY_PATH -m pip install --no-binary=protobuf \
    --upgrade -i "$pypi_uri" --trusted-host "$pypi_host" \
    -t "$requirements_full_dir" -r ".all_requirements.txt"
#  echo "$requirements_full_dir"/*"$requirements_filename" | xargs rm
}

function parse_args() {
    while [[ $# -gt 0 ]]; do
        option=$1; shift
        case ${option} in
            --common-struct-dir=*) COMMON_STRUCT_FULL_DIR="${option#*=}" ;;
            --third-party-dir=*) THIRD_PARTY_FULL_DIR="${option#*=}" ;;
            --py-path=*) PY_PATH="${option#*=}" ;;
            *) ;; # 可以在此处设置默认处理逻辑
        esac
    done
}

function main() {
    parse_args "$@"
    download_dependencies "$COMMON_STRUCT_FULL_DIR" "$_COMMON_STRUCT_REQUIREMENTS" "$_2ND_PARTY_PYPI_URI" "$_2ND_PARTY_PYPI_HOST"
    download_dependencies "$THIRD_PARTY_FULL_DIR" "$_THIRD_PARTY_REQUIREMENTS" "$_3RD_PARTY_PYPI_URI" "$_3RD_PARTY_PYPI_HOST"
}


################################# main #################################
main "$@"
