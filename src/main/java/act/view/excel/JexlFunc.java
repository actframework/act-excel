package act.view.excel;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Mark a class as Jexl function.
 *
 * See http://jxls.sourceforge.net/reference/expression_language.html
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface JexlFunc {
    /**
     * Specify the namespace of the function
     * @return the function namespace
     */
    String value();
}
