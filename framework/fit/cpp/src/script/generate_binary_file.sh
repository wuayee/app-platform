outpath=$1
exec_path=$2
pyinstaller -F $exec_path/get_static_token.py --distpath=$outpath
pyinstaller -F $exec_path/get_config_from_config_center.py --distpath=$outpath
echo "generate++++++++++++++++++++++"
rm -rf get_static_token.spec
rm -rf get_config_from_config_center.spec
rm -rf __pycache__