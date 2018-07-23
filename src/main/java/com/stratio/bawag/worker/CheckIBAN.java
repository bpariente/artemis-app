package com.stratio.bawag.worker;

import com.netflix.conductor.client.worker.Worker;
import com.netflix.conductor.common.metadata.tasks.Task;
import com.netflix.conductor.common.metadata.tasks.TaskResult;
import com.netflix.conductor.common.metadata.tasks.TaskResult.Status;
import com.stratio.bawag.domain.GetEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Slf4j
@Component("checkIbanWorker")
public class CheckIBAN implements Worker {

    private static final String IBAN_INPUT_FIELD = "iban";
    private static final String ENTITY_OUTPUT_FIELD = "entity";
    private static final String EXTERNAL_ENTITY_OUTPUT_VALUE = "";

    private static final String WORKER_TASK_DEF_NAME = "worker_task_check_iban";


    @Autowired
    private GetEntity getEntity;

    @Override
    public String getTaskDefName() {
        return WORKER_TASK_DEF_NAME;
    }

    @Override
    public TaskResult execute(Task task) {
        TaskResult result = new TaskResult(task);
        String iban = (String) task.getInputData().get(IBAN_INPUT_FIELD);
        Optional<String> entity = getEntity.getEntity(iban);
        result.setStatus(entity.map(e -> Status.COMPLETED).orElse(Status.FAILED));
        result.addOutputData(ENTITY_OUTPUT_FIELD, entity.orElse(EXTERNAL_ENTITY_OUTPUT_VALUE));
        return result;
    }
}
