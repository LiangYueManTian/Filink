package com.fiberhome.filink.stationserver.util;

import com.fiberhome.filink.stationserver.util.lockenum.DataType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * byteBuf工具类
 *
 * @author CongcaiYu
 */
public class ByteBufUtil {

    private static Logger logger = LoggerFactory.getLogger(ByteBufUtil.class);
    /**
     * 字节长度
     */
    private static final int BYTE_LENGTH = 1;
    /**
     * short类型长度
     */
    private static final int SHORT_LENGTH = 2;
    /**
     * int类型长度
     */
    private static final int INT_LENGTH = 4;
    /**
     * long类型长度
     */
    private static final int LONG_LENGTH = 8;

    /**
     * 设置dataBuf
     *
     * @param type       String
     * @param dataSource String
     * @param length     Integer
     * @param dataBuf    ByteBuf
     */
    public static void setDataBuf(String type, String dataSource, Integer length, ByteBuf dataBuf) {
        //int类型
        if (DataType.INT.equalsIgnoreCase(type)) {
            int dataSourceInt = Integer.decode(dataSource);
            if (length == BYTE_LENGTH) {
                dataBuf.writeByte(dataSourceInt);
            } else if (length == SHORT_LENGTH) {
                dataBuf.writeShort(dataSourceInt);
            } else if (length == INT_LENGTH) {
                dataBuf.writeInt(dataSourceInt);
            } else if (length == LONG_LENGTH) {
                dataBuf.writeLong(dataSourceInt);
            } else {
                logger.error("dataSourceLength is error!!!!!!!!!!!!!!!!");
            }
            //String类型
        } else if (DataType.STRING.equalsIgnoreCase(type)) {
            dataBuf.writeBytes(dataSource.getBytes());
        } else {
            logger.error("data type is error!!!!!!!!!!!!!");
        }
    }


    /**
     * 创建byteBuf
     *
     * @return ByteBuf
     */
    public static ByteBuf createByteBuf() {
        //Unpooled 创建 ByteBuf 实例
        return Unpooled.directBuffer();
    }
}
