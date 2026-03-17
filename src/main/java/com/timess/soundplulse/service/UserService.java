package com.timess.soundplulse.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.timess.soundplulse.model.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.timess.soundplulse.model.dto.user.UserQueryRequest;
import com.timess.soundplulse.model.vo.LoginUserVO;
import com.timess.soundplulse.model.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
* @author 33363
* @description 针对表【user(用户)】的数据库操作Service
*/
public interface UserService extends IService<User> {
    /**
     * 注册
     * @param userAccount
     * @param userPassword
     */
    void userRegister(String userAccount, String userPassword, String mail, String verifyCode);

    /**
     * 用户登录
     * @param userAccount
     * @param userPassword
     * @param request
     * @return
     */
    LoginUserVO userLogin(String userAccount, String userPassword, HttpServletRequest request);

    /**
     * 获取当前用户信息
     * @param request
     * @return
     */
    User getLoginUser(HttpServletRequest request);

    /**
     * 返回脱敏后的当前登录用户信息
     * @param request
     * @return
     */
    LoginUserVO getLoginUserVO(HttpServletRequest request);


    /**
     * 退出登录
     * @param request
     * @return
     */
    Boolean logout(HttpServletRequest request);

    /**
     * 脱敏用户信息
     * @param user
     * @return
     */
    UserVO getUserVO(User user);

    /**
     * 脱敏用户信息列表
     * @param userList
     * @return
     */
    List<UserVO> getUserVOList(List<User> userList);

    /**
     * 获取查询条件
     * @param userQueryRequest
     * @return
     */
    QueryWrapper<User> getQueryWrapper(UserQueryRequest userQueryRequest);

    /**
     * 是否为管理员
     * @param user
     * @return
     */
    boolean isAdmin(User user);
}
