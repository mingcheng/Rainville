package com.gracecode.android.rain.helper;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import com.gracecode.android.rain.R;

public class DonateHelper {
    public static void gotoPaypalPage(Context context) {
        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getPaypalUrl(context))));
    }

    public static String getPaypalUrl(Context context) {
        return String.format(
                context.getString(R.string.paypal_url),
                Uri.encode(context.getString(R.string.paypal_account)),
                Uri.encode(context.getString(R.string.paypal_item_name)),
                Uri.encode(context.getString(R.string.paypal_amount))
        );
    }
}
