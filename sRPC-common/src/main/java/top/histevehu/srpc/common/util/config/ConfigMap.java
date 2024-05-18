package top.histevehu.srpc.common.util.config;

import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ConfigMap {
    private final Map<String, String> configMap;

    public ConfigMap(Map<String, String> configMap) {
        this.configMap = configMap;
    }

    public Set<Map.Entry<String, String>> getMapEntries() {
        return configMap.entrySet();
    }

    public Optional<String> get(String key) {
        return Optional.ofNullable(configMap.get(key));
    }

    public void set(String key, String value) {
        configMap.put(key, value);
    }
}
