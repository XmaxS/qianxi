package com.horizon.user.pojo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import tk.mybatis.mapper.annotation.KeySql;

import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Table(name = "tb_user")
@Data
public class User {

    @Id
    @KeySql(useGeneratedKeys = true)
    private Long id;

    @NotEmpty
    @Length(min = 4,max = 32,message = "用户名长度必须在4~32位")
    private String username;

    @Length(min = 6,max = 20,message = "密码长度必须在6~20位")
    @JsonIgnore
    private String password;

    //手机号正则表达式
    @Pattern(regexp = "^1([38][0-9]|4[579]|5[0-3,5-9]|6[6]|7[0135678]|9[89])\\d{8}$",message = "手机号不正确")
    private String phone;

    private Date created; //创建时间

    @JsonIgnore
    private String salt;  //密码的盐值

}
