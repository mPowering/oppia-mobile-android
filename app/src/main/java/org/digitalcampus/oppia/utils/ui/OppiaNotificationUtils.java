package org.digitalcampus.oppia.utils.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;

import org.opendeliver.oppia.R;
import org.digitalcampus.oppia.activity.PrefsActivity;
import org.digitalcampus.oppia.application.MobileLearning;

import static android.content.Context.NOTIFICATION_SERVICE;

public class OppiaNotificationUtils {

    public static NotificationCompat.Builder getBaseBuilder(Context ctx, boolean setAutoCancel){
        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(ctx);
        notifBuilder.setSound(defaultSoundUri);
        notifBuilder.setAutoCancel(setAutoCancel);
        notifBuilder.setSmallIcon(R.drawable.ic_notification);

        //Notification styles changed since Lollipop
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int color;
            //We have to check the M version for the deprecation of the method getColor()
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                color = ctx.getResources().getColor(R.color.highlight_light);
            }
            else{
                color = ctx.getResources().getColor(R.color.highlight_light);
            }
            notifBuilder.setColor(color);
        }
        else{
            //in older versions, we show the App logo
            notifBuilder.setLargeIcon(BitmapFactory.decodeResource(ctx.getResources(), MobileLearning.APP_LOGO));
        }

        return notifBuilder;
    }

    public static void sendNotification(Context ctx, int id, Notification notification){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        boolean notificationsDisabled = prefs.getBoolean(PrefsActivity.PREF_DISABLE_NOTIFICATIONS, false);
        if(!notificationsDisabled) {
            NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.notify(id, notification);
        }
    }
}
