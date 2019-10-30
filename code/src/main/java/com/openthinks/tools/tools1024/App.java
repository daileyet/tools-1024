package com.openthinks.tools.tools1024;

/**
 * interface for all stand-alone application
 * 
 * @author dailey.dai@openthinks.com
 *
 */
public interface App {

  /**
   * execute application
   * 
   * @param args arguments for command line input
   * @throws Exception
   */
  public void exec(String[] args) throws Exception;

  /**
   * accept these command line arguments or not
   * 
   * @param args arguments for command line input
   * @return true or false
   */
  public default boolean accept(String[] args) {
    return true;
  };

  /**
   * help of usage
   * 
   * @return description of usage
   */
  public default String usage() {
    return "";
  }
}
