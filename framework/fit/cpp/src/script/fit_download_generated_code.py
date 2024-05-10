import os
import sys
import getopt
import json
import time
import zipfile
import requests


compile_module_config = {
    'protobuf': {
        'dir': 'protobuf',
        'enable': False
    },
    'json': {
        'dir': 'json',
        'enable': False
    },
    'c': {
        'dir': 'c',
        'enable': False
    }
}
recurse = 0
src_path = '.'
dst_path = './download'
url = 'http://fit.lab.huawei.com/definitions/sourceCodes'


def get_opt(argv):
    global compile_module_config
    global recurse
    global src_path
    global dst_path
    global url
    # parse args
    try:
        opts, args = getopt.getopt(argv, "hr:s:d:u:", ["help", "recurse=", "src_path=", "dst_path=", "url=", "module="])
    except getopt.GetoptError:
        usage()
        sys.exit(2)
    for opt, arg in opts:
        if opt in ('-h', '--help'):
            usage()
            sys.exit()
        elif opt in ('-r', '--recurse'):
            recurse = int(arg)
        elif opt in ('-s', '--src_path'):
            src_path = arg
        elif opt in ('-d', '--dst_path'):
            dst_path = arg
        elif opt in ('-e', '--url'):
            url = arg
        elif opt in ('--module'):
            dirs = arg.split(',')
            for name in dirs:
                compile_module_config[name]['enable'] = True


def main(argv):
    get_opt(argv)
    if not os.path.exists('%s/include/proto' % dst_path):
        os.makedirs('%s/include/proto' % dst_path)

    # parse genericable list
    dependency_file_list = acquire_dependency_file_list(src_path, recurse)
    print('dependency_file_list is: %s' % dependency_file_list)
    genericable_list = []
    for dependency_file in dependency_file_list:
        genericable_list.extend(parse_dependency(dependency_file))
    genericable_list = list(set(genericable_list))
    print('genericable_list is: %s' % genericable_list)

    # download
    download_files = download_source(genericable_list, url, dst_path)

    # uncompress
    for download_file in download_files:
        extracting = zipfile.ZipFile(os.path.join(dst_path, download_file))
        extracting.extractall(path=dst_path)
        extracting.close()
        os.remove(os.path.join(dst_path, download_file))

    # generate CMakeLists.txt
    with open(os.path.join(dst_path, 'CMakeLists.txt'), mode='w') as cmakelists_file:
        cmakelists_file.writelines('include_directories(${CMAKE_CURRENT_SOURCE_DIR})\n')
        if compile_module_config['protobuf']['enable']:
            cmakelists_file.writelines('include_directories(${CMAKE_CURRENT_SOURCE_DIR}/include/proto)\n')
            cmakelists_file.writelines('execute_process(COMMAND sh -c "protoc -I=${CMAKE_CURRENT_SOURCE_DIR}/public/proto \
            --cpp_out=${CMAKE_CURRENT_SOURCE_DIR}/include/proto serialization.proto")\n')
        cmake_add_converter_subdirectory(cmakelists_file, dst_path, 'component', compile_module_config)
        cmake_add_converter_subdirectory(cmakelists_file, dst_path, 'genericable', compile_module_config)


def cmake_add_converter_subdirectory(cmakelists_file, dst_path, target_dir, compile_module_config):
    for cmakelists in acquire_cmakelists(dst_path, target_dir):
        if cmakelists.endswith('c/service') and compile_module_config['c']['enable']:
            cmakelists_file.writelines('add_subdirectory(%s)\n' % (cmakelists))
            continue

        if cmakelists.endswith('formatter'):
            if compile_module_config['json']['enable']:
                cmakelists_file.writelines('add_subdirectory(%s/%s)\n' % (cmakelists, 'json'))
            if compile_module_config['protobuf']['enable']:
                cmakelists_file.writelines('add_subdirectory(%s/%s)\n' % (cmakelists, 'protobuf'))


def acquire_cmakelists(base_path, path):
    cmakelists = []
    if os.access(os.path.join(base_path, path, 'CMakeLists.txt'), mode=os.F_OK):
        cmakelists.append(path)
    elif os.path.exists(os.path.join(base_path, path)):
        for file in os.listdir(os.path.join(base_path, path)):
            if os.path.isdir(os.path.join(base_path, path, file)):
                cmakelists.extend(acquire_cmakelists(base_path, path + '/' + file))
    return cmakelists


def usage():
    print("Usage: %s [options]" % (os.path.basename(__file__)))
    help_str = "Options: \n"\
               "    -h, --help                         display this help and exit \n" \
               "    -s, --src_path <src path>          specify the source path, default is \"./\" \n" \
               "    -d, --dst_path <dest path>         \
               specify the destination path for download, default is \"./download\"\n"\
               "    --module <moudles:json,protobuf,c> generated code for json/protobuf converter, c api entry. \n"\
               "                                       usage: --module=json or --module=json,protobuf \n"\
               "    -r, --recurse <value>              \
               specify the depth of recursion relative to src_path, default is 0"
    print("%s" % help_str)


def acquire_dependency_file_list(path, recurse):
    dependency_file_list = []
    for file in os.listdir(path):
        filepath = os.path.join(path, file)
        if os.path.isfile(filepath) and file == 'fit_dependency.json':
            dependency_file_list.append(filepath)
        elif os.path.isdir(filepath) and recurse > 0:
            dependency_file_list.extend(acquire_dependency_file_list(filepath, recurse - 1))
    return dependency_file_list


def parse_dependency(config_file):
    genericable_list = []
    with open(config_file, 'r') as fp:
        json_data = json.load(fp)
    dependency = json_data['dependency']
    if 'genericables' in dependency:
        for item in dependency['genericables']:
            genericable_list.append((item['id'], item['version']))
    if 'genericables_c' in dependency:
        for item in dependency['genericables_c']:
            genericable_list.append((item['id'], item['version']))
    return genericable_list


def download_source(genericable_list, url, download_path):
    headers = {'user': '%e9%82%ac%e6%b6%a8%e8%b4%a2%2cp_fitlab'}
    json_data = {'request': {
        'genericableInfos': [
        ],
        'languages': [
            'C', 'CPlusPlus', 'Proto'
        ]
    }}
    for genericable in genericable_list:
        json_data['request']['genericableInfos'].append(
            {'genericableId': genericable[0], 'packageVersion': genericable[1], 'version': '1.0.0'})
    ret = []
    rsp = requests.post(url=url, json=json_data, headers=headers)
    json_rsp = json.loads(rsp.content)
    print('download result: %s' % json_rsp)
    if json_rsp['code'] is None or json_rsp['code'] is not 0:
        print('download fail!')
        exit(1)
    print('download result: %s' % ret)
    if 'data' in json_rsp.keys() and not json_rsp['data'] is None:
        for download_url in json_rsp['data']:
            download_file_name = get_download_file_name(download_url)
            download_rsp = requests.get(url=download_url)
            if not download_rsp.ok:
                exit(1)
            with open(os.path.join(download_path, download_file_name), 'wb') as code:
                code.write(download_rsp.content)
            ret.append(download_file_name)
    return ret


def get_download_file_name(url):
    file_name = ''
    if os.path.basename(url):
        file_name = os.path.basename(url).split('?')[0]
    else:
        file_name = time.time()
    return file_name


if __name__ == '__main__':
    main(sys.argv[1:])
