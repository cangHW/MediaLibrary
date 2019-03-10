package com.chx.livemaker.manager.media.player;

import com.chx.livemaker.manager.media.player.iPlayer.IPlayer;
import com.chx.livemaker.util.LiveLogger;

/**
 * Created by cangHX
 * on 2019/01/15  18:00
 */
public class PlayerImpl implements IPlayer {

    private static final LiveLogger mLogger = LiveLogger.create(PlayerImpl.class);
    private PlayerParams mPlayerParams = new PlayerParams();

    private PlayerImpl() {
    }

    public static IPlayer create() {
        return new PlayerImpl();
    }

    @Override
    public void onLifecycleStop() {

    }

    @Override
    public void onLifecycleResume() {

    }

    @Override
    public void onLifecycleDestroy() {

    }
}
