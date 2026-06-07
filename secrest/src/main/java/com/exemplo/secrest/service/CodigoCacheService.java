package com.exemplo.secrest.service;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

@Service
@EnableScheduling
public class CodigoCacheService {

    // Armazena o email como chave e os dados do código (número + validade) como valor
    private final ConcurrentHashMap<String, CacheEntry> cache = new ConcurrentHashMap<>();

    public String generateAndSaveCode(String email) {
        
        String code = String.format("%06d", new Random().nextInt(1000000));

       
        LocalDateTime expiration = LocalDateTime.now().plusMinutes(5);

        cache.put(email, new CacheEntry(code, expiration));
        return code;
    }

    public boolean verifyCode(String email, String code) {
        CacheEntry entry = cache.get(email);

        if (entry == null) {
            return false; 
        }

        if (LocalDateTime.now().isAfter(entry.expirationTime())) {
            cache.remove(email); 
            return false;
        }

        if (entry.code().equals(code)) {
            cache.remove(email); 
            return true;
        }

        return false;
    }

  
    @Scheduled(fixedRate = 60000)
    public void cleanUpCache() {
        LocalDateTime now = LocalDateTime.now();
        cache.entrySet().removeIf(entry -> now.isAfter(entry.getValue().expirationTime()));
    }

    // Record interno apenas para amarrar as duas informações
    private record CacheEntry(String code, LocalDateTime expirationTime) {}
}