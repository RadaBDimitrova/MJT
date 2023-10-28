package bg.sofia.uni.fmi.mjt.trading;
import bg.sofia.uni.fmi.mjt.trading.price.PriceChartAPI;
import bg.sofia.uni.fmi.mjt.trading.stock.AmazonStockPurchase;
import bg.sofia.uni.fmi.mjt.trading.stock.GoogleStockPurchase;
import bg.sofia.uni.fmi.mjt.trading.stock.MicrosoftStockPurchase;
import bg.sofia.uni.fmi.mjt.trading.stock.StockPurchase;

import java.time.LocalDateTime;

public class Portfolio implements PortfolioAPI{
    String owner;
    PriceChartAPI priceChart;
    StockPurchase[] stockPurchases;
    double budget;
    int maxSize;
    Portfolio(String owner, PriceChartAPI priceChart, double budget, int maxSize){
    this.owner = owner;
    this.priceChart = priceChart;
    this.budget = budget;
    this.maxSize = maxSize;
    }
    Portfolio(String owner, PriceChartAPI priceChart, StockPurchase[] stockPurchases, double budget, int maxSize){
        this.owner = owner;
        this.priceChart = priceChart;
        this.stockPurchases = stockPurchases;
        System.arraycopy(stockPurchases, 0, this.stockPurchases, 0, stockPurchases.length);
        this.budget = budget;
        this.maxSize = maxSize;
    }

    @Override
    public StockPurchase buyStock(String stockTicker, int quantity) {
        if (stockTicker == null || quantity <= 0 || budget < priceChart.getCurrentPrice(stockTicker)*quantity){
            return null;
        }
        StockPurchase purchase = switch (stockTicker) {
            case "AMZ" ->
                    new AmazonStockPurchase(quantity, LocalDateTime.now(), priceChart.getCurrentPrice(stockTicker));
            case "MSFT" ->
                    new MicrosoftStockPurchase(quantity, LocalDateTime.now(), priceChart.getCurrentPrice(stockTicker));
            case "GOOG" ->
                    new GoogleStockPurchase(quantity, LocalDateTime.now(), priceChart.getCurrentPrice(stockTicker));
            default -> null;
        };
        if (purchase != null) {
            priceChart.changeStockPrice(stockTicker,5);
            //stockPurchases.addStock
        }
        return purchase;
    }

    @Override
    public StockPurchase[] getAllPurchases() {
        return stockPurchases;
    }

    @Override
    public StockPurchase[] getAllPurchases(LocalDateTime startTimestamp, LocalDateTime endTimestamp) {
    StockPurchase[] arr = new StockPurchase[stockPurchases.length];
        for(int i = 0; i < stockPurchases.length; i++){
        if(stockPurchases[i].getPurchaseTimestamp().isAfter(startTimestamp) &&
           stockPurchases[i].getPurchaseTimestamp().isBefore(endTimestamp))
            arr[i] = stockPurchases[i];
        }
        return arr;
    }
    @Override
    public double getNetWorth() {
        double netWorth = 0.0;
        for (StockPurchase stockPurchase : stockPurchases) {
            netWorth += stockPurchase.getQuantity() * priceChart.getCurrentPrice(stockPurchase.getStockTicker());
        }
        return netWorth;
    }

    @Override
    public double getRemainingBudget() {
        return budget;
    }

    @Override
    public String getOwner() {
        return owner;
    }
}
