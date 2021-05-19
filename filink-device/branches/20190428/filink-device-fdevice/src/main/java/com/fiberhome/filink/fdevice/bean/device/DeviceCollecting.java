package com.fiberhome.filink.fdevice.bean.device;

import com.baomidou.mybatisplus.enums.IdType;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.activerecord.Model;
import com.baomidou.mybatisplus.annotations.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * <p>
 * 我的关注  实体
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019-03-14
 */
@TableName("device_collecting")
@Data
public class DeviceCollecting extends Model<DeviceCollecting> {

    private static final long serialVersionUID = 1L;

    /**
     * 主键
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 设施id
     */
    @TableField("device_id")
    private String deviceId;


    /**
     * 用户id
     */
    @TableField("user_id")
    private String userId;



    @Override
    protected Serializable pkVal() {
        return this.id;
    }

}
