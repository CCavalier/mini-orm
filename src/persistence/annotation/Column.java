package persistence.annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation de méthode get qui permet de préciser le nom et le type SQL de la colonne
 * @author dga
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
public @interface Column {
  String name() default "";
  String type() default "";
}
