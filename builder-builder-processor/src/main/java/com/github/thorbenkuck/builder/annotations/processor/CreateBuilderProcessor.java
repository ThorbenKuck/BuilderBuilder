package com.github.thorbenkuck.builder.annotations.processor;

import com.github.thorbenkuck.builder.annotations.IgnoreField;
import com.github.thorbenkuck.builder.annotations.InstantiationBuilder;
import com.github.thorbenkuck.builder.annotations.SetterName;
import com.github.thorbenkuck.builder.annotations.ValidateState;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.*;
import com.squareup.javapoet.CodeBlock.Builder;

import javax.annotation.Nullable;
import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import java.io.IOException;
import java.util.*;

@AutoService(Processor.class)
public class CreateBuilderProcessor extends AbstractProcessor {

    private Filer filer;

    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(InstantiationBuilder.class.getName());
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnvironment.getFiler();
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elementsAnnotatedWith = roundEnvironment.getElementsAnnotatedWith(InstantiationBuilder.class);
        for (Element element : elementsAnnotatedWith) {
            if (element.getKind().isClass()) {
                try {
                    handleAnnotatedClass((TypeElement) element);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

    private Map<String, VariableElement> mapAttributes(TypeElement typeElement) {
        Map<String, VariableElement> mapping = new HashMap<>();

        typeElement.getEnclosedElements()
                .stream()
                .filter(e -> e.getKind() == ElementKind.FIELD)
                .map(e -> (VariableElement) e)
                .filter(v -> v.getAnnotation(IgnoreField.class) == null)
                .forEach(variableElement -> {
                    SetterName setterName = variableElement.getAnnotation(SetterName.class);
                    String name;
                    if (setterName != null) {
                        name = setterName.value();
                    } else {
                        String rawName = variableElement.getSimpleName().toString();
                        name = "with" + rawName.substring(0, 1).toUpperCase() + rawName.substring(1);
                    }
                    mapping.put(name, variableElement);
                });

        return mapping;
    }

    private void applyRequiredSpecs(TypeSpec.Builder builder, TypeElement typeElement) {
        MethodSpec newInstance = MethodSpec.methodBuilder("instantiateConstruct")
                .returns(TypeName.get(typeElement.asType()))
                .addModifiers(Modifier.PRIVATE)
                .addCode(CodeBlock.builder()
                        .addStatement("return new $T()", ClassName.get(typeElement.asType()))
                        .build())
                .build();

        FieldSpec valueField = FieldSpec.builder(TypeName.get(typeElement.asType()), "value")
                .addModifiers(Modifier.PRIVATE)
                .build();

        MethodSpec build = MethodSpec.methodBuilder("build")
                .addModifiers(Modifier.PUBLIC)
                .returns(TypeName.get(typeElement.asType()))
                .addCode(CodeBlock.builder()
                        .addStatement("validate()")
                        .addStatement("$T returnValue = value", ClassName.get(typeElement.asType()))
                        .addStatement("value = instantiateConstruct()")
                        .addStatement("return returnValue")
                        .build())
                .build();

        MethodSpec constructor = MethodSpec.constructorBuilder()
                .addModifiers(Modifier.PRIVATE)
                .addCode(CodeBlock.builder()
                        .addStatement("value = instantiateConstruct()")
                        .build())
                .build();

        builder.addField(valueField)
                .addMethod(constructor)
                .addMethod(newInstance)
                .addMethod(build);
    }

    private void constructValidationMethod(TypeSpec.Builder builder, TypeElement typeElement) {
        ExecutableElement executableElement = null;
        for (Element enclosedElement : typeElement.getEnclosedElements()) {
            if (enclosedElement.getKind() == ElementKind.METHOD) {
                if (enclosedElement.getAnnotation(ValidateState.class) != null) {
                    if (executableElement != null) {
                        throw new IllegalStateException("Multiple definitions found for ValidateState!");
                    }

                    executableElement = (ExecutableElement) enclosedElement;
                }
            }
        }

        if (executableElement == null) {
            builder.addMethod(MethodSpec.methodBuilder("validate")
                    .addModifiers(Modifier.PRIVATE)
                    .addCode(CodeBlock.builder()
                            .addStatement("// TODO delegate to annotated method")
                            .build())
                    .build());
        } else {
            if (!executableElement.getParameters().isEmpty()) {
                throw new IllegalStateException("The Validation method has to have zero arguments!");
            }
            CodeBlock.Builder validateCode = CodeBlock.builder();
            if (executableElement.getModifiers().contains(Modifier.PRIVATE)) {
                validateCode.addStatement("$T.invokeMethod(value, $S)", ReflectionUtils.class, executableElement.getSimpleName().toString());
            } else {
                validateCode.addStatement("value.$L()", executableElement.getSimpleName().toString());
            }

            builder.addMethod(MethodSpec.methodBuilder("validate")
                    .addModifiers(Modifier.PRIVATE)
                    .addCode(validateCode.build())
                    .build());
        }

    }

    private void handleAnnotatedClass(TypeElement annotatedClass) throws IOException {
        InstantiationBuilder annotation = annotatedClass.getAnnotation(InstantiationBuilder.class);
        String builderName = annotation.builderName();
        if (builderName.equals(InstantiationBuilder.EMPTY)) {
            builderName = annotatedClass.getSimpleName().toString() + "Builder";
        }
        String packageName = ((PackageElement) annotatedClass.getEnclosingElement())
                .getQualifiedName()
                .toString();

        List<MethodSpec> setterMethods = new ArrayList<>();
        Map<String, VariableElement> stringVariableElementMap = mapAttributes(annotatedClass);

        TypeSpec.Builder builder = TypeSpec.classBuilder(builderName)
                .addModifiers(Modifier.PUBLIC);

        for (Map.Entry<String, VariableElement> entry : stringVariableElementMap.entrySet()) {
            VariableElement variable = entry.getValue();
            MethodSpec.Builder setterBuilder = MethodSpec.methodBuilder(entry.getKey())
                    .addModifiers(Modifier.PUBLIC)
                    .returns(ClassName.get(packageName, builderName))
                    .addParameter(TypeName.get(variable.asType()), "newValue");

            String variableName = variable.getSimpleName().toString();

            CodeBlock.Builder codeBlockBuilder = CodeBlock.builder();

            if(variable.getAnnotation(Nullable.class) == null) {
                codeBlockBuilder.addStatement("$T.notNull(newValue, $S)", Validate.class, "The field " + variableName + " may not be null!");
            }

            if (variable.getModifiers().contains(Modifier.PRIVATE)) {
                codeBlockBuilder.addStatement("$T.setField(value, $S, newValue)", ReflectionUtils.class, variableName);
            } else {
                codeBlockBuilder.addStatement("value.$L = newValue", variableName);
            }

            codeBlockBuilder.addStatement("return this");
            setterBuilder.addCode(codeBlockBuilder.build());

            setterMethods.add(setterBuilder.build());
        }

        applyRequiredSpecs(builder, annotatedClass);
        constructValidationMethod(builder, annotatedClass);

        builder.addMethod(MethodSpec.methodBuilder("newInstance")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .returns(ClassName.get(packageName, builderName))
                .addCode(CodeBlock.builder()
                        .addStatement("return new $T()", ClassName.get(packageName, builderName))
                        .build())
                .build());

        for (MethodSpec methodSpec : setterMethods) {
            builder.addMethod(methodSpec);
        }

        JavaFile.builder(packageName, builder.build())
                .build()
                .writeTo(filer);
    }
}
