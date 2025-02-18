package net.aubrecht.concurrencyvirtual.service;

import jakarta.annotation.Resource;
import jakarta.ejb.Singleton;
import jakarta.enterprise.concurrent.ManagedThreadFactory;
import jakarta.enterprise.concurrent.ManagedThreadFactoryDefinition;
import java.time.Duration;

/**
 * Test with Virtual Threads Factory.
 *
 * @author aubi
 */
@Singleton
@ManagedThreadFactoryDefinition(name = "java:app/concurrency/VirtFactory", virtual = true)
public class FactorySample {

    @Resource(lookup = "java:app/concurrency/VirtFactory")
    private ManagedThreadFactory factoryAppDefined;

    @Resource
    private ManagedThreadFactory factoryDefault;

    @Resource(lookup = "concurrent/VirtFactory")
    private ManagedThreadFactory factoryCustom;

    public String tryExectutorService(boolean simpler) {
        String report;
        RunnableWithDescription taskAppDefined = new RunnableWithDescription(simpler, "Application Defined ManagedThreadFactory with virtual=true");
        report = collectOutput(factoryAppDefined, taskAppDefined) + "\n";
        RunnableWithDescription taskDefault = new RunnableWithDescription(simpler, "Default ManagedThreadFactory (should use platform threads)");
        report += collectOutput(factoryDefault, taskDefault) + "\n";
        RunnableWithDescription taskCustom = new RunnableWithDescription(simpler, "Server-defined ManagedThreadFactory from concurrent/VirtFactory (should use virtual threads)");
        report += collectOutput(factoryCustom, taskCustom) + "\n";
        return report;
    }

    public String collectOutput(ManagedThreadFactory factory, RunnableWithDescription task) {
        Thread thread = factory.newThread(task);
        thread.start();
        boolean terminated = false;
        try {
            terminated = thread.join(Duration.ofSeconds(10));
        } catch (InterruptedException ex) {
            throw new RuntimeException("Well, it din't go well with virtual threads: " + ex.getMessage(), ex);
        }

        return (terminated ? "" : "THREAD NOT TERMINATED!!! ") + task.getReport();
    }

}
