package cn.lili.modules.message.serviceimpl;

import cn.lili.common.rocketmq.RocketmqSendCallbackBuilder;
import cn.lili.common.rocketmq.tags.OtherTagsEnum;
import cn.lili.common.utils.PageUtil;
import cn.lili.common.vo.PageVO;
import cn.lili.config.rocketmq.RocketmqCustomProperties;
import cn.lili.modules.message.entity.dos.Message;
import cn.lili.modules.message.entity.vos.MessageVO;
import cn.lili.modules.message.mapper.MessageMapper;
import cn.lili.modules.message.service.MessageService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.rocketmq.spring.core.RocketMQTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 消息内容业务层实现
 *
 * @author Chopper
 * @date 2020/11/17 3:48 下午
 */
@Service
@Transactional
public class MessageServiceImpl extends ServiceImpl<MessageMapper, Message> implements MessageService {

    @Autowired
    private RocketMQTemplate rocketMQTemplate;
    @Autowired
    private RocketmqCustomProperties rocketmqCustomProperties;


    @Override
    public IPage<Message> getPage(MessageVO messageVO, PageVO pageVO) {
        return this.page(PageUtil.initPage(pageVO), messageVO.lambdaQueryWrapper());
    }


    @Override
    public Boolean sendMessage(Message message) {
        //保存站内信信息
        this.save(message);
        //发送站内信消息提醒
        String destination = rocketmqCustomProperties.getOtherTopic() + ":" + OtherTagsEnum.MESSAGE.name();
        rocketMQTemplate.asyncSend(destination, message, RocketmqSendCallbackBuilder.commonCallback());
        String noticeSendDestination = rocketmqCustomProperties.getNoticeSendTopic() + ":" + OtherTagsEnum.MESSAGE.name();
        rocketMQTemplate.asyncSend(noticeSendDestination, message, RocketmqSendCallbackBuilder.commonCallback());
        return true;
    }

    @Override
    public Boolean deleteMessage(String id) {
        //只有查询到此记录才真实删除，未找到记录则直接返回true即可
        Message message = this.getById(id);
        if (message != null) {
            return this.removeById(id);
        }
        return true;
    }
}