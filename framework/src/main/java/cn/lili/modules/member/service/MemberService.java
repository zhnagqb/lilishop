package cn.lili.modules.member.service;


import cn.lili.common.token.Token;
import cn.lili.common.vo.PageVO;
import cn.lili.modules.connect.entity.dto.ConnectAuthUser;
import cn.lili.modules.member.entity.dos.Member;
import cn.lili.modules.member.entity.dto.ManagerMemberEditDTO;
import cn.lili.modules.member.entity.dto.MemberAddDTO;
import cn.lili.modules.member.entity.dto.MemberEditDTO;
import cn.lili.modules.member.entity.vo.MemberDistributionVO;
import cn.lili.modules.member.entity.vo.MemberSearchVO;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * 会员业务层
 *
 * @author Bulbasaur
 * @date 2020-02-25 14:10:16
 */
public interface MemberService extends IService<Member> {

    /**
     * 获取当前登录的用户信息
     *
     * @return 会员信息
     */
    Member getUserInfo();

    /**
     * 是否可以通过手机获取用户
     *
     * @param uuid   UUID
     * @param mobile 手机号
     * @return 操作状态
     */
    boolean findByMobile(String uuid, String mobile);

    /**
     * 通过用户名获取用户
     *
     * @param username 用户名
     * @return 会员信息
     */
    Member findByUsername(String username);

    /**
     * 登录：用户名、密码登录
     *
     * @param username 用户名
     * @param password 密码
     * @return token
     */
    Token usernameLogin(String username, String password);

    /**
     * 商家登录：用户名、密码登录
     *
     * @param username 用户名
     * @param password 密码
     * @return token
     */
    Token usernameStoreLogin(String username, String password);

    /**
     * 注册：手机号、验证码登录
     *
     * @return 是否登录成功
     */
    Token mobilePhoneLogin(String mobilePhone);

    /**
     * 修改会员信息
     *
     * @param memberEditDTO 会员修改信息
     * @return 修改后的会员
     */
    Member editOwn(MemberEditDTO memberEditDTO);

    /**
     * 修改用户密码
     *
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 操作结果
     */
    Member modifyPass(String oldPassword, String newPassword);

    /**
     * 注册会员
     *
     * @param userName    会员
     * @param password    密码
     * @param mobilePhone mobilePhone
     * @return 处理结果
     */
    Token register(String userName, String password, String mobilePhone);

    /**
     * 修改当前会员的手机号
     *
     * @param mobile 手机号
     * @return 操作结果
     */
    boolean changeMobile(String mobile);


    /**
     * 通过手机号修改密码
     *
     * @param mobile   手机号
     * @param password 密码
     * @return
     */
    boolean resetByMobile(String mobile, String password);

    /**
     * 后台-添加会员
     *
     * @param memberAddDTO 会员
     * @return 会员
     */
    Member addMember(MemberAddDTO memberAddDTO);

    /**
     * 后台-修改会员
     *
     * @param managerMemberEditDTO 后台修改会员参数
     * @return 会员
     */
    Member updateMember(ManagerMemberEditDTO managerMemberEditDTO);

    /**
     * 获取会员分页
     *
     * @param memberSearchVO 会员搜索VO
     * @param page           分页
     * @return 会员分页
     */
    IPage<Member> getMemberPage(MemberSearchVO memberSearchVO, PageVO page);

    /**
     * 一键注册会员
     *
     * @return
     */
    Token autoRegister();

    /**
     * 一键注册会员
     *
     * @return Token
     */
    Token autoRegister(ConnectAuthUser authUser);

    /**
     * 刷新token
     *
     * @param refreshToken
     * @return Token
     */
    Token refreshToken(String refreshToken);

    /**
     * 刷新token
     *
     * @param refreshToken
     * @return Token
     */
    Token refreshStoreToken(String refreshToken);

    /**
     * 会员积分变动
     *
     * @param point    变动积分
     * @param type     变动类型 1为增加 0为消费
     * @param memberId 会员id
     * @param content  变动详细
     * @return 操作结果
     */
    Boolean updateMemberPoint(Long point, Integer type, String memberId, String content);

    /**
     * 修改会员状态
     *
     * @param memberIds 会员id集合
     * @param status    状态
     * @return 修改结果
     */
    Boolean updateMemberStatus(List<String> memberIds, Boolean status);

    /**
     * 查看会员数据分布
     *
     * @return 会员数据分布
     */
    List<MemberDistributionVO> distribution();

    /**
     * 根据条件查询会员总数
     *
     * @param memberSearchVO
     * @return 会员总数
     */
    Integer getMemberNum(MemberSearchVO memberSearchVO);
}