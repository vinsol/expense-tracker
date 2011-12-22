package com.vinsol.expensetracker.models;

public class Entry {
	
	private Long id;
	private String amount;
	private String description;
	private String favorite;
	private String type;
	private String timeInMillis;
	private String timeLocation;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public String getFavorite() {
		return favorite;
	}
	public void setFavorite(String favorite) {
		this.favorite = favorite;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	public String getTimeInMillis() {
		return timeInMillis;
	}
	public void setTimeInMillis(String timeInMillis) {
		this.timeInMillis = timeInMillis;
	}
	
	public String getTimeLocation() {
		return timeLocation;
	}
	public void setTimeLocation(String timeLocation) {
		this.timeLocation = timeLocation;
	}
	
}
