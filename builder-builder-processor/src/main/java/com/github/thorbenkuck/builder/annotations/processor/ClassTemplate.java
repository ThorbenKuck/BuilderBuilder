package com.github.thorbenkuck.builder.annotations.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.lang.model.element.Modifier;

public class ClassTemplate extends AbstractTemplate {

  private List<Modifier> modifiers = new ArrayList<>();
  private List<MethodTemplate> methodTemplates = new ArrayList<>();
  private final String name;

  public ClassTemplate(String name) {
    super("class");
    replace("type", "class");
    replace("name", name);
    this.name = name;
  }

  public ClassTemplate addModifiers(Modifier... modifiers) {
    this.modifiers.addAll(Arrays.asList(modifiers));
    return this;
  }

  public ClassTemplate addMethod(MethodTemplate methodTemplate) {
    methodTemplates.add(methodTemplate);
    return this;
  }

  public String compile() {
    String join = String.join(" ", modifiers.stream()
        .map(Modifier::toString)
        .collect(Collectors.toSet()));

    replace("modifiers", join);
    replace("fields", "");
    replace("constructor", "");
    replace("methods", compileMethods());
    return null;
  }

  private String compileMethods() {
    StringBuilder resultBuilder = new StringBuilder();
    for(MethodTemplate methodTemplate : methodTemplates) {
      resultBuilder.append(methodTemplate.compile());
    }
    return resultBuilder.toString();
  }

  public CharSequence getName() {
    return name;
  }
}
