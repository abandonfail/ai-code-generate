package com.aicodegenerate.model.dto.user;

import lombok.Data;

import java.io.Serializable;

/**
 * 用户修改密码请求
 */
@Data
public class UserChangePasswordRequest implements Serializable {
    private static final long serialVersionUID = 8735650154179439661L;

    /**
     * 用户id
     */
    private Long id;

    /**
     * 旧密码
     */
    private String oldPassword;

    /**
     * 新密码
     */
    private String newPassword;

    /**
     * 确认密码
     */
    private String checkPassword;
}
