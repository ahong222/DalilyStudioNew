package com.syh.dalilystudio;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

public class ShareUtil {

    public static void share(Context context, String message, String imgPath) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        if (TextUtils.isEmpty(imgPath)) {
            intent.setType("text/plain"); // 纯文本
        } else {

            File f = new File(imgPath);
            if (f != null && f.exists() && f.isFile()) {
                intent.setType("image/png");
                Uri u = Uri.fromFile(f);
                intent.putExtra(Intent.EXTRA_STREAM, u);
            }

        }
        intent.putExtra(Intent.EXTRA_SUBJECT, "msgTitle");
        intent.putExtra(Intent.EXTRA_TEXT, message);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(Intent.createChooser(intent, "分享到"));
    }

}
