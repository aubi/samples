# Configuration

Compile in Java 21.

Before deployment, run these commands in asadmin. It will create the required concurrecy resources.

```
create-managed-thread-factory --useVirtualThreads=true --target=domain concurrent/VirtFactory
create-managed-executor-service --useVirtualThreads=true --target=domain concurrent/VirtMES
create-managed-scheduled-executor-service --useVirtualThreads=true --target=domain concurrent/VirtMSES

create-resource-ref --enabled=true --target=server concurrent/VirtFactory
create-resource-ref --enabled=true --target=server concurrent/VirtMES
create-resource-ref --enabled=true --target=server concurrent/VirtMSES
```

Then deploy the app to Payara 7 and follow the links in the front page.
