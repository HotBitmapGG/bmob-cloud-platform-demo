package com.hotbitmapgg.geekcommunity.config;

import android.annotation.SuppressLint;
import android.os.Environment;

/**
 * 常量管理类
 *
 * @author Administrator
 * @hcc
 */
@SuppressLint("SdCardPath")
public class RestUtils
{

    // Bmob后台Access Key
    public static final String AccessKey = "d5abf142fdc4f72e94034fb05ab80866";

    // Bmob后台
    public static final String SecretKey = "5374350";

    // BmobSMS短信模版名称
    public static final String SMSName = "短信验证码";

    // 存放图片缓存目录
    public static String BMOB_PICTURE_PATH = Environment.getExternalStorageDirectory() + "/bmobimdemo/image/";

    // 头像缓存目录
    public static String MyAvatarDir = "/sdcard/bmobimdemo/avatar/";

    // 拍照修改头像
    public static final int REQUESTCODE_UPLOADAVATAR_CAMERA = 1;

    // 本地相册修改头像
    public static final int REQUESTCODE_UPLOADAVATAR_LOCATION = 2;

    // 系统裁剪头像
    public static final int REQUESTCODE_UPLOADAVATAR_CROP = 3;

    // 拍照
    public static final int REQUESTCODE_TAKE_CAMERA = 0x000001;

    // 本地图片
    public static final int REQUESTCODE_TAKE_LOCAL = 0x000002;

    // 位置
    public static final int REQUESTCODE_TAKE_LOCATION = 0x000003;

    // 字符串key
    public static final String EXTRA_STRING = "extra_string";

    // 注册成功之后登陆页面退出
    public static final String ACTION_REGISTER_SUCCESS_FINISH = "register.success.finish";

    // 用户名key
    public static final String USERNAME_KEY = "username";

    // 用户密码key
    public static final String PASSWORD_KEY = "password";

    // 用户手机
    public static final String PHONE_KEY = "phone";

    public static final int PUBLISH_COMMENT = 1;

    public static final int NUMBERS_PER_PAGE = 20;

    public static final int SAVE_FAVOURITE = 2;

    public static final int GET_FAVOURITE = 3;

    public static final int GO_SETTINGS = 4;

    public static final String SEX_MALE = "male";

    public static final String SEX_FEMALE = "female";

    // SMS短信验证key 密码
    public static final String SMS_KEY = "ae1528bb9626";

    public static final String SMS_PASSWORD = "82e605975fcbb45d9169384d4922390e";
}