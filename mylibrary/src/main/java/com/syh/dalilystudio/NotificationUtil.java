package com.syh.dalilystudio;

import java.io.Serializable;

/**
 * Created by shenyh on 2016-08-06.
 */
public class NotificationUtil {

    public static void sendNotification(int id, String title, String message, Serializable serializable) {
//        if (serializable instanceof MessageInfo) {
//            String targetNum = ((MessageInfo) serializable).target_number;
//            SendMessageActivity sendMessageActivity = SendMessageActivity.getInstance();
//            if (sendMessageActivity != null && targetNum != null && targetNum.equals(sendMessageActivity.getTargetNumber())) {
//                return;//already in this activity
//            }
//        }
//        Context context = GlobalAppData.getContext();
//        Notification.Builder builder = new Notification.Builder(context);
//        if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
//            builder.setSmallIcon(R.drawable.ic_notify_chat);
//        } else {
//            builder.setSmallIcon(R.drawable.ic_launcher_small);
//        }
//
//        builder.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher_small));
//        builder.setContentTitle(title);
//        builder.setContentText(message);
//
//        Intent intent = new Intent(context, MainActivity.class);
//        intent.setAction(MainActivity.ACTION_NOTIFICATION);
//        intent.putExtra(MainActivity.EXTRA_DATA, serializable);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//
//        Notification notification = builder.build();
//        notification.flags |= Notification.FLAG_AUTO_CANCEL;
//        notification.contentIntent = pendingIntent;
//        notification.defaults = Notification.DEFAULT_SOUND;
//
//        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(id, notification);
    }
}
