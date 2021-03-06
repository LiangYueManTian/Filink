package com.fiberhome.filink.userserver.service.impl;

import com.alibaba.fastjson.JSON;
import com.fiberhome.filink.bean.RequestInfoUtils;
import com.fiberhome.filink.bean.Result;
import com.fiberhome.filink.bean.ResultCode;
import com.fiberhome.filink.bean.ResultUtils;
import com.fiberhome.filink.deviceapi.api.DeviceMapConfigFeign;
import com.fiberhome.filink.license.api.LicenseFeign;
import com.fiberhome.filink.license.bean.License;
import com.fiberhome.filink.license.bean.LicenseThresholdFeignBean;
import com.fiberhome.filink.license.enums.OperationTarget;
import com.fiberhome.filink.logapi.bean.AddLogBean;
import com.fiberhome.filink.logapi.constant.LogConstants;
import com.fiberhome.filink.logapi.log.LogProcess;
import com.fiberhome.filink.security.api.SecurityFeign;
import com.fiberhome.filink.security.bean.AccountSecurityStrategy;
import com.fiberhome.filink.server_common.utils.I18nUtils;
import com.fiberhome.filink.systemlanguage.utils.SystemLanguageUtil;
import com.fiberhome.filink.userserver.bean.Department;
import com.fiberhome.filink.userserver.bean.Role;
import com.fiberhome.filink.userserver.bean.User;
import com.fiberhome.filink.userserver.constant.FunctionCodeConstant;
import com.fiberhome.filink.userserver.constant.UserConstant;
import com.fiberhome.filink.userserver.constant.UserResultCode;
import com.fiberhome.filink.userserver.constant.UserI18n;
import com.fiberhome.filink.userserver.dao.DepartmentDao;
import com.fiberhome.filink.userserver.dao.RoleDao;
import com.fiberhome.filink.userserver.dao.UserDao;
import com.fiberhome.filink.userserver.exception.FilinkUserException;
import com.fiberhome.filink.userserver.service.ImportService;
import com.fiberhome.filink.userserver.utils.CheckEmptyUtils;
import com.fiberhome.filink.userserver.utils.NameUtils;
import com.fiberhome.filink.userserver.utils.PasswordUtils;
import com.fiberhome.filink.userserver.utils.UUIDUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * ?????????????????????
 *
 * @author xgong
 */
@Slf4j
@Service
public class ImportServiceImpl implements ImportService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private DepartmentDao departmentDao;

    @Autowired
    private LicenseFeign licenseFeign;

    @Autowired
    private SecurityFeign securityFeign;

    @Autowired
    private LogProcess logProcess;

    @Autowired
    private DeviceMapConfigFeign deviceMapConfigFeign;

    @Autowired
    private SystemLanguageUtil systemLanguageUtil;
    /**
     * ???????????????????????????
     */
    private static final String SUPER_ADMIN_ID = "superAdmin";
    /**
     * ???????????????????????????
     */
    /**
     * ??????????????????
     *
     * @param file ???????????????
     * @return ???????????????
     */
    @Override
    public Result importUserInfo(MultipartFile file) throws IOException, InvalidFormatException {
        if (!file.isEmpty()) {
            InputStream inputStream = file.getInputStream();
            Workbook workbook = null;
            try {
                workbook = WorkbookFactory.create(inputStream);
            } catch (Exception e) {
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.IMPORT_DATA_ERROR));
            }

            if (null == workbook) {
                return ResultUtils.success(ResultCode.SUCCESS);
            }
            Sheet sheet = null;
            Row row = null;
            //??????excel??????????????????
            List<User> users = new ArrayList<>();
            //???????????????????????????????????????????????????????????????
            List<User> userList = userDao.queryAllUser();
            List<Role> roleList = roleDao.queryAllRoles();
            List<Department> departmentList = departmentDao.queryToltalDepartment();
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                sheet = workbook.getSheetAt(i);
                if (sheet == null) {
                    continue;
                }
                removeBlankRow(sheet);
                //????????????????????????
                Result strategyResult = securityFeign.queryAccountSecurity();
                if (strategyResult == null || strategyResult.getData() == null) {
                    return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.STRATEGY_SERVER_ERROR));
                }
                AccountSecurityStrategy strategy = JSON.
                        parseObject(JSON.toJSONString(strategyResult.getData()), AccountSecurityStrategy.class);

                for (int j = sheet.getFirstRowNum() + UserConstant.UP_ROW; j <= sheet.getLastRowNum(); j++) {
                    row = sheet.getRow(j);
                    int beginColoum = 0;
                    //??????????????????????????????????????????
                    Result dataNullResult = checkDataNull(row);
                    if (dataNullResult != null) {
                        return dataNullResult;
                    }
                    //??????????????????????????????????????????????????????
                    Result dataResult = checkData(row, beginColoum, userList, roleList, departmentList);
                    if (dataResult != null) {
                        return dataResult;
                    }
                    //?????????????????????
                    User user = addUserFromExcel(row, beginColoum, departmentList, roleList);
                    //??????????????????????????????
                    Result dataFormatResult = checkDataFormat(user, strategy);
                    if (dataFormatResult != null) {
                        return dataFormatResult;
                    }
                    //?????????????????????????????????
                    Result result = checkRepeatData(user, users);
                    if (result != null) {
                        return result;
                    }
                    users.add(user);
                }
            }
            return batchAddUser(users);
        }
        return ResultUtils.warn(UserResultCode.FILE_IS_NULL, I18nUtils.getSystemString(UserI18n.FILE_IS_NULL));
    }


    /**
     * ??????sheet????????????
     *
     * @param sheet ?????????sheet
     */
    public void removeBlankRow(Sheet sheet) {
        int beginLine = sheet.getFirstRowNum();
        int lastRowNum = sheet.getLastRowNum();
        if (lastRowNum <= UserConstant.DATA_SIZE_IS_ZERO) {
            return;
        }
        boolean flag = false;

        for (int j = beginLine; j <= sheet.getLastRowNum(); j++) {
            Row row = sheet.getRow(j);
            if (row == null) {
                sheet.shiftRows(j + UserConstant.UP_ROW, sheet.getLastRowNum(), UserConstant.DOWN_ROW);
                j--;
                continue;
            }
            flag = false;
            for (Cell c : row) {
                if (c.getCellType() != Cell.CELL_TYPE_BLANK) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                if (j == sheet.getLastRowNum()) {
                    sheet.removeRow(row);
                } else {
                    sheet.shiftRows(j + UserConstant.UP_ROW, sheet.getLastRowNum(), UserConstant.DOWN_ROW);
                    j--;
                }
            }
        }
    }

    /**
     * ??????????????????
     *
     * @param user ????????????
     * @return ????????????
     */
    public Result checkDataFormat(User user, AccountSecurityStrategy strategy) {

        boolean userCodeMatche = user.getUserCode().matches(UserConstant.USER_CODE_PATTERN);
        boolean phoneNumMatche = user.getPhoneNumber().matches(UserConstant.PHONENUMBERPATTERN);
        if (!phoneNumMatche) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.USER_CODE_IS)
                    + user.getUserCode() + I18nUtils.getSystemString(UserI18n.PHONE_NUMBER_ERROR));
        }
        boolean emailMatch = user.getEmail().matches(UserConstant.EMAILPATTERN);
        String emailAccount = user.getEmail();
        if (!emailMatch || emailAccount.length() > UserConstant.EMAIL_MAX_LENGTH) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.USER_CODE_IS)
                    + user.getUserCode() + I18nUtils.getSystemString(UserI18n.EMAIL_ERROR));
        }
        if (user.getAddress() != null) {
            boolean addressMatche = user.getAddress().matches(UserConstant.USER_CODE_PATTERN);
            if (!addressMatche) {
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.USER_CODE_IS)
                        + user.getUserCode() + I18nUtils.getSystemString(UserI18n.ADDRESS_ERROR));
            }
        }
        //???????????????????????????
        if (user.getUserNickname() != null) {
            boolean nickNameMatch = user.getUserNickname().matches(UserConstant.USER_CODE_PATTERN);
            if (!nickNameMatch) {
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.USER_CODE_IS)
                        + user.getUserCode() + I18nUtils.getSystemString(UserI18n.USER_NICK_NAME_ERROR));
            }
        }
        //????????????????????????
        if (user.getUserName() != null) {
            String userName = user.getUserName();
            boolean userNameMatch = userName.matches(UserConstant.USER_CODE_PATTERN);
            if (!userNameMatch) {
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.USER_CODE_IS)
                        + user.getUserCode() + I18nUtils.getSystemString(UserI18n.USER_NAME_ERROR));
            }
            if (userName.length() > UserConstant.MAX_USER_CODE_LENGTH) {
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.USER_CODE_IS)
                        + user.getUserCode() + I18nUtils.getSystemString(UserI18n.USER_NAME_OUT_RANGE));
            }
        }
        //???????????????????????????
        if (user.getUserDesc() != null) {
            boolean userDescMatch = user.getUserDesc().matches(UserConstant.USER_CODE_PATTERN);
            if (!userDescMatch) {
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.USER_CODE_IS)
                        + user.getUserCode() + I18nUtils.getSystemString(UserI18n.USER_DESC_ERROR));
            }
        }
        //??????????????????
        String userStatus = user.getUserStatus();
        if (!UserConstant.START_STATUS_TIP.equals(userStatus) && !UserConstant.STOP_STATUS_TIP.equals(userStatus)) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.USER_CODE_IS)
                    + user.getUserCode() + I18nUtils.getSystemString(UserI18n.UNSUPPORTED_USER_STATUS));
        }
        if (userCodeMatche) {
            String importUserCode = user.getUserCode();
            Integer minLength = strategy.getMinLength();
            if (importUserCode.length() > UserConstant.MAX_USER_CODE_LENGTH || importUserCode.length() < minLength) {
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.USER_CODE_IS)
                        + user.getUserCode() + I18nUtils.getSystemString(UserI18n.USER_CODE_OUT_RANGE));
            }

            //????????????????????????
            String userNickname = user.getUserNickname();
            if (StringUtils.isNotEmpty(userNickname) && userNickname.length() > UserConstant.MAX_USER_NICK_NAME_LENGTH) {
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.USER_CODE_IS)
                        + user.getUserCode() + I18nUtils.getSystemString(UserI18n.USER_NICK_NAME_OUT_RANGE));
            }
            String loginType = user.getLoginType();
            if (!UserConstant.SINGLE_LOGIN_TIP.equals(loginType) && !UserConstant.MORE_LOGIN_TIP.equals(loginType)) {
                return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.USER_CODE_IS)
                        + user.getUserCode() + I18nUtils.getSystemString(UserI18n.UNSUPPORTED_USER_LOGIN_TYPE));
            }
            if (user.getLoginType().equals(UserConstant.SINGLE_LOGIN_TIP)) {
                if (user.getMaxUsers() != UserConstant.ONLY_ONE_DATA) {
                    return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.USER_CODE_IS)
                            + user.getUserCode() + I18nUtils.getSystemString(UserI18n.MAX_USER_NUM_FALL_OUTER_SIDE));
                }
                return null;
            } else {
                if (user.getMaxUsers() > UserConstant.MAX_USER_MAX || user.getMaxUsers() < UserConstant.MAX_USER_MIN) {
                    return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.USER_CODE_IS)
                            + user.getUserCode() + I18nUtils.getSystemString(UserI18n.MAX_USER_NUM_FALL_OUTER_SIDE));
                }
                return null;
            }
        }
        return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.USER_CODE_IS)
                + user.getUserCode() + I18nUtils.getSystemString(UserI18n.DATA_FORMAT_ERROR));
    }

    /**
     * ????????????????????????????????????
     *
     * @param userList ????????????????????????
     * @return
     */
    public Result checkRepeatImportUser(List<User> userList) {

        List<User> userCodeList = new ArrayList<>();

        if (CheckEmptyUtils.collectEmpty(userList)) {
            int userSize = userList.size();
            for (int i = 0; i < userSize - 1; i++) {
                User pairUser = userList.get(i);
                for (int j = i + 1; j < userSize; j++) {
                    User user = userList.get(j);
                    //????????????????????????????????????
                    if (pairUser.getUserCode().equals(user.getUserCode())) {
                        return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.USER_CODE_IS)
                                + user.getUserCode() + I18nUtils.getSystemString(UserI18n.REPEAT_IMPORT));
                    }
                    //???????????????????????????????????????
                    if (pairUser.getPhoneNumber().equals(user.getPhoneNumber())) {
                        return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.PHONE_NUMBER_IS)
                                + user.getPhoneNumber() + I18nUtils.getSystemString(UserI18n.REPEAT_IMPORT));
                    }
                    //????????????????????????????????????
                    if (pairUser.getEmail().equals(user.getEmail())) {
                        return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.EMAIL_IS)
                                + user.getEmail() + I18nUtils.getSystemString(UserI18n.REPEAT_IMPORT));
                    }
                }
            }
        }
        return null;
    }


    /**
     * ????????????????????????
     *
     * @param userList
     * @return
     */
    public Result batchAddUser(List<User> userList) {

        Result repeatResult = checkRepeatImportUser(userList);
        if (repeatResult != null) {
            return repeatResult;
        }

        //???????????????????????????
        try {
            License license = licenseFeign.getCurrentLicense();
            int maxUserName = Integer.parseInt(license.maxUserNum);
            int userNum = userDao.queryAllUser().size();
            if (userNum + userList.size() > maxUserName) {
                return ResultUtils.warn(UserResultCode.USER_NUM_OVER_MAX_USER_NUM,
                        I18nUtils.getSystemString(UserI18n.USER_NUM_OVER_MAX_USER_NUM));
            } else {
                LicenseThresholdFeignBean bean = new LicenseThresholdFeignBean();
                bean.setName(OperationTarget.USER.getValue());
                bean.setNum(userNum + userList.size());
                licenseFeign.synchronousLicenseThreshold(bean);
            }
        } catch (Exception e) {
            return ResultUtils.warn(UserResultCode.USER_NUM_OVER_MAX_USER_NUM,
                    I18nUtils.getSystemString(UserI18n.USER_NUM_OVER_MAX_USER_NUM));
        }

        List<String> userIdList = new ArrayList<>();

        userList.forEach(user -> {
            //???????????????id???UUID???
            String userId = UUIDUtil.getInstance().UUID32();
            userIdList.add(userId);
            String createUserId = RequestInfoUtils.getUserId();
            user.setId(userId);
            user.setCreateUser(createUserId);
            user.setCreateTime(System.currentTimeMillis());
            user.setDeleted(UserConstant.USER_DEFAULT_DELETED);
            user.setPassword(UserConstant.DEFAULT_PWD);
        });

        //??????????????????????????????????????????
        boolean flag = deviceMapConfigFeign.insertConfigBatchUsers(userIdList);
        log.info("Add user configuration information {}", flag);
        if (flag) {
            Integer integer = userDao.batchAddUser(userList);
            if (integer != userList.size()) {
                throw new FilinkUserException(I18nUtils.getSystemString(UserI18n.ADD_USER_FAIL));
            }
        } else {
            return ResultUtils.warn(UserResultCode.DEVICE_SERVER_ERROR,
                    I18nUtils.getSystemString(UserI18n.DEVICE_SERVER_ERROR));
        }


        String userId = RequestInfoUtils.getUserId();
        //??????????????????
        universalLog(userId, FunctionCodeConstant.IMPORT_USER_MODEL, LogConstants.LOG_TYPE_SECURITY);
        return ResultUtils.success(ResultCode.SUCCESS, I18nUtils.getSystemString(UserI18n.IMPORT_USER_INFO_SUCCESS));
    }

    /**
     * ???excel?????????????????????????????????
     *
     * @return
     */
    public User addUserFromExcel(Row row, int beginColoum, List<Department> departmentList, List<Role> roleList) {

        User user = new User();
        //????????????
        Cell userCodeCell = row.getCell(beginColoum++);
        String userCode = String.valueOf(getCellValue(userCodeCell));
        user.setUserCode(NameUtils.removeBlank(userCode));
        //????????????
        Cell userNameCell = row.getCell(beginColoum++);
        String userName = String.valueOf(getCellValue(userNameCell));
        user.setUserName(NameUtils.removeBlank(userName));
        //????????????
        Cell nickNameCell = row.getCell(beginColoum++);
        if (nickNameCell != null) {
            Object nickNameObject = getCellValue(nickNameCell);
            if (nickNameObject != null) {
                String nickName = String.valueOf(nickNameObject);
                user.setUserNickname(NameUtils.removeBlank(nickName));
            }
        }
        //????????????
        Cell userStatusCell = row.getCell(beginColoum++);
        String userStatus = NameUtils.removeBlank(String.valueOf(getCellValue(userStatusCell)));
        user.setUserStatus(userStatus.equals(UserConstant.START_STATUS) ? UserConstant.START_STATUS_TIP : UserConstant.STOP_STATUS_TIP);
        //????????????
        Cell roleCell = row.getCell(beginColoum++);
        String roldId = (String) checkRoleName(roleCell, roleList).getData();
        user.setRoleId(roldId);
        //????????????
        Cell departmentCell = row.getCell(beginColoum++);
        String deptId = (String) checkDepartmentName(departmentCell, departmentList).getData();
        user.setDeptId(deptId);
        //?????????
        Cell phoneCell = row.getCell(beginColoum++);
        String phone = NameUtils.removeBlank(String.valueOf(getCellValue(phoneCell)));
        BigDecimal bigDecimal = new BigDecimal(phone);
        String phoneNumber = bigDecimal.toPlainString();
        user.setPhoneNumber(phoneNumber);
        //??????
        Cell emailCell = row.getCell(beginColoum++);
        if (emailCell == null) {
            user.setEmail(null);
        } else {
            String email = NameUtils.removeBlank(String.valueOf(getCellValue(emailCell)));
            user.setEmail(email);
        }
        //??????
        Cell addressCell = row.getCell(beginColoum++);
        if (addressCell == null) {
            user.setAddress(null);
        } else {
            Object addressObject = getCellValue(addressCell);
            if (addressObject != null) {
                String address = NameUtils.removeBlank(String.valueOf(addressObject));
                user.setAddress(address);
            }
        }
        //????????????
        Cell loginTypeCell = row.getCell(beginColoum++);
        String loginType = NameUtils.removeBlank(String.valueOf(getCellValue(loginTypeCell)));
        user.setLoginType(loginType.equals(UserConstant.SINGLE_LOGIN_TYPE) ? UserConstant.SINGLE_LOGIN_TIP : UserConstant.MORE_LOGIN_TIP);
        //???????????????
        Cell useCountCell = row.getCell(beginColoum++);
        if (useCountCell == null) {
            user.setMaxUsers(UserConstant.SINGLE_LOGIN_NUM);
        } else {
            String userCount = NameUtils.removeBlank(String.valueOf(getCellValue(useCountCell)));
            if (UserConstant.SINGLE_LOGIN_TYPE.equals(loginType)) {
                user.setMaxUsers(UserConstant.SINGLE_LOGIN_NUM);
            } else {
                user.setMaxUsers(Integer.parseInt(userCount));
            }
        }
        //???????????????
        Cell valiDayCell = row.getCell(beginColoum++);
        if (valiDayCell == null) {
            user.setCountValidityTime(null);
        } else {
            Integer cellType = getCellType(valiDayCell);
            if (cellType != Cell.CELL_TYPE_BLANK) {
                String valiDay = NameUtils.removeBlank(String.valueOf(getCellValue(valiDayCell)));
                if (StringUtils.isNotEmpty(valiDay)) {
                    int valiTime = Integer.parseInt(valiDay);
                    user.setCountValidityTime(valiTime + UserConstant.DAY_TIP);
                }
            }
        }
        //??????
        Cell remarkCell = row.getCell(UserConstant.USER_DESC_SITE);
        if (remarkCell == null) {
            user.setUserDesc(null);
        } else {
            Object remarkObject = getCellValue(remarkCell);
            if (remarkObject != null) {
                String remark = NameUtils.removeBlank(String.valueOf(remarkObject));
                user.setUserDesc(remark);
            }
        }
        user.setPassword(PasswordUtils.passwordEncrypt(UserConstant.DEFAULT_PWD));
        return user;
    }

    /**
     * ????????????????????????????????????????????????
     *
     * @param row ???????????????
     * @return ????????????????????????
     */
    private Result checkDataNull(Row row) {
        //???????????????????????????
        Cell userCodeCell = row.getCell(UserConstant.USER_CODE_SITE);
        if (userCodeCell == null || getCellType(userCodeCell) == Cell.CELL_TYPE_BLANK) {
            return ResultUtils.warn(UserResultCode.USER_CODE_NULL, I18nUtils.getSystemString(UserI18n.USER_CODE_NULL));
        } else {
            String codeValue = (String) getCellValue(userCodeCell);
            if (StringUtils.isEmpty(codeValue.trim())) {
                return ResultUtils.warn(UserResultCode.USER_CODE_NULL, I18nUtils.getSystemString(UserI18n.USER_CODE_NULL));
            }
        }

        //????????????
        Cell userNameCell = row.getCell(UserConstant.USER_NAME_SITE);
        if (userNameCell == null || getCellType(userNameCell) == Cell.CELL_TYPE_BLANK) {
            return ResultUtils.warn(UserResultCode.USER_NAME_NULL, I18nUtils.getSystemString(UserI18n.USER_NAME_NULL));
        } else {
            String userNameValue = (String) getCellValue(userNameCell);
            if (StringUtils.isEmpty(userNameValue.trim())) {
                return ResultUtils.warn(UserResultCode.USER_NAME_NULL, I18nUtils.getSystemString(UserI18n.USER_NAME_NULL));
            }
        }

        //????????????
        Cell userStatusCell = row.getCell(UserConstant.USER_STATU_SITE);
        if (userStatusCell == null || getCellType(userStatusCell) == Cell.CELL_TYPE_BLANK) {
            return ResultUtils.warn(UserResultCode.USER_STATUS_NULL, I18nUtils.getSystemString(UserI18n.USER_STATUS_NULL));
        } else {
            String userStatusValue = (String) getCellValue(userStatusCell);
            if (StringUtils.isEmpty(userStatusValue.trim())) {
                return ResultUtils.warn(UserResultCode.USER_STATUS_NULL, I18nUtils.getSystemString(UserI18n.USER_STATUS_NULL));
            }
        }

        //????????????
        Cell departmentCell = row.getCell(UserConstant.DEPARTMENT_SITE);
        if (departmentCell == null || getCellType(departmentCell) == Cell.CELL_TYPE_BLANK) {
            return ResultUtils.warn(UserResultCode.USER_DEPT_NULL, I18nUtils.getSystemString(UserI18n.USER_DEPT_NULL));
        } else {
            String departmentValue = (String) getCellValue(departmentCell);
            if (StringUtils.isEmpty(departmentValue.trim())) {
                return ResultUtils.warn(UserResultCode.USER_DEPT_NULL, I18nUtils.getSystemString(UserI18n.USER_DEPT_NULL));
            }
        }

        //????????????
        Cell roleCell = row.getCell(UserConstant.ROLE_SITE);
        if (roleCell == null || getCellType(roleCell) == Cell.CELL_TYPE_BLANK) {
            return ResultUtils.warn(UserResultCode.USER_ROLE_NULL, I18nUtils.getSystemString(UserI18n.USER_ROLE_NULL));
        } else {
            String roleValue = (String) getCellValue(roleCell);
            if (StringUtils.isEmpty(roleValue.trim())) {
                return ResultUtils.warn(UserResultCode.USER_ROLE_NULL, I18nUtils.getSystemString(UserI18n.USER_ROLE_NULL));
            }
        }

        //????????????
        Cell loginTypeCell = row.getCell(UserConstant.LOGINT_TYPE_SITE);
        if (loginTypeCell == null || getCellType(loginTypeCell) == Cell.CELL_TYPE_BLANK) {
            return ResultUtils.warn(UserResultCode.USER_MODEL_NULL, I18nUtils.getSystemString(UserI18n.USER_MODEL_NULL));
        } else {
            String loginTypeValue = (String) getCellValue(loginTypeCell);
            if (StringUtils.isEmpty(loginTypeValue.trim())) {
                return ResultUtils.warn(UserResultCode.USER_MODEL_NULL, I18nUtils.getSystemString(UserI18n.USER_MODEL_NULL));
            }
        }

        //??????????????????
        Cell phoneNumberCell = row.getCell(UserConstant.PHONE_NUMBER_SITE);
        if (phoneNumberCell == null || getCellType(phoneNumberCell) == Cell.CELL_TYPE_BLANK) {
            return ResultUtils.warn(UserResultCode.PHONE_NUMBER_NULL, I18nUtils.getSystemString(UserI18n.PHONE_NUMBER_NULL));
        } else {
            String phoneNumberValue = (String) getCellValue(phoneNumberCell);
            if (StringUtils.isEmpty(phoneNumberValue.trim())) {
                return ResultUtils.warn(UserResultCode.PHONE_NUMBER_NULL, I18nUtils.getSystemString(UserI18n.PHONE_NUMBER_NULL));
            }
        }

        //??????
        Cell emailCell = row.getCell(UserConstant.EMAIL_SITE);
        if (emailCell == null || getCellType(emailCell) == Cell.CELL_TYPE_BLANK) {
            return ResultUtils.warn(UserResultCode.EMAIL_NULL, I18nUtils.getSystemString(UserI18n.EMAIL_NULL));
        } else {
            String emailValue = (String) getCellValue(emailCell);
            if (StringUtils.isEmpty(emailValue.trim())) {
                return ResultUtils.warn(UserResultCode.EMAIL_NULL, I18nUtils.getSystemString(UserI18n.EMAIL_NULL));
            }
        }

        //???????????????????????????
        Cell remarkCell = row.getCell(UserConstant.USER_DESC_SITE);
        if (remarkCell != null) {
            String remark = String.valueOf(getCellValue(remarkCell));
            if (remark.length() >= UserConstant.MAX_USER_DESC_LENGTH) {
                return ResultUtils.warn(UserResultCode.USER_DESC_TOO_LONG, I18nUtils.getSystemString(UserI18n.USER_DESC_TOO_LONG));
            }
        }

        //????????????????????????
        Cell addressCell = row.getCell(UserConstant.USER_ADDRESS_SITE);
        if (addressCell != null) {
            String address = String.valueOf(getCellValue(addressCell));
            if (address.length() > UserConstant.MAX_USER_ADDRESS_LENGTH) {
                return ResultUtils.warn(UserResultCode.USER_ADDRESS_TOLONG, I18nUtils.getSystemString(UserI18n.USER_ADDRESS_TOLONG));
            }
        }

        //?????????????????????????????????
        Cell validataCell = row.getCell(UserConstant.VALIDATE_TIME);
        if (validataCell != null && getCellType(validataCell) != Cell.CELL_TYPE_BLANK) {
            String validate = String.valueOf(getCellValue(validataCell));
            String validay = NameUtils.removeBlank(validate);
            if (StringUtils.isNotEmpty(validay)) {
                Integer validataType = getCellType(validataCell);
                if (validataType != Cell.CELL_TYPE_NUMERIC) {
                    return ResultUtils.warn(UserResultCode.VALIDATA_TIME_ABNORMAL_FORMAT,
                            I18nUtils.getSystemString(UserI18n.VALIDATA_TIME_ABNORMAL_FORMAT));
                }
            }
        }

        //???????????????
        Cell maxNumCell = row.getCell(UserConstant.MAXUSER_SITE);
        if (maxNumCell != null) {
            Integer maxNumType = getCellType(maxNumCell);
            if (maxNumType != Cell.CELL_TYPE_NUMERIC) {
                return ResultUtils.warn(UserResultCode.MAX_USER_NUMBER_ABNORMAL_FORMAT,
                        I18nUtils.getSystemString(UserI18n.MAX_USER_NUMBER_ABNORMAL_FORMAT));
            }
        }

        return null;
    }

    /**
     * ??????????????????????????????????????????
     *
     * @param checkUser ??????????????????
     * @param userList  ????????????
     * @return ????????????
     */
    private Result checkRepeatData(User checkUser, List<User> userList) {


        //????????????????????????????????????
        User findUser = userList.stream().filter(user -> user.getUserCode().equals(checkUser)).findFirst().orElse(null);
        if (findUser != null) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.USER_CODE_REPEAT)
                    + checkUser.getUserCode());
        }
        return null;
    }

    /**
     * ????????????????????????
     *
     * @param row ??????????????????
     */
    private Result checkData(Row row, int beginColoum, List<User> userList, List<Role> roleList,
                             List<Department> departmentList) {
        //????????????
        Cell userCodeCell = row.getCell(UserConstant.USER_CODE_SITE);
        Result userCodeResult = checkUserCode(userCodeCell, userList);
        if (userCodeResult != null) {
            return userCodeResult;
        }

        //????????????
        Cell roleCell = row.getCell(UserConstant.ROLE_SITE);
        Result result = checkRoleName(roleCell, roleList);
        if (result.getCode() != ResultCode.SUCCESS) {
            return result;
        }

        //????????????
        Cell departmentCell = row.getCell(UserConstant.DEPARTMENT_SITE);
        Result deptDataResult = checkDepartmentName(departmentCell, departmentList);
        if (deptDataResult.getCode() == ResultCode.FAIL) {
            return deptDataResult;
        }

        //????????????????????????
        Cell phoneCell = row.getCell(UserConstant.PHONE_NUMBER_SITE);
        Result phoneResult = checkPhoneNumber(phoneCell, userList);
        if (phoneResult != null) {
            return phoneResult;
        }

        //??????????????????
        Cell emailCell = row.getCell(UserConstant.EMAIL_SITE);
        Result emailResult = checkEmail(emailCell, userList);
        if (emailResult != null) {
            return emailResult;
        }

        Cell maxUserCell = row.getCell(UserConstant.MAXUSER_SITE);
        if (maxUserCell != null) {
            Object cellObject = getCellValue(maxUserCell);
            String maxUser = String.valueOf(cellObject);
            if (maxUser.indexOf(UserConstant.DOT) > 0) {
                return ResultUtils.warn(
                        ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.MAX_ONLINEUSER_MUST_BE_INTEGER));
            }
        }

        Cell valiDayCell = row.getCell(UserConstant.VALIDATE_TIME);

        if (valiDayCell != null) {
            Integer cellType = getCellType(valiDayCell);
            if (cellType != Cell.CELL_TYPE_BLANK) {
                String valiDay = String.valueOf(getCellValue(valiDayCell));
                valiDay = NameUtils.removeBlank(valiDay);
                if (StringUtils.isNotEmpty(valiDay)) {
                    int valiTime = Integer.parseInt(valiDay);
                    if (valiTime > UserConstant.VALIDATE_TIME_MAX || valiTime < UserConstant.VALIDATE_TIME_MIN) {
                        return ResultUtils.warn(
                                ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.VALID_TIME_OUT_RANGE));
                    }
                }
            }
        }

        return null;
    }

    /**
     * ????????????????????????
     *
     * @param emailCell ????????????
     * @param userList  ?????????????????????
     * @return ???????????????
     */
    public Result checkEmail(Cell emailCell, List<User> userList) {

        if (emailCell == null) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.EMAIL_NULL));
        }
        //????????????
        String email = String.valueOf(getCellValue(emailCell));
        User equalUser = userList.stream().
                filter(user -> user.getEmail().equals(email)).findFirst().orElse(null);
        if (equalUser != null) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.EMAIL_IS)
                    + email + I18nUtils.getSystemString(UserI18n.HAS_USED));
        }
        return null;
    }


    /**
     * ??????????????????????????????
     *
     * @param phoneCell ????????????
     * @param userList  ?????????????????????
     * @return ???????????????
     */
    public Result checkPhoneNumber(Cell phoneCell, List<User> userList) {

        if (phoneCell == null) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.PHONE_NUMBER_NULL));
        }
        //??????????????????????????????????????????
        String phone = String.valueOf(getCellValue(phoneCell));
        BigDecimal bigDecimal = new BigDecimal(phone);
        String phoneNumber = bigDecimal.toPlainString();
        User equalUser = userList.stream().
                filter(user -> user.getPhoneNumber().equals(phoneNumber)).findFirst().orElse(null);
        if (equalUser != null) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.PHONE_NUMBER_IS)
                    + phoneNumber + I18nUtils.getSystemString(UserI18n.HAS_USED));
        }
        return null;
    }

    /**
     * ??????????????????
     *
     * @param departmentCell ????????????
     * @param departmentList ??????????????????
     * @return
     */
    public Result checkDepartmentName(Cell departmentCell, List<Department> departmentList) {

        String deptName = NameUtils.removeBlank(String.valueOf(getCellValue(departmentCell)));
        List<String> departmentFullNameList = new ArrayList<>();
        departmentList.forEach(department -> {
            StringBuffer fullNameBuffer = new StringBuffer();
            getDeptFullName(fullNameBuffer, department);
            String fullName = fullNameBuffer.toString().substring(0, fullNameBuffer.toString().length() - 1);
            if (fullName.equals(deptName)) {
                departmentFullNameList.add(department.getId());
            }
        });

        if (departmentFullNameList.size() == UserConstant.DATA_SIZE_IS_ZERO) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.DEPARTMENT_NAME)
                    + deptName + I18nUtils.getSystemString(UserI18n.NOT_EXIST));
        }
        return ResultUtils.success(departmentFullNameList.get(UserConstant.LIST_FIRST));
    }

    /**
     * ????????????????????????
     *
     * @param department ??????????????????
     * @return ????????????????????????
     */
    public void getDeptFullName(StringBuffer fullName, Department department) {

        fullName.insert(UserConstant.BEGIN_INSERT, department.getDeptName() + UserConstant.SPLIT_SIGN);
        if (department.getParentDepartment() != null) {
            getDeptFullName(fullName, department.getParentDepartment());
        }
    }

    /**
     * ??????????????????
     *
     * @param userCodeCell ??????cell??????
     * @param userList     ????????????????????????
     * @return ????????????
     */
    public Result checkUserCode(Cell userCodeCell, List<User> userList) {

        if (userCodeCell == null) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.USER_CODE_NULL));
        }
        //??????????????????????????????????????????
        String userCode = String.valueOf(getCellValue(userCodeCell));
        User equalUser = userList.stream().
                filter(user -> user.getUserCode().equals(userCode)).findFirst().orElse(null);
        if (equalUser != null) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.USER_CODE_IS)
                    + userCode + I18nUtils.getSystemString(UserI18n.HAS_USED));
        }
        return null;
    }

    /**
     * ????????????????????????
     *
     * @param roleCell ????????????
     * @param roleList ????????????
     * @return ????????????
     */
    private Result checkRoleName(Cell roleCell, List<Role> roleList) {

        if (roleCell == null) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.USER_ROLE_NULL));
        }
        String roleName = NameUtils.removeBlank(String.valueOf(getCellValue(roleCell)));
        Role role = roleList.stream().filter(subRole -> subRole.getRoleName().equals(roleName)).findFirst().orElse(null);
        if (role == null) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.ROLE)
                    + roleName + I18nUtils.getSystemString(UserI18n.NOT_EXIST));
        }
        Role daoRole = roleDao.verityRoleByName(roleName);
        if (daoRole.getId().startsWith(SUPER_ADMIN_ID)) {
            return ResultUtils.warn(ResultCode.FAIL, I18nUtils.getSystemString(UserI18n.IMPORT_USER_ROLE_CANNOT_BE_SUPER_ADMINISTRATOR));
        }
        return ResultUtils.success(role.getId());
    }


    /**
     * ????????????cell????????????
     * cell ????????????????????????
     */
    public Object getCellValue(Cell cell) {

        Object value = null;
        //?????????number String?????????????????????
        DecimalFormat decimalFormat = new DecimalFormat(UserConstant.ZERO_DECIMAL_FORMAT);

        switch (cell.getCellType()) {

            case Cell.CELL_TYPE_STRING:
                //?????????????????????
                value = cell.getRichStringCellValue().getString();
                break;

            case Cell.CELL_TYPE_NUMERIC:
                //???????????????????????????????????????????????????????????????????????????
                value = decimalFormat.format(cell.getNumericCellValue());
                break;

            case Cell.CELL_TYPE_BOOLEAN:
                //?????????bool?????????????????????
                value = cell.getBooleanCellValue();
                break;

            case Cell.CELL_TYPE_BLANK:
                //??????????????????????????????
                break;

            default:
                break;
        }
        return value;
    }

    /**
     * ????????????cell????????????
     * cell ????????????????????????
     */
    public Integer getCellType(Cell cell) {

        if (cell == null) {
            return Cell.CELL_TYPE_BLANK;
        }
        switch (cell.getCellType()) {

            case Cell.CELL_TYPE_STRING:
                return Cell.CELL_TYPE_STRING;
            case Cell.CELL_TYPE_NUMERIC:
                return Cell.CELL_TYPE_NUMERIC;
            case Cell.CELL_TYPE_BOOLEAN:
                return Cell.CELL_TYPE_BOOLEAN;
            case Cell.CELL_TYPE_BLANK:
                return Cell.CELL_TYPE_BLANK;
            default:
                return Cell.CELL_TYPE_BLANK;
        }
    }

    /**
     * ???????????????????????????
     *
     * @param id      ??????id
     * @param model   ??????id
     * @param logType ????????????
     */
    private void universalLog(String id, String model, String logType) {
        systemLanguageUtil.querySystemLanguage();
        User user = userDao.selectById(id);
        //??????????????????
        AddLogBean addLogBean = logProcess.generateAddLogToCallParam(logType);
        addLogBean.setDataId("id");
        addLogBean.setFunctionCode(model);
        //??????????????????id
        addLogBean.setOptUserName(user.getUserName());
        addLogBean.setOptObjId(id);
        addLogBean.setOptObj(user.getUserName());
        //???????????????
        addLogBean.setDataOptType(LogConstants.DATA_OPT_TYPE_UPDATE);
        //??????????????????
        logProcess.addSecurityLogInfoToCall(addLogBean, LogConstants.ADD_LOG_LOCAL_FILE);
    }
}
