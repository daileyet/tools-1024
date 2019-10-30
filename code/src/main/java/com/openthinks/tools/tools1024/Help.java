package com.openthinks.tools.tools1024;

import java.util.Arrays;

public class Help implements App {

  @Override
  public void exec(String[] args) throws Exception {
    if (args == null || args.length == 0) {
      System.out.println(usage());
      return;
    }
    if (Arrays.asList(args).contains("--HELP")) {
      System.out.println(usage());
    }
  }

  @Override
  public String usage() {
    StringBuilder sb = new StringBuilder();
 // @formatter:off
    sb.append("\n----------------------------------\n")
      .append("\n1024 Tools Usage:\n")
      .append("\n1. PDF tool help:\n")
      .append("--PDF -H\n")
      .append("\n2. Image tool help:\n")
      .append("--IMG -H\n")
      .append("\n----------------------------------\n");
 // @formatter:on
    return sb.toString();
  }



}
