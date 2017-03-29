package com.compiler.model;

/**
 * Created by zhaoying on 2017/3/29.
 */

import com.compiler.TypeUtil;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterizedTypeName;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;

/**
 * AnnotationClass 指添加过注解的类，注解指 onClick、BindView、Meta 等定义的注解
 * 一个类可能包含多个注解，不同的注解对应不同的功能
 */
public class AnnotationClass {


    private TypeElement mClassTypeElement ;
    private List<BaseField> mFields ;
    private Elements mElementUtil ;
    /**
     *
     * @param typeElement   对应的类元素
     * @param mElement  对应的元素处理的工具
     */
    public AnnotationClass(TypeElement typeElement, Elements mElement) {
        this.mClassTypeElement = typeElement ;
        this.mElementUtil = mElement ;
        mFields = new ArrayList<>();
    }

    public void addField(BaseField metaField) {
        mFields.add(metaField);
    }


    public JavaFile generateFinder() {
        MethodSpec.Builder methodSpec = MethodSpec.methodBuilder("inject") // 方法名
                .addModifiers(Modifier.PUBLIC)                  // 方法访问类型
                .addAnnotation(Override.class)                  // 方法注解
                .addParameter(TypeName.get(mClassTypeElement.asType()),"host",Modifier.FINAL)   // 参数类型和参数
                .addParameter(TypeName.OBJECT,"source")         // 参数类型和参数
                .addParameter(TypeUtil.PROVIDER,"provider");    // 参数类型和参数

        for (BaseField field : mFields){
            if (field instanceof MetaField){

            }

            if (field instanceof BindViewField){
                methodSpec.addStatement("host.$N = ($T)(provider.getView(source,$L))" ,
                        ((BindViewField) field).getFieldName(),
                        ((BindViewField) field).getFieldType(),
                        ((BindViewField) field).getResId()) ;
            }

            if (field instanceof OnClickMethod){
//                methodSpec.addStatement("$T listener")
            }
        }

        TypeSpec finderClass = TypeSpec.classBuilder(mClassTypeElement.getSimpleName() + "$$Finder") // 类名
                .addModifiers(Modifier.PUBLIC)      // 类的修饰符
                .addSuperinterface(ParameterizedTypeName.get(TypeUtil.FINDER,TypeName.get(mClassTypeElement.asType())))// 实现接口，使用泛型，传入具体类和泛型类
                .addMethod(methodSpec.build())
                .build() ;

        String packageName = mElementUtil.getPackageOf(mClassTypeElement).getQualifiedName().toString() ;

        return JavaFile.builder(packageName,finderClass).build() ;

    }
}
