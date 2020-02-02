package com.newcoder.community.dao;

import com.newcoder.community.domain.LoginTicket;
import org.apache.ibatis.annotations.*;

/**
 * 登录凭证
 * 已废弃
 */
@Mapper
@Deprecated
public interface LoginTicketMapper {

    @Insert("INSERT INTO login_ticket (user_id, ticket, status, expired) VALUES (#{userId} , #{ticket} , #{status} , #{expired} )")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insert(LoginTicket loginTicket);

    @Update("UPDATE login_ticket SET status = #{status} WHERE ticket = #{ticket} ")
    void updateStatus(@Param("ticket") String ticket, @Param("status") Integer status);

    @Select("SELECT * FROM login_ticket WHERE ticket = #{ticket} ")
    LoginTicket getByTicket(String ticket);

}
