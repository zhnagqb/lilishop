package cn.lili.timetask.handler.impl.goods;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.lili.modules.goods.mapper.GoodsMapper;
import cn.lili.modules.member.entity.dos.MemberEvaluation;
import cn.lili.modules.member.mapper.MemberEvaluationMapper;
import cn.lili.timetask.handler.EveryDayExecute;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * 商品定时器
 *
 * @author Chopper
 * @date 2021/3/18 3:23 下午
 */
@Component
public class GoodsExecute implements EveryDayExecute {
    //会员评价
    @Autowired
    private MemberEvaluationMapper memberEvaluationMapper;
    //商品
    @Autowired
    private GoodsMapper goodsMapper;

    /**
     * 查询已上架的商品的评价数量并赋值
     */
    @Override
    public void execute() {

        //查询上次统计到本次的评价数量
        List<Map<String, Object>> list = memberEvaluationMapper.memberEvaluationNum(
                new QueryWrapper<MemberEvaluation>()
                        .between("create_time", DateUtil.yesterday(), new DateTime()));

        System.out.println("评论数量" + list.size());
        for (Map<String, Object> map : list) {
            goodsMapper.addGoodsCommentNum(Integer.parseInt(map.get("num").toString()), map.get("goods_id").toString());
        }

    }
}
