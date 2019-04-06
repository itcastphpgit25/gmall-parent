package com.atguigu.gmall.ums.mapper;

import com.atguigu.gmall.to.social.WeiboAccessTokenVo;
import com.atguigu.gmall.ums.entity.Member;
import com.atguigu.gmall.ums.entity.MemberSocial;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author Lfy
 * @since 2019-03-31
 */
public interface MemberSocialMapper extends BaseMapper<MemberSocial> {

    Member getMemberInfo(String uid);

    List<MemberSocial> selectAccessTokenForUpdate(String access_token);
}
