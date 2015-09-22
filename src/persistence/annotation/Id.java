package persistence.annotation;
import java.lang.annotation.*;

/**
 * Annotation deméthode get qui permet d'indiquer la propriété qui sert de clé identité
 * @author dga
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
public @interface Id {
	boolean autoGenerated() default false;
}
