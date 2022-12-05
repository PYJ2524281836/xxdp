package com.hmdp.utils;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.hmdp.dto.UserDTO;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static com.hmdp.utils.RedisConstants.LOGIN_USER_KEY;
import static com.hmdp.utils.RedisConstants.LOGIN_USER_TTL;

public class RefreshInterceptor implements HandlerInterceptor {

    private final StringRedisTemplate stringRedisTemplate;

    public RefreshInterceptor(StringRedisTemplate stringRedisTemplate){
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // TODO 1.获取请求头中的token
        String token = request.getHeader("authorization");
        if (StrUtil.isBlank(token)){
            return true;
        }
        // TODO 2.基于token获取redis中的用户
        String tokenKey = LOGIN_USER_KEY + token;
        Map<Object, Object> userMap = stringRedisTemplate.opsForHash().entries(tokenKey);
        //3.判断用户是否存在
        if (userMap.isEmpty()){
            return true;
        }
        // TODO 5.将查询到的Hash数据转换为UerDTO对象
        UserDTO userDTO = BeanUtil.fillBeanWithMap(userMap, new UserDTO(), false);
        // TODO 6.存在,保存用户信息到ThreadLocal
        UserHolder.saveUser(userDTO);
        // TODO 7.刷新token有效期
        //stringRedisTemplate.expire(tokenKey,LOGIN_USER_TTL, TimeUnit.MINUTES);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserHolder.removeUser();
    }
}
