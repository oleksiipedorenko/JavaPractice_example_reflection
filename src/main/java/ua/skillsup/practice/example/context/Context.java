package ua.skillsup.practice.example.context;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Proxy;
import java.util.*;
import java.util.stream.Collectors;

public class Context {

	private final Map<Class<?>, Object> instances = new HashMap<>();

	public Context(Class<?>... classes)
			throws IllegalAccessException, InstantiationException, InvocationTargetException {
		Map<Class<?>, List<Class<?>>> dependenciesMap = prepareDefinitions(classes);
		createServices(dependenciesMap);
	}

	private Map<Class<?>, List<Class<?>>> prepareDefinitions(Class<?>[] classes) {
		Map<Class<?>, List<Class<?>>> dependenciesMap = new HashMap<>();
		for (Class<?> aClass : classes) {
			Constructor<?>[] constructors = aClass.getDeclaredConstructors();
			Constructor<?> constructorToUse;
			if (constructors.length > 1) {
				List<Constructor<?>> constructorsWithInject = Arrays.stream(constructors)
						.filter(constructor -> !Objects.isNull(constructor.getAnnotation(Inject.class)))
						.collect(Collectors.toList());
				if (constructorsWithInject.size() > 1) {
					throw new IllegalStateException("More than 1 constructor with inject found for " + aClass.getName());
				} else if (constructorsWithInject.size() == 0) {
					throw new IllegalStateException("Found several constructors, but no Inject annotation present for " + aClass.getName());
				}
				constructorToUse = constructorsWithInject.get(0);
			} else {
				constructorToUse = constructors[0];
			}
			dependenciesMap.put(aClass, Arrays.asList(constructorToUse.getParameterTypes()));
		}
		return dependenciesMap;
	}

	private void createServices(Map<Class<?>, List<Class<?>>> dependenciesMap)
			throws IllegalAccessException, InvocationTargetException, InstantiationException {
		Set<Class<?>> notYetCreated = new HashSet<>(dependenciesMap.keySet());

		boolean atLeastSomethingWasCreated;
		do {
			List<Class<?>> created = new ArrayList<>();
			for (Class<?> candidate : notYetCreated) {
				List<Class<?>> dependencies = dependenciesMap.get(candidate);
				if (dependencies.isEmpty()) {
					Object newInstance = candidate.getConstructors()[0].newInstance();
					addNewInstance(candidate, newInstance);
					created.add(candidate);
				} else {
					if (dependencies.stream().allMatch(this.instances::containsKey)) {
						Constructor<?> constructor = candidate.getConstructors()[0];
						Object newInstance = constructor.newInstance(
								Arrays.stream(constructor.getParameterTypes())
								.map(this.instances::get).toArray());
						addNewInstance(candidate, newInstance);
						created.add(candidate);
					}
				}
			}
			atLeastSomethingWasCreated = !created.isEmpty();
			notYetCreated.removeAll(created);
		} while (!notYetCreated.isEmpty() && atLeastSomethingWasCreated);

		if (!notYetCreated.isEmpty()) {
			throw new IllegalStateException("Failed to initialized all context items");
		}
	}

	private void addNewInstance(Class<?> candidate, Object newInstance) {
		this.instances.put(candidate, newInstance);
		for (Class<?> anInterface : candidate.getInterfaces()) {
			this.instances.put(anInterface, wrap(anInterface, newInstance));
		}
	}

	private <T> T wrap(Class<T> type, Object instance) {
		if (Arrays.stream(instance.getClass().getMethods())
				.anyMatch(method -> method.isAnnotationPresent(FreeDay.class))) {
			FreeDaysHandler handler = new FreeDaysHandler(instance);
			return  (T) Proxy.newProxyInstance(type.getClassLoader(),
					new Class[] { type },
					handler);
		} else {
			return (T) instance;
		}
	}

	public <T> T getInstance(Class<T> aClass) {
		return (T) this.instances.get(aClass);
	}
}