package hu.jinfeng.mybatis.provider;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * @Author hujinfeng  @Date 2020/11/27
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Entity {
    /**
     * 表名
     **/
    String table() default "";

    /**
     * 主键字段名
     **/
    String primaryKey() default "id";

    /**
     * 当没有设置Column注解的name时，读取Entity配置。默认驼峰对应关系，属性名 userName <-映射-> 表字段名user_name
     */
    boolean camelCase() default true;

    /**
     * 分表名字生成器
     **/
    Class sharingTable() default Object.class;
}
