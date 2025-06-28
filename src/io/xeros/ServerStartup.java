package io.xeros;

import io.xeros.annotate.Init;
import io.xeros.annotate.PostInit;
import io.xeros.content.WeaponGames.WGManager;
import io.xeros.content.battlepass.Rewards;
import io.xeros.content.boosts.Boosts;
import io.xeros.content.bosses.godwars.GodwarsEquipment;
import io.xeros.content.bosses.godwars.GodwarsNPCs;
import io.xeros.content.bosses.nightmare.NightmareStatusNPC;
import io.xeros.content.bosses.sarachnis.SarachnisNpc;
import io.xeros.content.collection_log.CollectionLog;
import io.xeros.content.combat.stats.TrackedMonster;
import io.xeros.content.commands.CommandManager;
import io.xeros.content.dailyrewards.DailyRewardContainer;
import io.xeros.content.dailyrewards.DailyRewardsRecords;
import io.xeros.content.donationrewards.DonationReward;
import io.xeros.content.event.eventcalendar.EventCalendar;
import io.xeros.content.event.eventcalendar.EventCalendarWinnerSelect;
import io.xeros.content.events.monsterhunt.MonsterHunt;
import io.xeros.content.fireofexchange.FireOfExchangeBurnPrice;
import io.xeros.content.items.aoeweapons.AOESystem;
import io.xeros.content.polls.PollTab;
import io.xeros.content.preset.PresetManager;
import io.xeros.content.referral.ReferralCode;
import io.xeros.content.seasons.Christmas;
import io.xeros.content.seasons.Halloween;
import io.xeros.content.skills.runecrafting.ouriana.ZamorakGuardian;
import io.xeros.content.tournaments.TourneyManager;
import io.xeros.content.trails.TreasureTrailsRewards;
import io.xeros.content.vote_panel.VotePanelManager;
import io.xeros.content.wogw.Wogw;
import io.xeros.content.worldevent.WorldEventContainer;
import io.xeros.model.Npcs;
import io.xeros.model.collisionmap.ObjectDef;
import io.xeros.model.collisionmap.Region;
import io.xeros.model.collisionmap.doors.DoorDefinition;
import io.xeros.model.cycleevent.impl.BonusApplianceEvent;
import io.xeros.model.cycleevent.impl.DidYouKnowEvent;
import io.xeros.model.cycleevent.impl.LeaderboardUpdateEvent;
import io.xeros.model.definitions.*;
import io.xeros.model.entity.npc.NPCRelationship;
import io.xeros.model.entity.npc.NpcSpawnLoader;
import io.xeros.model.entity.npc.NpcSpawnLoaderOSRS;
import io.xeros.model.entity.npc.actions.CustomActions;
import io.xeros.model.entity.npc.stats.NpcCombatDefinition;
import io.xeros.model.entity.player.PlayerFactory;
import io.xeros.model.entity.player.save.PlayerSave;
import io.xeros.model.entity.player.save.backup.PlayerSaveBackup;
import io.xeros.model.lobby.LobbyManager;
import io.xeros.model.world.ShopHandler;
import io.xeros.objects.Doors;
import io.xeros.objects.DoubleDoors;
import io.xeros.objects.ForceDoors;
import io.xeros.punishments.PunishmentCycleEvent;
import io.xeros.util.ClassGraphHandler;
import io.xeros.util.Reflection;
import io.xeros.util.discord.DiscordIntegration;
import io.xeros.util.offlinestorage.ItemCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Stuff to do on startup.
 *
 * @author Michael Sasse (https://github.com/mikeysasse/)
 */
public class ServerStartup {

    private static final Logger logger = LoggerFactory.getLogger(ServerStartup.class);

    static void load() throws Exception {
        Reflection.getMethodsAnnotatedWith(Init.class).forEach(method -> {
            try {
                method.invoke(null);
            } catch (Exception e) {
                logger.error("Error loading @Init annotated method[{}] inside class[{}]", method, method.getClass(), e);
                e.printStackTrace();
                System.exit(1);
            }
        });
        Rewards.init();
        DonationReward.load();
        PlayerSave.loadPlayerSaveEntries();
        EventCalendarWinnerSelect.getInstance().init();
        TrackedMonster.init();
        Boosts.init();
        ItemDef.load();
        ShopDef.load();
        ShopHandler.load();
        DynamicClassLoader.load();
        NpcStats.load();
        for (var npc : NpcStats.npcStatsMap.int2ObjectEntrySet()) {
            if (npc == null) continue;
            NpcStats data = NpcStats.forId(npc.getIntKey());
            if (data.scripts != null) {
                data.scripts.resolve();
            }
        }
        ItemStats.load();
        NpcDef.load();
        // Npc Combat Definition must be above npc load
        NpcCombatDefinition.load();
        Server.npcHandler.init();
        NPCRelationship.setup();
        EventCalendar.verifyCalendar();
        Server.getPunishments().initialize();
        Server.getEventHandler().submit(new DidYouKnowEvent());
        Server.getEventHandler().submit(new BonusApplianceEvent());
        Server.getEventHandler().submit(new PunishmentCycleEvent(Server.getPunishments(), 50));
//        Server.getEventHandler().submit(new UpdateQuestTab());
        Server.getEventHandler().submit(new LeaderboardUpdateEvent());
        Wogw.init();
        AOESystem.getSingleton().loadAOEDATA();
        PollTab.init();
        DoorDefinition.load();
        GodwarsEquipment.load();
        GodwarsNPCs.load();
        LobbyManager.initializeLobbies();
        VotePanelManager.init();
        TourneyManager.initialiseSingleton();
        TourneyManager.getSingleton().init();
        WGManager.initialiseSingleton();
        Server.getDropManager().read();
        TreasureTrailsRewards.load();
        AnimationLength.startup();
        PresetManager.getSingleton().init();
        ObjectDef.loadConfig();
        Server.getGlobalObjects().loadGlobalObjectFile();
        ItemDefinitionLoader.init();
        CollectionLog.init();
        Region.load();
     //   DiscordIntegration.loadConnectedAccounts();
        Doors.getSingleton().load();
        DoubleDoors.getSingleton().load();
        // Keep this below region load and object loading
        NpcSpawnLoader.load();
        NpcSpawnLoaderOSRS.initOsrsSpawns();
        MonsterHunt.spawnNPC();
        Runtime.getRuntime().addShutdownHook(new ShutdownHook());
        CommandManager.initializeCommands();
        NightmareStatusNPC.init();

        CustomActions.loadActions();
        Christmas.initChristmas();

     //   PlayerFactory.createTestPlayers();
        if (Server.isDebug()) {
            PlayerFactory.createTestPlayers();
        }
        ReferralCode.load();
        DailyRewardContainer.load();
        DailyRewardsRecords.load();
        WorldEventContainer.getInstance().initialise();
        FireOfExchangeBurnPrice.init();
        Server.getLogging().schedule();

        ItemCollection.IO.init("offlinerewards");

        ForceDoors.Init();
//        Server.loadSqlNetwork();
//        Server.loadRealmSqlNetwork();
        //io.xeros.sql.ingamestore.Configuration.loadConfiguration();

        ZamorakGuardian.spawn();
        new SarachnisNpc(Npcs.SARACHNIS, SarachnisNpc.SPAWN_POSITION);

        PlayerSaveBackup.start(Configuration.PLAYER_SAVE_TIMER_MILLIS, Configuration.PLAYER_SAVE_BACKUP_EVERY_X_SAVE_TICKS);

        Reflection.getMethodsAnnotatedWith(PostInit.class).forEach(method -> {
            try {
                method.invoke(null);
            } catch (Exception e) {
                logger.error("Error loading @Init annotated method[{}] inside class[{}]", method, method.getClass(), e);
                e.printStackTrace();
                System.exit(1);
            }
        });

    }

}
