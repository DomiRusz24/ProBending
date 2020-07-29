package me.domirusz24.pk.probending.probending.config;

import me.domirusz24.pk.probending.probending.config.playerdata.DataConfig;
import me.domirusz24.pk.probending.probending.config.playerdata.TeamDataConfig;
import me.domirusz24.pk.probending.probending.config.winlosecommandsconfig.WinLoseCommands;

public class ConfigManager {

    private static Config ArenaLocationsConfig, ArenaTBStagesConfig, LanguageConfig, WinLoseCommands, DataConfig, TeamDataConfig;

    public static void register() {
        ArenaLocationsConfig = new ArenaLocationsConfig();
        ArenaLocationsConfig.setupConfig();
        ArenaTBStagesConfig = new ArenaTBStagesConfig();
        ArenaTBStagesConfig.setupConfig();
        LanguageConfig = new LanguageConfig();
        LanguageConfig.setupConfig();
        WinLoseCommands = new WinLoseCommands();
        WinLoseCommands.setupConfig();
        DataConfig = new DataConfig();
        DataConfig.setupConfig();
        TeamDataConfig = new TeamDataConfig();
        TeamDataConfig.setupConfig();
    }

    public static Config getArenaLocationsConfig() {
        return ArenaLocationsConfig;
    }

    public static Config getArenaTBStagesConfig() {
        return ArenaTBStagesConfig;
    }

    public static Config getLanguageConfig() {
        return LanguageConfig;
    }

    public static Config getWinLoseCommands() {
        return WinLoseCommands;
    }

    public static Config getDataConfig() {
        return DataConfig;
    }

    public static Config getTeamDataConfig() {
        return TeamDataConfig;
    }
}
