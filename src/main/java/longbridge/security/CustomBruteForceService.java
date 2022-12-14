package longbridge.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import wiremock.com.google.common.cache.CacheBuilder;
import wiremock.com.google.common.cache.CacheLoader;
import wiremock.com.google.common.cache.LoadingCache;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;


@Service
public class CustomBruteForceService
{
    @Value("${app.brute.max}")
    private  int MAX_ATTEMPT ;
    private final LoadingCache<String, Integer> attemptsCache;

    public CustomBruteForceService() {
        super();
        attemptsCache = CacheBuilder.newBuilder().expireAfterWrite(5, TimeUnit.MINUTES)
               // .removalListener()
                .build(new CacheLoader<>() {
                    @Override
                    public Integer load(final String key) {
                        return 0;
                    }
                });
    }

    //

    public void loginSucceeded(final String key) {
        attemptsCache.invalidate(key);
    }

    public void loginFailed(final String key) {
        int attempts = 0;
        try {
            attempts = attemptsCache.get(key);
        } catch (final ExecutionException e) {
            attempts = 0;
        }
        attempts++;
        attemptsCache.put(key, attempts);
    }

    public boolean isBlocked(final String key) {
        try {
            return attemptsCache.get(key) >= MAX_ATTEMPT;
        } catch (final ExecutionException e) {
            return false;
        }
    }
}
