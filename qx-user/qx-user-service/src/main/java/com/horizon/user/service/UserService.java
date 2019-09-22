package com.horizon.user.service;

import com.horizon.common.enums.ExceptionEnums;
import com.horizon.common.exception.QxException;
import com.horizon.common.utils.NumberUtils;
import com.horizon.user.mapper.UserMapper;
import com.horizon.user.pojo.User;
import com.horizon.user.utils.CodecUtils;
import com.sun.prism.impl.Disposer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private AmqpTemplate amqpTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final String KEY_PREFIX = "user:verify:phone";

    public Boolean checkData(String data, Integer type) {

        User record = new User();

        //判断数据类型
        switch (type){
            case 1:
                record.setUsername(data);
                break;
            case 2:
                record.setPhone(data);
                break;
            default:
                throw new QxException(ExceptionEnums.INVAILD_USER_DATA_TYPE);
        }

        return userMapper.selectCount(record) == 0;
    }

    public void sendCode(String phone) {

        //生成Key
        String key = KEY_PREFIX + phone;

        //生成验证码，随机6位
        String code = NumberUtils.generateCode(6);

        Map<String,String> msg = new HashMap<>();
        msg.put("phone",phone);
        msg.put("code",code);

        //发送验证码
        amqpTemplate.convertAndSend("qx.item.exchange","sms.verify.code",msg);

        //保存验证码
        redisTemplate.opsForValue().set(key,code,5, TimeUnit.MINUTES);


    }

    public void register(User user, String code) {

        //从redis中取出验证码
        String cacheCode = redisTemplate.opsForValue().get(KEY_PREFIX + user.getPhone());

        //校验验证码
        if (!StringUtils.equals(code,cacheCode)){
            throw new QxException(ExceptionEnums.INVALID_VERIFY_CODE);
        }

        //生成盐
        String salt = CodecUtils.genereteSalt();
        user.setSalt(salt);

        //对密码进行加密,MD5加密并添加盐值
        CodecUtils.md5Hex(user.getPassword(),salt);

        //保存数据
        user.setCreated(new Date());
        userMapper.insert(user);

    }

    public User queryUserByUsernameAndPassword(String username, String password) {

        //查询用户
        User record = new User();
        record.setUsername(username);
        User user = userMapper.selectOne(record);

        //校验
        if (user == null){
            throw new QxException(ExceptionEnums.INVALID_USERNAME_PASSWORD);
        }

        //校验密码
        if (!StringUtils.equals(user.getPassword(), CodecUtils.md5Hex(password,user.getSalt()))) {
            throw new QxException(ExceptionEnums.INVALID_USERNAME_PASSWORD);
        }

        //用户名和密码正确
        return user;

    }
}
