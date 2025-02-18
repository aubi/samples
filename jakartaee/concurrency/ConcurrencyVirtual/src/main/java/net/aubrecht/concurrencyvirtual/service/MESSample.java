package net.aubrecht.concurrencyvirtual.service;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.enterprise.concurrent.ManagedExecutorDefinition;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sample with Managed Executor Services and virtual threads.
 *
 * @author aubi
 */
@ManagedExecutorDefinition(name = "java:app/VirtMES", maxAsync = 10, virtual = true)
@Stateless
public class MESSample {
    @Resource(lookup = "java:app/VirtMES")
    private ManagedExecutorService mesApp;

    @Resource
    private ManagedExecutorService mesDefault;

    @Resource(lookup = "concurrent/VirtMES")
    private ManagedExecutorService mesCustom;

    public String tryExectutorService(boolean simpler) {
        String report;
        RunnableWithDescription taskAppDefined = new RunnableWithDescription(simpler, "Application Defined ManagedExecutorService with virtual=true");
        report = collectOutput(mesApp, taskAppDefined) + "\n";
        RunnableWithDescription taskDefault = new RunnableWithDescription(simpler, "Default ManagedExecutorService (should use platform threads)");
        report += collectOutput(mesDefault, taskDefault) + "\n";
        RunnableWithDescription taskCustom = new RunnableWithDescription(simpler, "Server-defined ManagedExecutorService from concurrent/VirtMES (should use virtual threads)");
        report += collectOutput(mesCustom, taskCustom) + "\n";
        return report;
    }

    public String collectOutput(ManagedExecutorService mes, RunnableWithDescription task) {
        Future<String> thread = mes.submit((Callable<String>) task);
        try {
            String msg = thread.get(10, TimeUnit.SECONDS);
            return msg;
        } catch (ExecutionException | InterruptedException | TimeoutException ex) {
            Logger.getLogger(MESSample.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Well, it din't go well with virtual threads: " + ex.getMessage(), ex);
        }
    }
}
