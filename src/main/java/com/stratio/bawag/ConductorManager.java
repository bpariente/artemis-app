package com.stratio.bawag;

import com.netflix.conductor.client.http.TaskClient;
import com.netflix.conductor.client.task.WorkflowTaskCoordinator;
import com.netflix.conductor.client.worker.Worker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;

@Configuration
public class ConductorManager {

    @Autowired
    @Qualifier("filterCustomersByAddressesWorker")
    private Worker filterCustomersByAddresses;

    @Autowired
    @Qualifier("checkIbanWorker")
    private Worker checkIban;


    @Value("${conductor.api.url}")
    public String conductorApiUrl;


    @PostConstruct
    public void run() {
        TaskClient taskClient = new TaskClient();
        taskClient.setRootURI(conductorApiUrl);

        //Create WorkflowTaskCoordinator
        WorkflowTaskCoordinator.Builder builder = new WorkflowTaskCoordinator.Builder();
        WorkflowTaskCoordinator coordinator = builder.withWorkers(checkIban, filterCustomersByAddresses).withThreadCount(1).withTaskClient(taskClient).build();

        //Start for polling and execution of the tasks
        coordinator.init();
    }

}
