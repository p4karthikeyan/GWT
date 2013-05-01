package com.google.gwt.sample.stockwatcher.client;

// import section
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.google.gwt.core.client.EntryPoint;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.i18n.client.NumberFormat;

import com.google.gwt.user.client.Random;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class StockWatcher implements EntryPoint {
  
	private static final int REFRESH_INTERVAL = 2000; // 2 seconds
	
	//Private fields - UI components
	private VerticalPanel mainPanel = new VerticalPanel();
	private FlexTable stocksTable = new FlexTable();
	private HorizontalPanel addNewStockPanel = new HorizontalPanel();
	private TextBox addNewStockInputBox = new TextBox();
	private Button addNewStockButton = new Button( "Add to stock table" );
	private Label lastUpdatedLabel = new Label();
	
	//Data structure to hold the list of stocks
	private List<String> stocks = new ArrayList<String>();
	
	/**
	 * EntryPoint onModuleLoad - starting point
	 */
	public void onModuleLoad(){
		
		//Define the stock table headers
		stocksTable.setText( 0, 0, "StockSymbol" );
		stocksTable.setText( 0, 1, "Price" );
		stocksTable.setText( 0, 2, "Change" );
		stocksTable.setText( 0, 3, "Remove" );
		
		//Construct the add new stock panel
		addNewStockPanel.add( addNewStockInputBox );
		addNewStockPanel.add( addNewStockButton );
		
		//Construct the Main Panel
		mainPanel.add(stocksTable);
		mainPanel.add(addNewStockPanel);
		mainPanel.add(lastUpdatedLabel);
		
		//Add the main panel to UI page, div id is p4StockList
		RootPanel.get("p4StockList").add(mainPanel);
		
		//Set the cursor focus on stock text box, so user can start adding new stock on load
		addNewStockInputBox.setFocus(true);
		
		//Add the click and enter_key handelers for stock input text area.
		addInputHandlers();
		
		//Setup a timer to refresh the stock prices change list automatically
		Timer refreshStocksChangeTimer = new Timer(){

			@Override
			public void run() {
				refreshStockPrices();
			}
			
		};
		refreshStocksChangeTimer.scheduleRepeating( REFRESH_INTERVAL );
	}
	
	/**
	 * Add the click and enter_key handelers for stock input text area.
	 */
	private void addInputHandlers()
	{
		//Adding mouse event handler to add button
		addNewStockButton.addClickHandler( new ClickHandler(){

			@Override
			public void onClick(ClickEvent event) {
				addNewStock();
			}
			
		} );
		
		//Adding enter key handler to input text box
		addNewStockInputBox.addKeyPressHandler( new KeyPressHandler() {
			
			@Override
			public void onKeyPress(KeyPressEvent event) {
				if( event.getCharCode() == KeyCodes.KEY_ENTER )
				{
					addNewStock();
				}
			}
		} );
	}
	
	/**
	 * Add new stock to the stock table. 
	 */
	private void addNewStock()
	{
		final String inputText = addNewStockInputBox.getText().toUpperCase().trim();
		addNewStockInputBox.setFocus(true);
		
		// Stock code must be between 1 and 10 chars that are numbers, letters, or dots.
		if (!inputText.matches("^[0-9A-Z\\.]{1,10}$")) {
			Window.alert("'" + inputText + "' is not a valid Stock Name.");
			addNewStockInputBox.selectAll();
			return;
		}
		
		// Do not add the stock if it is already available in the stocks table
		if( stocks.contains( inputText ) )
		{
			Window.alert( "Stock" + "'" + inputText + "'" + " is already available in the stocks table" );
			addNewStockInputBox.selectAll();
			return;
		}
		// Add the stock to the table
		else
		{
			//Get the current number of rows from the stock table
			int row = stocksTable.getRowCount();
			stocks.add( inputText );
			stocksTable.setText( row, 0, inputText );
			
			//Add the remove button to remove this stock from the stock table
			Button removeStockButton = new Button( "x" );
			removeStockButton.addClickHandler( new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					int index = stocks.indexOf( inputText );
					stocks.remove( index );
					stocksTable.removeRow( index + 1 );
				}
				
			} );
			stocksTable.setWidget( row, 3, removeStockButton );
			
			//Get the stock price as soon as the stock is added
			refreshStockPrices();
		}
		
		addNewStockInputBox.setText("");
	}
	
	/**
	 * Refresh the stock price change list ( changes are random )
	 */
	private void refreshStockPrices()
	{
		final double MAX_PRICE = 105.0;
		final double MAX_PRICE_CHANGE = 0.08; // +/- 8%
		
		StockPrice[] updatedPrices = new StockPrice[stocks.size()];
		for( int i=0; i < stocks.size(); i++ ) {
			double price = Random.nextDouble() * MAX_PRICE;
			double change = price * MAX_PRICE_CHANGE * ( Random.nextDouble() * 2.0 - 1.0 );
			updatedPrices[i] = new StockPrice( stocks.get( i ), price, change );
		}
		
		updateStocksTablePrices( updatedPrices );
	}
	
	/**
	 * Update the stocks table prices with the input updated prices list
	 * for all the stocks in the stocksTable.
	 * 
	 * @param updatedPrices - updated prices for all stocks in the stocksTable
	 */
	private void updateStocksTablePrices( StockPrice[] updatedPrices )
	{
		for( StockPrice price : updatedPrices )
		{
			updateStocksTablePrice( price );
		}
		
		// Display timestamp showing last refresh.
		lastUpdatedLabel.setText("Last update : " + DateTimeFormat.getMediumDateTimeFormat().format(new Date()) );
	}
	
	/**
	 * Update the stocks table price ( single price ) with the updated price
	 * 
	 * @param updatedPrice - updated price for a specific stock in the stocksTable
	 */
	private void updateStocksTablePrice( StockPrice updatedPrice )
	{
		//First check if the stock is still in the stocks table
		if( !stocks.contains( updatedPrice.getName() ) )
		{
			return;
		}
		
		int row = stocks.indexOf( updatedPrice.getName() ) + 1;
		
		//Format the data in the price and change fields
		String priceText = NumberFormat.getFormat("#,##0.00").format( updatedPrice.getPrice() );
		NumberFormat changeFormat = NumberFormat.getFormat("+#,##0.00;-#,##0.00");
		String changeText = changeFormat.format(updatedPrice.getChange());
		String changePercentText = changeFormat.format(updatedPrice.getChangePercent());
		
		// Populate the Price and Change fields with new data.
		stocksTable.setText( row, 1, priceText );
		stocksTable.setText( row, 2, changeText + " (" + changePercentText + "%)" );
	}
}
