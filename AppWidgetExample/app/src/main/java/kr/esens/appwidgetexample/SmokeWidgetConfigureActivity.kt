package kr.esens.appwidgetexample

import android.app.Activity
import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import kr.esens.appwidgetexample.databinding.SmokeWidgetConfigureBinding

/**
 * The configuration screen for the [SmokeWidget] AppWidget.
 */
class SmokeWidgetConfigureActivity : Activity() {
    private var appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID
    private lateinit var appWidgetText: EditText
    private var onclicklistener = view.onclicklistener {
        val context = this@smokewidgetconfigureactivity

        val widgettext = appwidgettext.text.tostring()
        savetitlepref(context, appwidgetid, widgettext)

        val appwidgetmanager = appwidgetmanager.getinstance(context)
        updateappwidget(context, appwidgetmanager, appwidgetid)

        val resultvalue = intent()
        resultvalue.putextra(appwidgetmanager.extra_appwidget_id, appwidgetid)
        setresult(result_ok, resultvalue)
        finish()
    }
    private lateinit var binding: SmokeWidgetConfigureBinding

    public override fun onCreate(icicle: Bundle?) {
        super.onCreate(icicle)

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED)

        binding = SmokeWidgetConfigureBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appWidgetText = binding.appwidgetText as EditText
        binding.addButton.setOnClickListener(onClickListener)

        // Find the widget id from the intent.
        val intent = intent
        val extras = intent.extras
        if (extras != null) {
            appWidgetId = extras.getInt(
                AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID
            )
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish()
            return
        }

        appWidgetText.setText(loadTitlePref(this@SmokeWidgetConfigureActivity, appWidgetId))
    }

}

private const val PREFS_NAME = "kr.esens.appwidgetexample.SmokeWidget"
private const val PREF_PREFIX_KEY = "appwidget_"
private val TAG = SmokeWidgetConfigureActivity::class.simpleName


// Write the prefix to the SharedPreferences object for this widget
internal fun saveTitlePref(context: Context, appWidgetId: Int, text: String) {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
    prefs.putString(PREF_PREFIX_KEY + appWidgetId, text)
    prefs.apply()
}

// Read the prefix from the SharedPreferences object for this widget.
// If there is no preference saved, get the default from a resource
internal fun loadTitlePref(context: Context, appWidgetId: Int): String {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0)
    val titleValue = prefs.getString(PREF_PREFIX_KEY + appWidgetId, null)
    return titleValue ?: context.getString(R.string.appwidget_text)
}

internal fun loadValuePref(context: Context): Int {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0)
    val intValue = prefs.getInt(PREF_PREFIX_KEY + "SMOKE_VALUE", 0)
    return intValue ?: 0
}


internal fun saveValuePref(context: Context, nValue: Int) {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
    prefs.putInt(PREF_PREFIX_KEY + "SMOKE_VALUE", nValue)
    prefs.apply()
}

internal fun deleteTitlePref(context: Context, appWidgetId: Int) {
    val prefs = context.getSharedPreferences(PREFS_NAME, 0).edit()
    prefs.remove(PREF_PREFIX_KEY + appWidgetId)
    prefs.apply()
}