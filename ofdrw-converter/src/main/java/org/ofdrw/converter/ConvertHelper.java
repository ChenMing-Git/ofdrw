package org.ofdrw.converter;

import org.apache.pdfbox.jbig2.util.log.Logger;
import org.apache.pdfbox.jbig2.util.log.LoggerFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.ofdrw.reader.DLOFDReader;
import org.ofdrw.reader.model.OfdPageVo;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * PDF 转换 工具
 *
 * @author minghu.zhang
 * @since 12:53 2020/11/14
 **/
public class ConvertHelper {

    private final static Logger logger = LoggerFactory.getLogger(ConvertHelper.class);

    /**
     * OFD转换PDF
     *
     * @param input  OFD文件路径，支持OutputStream、Path
     * @param output PDF输出流，支持OutputStream、Path、File、String（文件路径）
     * @throws IllegalArgumentException 参数错误
     * @throws GeneralConvertException  文档转换过程中异常
     */
    private static void ofd2pdf(Object input, Object output) {
        DLOFDReader reader = null;
        PDDocument pdfDocument = null;
        try {
            if (input instanceof InputStream) {
                reader = new DLOFDReader((InputStream) input);
            } else if (input instanceof Path) {
                reader = new DLOFDReader((Path) input);
            } else if (input instanceof File){
                reader = new DLOFDReader(new FileInputStream((File) input));
            }else {
                throw new IllegalArgumentException("不支持的输入格式(input)，仅支持InputStream、Path");
            }
            pdfDocument = new PDDocument();

            List<OfdPageVo> ofdPageVoList = reader.getOFDDocumentVo().getOfdPageVoList();

            long start;
            long end;
            int pageNum = 1;

            PdfboxMaker pdfMaker = new PdfboxMaker(reader, pdfDocument);
            for (OfdPageVo ofdPageVo : ofdPageVoList) {
                start = System.currentTimeMillis();
                pdfMaker.makePage(ofdPageVo);
                end = System.currentTimeMillis();
                logger.info(String.format("page %d speed time %d", pageNum++, end - start));
            }

            if (output instanceof OutputStream) {
                pdfDocument.save((OutputStream) output);
            } else if (output instanceof File) {
                pdfDocument.save((File) output);
            } else if (output instanceof String) {
                pdfDocument.save((String) output);
            } else if (output instanceof Path) {
                pdfDocument.save(Files.newOutputStream((Path) output));
            } else {
                throw new IllegalArgumentException("不支持的输出格式(output)，仅支持OutputStream、Path、File、String");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            logger.error("convert to pdf failed", e);
            throw new GeneralConvertException(e);
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
                if (pdfDocument != null) {
                    pdfDocument.close();
                }
            } catch (IOException e) {
                logger.error("close OFDReader failed", e);
            }

        }
    }

    /**
     * 转PDF
     *
     * @param input  OFD输入流
     * @param output PDF输出流
     * @throws IllegalArgumentException 参数错误
     * @throws GeneralConvertException  文档转换过程中异常
     */
    public static void toPdf(InputStream input, OutputStream output) {
        ofd2pdf(input, output);
    }

    /**
     * 转PDF
     *
     * @param input  OFD输入流
     * @param output PDF输出文件
     * @throws IllegalArgumentException 参数错误
     * @throws GeneralConvertException  文档转换过程中异常
     */
    public static void toPdf(InputStream input, File output) {
        ofd2pdf(input, output);
    }

    /**
     * 转PDF
     *
     * @param input  OFD输入流
     * @param output PDF输出文件路径
     * @throws IllegalArgumentException 参数错误
     * @throws GeneralConvertException  文档转换过程中异常
     */
    public static void toPdf(InputStream input, String output) {
        ofd2pdf(input, output);
    }

    /**
     * 转PDF
     *
     * @param input  OFD输入文件
     * @param output PDF输出流
     * @throws IllegalArgumentException 参数错误
     * @throws GeneralConvertException  文档转换过程中异常
     */
    public static void toPdf(Path input, OutputStream output) {
        ofd2pdf(input, output);
    }

    /**
     * 转PDF
     *
     * @param input  OFD输入文件
     * @param output PDF输出文件
     * @throws IllegalArgumentException 参数错误
     * @throws GeneralConvertException  文档转换过程中异常
     */
    public static void toPdf(Path input, File output) {
        ofd2pdf(input, output);
    }

    /**
     * 转PDF
     *
     * @param input  OFD输入文件
     * @param output PDF输出文件路径
     * @throws IllegalArgumentException 参数错误
     * @throws GeneralConvertException  文档转换过程中异常
     */
    public static void toPdf(Path input, Path output) {
        ofd2pdf(input, output);
    }

}
