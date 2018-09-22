package ua.skillsup.practice.example.context;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.Objects;

public class FreeDaysHandler implements InvocationHandler {
	private final Object original;

	public FreeDaysHandler(Object original) {
		this.original = original;
	}

	public Object invoke(Object proxy, Method method, Object[] args)
			throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException {
		FreeDay annotation = method.getAnnotation(FreeDay.class);
		if (Objects.isNull(annotation)) {
			annotation = original.getClass().getMethod(method.getName(), method.getParameterTypes())
					.getAnnotation(FreeDay.class);
		}

		if (!Objects.isNull(annotation) && LocalDate.now().getDayOfWeek() == annotation.value()) {
			throw new IllegalStateException("I have a rest day today!");
		}
		method.invoke(original, args);
		return null;
	}
}
