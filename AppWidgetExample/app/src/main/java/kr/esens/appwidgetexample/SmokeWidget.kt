package kr.esens.appwidgetexample

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in [SmokeWidgetConfigureActivity]
 */
class SmokeWidget : AppWidgetProvider() {

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

    override fun onDeleted(context: Context, appWidgetIds: IntArray) {
        // When the user deletes the widget, delete the preference associated with it.
        for (appWidgetId in appWidgetIds) {
            deleteTitlePref(context, appWidgetId)
        }
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
    }
}

private fun getPendingIntent(context: Context, value: Int): PendingIntent {

    val intent = Intent(context, SmokeWidget::class.java)
    intent.action = Constants.SMOKE_ACTION_ID
    intent.putExtra(Constants.EXTRA_ID, value)
    return PendingIntent.getBroadcast(context, value, intent, 0)
}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
    val widgetText = loadTitlePref(context, appWidgetId)
    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.smoke_widget)
    views.setTextViewText(R.id.appwidget_text, widgetText)

//    views.setOnClickPendingIntent(R.id.btn_clicks,
//        getPendingIntent(context, loadValuePref(context)))

    appWidgetManager.updateAppWidget(appWidgetId, views)
}
