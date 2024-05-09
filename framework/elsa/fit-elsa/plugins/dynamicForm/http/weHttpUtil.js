/**
 * we码请求.
 */
export const weHttpUtil = () => {
    const self = {};

    /**
     * get请求.
     *
     * @param url 请求的url地址.
     * @param callback 回调.
     * @param headers 请求头.
     */
    self.get = (url, callback, headers = new Map()) => {
        const weHeaders = {"Content-Type": "application/json"};
        headers.forEach((v, k) => weHeaders[k] = v)
        HWH5.fetch(url, {method: 'get', headers: weHeaders, timeout: 6000})
            .then((res) => res.json())
            .then((reply) => callback(reply))
            .catch((error) => {
                throw new Error("Get request[" + url + "] failed: " + error);
            });
    };

    /**
     * post请求.
     *
     * @param url 请求的url地址.
     * @param data 请求的数据.
     * @param callback 回调.
     * @param headers 请求头.
     */
    self.post = (url, data, callback, headers = new Map()) => {
        const weHeaders = {"Content-Type": "application/json"};
        headers.forEach((v, k) => weHeaders[k] = v)
        HWH5.fetch(url, {method: 'post', body: JSON.stringify(data), headers: weHeaders})
            .then((res) => res.json())
            .then((reply) => callback(reply))
            .catch((error) => {
                throw new Error("Post request[" + url + "] failed: " + error);
            });
    };

    /**
     * put请求.
     *
     * @param url 请求的url地址.
     * @param data 请求的数据.
     * @param callback 回调.
     * @param headers 请求头.
     */
    self.put = (url, data, callback, headers = new Map()) => {
        const weHeaders = {"Content-Type": "application/json"};
        headers.forEach((v, k) => weHeaders[k] = v)
        HWH5.fetch(url, {method: 'put', body: JSON.stringify(data), headers: weHeaders})
            .then((res) => res.json())
            .then((reply) => callback(reply))
            .catch((error) => {
                throw new Error("Put request[" + url + "] failed: " + error);
            });
    };

    /**
     * 文件上传.
     *
     * @param url 接口地址.
     * @param file 文件对象.
     * @param processCallback 上传进度处理回调.
     * @param callback 上传完成或失败回调.
     * @param headers 请求头.
     * @return XMLHttpRequest 请求对象.
     */
    self.uploadFile = async (url, file, processCallback, callback, headers = new Map()) => {
        console.log("============== welink test: url[" + url + "]");
        console.log("============== welink test: file[" + JSON.stringify(file) + "]");
        console.log("============== welink test: headers: ", JSON.stringify(headers));
        const weHeaders = {};
        headers.forEach((v, k) => {
            weHeaders[k] = v;
        });
        console.log("============== welink test: weHeaders: ", JSON.stringify(weHeaders));
        const uploadUrl = url.replace("ProxyForText", "ProxyForUpload");
        return await HWH5.uploadFileOperation({
            serverUrl: uploadUrl,
            filePath: file.path,
            name: 'file',
            formData: {},
            headers: weHeaders,
            timeout: 60 * 1000 * 10, // 10分钟超时.
            progress: 1,
            onProgress: (_data) => {
                console.log("============== welink test: progressData[" + JSON.stringify(_data) + "]");
                processCallback(_data.progress);
            }, onSuccess: (res) => {
                console.log("============== welink test: res[" + JSON.stringify(res) + "]");
                callback(res);
            }, onError: (err) => {
                throw new Error("Upload file[" + file.path + "] failed: " + err);
            }
        });

        // todo@zhangyue 暂时不删除.
        // HWH5.uploadFile({
        //     zip: true,
        //     serverType: 0,
        //     serverUrl: url,
        //     filePath: file.path,
        //     name: 'file',
        //     headers: weHeaders,
        //     formData: {},
        //     timeout: 60 * 1000 * 10, // 10分钟超时.
        //     progress: 1,
        //     onProgress: (_data) => {
        //         console.log("============== welink test: progressData[" + JSON.stringify(_data) + "]");
        //         processCallback(_data.progress);
        //     }
        // })
        //     .then((data) => {
        //         console.log("============== welink test: res[" + JSON.stringify(data) + "]");
        //         callback(data);
        //     })
        //     .catch((error) => {
        //         console.log('============== welink test, error: ', error);
        //         throw new Error("Upload file[" + file.path + "] failed: " + error);
        //     });
    };

    return self;
};