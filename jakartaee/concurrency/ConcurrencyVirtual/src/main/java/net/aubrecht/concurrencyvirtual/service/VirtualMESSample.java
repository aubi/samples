package net.aubrecht.concurrencyvirtual.service;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.enterprise.concurrent.ContextService;
import jakarta.enterprise.concurrent.ContextServiceDefinition;
import jakarta.enterprise.concurrent.ManagedExecutorDefinition;
import jakarta.enterprise.concurrent.ManagedExecutorService;
import jakarta.inject.Inject;
import java.util.Arrays;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author aubi
 */
@ManagedExecutorDefinition(name = "java:app/VirtMES", maxAsync = 10, virtual = true)
@ContextServiceDefinition(name = "java:app/ContextJNDI")
@ContextServiceDefinition(name = "java:app/ContextA", qualifiers = MyContextQualifierA.class)
//@ContextServiceDefinition(name = "java:app/ContextB", qualifiers = MyContextQualifierB.class)
//@ContextServiceDefinition(name = "java:app/ContextAB", qualifiers = {MyContextQualifierA.class, MyContextQualifierB.class})
@ContextServiceDefinition(name = "java:app/ContextBC", qualifiers = {MyContextQualifierB.class, MyContextQualifierC.class})
//@ContextServiceDefinition(name = "java:app/ContextABC", qualifiers = {MyContextQualifierA.class, MyContextQualifierB.class, MyContextQualifierC.class})
@Stateless
public class VirtualMESSample {

    @Resource(lookup = "java:app/VirtMES")
    private ManagedExecutorService mes;

    @Resource
    private ManagedExecutorService defaultMes;

    @Resource(lookup = "java:app/ContextJNDI")
    ContextService csJNDI;

    @Inject
    ContextService csDefault;

    @Inject
    @MyContextQualifierA
    ContextService csA;

//    @Inject
//    @MyContextQualifierB
//    ContextService csB;

//    @Inject
//    @MyContextQualifierA
//    @MyContextQualifierB
//    ContextService csAB;

    @Inject
    @MyContextQualifierB
    @MyContextQualifierC
    ContextService csBC;

//    @Inject
//    @MyContextQualifierA
//    @MyContextQualifierB
//    @MyContextQualifierC
//    ContextService csABC;

    public String tryVirtualMes() {
        String msg = "";

        msg += "ContextService with NO qualifiers via JNDI             " + (csJNDI == null ? "WASN'T" : "was") + " successfully injected.\n";
        msg += "ContextService with NO qualifiers                      " + (csDefault == null ? "WASN'T" : "was") + " successfully injected.\n";
        msg += "ContextService with qualifier  MyContextQualifierA     " + (csA == null ? "WASN'T" : "was") + " successfully injected.\n";
//        msg += "ContextService with qualifier  MyContextQualifierB     " + (csB == null ? "WASN'T" : "was") + " successfully injected.\n";
//        msg += "ContextService with qualifiers MyContextQualifierA+B   " + (csAB == null ? "WASN'T" : "was") + " successfully injected.\n";
        msg += "ContextService with qualifiers MyContextQualifierB+C   " + (csBC == null ? "WASN'T" : "was") + " successfully injected.\n";
//        msg += "ContextService with qualifiers MyContextQualifierA+B+C " + (csABC == null ? "WASN'T" : "was") + " successfully injected.\n";
        try {
            msg += csJNDI.contextualCallable(() -> "csJNDI.contextualCallable()    is fine").call() + "\n";
            msg += csDefault.contextualCallable(() -> "csDefault.contextualCallable() is fine").call() + "\n";
            msg += csA.contextualCallable(() -> "csA.contextualCallable()       is fine").call() + "\n";
            msg += csBC.contextualCallable(() -> "csBC.contextualCallable()      is fine").call() + "\n";
        } catch (Exception ex) {
            msg += "ERROR!!! " + ex.getMessage() + "\n";
        }

        Callable<String> task = () -> {
            System.out.println("In task!");
            Thread.sleep(1_000);
            return Arrays.toString(Thread.currentThread().getStackTrace()) + " => " + (Thread.currentThread().isVirtual() ? "virtual" : "platform");
        };
        Future<String> f1 = mes.submit(task);
        Future<String> f2 = mes.submit(task);
        Future<String> f3 = mes.submit(task);
        Future<String> f4 = defaultMes.submit(task);
        Future<String> f5 = defaultMes.submit(task);
        Future<String> f6 = defaultMes.submit(task);
        try {
            msg += f1.get() + "\n" + f2.get() + "\n" + f3.get() + "\n" + f4.get() + "\n" + f5.get() + "\n" + f6.get();
            return msg;
        } catch (InterruptedException | ExecutionException ex) {
            Logger.getLogger(VirtualMESSample.class.getName()).log(Level.SEVERE, null, ex);
            throw new RuntimeException("Well, it din't go well with virtual threads: " + ex.getMessage(), ex);
        }
    }

}
