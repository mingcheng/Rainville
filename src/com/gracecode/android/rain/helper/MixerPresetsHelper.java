package com.gracecode.android.rain.helper;


public abstract interface MixerPresetsHelper {
    float[] FAIRY_RAIN = new float[]{.0f, .0f, .0f, .0f, .1f, .2f, .3f, .4f, .5f, .6f};
    float[] BEDROOM = new float[]{.0f, .0f, .0f, .0f, .1f, .2f, .3f, .2f, .1f, .0f};
    float[] UNDER_THE_PORCH = new float[]{.0f, .3f, .2f, .25f, .3f, .25f, .2f, .15f, .1f, .0f};
    float[] DISTANT_STORM = new float[]{.7f, .6f, .5f, .4f, .3f, .2f, .2f, .3f, .2f, .1f};
    float[] GETTING_WET = new float[]{.7f, .5f, .2f, .35f, .55f, .35f, .3f, .25f, .2f, .2f};
    float[] ONLY_RUMBLE = new float[]{.7f, .5f, .15f, .15f, .15f, .15f, .1f, .1f, .0f, .0f};
    float[] UNDER_THE_LEAVES = new float[]{.3f, .3f, .0f, .0f, .0f, .3f, .0f, .0f, .1f, .2f};
    float[] DARK_RAIN = new float[]{.0f, .5f, .4f, .3f, .2f, .0f, .0f, .1f, .1f, .0f};
    float[] JUNGLE_LODGE = new float[]{.7f, .0f, .2f, .0f, .25f, .0f, .25f, .0f, .2f, .0f};

    float[] BROWN_NOISE = new float[]{.65f, .6f, .55f, 5f, .45f, .4f, .35f, .3f, .25f, .2f};
    float[] PINK_NOISE = new float[]{.3f, .3f, .3f, .3f, .3f, .3f, .3f, .3f, .3f, .3f};
    float[] WHITE_NOISE = new float[]{.2f, .25f, .3f, .35f, .4f, .45f, .5f, .55f, .6f, .65f};
    float[] GREY_NOISE = new float[]{.5f, .45f, .4f, .3f, .3f, .3f, .25f, .25f, .3f, .35f};

    float[] _60_HZ = new float[]{.4f, .5f, .4f, .0f, .0f, .0f, .0f, .0f, .0f, .0f};
    float[] _125_HZ = new float[]{.0f, .4f, .5f, .4f, .0f, .0f, .0f, .0f, .0f, .0f};
    float[] _250_HZ = new float[]{.0f, .0f, .4f, .5f, .4f, .0f, .0f, .0f, .0f, .0f};
    float[] _500_HZ = new float[]{.0f, .0f, .0f, .4f, .5f, .4f, .0f, .0f, .0f, .0f};
    float[] _1K_HZ = new float[]{.0f, .0f, .0f, .0f, .4f, .5f, .4f, .0f, .0f, .0f};
    float[] _2K_HZ = new float[]{.0f, .0f, .0f, .0f, .0f, .4f, .5f, .4f, .0f, .0f};
    float[] _4K_HZ = new float[]{.0f, .0f, .0f, .0f, .0f, .0f, .4f, .5f, .4f, .0f};
    float[] _8K_HZ = new float[]{.0f, .0f, .0f, .0f, .0f, .0f, .0f, .4f, .5f, .4f};

    float[] DEFAULT_PRESET = PINK_NOISE;

    float[][] ALL_PRESETS = new float[][]{
            DEFAULT_PRESET, FAIRY_RAIN, BEDROOM, UNDER_THE_PORCH, DISTANT_STORM,
            GETTING_WET, ONLY_RUMBLE, UNDER_THE_LEAVES, DARK_RAIN, JUNGLE_LODGE,
            BROWN_NOISE, PINK_NOISE, WHITE_NOISE, GREY_NOISE,
            _60_HZ, _125_HZ, _250_HZ, _500_HZ, _1K_HZ, _2K_HZ, _4K_HZ, _8K_HZ
    };

    String[] PRESET_TITLES = new String[]{
            "Default", "Fairy Rain", "Bedroom", "Under The Porch", "Distant Storm",
            "Getting Wet", "Only Rumble", "Under The Leaves", "Dark Rain", "Jungle Lodge",
            "Brown Noise", "Pink Noise", "White Noise", "Grey Noise",
            "60 Hz", "125 Hz", "250 Hz", "500 Hz", "1000 Hz", "2000 Hz", "4000 Hz", "8000 Hz"
    };
}
