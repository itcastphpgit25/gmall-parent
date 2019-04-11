package com.atguigu.gmall.ums.service.impl;

import com.alibaba.dubbo.config.annotation.Service;
import com.alibaba.fastjson.JSON;
import com.atguigu.gmall.constant.social.SocialConstant;
import com.atguigu.gmall.to.social.AccessTokenVo;
import com.atguigu.gmall.to.social.WeiboAccessTokenVo;
import com.atguigu.gmall.to.social.WeiboUserVo;
import com.atguigu.gmall.ums.entity.Member;
import com.atguigu.gmall.ums.entity.MemberSocial;
import com.atguigu.gmall.ums.mapper.MemberMapper;
import com.atguigu.gmall.ums.mapper.MemberSocialMapper;
import com.atguigu.gmall.ums.service.MemberSocialService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sun.xml.internal.ws.api.model.MEP;
import io.shardingjdbc.core.api.HintManager;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author Lfy
 * @since 2019-03-31
 */
@Service(version = "1.0")
@Component
public class MemberSocialServiceImpl extends ServiceImpl<MemberSocialMapper, MemberSocial> implements MemberSocialService {
    @Autowired
    MemberSocialMapper memberSocialMapper;
    @Autowired
    MemberMapper memberMapper;

    /**
     * 分布式锁和数据库层Transaction提供的锁定机制都可以；
     * SELECT FOR UPDATE
     *  如果FOR  UPDATE 的字段没有建立索引，会导致全表扫描，导致变成表锁；导致表数据插入等出现问题；
     *
     * @param tokenVo
     * @return
     */
    @Transactional
    @Override
    public Member getMemberInfo(AccessTokenVo tokenVo) {
        Member member=null;

        if(tokenVo instanceof WeiboAccessTokenVo){
            WeiboAccessTokenVo token=(WeiboAccessTokenVo)tokenVo;
            //1.查看系统有没有，根据uid查看数据库是否有此数据
            member=memberSocialMapper.getMemberInfo(token.getUid());
            if(member==null){
                //这个社区账号第一次登陆
                //注册与绑定流程
                Member registMember = new Member();

                CloseableHttpClient httpClient = HttpClients.createDefault();
                HttpGet httpGet=new HttpGet("https://api.weibo.com/2/users/show.json?access_token="+tokenVo.getAccess_token()+"&uid="+token.getUid());
                try {
                    CloseableHttpResponse execute = httpClient.execute(httpGet);
                    HttpEntity entity = execute.getEntity();
                    String s = EntityUtils.toString(entity);
                    System.out.println("从社交平台获取到的数据:"+s);
                    WeiboUserVo object = JSON.parseObject(s, WeiboUserVo.class);

                    //初始化微博数据进来
                    registMember.setIcon(object.getProfile_image_url());
                    registMember.setNickname(object.getName());

                    //数据库底层的select与select for update
                    //幂等性设计
                    List<MemberSocial> memberSocials=memberSocialMapper.selectAccessTokenForUpdate(tokenVo.getAccess_token());
                    if(memberSocials!=null&&memberSocials.size()>0){
                        //查出了数据，有社交信息，直接按照社交的uid
                        MemberSocial memberSocial = memberSocials.get(0);
                        //查出这个member返回
                        member = memberMapper.selectById(memberSocial.getUserId());
                    }else {
                        HintManager.getInstance().setMasterRouteOnly();

                        //给系统插入一个新用户
                        memberMapper.insert(registMember);
                        //新用户的id，并建立social关系
                        MemberSocial memberSocial = new MemberSocial();
                        memberSocial.setType(SocialConstant.SocialTypeEnum.WEIBO.getType());
                        memberSocial.setAccessToken(tokenVo.getAccess_token());
                        memberSocial.setUid(object.getId().toString());
                        memberSocial.setUserId(registMember.getId());

                        //保存用户
                        memberSocialMapper.insert(memberSocial);
                        System.out.println("=======memberSocialMapper.insert...."+tokenVo.getAccess_token());
                    }
               }catch (IOException e){

                }
            }else {
                return member;
            }


        }
        return member;
    }
}
