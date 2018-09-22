package ua.skillsup.practice.example.context;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.time.DayOfWeek;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface FreeDay {
	DayOfWeek value();
}