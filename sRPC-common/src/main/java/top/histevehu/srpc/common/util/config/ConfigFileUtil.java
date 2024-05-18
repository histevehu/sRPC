package top.histevehu.srpc.common.util.config;

import lombok.extern.slf4j.Slf4j;
import top.histevehu.srpc.common.enumeration.RpcError;
import top.histevehu.srpc.common.exception.RpcException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/*
 * 配置文件工具类
 */
@Slf4j
public class ConfigFileUtil {
    public static ConfigMap parseConfigFile(String configFilePath) throws IOException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        URL configFileURL = classLoader.getResource(configFilePath);
        if (configFileURL == null) {
            log.error("配置文件{}未找到", configFilePath);
            throw new RpcException(RpcError.CONFIG_FILE_NOT_FOUND);
        }
        return parseConfigFile(configFileURL);
    }

    public static ConfigMap parseConfigFile(URL configFileURL) throws IOException {
        if (configFileURL == null) {
            log.error("配置文件未找到");
            throw new RpcException(RpcError.CONFIG_FILE_NOT_FOUND);
        }
        Map<String, String> configMap = new HashMap<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(configFileURL.openStream(), StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                final int ci = line.indexOf('#');
                if (ci >= 0) {
                    line = line.substring(0, ci);
                }
                line = line.trim();
                if (!line.isEmpty()) {
                    final int ei = line.indexOf('=');
                    if (ei <= 0 || ei >= line.length() - 1) {
                        log.warn("配置文件格式错误：行{}:{}，每行应为\"KEY=VALUE格式\"，且VALUE中不含\"=\"", lineNumber, line);
                        continue;
                    }
                    String name = line.substring(0, ei).trim();
                    String value = line.substring(ei + 1).trim();
                    if (!name.isEmpty() && !value.isEmpty()) {
                        configMap.put(name, value);
                    } else {
                        log.warn("配置文件存在空值对：行{}:{}", lineNumber, line);
                    }
                }
            }
        } catch (IOException e) {
            throw new IOException(e);
        }
        return new ConfigMap(configMap);
    }

}

