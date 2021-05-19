package com.fiberhome.filink.about.controller;


import com.fiberhome.filink.about.bean.About;
import com.fiberhome.filink.about.exception.AboutParamsException;
import com.fiberhome.filink.about.service.AboutService;
import com.fiberhome.filink.bean.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 关于  前端控制器
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019-03-06
 */
@RestController
@RequestMapping("/about")
public class AboutController {
    /**
     * 注入关于逻辑层
     */
    @Autowired
    private AboutService aboutService;

    /**
     * 获取关于信息
     *
     * @return 关于信息
     */
    @GetMapping("/get")
    public Result getAbout() {
        return aboutService.getAbout();
    }

    /**
     * 根据id更新关于信息
     *
     * @param about
     * @return 更新结果
     */
    @PostMapping("/update")
    public Result updateAbout(@RequestBody About about) {
        //检验参数
        if (StringUtils.isEmpty(about)  || StringUtils.isEmpty(about.getLicenseAuthorize())) {
            throw new AboutParamsException();
        }
        return aboutService.updateAboutById(about);
    }

}
