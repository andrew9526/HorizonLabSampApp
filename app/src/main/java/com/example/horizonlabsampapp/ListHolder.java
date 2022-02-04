package com.example.horizonlabsampapp;

public class ListHolder {
	String feed_id= null;
	String feed_header= null;
	String feed_details= null;
	String date_added= null;
	String status= null;
	String videolink= null;
	String videoimage= null;
	String poster= null;


	public ListHolder(String feed_id,
                      String feed_header,
                      String feed_details,
                      String date_added,
                      String status,
                      String videolink,
                      String videoimage,
                      String poster) {
		super();
		this.feed_id=feed_id;
		this.feed_header=feed_header;
		this.feed_details=feed_details;
		this.date_added=date_added;
		this.status=status;
		this.videolink=videolink;
		this.videoimage=videoimage;
		this.poster=poster;
	}

	public String getFeed_id() {
		return feed_id;
	}

	public void setFeed_id(String feed_id) {
		this.feed_id = feed_id;
	}

	public String getFeed_header() {
		return feed_header;
	}

	public void setFeed_header(String feed_header) {
		this.feed_header = feed_header;
	}

	public String getFeed_details() {
		return feed_details;
	}

	public void setFeed_details(String feed_details) {
		this.feed_details = feed_details;
	}

	public String getDate_added() {
		return date_added;
	}

	public void setDate_added(String date_added) {
		this.date_added = date_added;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getVideolink() {
		return videolink;
	}

	public void setVideolink(String videolink) {
		this.videolink = videolink;
	}

	public String getVideoimage() {
		return videoimage;
	}

	public void setVideoimage(String videoimage) {
		this.videoimage = videoimage;
	}

	public String getPoster() {
		return poster;
	}

	public void setPoster(String poster) {
		this.poster = poster;
	}
}