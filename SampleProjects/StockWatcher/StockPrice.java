package com.google.gwt.sample.stockwatcher.client;

public class StockPrice {

  private String name;
	private double price;
	private double change;
	
	public StockPrice() {
		
	}
	
	public StockPrice( String name, double price, double change ) {
		this.name = name;
		this.setPrice(price);
		this.setChange(change);
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public double getChange() {
		return change;
	}

	public void setChange(double change) {
		this.change = change;
	}
	
	public double getChangePercent() {
		return 100.0 * this.change / this.price;
	}
}
