package persistence.lazyloading;

import java.lang.reflect.Method;

import javassist.util.proxy.MethodFilter;

public class OneToManyFilter implements MethodFilter {

	@Override
	public boolean isHandled(Method arg0) {
		return true;
	}

}
