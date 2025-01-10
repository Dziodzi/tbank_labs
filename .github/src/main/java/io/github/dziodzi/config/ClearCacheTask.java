package io.github.dziodzi.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@Component
public class ClearCacheTask {

    private final CacheManager cacheManager;

    @Autowired
    public ClearCacheTask(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Scheduled(fixedRateString = "${clear.all.cache.fixed.rate}", initialDelayString = "${clear.all.cache.init.delay}")
    public void clearCaches() {
        log.info("Clearing all caches");
        cacheManager.getCacheNames().parallelStream().forEach(name -> {
            try {
                Objects.requireNonNull(cacheManager.getCache(name)).clear();
            } catch (Exception e) {
                log.error("Error clearing cache: {}", name, e);
            }
        });
    }
}
