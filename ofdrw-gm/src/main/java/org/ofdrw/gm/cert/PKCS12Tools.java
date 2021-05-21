package org.ofdrw.gm.cert;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.cert.Certificate;

/**
 * PKCS12 解析工具
 *
 * @author 权观宇
 * @since 2020-04-21 02:04:24
 */
public class PKCS12Tools {

    /**
     * 从P12中获取私钥
     *
     * @param userP12 PKCS12文件路径
     * @param pwd     解密密钥
     * @return 私钥
     * @throws GeneralSecurityException 加解密异常
     * @throws IOException              文件读取异常
     */
    public static PrivateKey ReadPrvKey(Path userP12, String pwd)
            throws GeneralSecurityException, IOException {
        try (InputStream rootKsIn = Files.newInputStream(userP12)) {
            return ReadPrvKey(rootKsIn, pwd);
        }
    }

    /**
     * 从P12中获取私钥
     *
     * @param rootKsIn PKCS12 is
     * @param pwd     解密密钥
     * @return 私钥
     * @throws GeneralSecurityException 加解密异常
     * @throws IOException              文件读取异常
     */
    public static PrivateKey ReadPrvKey(InputStream rootKsIn, String pwd)
            throws GeneralSecurityException, IOException {
        KeyStore ks = KeyStore.getInstance("PKCS12", new BouncyCastleProvider());
        ks.load(rootKsIn, pwd.toCharArray());
        String alias = ks.aliases().nextElement();
        return (PrivateKey) ks.getKey(alias, pwd.toCharArray());

    }

    /**
     * 从P12中获取私钥
     *
     * @param userP12 PKCS12文件路径
     * @param alias   密钥存储别名
     * @param pwd     解密密钥
     * @return 私钥
     * @throws GeneralSecurityException 加解密异常
     * @throws IOException              文件读取异常
     */
    public static PrivateKey ReadPrvKey(Path userP12, String alias, String pwd)
            throws GeneralSecurityException, IOException {
        try (InputStream rootKsIn = Files.newInputStream(userP12)) {
            return ReadPrvKey(rootKsIn, alias, pwd);
        }
    }

    /**
     * 从P12中获取私钥
     *
     * @param rootKsIn PKCS12文件is
     * @param alias   密钥存储别名
     * @param pwd     解密密钥
     * @return 私钥
     * @throws GeneralSecurityException 加解密异常
     * @throws IOException              文件读取异常
     */
    public static PrivateKey ReadPrvKey(InputStream rootKsIn, String alias, String pwd)
            throws GeneralSecurityException, IOException {
        KeyStore ks = KeyStore.getInstance("PKCS12", new BouncyCastleProvider());
        ks.load(rootKsIn, pwd.toCharArray());
        return (PrivateKey) ks.getKey(alias, pwd.toCharArray());
    }

    /**
     * 从P12中获取证书链
     *
     * @param rootKsIn PKCS12文件is
     * @param pwd     解密密钥
     * @return 证书链，第一张证书为用户证书
     * @throws GeneralSecurityException 加解密异常
     * @throws IOException              文件读取异常
     */
    public static Certificate[] ReadCertChain(InputStream rootKsIn, String pwd)
            throws GeneralSecurityException, IOException {
        KeyStore ks = KeyStore.getInstance("PKCS12", new BouncyCastleProvider());
        ks.load(rootKsIn, pwd.toCharArray());
        String alias = ks.aliases().nextElement();
        return ks.getCertificateChain(alias);

    }

    /**
     * 从P12中获取用户证书
     *
     * @param userP12 PKCS12文件路径
     * @param pwd     解密密钥
     * @return 用户证书
     * @throws GeneralSecurityException 加解密异常
     * @throws IOException              文件读取异常
     */
    public static Certificate ReadUserCert(Path userP12, String pwd)
            throws GeneralSecurityException, IOException {
        try (InputStream rootKsIn = Files.newInputStream(userP12)) {
            return ReadUserCert(rootKsIn, pwd);
        }

    }

    /**
     * 从P12中获取用户证书
     *
     * @param rootKsIn PKCS12文件is
     * @param pwd     解密密钥
     * @return 用户证书
     * @throws GeneralSecurityException 加解密异常
     * @throws IOException              文件读取异常
     */
    public static Certificate ReadUserCert(InputStream rootKsIn, String pwd)
            throws GeneralSecurityException, IOException {
        return ReadCertChain(rootKsIn, pwd)[0];
    }


    /**
     * 从P12中获取证书链
     *
     * @param rootKsIn PKCS12的is
     * @param alias    密钥存储别名
     * @param pwd      解密密钥
     * @return 证书链，第一张证书为用户证书
     * @throws GeneralSecurityException 加解密异常
     * @throws IOException              文件读取异常
     */
    public static Certificate[] ReadCertChain(InputStream rootKsIn, String alias, String pwd)
            throws GeneralSecurityException, IOException {
        KeyStore ks = KeyStore.getInstance("PKCS12", new BouncyCastleProvider());
        ks.load(rootKsIn, pwd.toCharArray());
        return ks.getCertificateChain(alias);
    }

    /**
     * 从P12中获取用户证书
     *
     * @param userP12 PKCS12文件路径
     * @param alias   密钥存储别名
     * @param pwd     解密密钥
     * @return 用户证书
     * @throws GeneralSecurityException 加解密异常
     * @throws IOException              文件读取异常
     */
    public static Certificate ReadUserCert(Path userP12, String alias, String pwd)
            throws GeneralSecurityException, IOException {
        try (InputStream rootKsIn = Files.newInputStream(userP12)) {
            return ReadUserCert(rootKsIn, alias, pwd);
        }

    }

    /**
     * 从P12中获取用户证书
     *
     * @param rootKsIn PKCS12文件is
     * @param alias   密钥存储别名
     * @param pwd     解密密钥
     * @return 用户证书
     * @throws GeneralSecurityException 加解密异常
     * @throws IOException              文件读取异常
     */
    public static Certificate ReadUserCert(InputStream rootKsIn, String alias, String pwd)
            throws GeneralSecurityException, IOException {
        return ReadCertChain(rootKsIn, alias, pwd)[0];
    }

}
