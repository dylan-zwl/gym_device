package com.tapc.platform.model.scancode.dao.response;

public class Randomcode {
	private String device_id;
	private String random_code;
	private String deadtime;

	public String getDevice_id() {
		return device_id;
	}

	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}

	public String getDeadtime() {
		return deadtime;
	}

	public void setDeadtime(String deadtime) {
		this.deadtime = deadtime;
	}

	public String getRandom_code() {
		return random_code;
	}

	public void setRandom_code(String random_code) {
		this.random_code = random_code;
	}
}
