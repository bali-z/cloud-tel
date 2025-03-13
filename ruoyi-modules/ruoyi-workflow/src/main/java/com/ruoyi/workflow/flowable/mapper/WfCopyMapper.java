package com.ruoyi.workflow.flowable.mapper;


import com.ruoyi.workflow.core.mapper.BaseMapperPlus;
import com.ruoyi.workflow.flowable.domain.WfCopy;
import com.ruoyi.workflow.flowable.domain.vo.WfCopyVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 流程抄送Mapper接口
 *
 * @author KonBAI
 * @date 2022-05-19
 */
@Mapper
public interface WfCopyMapper extends BaseMapperPlus<WfCopyMapper, WfCopy, WfCopyVo> {

}
