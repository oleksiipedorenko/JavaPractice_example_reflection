package ua.skillsup.practice.example.service;

import ua.skillsup.practice.example.context.FreeDay;
import ua.skillsup.practice.example.context.Service;

import java.time.DayOfWeek;

@Service
public class SlaveServiceImpl implements SlaveService {

	@FreeDay(DayOfWeek.SATURDAY)
	@Override
	public void doWork() {
		System.out.printf("I'm doing my job....");
	}
}