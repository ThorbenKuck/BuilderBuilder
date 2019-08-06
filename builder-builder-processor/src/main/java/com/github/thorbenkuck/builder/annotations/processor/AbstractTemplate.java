package com.github.thorbenkuck.builder.annotations.processor;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.annotation.processing.Filer;
import javax.tools.FileObject;
import javax.tools.StandardLocation;

public class AbstractTemplate {

  private String template;
  public static Filer filer;

  protected AbstractTemplate(String templateName) {
    template = loadTemplate(templateName);
  }

  private String loadTemplate(String templateName) {
    if (!templateName.startsWith("template/")) {
      templateName = "template/" + templateName;
    }
    if(!templateName.endsWith(".template")) {
      templateName += ".template";
    }
    FileObject url;
    try {
      url = filer.getResource(StandardLocation.CLASS_PATH, "", templateName);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
    if (url == null) {
      throw new IllegalStateException("Could not find the template " + templateName);
    }

    try {
      StringBuilder stringBuilder = new StringBuilder();
      BufferedReader bufferedReader = new BufferedReader(url.openReader(true));
      bufferedReader.lines().forEach(s -> stringBuilder.append(s).append("\n"));
      return stringBuilder.toString();
    } catch (IOException e) {
      throw new IllegalStateException("Could not read the template " + templateName, e);
    }
  }

  protected String getTemplate() {
    return template;
  }

  protected void replace(String key, String value) {
    String searchKey = "\\{" + key + "\\}";
    if(template.contains(searchKey)) {
      template = template.replaceAll(searchKey, value);
    }
  }
}
