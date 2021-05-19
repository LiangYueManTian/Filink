package com.fiberhome.filink.rfid.controller.template;

import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.rfid.req.template.RealPortReqDto;
import com.fiberhome.filink.rfid.req.template.RealReqDto;
import com.fiberhome.filink.rfid.req.template.TemplateReqDto;
import com.fiberhome.filink.rfid.service.template.TemplateService;
import mockit.Expectations;
import mockit.Injectable;
import mockit.Tested;
import mockit.integration.junit4.JMockit;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * 模板测试类
 *
 * @author liyj
 * @date 2019/7/5
 */
@RunWith(JMockit.class)
public class TemplateControllerTest {
    /**
     * tested
     */
    @Tested
    private TemplateController templateController;

    /**
     * 服务类
     */
    @Injectable
    private TemplateService templateService;

    /**
     * 创建模板
     *
     * @throws Exception exception
     */
    @Test
    public void createTemplate() throws Exception {
        new Expectations() {
            {
                templateService.createTemplate((TemplateReqDto) any);
                result = ResultUtils.success();
            }
        };
        Result result = templateController.createTemplate(new TemplateReqDto());
        Assert.assertTrue(ResultCode.SUCCESS.equals(result.getCode()));
    }

    /**
     * 查询模板
     *
     * @throws Exception exception
     */
    @Test
    public void queryAllTemplate() throws Exception {
        new Expectations() {
            {
                templateService.queryAllTemplate((TemplateReqDto) any);
                result = ResultUtils.success();
            }
        };
        Result result = templateController.queryAllTemplate(new TemplateReqDto());
        Assert.assertTrue(ResultCode.SUCCESS.equals(result.getCode()));
    }

    /**
     * 查询实景图
     *
     * @throws Exception
     */
    @Test
    public void queryRealPosition() throws Exception {
        new Expectations() {
            {
                templateService.queryRealPosition((RealReqDto) any);
                result = ResultUtils.success();
            }
        };
        Result result = templateController.queryRealPosition(new RealReqDto());
        Assert.assertTrue(ResultCode.SUCCESS.equals(result.getCode()));
    }

    /**
     * 通过框id 查询出放大的实景图信息
     *
     * @throws Exception
     */
    @Test
    public void queryRealPositionByFrameId() throws Exception {

        new Expectations() {
            {
                templateService.queryRealPositionByFrameId(anyString, anyBoolean);
                result = ResultUtils.success();
            }
        };
        Result result = templateController.queryRealPositionByFrameId("frameId");
        Assert.assertTrue(ResultCode.SUCCESS.equals(result.getCode()));
    }


    /**
     * 通过框id 查询出放大的实景图信息
     *
     * @throws Exception
     */
    @Test
    public void queryFormationByFrameId() throws Exception {
        new Expectations() {
            {
                templateService.queryRealPositionByFrameId(anyString, anyBoolean);
                result = ResultUtils.success();
            }
        };
        Result result = templateController.queryFormationByFrameId("frameId");
        Assert.assertTrue(ResultCode.SUCCESS.equals(result.getCode()));
    }

    /**
     * 通过设施id 查询出箱框的信息
     *
     * @throws Exception
     */
    @Test
    public void queryFormationByDeviceId() throws Exception {
        new Expectations() {
            {
                templateService.queryFormationByDeviceId(anyString);
                result = ResultUtils.success();
            }
        };
        Result result = templateController.queryFormationByDeviceId("deviceId");
        Assert.assertTrue(ResultCode.SUCCESS.equals(result.getCode()));
    }

    /**
     * 保存设施和模板的关系
     *
     * @throws Exception
     */
    @Test
    public void saveDeviceAndTempRelation() throws Exception {
        new Expectations() {
            {
                templateService.saveDeviceAndTempRelation((TemplateReqDto) any);
                result = ResultUtils.success();
            }
        };
        Result result = templateController.saveDeviceAndTempRelation(new TemplateReqDto());
        Assert.assertTrue(ResultCode.SUCCESS.equals(result.getCode()));
    }

    /**
     * 查询端口信息
     *
     * @throws Exception
     */
    @Test
    public void queryPortInfoByPortId() throws Exception {
        new Expectations() {
            {
                templateService.queryPortInfoByPortId(anyString);
                result = ResultUtils.success();
            }
        };
        Result result = templateController.queryPortInfoByPortId("portId");
        Assert.assertTrue(ResultCode.SUCCESS.equals(result.getCode()));
    }

    /**
     * 查询端口信息
     *
     * @throws Exception exception
     */
    @Test
    public void queryTemplateTop() throws Exception {
        new Expectations() {
            {
                templateService.queryTemplateTop(anyString);
                result = ResultUtils.success();
            }
        };
        Result result = templateController.queryTemplateTop("deviceId");
        Assert.assertTrue(ResultCode.SUCCESS.equals(result.getCode()));
    }

    /**
     * 实景图 盘上下架
     *
     * @throws Exception
     */
    @Test
    public void updatePortState() throws Exception {
        new Expectations() {
            {
                templateService.updatePortState((RealPortReqDto) any);
                result = ResultUtils.success();
            }
        };
        Result result = templateController.updatePortState(new RealPortReqDto());
        Assert.assertTrue(ResultCode.SUCCESS.equals(result.getCode()));
    }

    /**
     * 修改模板
     *
     * @throws Exception 修改模板
     */
    @Test
    public void updateTemplate() throws Exception {
        new Expectations() {
            {
                templateService.updateTemplate((TemplateReqDto) any);
                result = ResultUtils.success();
            }
        };
        Result result = templateController.updateTemplate(new TemplateReqDto());
        Assert.assertTrue(ResultCode.SUCCESS.equals(result.getCode()));
    }

    /**
     * 删除模板
     *
     * @throws Exception exception
     */
    @Test
    public void deleteTemplate() throws Exception {
        new Expectations() {
            {
                templateService.deleteTemplate((TemplateReqDto) any);
                result = ResultUtils.success();
            }
        };
        Result result = templateController.deleteTemplate(new TemplateReqDto());
        Assert.assertTrue(ResultCode.SUCCESS.equals(result.getCode()));
    }

}