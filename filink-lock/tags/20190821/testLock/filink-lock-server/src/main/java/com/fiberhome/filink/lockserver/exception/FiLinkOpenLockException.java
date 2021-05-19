package com.fiberhome.filink.lockserver.exception;

/**
 * <p>
 * 开锁没有权限异常
 * </p>
 *
 * @author wanzhaozhang@wistronits.com
 * @since 2019/4/20
 */
public class FiLinkOpenLockException extends RuntimeException {
    public FiLinkOpenLockException(String message) {
        super(message);
    }
}
