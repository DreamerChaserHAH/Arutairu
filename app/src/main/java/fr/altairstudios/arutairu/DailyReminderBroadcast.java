package fr.altairstudios.arutairu;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import static android.content.Context.MODE_PRIVATE;

public class DailyReminderBroadcast extends BroadcastReceiver {
    public static final String ARUTAIRU_SHARED_PREFS = "ArutairuSharedPrefs";

    @Override
    public void onReceive(Context context, Intent intent) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(ARUTAIRU_SHARED_PREFS, MODE_PRIVATE);

        if (sharedPreferences.getBoolean("NOTIFS", false) && sharedPreferences.getBoolean("MESSAGE1", true)) {
            Log.d("NOTIFS", "WE'RE IN !");

            NotificationManagerCompat nManager = NotificationManagerCompat.from(context);

            Intent dailySurprise = new Intent(context, MainActivity.class);

            PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, dailySurprise, PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = new NotificationCompat.Builder(context, "Daily Reminder")
                    .setSmallIcon(R.drawable.ic_stat_name)
                    .setContentText(context.getString(R.string.message))
                    .setContentTitle(context.getString(R.string.messagecontent))
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setCategory(NotificationCompat.CATEGORY_EVENT)
                    .setContentIntent(pendingIntent)
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText(context.getString(R.string.message)))
                    .setAutoCancel(true)
                    .build();

            nManager.notify(100,notification);
            sharedPreferences.edit().putBoolean("MESSAGE1",false).apply();
        }
    }
}
