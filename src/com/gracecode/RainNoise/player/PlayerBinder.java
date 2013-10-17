package com.gracecode.RainNoise.player;


public interface PlayerBinder {
    public abstract void bindPlayerManager(final PlayerManager manager);

    public abstract void unbindPlayerManager();

    public abstract void refresh();
}
