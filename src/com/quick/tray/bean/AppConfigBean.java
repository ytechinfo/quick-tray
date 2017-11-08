package com.quick.tray.bean;

import java.io.Serializable;

public class AppConfigBean implements Serializable {
	private static final long serialVersionUID = 6391302177775735162L;
	
	private String entryId;
	private String name;
	private String type;
	private String command;
	
	public String getEntryId() {
		return entryId;
	}
	public String getName() {
		return name;
	}
	public String getType() {
		return type;
	}
	public String getCommand() {
		return command;
	}

	public void setEntryId(String entryId) {
		this.entryId = entryId;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setCommand(String command) {
		this.command = command;
	}
	
	@Override
	public String toString() {
		return new StringBuilder()
				.append("name : ").append(this.name)
				.append(" type : ").append(this.type)
				.append(" command : ").append(this.command).toString();
	}
	
}
