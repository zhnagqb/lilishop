package cn.lili.controller.goods;

import cn.lili.common.enums.ResultUtil;
import cn.lili.common.vo.PageVO;
import cn.lili.common.vo.ResultMessage;
import cn.lili.modules.distribution.service.DistributionService;
import cn.lili.modules.goods.entity.dos.Goods;
import cn.lili.modules.goods.entity.dto.GoodsSearchParams;
import cn.lili.modules.goods.entity.vos.GoodsVO;
import cn.lili.modules.goods.service.GoodsService;
import cn.lili.modules.goods.service.GoodsSkuService;
import cn.lili.modules.search.entity.dos.EsGoodsIndex;
import cn.lili.modules.search.entity.dos.EsGoodsRelatedInfo;
import cn.lili.modules.search.entity.dto.EsGoodsSearchDTO;
import cn.lili.modules.search.service.EsGoodsSearchService;
import cn.lili.modules.statistics.aop.PageViewPoint;
import cn.lili.modules.statistics.aop.enums.PageViewEnum;
import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * 买家端,商品接口
 *
 * @author Chopper
 * @date 2020/11/16 10:06 下午
 */
@Api(tags = "买家端,商品接口")
@RestController
@RequestMapping("/buyer/goods")
public class GoodsBuyerController {

    /**
     * 商品
     */
    @Autowired
    private GoodsService goodsService;
    /**
     * 商品SKU
     */
    @Autowired
    private GoodsSkuService goodsSkuService;
    /**
     * ES商品搜索
     */
    @Autowired
    private EsGoodsSearchService goodsSearchService;
    /**
     * 分销员
     */
    @Autowired
    private DistributionService distributionService;


    @ApiOperation(value = "通过id获取商品信息")
    @ApiImplicitParam(name = "goodsId", value = "商品ID", required = true, paramType = "path", dataType = "Long")
    @GetMapping(value = "/get/{goodsId}")
    public ResultMessage<GoodsVO> get(@NotNull(message = "商品ID不能为空") @PathVariable("goodsId") String id) {
        return ResultUtil.data(goodsService.getGoodsVO(id));
    }

    @ApiOperation(value = "通过id获取商品信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "goodsId", value = "商品ID", required = true, paramType = "path"),
            @ApiImplicitParam(name = "skuId", value = "skuId", required = true, paramType = "path")
    })
    @GetMapping(value = "/sku/{goodsId}/{skuId}")
    @PageViewPoint(type = PageViewEnum.SKU, id = "#id")
    public ResultMessage<Map<String, Object>> getSku(@NotNull(message = "商品ID不能为空") @PathVariable("goodsId") String goodsId,
                                                     @NotNull(message = "SKU ID不能为空") @PathVariable("skuId") String skuId) {

        Map<String, Object> map = goodsSkuService.getGoodsSkuDetail(goodsId, skuId);



        return ResultUtil.data(map);
    }

    @ApiOperation(value = "获取商品分页列表")
    @GetMapping
    public ResultMessage<IPage<Goods>> getByPage(GoodsSearchParams goodsSearchParams) {
        return ResultUtil.data(goodsService.queryByParams(goodsSearchParams));
    }

    @ApiOperation(value = "从ES中获取商品信息")
    @GetMapping("/es")
    public ResultMessage<Page<EsGoodsIndex>> getGoodsByPageFromEs(EsGoodsSearchDTO goodsSearchParams, PageVO pageVO) {
        pageVO.setNotConvert(true);
        Page<EsGoodsIndex> esGoodsIndices = goodsSearchService.searchGoods(goodsSearchParams, pageVO);
        return ResultUtil.data(esGoodsIndices);
    }

    @ApiOperation(value = "从ES中获取相关商品品牌名称，分类名称及属性")
    @GetMapping("/es/related")
    public ResultMessage<EsGoodsRelatedInfo> getGoodsRelatedByPageFromEs(EsGoodsSearchDTO goodsSearchParams, PageVO pageVO) {
        pageVO.setNotConvert(true);
        EsGoodsRelatedInfo selector = goodsSearchService.getSelector(goodsSearchParams, pageVO);
        return ResultUtil.data(selector);
    }

    @ApiOperation(value = "获取搜索热词")
    @GetMapping("/hot-words")
    public ResultMessage<List<String>> getGoodsHotWords(Integer start, Integer end) {
        List<String> hotWords = goodsSearchService.getHotWords(start, end);
        return ResultUtil.data(hotWords);
    }

}
