package com.fiberhome.filink.parts.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.service.IService;
import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.parts.bean.PartInfo;
import com.fiberhome.filink.parts.exception.FilinkPartsException;
import com.fiberhome.filink.parts.constant.PartsI18n;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.userapi.bean.Department;
import com.fiberhome.filink.userapi.bean.User;

/**
 * <p>
 * 服务类
 * </p>
 *
 * @author gzp
 * @since 2019-02-12
 */
public interface PartInfoService extends IService<PartInfo> {

    /**
     * 新增配件
     *
     * @param partInfo 配件信息
     * @return Result
     */
    Result addPart(PartInfo partInfo);

    /**
     * 校验配件名是否重复
     *
     * @param partId   配件id
     * @param partName 配件名
     * @return boolean
     */
    boolean checkPartsName(String partId, String partName);

    /**
     * 修改配件
     *
     * @param partInfo 配件信息
     * @return Result
     */
    Result updateParts(PartInfo partInfo);

    /**
     * 批量删除配件
     *
     * @param partsIds 配件id数组
     * @return Result
     */
    Result deletePartsByIds(String[] partsIds);

    /**
     * 根据id查配件信息
     *
     * @param partsId 配件id
     * @return Result
     * @throws Exception 异常
     */
    Result findPartsById(String partsId) throws Exception;

    /**
     * 模糊查询
     *
     * @param queryCondition
     * @param user
     * @return
     */
    Result queryListByPage(QueryCondition<PartInfo> queryCondition, User user);

    /**
     * 条件查询配件数量
     *
     * @param queryCondition
     * @param user
     * @return
     */
    Integer queryPartsCount(QueryCondition<PartInfo> queryCondition, User user);

    /**
     * 转换为用户对象
     *
     * @param userObj
     * @return
     */
    static User convertObjectToUser(Object userObj) {
        //校验是否有值
        if (userObj == null) {
            throw new FilinkPartsException(I18nUtils.getSystemString(PartsI18n.USER_SERVER_ERROR));
        }
        User user = JSON.parseObject(JSON.toJSONString(userObj), User.class);
        //校验用户信息
        if (user == null || user.getRole() == null) {
            throw new FilinkPartsException(I18nUtils.getSystemString(PartsI18n.USER_AUTH_INFO_ERROR));
        }

        if (user.getDepartment() == null) {
            user.setDepartment(new Department());
        }
        return user;
    }

}
