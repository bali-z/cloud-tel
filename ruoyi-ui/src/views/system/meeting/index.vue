<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryRef" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="会议议题" prop="title">
        <el-input
            v-model="queryParams.title"
            placeholder="请输入会议议题"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="会议密码" prop="password">
        <el-input
            v-model="queryParams.password"
            placeholder="请输入会议密码"
            clearable
            @keyup.enter="handleQuery"
        />
      </el-form-item>
      <el-form-item label="会议状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择会议状态" clearable>
          <el-option
              v-for="dict in sys_meeting_status"
              :key="dict.value"
              :label="dict.label"
              :value="dict.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="计划开始" prop="planStartTime">
        <el-date-picker clearable
                        v-model="queryParams.planStartTime"
                        type="datetime"
                        value-format="YYYY-MM-DD HH:mm"
                        placeholder="请选择计划开始">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="计划结束" prop="planEndTime">
        <el-date-picker clearable
                        v-model="queryParams.planEndTime"
                        type="datetime"
                        value-format="YYYY-MM-DD HH:mm"
                        placeholder="请选择计划结束">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
            type="primary"
            plain
            icon="Plus"
            @click="handleAdd"
            v-hasPermi="['system:meeting:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="success"
            plain
            icon="Edit"
            :disabled="single"
            @click="handleUpdate"
            v-hasPermi="['system:meeting:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="danger"
            plain
            icon="Delete"
            :disabled="multiple"
            @click="handleDelete"
            v-hasPermi="['system:meeting:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
            type="warning"
            plain
            icon="Download"
            @click="handleExport"
            v-hasPermi="['system:meeting:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar v-model:showSearch="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="meetingList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="会议号" align="center" prop="meetingId" />
      <el-table-column label="会议议题" align="center" prop="title" />
      <el-table-column label="会议密码" align="center" prop="password" />
      <el-table-column label="会议状态" align="center" prop="status">
        <template #default="scope">
          <dict-tag :options="sys_meeting_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="计划开始" align="center" prop="planStartTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.planStartTime, '{y}-{m}-{d} {h}:{m}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="计划结束" align="center" prop="planEndTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.planEndTime, '{y}-{m}-{d} {h}:{m}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="实际开始" align="center" prop="actStartTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.actStartTime, '{y}-{m}-{d} {h}:{m}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="实际结束" align="center" prop="actEndTime" width="180">
        <template #default="scope">
          <span>{{ parseTime(scope.row.actEndTime, '{y}-{m}-{d} {h}:{m}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template #default="scope">
          <el-button link type="primary" icon="Edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:meeting:edit']">修改</el-button>
          <el-button link type="primary" icon="Delete" @click="handleDelete(scope.row)" v-hasPermi="['system:meeting:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
        v-show="total>0"
        :total="total"
        v-model:page="queryParams.pageNum"
        v-model:limit="queryParams.pageSize"
        @pagination="getList"
    />

    <!-- 添加或修改系统会议对话框 -->
    <el-dialog :title="title" v-model="open" width="500px" append-to-body>
      <el-form ref="meetingRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="会议议题" prop="title">
          <el-input v-model="form.title" placeholder="请输入会议议题" />
        </el-form-item>
        <el-form-item label="会议密码" prop="password">
          <el-input v-model="form.password" placeholder="请输入会议密码" />
        </el-form-item>
        <el-form-item label="计划开始" prop="planStartTime">
          <el-date-picker clearable
                          v-model="form.planStartTime"
                          type="datetime"
                          value-format="YYYY-MM-DD HH:mm"
                          placeholder="请选择计划开始">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="计划结束" prop="planEndTime">
          <el-date-picker clearable
                          v-model="form.planEndTime"
                          type="datetime"
                          value-format="YYYY-MM-DD HH:mm"
                          placeholder="请选择计划结束">
          </el-date-picker>
        </el-form-item>
      </el-form>
      <template #footer>
        <div class="dialog-footer">
          <el-button type="primary" @click="submitForm">确 定</el-button>
          <el-button @click="cancel">取 消</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup name="Meeting">
import { listMeeting, getMeeting, delMeeting, addMeeting, updateMeeting } from "@/api/system/meeting";

const { proxy } = getCurrentInstance();
const { sys_meeting_status } = proxy.useDict('sys_meeting_status');

const meetingList = ref([]);
const open = ref(false);
const loading = ref(true);
const showSearch = ref(true);
const ids = ref([]);
const single = ref(true);
const multiple = ref(true);
const total = ref(0);
const title = ref("");

const data = reactive({
  form: {},
  queryParams: {
    pageNum: 1,
    pageSize: 10,
    title: null,
    password: null,
    status: null,
    planStartTime: null,
    planEndTime: null,
  },
  rules: {
    title: [
      { required: true, message: "会议议题不能为空", trigger: "blur" }
    ],
    holdUserId: [
      { required: true, message: "会议发起人不能为空", trigger: "blur" }
    ],
    status: [
      { required: true, message: "会议状态不能为空", trigger: "change" }
    ],
    planStartTime: [
      { required: true, message: "计划开始不能为空", trigger: "blur" }
    ],
    planEndTime: [
      { required: true, message: "计划结束不能为空", trigger: "blur" }
    ],
  }
});

const { queryParams, form, rules } = toRefs(data);

/** 查询系统会议列表 */
function getList() {
  loading.value = true;
  listMeeting(queryParams.value).then(response => {
    meetingList.value = response.rows;
    total.value = response.total;
    loading.value = false;
  });
}

// 取消按钮
function cancel() {
  open.value = false;
  reset();
}

// 表单重置
function reset() {
  form.value = {
    meetingId: null,
    title: null,
    password: null,
    holdUserId: null,
    status: null,
    planStartTime: null,
    planEndTime: null,
    actStartTime: null,
    actEndTime: null
  };
  proxy.resetForm("meetingRef");
}

/** 搜索按钮操作 */
function handleQuery() {
  queryParams.value.pageNum = 1;
  getList();
}

/** 重置按钮操作 */
function resetQuery() {
  proxy.resetForm("queryRef");
  handleQuery();
}

// 多选框选中数据
function handleSelectionChange(selection) {
  ids.value = selection.map(item => item.meetingId);
  single.value = selection.length != 1;
  multiple.value = !selection.length;
}

/** 新增按钮操作 */
function handleAdd() {
  reset();
  open.value = true;
  title.value = "添加系统会议";
}

/** 修改按钮操作 */
function handleUpdate(row) {
  reset();
  const _meetingId = row.meetingId || ids.value
  getMeeting(_meetingId).then(response => {
    form.value = response.data;
    open.value = true;
    title.value = "修改系统会议";
  });
}

/** 提交按钮 */
function submitForm() {
  proxy.$refs["meetingRef"].validate(valid => {
    if (valid) {
      if (form.value.meetingId != null) {
        updateMeeting(form.value).then(response => {
          proxy.$modal.msgSuccess("修改成功");
          open.value = false;
          getList();
        });
      } else {
        addMeeting(form.value).then(response => {
          proxy.$modal.msgSuccess("新增成功");
          open.value = false;
          getList();
        });
      }
    }
  });
}

/** 删除按钮操作 */
function handleDelete(row) {
  const _meetingIds = row.meetingId || ids.value;
  proxy.$modal.confirm('是否确认删除系统会议编号为"' + _meetingIds + '"的数据项？').then(function() {
    return delMeeting(_meetingIds);
  }).then(() => {
    getList();
    proxy.$modal.msgSuccess("删除成功");
  }).catch(() => {});
}

/** 导出按钮操作 */
function handleExport() {
  proxy.download('system/meeting/export', {
    ...queryParams.value
  }, `meeting_${new Date().getTime()}.xlsx`)
}

getList();
</script>
