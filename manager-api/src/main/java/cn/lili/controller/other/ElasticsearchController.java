package cn.lili.controller.other;

import cn.lili.modules.goods.entity.dos.GoodsSku;
import cn.lili.modules.goods.entity.enums.GoodsAuthEnum;
import cn.lili.modules.goods.entity.enums.GoodsStatusEnum;
import cn.lili.modules.goods.service.GoodsSkuService;
import cn.lili.modules.promotion.service.PromotionService;
import cn.lili.modules.search.entity.dos.EsGoodsIndex;
import cn.lili.modules.search.service.EsGoodsIndexService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.annotations.Api;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ElasticsearchController
 *
 * @author Chopper
 * @version v1.0
 * 2021-03-24 18:32
 */
@RestController
@Api(tags = "ES初始化接口")
@RequestMapping("/manager/elasticsearch")
public class ElasticsearchController {

    @Autowired
    private EsGoodsIndexService esGoodsIndexService;

    @Autowired
    private GoodsSkuService goodsSkuService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Autowired
    private PromotionService promotionService;

    @GetMapping
    public void init() {
        //查询商品信息
        LambdaQueryWrapper<GoodsSku> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(GoodsSku::getIsAuth, GoodsAuthEnum.PASS.name());
        queryWrapper.eq(GoodsSku::getMarketEnable, GoodsStatusEnum.UPPER.name());

        List<GoodsSku> list = goodsSkuService.list(queryWrapper);
        List<EsGoodsIndex> esGoodsIndices = new ArrayList<>();
        //库存锁是在redis做的，所以生成索引，同时更新一下redis中的库存数量
        for (GoodsSku goodsSku : list) {
            EsGoodsIndex index = new EsGoodsIndex(goodsSku);
            Map<String, Object> goodsCurrentPromotionMap = promotionService.getGoodsCurrentPromotionMap(index);
            index.setPromotionMap(goodsCurrentPromotionMap);
            esGoodsIndices.add(index);
            stringRedisTemplate.opsForValue().set(GoodsSkuService.getStockCacheKey(goodsSku.getId()), goodsSku.getQuantity().toString());
        }
        //初始化商品索引
        esGoodsIndexService.initIndex(esGoodsIndices);
        Assertions.assertTrue(true);
    }
}
