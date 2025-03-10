package cn.lili.modules.member.mapper;

import cn.lili.modules.member.entity.dos.MemberEvaluation;
import cn.lili.modules.member.entity.vo.MemberEvaluationListVO;
import cn.lili.modules.member.entity.vo.StoreRatingVO;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.Map;

/**
 * 会员商品评价数据处理层
 *
 * @author Bulbasaur
 * @date 2020-02-25 14:10:16
 */
public interface MemberEvaluationMapper extends BaseMapper<MemberEvaluation> {


    @Select("select me.* from li_member_evaluation as me ${ew.customSqlSegment}")
    IPage<MemberEvaluationListVO> getMemberEvaluationList(IPage<MemberEvaluationListVO> page, @Param(Constants.WRAPPER) Wrapper<MemberEvaluationListVO> queryWrapper);

    @Select("select grade,count(1) as num from li_member_evaluation Where goods_id=#{goodsId} GROUP BY grade")
    List<Map<String, Object>> getEvaluationNumber(String goodsId);

    @Select("SELECT round( AVG( delivery_score ), 2 ) AS delivery_score" +
            ",round( AVG( description_score ), 2 ) AS description_score" +
            ",round( AVG( service_score ), 2 ) AS service_score " +
            "FROM li_member_evaluation ${ew.customSqlSegment}")
    StoreRatingVO getStoreRatingVO(@Param(Constants.WRAPPER) Wrapper<MemberEvaluation> queryWrapper);

    @Select("SELECT goods_id,COUNT(goods_id) AS num FROM li_member_evaluation GROUP BY goods_id")
    List<Map<String, Object>> memberEvaluationNum(@Param(Constants.WRAPPER) Wrapper<MemberEvaluation> queryWrapper);
}