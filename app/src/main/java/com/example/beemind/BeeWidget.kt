package com.example.beemind

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import com.example.beemind.R
import com.example.beemind.databaseData.CureData
import com.example.beemind.databaseData.InspectionData

/**
 * Implementation of App Widget functionality.
 */
class BeeWidget : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

/**
 * On update gets days since last Cure and Inspection data
 *
 * @param context of the application
 * @param appWidgetManager
 * @param appWidgetId
 */
internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val views = RemoteViews(context.packageName, R.layout.bee_cure_widget)

    val durationC = CureData.getDurationSinceLast(context)
    if (durationC >= 0) {
        views.setTextViewText(R.id.lastCureText,"Cure $durationC days ago")
    } else {
        views.setTextViewText(R.id.lastCureText,"No Cures")
    }

    val durationI = InspectionData.getDurationSinceLast(context)
    if (durationI >= 0) {
        views.setTextViewText(R.id.lastInspectionText,"Inspection $durationI days ago")
    } else {
        views.setTextViewText(R.id.lastInspectionText,"No Inspections")
    }
    appWidgetManager.updateAppWidget(appWidgetId, views)
}