#!/usr/bin/env python3
import getopt
import json
import os
import sys
import zipfile

import requests

print("参数个数为:", len(sys.argv), "个参数。")
print("参数列表:", str(sys.argv))
DEPENDENCY_FILE = ""
OUTPUT_DIR = ""
OUTPUT_HEADER_DIR = ""
USE_TEST = False
TMP_DIR = "/tmp"

try:
    opts, args = getopt.getopt(sys.argv[1:], "d:o:t:h:", ["dependfile=", "outlibdir=", "test=", "outheaderdir="])
    print(opts)
    for o, a in opts:
        if o in ("-d", "--dependfile"):
            DEPENDENCY_FILE = a
        if o in ("-o", "--outlibdir"):
            OUTPUT_DIR = a
        if o in ("-t", "--test"):
            USE_TEST = (a == "true")
        if o in ("-h", "--outheaderdir"):
            OUTPUT_HEADER_DIR = a
except getopt.GetoptError:
    print("option like: -d <dependfile> -o <outlibdir> -h <outheaderdir>")
    sys.exit(2)

print("Dependency file = {}\noutput dir = {}".format(DEPENDENCY_FILE, OUTPUT_DIR))

FIT_DEP_URL = "http://fit.lab.huawei.com/plugins/genericables/dependencies"
FIT_DEP_TEST_URL = "http://daily-fit.lab.huawei.com/plugins/genericables/dependencies"


def download_so_file(url: str):
    so_name = url.split('/')[-1]
    so_path = os.path.join(OUTPUT_DIR, so_name)

    res = requests.get(url)
    if res.status_code != 200:
        print("Error: download so file failed, target url is {}".format(url))
        exit(1)

    with open(so_path, 'wb') as f:
        f.write(res.content)


def download_zip_file(url: str, path: str):
    res = requests.get(url)
    if res.status_code != 200:
        print("Error: download zip file failed, target url is {}".format(url))
        exit(1)

    zip_path = os.path.join(TMP_DIR, "tmp.zip")
    with open(zip_path, 'wb') as f:
        f.write(res.content)

    with zipfile.ZipFile(zip_path, 'r') as f:
        f.extractall(path)


def download_generic_deps(genericable: dict, lang: str):
    gid = genericable["id"]
    print("Download dependencies for {}".format(gid))

    if lang != "CPlusPlus" and lang != "Cpp":
        return

    url = FIT_DEP_URL
    if USE_TEST:
        print("use test plat api : ", FIT_DEP_TEST_URL)
        url = FIT_DEP_TEST_URL

    querystring = {"genericableVersionName": "1.0.0", "language": lang,
                   "genericId": gid}
    headers = {"user": "a,a"}

    response = requests.request(
        "PUT", url, headers=headers, params=querystring)
    if response.status_code != 200:
        print("Error: failed to fetch genericable dependency info!")
        exit(1)

    print("response is = ", response.json())
    deps = response.json()["data"]

    for dep in deps:
        for dep_url in dep["urls"]:
            if dep_url["type"] == "header" and OUTPUT_HEADER_DIR != "":
                # download head and unzip to .dependency/
                download_zip_file(dep_url["url"], OUTPUT_HEADER_DIR)
            elif dep_url["type"].endswith("So") and genericable.get("local_only", False) is False:
                # download so file and unzip to lib/
                download_so_file(dep_url["url"])


if __name__ == "__main__":
    os.makedirs(OUTPUT_DIR, exist_ok=True)

    config_path = DEPENDENCY_FILE
    with open(config_path, 'r') as f:
        config = json.load(f)

    for generic in config["dependency"]["genericables"]:
        download_generic_deps(generic, "CPlusPlus")

    if "genericables_c" in config["dependency"]:
        for c_generic in config["dependency"]["genericables_c"]:
            download_generic_deps(c_generic, "Cpp")
