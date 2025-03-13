package com.ruoyi.workflow.flowable.mapper;


import com.ruoyi.workflow.core.mapper.BaseMapperPlus;
import com.ruoyi.workflow.flowable.domain.WfDeployForm;
import com.ruoyi.workflow.flowable.domain.vo.WfDeployFormVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 流程实例关联表单Mapper接口
 *
 * @author KonBAI
 * @createTime 2022/3/7 22:07
 */
@Mapper
public interface WfDeployFormMapper extends BaseMapperPlus<WfDeployFormMapper, WfDeployForm, WfDeployFormVo> {

}
