-- 一级菜单
insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('流程管理', 0, 4, 'process', null, '', 1, 0, 'M', '0', '0', '', 'skill', 'admin', NOW(), '', null, '流程管理目录');

-- 记录流程管理ID变量
SET @process_management_id = LAST_INSERT_ID();

insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('办公管理', 0, 5, 'work', null, '', 1, 0, 'M', '0', '0', '', 'job', 'admin', NOW(), '', null, '办公管理目录');

-- 记录办公管理ID变量
SET @office_management_id = LAST_INSERT_ID();

-- 二级菜单
-- 流程管理
insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('流程分类', @process_management_id, 1, 'category', 'workflow/category/index', '', 1, 0, 'C', '0', '0', 'workflow:category:list', 'nested', 'admin', NOW(), '', null, '流程分类菜单');

-- 记录流程分类ID变量
SET @category_id = LAST_INSERT_ID();

insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('表单配置', @process_management_id, 2, 'form', 'workflow/form/index', '', 1, 0, 'C', '0', '0', 'workflow:form:list', 'form', 'admin', NOW(), '', null, '表单配置菜单');

-- 记录表单配置ID变量
SET @form_id = LAST_INSERT_ID();

insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('流程模型', @process_management_id, 3, 'model', 'workflow/model/index', '', 1, 0, 'C', '0', '0', 'workflow:model:list', 'component', 'admin', NOW(), '', null, '流程模型菜单');

-- 记录流程模型ID变量
SET @model_id = LAST_INSERT_ID();

insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('部署管理', @process_management_id, 4, 'deploy', 'workflow/deploy/index', '', 1, 0, 'C', '0', '0', 'workflow:deploy:list', 'example', 'admin', NOW(), '', null, '部署管理菜单');

-- 记录部署管理ID变量
SET @deploy_id = LAST_INSERT_ID();

-- 办公管理
insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('新建流程', @office_management_id, 1, 'create', 'workflow/work/index', '', 1, 0, 'C', '0', '0', 'workflow:process:startList', 'guide', 'admin', NOW(), '', null, '新建流程菜单');

-- 记录新建流程ID变量
SET @create_process_id = LAST_INSERT_ID();

insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('我的流程', @office_management_id, 2, 'own', 'workflow/work/own', '', 1, 0, 'C', '0', '0', 'workflow:process:ownList', 'cascader', 'admin', NOW(), '', null, '我的流程菜单');

-- 记录我的流程ID变量
SET @my_process_id = LAST_INSERT_ID();

insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('待办任务', @office_management_id, 3, 'todo', 'workflow/work/todo', '', 1, 0, 'C', '0', '0', 'workflow:process:todoList', 'time-range', 'admin', NOW(), '', null, '待办任务菜单');

-- 记录待办任务ID变量
SET @todo_task_id = LAST_INSERT_ID();

insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('待签任务', @office_management_id, 4, 'claim', 'workflow/work/claim', '', 1, 0, 'C', '0', '0', 'workflow:process:claimList', 'checkbox', 'admin', NOW(), '', null, '待签任务菜单');

-- 记录待签任务ID变量
SET @claim_task_id = LAST_INSERT_ID();

insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('已办任务', @office_management_id, 5, 'finished', 'workflow/work/finished', '', 1, 0, 'C', '0', '0', 'workflow:process:finishedList', 'checkbox', 'admin', NOW(), '', null, '已办任务菜单');

-- 记录已办任务ID变量
SET @finished_task_id = LAST_INSERT_ID();

insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('抄送我的', @office_management_id, 6, 'copy', 'workflow/work/copy', '', 1, 0, 'C', '0', '0', 'workflow:process:copyList', 'checkbox', 'admin', NOW(), '', null, '抄送我的菜单');

-- 记录抄送我的ID变量
SET @copy_to_me_id = LAST_INSERT_ID();

-- 三级菜单
-- 流程分类管理
insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('分类查询', @category_id, 1, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:category:query', '#', 'admin', NOW(), '', null, '');

insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('分类新增', @category_id, 2, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:category:add', '#', 'admin', NOW(), '', null, '');

insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('分类编辑', @category_id, 3, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:category:edit', '#', 'admin', NOW(), '', null, '');

insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('分类删除', @category_id, 4, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:category:remove', '#', 'admin', NOW(), '', null, '');

-- 表单配置
insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('表单查询', @form_id, 1, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:form:query', '#', 'admin', NOW(), '', null, '');

insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('表单新增', @form_id, 2, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:form:add', '#', 'admin', NOW(), '', null, '');

insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('表单修改', @form_id, 3, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:form:edit', '#', 'admin', NOW(), '', null, '');

insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('表单删除', @form_id, 4, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:form:remove', '#', 'admin', NOW(), '', null, '');

insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('表单导出', @form_id, 5, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:form:export', '#', 'admin', NOW(), '', null, '');

-- 流程模型
insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('模型查询', @model_id, 1, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:model:query', '#', 'admin', NOW(), '', null, '');

insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('模型新增', @model_id, 2, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:model:add', '#', 'admin', NOW(), '', null, '');

insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('模型修改', @model_id, 3, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:model:edit', '#', 'admin', NOW(), '', null, '');

insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('模型删除', @model_id, 4, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:model:remove', '#', 'admin', NOW(), '', null, '');

insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('模型导出', @model_id, 5, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:model:export', '#', 'admin', NOW(), '', null, '');

insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('模型导入', @model_id, 6, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:model:import', '#', 'admin', NOW(), '', null, '');

insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('模型设计', @model_id, 7, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:model:designer', '#', 'admin', NOW(), '', null, '');

insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('模型保存', @model_id, 8, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:model:save', '#', 'admin', NOW(), '', null, '');

insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('流程部署', @model_id, 9, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:model:deploy', '#', 'admin', NOW(), '', null, '');

-- 部署管理
insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('部署查询', @deploy_id, 1, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:deploy:query', '#', 'admin', NOW(), '', null, '');

insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('部署删除', @deploy_id, 2, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:deploy:remove', '#', 'admin', NOW(), '', null, '');

insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('更新状态', @deploy_id, 3, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:deploy:status', '#', 'admin', NOW(), '', null, '');

-- 新建流程
insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('发起流程', @create_process_id, 1, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:process:start', '#', 'admin', NOW(), '', null, '');

insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('新建流程导出', @create_process_id, 2, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:process:startExport', '#', 'admin', NOW(), '', null, '');

-- 我的流程
insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('流程详情', @my_process_id, 1, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:process:query', '#', 'admin', NOW(), '', null, '');

insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('流程删除', @my_process_id, 2, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:process:remove', '#', 'admin', NOW(), '', null, '');

insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('流程取消', @my_process_id, 3, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:process:cancel', '#', 'admin', NOW(), '', null, '');

insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('我的流程导出', @my_process_id, 4, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:process:ownExport', '#', 'admin', NOW(), '', null, '');

-- 待办任务
insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('流程办理', @todo_task_id, 1, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:process:approval', '#', 'admin', NOW(), '', null, '');

insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('待办流程导出', @todo_task_id, 2, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:process:todoExport', '#', 'admin', NOW(), '', null, '');

-- 待签任务
insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('流程签收', @claim_task_id, 1, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:process:claim', '#', 'admin', NOW(), '', null, '');

insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('待签流程导出', @claim_task_id, 2, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:process:claimExport', '#', 'admin', NOW(), '', null, '');

-- 已办任务
insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('流程撤回', @finished_task_id, 1, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:process:revoke', '#', 'admin', NOW(), '', null, '');

insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('已办流程导出', @finished_task_id, 2, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:process:finishedExport', '#', 'admin', NOW(), '', null, '');

-- 抄送我的
insert into sys_menu(menu_name, parent_id, order_num, path, component, query, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, update_by, update_time, remark)
values('抄送流程导出', @copy_to_me_id, 1, '#', '', '', 1, 0, 'F', '0', '0', 'workflow:process:copyExport', '#', 'admin', NOW(), '', null, '');