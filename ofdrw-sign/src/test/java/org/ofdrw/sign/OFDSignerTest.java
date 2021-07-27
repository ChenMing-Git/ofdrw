package org.ofdrw.sign;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.ofdrw.core.signatures.Signatures;
import org.ofdrw.gm.cert.PKCS12Tools;
import org.ofdrw.gm.ses.v4.SESeal;
import org.ofdrw.reader.OFDReader;
import org.ofdrw.sign.signContainer.SESV4Container;
import org.ofdrw.sign.stamppos.NormalStampPos;
import org.ofdrw.sign.verify.OFDValidator;
import org.ofdrw.sign.verify.container.SESV4ValidateContainer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.Certificate;

/**
 * OFD签名引擎测试
 *
 * @author 权观宇
 * @since 2020-04-18 11:10:43
 */
class OFDSignerTest {

    /**
     * 测试文档中没有Doc_0 只有Doc_1的情况签章
     *
     * @throws GeneralSecurityException _
     * @throws IOException              _
     */
    @Test
    void testOnlyDoc1Sign() throws GeneralSecurityException, IOException {
        Path userP12Path = Paths.get("src/test/resources", "USER.p12");
        Path sealPath = Paths.get("src/test/resources", "UserV4.esl");

        PrivateKey prvKey = PKCS12Tools.ReadPrvKey(userP12Path, "private", "777777");
        Certificate signCert = PKCS12Tools.ReadUserCert(userP12Path, "private", "777777");
        SESeal seal = SESeal.getInstance(Files.readAllBytes(sealPath));

        Path src = Paths.get("src/test/resources", "doc1File.ofd");
        Path out = Paths.get("target/Doc1FileSigned.ofd");

        // 1. 构造签名引擎
        try (OFDReader reader = new OFDReader(src);
             OFDSigner signer = new OFDSigner(reader, out, new NumberFormatAtomicSignID())
        ) {
            SESV4Container signContainer = new SESV4Container(prvKey, seal, signCert);
            // 2. 设置签名模式
            signer.setSignMode(SignMode.WholeProtected);
            // 3. 设置签名使用的扩展签名容器
            signer.setSignContainer(signContainer);
            // 4. 设置显示位置
            signer.addApPos(new NormalStampPos(1, 50, 50, 40, 40));
            // 5. 执行签名
            signer.exeSign();
            // 6. 关闭签名引擎，生成文档。
        }
        // 验证
        try (OFDReader reader = new OFDReader(out);
             OFDValidator validator = new OFDValidator(reader)) {
            validator.setValidator(new SESV4ValidateContainer());
            validator.exeValidate();
            System.out.println(">> 验证通过");
        }

    }

    /**
     * 已经有印章的情况下再追加签章
     *
     * @throws GeneralSecurityException _
     * @throws IOException              _
     */
    @Test
    void reSignTest() throws GeneralSecurityException, IOException {
        Path userP12Path = Paths.get("src/test/resources", "USER.p12");
        Path sealPath = Paths.get("src/test/resources", "UserV4.esl");

        PrivateKey prvKey = PKCS12Tools.ReadPrvKey(userP12Path, "private", "777777");
        Certificate signCert = PKCS12Tools.ReadUserCert(userP12Path, "private", "777777");
        SESeal seal = SESeal.getInstance(Files.readAllBytes(sealPath));

        Path src = Paths.get("src/test/resources", "signedFile.ofd");
        Path out = Paths.get("target/ReSign.ofd");

        // 1. 构造签名引擎
        try (OFDReader reader = new OFDReader(src);
             OFDSigner signer = new OFDSigner(reader, out, new NumberFormatAtomicSignID())
        ) {
            SESV4Container signContainer = new SESV4Container(prvKey, seal, signCert);
            // 2. 设置签名模式
            signer.setSignMode(SignMode.WholeProtected);
            // 3. 设置签名使用的扩展签名容器
            signer.setSignContainer(signContainer);
            // 4. 设置显示位置
            signer.addApPos(new NormalStampPos(1, 50, 50, 40, 40));
            // 5. 执行签名
            signer.exeSign();
            // 6. 关闭签名引擎，生成文档。
        }

        // 验证
        try (OFDReader reader = new OFDReader(out);
             OFDValidator validator = new OFDValidator(reader)) {
            validator.setValidator(new SESV4ValidateContainer());
            validator.exeValidate();
            System.out.println(">> 验证通过");
        }
    }

    /**
     * 文档全保护状态下签章测试，应该抛出异常
     *
     * @throws GeneralSecurityException _
     * @throws IOException              _
     */
    @Test
    void reSignExceptionTest() throws GeneralSecurityException, IOException {
        Path userP12Path = Paths.get("src/test/resources", "USER.p12");
        Path sealPath = Paths.get("src/test/resources", "UserV4.esl");

        PrivateKey prvKey = PKCS12Tools.ReadPrvKey(userP12Path, "private", "777777");
        Certificate signCert = PKCS12Tools.ReadUserCert(userP12Path, "private", "777777");
        SESeal seal = SESeal.getInstance(Files.readAllBytes(sealPath));

        Path src = Paths.get("src/test/resources", "allprotected.ofd");
        Path out = Paths.get("target/ReSignException.ofd");
        Assertions.assertThrows(SignatureTerminateException.class, () -> {
            // 1. 构造签名引擎
            try (OFDReader reader = new OFDReader(src);
                 OFDSigner signer = new OFDSigner(reader, out, new NumberFormatAtomicSignID())
            ) {
                SESV4Container signContainer = new SESV4Container(prvKey, seal, signCert);
                // 2. 设置签名模式
                signer.setSignMode(SignMode.WholeProtected);
                // 3. 设置签名使用的扩展签名容器
                signer.setSignContainer(signContainer);
                // 4. 设置显示位置
                signer.addApPos(new NormalStampPos(1, 50, 50, 40, 40));
                // 5. 执行签名
                signer.exeSign();
                // 6. 关闭签名引擎，生成文档。
            }
        });
    }

    /**
     * 过滤需要被保护的文件
     */
    @Test
    void setProtectFileFilter() throws IOException, GeneralSecurityException {
        Path userP12Path = Paths.get("src/test/resources", "USER.p12");
        Path sealPath = Paths.get("src/test/resources", "UserV4.esl");

        PrivateKey prvKey = PKCS12Tools.ReadPrvKey(userP12Path, "private", "777777");
        Certificate signCert = PKCS12Tools.ReadUserCert(userP12Path, "private", "777777");
        SESeal seal = SESeal.getInstance(Files.readAllBytes(sealPath));

        Path src = Paths.get("src/test/resources", "helloworld.ofd");
        Path out = Paths.get("target/filter_ed.ofd");
        // 1. 构造签名引擎
        try (OFDReader reader = new OFDReader(src);
//             OFDSigner signer = new OFDSigner(reader, out)
             OFDSigner signer = new OFDSigner(reader, out, new NumberFormatAtomicSignID())
        ) {
            SESV4Container signContainer = new SESV4Container(prvKey, seal, signCert);
            // 2. 设置签名模式
//            signer.setSignMode(SignMode.WholeProtected);
            signer.setSignMode(SignMode.ContinueSign);
            // 3. 设置签名使用的扩展签名容器
            signer.setSignContainer(signContainer);
            // 4. 设置显示位置
            signer.addApPos(new NormalStampPos(1, 50, 50, 40, 40));
            signer.setProtectFileFilter(absPath -> {
                System.out.println(">> Filter protect file Path: " + absPath);
                if ("/OFD.xml".equals(absPath.toString())) {
                    return false;
                }
                return true;
            });
            // 5. 执行签名
            signer.exeSign();
            // 6. 关闭签名引擎，生成文档。
        }
        System.out.println(">> 生成文件位置: " + out.toAbsolutePath().toAbsolutePath());

    }

    /**
     * 设置本次签名的“基”签名，形成链式验证
     */
    @Test
    void setRelative() throws GeneralSecurityException, IOException {
        Path userP12Path = Paths.get("src/test/resources", "USER.p12");
        Path sealPath = Paths.get("src/test/resources", "UserV4.esl");

        PrivateKey prvKey = PKCS12Tools.ReadPrvKey(userP12Path, "private", "777777");
        Certificate signCert = PKCS12Tools.ReadUserCert(userP12Path, "private", "777777");
        SESeal seal = SESeal.getInstance(Files.readAllBytes(sealPath));

        Path src = Paths.get("src/test/resources", "signedFile.ofd");
        Path out = Paths.get("target/linked_verify.ofd");

        // 1. 构造签名引擎
        try (OFDReader reader = new OFDReader(src);
             OFDSigner signer = new OFDSigner(reader, out, new NumberFormatAtomicSignID())
        ) {
            SESV4Container signContainer = new SESV4Container(prvKey, seal, signCert);
            // 2. 设置签名模式
            signer.setSignMode(SignMode.WholeProtected);
            // 3. 设置签名使用的扩展签名容器
            signer.setSignContainer(signContainer);
            // 4. 设置显示位置
            signer.addApPos(new NormalStampPos(1, 50, 50, 40, 40));

            // 借助Reader获取已经存在签名的信息
            final Signatures signatures = reader.getDefaultSignatures();
            // 获取第一个签名的ID
            final String relativeId = signatures.getSignatures().get(0).getID();
            // 设置 关联的ID，形成链式验证
            signer.setRelative(relativeId);
            // 5. 执行签名
            signer.exeSign();
            // 6. 关闭签名引擎，生成文档。
        }
        System.out.println(">> 生成文件位置: " + out.toAbsolutePath().toAbsolutePath());
    }

    /**
     * 测试不标准的命名空间签名
     */
    @Test
    public void testNoStdNs() throws GeneralSecurityException, IOException {
        Path userP12Path = Paths.get("src/test/resources", "USER.p12");
        Path sealPath = Paths.get("src/test/resources", "UserV4.esl");

        PrivateKey prvKey = PKCS12Tools.ReadPrvKey(userP12Path, "private", "777777");
        Certificate signCert = PKCS12Tools.ReadUserCert(userP12Path, "private", "777777");
        SESeal seal = SESeal.getInstance(Files.readAllBytes(sealPath));

        Path src = Paths.get("src/test/resources", "namespace_no_std.ofd");
        Path out = Paths.get("target/namespace_no_std_signed.ofd");
        // 1. 构造签名引擎
        try (OFDReader reader = new OFDReader(src);
             OFDSigner signer = new OFDSigner(reader, out, new NumberFormatAtomicSignID())
        ) {
            SESV4Container signContainer = new SESV4Container(prvKey, seal, signCert);
            // 2. 设置签名模式
//            signer.setSignMode(SignMode.WholeProtected);
            signer.setSignMode(SignMode.ContinueSign);
            // 3. 设置签名使用的扩展签名容器
            signer.setSignContainer(signContainer);
            // 4. 设置显示位置
            signer.addApPos(new NormalStampPos(1, 50, 50, 40, 40));
            // 5. 执行签名
            signer.exeSign();
            // 6. 关闭签名引擎，生成文档。
        }
        System.out.println(">> 生成文件位置: " + out.toAbsolutePath().toAbsolutePath());
    }

}