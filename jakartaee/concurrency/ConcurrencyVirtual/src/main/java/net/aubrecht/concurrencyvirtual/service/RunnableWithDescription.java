package net.aubrecht.concurrencyvirtual.service;

import java.util.concurrent.Callable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Runnable and Callable with description, creating thread report.
 *
 * @author Petr Aubrecht <aubrecht@asoftware.cz>
 */
public class RunnableWithDescription implements Runnable, Callable<String> {

    private final boolean simpler;
    private String description;
    private StringBuilder report = new StringBuilder();

    public RunnableWithDescription(boolean simpler, String description) {
        this.simpler = simpler;
        this.description = description;
    }

    @Override
    public void run() {
        String stack = Stream.of(Thread.currentThread().getStackTrace()).map(x -> x.toString()).collect(Collectors.joining("\n"));
        report.append(description)
                .append("\n")
                .append("-".repeat(description.length()))
                .append("\n")
                .append("Thread '")
                .append(Thread.currentThread().getName())
                .append("' => ")
                .append(Thread.currentThread().isVirtual() ? "virtual" : "platform")
                .append("\n");
        if (!simpler) {
            report.append(stack).append("\n");
        }
    }

    public String getReport() {
        return report.toString();
    }

    @Override
    public String call() throws Exception {
        run();
        return getReport();
    }
}
