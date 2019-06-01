package com.cloudin.commons.langs.support.poi;

import com.cloudin.commons.langs.io.TempFileUtil;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STLineSpacingRule;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author 小天
 * @date 2019/5/31 21:29
 */
public class PoiWordToolTest {

    private String defaultFontFamily = "宋体";
    private int    defaultFontSize   = 14;
    private String defaultColor      = "000000";
    private File   tempFileDir;

    @Before
    public void setUp() throws Exception {
        tempFileDir = TempFileUtil.createTempFileDir();
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void initDocForA4() {
    }

    @Test
    public void addBlankLine() {
    }

    @Test
    public void createParagraph() {
    }

    @Test
    public void addParagraph() {
    }

    @Test
    public void setLineHeight() throws IOException {
        XWPFDocument doc = new XWPFDocument();
        PoiWordTool.initDocForA4(doc);
        XWPFParagraph paragraph;

        paragraph = doc.createParagraph();
        PoiWordTool.setLineHeightMultiple(paragraph, 1.5f);
        PoiWordTool.addParagraph(paragraph, "1.5 倍 行间距测试", defaultFontFamily, defaultFontSize, defaultColor);

        paragraph = doc.createParagraph();
        PoiWordTool.setLineHeightMultiple(paragraph, 2.0f);
        PoiWordTool.addParagraph(paragraph, "2.0 倍 行间距测试", defaultFontFamily, defaultFontSize, defaultColor);

        paragraph = doc.createParagraph();
        PoiWordTool.setLineHeightMultiple(paragraph, 3.0f);
        PoiWordTool.addParagraph(paragraph, "3.0 倍 行间距测试", defaultFontFamily, defaultFontSize, defaultColor);

        File wordFile = TempFileUtil.createTempFile(tempFileDir, "docx");

        System.out.println(wordFile);

        FileOutputStream out = new FileOutputStream(wordFile);
        doc.write(out);
        out.close();
    }

    @Test
    public void setParagraphSpaceOfPound() {
    }

    @Test
    public void setParagraphSpaceOfLine() {
    }

    @Test
    public void addBreak() {
    }

    @Test
    public void setParagraphStyle() {
    }

    @Test
    public void getXWPFRun() {
    }

    @Test
    public void getRunProperties() {
    }

    @Test
    public void createPicture() {
    }

    @Test
    public void addPicture() {
    }

    @Test
    public void setPicturePosition() {
    }

    @Test
    public void createTableWithoutBorder() {
    }

    @Test
    public void createTable() {
    }

    @Test
    public void setTableRowHeightOfPixel() {
    }

    @Test
    public void setTableWidth() {
    }

    @Test
    public void setTableCellAlign() {
    }

    @Test
    public void setTableCellAlign1() {
    }

    @Test
    public void setTableCellBgColor() {
    }
}