package com.databit.stockia.platform.notificationsmessaging.application.commands;

/**
 * New delivery attempt on the first required channel without a successful one.
 */
public record RetryAlertDeliveryCommand(Long alertId, Long restaurantId) {
}
