package bg.sofia.uni.fmi.mjt.trading.stock;

import java.time.LocalDateTime;

public class GoogleStockPurchase implements StockPurchase{
    int quantity;
    LocalDateTime purchaseTimestamp;
    double purchasePricePerUnit;
    static final String ticker = "GOOG";

    public GoogleStockPurchase(int quantity, LocalDateTime purchaseTimestamp, double purchasePricePerUnit){
        this.quantity = quantity;
        this.purchaseTimestamp = purchaseTimestamp;
        this.purchasePricePerUnit = (double) Math.round(purchasePricePerUnit * 100) / 100;
    }
    @Override
    public int getQuantity() {
        return quantity;
    }

    @Override
    public LocalDateTime getPurchaseTimestamp() {
        return purchaseTimestamp;
    }

    @Override
    public double getPurchasePricePerUnit() {
        return (double) Math.round(purchasePricePerUnit * 100) / 100;
    }

    @Override
    public double getTotalPurchasePrice() {
        return (double) Math.round(quantity*purchasePricePerUnit * 100) / 100;
    }

    @Override
    public String getStockTicker() {
        return ticker;
    }
}
