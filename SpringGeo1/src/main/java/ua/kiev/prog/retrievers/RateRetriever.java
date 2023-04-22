package ua.kiev.prog.retrievers;

import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import ua.kiev.prog.json.Rate;

import java.time.LocalTime;

@Component
public class RateRetriever {


    private static final String URL3 = "https://api.apilayer.com/fixer/latest?symbols=UAH&base=EUR";
    String key = "SlGZ8w8BP3dlZaceHch3LryEgGvvvnRi";
    private CacheManager cacheManager;

//    public RateRetriever(CacheManager cacheManager) {
//        this.cacheManager = cacheManager;
//    }

    @Cacheable("rates") // Redis
    public Rate getRate() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", key);
        HttpEntity<String> entity = new HttpEntity<String>("parameters", headers);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Rate> response = restTemplate.exchange(
                URL3,
                HttpMethod.GET,
                entity,
                Rate.class
        );
        return response.getBody();
    }

    @CacheEvict(value = "rates", allEntries = true)
    @Scheduled(cron = "0 1 * * * *")
    public void evictRateCache() {
        for (String names : cacheManager.getCacheNames()) {
            cacheManager.getCache(names).clear();
        }
    }
}
