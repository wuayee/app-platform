'''
Author: your name
Date: 2022-03-04 10:52:31
LastEditTime: 2022-03-07 09:43:32
LastEditors: Please set LastEditors
Description: In User Settings Edit
FilePath: \fit\core\cpp\src\script\fit_get_genericable_ids.py
'''
import os
import sys
import getopt
import json


def main(argv):
    dep_files = []
    # parse args
    try:
        opts, args = getopt.getopt(argv, "hd:", ["help", "dependency_file="])
    except getopt.GetoptError:
        usage()
        sys.exit(2)
    for opt, arg in opts:
        if opt in ('-h', '--help'):
            usage()
            sys.exit()
        elif opt in ('-d', '--dependency_file'):
            dep_files.append(arg)

    genericable_list = []
    for dep_file in dep_files:
        genericable_list.extend(parse_dependency(dep_file))
    genericable_list = list(set(genericable_list))
    print(','.join(genericable_list))


def usage():
    print("Usage: %s [options]" % (os.path.basename(__file__)))
    help_str = "Options: \n"\
               "    -d <dependency_file> dependency file, support mutil -d"
    print("%s" % help_str)


def parse_dependency(config_file):
    genericable_list = []
    with open(config_file, 'r') as fp:
        json_data = json.load(fp)
    dependency = json_data['dependency']
    if 'genericables' in dependency:
        for item in dependency['genericables']:
            genericable_list.append(item['id'])
    if 'genericables_c' in dependency:
        for item in dependency['genericables_c']:
            genericable_list.append(item['id'])
    return genericable_list


if __name__ == '__main__':
    main(sys.argv[1:])
