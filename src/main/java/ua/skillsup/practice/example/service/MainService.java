package ua.skillsup.practice.example.service;

import ua.skillsup.practice.example.context.Inject;
import ua.skillsup.practice.example.context.Service;

@Service
public class MainService {

	private final SlaveService slave;

	@Inject
	public MainService(SlaveService slave) {
		this.slave = slave;
	}

	public void doSmt() {
		slave.doWork();
	}
}