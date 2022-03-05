package org.pmv.notification;

public record NotificationRequest(
        Integer toCustomerId,
        String toCustomerName,
        String message
) {}
