package com.static4u.netcar.constant;

public class URLConstant {

    // 手动DEBUG模式
    public static boolean IS_DEBUG = false;
    // 强制log显示RE
    public static boolean FORCE_LOG = true;

    private static final String DEBUG_HOST_FUNC = "http://w.hchz315.com/control/sys.asmx";

    private static final String RELEASE_HOST_FUNC = "http://w.hchz315.com/control/sys.asmx";

    // 功能列表host
    public static String HOST_FUNC;

    static {
        if (IS_DEBUG) {
            HOST_FUNC = DEBUG_HOST_FUNC;
        } else {
            HOST_FUNC = RELEASE_HOST_FUNC;
        }
    }
}
