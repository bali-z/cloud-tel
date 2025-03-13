package com.ruoyi.workflow.flowable.service;


import com.ruoyi.workflow.core.domain.ProcessQuery;
import com.ruoyi.workflow.core.domain.model.PageQuery;
import com.ruoyi.workflow.core.page.TableDataInfo;
import com.ruoyi.workflow.flowable.domain.vo.WfDeployVo;

import java.util.List;

/**
 * @author KonBAI
 * @createTime 2022/6/30 9:03
 */
public interface IWfDeployService {

    TableDataInfo<WfDeployVo> queryPageList(ProcessQuery processQuery, PageQuery pageQuery);

    TableDataInfo<WfDeployVo> queryPublishList(String processKey, PageQuery pageQuery);

    void updateState(String definitionId, String stateCode);

    String queryBpmnXmlById(String definitionId);

    void deleteByIds(List<String> deployIds);
}
