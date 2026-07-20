package io.github.dyskaura.easyboot.cache;

import java.time.Duration;
import java.util.Optional;

public interface KeyValueStore {
    void put(String key, String value, Duration ttl);
    Optional<String> get(String key);
    void delete(String key);
}
