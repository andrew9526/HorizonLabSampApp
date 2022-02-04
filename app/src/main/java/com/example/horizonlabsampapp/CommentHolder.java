package com.example.horizonlabsampapp;

public class CommentHolder {
	String comments= null;
	String user_id= null;
	String date_added= null;
	String name= null;


	public CommentHolder(String comments,
                         String user_id,
                         String date_added,
                         String name) {
		super();
		this.comments=comments;
		this.user_id=user_id;
		this.date_added=date_added;
		this.name=name;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

	public String getUser_id() {
		return user_id;
	}

	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}

	public String getDate_added() {
		return date_added;
	}

	public void setDate_added(String date_added) {
		this.date_added = date_added;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}