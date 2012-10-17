package edu.yale.plugins.tasks.model;

/**
 * Created with IntelliJ IDEA.
 * User: nathan
 * Date: 10/16/12
 * Time: 2:10 PM
 * A class that stores config data about the plugin
 */
public class ConfigData {
    private Boolean cacheBoxRecords = true;
    private Boolean cacheContainerRecords = true;

    public ConfigData() { }

    public Boolean getCacheBoxRecords() {
        return cacheBoxRecords;
    }

    public void setCacheBoxRecords(Boolean cacheBoxRecords) {
        this.cacheBoxRecords = cacheBoxRecords;
    }

    public Boolean getCacheContainerRecords() {
        return cacheContainerRecords;
    }

    public void setCacheContainerRecords(Boolean cacheContainerRecords) {
        this.cacheContainerRecords = cacheContainerRecords;
    }
}
