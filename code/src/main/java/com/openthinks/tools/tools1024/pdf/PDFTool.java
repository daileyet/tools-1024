package com.openthinks.tools.tools1024.pdf;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.pdfbox.io.MemoryUsageSetting;
import org.apache.pdfbox.multipdf.PDFMergerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.state.PDExtendedGraphicsState;
import org.apache.pdfbox.util.Matrix;
import com.openthinks.libs.utilities.DateFormatUtil;
import com.openthinks.tools.tools1024.AbstractApp;

public class PDFTool extends AbstractApp {

  public static final String MERGE_PARAM_TOKEN = "-MERGE";
  public static final String MARK_PARAM_TOKEN = "-MARK";
  public static final String SORT_DESC = "DESC";
  public static final String DATE_FORMAT_PATTERN = "yyMMddHHmmss";
  public static final String PDF_SUFIXX = ".pdf";
  public static final String PARAM_PREFIX = "--PDF";
  public static final String DEFAULT_SORT = "ASC";
  private static final String DEFAULT_OUTPUT_MERGED_PDF = "merged_%s.pdf";
  public static final float DEFAULT_TEXT_MARK_SIZE = 50.0f;
  public static final String DEFAULT_TEXT_COLOR = "#FF0000";
  public static final PDFont DEFAULT_TEXT_FONT = PDType1Font.TIMES_BOLD;

  static class TextMarkInfo {
    double theta = 45.0;
    float tx = 150.0f;
    float ty = 150.0f;

    public static TextMarkInfo valueOf(List<String> info) {
      TextMarkInfo instance = new TextMarkInfo();
      instance.theta = Double.valueOf(info.get(0));
      instance.tx = Float.valueOf(info.get(1));
      instance.ty = Float.valueOf(info.get(2));
      return instance;
    }

    @Override
    public String toString() {
      return "TextMarkInfo [theta=" + theta + ", tx=" + tx + ", ty=" + ty + "]";
    }

  }

  @Override
  protected String keyParam() {
    return PARAM_PREFIX;
  }

  @Override
  public String usage() {
    StringBuilder sb = new StringBuilder();
    // @formatter:off
		sb.append("PDF usage:\n")
		.append("\nHelp: -H\n")
		.append("\nmerge pdf into one:\n")
		.append("1) -MERGE <pdf1 path> [<pdf2 path>...] -O <merged output pdf path>\n")
		.append("2) -MERGE <pdfs flder> -O <merged output pdf path> [-S [ASC|DESC]]\n")
		.append("\nDigitally sign PDF files:\n")
		.append("-MARK <pdf path> -T <mark text> " 
				+ "\n[-F <font name:TIMES_BOLD>] " 
				+ "\n[-S <font size:50>] "
				+ "\n[-R <font color:#FF0000>] " + "\n[-P <text rotate and position:45 150 150>] "
				+ "\n[-O <marked output pdf path>]\n")
		.append("\nSupport font:\n").append("1. TIMES_ROMAN \n").append("2. TIMES_BOLD \n")
		.append("3. TIMES_ITALIC \n").append("4. TIMES_BOLD_ITALIC \n").append("5. HELVETICA \n")
		.append("6. HELVETICA_BOLD \n").append("7. HELVETICA_OBLIQUE \n").append("8. HELVETICA_BOLD_OBLIQUE \n")
		.append("9. COURIER \n").append("10. COURIER_BOLD \n").append("11. COURIER_OBLIQUE \n")
		.append("12. COURIER_BOLD_OBLIQUE \n").append("13. SYMBOL \n").append("14. ZAPF_DINGBATS \n");
		// @formatter:on
    return sb.toString();
  }

  @Override
  protected void run(final List<String> params) throws Exception {
    if (params.contains("-H")) {
      System.out.println(usage());
      return;
    }
    if (params.contains(MERGE_PARAM_TOKEN)) {
      doMergePDF(params);
      return;
    }
    if (params.contains(MARK_PARAM_TOKEN)) {
      doMarkPDF(params);
      return;
    }
    System.out.println(usage());
  }

  // mark pdf
  private void doMarkPDF(List<String> params) throws IOException {
    PartParams sources = getPartParamsBy(params, MARK_PARAM_TOKEN);
    if (!sources.isHasToken() || sources.isEmpty()) {
      System.out.println(usage());
      return;
    }
    PartParams texts = getPartParamsBy(params, "-T");
    if (!texts.isHasToken() || texts.isEmpty()) {
      System.out.println(usage());
      return;
    }
    float fontSize = DEFAULT_TEXT_MARK_SIZE;
    PartParams sizes = getPartParamsBy(params, "-S");
    if (!sizes.isEmpty()) {
      try {
        fontSize = Float.valueOf(sizes.get(0));
      } catch (NumberFormatException e) {
        System.out.println("PDF tool use default text mark size:" + DEFAULT_TEXT_MARK_SIZE);
      }
    }
    Color color = Color.decode(DEFAULT_TEXT_COLOR);
    PartParams colors = getPartParamsBy(params, "-S");
    if (!colors.isEmpty()) {
      try {
        color = Color.decode(colors.get(0));
      } catch (NumberFormatException e) {
        System.out.println("PDF tool use default text mark color:" + DEFAULT_TEXT_COLOR);
      }
    }
    PDFont font = DEFAULT_TEXT_FONT;
    PartParams fonts = getPartParamsBy(params, "-F");
    if (!fonts.isEmpty()) {
      try {
        font = getEmbeddedFont(fonts.get(0));
        if (font == null) {
          font = DEFAULT_TEXT_FONT;
        }
      } catch (Exception e) {
        System.out.println("PDF tool use default text mark font:" + font);
      }
    }
    TextMarkInfo markInfo = new TextMarkInfo();
    PartParams positions = getPartParamsBy(params, "-P");
    if (!positions.isEmpty()) {
      try {
        markInfo = TextMarkInfo.valueOf(positions.getParams());
      } catch (Exception e) {
        System.out.println("PDF tool use default text mark rotation and translation:" + markInfo);
      }
    }
    PDDocument doc = null;
    String targetPath = null;
    PartParams targets = getPartParamsBy(params, "-O");
    if (!targets.isEmpty()) {
      targetPath = targets.get(0);
    }
    File file = new File(sources.get(0));
    doc = PDDocument.load(file);
    doc.setAllSecurityToBeRemoved(true);
    for (PDPage page : doc.getPages()) {
      PDPageContentStream cs =
          new PDPageContentStream(doc, page, PDPageContentStream.AppendMode.APPEND, true, true);
      String ts = texts.getAllJoined();
      PDExtendedGraphicsState r0 = new PDExtendedGraphicsState();
      r0.setNonStrokingAlphaConstant(0.2f);
      r0.setAlphaSourceFlag(true);
      cs.setGraphicsStateParameters(r0);
      cs.setNonStrokingColor(color);
      cs.beginText();
      cs.setFont(font, fontSize);
      cs.setTextMatrix(Matrix.getRotateInstance(markInfo.theta, markInfo.tx, markInfo.ty));
      cs.showText(ts);
      cs.endText();
      cs.close();
    }
    if (targetPath == null) {
      targetPath = file.getAbsolutePath();
    }
    doc.save(targetPath);
    System.out.println("PDF:" + targetPath + " marked successfully.");
  }

  private PDFont getEmbeddedFont(String fontName) throws NoSuchFieldException, SecurityException,
      IllegalArgumentException, IllegalAccessException {
    Field field = PDType1Font.class.getDeclaredField(fontName);
    return (PDFont) field.get(null);
  }

  // merge pdf
  private void doMergePDF(List<String> params) throws IOException {
    PartParams sources = getPartParamsBy(params, MERGE_PARAM_TOKEN);
    if (!sources.isHasToken() || sources.isEmpty()) {
      System.out.println(usage());
      return;
    }
    String targetPath = null;
    PartParams targets = getPartParamsBy(params, "-O");
    if (!targets.isEmpty()) {
      targetPath = targets.get(0);
    }
    PartParams sorts = getPartParamsBy(params, "-S");
    Boolean isASC = null;
    if (sorts.isHasToken()) {
      isASC = true;
      if (!sorts.isEmpty()) {
        isASC = SORT_DESC.equalsIgnoreCase(sorts.get(0).trim()) ? false : true;
      }
    }
    final List<String> sourcePdfFiles = new ArrayList<>();
    sources.getParams().forEach((source) -> {
      File file = Paths.get(source).toFile();
      if (file.isDirectory()) {
        getPDFfiles(file, sourcePdfFiles);
      } else {
        if (file.getName().toLowerCase().endsWith(PDF_SUFIXX)) {
          sourcePdfFiles.add(file.getAbsolutePath());
        }
      }
    });
    if (isASC != null) {
      if (isASC == true)
        Collections.sort(sourcePdfFiles);
      else
        Collections.reverse(sourcePdfFiles);
    }
    PDFMergerUtility mergePdf = new PDFMergerUtility();
    sourcePdfFiles.forEach((file) -> {
      try {
        mergePdf.addSource(file);
      } catch (FileNotFoundException e) {
        e.printStackTrace();
      }
    });
    if (targetPath == null) {
      String dateStr = DateFormatUtil.format(DATE_FORMAT_PATTERN, new Date());
      targetPath = DEFAULT_OUTPUT_MERGED_PDF.replace("%s", dateStr);
    }
    targetPath = new File(targetPath).getAbsolutePath();
    mergePdf.setDestinationFileName(targetPath);
    mergePdf.mergeDocuments(MemoryUsageSetting.setupMixed(-1));
    System.out.println("PDF:" + targetPath + " merged successfully.");
  }

  private List<? extends String> getPDFfiles(File file, List<String> pdfFiles) {
    for (File f : file.listFiles()) {
      if (f.isDirectory()) {
        getPDFfiles(f, pdfFiles);
      } else if (f.isFile()) {
        if (f.getAbsolutePath().toLowerCase().endsWith(PDF_SUFIXX)) {
          pdfFiles.add(f.getAbsolutePath());
        }
      }
    }
    return pdfFiles;
  }

}
