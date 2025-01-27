package io.metersphere.system.dto.taskhub;

import io.metersphere.system.domain.ExecTaskItem;
import io.metersphere.validation.groups.Created;
import io.metersphere.validation.groups.Updated;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * @author wx
 */
@Data
public class TaskHubItemDTO extends ExecTaskItem {

    @Schema(description = "业务id")
    private Long num;

    @Schema(description = "任务名称")
    private String taskName;

    @Schema(description = "操作人名称")
    private String userName;

    @Schema(description = "资源池名称")
    private String resourcePoolName;

    @Schema(description = "执行方式")
    private String triggerMode;

    @Schema(description = "排队数")
    private int lineNum;

    @Schema(description = "线程id")
    private String threadId;

    @Schema(description = "项目名称")
    private String projectName;

    @Schema(description = "组织名称")
    private String organizationName;
}
