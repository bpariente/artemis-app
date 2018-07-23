package com.stratio.bawag.worker;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import com.stratio.bawag.domain.Customer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component("filterCustomersByAddressesWorker")
public class FilterCustomersByAddresses implements Worker {

    @Autowired
    private ObjectMapper objectMapper;

    private static final String GIVEN_ADDRESS_INPUT_FIELD = "givenAddress";

    private static final String CUSTOMERS_INPUT_FIELD = "customers";

    private static final String MATCHING_CUSTOMERS_OUTPUT_FIELD = "matchingCustomers";

    private static final String WORKER_TASK_DEF_NAME = "worker_task_filter_customers_by_addresses";

    @Override
    public String getTaskDefName() {
        return WORKER_TASK_DEF_NAME;
    }

    @Override
    public TaskResult execute(Task task) {

        TaskResult result = new TaskResult(task);

        List<Customer> customers = Arrays.asList(objectMapper.convertValue(task.getInputData().get(CUSTOMERS_INPUT_FIELD), Customer[].class));
        String givenAddress = (String) task.getInputData().get(GIVEN_ADDRESS_INPUT_FIELD);

        List<Customer> matchingCustomers = customers.stream().filter(customer -> givenAddress.equals(customer.getAddress())).collect(Collectors.toList());

        task.setOutputData(Collections.singletonMap(MATCHING_CUSTOMERS_OUTPUT_FIELD, matchingCustomers));
        task.setStatus(Task.Status.COMPLETED);
        return result;
    }
}
