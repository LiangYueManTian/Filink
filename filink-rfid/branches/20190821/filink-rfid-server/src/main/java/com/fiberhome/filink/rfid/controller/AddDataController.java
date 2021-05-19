package com.fiberhome.filink.rfid.controller;

import com.fiberhome.filink.bean.NineteenUUIDUtils;
import com.fiberhome.filink.rfid.bean.template.RealPosition;
import com.fiberhome.filink.rfid.dao.template.TemplateDao;
import com.fiberhome.filink.rfid.resp.template.RealRspDto;
import com.fiberhome.filink.rfid.service.opticcable.OpticCableSectionInfoService;
import com.google.common.collect.Lists;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * AddDataController
 * todo: 测试代码 造千万脏数据
 *
 * @author congcongsun2@wistronits.com
 * @since 2019/7/10
 */
@RestController
@RequestMapping("/addDataController")
public class AddDataController {

    @Autowired
    private AddDataService addDataService;
    @Autowired
    private OpticCableSectionInfoService opticCableSectionInfoService;

    /**
     * 成端的额
     */
    @GetMapping("/addData")
    public void addData() {
        addDataService.addData();
    }

    /**
     * 跳接
     */
    @GetMapping("/addDataByJump")
    public void addDataByJump() {
        addDataService.addDataByJump();
    }

    /**
     * 熔纤
     */
    @GetMapping("/addDataByCore")
    public void addDataByCore() {
        addDataService.addDataByCore();
    }


    @Autowired
    private TemplateDao templateDao;

    /**
     * 端口数据
     *
     * @return
     */
    @GetMapping("/addport")
    public void createBadPortData() {
        List<String> list = new ArrayList<>();
        list.add("KmrJR83WVTLjNApYyFK");
        List<RealRspDto> realRspDtos = templateDao.queryPortReal(list);
        RealRspDto realRspDto = realRspDtos.get(0);
        //开始造脏数据

        List<Integer> li = Lists.newArrayList();
        for (int i = 0; i < 10; i++) {
            li.add(i);
        }

        // 1000*1000=100 0000 数据
        li.parallelStream().forEach(obj -> {
            int m = obj * 1000;
            for (int i = m; i < (m + 1000); i++) {
                List<RealPosition> port = Lists.newArrayList();
                for (int j = 0; j < 1000; j++) {
                    RealRspDto por1t = new RealRspDto();
                    BeanUtils.copyProperties(realRspDto, por1t);
                    por1t.setId(NineteenUUIDUtils.uuid());
                    por1t.setParentId(NineteenUUIDUtils.uuid());
                    port.add(por1t);
                }
                templateDao.saveRealPositionPort(port);
                System.out.println("开始插入" + i);
            }
        });
    }


}
