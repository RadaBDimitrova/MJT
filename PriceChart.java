package bg.sofia.uni.fmi.mjt.trading.price;

public class PriceChart implements PriceChartAPI{
    private double amazonStockPrice = 0.0;
    private double googleStockPrice = 0.0;
    private double microsoftStockPrice = 0.0;
    public PriceChart(double microsoftStockPrice, double googleStockPrice, double amazonStockPrice){
        this.amazonStockPrice = (double) Math.round(amazonStockPrice * 100) / 100;
        this.googleStockPrice = (double) Math.round(googleStockPrice * 100) / 100;
        this.microsoftStockPrice = (double) Math.round(microsoftStockPrice * 100) / 100;
    }
    @Override
    public double getCurrentPrice(String stockTicker) {
        if (stockTicker != null) {
            return switch (stockTicker) {
                case "AMZ" -> amazonStockPrice;
                case "GOOG" -> googleStockPrice;
                case "MSFT" -> microsoftStockPrice;
                default -> 0.0;
            };
        }
        return 0.0;
    }

    @Override
    public boolean changeStockPrice(String stockTicker, int percentChange) {
        if (stockTicker!=null && percentChange > 0){
            return switch (stockTicker) {
                case "AMZ" -> {
                    amazonStockPrice += amazonStockPrice * (double)percentChange/100;
                    amazonStockPrice = (double) Math.round(amazonStockPrice * 100) / 100;
                    yield true;
                }
                case "GOOG" -> {
                    googleStockPrice += googleStockPrice * (double)percentChange/100;
                    googleStockPrice = (double) Math.round(googleStockPrice * 100) / 100;
                    yield true;
                }
                case "MSFT" -> {
                    microsoftStockPrice += microsoftStockPrice * (double)percentChange/100;
                    microsoftStockPrice = (double) Math.round(microsoftStockPrice * 100) / 100;
                    yield true;
                }
                default -> false;
            };
        }
        return false;
    }
}
