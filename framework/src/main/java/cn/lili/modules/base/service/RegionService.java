package cn.lili.modules.base.service;

import cn.lili.modules.system.entity.dos.Region;
import cn.lili.modules.system.entity.vo.RegionVO;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;

/**
 * 行政地区业务层
 *
 * @author Chopper
 * @date 2020/12/2 14:14
 */
@CacheConfig(cacheNames = "{regions}")
public interface RegionService extends IService<Region> {

    /**
     * 同步行政数据
     *
     * @param url
     */
    @CacheEvict
    void synchronizationData(String url);


    @Cacheable(key = "#id")
    List<Region> getItem(String id);

    /**
     * 获取地址
     *
     * @param cityCode 城市编码
     * @param townName 镇名称
     * @return
     */
    Map<String, Object> getRegion(String cityCode, String townName);

    /**
     * 获取所有的城市
     *
     * @return
     */
    @Cacheable(key = "'ALL_CITY'")
    List<RegionVO> getAllCity();
}