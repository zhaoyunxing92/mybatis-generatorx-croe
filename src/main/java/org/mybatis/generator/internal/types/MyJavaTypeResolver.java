package org.mybatis.generator.internal.types;

import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;

import java.sql.Types;

/**
 * @author zhaoyunxing
 * @date: 2018-12-07 11:30
 * @des: TINYINT >> Integer
 */
public class MyJavaTypeResolver extends JavaTypeResolverDefaultImpl {

    public MyJavaTypeResolver() {
        super();
        super.typeMap.put(Types.TINYINT, new JavaTypeResolverDefaultImpl.JdbcTypeInformation("TINYINT", new FullyQualifiedJavaType(Integer.class.getName())));
    }
}
