# 打包方式：在 python 目录下执行 python setup.py sdist bdist_wheel
import setuptools

_FIT_FRAMEWORK_VERSION = "1.0.1.dev"

setuptools.setup(
    name="fit",
    version=_FIT_FRAMEWORK_VERSION,
    author="fit",
    url="https://gitlab.huawei.com/fitlab/fit",
    packages=setuptools.find_packages(
        exclude=["*.tests", "*.tests.*", "tests.*", "tests", "_test.*", "_test"]),
    classifiers=[
        "Programming Language :: Python :: 3",
        "License :: Huawei license",
        "Operating System :: OS Independent",
    ],
    install_requires=["PyYAML==6.0.1",
                      "flask==2.2.5",
                      "numpy==1.25.2",
                      "watchdog==3.0.0",
                      "requests==2.31.0"],
    python_requires='==3.9.11'
)
