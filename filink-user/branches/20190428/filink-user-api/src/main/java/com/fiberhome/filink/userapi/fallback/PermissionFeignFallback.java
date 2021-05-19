package com.fiberhome.filink.userapi.fallback;

import com.fiberhome.filink.userapi.api.PermissionFeign;
import com.fiberhome.filink.userapi.bean.Permission;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * 查询部门信息
 * @author xuangong
 */

@Slf4j
@Component
public class PermissionFeignFallback implements PermissionFeign{

    @Override
    public List<Permission> queryPermissionByUserIds(List<String> userIds) {
        log.info("queryPermissionByUserIds feign调用熔断》》》》》》》》》》");
        return null;
    }
}
