package com.stottlerhenke.simbionic.common.xmlConverters.model;

public class ProjectProperties {

	public ProjectProperties () {
		
	}
	
	public String getAuthor() {
		return author;
	}
	public void setAuthor(String author) {
		this.author = author;
	}
	public String getProjectName() {
		return projectName;
	}
	public void setProjectName(String projectName) {
		this.projectName = projectName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getDateLastUpdate() {
		return dateLastUpdate;
	}
	public void setDateLastUpdate(String dateLastUpdate) {
		this.dateLastUpdate = dateLastUpdate;
	}
	public String getSimbionicVersion() {
		return simbionicVersion;
	}
	public void setSimbionicVersion(String simbionicVersion) {
		this.simbionicVersion = simbionicVersion;
	}

	String author;
	String projectName;
	String description;
	String dateLastUpdate;
	String simbionicVersion;
}
