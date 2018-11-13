/**
 * Copyright(C) 2018 Hangzhou zhaoyunxing Technology Co., Ltd. All rights reserved.
 */
package org.mybatis.generator.plugins;

import org.codehaus.plexus.util.StringUtils;
import org.mybatis.generator.api.*;
import org.mybatis.generator.api.dom.java.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.StringTokenizer;

/**
 * @author zhaoyunxing
 * @class: org.mybatis.generator.plugins.InterFaceExtendsPlugin
 * @date: 2018-08-17 00:39
 * @des:
 */
public class InterFaceExtendsPlugin extends PluginAdapter {
    /**
     * 根mapper
     */
    private String baseMapper;
    /**
     * 根model型
     */
    private String baseModel;

    /**
     * 主键类型，默认获取数据库表的第一个字段类型
     */
    private String primaryKeyType;

    @Override
    public void setProperties(Properties properties) {
        super.setProperties(properties);

        /*
         * 添加baseMapper
         */
        baseMapper = this.properties.getProperty("baseMapper");
        /*
         * 添加baseModel
         */
        baseModel = this.properties.getProperty("baseModel");
    }

    /**
     * 生成的mapper接口能够继承基类方法
     *
     * @param interfaze
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass,
                                   IntrospectedTable introspectedTable) {

        //introspectedTable.getContext().getJavaClientGeneratorConfiguration().getTargetProject()
        //mapper 文件生成过一次就生成
        if (hasInterfaceMapperFile(introspectedTable.getContext().getJavaClientGeneratorConfiguration().getTargetProject(), interfaze.getType().getPackageName(), interfaze.getType().getShortName())) {
            return false;
        }
        //C:\code\java\ccclubs-ntsp-open-api\src\main\java\com\sunny\boot\cherrytomato\vehicleState\mapper\TbStateMapper.java
        // 获取实体类
        FullyQualifiedJavaType entityType = new FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
        // import接口
        if (!StringUtils.isEmpty(baseMapper)) {
            interfaze.getMethods().clear();
            interfaze.addImportedType(new FullyQualifiedJavaType(baseMapper));
            interfaze.addSuperInterface(new FullyQualifiedJavaType(baseMapper + "<" + entityType.getShortName() + "," + primaryKeyType + ">"));
        }

        // import实体类
        interfaze.addImportedType(entityType);
        //添加 @Repository注解
        interfaze.addAnnotation("@Repository");
        interfaze.addImportedType(new FullyQualifiedJavaType("org.springframework.stereotype.Repository"));
        return true;
    }

    /**
     * 是否有该文件
     *
     * @param targetProject 项目名称
     * @param targetPackage 包名称
     * @param name          文件名称
     * @return true/false
     */
    private boolean hasInterfaceMapperFile(String targetProject, String targetPackage, String name) {
        StringBuilder sb = new StringBuilder();
        sb.append(targetProject);
        StringTokenizer st = new StringTokenizer(targetPackage, ".");
        while (st.hasMoreTokens()) {
            sb.append(File.separatorChar);
            sb.append(st.nextToken());
        }
        sb.append(File.separatorChar);
        sb.append(name);
        sb.append(".java");
        return new File(sb.toString()).exists();
    }

    /**
     * model类方法
     *
     * @param topLevelClass
     * @param introspectedTable
     * @return
     */
    @Override
    public boolean modelBaseRecordClassGenerated(TopLevelClass topLevelClass,
                                                 IntrospectedTable introspectedTable) {
        addSerialVersionUID(topLevelClass, introspectedTable);
        // 获取表第一个字段作为主键类型
        //        primaryKeyType = topLevelClass.getFields().get(0).getType().getShortName();
        // 多组件情况下，值去第一个
        primaryKeyType = introspectedTable.getPrimaryKeyColumns().get(0).getFullyQualifiedJavaType().getShortName();
        ;
        if (!StringUtils.isEmpty(baseModel)) {
            // topLevelClass.
            topLevelClass.addImportedType(baseModel);
            topLevelClass.setSuperClass(new FullyQualifiedJavaType(baseModel + "<" + primaryKeyType + ">"));
        }

        return super.modelBaseRecordClassGenerated(topLevelClass, introspectedTable);
    }

    /**
     * 生成实体中每个属性
     */
    @Override
    public boolean modelGetterMethodGenerated(Method method, TopLevelClass topLevelClass,
                                              IntrospectedColumn introspectedColumn, IntrospectedTable introspectedTable, ModelClassType modelClassType) {
        return true;
    }

    @Override
    public boolean clientInsertMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean clientInsertSelectiveMethodGenerated(Method method, Interface interfaze, IntrospectedTable introspectedTable) {
        return false;
    }

    @Override
    public boolean validate(List<String> warnings) {
        return true;
    }

    @Override
    public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {
        JavaFormatter javaFormatter = context.getJavaFormatter();
        List<GeneratedJavaFile> mapperJavaFiles = new ArrayList<GeneratedJavaFile>();

        return mapperJavaFiles;
    }

    /**
     * 添加序列号id
     *
     * @param topLevelClass
     * @param introspectedTable
     */
    private void addSerialVersionUID(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
        CommentGenerator commentGenerator = context.getCommentGenerator();
        Field field = new Field();
        field.setVisibility(JavaVisibility.PRIVATE);
        field.setType(new FullyQualifiedJavaType("long"));
        field.setStatic(true);
        field.setFinal(true);
        field.setName("serialVersionUID");
        field.setInitializationString("1L");
        commentGenerator.addFieldComment(field, introspectedTable);
        topLevelClass.addField(field);
    }
}
