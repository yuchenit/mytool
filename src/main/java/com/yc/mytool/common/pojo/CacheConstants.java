package com.yc.mytool.common.pojo;

/**
 * 缓存的key 常量
 *
 * @author camojojo
 */
public class CacheConstants {

    /**
     * 缓存有效期，默认720（分钟）
     */
    public final static long EXPIRATION = 720;

    /**
     * 缓存刷新时间，默认120（分钟）
     */
    public final static long REFRESH_TIME = 120;

    /**
     * 权限缓存前缀
     */
    public final static String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * 验证码有效期，默认30（分钟）
     */
    public final static long EXPIRCODE = 30;

    /**
     * APP用户信息缓存key
     */
    public static final String USER_SESSION_KEY = "USER_SESSION_KEY:";

    /**
     * APP用户信息缓存key,有效期,默认 3天
     */
    public final static long APP_TOKEN_EXPIRE = 3;

    /**
     * APP用户信息缓存key,缓存刷新时间，默认2天
     */
    public final static long APP_REFRESH_TIME = 2;

    /**
     * 重复请求限制默认时间2s
     */
    public final static long REPEAT_TIME = 2;

    /**
     * 已赠送免费计划标识
     */
    public final static String FREE_PLAN = "freePlan:";

    // 相机访问服务器凭证
    public final static String ACM_TOKEN = "acmToken:";

    public final static String GET_PIC = "get_pic";

    // 相机SD卡内存不足10%通知和推送
    public final static String SD_CARD = "cam:sdCard:";

    // 相机电量不足10%通知和推送
    public final static String ABTTERY = "cam:battery:";

    // 相机疑似停止工作通知和推送
    public final static String WORK = "cam:work:";

    // 相机疑似离线通知和推送
    public final static String WORK_OFFLINE = "cam:offline:";

    // 相机同步推送
    public static final String CAM_SYN = "cam:syn";

    public final static String CAM_LOGIN_TYPE = "cam:loginType:";

    // 相机当前设置缓存key
    public final static String CAM_CONFIG = "cam:config:";

    //p2p连接
    public final static String CAM_P2P_STATUS = "p2p:status:";

    /**
     * 用于从redis中获取imei绑定的acm服务地址
     * redis key = ACM_ALIVE_CLUSTER_KEY + imei
     * redis value = acm服务地址
     */
    public static final String ACM_ALIVE_CLUSTER_KEY = "acm:alive:";

    // Ali P2p用户凭证
    public final static String ALI_TOKEN = "ali:token:";


    // Ali iot用户区域
    public final static String ALI_REGION = "ali:iot:region:";

    public final static String ALI_MQTT = "ali:mqtt:";

    public static final String PUSH_CER = "push:cer:";

    // APP Statistics数据统计缓存key
    public static final String COUNT_TYPE = "count:type:";

    // 支付锁key前缀
    public final static String PAY_LOCK = "payLock:";

    // 用户UGC禁言key
    public final static String UGC_LIMIT = "ugcLimei:uuid:";


}
