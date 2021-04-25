package org.ofdrw.converter;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import org.apache.pdfbox.jbig2.util.log.Logger;
import org.apache.pdfbox.jbig2.util.log.LoggerFactory;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.ofdrw.reader.OFDReader;
import org.ofdrw.reader.PageInfo;

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
     * @param input  OFD文件路径，支持OutputStream、Path、String（文件路径）
     * @param output PDF输出流，支持OutputStream、Path、File、String（文件路径）
     * @throws IllegalArgumentException 参数错误
     * @throws GeneralConvertException  文档转换过程中异常
     */
    public static void ofd2pdf(Object input, Object output) {
        OFDReader reader = null;
        PdfDocument pdfDocument = null;
        try {
            if (input instanceof InputStream) {
                reader = new OFDReader((InputStream) input);
            } else if (input instanceof Path) {
                reader = new OFDReader((Path) input);
            } else if (input instanceof File) {
                reader = new OFDReader(new FileInputStream((File) input));
            } else if (input instanceof String) {
                reader = new OFDReader((String) input);
            } else {
                throw new IllegalArgumentException("不支持的输入格式(input)，仅支持InputStream、Path、File、String");
            }

            List<PageInfo> ofdPageVoList = reader.getPageList();
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            PdfWriter pdfWriter = new PdfWriter(bos);
            pdfDocument = new PdfDocument(pdfWriter);
            long start;
            long end;
            int pageNum = 1;

            ItextMaker pdfMaker = new ItextMaker(reader);
            for (PageInfo pageInfo : ofdPageVoList) {
                start = System.currentTimeMillis();
                pdfMaker.makePage(pdfDocument, pageInfo);
                end = System.currentTimeMillis();
                logger.debug(String.format("page %d speed time %d", pageNum++, end - start));
            }

            pdfDocument.close();
            if (output instanceof OutputStream) {
                bos.writeTo((OutputStream) output);
            } else if (output instanceof File) {
                FileOutputStream fileOutputStream = new FileOutputStream((File) output);
                bos.writeTo(fileOutputStream);
            } else if (output instanceof String) {
                FileOutputStream fileOutputStream = new FileOutputStream(new File((String) output));
                bos.writeTo(fileOutputStream);
            } else if (output instanceof Path) {
                bos.writeTo(Files.newOutputStream((Path) output));
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
                if (pdfDocument != null) {
                    pdfDocument.close();
                }
                if (reader != null) {
                    reader.close();
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


    /**
     * 转PDF，源文件目录为已经解压的OFD根目录
     *
     * @param unzippedPathRoot 已经解压的OFD根目录位置，因此通过参数控制是否删除目录。
     * @param output           PDF输出文件路径
     * @param deleteOnClose    退出时是否删除 unzippedPathRoot 文件，true - 退出时删除；false - 不删除
     */
    public static void toPdf(String unzippedPathRoot, String output, boolean deleteOnClose) {
        OFDReader reader = null;
        PDDocument pdfDocument = null;
        try {
            reader = new OFDReader(unzippedPathRoot, deleteOnClose);
            pdfDocument = new PDDocument();

            PdfboxMaker pdfMaker = new PdfboxMaker(reader, pdfDocument);
            List<PageInfo> ofdPageVoList = reader.getPageList();
            long start = 0, end = 0, pageNum = 1;
            for (PageInfo pageInfo : ofdPageVoList) {
                start = System.currentTimeMillis();
                pdfMaker.makePage(pageInfo);
                end = System.currentTimeMillis();
                logger.debug(String.format("page %d speed time %d", pageNum++, end - start));
            }
            pdfDocument.save(output);

        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            logger.error("convert to pdf failed", e);
            throw new GeneralConvertException(e);
        } finally {
            try {
                if (pdfDocument != null) {
                    pdfDocument.close();
                }
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException e) {
                logger.error("close OFDReader failed", e);
            }
        }
    }


}
