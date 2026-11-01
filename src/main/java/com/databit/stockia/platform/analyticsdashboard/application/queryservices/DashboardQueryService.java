package com.databit.stockia.platform.analyticsdashboard.application.queryservices;

import com.databit.stockia.platform.analyticsdashboard.application.queries.GetDashboardSummaryQuery;
import com.databit.stockia.platform.analyticsdashboard.application.queries.GetWeeklySavingsQuery;
import com.databit.stockia.platform.analyticsdashboard.domain.model.valueobjects.DashboardSummary;
import com.databit.stockia.platform.analyticsdashboard.domain.model.valueobjects.WeeklySavings;

/**
 * Analytics &amp; Dashboard has no aggregates of its own: it composes the other bounded contexts through
 * their ACL facades (Metrics View Updater, Alerts Panel, Demand Prediction View and Weekly Savings Calculator).
 */
public interface DashboardQueryService {
    DashboardSummary handle(GetDashboardSummaryQuery query);

    WeeklySavings handle(GetWeeklySavingsQuery query);
}
