package com.kyee.upgrade.utils;

public class ConstantUtil {

    public static final String GIT_USERNAME = "zhanghaoyu";
    public static final String GIT_PASSWORD = "960808aAa";

    /**
     * 补丁包状态（云）
     */
    public enum UpPatchStatus{
        REGISTERED("8"),
        NOT_UPGRADE("10"),  //未升级
        PKGING("11"),       //打包中
        PKG_SUCCESS("20"),  //已打包
        PKG_FAIL("25"),     //打包失败
        PUBLISHED("15"),    //发布
        UPGRADE_PART("70"), //部分升级
        UPGRADE_ALL("80"),  //已升级
        ROLLBACK("75"),     //已回退
        LOCKED("77"),       //已锁定
        SQL_EXECUTED("88"); //脚本已执行

        private String value;
        UpPatchStatus(String value){this.value = value;}
        public String getValue(){ return value; }

        public boolean isRegistered(){
            return this.equals(REGISTERED);
        }
    }

    /**
     * 补丁包状态（端）
     */
    public enum UpgradeStatus{
        NOT_UPGRADE("10"),      // 未升级
        UPGRADING("20"),        // 升级中（待删除）
        NOT_TEST("93"),         // 未测试
        SQL_TESTED("94"),       // 脚本已测试
        TEST_UPGRADE("95"),     // 测试升级
        UPGRADE_PART("70"),     // 部分升级
        UPGRADE_ALL("80"),      // 已升级
        SQL_EXECUTED("88");     // 脚本已执行

        private String value;

        UpgradeStatus(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

    /**
     * 升级记录状态
     */
    public enum UpgradeRecordStatus {
        UPGRADE_FAIL("30"),     // 失败
        UPGRADE_OK("40"),       // 成功
        ROLLBACK_FAIL("50"),    // 回退失败
        ROLLBACK_OK("60");      // 已回退

        private String value;
        UpgradeRecordStatus(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

    /**
     * 是否含SQL
     */
    public enum SqlFlag{
        SQL("Y"),
        NOT_SQL("N");
        private String value;
        private SqlFlag(String value){this.value = value;}
        public String getValue(){ return value; }
        public boolean isSql(){
            return this.equals(SQL);
        }
        public boolean isNotSql(){
            return this.equals(NOT_SQL);
        }
    }

    /**
     * 文件类型
     */
    public enum FileType{
        SQL("SQL"),
        JAVA("JAVA"),
        OTHER("OTHER");

        private String value;
        private FileType(String value){
            this.value = value;
        }

        public String getValue(){
            return value;
        }

        public boolean isSql(){
            return this.equals(SQL);
        }

        public boolean isJava(){
            return this.equals(JAVA);
        }

        public boolean isOther(){
            return this.equals(OTHER);
        }
    }

    /**
     * 代码文件类型
     */
    public enum CodeType{
        SQL("SQL"),
        CODE("CODE"),
        OTHER("OTHER");

        private String value;
        private CodeType(String value){
            this.value = value;
        }

        public String getValue(){
            return value;
        }

        public boolean isSql(){
            return this.equals(SQL);
        }

        public boolean isCode(){
            return this.equals(CODE);
        }

        public boolean isOther(){
            return this.equals(OTHER);
        }
    }

    /**
     * 工作空间状态
     */
    public enum WorkSpaceSouceState{
        WORKING(1), //使用中
        IDLE(0); //空闲中

        private Integer value;

        private WorkSpaceSouceState(Integer value){
            this.value = value;
        }

        public Integer getValue(){
            return this.value;
        }

        public boolean isWorking(){
            return this.equals(WORKING);
        }

        public boolean isIdle(){
            return this.equals(IDLE);
        }

    }

    /**
     * 日志记录类型
     */
    public enum LogRecordType{
        MAVEN("maven"),
        GIT("git");

        private String value;
        LogRecordType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * 服务器状态
     */
    public enum ServerStatus{
        NOT_START("30"),         // 未启动
        START_OK("50");        // 已启动

        private String value;
        ServerStatus(String value) {
            this.value = value;
        }
        public String getValue() { 
            return value; 
        }
    }

    /**
     * 补丁包类型
     */
    public enum PatchType{
        SINGLE("1"),      // 单任务
        RELEASE("2");      // 版本

        private String value;

        PatchType(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

    /**
     * 分支类型
     */
    public enum BranchType{
        DEV("1"),      // DEV
        MASTER("2"),   // MASTER
        RELEASE("3");  // RELEASE

        private String value;

        BranchType(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

    /**
     * 代码列表仓库
     */
    public enum CodeResp{
        HIS_CODE(1,"http://s.kyee.com.cn/rest/api/latest/projects/YLY/repos/his_v3/pull-requests/"),                      // HIS
        AUTH_INTER_CODE(2,"http://s.kyee.com.cn/rest/api/latest/projects/YLY/repos/auth_inter/pull-requests/"),          // AUTH_INTER
        HIS_SCHEDULED_CODE(3,"http://s.kyee.com.cn/rest/api/latest/projects/YLY/repos/his-scheduled/pull-requests/"),   // HIS_SCHEDULED
        RHCMS_SERVER_CODE(4,"http://s.kyee.com.cn/rest/api/latest/projects/YLZF/repos/rhcms_server/pull-requests/"),   // RHCMS_SERVER
        RHCMS_WEB_CODE(5,"http://s.kyee.com.cn/rest/api/latest/projects/YLZF/repos/rhcms_web/pull-requests/"),        // RHCMS_WEB
        STANDARD_CODE(6,"http://s.kyee.com.cn/rest/api/latest/projects/YLZF/repos/standard/pull-requests/"),         // STANDARD
        LIS(7,"http://s.kyee.com.cn/rest/api/latest/projects/YLY/repos/lis/pull-requests/"),                        // LIS
        HISV5_CODE(9,"http://s.kyee.com.cn/rest/api/latest/projects/YLY/repos/his_v5/pull-requests/"),             // HISV5
        HIS_SCHEDULED_V2_CODE(10,"http://s.kyee.com.cn/rest/api/latest/projects/YLY/repos/his-scheduled/pull-requests/");    // HIS_SCHEDULED(新版)

        private Integer key;
        private String value;

        CodeResp(Integer key, String value) {
            this.key = key;
            this.value = value;
        }

        public Integer getKey() {
            return key;
        }

        public String getValue() {
            return value;
        }}


    /**
     * 编码类型
     */
    public enum EncodeType{
        UTF8("UTF-8"),      // UTF-8
        GB2312("GB2312"),   // GB2312
        ISO_8859_1("ISO-8859-1"),   // ISO-8859-1
        OTHER("OTHER");    // OTHER

        private String value;

        EncodeType(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

    /**
     * SQL存放目录
     */
    public enum SQLDIR {
        TABLE("0TABLE"),
        VIEW_FUNC("2VIEW_FUNC"),
        PROCEDURE("3PROCEDURE"),
        DATA("1DATA");

        private String value;

        SQLDIR(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

    /**
     * 打包分支
     */
    public enum PatchBranch {
        MASTERBRANCH("1"),
        COMMITID("2");

        private String value;

        PatchBranch(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

    /**
     * 编译类型
     */
    public enum BuildType {
        ALL_BUILD("1"),
        MODULE_BUILD("2"),
        BOOT_BUILD("3"),
        CLASS_BUILD("4");

        private String value;

        BuildType(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

    /**
     * 任务类型
     */
    public enum TaskType {
        SCENE_DEMAND("1"),//现场需求单
        SCENE_IMPROVE("2"),//现场改进单
        SCENE_BUG("3"),//现场BUG单
        TEST_IMPROVE("4"),//测试改进单
        TEST_BUG("5"),//测试BUG单
        DEVELOP_BUILD("6");//研发自建单

        private String value;
        TaskType(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }

    /**
     * 多现场发包
     */
    public enum ProjectProductEnum {
        CHENGHUA_SANYUAN(39), // 成华三院
        CHENGHUA_QIYUAN(46),  // 成华七院
        LIS(7);               // LIS
        private Integer value;

        ProjectProductEnum(Integer value) {
            this.value = value;
        }
        public Integer getValue() {
            return value;
        }
    }

    /**
     * 部门ID
     */
    public enum DEPTID {
        HISDEPTID("101"),         // HIS研发中心
        PROJECTDEPTID("110");     // 项目研发中心
        private String value;

        DEPTID(String value) {
            this.value = value;
        }
        public String getValue() {
            return value;
        }
    }
}
