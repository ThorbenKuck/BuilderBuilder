package com.github.thorbenkuck.builder.annotations.processor;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import javax.annotation.processing.Filer;

public class FileTemplate extends AbstractTemplate {

  private ClassTemplate classTemplate;
  private List<String> imports = new ArrayList<>();

  private FileTemplate() {
    super("file");
  }

  private void withClassTemplate(ClassTemplate classTemplate) {
    this.classTemplate = classTemplate;
  }

  public FileTemplate alterClassTemplate(Consumer<ClassTemplate> consumer) {
    consumer.accept(classTemplate);

    return this;
  }

  public FileTemplate setPackage(String packageName) {
    replace("package", packageName);

    return this;
  }

  public FileTemplate addImport(String importStatement) {
    imports.add(importStatement);

    return this;
  }

  public static FileTemplate forClass(String name) {
    FileTemplate fileTemplate = new FileTemplate();
    fileTemplate.withClassTemplate(new ClassTemplate(name));

    return fileTemplate;
  }

  public void write(Filer filer) {
    String join = String.join("\n", imports);
    replace("imports", join);
    replace("class", classTemplate.compile());

    try {
      filer.createClassFile(classTemplate.getName())
          .openWriter()
          .write(getTemplate());
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

}
