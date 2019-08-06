package com.github.thorbenkuck.builder.annotations.processor;

import com.github.thorbenkuck.builder.annotations.InstantiationBuilder;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import java.io.IOException;
import java.util.Collections;
import java.util.Set;

@AutoService(Processor.class)
@SupportedAnnotationTypes("com.github.thorbenkuck.builder.annotations.CreateBuilder")
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
        AbstractTemplate.filer = filer;
    }

    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        Set<? extends Element> elementsAnnotatedWith = roundEnvironment.getElementsAnnotatedWith(InstantiationBuilder.class);
        for(Element element : elementsAnnotatedWith) {
            if(element.getKind().isClass()) {
                try {
                    handleAnnotatedClass((TypeElement) element);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        return true;
    }

    private void handleAnnotatedClass(TypeElement annotatedClass) throws IOException {
        InstantiationBuilder annotation = annotatedClass.getAnnotation(InstantiationBuilder.class);
        String builderName = annotation.builderName();
        if(builderName.equals(InstantiationBuilder.EMPTY)) {
            builderName = annotatedClass.getSimpleName().toString() + "Builder";
        }
        String packageName = ((PackageElement) annotatedClass.getEnclosingElement())
                .getQualifiedName()
                .toString();

        FileTemplate.forClass(builderName)
            .setPackage(packageName)
            .alterClassTemplate(classTemplate -> classTemplate.addModifiers(Modifier.PUBLIC, Modifier.FINAL))
            .write(filer);

//        TypeSpec builderClass = TypeSpec.classBuilder(builderName)
//                .addModifiers(Modifier.PUBLIC)
//                .build();
//
//        JavaFile.builder(packageName, builderClass)
//                .build()
//                .writeTo(filer);
    }
}
