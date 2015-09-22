package persistence.annotation;
import java.lang.annotation.*;

/**
 * Annotation de classe persistante<br/>
 * Permet de préciser le nom de la table associée
 * Par défaut le nom simple de la classe
 * @author dga
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(value={ElementType.TYPE})
public @interface Table {
  String name();
}
