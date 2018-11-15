package com.example.hanmi.lifemanager.Receiver;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;
import static java.lang.Math.toIntExact;
import com.example.hanmi.lifemanager.Activity.Noti_dialog;
import com.example.hanmi.lifemanager.Activity.Noti_dialog_finish;
import com.example.hanmi.lifemanager.R;


public class scheduleReceiver extends BroadcastReceiver {
    private static final int DIALOG_MESSAGE = 1;
    Context mContext;

    private NotificationManager notificationManager;


    @RequiresApi(api = Build.VERSION_CODES.O)
    public void onReceive(Context context, Intent intent) {
        mContext = context;
        String title = intent.getStringExtra("getTitle");

        long id = intent.getLongExtra("getId",-1);
        int intID = toIntExact(id);
        if (id == -1)
            Toast.makeText(context, "스케줄에서 id를 잘못받음", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, "스케줄에서 잘받음"+id, Toast.LENGTH_SHORT).show();


        notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        // Android API26(Oreo) 이상은 알림채널이 필요하다.
        NotificationChannel channel = new NotificationChannel("default",
                "Channel name",
                NotificationManager.IMPORTANCE_DEFAULT);
        channel.setDescription("Channel description");
        notificationManager.createNotificationChannel(channel);
        //여기까지 채널에대한 내용구성

        Intent intent1 = new Intent(mContext, Noti_dialog.class);
        Intent intent2 = new Intent(mContext, Noti_dialog_finish.class);
        intent1.putExtra("id",id);
        intent2.putExtra("id",id);


        // pendingIntent함수는 알림을 클릭하면 메인액티비티로 가도록 한다.
        PendingIntent sdContinue = PendingIntent.getActivity(mContext, intID, intent1, PendingIntent.FLAG_UPDATE_CURRENT);

        PendingIntent sdFinish = PendingIntent.getActivity(mContext, intID, intent2, PendingIntent.FLAG_UPDATE_CURRENT);

        // 알림내용 작성하기
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "default")
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher)) //알림의 큰아이콘. 앱아이콘 넣으면 좋을듯
                .setSmallIcon(R.drawable.ic_launcher)  //알림의 작은아이콘
                .setTicker("상태바 한줄메세지") // 근데 이건 5.0이상 버전에서는 안뜬다.
                .setContentTitle("'" + title +" "+id+ "'"+"에 대한 일정종료"+intID)  //알림제목
                .setContentText("일정을 종료하실건가요?") //알림내용
                .setSubText("Sub Text") //알림의 서브내용
                .addAction(R.drawable.ic_launcher,"일정 지속하기", sdContinue)
                .addAction(R.drawable.ic_launcher,"알람종료",sdFinish)
                .setContentIntent(null) //알람 자체 클릭하면 아무반응없게한다
                .setDefaults(NotificationCompat.DEFAULT_VIBRATE) // 알림이 생기면 진동으로 알려준다.
                .setPriority(NotificationCompat.PRIORITY_DEFAULT) //알림의 중요도(?)
                .setAutoCancel(true); //알림을 터치하면 알림이 사라지도록 한다.

        // 알림바에 알람을 표시하기 위한 코드
        notificationManager.notify(intID, builder.build());
    }
}