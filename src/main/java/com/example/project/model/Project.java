package com.example.project.model;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.JsonObject;

@DataObject
public class Project {

	private long projectid;
	private String owner_firstname;
	private String owner_lastname;
	private String owner_email;
	private String project_title;
	private String project_description;
	private String project_status;
	
	public Project() {

    }

    public Project(JsonObject json) {
        this.projectid = json.getLong("projectid");
        this.owner_firstname = json.getString("owner_firstname");
        this.owner_lastname = json.getString("owner_lastname");
        this.owner_email = json.getString("owner_email");
        this.project_title = json.getString("project_title");
        this.project_description = json.getString("project_description");
        this.project_status = json.getString("project_status");
        
    }
    
	public long getProjectid() {
		return projectid;
	}
	public void setProjectid(long projectid) {
		this.projectid = projectid;
	}
	public String getOwner_firstname() {
		return owner_firstname;
	}
	public void setOwner_firstname(String owner_firstname) {
		this.owner_firstname = owner_firstname;
	}
	public String getOwner_lastname() {
		return owner_lastname;
	}
	public void setOwner_lastname(String owner_lastname) {
		this.owner_lastname = owner_lastname;
	}
	public String getOwner_email() {
		return owner_email;
	}
	public void setOwner_email(String owner_email) {
		this.owner_email = owner_email;
	}
	public String getProject_title() {
		return project_title;
	}
	public void setProject_title(String project_title) {
		this.project_title = project_title;
	}
	public String getProject_description() {
		return project_description;
	}
	public void setProject_description(String project_description) {
		this.project_description = project_description;
	}
	public String getProject_status() {
		return project_status;
	}
	public void setProject_status(String project_status) {
		this.project_status = project_status;
	}
	public JsonObject toJson() {

        final JsonObject json = new JsonObject();
        json.put("projectid", this.projectid);
        json.put("owner_firstname", this.owner_firstname);
        json.put("owner_lastname", this.owner_lastname);
        json.put("owner_email", this.owner_email);
        json.put("project_title", this.project_title);
        json.put("project_description", this.project_description);
        json.put("project_status", this.project_status);

        return json;
    }
	
}
