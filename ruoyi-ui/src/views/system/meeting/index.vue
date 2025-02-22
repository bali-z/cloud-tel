<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="会议议题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入会议议题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="会议状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择会议状态" clearable>
          <el-option
            v-for="dict in dict.type.sys_meeting_status"
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
          value-format="yyyy-MM-dd HH:mm"
          placeholder="请选择计划开始">
        </el-date-picker>
      </el-form-item>
      <el-form-item label="计划结束" prop="planEndTime">
        <el-date-picker clearable
          v-model="queryParams.planEndTime"
          type="datetime"
          value-format="yyyy-MM-dd HH:mm"
          placeholder="请选择计划结束">
        </el-date-picker>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:meeting:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:meeting:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:meeting:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:meeting:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="meetingList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="会议号" align="center" prop="meetingId" />
      <el-table-column label="会议议题" align="center" prop="title" />
      <el-table-column label="会议密码" align="center" prop="password" />
      <el-table-column label="会议状态" align="center" prop="status">
        <template slot-scope="scope">
          <dict-tag :options="dict.type.sys_meeting_status" :value="scope.row.status"/>
        </template>
      </el-table-column>
      <el-table-column label="计划开始" align="center" prop="planStartTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.planStartTime, '{y}-{m}-{d} {h}:{m}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="计划结束" align="center" prop="planEndTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.planEndTime, '{y}-{m}-{d} {h}:{m}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="实际开始" align="center" prop="actStartTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.actStartTime, '{y}-{m}-{d} {h}:{m}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="实际结束" align="center" prop="actEndTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.actEndTime, '{y}-{m}-{d} {h}:{m}') }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:meeting:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:meeting:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改系统会议对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
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
            value-format="yyyy-MM-dd HH:mm"
            placeholder="请选择计划开始">
          </el-date-picker>
        </el-form-item>
        <el-form-item label="计划结束" prop="planEndTime">
          <el-date-picker clearable
            v-model="form.planEndTime"
            type="datetime"
            value-format="yyyy-MM-dd HH:mm"
            placeholder="请选择计划结束">
          </el-date-picker>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listMeeting, getMeeting, delMeeting, addMeeting, updateMeeting } from "@/api/system/meeting";

export default {
  name: "Meeting",
  dicts: ['sys_meeting_status'],
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 系统会议表格数据
      meetingList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: null,
        holdUserId: null,
        status: null,
        planStartTime: null,
        planEndTime: null,
        actStartTime: null,
        actEndTime: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        title: [
          { required: true, message: "会议议题不能为空", trigger: "blur" }
        ],
        holdUserId: [
          { required: true, message: "会议主不能为空", trigger: "blur" }
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
    };
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询系统会议列表 */
    getList() {
      this.loading = true;
      listMeeting(this.queryParams).then(response => {
        this.meetingList = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },
    // 取消按钮
    cancel() {
      this.open = false;
      this.reset();
    },
    // 表单重置
    reset() {
      this.form = {
        meetingId: null,
        title: null,
        holdUserId: null,
        status: null,
        planStartTime: null,
        planEndTime: null,
        actStartTime: null,
        actEndTime: null
      };
      this.resetForm("form");
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm");
      this.handleQuery();
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.meetingId)
      this.single = selection.length!==1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset();
      this.open = true;
      this.title = "添加系统会议";
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset();
      const meetingId = row.meetingId || this.ids
      getMeeting(meetingId).then(response => {
        this.form = response.data;
        this.open = true;
        this.title = "修改系统会议";
      });
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.meetingId != null) {
            updateMeeting(this.form).then(response => {
              this.$modal.msgSuccess("修改成功");
              this.open = false;
              this.getList();
            });
          } else {
            addMeeting(this.form).then(response => {
              this.$modal.msgSuccess("新增成功");
              this.open = false;
              this.getList();
            });
          }
        }
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const meetingIds = row.meetingId || this.ids;
      this.$modal.confirm('是否确认删除系统会议编号为"' + meetingIds + '"的数据项？').then(function() {
        return delMeeting(meetingIds);
      }).then(() => {
        this.getList();
        this.$modal.msgSuccess("删除成功");
      }).catch(() => {});
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/meeting/export', {
        ...this.queryParams
      }, `meeting_${new Date().getTime()}.xlsx`)
    }
  }
};
</script>
