package com.openthinks.tools.tools1024;

import java.util.Iterator;
import java.util.ServiceLoader;

/**
 * Main starter
 * 
 * @author dailey.dai@openthinks.com
 *
 */
public class Bootstrap {
  public static void main(String[] args) {
    ServiceLoader<App> serviceLoader = ServiceLoader.load(App.class);
    Iterator<App> iterator = serviceLoader.iterator();
    while (iterator.hasNext()) {
      App app = iterator.next();
      processApp(app, args);
    }
  }

  static void processApp(App app, String[] args) {
    try {
      app.exec(args);
    } catch (Exception e) {
      System.err.println("Unexpected exception or invalid arguments.:" + e.getMessage());
      System.err.println("\n" + app.usage());
    }
  }
}
