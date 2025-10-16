package co.edu.unbosque.foresta.util;

import org.springframework.stereotype.Component;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TransferTracker {
    private final Map<String, Instant> lastIncomingByAlpacaId = new ConcurrentHashMap<>();

    public void markIncoming(String alpacaId) {
        if (alpacaId != null && !alpacaId.isBlank()) {
            lastIncomingByAlpacaId.put(alpacaId, Instant.now());
        }
    }

    public boolean hasRecentIncoming(String alpacaId, int minutesWindow) {
        Instant ts = lastIncomingByAlpacaId.get(alpacaId);
        if (ts == null) return false;
        return ts.isAfter(Instant.now().minusSeconds(minutesWindow * 60L));
    }
}
