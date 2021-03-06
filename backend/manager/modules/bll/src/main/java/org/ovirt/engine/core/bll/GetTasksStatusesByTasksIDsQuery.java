package org.ovirt.engine.core.bll;

import org.ovirt.engine.core.bll.tasks.TaskManagerUtil;
import org.ovirt.engine.core.common.queries.GetTasksStatusesByTasksIDsParameters;

public class GetTasksStatusesByTasksIDsQuery<P extends GetTasksStatusesByTasksIDsParameters>
        extends QueriesCommandBase<P> {
    public GetTasksStatusesByTasksIDsQuery(P parameters) {
        super(parameters);
    }

    @Override
    protected void executeQueryCommand() {
        getQueryReturnValue().setReturnValue(TaskManagerUtil.pollTasks(getParameters().getTasksIDs()));
    }
}
