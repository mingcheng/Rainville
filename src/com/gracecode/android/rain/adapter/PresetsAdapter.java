package com.gracecode.android.rain.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.gracecode.android.rain.R;
import com.gracecode.android.rain.helper.TypefaceHelper;


public class PresetsAdapter extends BaseAdapter {
    private static Context mContext;
    private final String[] mPresets;

    public PresetsAdapter(Context context, String[] presets) {
        mContext = context;
        mPresets = presets;
    }

    @Override
    public int getCount() {
        return mPresets.length;
    }

    @Override
    public String getItem(int i) {
        return mPresets[i];
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            view = inflater.inflate(R.layout.item_preset, null);
        }

        Holder h = Holder.get(view);
        h.title.setText(mPresets[i]);
        return view;
    }


    private static final class Holder {
        public final TextView title;

        private Holder(View v) {
            title = (TextView) v.findViewById(android.R.id.text1);
            title.setTypeface(TypefaceHelper.getTypefaceMusket2(mContext));
            v.setTag(this);
        }

        public static Holder get(View v) {
            if (v.getTag() instanceof Holder) {
                return (Holder) v.getTag();
            }

            return new Holder(v);
        }
    }
}
