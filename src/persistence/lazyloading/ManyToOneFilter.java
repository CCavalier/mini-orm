package persistence.lazyloading;

import java.lang.reflect.Method;

import javassist.util.proxy.MethodFilter;
import persistence.annotation.Id;
import persistence.annotation.Transient;

public class ManyToOneFilter implements MethodFilter {

	@Override
	public boolean isHandled(Method method) {
		if (method.getName().startsWith("get") // Une méthode get
				&& method.getParameterTypes().length == 0 // Sans paramètres
				&& method.getAnnotation(Transient.class) == null// Non transient
				&& method.getAnnotation(Id.class) == null ) { // Et qu'il ne s'agit pas de l'id
			return true;
		}
		return false;
	}

}
