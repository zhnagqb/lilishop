package cn.lili.modules.system.mapper;

import cn.lili.modules.system.entity.dos.AppVersion;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

/**
 * app版本控制数据处理层
 *
 * @author Chopper
 * @date 2020/11/17 8:01 下午
 */
public interface AppVersionMapper extends BaseMapper<AppVersion> {

    @Select("SELECT * FROM li_app_version WHERE type=#{appType} ORDER BY version_update_date DESC LIMIT 1")
    AppVersion getLatestVersion(String appType);

}