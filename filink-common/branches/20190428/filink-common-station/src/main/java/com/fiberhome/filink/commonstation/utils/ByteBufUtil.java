package com.fiberhome.filink.commonstation.utils;

import com.fiberhome.filink.commonstation.exception.ResponseException;
import com.fiberhome.filink.commonstation.constant.DataType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.PooledByteBufAllocator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * byteBuf工具类
 *
 * @author CongcaiYu
 */
public class ByteBufUtil {

    private static Logger logger = LoggerFactory.getLogger(ByteBufUtil.class);

    private static final int BYTE_LENGTH = 1;
    private static final int SHORT_LENGTH = 2;
    private static final int MEDIUM_LENGTH = 3;
    private static final int INT_LENGTH = 4;
    private static final int LONG_LENGTH = 8;

    private static final String UNSIGNED = "unsigned";

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
        if (DataType.INT.equalsIgnoreCase(type) || DataType.UNSIGNED_INT.equalsIgnoreCase(type)) {
            int dataSourceInt = Integer.decode(dataSource);
            if (length == BYTE_LENGTH) {
                dataBuf.writeByte(dataSourceInt);
            } else if (length == SHORT_LENGTH) {
                dataBuf.writeShort(dataSourceInt);
            } else if (length == MEDIUM_LENGTH){
                dataBuf.writeMedium(dataSourceInt);
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
     * 获取整型数据
     *
     * @param length     长度
     * @param dataBuf    字节缓冲对象
     */
    public static String getIntDataSource(Integer length, ByteBuf dataBuf) {
        //有符号整型
        String dataSource;
        if (length == BYTE_LENGTH) {
            dataSource = String.valueOf(dataBuf.readByte());
        } else if (length == SHORT_LENGTH) {
            dataSource = String.valueOf(dataBuf.readShort());
        } else if(length == MEDIUM_LENGTH){
            dataSource = String.valueOf(dataBuf.readMedium());
        } else if (length == INT_LENGTH) {
            dataSource = String.valueOf(dataBuf.readInt());
        } else if (length == LONG_LENGTH) {
            dataSource = String.valueOf(dataBuf.readLong());
        } else {
            throw new ResponseException("dataSourceLength is error!!!!!!!!!!!!!!!!");
        }
        return dataSource;
    }


    /**
     * 创建byteBuf
     *
     * @return ByteBuf
     */
    public static ByteBuf createByteBuf() {
        //创建 池化ByteBuf 实例
        return PooledByteBufAllocator.DEFAULT.buffer();
    }

}
