package com.ruoyi.workflow.flowable.mapper;


import com.ruoyi.workflow.core.mapper.BaseMapperPlus;
import com.ruoyi.workflow.flowable.domain.WfCategory;
import com.ruoyi.workflow.flowable.domain.vo.WfCategoryVo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 流程分类Mapper接口
 *
 * @author KonBAI
 * @date 2022-01-15
 */
@Mapper
public interface WfCategoryMapper extends BaseMapperPlus<WfCategoryMapper, WfCategory, WfCategoryVo> {

}
