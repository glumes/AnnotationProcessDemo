package com.compiler;

import com.annotation.BindView;
import com.annotation.Meta;
import com.annotation.OnClick;
import com.compiler.model.AnnotationClass;
import com.compiler.model.BindViewField;
import com.compiler.model.MetaField;
import com.compiler.model.OnClickMethod;
import com.google.auto.service.AutoService;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic;

/**
 * Created by zhaoying on 2017/3/29.
 */

@AutoService(Processor.class)
public class AnnotationProcesser extends AbstractProcessor {

    private Messager messager ;
    private Filer mFiler ;
    private Elements mElement ;



    @Override
    public Set<String> getSupportedAnnotationTypes() {
        Set<String> types = new LinkedHashSet<>();
        types.add(BindView.class.getCanonicalName());
        types.add(OnClick.class.getCanonicalName());
        types.add(Meta.class.getCanonicalName());
        return types;
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        messager = processingEnvironment.getMessager() ;
        mFiler = processingEnvironment.getFiler() ;
        mElement = processingEnvironment.getElementUtils() ;
    }

    /**
     *
     * @param set
     * @param roundEnvironment
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> set, RoundEnvironment roundEnvironment) {
        messager.printMessage(Diagnostic.Kind.NOTE,"start annotation process");

        Map<String,AnnotationClass> parseMap = parseAnnotations(roundEnvironment) ;

        for (Map.Entry<String,AnnotationClass> entry : parseMap.entrySet()){
            String annotationClassName = entry.getKey() ;
            AnnotationClass annotationClass = entry.getValue() ;
            try {
                annotationClass.generateFinder().writeTo(mFiler);
            } catch (IOException e) {
                print("can not get annotationClass %s",annotationClassName);
                e.printStackTrace();
            }
        }
        return true ;
    }

    public Map<String,AnnotationClass> parseAnnotations(RoundEnvironment roundEnvironment){
        Map<String, AnnotationClass> resultMap = new LinkedHashMap<>();

        processMetaAnnotation(roundEnvironment,resultMap);

        processBindViewAnnotation(roundEnvironment,resultMap);

        processOnClickAnnotation(roundEnvironment,resultMap);

        return resultMap ;
    }

    public void processMetaAnnotation(RoundEnvironment roundEnvironment,Map<String,AnnotationClass> map){

        for (Element element : roundEnvironment.getElementsAnnotatedWith(Meta.class)){
            AnnotationClass annotationClass = getAnnotationClass(map,element);
            MetaField metaField = new MetaField(element) ;
            annotationClass.addField(metaField) ;
        }
    }

    public void processBindViewAnnotation(RoundEnvironment roundEnvironment,Map<String,AnnotationClass> map){
        for (Element element : roundEnvironment.getElementsAnnotatedWith(BindView.class)){
            AnnotationClass annotationClass = getAnnotationClass(map,element) ;
            BindViewField bindView = new BindViewField(element);
            annotationClass.addField(bindView);
        }
    }

    public void processOnClickAnnotation(RoundEnvironment roundEnvironment,Map<String,AnnotationClass> map){
        for (Element element : roundEnvironment.getElementsAnnotatedWith(OnClick.class)){
            AnnotationClass annotationClass = getAnnotationClass(map,element);
            OnClickMethod onClickMethod = new OnClickMethod(element);
            annotationClass.addField(onClickMethod);
        }
    }

    /**
     * 从注解元素中得到它的父元素，就是注解所在的类
     * @param element
     * @return
     */
    private AnnotationClass getAnnotationClass(Map<String,AnnotationClass> map , Element element) {
        TypeElement typeElement = (TypeElement) element.getEnclosingElement(); // 获取父元素
        String annotationClassName = typeElement.getQualifiedName().toString() ; // 得到类的全限定名称
        AnnotationClass annotationClass = map.get(annotationClassName);
        if (annotationClass == null){
            annotationClass = new AnnotationClass(typeElement,mElement) ;
            map.put(annotationClassName,annotationClass);
        }
        return annotationClass;
    }




    /**
     * 打印消息，要在 Gradle Console 窗口中才能看到
     * @param message
     * @param args
     */
    private void print(String message, Object... args)
    {
        if (args.length > 0)
        {
            message = String.format(message, args);
        }
        messager.printMessage(Diagnostic.Kind.NOTE, message);
    }

}
