package net.aubrecht.concurrencyvirtual;

import jakarta.ejb.EJB;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.aubrecht.concurrencyvirtual.service.FactorySample;
import net.aubrecht.concurrencyvirtual.service.MESSample;
import net.aubrecht.concurrencyvirtual.service.MSESSample;
import net.aubrecht.concurrencyvirtual.service.VirtualMESSample;

/**
 * Generate reports from various types of Concurrency resources.
 *
 * @author aubi
 */
@Path("test")
public class VirtualThreadTestsResource {
    @EJB
    private VirtualMESSample service;

    @EJB
    private MESSample defaultService;

    @EJB
    private MSESSample scheduledService;
    @EJB
    private FactorySample factoryService;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public Response test() {
        String report
                = generateReport(false);
        return Response
                .ok(report)
                .build();
    }

    @GET
    @Path("simpler")
    @Produces(MediaType.TEXT_PLAIN)
    public Response testSimpler() {
        String report
                = generateReport(true);
        return Response
                .ok(report)
                .build();
    }

    private String generateReport(boolean simpler) {
        String report
                = "ManagedExecutorService\n"
                + "======================\n"
                + defaultService.tryExectutorService(simpler)
                + "\n\n"
                + "ManagedScheduledExecutorService\n"
                + "===============================\n"
                + scheduledService.tryExectutorService(simpler)
                + "\n\n"
                + "ManagedThreadFactory\n"
                + "====================\n"
                + factoryService.tryExectutorService(simpler);
        return report;
    }

    @GET
    @Path("dependency")
    @Produces(MediaType.TEXT_PLAIN)
    public Response testDependency() {
        return Response
                .ok(service.tryVirtualMes())
                .build();
    }

    @GET
    @Path("default")
    @Produces(MediaType.TEXT_PLAIN)
    public Response testDefault() {
        return Response
                .ok(defaultService.tryExectutorService(false))
                .build();
    }

    @GET
    @Path("scheduled")
    @Produces(MediaType.TEXT_PLAIN)
    public Response testScheduled() {
        return Response
                .ok(scheduledService.tryExectutorService(false))
                .build();
    }

    @GET
    @Path("factory")
    @Produces(MediaType.TEXT_PLAIN)
    public Response testFactory() {
        return Response
                .ok(factoryService.tryExectutorService(false))
                .build();
    }
}
