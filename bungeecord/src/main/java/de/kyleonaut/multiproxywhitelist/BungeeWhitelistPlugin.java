package de.kyleonaut.multiproxywhitelist;

import de.kyleonaut.multiproxywhitelist.command.WhitelistCommand;
import de.kyleonaut.multiproxywhitelist.config.BungeeConfig;
import de.kyleonaut.multiproxywhitelist.executor.RequestExecutor;
import de.kyleonaut.multiproxywhitelist.listener.PlayerJoinListener;
import de.kyleonaut.multiproxywhitelist.manager.DatabaseManager;
import de.kyleonaut.multiproxywhitelist.manager.JedisManager;
import de.kyleonaut.multiproxywhitelist.repository.MojangRepository;
import de.kyleonaut.multiproxywhitelist.repository.WhitelistRepository;
import de.kyleonaut.multiproxywhitelist.request.MojangRequests;
import de.kyleonaut.multiproxywhitelist.service.MojangService;
import de.kyleonaut.multiproxywhitelist.service.WhitelistService;
import lombok.Getter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * @author kyleonaut
 */
public class BungeeWhitelistPlugin extends Plugin {
    @Getter
    private static RequestExecutor requestExecutor;
    private JedisManager jedisManager;
    private DatabaseManager databaseManager;

    @Override
    public void onEnable() {
        final MojangRequests mojangRequests = new Retrofit.Builder()
                .baseUrl("https://api.mojang.com/")
                .addConverterFactory(JacksonConverterFactory.create())
                .build()
                .create(MojangRequests.class);
        final BungeeConfig bungeeConfig = new BungeeConfig(this);
        requestExecutor = new RequestExecutor();
        this.databaseManager = new DatabaseManager(
                bungeeConfig.getConfigData().getMysqlUrl(),
                bungeeConfig.getConfigData().getMysqlUser(),
                bungeeConfig.getConfigData().getMysqlPassword()
        );
        this.databaseManager.getConnection();
        this.jedisManager = new JedisManager(
                bungeeConfig.getConfigData().getRedisHost(),
                bungeeConfig.getConfigData().getRedisPort(),
                bungeeConfig.getConfigData().getRedisPassword()
        );
        final MojangRepository mojangRepository = new MojangRepository(mojangRequests);
        final WhitelistRepository whitelistRepository = new WhitelistRepository(databaseManager.getConnection());
        final MojangService mojangService = new MojangService(mojangRepository, whitelistRepository);
        final WhitelistService whitelistService = new WhitelistService(mojangService, whitelistRepository, jedisManager);
        whitelistService.init();

        ProxyServer.getInstance().getPluginManager().registerCommand(this, new WhitelistCommand(whitelistService));
        ProxyServer.getInstance().getPluginManager().registerListener(this, new PlayerJoinListener(whitelistService));

    }

    @Override
    public void onDisable() {
        this.jedisManager.shutdown();
        this.databaseManager.shutdown();
        requestExecutor.close();
    }
}
