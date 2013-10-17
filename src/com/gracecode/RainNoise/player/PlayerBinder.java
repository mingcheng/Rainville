package com.gracecode.RainNoise.player;


public interface PlayerBinder {
    public abstract void bindPlayerManager(final PlayManager manager);

    public abstract void unbindPlayerManager();

    public abstract void refresh();
}
