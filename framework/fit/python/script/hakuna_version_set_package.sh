# TODO 增加使用说明
# TODO out-dir如果不存在，应该自动创建
# TODO 增加使用说明
# default value, which could be written by external command arguments.
# 外部指定了--not-compress，则不压缩；外部指定了--package-name，则压缩（默认）

shopt -s nullglob

# 路径相关的输入参数
IN_FRAMEWORK_DIR="fit/core/python/"
#IN_FRAMEWORK_SUB_PATHS_TO_PACK="{fitframework,conf,script}" # 注意`,`之间不能加空格！
IN_BOOTSTRAP_DIR="fit/plugin-bootstrap/"
IN_SYS_PLUGIN_DIR="fit/plugin/"

# 路径相关的输出参数
OUT_VERSION="1.0.0"
OUT_DIR="hakuna/"
OUT_PACKAGE_NAME="fit_hakuna_python_$OUT_VERSION.tar.gz"
DO_COMPRESS=true

# 其他内部的路径相关输出参数
_COMMON_STRUCT_PACK_DIR="fit_common_struct"
_THIRD_PARTY_PACK_DIR="third_party"
_COMMON_STRUCT_REQUIREMENTS="common_struct_requirements.txt"
_THIRD_PARTY_REQUIREMENTS="third_party_requirements.txt"


function copy_as_a_different_structure {
  echo -e "\n---------------------------- copying source codes from repository... ----------------------------"
  mkdir -p "$OUT_DIR/$_COMMON_STRUCT_PACK_DIR" "$OUT_DIR/$_THIRD_PARTY_PACK_DIR"
  cp -a "$IN_FRAMEWORK_DIR"/{fitframework,conf,script} "$OUT_DIR/"
  echo "$OUT_VERSION" > "$OUT_DIR/conf/fit.out-version"

  rsync -a "$IN_BOOTSTRAP_DIR"/fit_py_*/fit_py_* "$OUT_DIR/bootstrap/"  # will create `bootstrap` in dst automatically.
  rsync -a "$IN_SYS_PLUGIN_DIR"/fit_py_*/fit_py_* "$OUT_DIR/system_plugin/"  # will create `system_plugin` in dst automatically.
  mkdir -p "$OUT_DIR/user_plugin"
}

function collect_dependencies {
  local requirements_filename="$1"
  local sub_out_dir="$2"
  echo -e "\n---------------------------- collecting dependencies from $requirements_filename... ----------------------------"

  for full_filename in \
      "$IN_FRAMEWORK_DIR/$requirements_filename" \
      "$IN_SYS_PLUGIN_DIR"/fit_py_*/"$requirements_filename" \
      "$IN_BOOTSTRAP_DIR"/fit_py_*/"$requirements_filename" ; do
    full_filename=$(readlink -f "$full_filename")  # 正规化
    local new_filename
    # e.g. given `full_filename`: xxx1/xxx2/xxx3/sample.txt
    # -> then `new_filename` is: xxx3_sample.txt
    new_filename=$(echo "$full_filename" | sed -r 's/.*\/(.*)\/(.*)$/\1_\2/')
    cp "$full_filename" "$OUT_DIR/$sub_out_dir/$new_filename"
  done
}

function package() {
  #  the packaged hakuna structure is shown below (`src` is the output directory):
  #
  #  src
  #├── common
  #├── conf
  #├── core
  #├── system_plugin
  #├── script
  #├── bootstrap
  #└── third_party
  #
  if [ "$DO_COMPRESS" = true ] ; then
    echo -e "\n---------------------------- packaging as $OUT_PACKAGE_NAME to $OUT_DIR... ----------------------------"
    tar -czvf "$OUT_DIR"/"$OUT_PACKAGE_NAME" -C "$OUT_DIR" ./ --warning=no-file-changed
  else
    echo -e "\n---------------------------- packaging directly to $OUT_DIR... ----------------------------"
  fi
}

function parse_args() {
    while [[ $# -gt 0 ]]; do
        option=$1; shift
        case ${option} in
            --out-version=*) OUT_VERSION=${option#*=}; OUT_PACKAGE_NAME="hakuna_python_source_$OUT_VERSION.tar.gz" ;;
            --in-framework-dir=*) IN_FRAMEWORK_DIR=${option#*=} ;;
            --in-bootstrap-dir=*) IN_BOOTSTRAP_DIR=${option#*=} ;;
            --in-sys-plugin-dir=*) IN_SYS_PLUGIN_DIR=${option#*=} ;;
            --not-compress) DO_COMPRESS=false ;;
            --package-name=*) OUT_PACKAGE_NAME="${option#*=}.tar.gz"; DO_COMPRESS=true ;;
            --out-dir=*) OUT_DIR=${option#*=} ;;
            *) ;; # 可以在此处设置默认处理逻辑
        esac
    done
}

function main() {
    parse_args "$@"
    copy_as_a_different_structure
    collect_dependencies "$_COMMON_STRUCT_REQUIREMENTS" "$_COMMON_STRUCT_PACK_DIR"
    collect_dependencies "$_THIRD_PARTY_REQUIREMENTS" "$_THIRD_PARTY_PACK_DIR"
    package
}


################################# main #################################
main "$@"
