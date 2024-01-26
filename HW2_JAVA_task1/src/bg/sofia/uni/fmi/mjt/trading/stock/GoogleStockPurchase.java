package bg.sofia.uni.fmi.mjt.trading.stock;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

public class GoogleStockPurchase implements StockPurchase{
    int quantity;
    LocalDateTime purchaseTimestamp;
    double purchasePricePerUnit;
    static final String ticker = "GOOG";

    public GoogleStockPurchase(int quantity, LocalDateTime purchaseTimestamp, double purchasePricePerUnit){
        this.quantity = quantity;
        this.purchaseTimestamp = purchaseTimestamp;
        BigDecimal rounded = BigDecimal.valueOf(purchasePricePerUnit).setScale(2, RoundingMode.HALF_UP);
        this.purchasePricePerUnit = rounded.doubleValue();
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
        BigDecimal rounded = BigDecimal.valueOf(purchasePricePerUnit).setScale(2, RoundingMode.HALF_UP);
        return rounded.doubleValue();
    }

    @Override
    public double getTotalPurchasePrice() {
        BigDecimal rounded = BigDecimal.valueOf(purchasePricePerUnit*quantity).setScale(2, RoundingMode.HALF_UP);
        return rounded.doubleValue();
    }

    @Override
    public String getStockTicker() {
        return ticker;
    }
}
