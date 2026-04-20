import java.util.List;
import java.util.HashSet;

public class DNSSimulator {

    public interface RouteHighlighter {
        void highlight(List<NetworkDiagramPanel.NodeId> route, String message);
    }

    private static String iterativeCache = "";
    private static String recursiveCache = "";
    private static String enhancedCache = "";
    private static final HashSet<String> recursiveRootCache = new HashSet<>();
    private static final HashSet<String> recursiveTLDCache = new HashSet<>();

    private static final int ROOT_DELAY = 70;
    private static final int TLD_DELAY = 70;
    private static final int AUTH_DELAY = 70;
    private static final int BACK_TO_CACHE_DELAY = 70;
    private static final int CACHE_DELAY = 25;
    private static final int STEP_PAUSE = 220;

    public static long simulateIterativeDNS(String url) {
        return simulateIterativeDNS(url, null);
    }

    public static long simulateRecursiveDNS(String url) {
        return simulateRecursiveDNS(url, null);
    }

    public static long simulateEnhancedDNS(String url) {
        return simulateEnhancedDNS(url, null);
    }

    // Iterative DNS
    public static long simulateIterativeDNS(String url, RouteHighlighter highlighter) {
        long start = System.currentTimeMillis();
        
        // IP is located in the Cache
        if (iterativeCache.equals(url)) {
        	pulse(highlighter,
                    List.of(NetworkDiagramPanel.NodeId.HOST, NetworkDiagramPanel.NodeId.CACHE),
                    "Iterative DNS: host sends request to cache/local resolver.");
        	sleep(CACHE_DELAY);
                
            pulse(highlighter,
    	            List.of(NetworkDiagramPanel.NodeId.CACHE, NetworkDiagramPanel.NodeId.HOST),
    	            "Iterative DNS: cache returns final answer to host.");
            sleep(CACHE_DELAY);
        // After searching the Authoritative server, we do not return to root or TLD so they never cache
        } else {
	        pulse(highlighter,
	            List.of(NetworkDiagramPanel.NodeId.HOST, NetworkDiagramPanel.NodeId.CACHE),
	            "Iterative DNS: host sends request to cache/local resolver.");
	        sleep(CACHE_DELAY);
	
	        pulse(highlighter,
	            List.of(NetworkDiagramPanel.NodeId.CACHE, NetworkDiagramPanel.NodeId.ROOT),
	            "Iterative DNS: cache contacts root server.");
	        sleep(ROOT_DELAY);
	
	        pulse(highlighter,
	            List.of(NetworkDiagramPanel.NodeId.ROOT, NetworkDiagramPanel.NodeId.CACHE),
	            "Iterative DNS: root returns referral.");
	        sleep(BACK_TO_CACHE_DELAY);
	
	        pulse(highlighter,
	            List.of(NetworkDiagramPanel.NodeId.CACHE, NetworkDiagramPanel.NodeId.TLD),
	            "Iterative DNS: cache follows the referral to the TLD server.");
	        sleep(TLD_DELAY);
	
	        pulse(highlighter,
	            List.of(NetworkDiagramPanel.NodeId.TLD, NetworkDiagramPanel.NodeId.CACHE),
	            "Iterative DNS: TLD returns referral to the authoritative server.");
	        sleep(BACK_TO_CACHE_DELAY);
	
	        pulse(highlighter,
	            List.of(NetworkDiagramPanel.NodeId.CACHE, NetworkDiagramPanel.NodeId.AUTH),
	            "Iterative DNS: authoritative server is contacted for final answer.");
	        sleep(AUTH_DELAY);
	
	        pulse(highlighter,
	            List.of(NetworkDiagramPanel.NodeId.AUTH, NetworkDiagramPanel.NodeId.CACHE),
	            "Iterative DNS: final answer returns to cache.");
	        sleep(BACK_TO_CACHE_DELAY);
	
	        pulse(highlighter,
	            List.of(NetworkDiagramPanel.NodeId.CACHE, NetworkDiagramPanel.NodeId.HOST),
	            "Iterative DNS: cache returns final answer to host.");
	        sleep(CACHE_DELAY);
	        
	        iterativeCache = url;
        }

        return System.currentTimeMillis() - start;
    }

    // Recursive DNS
    public static long simulateRecursiveDNS(String url, RouteHighlighter highlighter) {
        long start = System.currentTimeMillis();

        // IP is located in the cache
        if (recursiveCache.equals(url)) {
            pulse(highlighter,
                List.of(NetworkDiagramPanel.NodeId.HOST, NetworkDiagramPanel.NodeId.CACHE),
                "Recursive DNS cache hit: host sends request to local resolver.");
            sleep(CACHE_DELAY);

            pulse(highlighter,
                List.of(NetworkDiagramPanel.NodeId.CACHE, NetworkDiagramPanel.NodeId.HOST),
                "Recursive DNS cache hit: resolver returns cached answer to host.");
            sleep(CACHE_DELAY);

            return System.currentTimeMillis() - start;
        // IP is contained in the root server
        } else if (recursiveRootCache.contains(url)) {
        	pulse(highlighter,
    	            List.of(NetworkDiagramPanel.NodeId.HOST, NetworkDiagramPanel.NodeId.CACHE),
    	            "Recursive DNS: host sends request to local resolver.");
    	        sleep(CACHE_DELAY);
    	
    	        pulse(highlighter,
    	            List.of(NetworkDiagramPanel.NodeId.CACHE, NetworkDiagramPanel.NodeId.ROOT),
    	            "Recursive DNS: resolver starts at the root server.");
    	        sleep(ROOT_DELAY);
    	        
    	        pulse(highlighter,
    		            List.of(NetworkDiagramPanel.NodeId.ROOT, NetworkDiagramPanel.NodeId.CACHE),
    		            "Recursive DNS: root passes the answer back to the resolver.");
    	        sleep(BACK_TO_CACHE_DELAY);
    		
		        pulse(highlighter,
		            List.of(NetworkDiagramPanel.NodeId.CACHE, NetworkDiagramPanel.NodeId.HOST),
		            "Recursive DNS: resolver returns the final answer to the host.");
		        sleep(CACHE_DELAY);
        //IP is contained in the TLD server
        } else if (recursiveTLDCache.contains(url)) {
        	pulse(highlighter,
    	            List.of(NetworkDiagramPanel.NodeId.HOST, NetworkDiagramPanel.NodeId.CACHE),
    	            "Recursive DNS: host sends request to local resolver.");
        	sleep(CACHE_DELAY);
    	
	        pulse(highlighter,
	            List.of(NetworkDiagramPanel.NodeId.CACHE, NetworkDiagramPanel.NodeId.ROOT),
	            "Recursive DNS: resolver starts at the root server.");
	        sleep(ROOT_DELAY);
	        
	        pulse(highlighter,
		            List.of(NetworkDiagramPanel.NodeId.ROOT, NetworkDiagramPanel.NodeId.TLD),
		            "Recursive DNS: root forwards the lookup to the TLD server.");
	        sleep(TLD_DELAY);
		        
	        pulse(highlighter,
		            List.of(NetworkDiagramPanel.NodeId.TLD, NetworkDiagramPanel.NodeId.ROOT),
		            "Recursive DNS: TLD passes the answer back to the root server.");
		    sleep(BACK_TO_CACHE_DELAY);
		        
	        pulse(highlighter,
		            List.of(NetworkDiagramPanel.NodeId.ROOT, NetworkDiagramPanel.NodeId.CACHE),
		            "Recursive DNS: root passes the answer back to the resolver.");
	        sleep(BACK_TO_CACHE_DELAY);
		
	        pulse(highlighter,
	            List.of(NetworkDiagramPanel.NodeId.CACHE, NetworkDiagramPanel.NodeId.HOST),
	            "Recursive DNS: resolver returns the final answer to the host.");
	        sleep(CACHE_DELAY);
	        
	        recursiveRootCache.add(url);
	    // IP is contained in the authoritative server
        } else {
	        pulse(highlighter,
	            List.of(NetworkDiagramPanel.NodeId.HOST, NetworkDiagramPanel.NodeId.CACHE),
	            "Recursive DNS: host sends request to local resolver.");
	        sleep(CACHE_DELAY);
	
	        pulse(highlighter,
	            List.of(NetworkDiagramPanel.NodeId.CACHE, NetworkDiagramPanel.NodeId.ROOT),
	            "Recursive DNS: resolver starts at the root server.");
	        sleep(ROOT_DELAY);
	
	        pulse(highlighter,
	            List.of(NetworkDiagramPanel.NodeId.ROOT, NetworkDiagramPanel.NodeId.TLD),
	            "Recursive DNS: root forwards the lookup to the TLD server.");
	        sleep(TLD_DELAY);
	
	        pulse(highlighter,
	            List.of(NetworkDiagramPanel.NodeId.TLD, NetworkDiagramPanel.NodeId.AUTH),
	            "Recursive DNS: TLD forwards the lookup to the authoritative server.");
	        sleep(AUTH_DELAY);
	
	        pulse(highlighter,
	            List.of(NetworkDiagramPanel.NodeId.AUTH, NetworkDiagramPanel.NodeId.TLD),
	            "Recursive DNS: authoritative server returns the answer to the TLD server.");
	        sleep(BACK_TO_CACHE_DELAY);
	
	        pulse(highlighter,
	            List.of(NetworkDiagramPanel.NodeId.TLD, NetworkDiagramPanel.NodeId.ROOT),
	            "Recursive DNS: TLD passes the answer back to the root server.");
	        sleep(BACK_TO_CACHE_DELAY);
	
	        pulse(highlighter,
	            List.of(NetworkDiagramPanel.NodeId.ROOT, NetworkDiagramPanel.NodeId.CACHE),
	            "Recursive DNS: root passes the answer back to the resolver.");
	        sleep(BACK_TO_CACHE_DELAY);
	
	        pulse(highlighter,
	            List.of(NetworkDiagramPanel.NodeId.CACHE, NetworkDiagramPanel.NodeId.HOST),
	            "Recursive DNS: resolver returns the final answer to the host.");
	        sleep(CACHE_DELAY);
	        
	        recursiveTLDCache.add(url);
	        recursiveRootCache.add(url);
        }
        
        recursiveCache = url;

        return System.currentTimeMillis() - start;
    }

    // Enhanced Recursive DNS
    public static long simulateEnhancedDNS(String url, RouteHighlighter highlighter) {
        long start = System.currentTimeMillis();

        // IP is located in the cache
        if (enhancedCache.equals(url)) {
            pulse(highlighter,
                List.of(NetworkDiagramPanel.NodeId.HOST, NetworkDiagramPanel.NodeId.CACHE),
                "Enhanced DNS cache hit: host sends request to cache.");
            sleep(CACHE_DELAY);

            pulse(highlighter,
                List.of(NetworkDiagramPanel.NodeId.CACHE, NetworkDiagramPanel.NodeId.HOST),
                "Enhanced DNS cache hit: cache returns answer directly to host.");
            sleep(CACHE_DELAY);
        } else {
            pulse(highlighter,
                List.of(NetworkDiagramPanel.NodeId.HOST, NetworkDiagramPanel.NodeId.CACHE),
                "Enhanced DNS: host sends request to cache.");
            sleep(CACHE_DELAY);

            pulse(highlighter,
                List.of(NetworkDiagramPanel.NodeId.CACHE, NetworkDiagramPanel.NodeId.ROOT),
                "Enhanced DNS: cache forwards request toward root.");
            sleep(ROOT_DELAY);

            pulse(highlighter,
                List.of(NetworkDiagramPanel.NodeId.ROOT, NetworkDiagramPanel.NodeId.TLD),
                "Enhanced DNS: root forwards resolution toward TLD.");
            sleep(TLD_DELAY);

            pulse(highlighter,
                List.of(NetworkDiagramPanel.NodeId.TLD, NetworkDiagramPanel.NodeId.AUTH),
                "Enhanced DNS: TLD reaches authoritative server.");
            sleep(AUTH_DELAY);

            pulse(highlighter,
                List.of(NetworkDiagramPanel.NodeId.AUTH, NetworkDiagramPanel.NodeId.CACHE),
                "Enhanced DNS: authoritative answer returns directly to cache.");
            sleep(BACK_TO_CACHE_DELAY);

            pulse(highlighter,
                List.of(NetworkDiagramPanel.NodeId.CACHE, NetworkDiagramPanel.NodeId.HOST),
                "Enhanced DNS: cache returns final answer to host.");
            sleep(CACHE_DELAY);

            enhancedCache = url;
        }

        return System.currentTimeMillis() - start;
    }

    private static void pulse(RouteHighlighter highlighter, List<NetworkDiagramPanel.NodeId> route, String message) {
        if (highlighter != null) {
            highlighter.highlight(route, message);
            sleep(STEP_PAUSE);
        }
    }

    private static void sleep(int base) {
        try {
            Thread.sleep(base);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
