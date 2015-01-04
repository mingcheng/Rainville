package com.gracecode.android.rain.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckedTextView;
import com.gracecode.android.rain.R;
import com.gracecode.android.rain.helper.TypefaceHelper;


public class PresetsAdapter extends BaseAdapter {
    private static Context mContext;
    private final String[] mPresets;
    private String mCurrentPreset;

    public PresetsAdapter(Context context, String[] presets) {
        mContext = context;
        mPresets = presets;
    }

//    public int getPositionFromName(String name) {
//        for (int i = mPresets.length - 1; i >= 0; i--) {
//            if (name.equals(mPresets[i])) {
//                return i;
//            }
//        }
//
//        return -1;
//    }

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
        String preset = mPresets[i];
        h.title.setText(preset);

        if (view != null) {
            h.title.setChecked(preset.equals(mCurrentPreset));
        }
        return view;
    }


    /**
     * 设置当前的位置
     *
     * @param position
     */
    public void setCurrentPosition(int position) {
        mCurrentPreset = getItem(position);
    }


    /**
     * 设置当前的名字
     *
     * @param name
     */
    public void setCurrentPresetName(String name) {
        mCurrentPreset = name;
    }

    public String getCurrentPreset() {
        return mCurrentPreset;
    }

    private static final class Holder {
        public final CheckedTextView title;

        private Holder(View v) {
            title = (CheckedTextView) v.findViewById(android.R.id.text1);
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
