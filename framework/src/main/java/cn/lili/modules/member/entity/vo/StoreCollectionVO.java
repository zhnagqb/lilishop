package cn.lili.modules.member.entity.vo;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 会员店铺收藏VO
 *
 * @author pikachu
 * @date 2020-02-25 14:10:16
 */
@Data
public class StoreCollectionVO {

    @ApiModelProperty(value = "店铺id")
    private String storeId;

    @ApiModelProperty(value = "店铺名称")
    private String storeName;

    @ApiModelProperty(value = "店铺Logo")
    private String logo;

    @ApiModelProperty(value = "是否自营")
    private boolean selfOperated;
}
