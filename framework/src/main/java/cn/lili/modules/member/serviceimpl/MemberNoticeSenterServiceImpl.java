package cn.lili.modules.member.serviceimpl;

import cn.lili.common.exception.ServiceException;
import cn.lili.common.utils.StringUtils;
import cn.lili.modules.member.entity.dos.MemberNoticeSenter;
import cn.lili.modules.member.entity.dos.Member;
import cn.lili.modules.member.entity.dos.MemberNotice;
import cn.lili.modules.member.entity.enums.SendTypeEnum;
import cn.lili.modules.member.mapper.MemberNoticeSenterMapper;
import cn.lili.modules.member.service.MemberNoticeSenterService;
import cn.lili.modules.member.service.MemberNoticeService;
import cn.lili.modules.member.service.MemberService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 会员消息业务层实现
 *
 * @author Chopper
 * @date 2020/11/17 3:44 下午
 */
@Service
@Transactional(propagation = Propagation.REQUIRED, rollbackFor = java.lang.Exception.class)
public class MemberNoticeSenterServiceImpl extends ServiceImpl<MemberNoticeSenterMapper, MemberNoticeSenter> implements MemberNoticeSenterService {

    //会员
    @Autowired
    private MemberService memberService;
    //会员站内信
    @Autowired
    private MemberNoticeService memberNoticeService;

    @Override
    public boolean customSave(MemberNoticeSenter memberNoticeSenter) {

        if (this.saveOrUpdate(memberNoticeSenter)) {
            List<MemberNotice> memberNotices = new ArrayList<>();
            //如果是选中会员发送
            if (memberNoticeSenter.getSendType().equals(SendTypeEnum.SELECT.name())) {
                //判定消息是否有效
                if (!StringUtils.isEmpty(memberNoticeSenter.getMemberIds())) {
                    String[] ids = memberNoticeSenter.getMemberIds().split(",");
                    MemberNotice memberNotice;
                    for (String id : ids) {
                        memberNotice = new MemberNotice();
                        memberNotice.setIsRead(false);
                        memberNotice.setContent(memberNoticeSenter.getContent());
                        memberNotice.setMemberId(id);
                        memberNotice.setTitle(memberNoticeSenter.getTitle());
                        memberNotices.add(memberNotice);
                    }
                } else {
                    return true;
                }
            } // 否则是全部会员发送
            else {
                List<Member> members = memberService.list();
                MemberNotice memberNotice;
                for (Member member : members) {
                    memberNotice = new MemberNotice();
                    memberNotice.setIsRead(false);
                    memberNotice.setContent(memberNoticeSenter.getContent());
                    memberNotice.setMemberId(member.getId());
                    memberNotice.setTitle(memberNoticeSenter.getTitle());
                    memberNotices.add(memberNotice);
                }
            }
            //防止没有会员导致报错
            if (memberNotices.size() > 0) {
                //批量保存
                if (memberNoticeService.saveBatch(memberNotices)) {
                    return true;
                } else {
                    throw new ServiceException("发送站内信异常，请检查系统日志");
                }
            }
        }
        return true;
    }
}