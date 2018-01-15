package com.tapc.platform.model.scancode.dao.response;

public class OpenDeviceAck {
	private int command;
	private String device_id;
	private String user_id;
	private String status;

	public OpenDeviceAck(int command, String device_id, String user_id, String status) {
		this.command = command;
		this.device_id = device_id;
		this.setUser_id(user_id);
		this.status = status;
	}

	public int getCommand() {
		return command;
	}

	public void setCommand(int command) {
		this.command = command;
	}

	public String getDevice_id() {
		return device_id;
	}

	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

}
