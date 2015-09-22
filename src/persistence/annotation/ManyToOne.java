package persistence.annotation;
import java.lang.annotation.*;

/**
 * Annotation pour une propriété relation ManyToOne
 * @author dga
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.METHOD})
public @interface ManyToOne {
	boolean lazy() default false;
}
