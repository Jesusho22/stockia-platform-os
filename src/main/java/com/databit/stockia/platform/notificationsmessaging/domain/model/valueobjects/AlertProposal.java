package com.databit.stockia.platform.notificationsmessaging.domain.model.valueobjects;

import java.util.List;

/**
 * An automatic alert the policy proposes; its text is a message key with its arguments.
 */
public record AlertProposal(AlertType type, AlertSeverity severity, AlertChannel channel, String messageKey, List<String> messageArgs) {
}
