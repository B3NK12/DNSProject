import java.util.HashSet;
import java.util.Random;

public class DNSSimulator {

    private static final Random rand = new Random();

    // Resolver cache for recursive DNS; separate one for enhanced DNS
    private static String iterativeCache = new String();
    private static String recursiveCache = new String();
    private static String enhancedCache = new String();
    private static final HashSet<String> recursiveRootCache = new HashSet<>();
    private static final HashSet<String> recursiveTLDCache = new HashSet<>();

    // Delays on each stage of the DNS lookup process (ms)
    private static final int ROOT_DELAY = 70;
    private static final int TLD_DELAY = 70;
    private static final int AUTH_DELAY = 70;

    // Back to cache (straight from auth) delay for our new recursive DNS
    private static final int BACK_TO_CACHE_DELAY = 70;

    // cache delay (for contacting local cache)
    private static final int CACHE_DELAY = 25;

    // Standard recursive additional overhead due to recursing

    // Iterative DNS: full lookup every time
    public static long simulateIterativeDNS(String url) {
        long start = System.currentTimeMillis();

        // If cache hit
        if (iterativeCache.contains(url)) {
        	sleep(2*CACHE_DELAY);
        } else {
        	sleep(CACHE_DELAY);
        	sleep(ROOT_DELAY);
        	sleep(BACK_TO_CACHE_DELAY);
        	sleep(TLD_DELAY);
        	sleep(BACK_TO_CACHE_DELAY);
        	sleep(AUTH_DELAY);
        	sleep(BACK_TO_CACHE_DELAY);
        	sleep(CACHE_DELAY);
        	
        	iterativeCache = new String(url);
        }

        return System.currentTimeMillis() - start;
    }

    // Standard Recursive DNS: fully recurses every time through each server; no knowledge of local server address
    public static long simulateRecursiveDNS(String url) {
        long start = System.currentTimeMillis();

        // If cache hit
        if (recursiveCache.contains(url)) {
            sleep(2*CACHE_DELAY);
        // Else, go through the whole recurse
        } else if (recursiveRootCache.contains(url)) {
        	sleep(CACHE_DELAY);
        	sleep(ROOT_DELAY);
        	sleep(BACK_TO_CACHE_DELAY);
        	sleep(CACHE_DELAY);
        } else if (recursiveTLDCache.contains(url)) {
        	sleep(CACHE_DELAY);
        	sleep(ROOT_DELAY);
        	sleep(TLD_DELAY);
        	sleep(ROOT_DELAY);
        	sleep(BACK_TO_CACHE_DELAY);
        	sleep(CACHE_DELAY);
        	recursiveRootCache.add(url);
        } else {
            sleep(CACHE_DELAY);
            sleep(ROOT_DELAY);
            sleep(TLD_DELAY);
            sleep(AUTH_DELAY);
            sleep(TLD_DELAY);
            sleep(ROOT_DELAY);
            sleep(BACK_TO_CACHE_DELAY);
            sleep(CACHE_DELAY);
            recursiveRootCache.add(url);
            recursiveTLDCache.add(url);
        }
        recursiveCache = new String(url);

        return System.currentTimeMillis() - start;
    }

    // Our new recursive DNS: once authoritative server is reached, the resolver caches the result for future queries;
    // no need for full recursion which eliminates overhead
    public static long simulateEnhancedDNS(String url) {
        long start = System.currentTimeMillis();

        // If cache hit
        if (enhancedCache.contains(url)) {
            sleep(2*CACHE_DELAY);
        // Else, recurse to the authoritative server, then back to cache without full recursion
        } else {
            sleep(CACHE_DELAY);
            sleep(ROOT_DELAY);
            sleep(TLD_DELAY);
            sleep(AUTH_DELAY);
            sleep(BACK_TO_CACHE_DELAY);
            sleep(CACHE_DELAY);

            enhancedCache = new String(url);
        }

        return System.currentTimeMillis() - start;
    }

    // Simulate delay with slight randomness to simulate network busyness and other factors
    private static void sleep(int base) {
        try {
            //Thread.sleep(base + rand.nextInt(10));
        	Thread.sleep(base);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}