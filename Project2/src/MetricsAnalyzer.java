import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.*;

public class MetricsAnalyzer {
    //Zamiast AtomicInteger jest LongAdder, żeby troche usprawnić to
    private final LongAdder clientCount = new LongAdder();
    private final LongAdder totalOperations = new LongAdder();
    private final Map<String, LongAdder> operationCounts = new ConcurrentHashMap<>();
    private final LongAdder errorCount = new LongAdder();
    private final LongAdder resultSum = new LongAdder();

    public MetricsAnalyzer() {
        operationCounts.put("ADD", new LongAdder());
        operationCounts.put("SUB", new LongAdder());
        operationCounts.put("MUL", new LongAdder());
        operationCounts.put("DIV", new LongAdder());
    }
    public void incrementClientCount() {
        clientCount.increment();
    }

    public void incrementOperationCount(String operation) {
        operationCounts.computeIfAbsent(operation, k -> new LongAdder()).increment();
        totalOperations.increment();
    }

    public void incrementErrorCount() {
        errorCount.increment();
    }

    public void addResult(int result) {
        resultSum.add(result);
    }
    public void startReporting(long intervalMillis) {
        CompletableFuture.runAsync(() -> {
            long lastReportTime = System.currentTimeMillis();

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(intervalMillis); // Czekamy przez podany czas
                } catch (InterruptedException e) {
                    System.err.println("Statistics reporting interrupted.");
                    return;
                }

                long currentTime = System.currentTimeMillis();
                System.out.println("\n--- Statistics Report ---");
                System.out.println("Total clients connected: " + clientCount.sum());
                System.out.println("Total operations performed: " + totalOperations.sum());
                System.out.println("Operation counts: " + getOperationCountsReport());
                System.out.println("Total errors: " + errorCount.sum());
                System.out.println("Sum of results: " + resultSum.sum());

                lastReportTime = currentTime;
            }
        });
    }

    private String getOperationCountsReport() {
        StringBuilder report = new StringBuilder();
        operationCounts.forEach((operation, count) ->
                report.append(operation).append(": ").append(count.sum()).append("  "));
        return report.toString();
    }
}
