package com.github.thorbenkuck.builder.annotations.processor;

public class MethodTemplate {

  private String indentation = System.getProperty("indentation") == null ? "  " : System.getProperty("indentation");
  private String body;
  private int depth = 0;

  private String generateIndentation() {
    String indentation = "";
    for(int i = 0 ; i < depth ; i++) {
      indentation += this.indentation;
    }

    return indentation;
  }

  public MethodTemplate appendStatement(String statement) {
    body += generateIndentation() + statement + "\n";

    return this;
  }

  public MethodTemplate beginControlFlow(String statement) {
    ++depth;
    appendStatement(statement + " {");
    return this;
  }

  public MethodTemplate endControlFlow() {
    --depth;
    if(depth < 0) {
      throw new IllegalStateException("Illegal end of control flow");
    }
    appendStatement("}");
    return this;
  }

  public String compile() {
    return body;
  }
}
