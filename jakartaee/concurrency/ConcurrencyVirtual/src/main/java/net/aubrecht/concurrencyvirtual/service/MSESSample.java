package net.aubrecht.concurrencyvirtual.service;

import jakarta.annotation.Resource;
import jakarta.ejb.Singleton;
import jakarta.enterprise.concurrent.CronTrigger;
import jakarta.enterprise.concurrent.ManagedScheduledExecutorDefinition;
import jakarta.enterprise.concurrent.ManagedScheduledExecutorService;
import java.time.ZoneId;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Sample with Managed Scheduled Executor Services and virtual threads.
 *
 * @author aubi
 */
@Singleton
@ManagedScheduledExecutorDefinition(name = "java:app/VirtMSES", virtual = true)
public class MSESSample {

    @Resource(lookup = "java:app/VirtMSES")
    private ManagedScheduledExecutorService msesApp;

    @Resource
    private ManagedScheduledExecutorService msesDefault;

    @Resource(lookup = "concurrent/VirtMSES")
    private ManagedScheduledExecutorService msesCustom;

    private AtomicLong counter = new AtomicLong();
    private Future<String> thread = null;

    public String tryExectutorService(boolean simpler) {
        String report;
        RunnableWithDescription taskAppDefined = new RunnableWithDescription(simpler, "Application Defined ManagedScheduledExecutorService with virtual=true");
        report = collectOutput(msesApp, taskAppDefined) + "\n";
        RunnableWithDescription taskDefault = new RunnableWithDescription(simpler, "Default ManagedScheduledExecutorService (should use platform threads)");
        report += collectOutput(msesDefault, taskDefault) + "\n";
        RunnableWithDescription taskCustom = new RunnableWithDescription(simpler, "Server-defined ManagedScheduledExecutorService from concurrent/VirtMSES (should use virtual threads)");
        report += collectOutput(msesCustom, taskCustom) + "\n";
        return report;
//        if (thread == null) {
//            Callable<String> task = () -> {
//                long c = counter.incrementAndGet();
//                String stack = Stream.of(Thread.currentThread().getStackTrace()).map(x -> x.toString()).collect(Collectors.joining("\n"));
//                return "Thread '" + Thread.currentThread().getName() + "' => " + (Thread.currentThread().isVirtual() ? "virtual" : "platform")
//                        + ", counter: " + c + "\n"
//                        + stack;
//            };
//
//            thread = msesApp.schedule(task, new CronTrigger("* * * * * *", ZoneId.systemDefault()));
//        }
//        try {
//            String msg = thread.get();
//            return msg;
//        } catch (InterruptedException | ExecutionException ex) {
//            Logger.getLogger(MSESSample.class.getName()).log(Level.SEVERE, null, ex);
//            throw new RuntimeException("Well, it din't go well with virtual threads: " + ex.getMessage(), ex);
//        }
    }
    public String collectOutput(ManagedScheduledExecutorService mses, RunnableWithDescription task) {
        Future<String> thread = mses.schedule((Callable<String>) task, new CronTrigger("* * * * * *", ZoneId.systemDefault()));
        try {
            String msg = thread.get(10, TimeUnit.SECONDS);
            return msg;
        } catch (ExecutionException | InterruptedException | TimeoutException ex) {
            Logger.getLogger(MESSample.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Well, it din't go well with virtual threads: " + ex.getMessage(), ex);
        }
    }

}
