package cn.lili.modules.message.mapper;

import cn.lili.modules.message.entity.dos.StoreMessage;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 消息发送数据处理层
 *
 * @author Chopper
 * @date 2021/1/30 4:17 下午
 */
public interface StoreMessageMapper extends BaseMapper<StoreMessage> {

    @Select("select me.title,me.content,me.create_time,sp.store_name,sp.store_id,sp.id,sp.status from li_message me inner join li_store_message sp on me.id = sp.message_id ${ew.customSqlSegment} ")
    IPage<StoreMessage> queryByParams(IPage<StoreMessage> page, @Param(Constants.WRAPPER) Wrapper<StoreMessage> queryWrapper);
}