package ua.skillsup.practice.example;

import ua.skillsup.practice.example.context.Context;
import ua.skillsup.practice.example.service.MainService;
import ua.skillsup.practice.example.service.SlaveServiceImpl;

import java.lang.reflect.InvocationTargetException;

public class App {

	public static void main(String[] args) throws IllegalAccessException, InvocationTargetException, InstantiationException {
		Context context = new Context(MainService.class, SlaveServiceImpl.class);

		MainService mainService = context.getInstance(MainService.class);
		mainService.doSmt();
	}
}
