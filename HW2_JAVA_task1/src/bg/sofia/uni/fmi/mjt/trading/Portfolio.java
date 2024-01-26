package bg.sofia.uni.fmi.mjt.trading;
import bg.sofia.uni.fmi.mjt.trading.price.PriceChartAPI;
import bg.sofia.uni.fmi.mjt.trading.stock.AmazonStockPurchase;
import bg.sofia.uni.fmi.mjt.trading.stock.GoogleStockPurchase;
import bg.sofia.uni.fmi.mjt.trading.stock.MicrosoftStockPurchase;
import bg.sofia.uni.fmi.mjt.trading.stock.StockPurchase;

import java.math.BigDecimal;
import java.math.RoundingMode;
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
        this.stockPurchases = new StockPurchase[maxSize];
        System.arraycopy(stockPurchases, 0, this.stockPurchases, 0, stockPurchases.length);
        this.budget = budget;
        this.maxSize = maxSize;
    }
    public int getStockPurchasesCount() {
        int count = 0;
        if (stockPurchases != null) {
            for (StockPurchase purchase : stockPurchases) {
                if (purchase != null) {
                    count++;
                }
            }
        }
        return count;
    }

    private void addStock(StockPurchase purchase) {
        if (stockPurchases == null) {
            stockPurchases = new StockPurchase[maxSize];
        }
        int stockCount = getStockPurchasesCount();
        if (stockCount < maxSize) {
            stockPurchases[stockCount] = purchase;
        }
    }
    @Override
    public StockPurchase buyStock(String stockTicker, int quantity) {
        if (stockTicker == null || quantity <= 0 || budget < priceChart.getCurrentPrice(stockTicker)*quantity){
            return null;
        }
        if(getStockPurchasesCount()>=maxSize){
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
            budget -= priceChart.getCurrentPrice(stockTicker)*quantity;
            BigDecimal roundedBudget = BigDecimal.valueOf(budget).setScale(2, RoundingMode.HALF_UP);
            budget = roundedBudget.doubleValue();
            priceChart.changeStockPrice(stockTicker,5);
            addStock(purchase);
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
        int idx = 0;
        for (StockPurchase stockPurchase : stockPurchases) {
            if (stockPurchase != null) {
                LocalDateTime purchaseTimestamp = stockPurchase.getPurchaseTimestamp();
                if (purchaseTimestamp != null && purchaseTimestamp.isAfter(startTimestamp) && purchaseTimestamp.isBefore(endTimestamp)) {
                    arr[idx] = stockPurchase;
                    idx++;
                }
            }
        }
        StockPurchase[] newArr = new StockPurchase[idx];
        System.arraycopy(arr, 0, newArr, 0, idx);
        return newArr;
    }
    @Override
    public double getNetWorth() {
        double netWorth = 0.0;
        if (stockPurchases != null) {
            for (StockPurchase stockPurchase : stockPurchases) {
                if (stockPurchase != null) {
                    netWorth += stockPurchase.getQuantity() * priceChart.getCurrentPrice(stockPurchase.getStockTicker());
                }
            }
        }
        BigDecimal rounded = BigDecimal.valueOf(netWorth).setScale(2, RoundingMode.HALF_UP);
        return rounded.doubleValue();
    }

    @Override
    public double getRemainingBudget() {
        BigDecimal roundedBudget = BigDecimal.valueOf(budget).setScale(2, RoundingMode.HALF_UP);
        budget = roundedBudget.doubleValue();
        return budget;
    }

    @Override
    public String getOwner() {
        return owner;
    }
}
