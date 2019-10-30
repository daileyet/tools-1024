package com.openthinks.tools.tools1024.image;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import com.jhlabs.image.CropFilter;
import com.jhlabs.image.MirrorFilter;
import com.openthinks.tools.tools1024.AbstractApp;

public class ImageTool extends AbstractApp {

  static class CropInfo {
    int x = 0, y = 0, width = 32, height = 32;

    @Override
    public String toString() {
      return "CropInfo [x=" + x + ", y=" + y + ", width=" + width + ", height=" + height + "]";
    }

  }

  static class MirrorInfo {
    float opacity = 0.1f, centreY = 0.5f, distance = 1, angle = 0, rotation = 0, gap = 0;

    @Override
    public String toString() {
      return "MirrorInfo [opacity=" + opacity + ", centreY=" + centreY + ", distance=" + distance
          + ", angle=" + angle + ", rotation=" + rotation + ", gap=" + gap + "]";
    }
  }

  private BufferedImage inputImage;

  @Override
  public String usage() {
    StringBuilder sb = new StringBuilder();
    // @formatter:off
		sb.append("Image tool usage:\n")
		.append("\nHelp: -H\n")
		.append("\nCommon args:\n")
		.append("-I <input image file path> [-O <output image file path>]\n")
		.append("\nSupport feature:")
		.append("\n1) crop image:\n")
		.append("-CROP <crop rectangle:0 0 32 32>")
		.append("\n2) format image:\n")
		.append("-FORMAT <[PNG|JPG|GIF|BMP]>")
		.append("\n3 mirror image\n")
		.append("-MIRROR [-opacity <0.5>] [-centreY <0.5>] [-distance <1>] [-angle <0>] [-rotation <0>] [-gap <0>]")
			;
		// @formatter:on
    return sb.toString();
  }

  @Override
  protected void run(List<String> params) throws Exception {
    if (params.isEmpty()) {
      System.out.println(usage());
      return;
    }
    PartParams inputs = getPartParamsBy(params, "-I");
    if (inputs.isEmpty()) {
      System.out.println(usage());
      return;
    }
    String inputfile = inputs.get(0);
    String outputType = "PNG";
    try {
      outputType = inputfile.substring(inputfile.lastIndexOf(".") + 1);
    } catch (Exception e) {
    }
    setInputImage(readImage(inputfile));
    PartParams outputs = getPartParamsBy(params, "-O");
    String targetPath = inputfile;
    if (!outputs.isEmpty()) {
      targetPath = outputs.get(0);
    }
    if (params.contains("-CROP")) {
      doCropImage(params, inputfile);
    }
    if (params.contains("-MIRROR")) {
      doMirrorImage(params, inputfile);
    }

    if (params.contains("-FORMAT")) {
      String formatType = doFormatImage(params, inputfile);
      if (formatType != null) {
        outputType = formatType;
      }
    }
    if (inputImage != null) {
      ImageIO.write(inputImage, outputType, new File(targetPath));
      if (params.contains("-FORMAT")) {
        System.out.println("IMAGE:" + inputfile + " formated to " + outputType + " successfully.");
      }
    }

  }

  private void doMirrorImage(List<String> params, String inputfile) {
    MirrorInfo info = new MirrorInfo();
    PartParams opacitys = getPartParamsBy(params, "-opacity");
    PartParams centreYs = getPartParamsBy(params, "-centreY");
    PartParams distances = getPartParamsBy(params, "-distance");
    PartParams angles = getPartParamsBy(params, "-angle");
    PartParams rotations = getPartParamsBy(params, "-rotation");
    PartParams gaps = getPartParamsBy(params, "-gap");
    if (!opacitys.isEmpty()) {
      try {
        info.opacity = Float.valueOf(opacitys.get(0));
      } catch (NumberFormatException e) {
      }
    }
    if (!centreYs.isEmpty()) {
      try {
        info.centreY = Float.valueOf(centreYs.get(0));
      } catch (NumberFormatException e) {
      }
    }
    if (!distances.isEmpty()) {
      try {
        info.distance = Float.valueOf(distances.get(0));
      } catch (NumberFormatException e) {
      }
    }
    if (!angles.isEmpty()) {
      try {
        info.angle = Float.valueOf(angles.get(0));
      } catch (NumberFormatException e) {
      }
    }
    if (!rotations.isEmpty()) {
      try {
        info.rotation = Float.valueOf(rotations.get(0));
      } catch (NumberFormatException e) {
      }
    }
    if (!gaps.isEmpty()) {
      try {
        info.gap = Float.valueOf(gaps.get(0));
      } catch (NumberFormatException e) {
      }
    }

    MirrorFilter filter = new MirrorFilter();
    filter.setAngle(info.angle);
    filter.setCentreY(info.centreY);
    filter.setDistance(info.distance);
    filter.setOpacity(info.opacity);
    filter.setGap(info.gap);
    filter.setRotation(info.rotation);
    if (inputImage != null) {
      inputImage = filter.filter(inputImage, null);
      System.out.println("IMAGE:" + inputfile + " mirrored:" + info + " successfully.");
    }
  }

  private String doFormatImage(List<String> params, String inputfile) {
    String formatName = null;
    PartParams formats = getPartParamsBy(params, "-FORMAT");
    if (!formats.isEmpty()) {
      formatName = formats.get(0);
    }
    return formatName;
  }

  private void doCropImage(List<String> params, String inputfile) {
    PartParams crops = getPartParamsBy(params, "-CROP");
    CropInfo info = new CropInfo();
    if (!crops.isEmpty()) {
      try {
        info.x = Integer.valueOf(crops.get(0));
        info.y = Integer.valueOf(crops.get(1));
        info.width = Integer.valueOf(crops.get(2));
        info.height = Integer.valueOf(crops.get(3));
      } catch (Exception e) {
        System.out.println("Image tool crop processing use default crop rectangle:" + info);
      }
    }
    CropFilter filter = new CropFilter(info.x, info.y, info.width, info.height);
    BufferedImage updateBufferedImg = filter.filter(inputImage, null);
    setInputImage(updateBufferedImg);
    System.out.println("IMAGE:" + inputfile + " croped:" + info + " successfully.");
  }

  @Override
  protected String keyParam() {
    return "--IMG";
  }

  void setInputImage(BufferedImage inputImage) {
    this.inputImage = inputImage;
  }

  BufferedImage readImage(String file) throws IOException {
    return ImageIO.read(new File(file));
  }

}
