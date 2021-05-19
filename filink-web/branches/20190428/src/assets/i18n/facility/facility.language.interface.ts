/**
 * Created by wh1709040 on 2019/1/4.
 */
export interface FacilityLanguageInterface {
  areaName: string;
  level: string;
  provinceName: string;
  cityName: string;
  districtName: string;
  address: string;
  accountabilityUnit: string;
  remarks: string;
  createTime: string;
  parentId: string;
  creator: string;
  updateTime: string;
  updater: string;
  deleteHandle: string;
  update: string;
  operate: string;
  addArea: string;
  select: string;
  modify: string;
  managementFacilities: string;
  setDevice;
  do_not: string;
  open: string;
  area: string;
  device: string;
  deviceType: string;
  deviceType_a: string;
  deviceName: string;
  deviceName_a: string;
  deviceStatus: string;
  deviceStatus_a: string;
  deviceCode: string;
  deviceCode_a: string;
  deployStatus: string;
  position: string;
  exclusive: string;
  deviceLogName: string;
  deviceLogType: string;
  nodeObject: string;
  createUser: string;
  updateUser: string;
  deployed: string;
  noDefence: string;
  config: {
    Optical_Box: string,
    Well: string,
    Distribution_Frame: string,
    Junction_Box: string,
    Optical_Cable: string,
    Splitting_Box: string,
    Parts: string,
    NORMAL: string,
    ALARM: string,
    OFFLINE: string,
    OUT_OF_CONTACT: string,
    UNKNOWN: string,
    DEPLOYED: string,
    NODEFENCE: string,
    NOTUSED: string,
    MAIINTENANCE: string,
    DISMANTLE: string,
    DEPLOYING: string,
    AREA_LEVEL_ONE: string,
    AREA_LEVEL_TWO: string,
    AREA_LEVEL_THREE: string,
    AREA_LEVEL_FOUR: string,
    AREA_LEVEL_FIVE: string,
    event: string,
    alarm: string,
    controller: string,
    PASSIVE_LOCK: string,
    MECHANICAL_LOCK_CYLINDER: string,
    ELECTRONIC_LOCK_CYLINDER: string,
    mechanicalKey: string,
    bluetoothKey: string,
    labelGun: string
  };
  serialNumber;
  extraRemarks;
  partName: string;
  department: string;
  depositary: string;
  time: string;
  addParts: string;
  updateParts: string;
  partsType: string;
  person: string;
  partType: string;

  selectUnit: string;
  deptName: string;
  deptLevel: string;
  parentDept: string;
  nameExists: string;
  handleOk: string;
  handleCancel: string;
  location: string;
  viewDetail: string;
  remoteUnlock: string;
  noSupport: string;
  chooseDoorLock: string;
  statistical: string;
  configuration: string;
  errMsg: string;
  noControlMsg: string;
  errorMsg: string;
  doorNum: string;
  doorName: string;
  lockNum: string;
  lockStatus: string;
  doorStatus: string;
  lockOpen: string;
  lockOff: string;
  lockInvalid: string;
  doorOpen: string;
  doorOff: string;
  basicOperation: string;
  configurationStrategy: string;
  authorization: string;
  facilityEdit: string;
  facilityDelete: string;
  removal: string;
  facilityOff: string;
  deleteFacilityMsg: string;
  prompt: string;
  noRelatedData: string;
  realisticPicture: string;
  thumbnail: string;
  InfrastructureDetails: string;
  infrastructureName: string;
  infrastructureCode: string;
  intelligentEntranceGuard: string;
  boxInformation: string;
  softwareVersion: string;
  SvuTime: string;
  active: string;
  dormancy: string;
  solarEnergy: string;
  controlUnitStatus: string;
  hardwareVersion: string;
  moduleType: string;
  operator: string;
  wirelessModuleSignal: string;
  electricity: string;
  sensingInformation: string;
  temperature: string;
  humidity: string;
  leach: string;
  lean: string;
  logicalArea: string;
  control: string;
  picDetails: string;
  devicePic: string;
  picDate: string;
  picSize: string;
  picResource: string;
  picInfo: {
    name: string;
    facilityName: string;
    facilityType: string;
    facilityNo: string;
    area: string;
    address: string;
    imgType: string;
    time: string;
    source: string;
    delete: string;
    download: string;
    day: string;
    week: string;
    month: string;
    year: string;
    pleaseChoose: string;
    alarm: string;
    worker: string;
    picture: string;
    downloadMsg: string;
  };
  okText: string;
  cancelText: string;
  pleaseSelectTheUnitToWhichYouBelong: string;
  noDepositaryUnderResponsibleUnit: string;
  editDeployStatusMsg: string;
  questionMark: string;
  dismantleMsg: string;
  unOpenLock: string;
}
