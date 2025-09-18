package security;

import java.util.*;

//защита от брутфорса
public class LoginRateLimiter {
    private static final int MAX_ATTEMPTS = 5;
    private static final long WINDOW_MS = 15 * 60_000;

    private static final Map<String, Deque<Long>> attempts = new HashMap<>();

    public static boolean isAllowed(String key) {
        long now = System.currentTimeMillis();
        attempts.computeIfAbsent(key, k -> new ArrayDeque<>());
        Deque<Long> q = attempts.get(key);

        while (!q.isEmpty() && now - q.peek() > WINDOW_MS) {
            q.pollFirst();
        }

        if (q.size() >= MAX_ATTEMPTS) return false;

        q.addLast(now);
        return true;
    }
}
