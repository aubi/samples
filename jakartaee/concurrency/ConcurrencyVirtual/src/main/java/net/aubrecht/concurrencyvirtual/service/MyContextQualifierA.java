package net.aubrecht.concurrencyvirtual.service;

import jakarta.inject.Qualifier;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * My qualifier used to inject my context definition.
 *
 * @author aubi
 */
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
//@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE})
@Target(ElementType.FIELD)
public @interface MyContextQualifierA {

//    class Literal extends AnnotationLiteral<MyContextQualifierA> implements MyContextQualifierA {
//
//        private static final long serialVersionUID = 1L;
//
//        private static Literal inst = new Literal();
//
//        public static Literal get() {
//            return inst;
//        }
//    }

}
