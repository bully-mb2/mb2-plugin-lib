package com.taivas.plugin;

import com.taivas.mb2_plugin_lib.schema.*;
import com.taivas.rcon.RconClient;
import com.taivas.settings.Settings;
import org.pf4j.ExtensionPoint;

public interface MBPlugin extends ExtensionPoint {

    String getPluginName();
    void onPluginActivate(RconClient rcon, Settings settings);
    void onPluginDeactivate();
    void onAdminSay(AdminSayEvent adminSayEvent);
    void onClientBegin(ClientBeginEvent clientBeginEvent);
    void onClientConnect(ClientConnectEvent clientConnectEvent);
    void onClientDisconnect(ClientDisconnectEvent clientDisconnectEvent);
    void onClientSpawned(ClientSpawnedEvent clientSpawnedEvent);
    void onClientUserinfoChanged(ClientUserinfoChangedEvent clientUserinfoChangedEvent);
    void onFragLimitHit(FragLimitHitEvent fragLimitHitEvent);
    void onInitGame(InitGameEvent initGameEvent);
    void onKill(KillEvent killEvent);
    void onSay(SayEvent sayEvent);
    void onSendingGameReport(SendingGameReportEvent sendingGameReportEvent);
    void onServerInitialization(ServerInitializationEvent serverInitializationEvent);
    void onShutdownGame(ShutdownGameEvent shutdownGameEvent);
    void onSmod(SmodEvent smodEvent);
}
