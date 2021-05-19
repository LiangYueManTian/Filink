package com.fiberhome.filink.commonstation.handler.impl;//package com.fiberhome.filink.stationserver.handler.impl;
//
//import com.fiberhome.filink.stationserver.entity.xmlbean.format.DataFormat;
//import com.fiberhome.filink.stationserver.entity.xmlbean.format.DataFormatParam;
//import com.fiberhome.filink.stationserver.handler.DataHandler;
//import com.fiberhome.filink.stationserver.util.HexUtil;
//import com.fiberhome.filink.stationserver.util.lockenum.DataType;
//import io.netty.buffer.ByteBuf;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.text.SimpleDateFormat;
//import java.util.Date;
//
///**
// * 日期解码
// *
// * @author CongcaiYu
// */
//public class DateDecoder implements DataHandler {
//
//    private Logger logger = LoggerFactory.getLogger(this.getClass());
//
//    private final int DATE_LENGTH = 7;
//
//    /**
//     * 解析日期
//     *
//     * @param dataFormat DataFormat
//     * @param byteBuf    ByteBuf
//     * @return Object
//     */
//    @Override
//    public Object handle(DataFormat dataFormat, ByteBuf byteBuf) {
//        try {
//            //不是日期
//            if (byteBuf.readableBytes() < DATE_LENGTH) {
//                return HexUtil.bufToInt(byteBuf);
//            }
//            StringBuffer stringBuffer = new StringBuffer();
//            for (DataFormatParam dataFormatParam : dataFormat.getDataFormatParams()) {
//                String dataType = dataFormatParam.getType();
//                //读取dataParam数据
//                ByteBuf dataParamBuf = byteBuf.readBytes(dataFormatParam.getLength());
//                //int类型
//                String param;
//                if (DataType.INT.equalsIgnoreCase(dataType)) {
//                    param = String.valueOf(HexUtil.bufToInt(dataParamBuf));
//                    //String类型
//                } else if (DataType.STRING.equalsIgnoreCase(dataType)) {
//                    param = HexUtil.bufToStr(dataParamBuf);
//                } else {
//                    param = "error";
//                }
//                stringBuffer.append(param + "-");
//            }
//            String timeStr = stringBuffer.toString();
//            String time = timeStr.substring(0, timeStr.length() - 1);
//            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd-hh-mm-ss");
//            Date date = dateFormat.parse(time);
//            return date.getTime();
//        } catch (Exception e) {
//            logger.info("date format failed>>>>>>>>>>>");
//        }
//        return null;
//    }
//}
