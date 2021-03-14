import org.junit.jupiter.api.Test;
import org.ofdrw.converter.ImageMaker;
import org.ofdrw.converter.utils.CommonUtil;
import org.ofdrw.converter.utils.FontUtils;
import org.ofdrw.reader.DLOFDReader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;


public class OFD2IMGTest {

    @Test
    public void test() throws IOException {

        //加载系统字体和默认字体
        FontUtils.init();

        //加载指定目录字体
        FontUtils.scanFontDir(new File("src/test/resources/fonts"));

        //为不规范的字体名创建映射
        FontUtils.addAliasMapping("null", "仿宋简体", "null", "方正仿宋简体");
        FontUtils.addAliasMapping("null", "仿宋", "null", "方正仿宋简体");
        FontUtils.addAliasMapping("null", "小标宋体", "方正小标宋简体", "方正小标宋简体");
        FontUtils.addAliasMapping("null", "KaiTi_GB2312", "楷体", "楷体");


        toPng("src/test/resources/zsbk.ofd", "target/zsbk.ofd");
        toPng("src/test/resources/发票示例.ofd", "target/发票示例.ofd");
        toPng("src/test/resources/文字横向-数科.ofd", "target/文字横向-数科.ofd");
    }

    private static void toPng(String filename, String dirPath) throws IOException {
        File dir = new File(dirPath);
        dir.mkdirs();
        
        Path src = Paths.get(filename);
        ImageMaker imageMaker = new ImageMaker(new DLOFDReader(src), 15);
        imageMaker.config.setDrawBoundary(false);
        for (int i = 0; i < imageMaker.pageSize(); i++) {
            BufferedImage image = imageMaker.makePage(i);
            ImageIO.write(image, "PNG", new File(dir, +i + ".png"));
        }
    }

}
