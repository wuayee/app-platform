# -- encoding: utf-8 --
# Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
"""
功 能：scc 加解密操作
"""
import ctypes
import threading

from fitframework.api.decorators import value, fitable

SEC_SUCCESS = 0
SEC_TRUE = 1
SEC_FALSE = 0
DEFAULT_DOMAIN_ID = 0
DEFAULT_KEY_LEN = 32
KEYID_AUTO_GET = 0

gInitFlag = False
lock = threading.Lock()


@value("scc.conf-file")
def get_conf_file():
    pass


@value("scc.sc-secrypto-file")
def get_sc_secrypto_file():
    pass


class CryptoAPI(object):
    def __init__(self):
        self.pdll = ctypes.cdll.LoadLibrary(get_sc_secrypto_file())

    def encode(self, value):
        if isinstance(value, bytes):
            return value
        else:
            return value.encode('UTF-8')

    def decode(self, value):
        if isinstance(value, bytes):
            return value.decode('UTF-8')
        else:
            return value

    def initialize(self, confFile):
        result = None
        if confFile is None:
            raise Exception('initialize input confFile is None.')
        else:
            confFileAsc = self.encode(confFile)
            try:
                lock.acquire()
                global gInitFlag
                if gInitFlag == True:
                    raise Exception('Repeat to call Initialize. ')
                else:
                    result = self.pdll.SCC_Initialize(confFileAsc)
                    if result == SEC_SUCCESS:
                        gInitFlag = True
            except Exception as e:
                raise Exception('Initialize Failed. Exception=' + str(e))
            finally:
                lock.release()
                if result == SEC_SUCCESS:
                    pass
                elif result == None:
                    pass
                elif result == 1001:
                    raise Exception('Initialize Failed. Error=ERR_NO_INIT,ErrCode=' + str(result))
                elif result == 1002:
                    raise Exception('Initialize Failed. Error=ERR_REPEAT_INIT,ErrCode=' + str(result))
                elif result == 1003:
                    raise Exception('Initialize Failed. Error=ERR_INIT_SYS_LOG_FAIL,ErrCode=' + str(result))
                elif result == 1004:
                    raise Exception('Initialize Failed. Error=ERR_INIT_LOG_FAIL,ErrCode=' + str(result))
                elif result == 1005:
                    raise Exception('Initialize Failed. Error=ERR_INIT_CFG_FAIL,ErrCode=' + str(result))
                elif result == 2001:
                    raise Exception('Initialize Failed. Error=ERR_INIT_KMC_FAIL,ErrCode=' + str(result))
                elif result == 2002:
                    raise Exception('Initialize Failed. Error=ERR_INIT_DOMAIN_FAIL,ErrCode=' + str(result))
                else:
                    raise Exception('Initialize Failed. ErrCode=' + str(result))
        return result

    def finalize(self):
        result = None
        try:
            lock.acquire()
            result = self.pdll.SCC_Finalize()
            global gInitFlag
            gInitFlag = False
        except Exception as e:
            raise Exception('Finalize  Failed. Exception=' + str(e))
        finally:
            lock.release()
            if result != SEC_SUCCESS:
                raise Exception('Finalize  Failed. result=' + str(result))
        return result

    def encrypt(self, plain, domainId=DEFAULT_DOMAIN_ID):
        result = None
        if plain is None:
            raise Exception('encrypt input plain is None.')
        elif not gInitFlag:
            raise Exception('encrypt:initialize SCC first')
        else:
            plainAsc = self.encode(plain)
            plainLen = len(plainAsc)
            cipher = ctypes.c_char_p()
            cipherLen = ctypes.c_int()
            p_cipher = ctypes.pointer(cipher)
            p_cipherLen = ctypes.pointer(cipherLen)
            try:
                result = self.pdll.SCC_EncryptByDomain(domainId, plainAsc, plainLen, p_cipher, p_cipherLen)
            except Exception as e:
                raise Exception('Encrypt Failed. Exception=' + str(e))
            finally:
                if result == SEC_SUCCESS:
                    cipherText = self.decode(cipher.value)
                    self.pdll.SCC_Free(cipher)
                else:
                    raise Exception('Encrypt failed. result=' + str(result))
        return cipherText

    def decrypt(self, cipher, domainId=DEFAULT_DOMAIN_ID):
        result = None
        if cipher is None:
            raise Exception('decrypt input cipher is None.')
        elif not gInitFlag:
            raise Exception('decrypt:initialize SCC first')
        else:
            cipherAsc = self.encode(cipher)
            cipherLen = len(cipherAsc)
            plain = ctypes.c_char_p()
            plainLlen = ctypes.c_int()
            p_plain = ctypes.pointer(plain)
            p_plainLlen = ctypes.pointer(plainLlen)
            try:
                result = self.pdll.SCC_DecryptByDomain(domainId, cipherAsc, cipherLen, p_plain, p_plainLlen)
            except Exception as e:
                raise Exception('Decrypt Failed. Exception=' + str(e))
            finally:
                if result == SEC_SUCCESS:
                    plainText = self.decode(plain.value)
                    self.pdll.SCC_Free(plain)
                else:
                    raise Exception('Decrypt failed. result=' + str(result))
        return plainText

    def setDomain(self, domainId=DEFAULT_DOMAIN_ID):
        try:
            self.pdll.SCC_SetDomain(domainId)
        except Exception as e:
            raise Exception('SetDomain Failed. Exception=' + str(e))

    def resetDomain(self):
        try:
            self.pdll.SCC_ResetDomain()
        except Exception as e:
            raise Exception('ResetDomain Failed. Exception=' + str(e))

    def reEncrypt(self, cipher, domainId=DEFAULT_DOMAIN_ID):
        result = None
        if cipher is None:
            raise Exception('reEncrypt input cipher is None.')
        elif not gInitFlag:
            raise Exception('reEncrypt:initialize SCC first')
        else:
            cipherAsc = self.encode(cipher)
            cipherLen = len(cipherAsc)
            newCipher = ctypes.c_char_p()
            newCipherLen = ctypes.c_int()
            p_newCipher = ctypes.pointer(newCipher)
            p_newCipherLen = ctypes.pointer(newCipherLen)
            try:
                result = self.pdll.SCC_ReEncryptByDomain(domainId, cipherAsc, cipherLen, p_newCipher, p_newCipherLen)
            except Exception as e:
                raise Exception('ReEncryptByDomain Failed. Exception=' + str(e))
            finally:
                if result == SEC_SUCCESS:
                    newCipherText = self.decode(newCipher.value)
                    self.pdll.SCC_Free(newCipher)
                else:
                    raise Exception('ReEncryptByDomain failed. result=' + str(result))
        return newCipherText

    def activeNewKey(self, domainId=DEFAULT_DOMAIN_ID):
        result = None
        if not gInitFlag:
            raise Exception('activeNewKey:initialize SCC first')
        else:
            try:
                result = self.pdll.SCC_ActiveNewKey(domainId)
            except Exception as e:
                raise Exception('ActiveNewKey Failed. Exception=' + str(e))
            finally:
                if result == SEC_SUCCESS:
                    activeResult = True
                else:
                    raise Exception('ActiveNewKey failed. result=' + str(result))
        return activeResult

    def genSecKey(self, keyLen=DEFAULT_KEY_LEN):
        result = None

        key = ctypes.c_char_p()
        newkeyLen = ctypes.c_int()
        p_key = ctypes.pointer(key)
        p_keyLen = ctypes.pointer(newkeyLen)
        try:
            result = self.pdll.SCC_GenSecKey(keyLen, p_key, p_keyLen)
        except Exception as e:
            raise Exception('GenSecKey Failed. Exception=' + str(e))
        finally:
            if result == SEC_SUCCESS:
                keyText = self.decode(key.value)
                self.pdll.SCC_Free(key)
            else:
                raise Exception('GenSecKey failed. result=' + str(result))
        return keyText

    def registerKey(self, key, domainId, keyID=KEYID_AUTO_GET):
        result = None
        if not gInitFlag:
            raise Exception('registerKey:initialize SCC first')
        else:
            try:
                keyAsc = self.encode(key)
                keyLen = len(keyAsc)
                result = self.pdll.SCC_RegisterKey(domainId, keyID, keyAsc, keyLen)
            except Exception as e:
                raise Exception('RegisterKey Failed. Exception=' + str(e))
            finally:
                if result == SEC_SUCCESS:
                    regResult = True
                else:
                    raise Exception('RegisterKey failed. result=' + str(result))
        return regResult

    def getKey(self, domainId=DEFAULT_DOMAIN_ID, keyID=KEYID_AUTO_GET, encodeInd=True):
        result = None
        if not gInitFlag:
            raise Exception('getKey:initialize SCC first')
        else:
            key = ctypes.c_char_p()
            keyLen = ctypes.c_int()
            p_key = ctypes.pointer(key)
            p_keyLen = ctypes.pointer(keyLen)
            encodeIndParm = SEC_TRUE
            if encodeInd == False:
                encodeIndParm = SEC_FALSE
            try:
                result = self.pdll.SCC_GetKeyByID(domainId, keyID, p_key, p_keyLen, encodeIndParm)
            except Exception as e:
                raise Exception('GetKeyByID Failed. Exception=' + str(e))
            finally:
                if result == SEC_SUCCESS:
                    keyText = self.decode(key.value)
                    self.pdll.SCC_Free(key)
                else:
                    raise Exception('GetKeyByID failed. result=' + str(result))
        return keyText

    def getMaxMkID(self, domainId):
        result = None
        keyID = ctypes.c_int()
        p_KeyID = ctypes.pointer(keyID)
        if not gInitFlag:
            raise Exception('getMaxMkID:initialize SCC first')
        else:
            try:
                result = self.pdll.SCC_GetMaxMkID(domainId, p_KeyID)
            except Exception as e:
                raise Exception('GetMaxMkID Failed. Exception=' + str(e))
            finally:
                if result == SEC_SUCCESS:
                    maxKeyID = keyID.value
                else:
                    raise Exception('GetMaxMkID failed. result=' + str(result))
        return maxKeyID

    def isExistMkID(self, domainId, keyID):
        result = None
        existResult = False
        if not gInitFlag:
            raise Exception('isExistMkID:initialize SCC first')
        else:
            existInd = ctypes.c_int()
            p_ExistInd = ctypes.pointer(existInd)

            try:
                result = self.pdll.SCC_IsExistMkID(domainId, keyID, p_ExistInd)
            except Exception as e:
                raise Exception('IsExistMkID Failed. Exception=' + str(e))
            finally:
                if result == SEC_SUCCESS:
                    if existInd.value == SEC_TRUE:
                        existResult = True
                else:
                    raise Exception('IsExistMkID failed. result=' + str(result))
        return existResult

    def getPublicKey(self):
        result = None
        if not gInitFlag:
            raise Exception('getPublicKey:initialize SCC first')
        else:
            key = ctypes.c_char_p()
            p_key = ctypes.pointer(key)
            try:
                result = self.pdll.SCC_GetPublicKey(p_key)
            except Exception as e:
                raise Exception('GetPublicKey Failed. Exception=' + str(e))
            finally:
                if result == SEC_SUCCESS:
                    keyText = self.decode(key.value)
                    self.pdll.SCC_Free(key)
                else:
                    raise Exception('GetPublicKey failed. result=' + str(result))
        return keyText

    def publicKeyEncrypt(self, plain, publicKey):
        result = None
        if plain is None:
            raise Exception('encrypt input plain is None.')
        elif not gInitFlag:
            raise Exception('encrypt:initialize SCC first')
        else:
            publicKeyAsc = self.encode(publicKey)
            plainAsc = self.encode(plain)
            plainLen = len(plainAsc)
            cipher = ctypes.c_char_p()
            cipherLen = ctypes.c_int()
            p_cipher = ctypes.pointer(cipher)
            p_cipherLen = ctypes.pointer(cipherLen)
            try:
                result = self.pdll.SCC_PublicKeyEncrypt(plainAsc, plainLen, publicKeyAsc, p_cipher, p_cipherLen)
            except Exception as e:
                raise Exception('PublicKeyEncrypt Failed. Exception=' + str(e))
            finally:
                if result == SEC_SUCCESS:
                    cipherText = self.decode(cipher.value)
                    self.pdll.SCC_Free(cipher)
                else:
                    raise Exception('PublicKeyEncrypt failed. result=' + str(result))
        return cipherText

    def privateKeyDecrypt(self, cipher):
        result = None
        if cipher is None:
            raise Exception('decrypt input cipher is None.')
        elif not gInitFlag:
            raise Exception('decrypt:initialize SCC first')
        else:
            cipherAsc = self.encode(cipher)
            cipherLen = len(cipherAsc)
            plain = ctypes.c_char_p()
            plainLlen = ctypes.c_int()
            p_plain = ctypes.pointer(plain)
            p_plainLlen = ctypes.pointer(plainLlen)
            try:
                result = self.pdll.SCC_PrivateKeyDecrypt(cipherAsc, cipherLen, p_plain, p_plainLlen)
            except Exception as e:
                raise Exception('PrivateKeyDecrypt Failed. Exception=' + str(e))
            finally:
                if result == SEC_SUCCESS:
                    plainText = self.decode(plain.value)
                    self.pdll.SCC_Free(plain)
                else:
                    raise Exception('PrivateKeyDecrypt failed. result=' + str(result))
        return plainText

    def resetRSAKey(self):
        result = None
        try:
            result = self.pdll.SCC_ResetRSAKey()
        except Exception as e:
            raise Exception('ResetRSAKey Failed. Exception=' + str(e))
        finally:
            if result != SEC_SUCCESS:
                raise Exception('ResetRSAKey failed. result=' + str(result))


@fitable("com.huawei.fit.security.encrypt", "com.huawei.fit.security.scc.encrypt")
def encrypt(plain: str) -> str:
    """
    对于未加密的内容通过 scc 方式进行加密。

    :param plain: 未加密内容。
    :return: 加密后内容。
    """
    try:
        CryptoAPI().initialize(get_conf_file())
        cipher = CryptoAPI().encrypt(plain)
        CryptoAPI().finalize()
    except Exception as e:
        raise Exception("Failed to encrypt, please check conf first and then contact KMC", e)
    return cipher


@fitable("com.huawei.fit.security.decrypt", "com.huawei.fit.security.scc.decrypt")
def decrypt(cipher: str) -> str:
    """
    对于加密后的内容通过 scc 方式进行解密。

    :param cipher: 加密后内容。
    :return: 解密后内容。
    """
    try:
        CryptoAPI().initialize(get_conf_file())
        plain = CryptoAPI().decrypt(cipher)
        CryptoAPI().finalize()
    except Exception as e:
        raise Exception("Failed to decrypt, please check conf first and then contact KMC", e)
    return plain
