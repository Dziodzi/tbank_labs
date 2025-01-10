package io.github.dziodzi.service;

import io.github.dziodzi.entity.Event;
import io.github.dziodzi.entity.EventResponse;
import io.github.dziodzi.exception.ConvertingException;
import io.github.dziodzi.tools.LogExecutionTime;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Slf4j
@LogExecutionTime
public class EventFutureService {
    
    private final RestTemplate restTemplate;
    private final CurrencyService currencyService;
    private final ExecutorService executorService;
    
    @Value("${custom.api.events}")
    private String eventApiUrl;
    
    @Value("${custom.filters.events}")
    private String urlFilter;
    
    private static final Pattern PRICE_PATTERN = Pattern.compile("\\d+");
    
    @PreDestroy
    public void shutdownExecutorService() {
        executorService.shutdown();
    }
    
    public CompletableFuture<List<Event>> getFilteredEvents(double budget, String currency, Long dateFrom, Long dateTo) {
        long[] dateRange = resolveDateRange(dateFrom, dateTo);
        dateFrom = dateRange[0];
        dateTo = dateRange[1];
        
        CompletableFuture<List<Event>> eventsFuture = getEvents(dateFrom, dateTo);
        CompletableFuture<Double> convertedBudgetFuture = CompletableFuture.supplyAsync(
                () -> convertCurrency(budget, currency), executorService);
        
        return eventsFuture.thenCombineAsync(convertedBudgetFuture, (events, convertedBudget) -> {
            events.forEach(event -> event.setParsedPrice(parsePrice(event.getPrice(), event.isFree())));
            return filterEventsByBudget(events, convertedBudget);
        }, executorService);
    }
    
    private double convertCurrency(double budget, String currency) {
        try {
            return currencyService.getValueOfCurrencyByCode(currency) * budget;
        } catch (Exception e) {
            log.error("Failed to convert currency: {}", currency);
            throw new ConvertingException("Currency conversion failed");
        }
    }
    
    private long[] resolveDateRange(Long dateFrom, Long dateTo) {
        if (dateFrom == null || dateTo == null) {
            LocalDate nowLocalDate = LocalDate.now();
            LocalDate oneWeekAgo = nowLocalDate.minusDays(7);
            dateFrom = oneWeekAgo.atStartOfDay(ZoneId.of("UTC")).toEpochSecond();
            dateTo = nowLocalDate.atStartOfDay(ZoneId.of("UTC")).toEpochSecond();
        }
        return new long[]{dateFrom, dateTo};
    }
    
    public CompletableFuture<List<Event>> getEvents(Long dateFrom, Long dateTo) {
        return CompletableFuture.supplyAsync(() -> {
            String url = buildUrl(dateFrom, dateTo);
            log.info("Fetching events from URL: {}", url);
            
            try {
                EventResponse response = restTemplate.getForObject(url, EventResponse.class);
                return response != null ? response.getResults() : Collections.emptyList();
            } catch (Exception e) {
                log.error("Error fetching events from external API", e);
                return Collections.emptyList();
            }
        }, executorService);
    }
    
    private String buildUrl(Long dateFrom, Long dateTo) {
        StringBuilder urlBuilder = new StringBuilder(String.format("%s%s", eventApiUrl, urlFilter));
        if (dateFrom != null) {
            urlBuilder.append("&actual_since=").append(dateFrom);
        }
        if (dateTo != null) {
            urlBuilder.append("&actual_until=").append(dateTo);
        }
        return urlBuilder.toString();
    }
    
    public List<Event> filterEventsByBudget(List<Event> events, double budget) {
        return events.stream()
                .filter(event -> event.isFree() || (event.getParsedPrice() != null && event.getParsedPrice() <= budget))
                .toList();
    }
    
    private Double parsePrice(String priceString, boolean isFree) {
        if (priceString == null || priceString.isEmpty()) {
            return isFree ? 0.0 : null;
        }
        Matcher matcher = PRICE_PATTERN.matcher(priceString.replaceAll("\\s+", ""));
        return matcher.find() ? Double.parseDouble(matcher.group()) : (isFree ? 0.0 : null);
    }
}
