package io.metersphere.functional.controller;

import io.metersphere.functional.domain.FunctionalCase;
import io.metersphere.functional.dto.CaseCustomFieldDTO;
import io.metersphere.functional.request.*;
import io.metersphere.functional.result.FunctionalCaseResultCode;
import io.metersphere.functional.utils.FileBaseUtils;
import io.metersphere.project.domain.Notification;
import io.metersphere.project.domain.NotificationExample;
import io.metersphere.project.mapper.NotificationMapper;
import io.metersphere.sdk.constants.CustomFieldType;
import io.metersphere.sdk.constants.TemplateScene;
import io.metersphere.sdk.constants.TemplateScopeType;
import io.metersphere.sdk.util.JSON;
import io.metersphere.system.base.BaseTest;
import io.metersphere.system.controller.handler.ResultHolder;
import io.metersphere.system.domain.CustomField;
import io.metersphere.system.mapper.CustomFieldMapper;
import io.metersphere.system.notice.constants.NoticeConstants;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.util.LinkedMultiValueMap;

import java.nio.charset.StandardCharsets;
import java.util.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
public class FunctionalCaseControllerTests extends BaseTest {

    public static final String FUNCTIONAL_CASE_ADD_URL = "/functional/case/add";
    public static final String DEFAULT_TEMPLATE_FIELD_URL = "/functional/case/default/template/field/";
    public static final String FUNCTIONAL_CASE_DETAIL_URL = "/functional/case/detail/";
    public static final String FUNCTIONAL_CASE_UPDATE_URL = "/functional/case/update";
    public static final String FUNCTIONAL_CASE_EDIT_FOLLOWER_URL = "/functional/case/edit/follower";
    public static final String FUNCTIONAL_CASE_FOLLOWER_URL = "/functional/case/follower/";
    public static final String FUNCTIONAL_CASE_DELETE_URL = "/functional/case/delete";
    public static final String FUNCTIONAL_CASE_LIST_URL = "/functional/case/page";
    public static final String FUNCTIONAL_CASE_BATCH_DELETE_URL = "/functional/case/batch/delete-to-gc";
    public static final String FUNCTIONAL_CASE_TABLE_URL = "/functional/case/custom/field/";
    public static final String FUNCTIONAL_CASE_BATCH_MOVE_URL = "/functional/case/batch/move";
    public static final String FUNCTIONAL_CASE_BATCH_COPY_URL = "/functional/case/batch/copy";
    public static final String FUNCTIONAL_CASE_VERSION_URL = "/functional/case/version/";

    @Resource
    private NotificationMapper notificationMapper;

    @Resource
    private CustomFieldMapper customFieldMapper;

    @Test
    @Order(1)
    @Sql(scripts = {"/dml/init_file_metadata_test.sql"}, config = @SqlConfig(encoding = "utf-8", transactionMode = SqlConfig.TransactionMode.ISOLATED))
    public void testFunctionalCaseAdd() throws Exception {
        //新增
        FunctionalCaseAddRequest request = creatFunctionalCase();
        LinkedMultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        List<MockMultipartFile> files = new ArrayList<>();
        paramMap.add("request", JSON.toJSONString(request));
        paramMap.add("files", files);
        MvcResult mvcResult = this.requestMultipartWithOkAndReturn(FUNCTIONAL_CASE_ADD_URL, paramMap);
        // 获取返回值
        String returnData = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResultHolder resultHolder = JSON.parseObject(returnData, ResultHolder.class);
        // 返回请求正常
        Assertions.assertNotNull(resultHolder);

        //设置自定义字段
        List<CaseCustomFieldDTO> dtoList = creatCustomFields();
        request.setCustomFields(dtoList);

        //设置文件
        String filePath = Objects.requireNonNull(this.getClass().getClassLoader().getResource("file/test.JPG")).getPath();
        MockMultipartFile file = new MockMultipartFile("file", "file_re-upload.JPG", MediaType.APPLICATION_OCTET_STREAM_VALUE, FileBaseUtils.getFileBytes(filePath));
        files.add(file);

        //设置关联文件
        request.setRelateFileMetaIds(Arrays.asList("relate_file_meta_id_1", "relate_file_meta_id_2"));
        paramMap = new LinkedMultiValueMap<>();
        paramMap.add("request", JSON.toJSONString(request));
        paramMap.add("files", files);

        MvcResult functionalCaseMvcResult = this.requestMultipartWithOkAndReturn(FUNCTIONAL_CASE_ADD_URL, paramMap);

        String functionalCaseData = functionalCaseMvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResultHolder functionalCaseResultHolder = JSON.parseObject(functionalCaseData, ResultHolder.class);
        FunctionalCase functionalCase = JSON.parseObject(JSON.toJSONString(functionalCaseResultHolder.getData()), FunctionalCase.class);

        NotificationExample notificationExample = new NotificationExample();
        notificationExample.createCriteria().andResourceNameEqualTo(functionalCase.getName()).andResourceTypeEqualTo(NoticeConstants.TaskType.FUNCTIONAL_CASE_TASK);
        List<Notification> notifications = notificationMapper.selectByExampleWithBLOBs(notificationExample);
        String jsonString = JSON.toJSONString(notifications);
        System.out.println(jsonString);
    }


    @Test
    @Order(2)
    public void testDefaultTemplateField() throws Exception {
        MvcResult mvcResult = this.requestGetWithOkAndReturn(DEFAULT_TEMPLATE_FIELD_URL + DEFAULT_PROJECT_ID);
        // 获取返回值
        String returnData = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResultHolder resultHolder = JSON.parseObject(returnData, ResultHolder.class);
        // 返回请求正常
        Assertions.assertNotNull(resultHolder);
    }


    @Test
    @Order(3)
    public void testFunctionalCaseDetail() throws Exception {
        assertErrorCode(this.requestGet(FUNCTIONAL_CASE_DETAIL_URL + "ERROR_TEST_FUNCTIONAL_CASE_ID"), FunctionalCaseResultCode.FUNCTIONAL_CASE_NOT_FOUND);
        MvcResult mvcResult = this.requestGetWithOkAndReturn(FUNCTIONAL_CASE_DETAIL_URL + "TEST_FUNCTIONAL_CASE_ID");
        // 获取返回值
        String returnData = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResultHolder resultHolder = JSON.parseObject(returnData, ResultHolder.class);
        // 返回请求正常
        Assertions.assertNotNull(resultHolder);
    }

    private List<CaseCustomFieldDTO> creatCustomFields() {
        insertCustomField();
        List<CaseCustomFieldDTO> list = new ArrayList<>();
        CaseCustomFieldDTO customFieldDTO = new CaseCustomFieldDTO();
        customFieldDTO.setFieldId("custom_field_id_1");
        customFieldDTO.setValue("custom_field_value_1");
        list.add(customFieldDTO);
        CaseCustomFieldDTO customFieldDTO2 = new CaseCustomFieldDTO();
        customFieldDTO2.setFieldId("custom_field_id_2");
        customFieldDTO2.setValue("custom_field_value_2");
        list.add(customFieldDTO2);
        return list;
    }

    private void insertCustomField() {
        CustomField customField = new CustomField();
        customField.setId("custom_field_id_1");
        customField.setName("test_custom_field_id_1");
        customField.setType(CustomFieldType.INPUT.toString());
        customField.setScene(TemplateScene.FUNCTIONAL.name());
        customField.setCreateUser("gyq");
        customField.setCreateTime(System.currentTimeMillis());
        customField.setUpdateTime(System.currentTimeMillis());
        customField.setRefId("test_custom_field_id_1");
        customField.setScopeId(DEFAULT_PROJECT_ID);
        customField.setScopeType(TemplateScopeType.PROJECT.name());
        customField.setInternal(false);
        customField.setEnableOptionKey(false);
        customField.setRemark("1");
        customFieldMapper.insertSelective(customField);
    }

    private FunctionalCaseAddRequest creatFunctionalCase() {
        FunctionalCaseAddRequest functionalCaseAddRequest = new FunctionalCaseAddRequest();
        functionalCaseAddRequest.setProjectId(DEFAULT_PROJECT_ID);
        functionalCaseAddRequest.setTemplateId("default_template_id");
        functionalCaseAddRequest.setName("测试用例新增");
        functionalCaseAddRequest.setCaseEditType("STEP");
        functionalCaseAddRequest.setModuleId("default_module_id");
        return functionalCaseAddRequest;
    }


    @Test
    @Order(3)
    public void testUpdateFunctionalCase() throws Exception {
        FunctionalCaseEditRequest request = creatEditRequest();
        //设置自定义字段
        List<CaseCustomFieldDTO> list = updateCustomFields(request);
        request.setCustomFields(list);
        LinkedMultiValueMap<String, Object> paramMap = new LinkedMultiValueMap<>();
        List<MockMultipartFile> files = new ArrayList<>();
        paramMap.add("request", JSON.toJSONString(request));
        paramMap.add("files", files);
        MvcResult mvcResult = this.requestMultipartWithOkAndReturn(FUNCTIONAL_CASE_UPDATE_URL, paramMap);
        String returnData = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResultHolder resultHolder = JSON.parseObject(returnData, ResultHolder.class);
        Assertions.assertNotNull(resultHolder);

        //设置删除文件id
        request.setDeleteFileMetaIds(Arrays.asList("delete_file_meta_id_1"));
        paramMap = new LinkedMultiValueMap<>();
        paramMap.add("request", JSON.toJSONString(request));
        paramMap.add("files", files);
        MvcResult updateResult = this.requestMultipartWithOkAndReturn(FUNCTIONAL_CASE_UPDATE_URL, paramMap);
        String updateReturnData = updateResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResultHolder updateResultHolder = JSON.parseObject(updateReturnData, ResultHolder.class);
        Assertions.assertNotNull(updateResultHolder);

    }

    private List<CaseCustomFieldDTO> updateCustomFields(FunctionalCaseEditRequest editRequest) {
        List<CaseCustomFieldDTO> list = new ArrayList<>() {{
            add(new CaseCustomFieldDTO() {{
                setFieldId("custom_field_id_1");
                setValue("测试更新");
            }});
            add(new CaseCustomFieldDTO() {{
                setFieldId("custom_field_id_2");
                setValue("更新时存在新字段");
            }});
        }};
        return list;
    }

    private FunctionalCaseEditRequest creatEditRequest() {
        FunctionalCaseEditRequest editRequest = new FunctionalCaseEditRequest();
        editRequest.setProjectId(DEFAULT_PROJECT_ID);
        editRequest.setTemplateId("default_template_id");
        editRequest.setName("测试用例编辑");
        editRequest.setCaseEditType("STEP");
        editRequest.setModuleId("default_module_id");
        editRequest.setId("TEST_FUNCTIONAL_CASE_ID");
        editRequest.setSteps("");
        return editRequest;
    }


    @Test
    @Order(4)
    public void testEditFollower() throws Exception {
        FunctionalCaseFollowerRequest functionalCaseFollowerRequest = new FunctionalCaseFollowerRequest();
        functionalCaseFollowerRequest.setFunctionalCaseId("TEST_FUNCTIONAL_CASE_ID");
        functionalCaseFollowerRequest.setUserId("admin");
        //关注
        MvcResult mvcResult = this.requestPostWithOkAndReturn(FUNCTIONAL_CASE_EDIT_FOLLOWER_URL, functionalCaseFollowerRequest);
        String returnData = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResultHolder resultHolder = JSON.parseObject(returnData, ResultHolder.class);
        Assertions.assertNotNull(resultHolder);
        //获取关注人
        MvcResult followerMvcResult = this.requestGetWithOkAndReturn(FUNCTIONAL_CASE_FOLLOWER_URL + "TEST_FUNCTIONAL_CASE_ID");
        String followerReturnData = followerMvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResultHolder followerResultHolder = JSON.parseObject(followerReturnData, ResultHolder.class);
        Assertions.assertNotNull(followerResultHolder);

        //取消关注
        MvcResult editMvcResult = this.requestPostWithOkAndReturn(FUNCTIONAL_CASE_EDIT_FOLLOWER_URL, functionalCaseFollowerRequest);
        String editReturnData = editMvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResultHolder editResultHolder = JSON.parseObject(editReturnData, ResultHolder.class);
        Assertions.assertNotNull(editResultHolder);
        //获取关注人
        MvcResult editFollowerMvcResult = this.requestGetWithOkAndReturn(FUNCTIONAL_CASE_FOLLOWER_URL + "TEST_FUNCTIONAL_CASE_ID");
        String editFollowerReturnData = editFollowerMvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResultHolder editFollowerResultHolder = JSON.parseObject(editFollowerReturnData, ResultHolder.class);
        Assertions.assertNotNull(editFollowerResultHolder);
    }

    @Test
    @Order(5)
    public void testGetPageList() throws Exception {
        FunctionalCasePageRequest request = new FunctionalCasePageRequest();
        request.setProjectId("test_project_id");
        request.setCurrent(1);
        request.setPageSize(10);
        this.requestPost(FUNCTIONAL_CASE_LIST_URL, request);
        request.setProjectId(DEFAULT_PROJECT_ID);
        request.setSort(new HashMap<>() {{
            put("createTime", "desc");
        }});
        MvcResult mvcResult = this.requestPostWithOkAndReturn(FUNCTIONAL_CASE_LIST_URL, request);
        String returnData = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResultHolder resultHolder = JSON.parseObject(returnData, ResultHolder.class);
        Assertions.assertNotNull(resultHolder);

        //自定义字段 测试
        Map<String, Object> map = new HashMap<>();
        map.put("custom", Arrays.asList(new LinkedHashMap() {{
            put("id", "TEST_FIELD_ID");
            put("operator", "in");
            put("value", "222");
            put("type", "List");
        }}));
        request.setCombine(map);
        this.requestPostWithOkAndReturn(FUNCTIONAL_CASE_LIST_URL, request);
    }


    @Test
    @Order(19)
    public void testDeleteFunctionalCase() throws Exception {
        FunctionalCaseDeleteRequest request = new FunctionalCaseDeleteRequest();
        request.setId("TEST_FUNCTIONAL_CASE_ID");
        request.setDeleteAll(false);
        request.setProjectId(DEFAULT_PROJECT_ID);
        MvcResult mvcResult = this.requestPostWithOkAndReturn(FUNCTIONAL_CASE_DELETE_URL, request);
        String returnData = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResultHolder resultHolder = JSON.parseObject(returnData, ResultHolder.class);
        Assertions.assertNotNull(resultHolder);

        request.setId("TEST_FUNCTIONAL_CASE_ID_1");
        request.setDeleteAll(false);
        this.requestPostWithOkAndReturn(FUNCTIONAL_CASE_DELETE_URL, request);
        request.setId("TEST_FUNCTIONAL_CASE_ID_3");
        request.setDeleteAll(true);
        this.requestPostWithOkAndReturn(FUNCTIONAL_CASE_DELETE_URL, request);
    }


    @Test
    @Order(20)
    public void testBatchDelete() throws Exception {
        FunctionalCaseBatchRequest request = new FunctionalCaseBatchRequest();
        request.setProjectId(DEFAULT_PROJECT_ID);
        request.setSelectAll(false);
        this.requestPostWithOkAndReturn(FUNCTIONAL_CASE_BATCH_DELETE_URL, request);

        request.setSelectIds(Arrays.asList("TEST_FUNCTIONAL_CASE_ID_5", "TEST_FUNCTIONAL_CASE_ID_7"));
        this.requestPostWithOkAndReturn(FUNCTIONAL_CASE_BATCH_DELETE_URL, request);
        request.setSelectAll(true);
        request.setExcludeIds(Arrays.asList("TEST_FUNCTIONAL_CASE_ID_2"));
        this.requestPostWithOkAndReturn(FUNCTIONAL_CASE_BATCH_DELETE_URL, request);
    }

    @Test
    @Order(7)
    public void testTableCustomField() throws Exception {
        this.requestGetWithOkAndReturn(FUNCTIONAL_CASE_TABLE_URL + DEFAULT_PROJECT_ID);
    }

    @Test
    @Order(4)
    public void testBatchMove() throws Exception {
        FunctionalCaseBatchMoveRequest request = new FunctionalCaseBatchMoveRequest();
        request.setProjectId(DEFAULT_PROJECT_ID);
        request.setModuleId("TEST_MOVE_MODULE_ID");
        request.setSelectAll(false);
        this.requestPostWithOkAndReturn(FUNCTIONAL_CASE_BATCH_MOVE_URL, request);
        request.setSelectAll(true);
        this.requestPostWithOkAndReturn(FUNCTIONAL_CASE_BATCH_MOVE_URL, request);
    }


    @Test
    @Order(2)
    public void testBatchCopy() throws Exception {
        FunctionalCaseBatchMoveRequest request = new FunctionalCaseBatchMoveRequest();
        request.setProjectId(DEFAULT_PROJECT_ID);
        request.setModuleId("TEST_MOVE_MODULE_ID");
        request.setSelectIds(Arrays.asList("TEST"));
        request.setSelectAll(false);
        this.requestPostWithOkAndReturn(FUNCTIONAL_CASE_BATCH_COPY_URL, request);
        request.setSelectIds(new ArrayList<>());
        request.setSelectAll(true);
        this.requestPostWithOkAndReturn(FUNCTIONAL_CASE_BATCH_COPY_URL, request);
    }

    @Test
    @Order(2)
    public void testVersion() throws Exception {
        MvcResult mvcResult = this.requestGetWithOkAndReturn(FUNCTIONAL_CASE_VERSION_URL + "TEST_FUNCTIONAL_CASE_ID");
        String returnData = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResultHolder resultHolder = JSON.parseObject(returnData, ResultHolder.class);
        Assertions.assertNotNull(resultHolder);
    }

}