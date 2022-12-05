package com.hmdp.utils;

public interface ILock {

    /**
     * 尝试获取锁
     * @param timeOutSec 锁持有的过期时间，过期后会自动失效
     * @return true 代表获取锁成功; false 代表获取锁失败
     */
    boolean tryLock(long timeOutSec);

    /**
     * 释放锁
     */
    void unlock();
}
