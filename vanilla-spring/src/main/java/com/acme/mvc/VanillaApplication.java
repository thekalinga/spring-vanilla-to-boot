package com.acme.mvc;

import org.apache.catalina.Container;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;

import java.io.File;
import java.io.IOException;

public class VanillaApplication {
  private static final int PORT = 8080;

  public static void main(String[] args) throws LifecycleException {
    String appBase = ".";
    Tomcat tomcat = new Tomcat();
    tomcat.setBaseDir(createTempDir());
    tomcat.setPort(PORT);
    tomcat.getHost().setAppBase(appBase);
    tomcat.addWebapp("", ".");
    tomcat.getConnector(); // Trigger the creation of the default connector
    tomcat.start();
    tomcat.getServer().await();
  }

  private static Context findContext(Tomcat tomcat) {
    for (Container child : tomcat.getHost().findChildren()) {
      if (child instanceof Context) {
        return (Context) child;
      }
    }
    throw new IllegalStateException("The host does not contain a Context");
  }

  // based on AbstractEmbeddedServletContainerFactory
  private static String createTempDir() {
    try {
      File tempDir = File.createTempFile("tomcat.", "." + PORT);
      tempDir.delete();
      tempDir.mkdir();
      tempDir.deleteOnExit();
      return tempDir.getAbsolutePath();
    } catch (IOException ex) {
      throw new RuntimeException(
          "Unable to create tempDir. java.io.tmpdir is set to " + System.getProperty("java.io.tmpdir"),
          ex
      );
    }
  }
}


