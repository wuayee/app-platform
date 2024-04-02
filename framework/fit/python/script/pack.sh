#！/bin/bash
# 在 FIT for Python 根目录中执行该脚本，将得到位于 output 目录中名为 fitframework-版本号.zip 的 FIT for Python 框架制品。
set -ex

if [ "$(basename "$(pwd)")" != "python" ] && [ ! -d "bootstrap" ]; then
  echo "The current directory is not the root of the FIT for Python project, The packaging process ends."
  exit 1
fi

export LC_ALL=C
# 通过 setup.py 提取框架的版本信息
FIT_VERSION=$(grep -o '_FIT_FRAMEWORK_VERSION\s*=\s*"[^"]\+"' setup.py | sed 's/_FIT_FRAMEWORK_VERSION\s*=\s*"//; s/"$//')
echo "FIT for Python version: $FIT_VERSION"

export OUTPUT_PATH=./output
export PACKED_PROJECT_NAME="fitframework-$FIT_VERSION"
export PACKED_PROJECT_PATH=./$OUTPUT_PATH/$PACKED_PROJECT_NAME
export SYSTEM_PLUGIN_PREFIX="fit_py_"

rm -rf $OUTPUT_PATH
mkdir -p $PACKED_PROJECT_PATH
mkdir -p $PACKED_PROJECT_PATH/plugin

cp -r ./bootstrap $PACKED_PROJECT_PATH
cp -r ./conf $PACKED_PROJECT_PATH
cp -r ./fit_common_struct $PACKED_PROJECT_PATH
cp -r ./fitframework $PACKED_PROJECT_PATH
cp -r ./plugin/$SYSTEM_PLUGIN_PREFIX* $PACKED_PROJECT_PATH/plugin
cp ./requirements.txt $PACKED_PROJECT_PATH

cd $OUTPUT_PATH
zip -q -r $PACKED_PROJECT_NAME.zip $PACKED_PROJECT_NAME || { echo "Zip operation failed"; exit 1;}
rm -rf ./$PACKED_PROJECT_NAME
cd ..

echo "FIT for Python project packed successfully."
