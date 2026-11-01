package com.databit.stockia.platform.notificationsmessaging.domain.model.valueobjects;

import java.math.BigDecimal;

/**
 * Stock data the alert policy needs (taken from Stock Management). Status: AVAILABLE, LOW, CRITICAL or EXPIRED.
 */
public record StockSnapshot(Long inventoryItemId, String name, String unit, BigDecimal quantity, BigDecimal minThreshold, long daysToExpire,
                            String status) {
}
