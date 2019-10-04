package com.yc.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
/**
 * 、@Documented:	用于标记在生成javadoc时是否将注解包含进去，可以看到这个注解和@Override一样，注解中空空如也，什么东西都没有
 * 、3、@Inherited 允许子类继承父类中的注解，可以通过反射获取到父类的注解
 * @Constraint  用于校验属性值是否合法
 * @author LQ
 *
 */
@Target(ElementType.TYPE)   //说明了Annotation所修饰的对象范围
@Retention(RetentionPolicy.RUNTIME)   //注解的声明周期，用于定义注解的存活阶段，可以存活在源码级别、编译级别(字节码级别)、运行时级别。
public @interface WebServlet {
	String[] value() default {};
}
