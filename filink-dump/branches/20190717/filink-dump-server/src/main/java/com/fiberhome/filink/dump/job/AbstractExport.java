package com.fiberhome.filink.dump.job;

import com.fiberhome.filink.bean.QueryCondition;
import com.fiberhome.filink.dump.service.TaskInfoService;
import org.springframework.beans.factory.annotation.Autowired;


/**
 * 列表导出抽象类
 *
 * @author qiqizhu@wistronits.com
 * create on 2019-03-28 21:51
 */
public abstract class AbstractExport {

    @Autowired
    private TaskInfoService exportFeign;
//    /**
//     * 每个文件记录数据的最大条数
//     */
//    @Value(ExportApiConstant.LIST_EXCEL_SIZE)
//    private Integer listExcelSize;
//    /**
//     * 临时文件的路径
//     */
//    @Value(ExportApiConstant.LIST_EXCEL_FILE_PATH)
//    private String listExcelFilePath;
//    /**
//     * 上传文件服务类实体
//     */
//    @Autowired
//    private UploadFile uploadFile;
//    /**
//     * 打包实体
//     */
//    @Autowired
//    private ZipUtil zipUtil;
//    /**
//     * 最大导出条数
//     */
//    @Value(ExportApiConstant.MAX_EXPORT_DATA_SIZE)
//    private Integer maxExportDataSize;
//
//    /**
//     * 导出数据
//     *
//     * @param
//     */
//    @Async
//    public void exportData(Export export,List list) {
//        Map<String, String> map = new HashMap<>();
//        //存入时区信息
////        map.put("timeZone", dump.getTimeZone());
////        map.put("userId", dump.getUserId());
////        ExportApiUtils.RESOURCE.set(map);
//        QueryCondition queryCondition = export.getQueryCondition();
//        PageCondition pageCondition = queryCondition.getPageCondition();
//        pageCondition.setPageSize(listExcelSize);
//        pageCondition.setPageNum(1);
//        pageCondition.setBeginNum((pageCondition.getPageNum() - 1) * pageCondition.getPageSize());
//        //创建该任务文件夹
//        String taskFolderPath = listExcelFilePath + export.getTaskId();
//        File dirFile = new File(taskFolderPath);
//        //如果文件夹不存在，则创建新的文件夹
//        if (!dirFile.exists()) {
//            dirFile.mkdirs();
//        }
//        Date date = new Date();
//        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
//        String time = sdf.format(date);
//        String excelFolderName = export.getListName() + time;
//        export.setExcelFolderName(excelFolderName);
//        export.setTaskFolderPath(taskFolderPath);
//        startExport(export,list);
//    }
//
//    private void startExport(Export export,List list) {
//        try {
//            specificExportData(export,list);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        updateTaskInfo(export,list);
//    }
//
//    /**
//     * 具体导出数据
//     *
//     * @param
//     */
//    private void specificExportData(Export export,List list) throws Exception {
//        QueryCondition queryCondition = export.getQueryCondition();
////        List list = queryData(queryCondition);
//        ExportApiUtils.handelExportDto(export, list);
//        List<List<String>> dataList = export.getDataList();
//        List<String> columnNameList = dataList.get(0);
//        dataList.remove(0);
//        List<List<String>> valueList = dataList;
//        String excelFolderName = export.getExcelFolderName();
//        //生成文件名称
//        String fileName = excelFolderName + "-" + queryCondition.getPageCondition().getPageNum();
//        String excelFolderPath = export.getTaskFolderPath() + "/" + excelFolderName + "/";
//        if (export.getExcelType() == 0) {
//            ListExcelUtil.listExport(columnNameList, valueList, excelFolderPath, fileName);
//        } else {
//            ListExcelUtil.listExportToCsv(columnNameList, valueList, excelFolderPath, fileName);
//        }
//    }
//
//    /**
//     * 更新任务信息
//     */
//    private void updateTaskInfo(Export export,List list) {
//        PageCondition pageCondition = export.getQueryCondition().getPageCondition();
//        Integer pageNum = pageCondition.getPageNum();
//        export.setFileGeneratedNum(export.getFileGeneratedNum() + 1);
//        ExportDto exportDto = new ExportDto();
//        BeanUtils.copyProperties(export, exportDto);
//
//        if (pageNum == (int) Math.ceil((double) export.getDateSize() / (double) listExcelSize)) {
//            String filePath = packageFile(export);
//            exportDto.setFileGeneratedNum(export.getFileNum());
//            exportDto.setFilePath(filePath);
//            //如果更新返回值为true 即更新成功
//            if (exportFeign.updateTaskFileNumById(exportDto)) {
//                //将最原始对象文件数与生成数设置为相等，通知切面此任务已经完成
//                export.setFileGeneratedNum(export.getFileNum());
//            }
//        } else {
//            pageCondition.setPageNum(pageCondition.getPageNum() + 1);
//            pageCondition.setBeginNum((pageCondition.getPageNum() - 1) * pageCondition.getPageSize());
//            startExport(export,list);
//        }
//    }
//
//    /**
//     * 文件上传打包
//     */
//    private String packageFile(Export export) {
//        String taskFolderPath = export.getTaskFolderPath();
//        String excelFolderName = export.getExcelFolderName();
//        String excelFolderPath = taskFolderPath + "/" + excelFolderName;
//        zipUtil.createZip(excelFolderPath, taskFolderPath + "/" + excelFolderName, export.getTaskId());
//        File file = new File(taskFolderPath + "/" + excelFolderName + ".zip");
//        if(export.getDumpSite() == 1) {
//            String filePath = uploadFile.uploadFile(file);
//            //上传文件失败返回null
//            if (StringUtils.isEmpty(filePath)) {
//                throw new FilinkExportException();
//            }
//            return filePath;
//        }
//        return taskFolderPath + "/" + excelFolderName + ".zip";
//    }
//
//
//    /**
//     * 查询需要导出的数据
//     *
//     * @param condition
//     * @return
//     */
//    protected abstract List queryData(QueryCondition condition);
//
//
//    /**
//     * 插入任务信息
//     *
//     * @return
//     */
//    public Export insertTask(ExportDto exportDto, String serverName, String listName,Integer count) {
//        QueryCondition queryCondition = exportDto.getQueryCondition();
//        //拼接服务名称前缀
//        String requestURI = serverName + ExportApiUtils.getRequestUri();
//        exportDto.setMethodPath(requestURI);
//        //存入操作用户
//        exportDto.setUserId(RequestInfoUtils.getUserId());
//        exportDto.setListName(listName);
//        PageCondition pageCondition = queryCondition.getPageCondition();
//        pageCondition.setPageSize(listExcelSize);
//        pageCondition.setPageNum(1);
//        // 查询count等信息  构造任务数据 queryCount可以修改方法 ，
////        Integer count = queryCount(queryCondition);
//        if (count == 0) {
//            throw new FilinkExportNoDataException();
//        }
//        if (count > maxExportDataSize) {
//            throw new FilinkExportDataTooLargeException(String.valueOf(count));
//        }
//        double fileRealNum = (double) count / (double) listExcelSize;
//        int fileNum = (int) (Math.ceil(fileRealNum + fileRealNum * 0.1 + 1));
//        exportDto.setFileNum(fileNum);
//        Result result = exportFeign.addTask(exportDto);
//        int code = result.getCode();
//        if (code == ExportApiConstant.TASK_NUM_TOO_BIG_CODE) {
//            throw new FilinkExportTaskNumTooBigException();
//        }//调用失败
//        else if (code != 0) {
//            throw new FilinkExportException();
//        }
//        Export export = new Export<>();
//        BeanUtils.copyProperties(exportDto, export);
//        export.setDateSize(count);
//        //从请求头中获取时区、token
//        export.setTimeZone(ExportApiUtils.getZone());
//        export.setToken(RequestInfoUtils.getToken());
//        export.setTaskId((String) result.getData());
//        return export;
//    }


    /**
     * 查询数据总条数
     *
     * @param condition
     * @return
     */
    protected abstract Integer queryCount(QueryCondition condition);
}
