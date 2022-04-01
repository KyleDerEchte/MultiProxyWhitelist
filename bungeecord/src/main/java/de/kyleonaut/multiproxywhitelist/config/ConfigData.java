package de.kyleonaut.multiproxywhitelist.config;

import lombok.Data;

/**
 * @author kyleonaut
 */
@Data
public class ConfigData {
    private String mysqlUrl;
    private String mysqlUser;
    private String mysqlPassword;
    private String redisHost;
    private int redisPort;
    private String redisPassword;
}
