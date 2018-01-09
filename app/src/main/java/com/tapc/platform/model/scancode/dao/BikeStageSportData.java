package com.tapc.platform.model.scancode.dao;

public class BikeStageSportData {
	private String watt;
	private String resistance;
	private String speed;
	private String time;

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getWatt() {
		if (watt == null || watt.isEmpty()) {
			watt = "0";
		}
		return watt;
	}

	public void setWatt(String watt) {
		this.watt = watt;
	}

	public String getResistance() {
		if (resistance == null || resistance.isEmpty()) {
			resistance = "0";
		}
		return resistance;
	}

	public void setResistance(String resistance) {
		this.resistance = resistance;
	}

	public String getSpeed() {
		return speed;
	}

	public void setSpeed(String speed) {
		this.speed = speed;
	}
}
