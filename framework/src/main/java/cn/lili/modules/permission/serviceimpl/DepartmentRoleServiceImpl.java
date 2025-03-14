package cn.lili.modules.permission.serviceimpl;

import cn.lili.modules.permission.entity.dos.DepartmentRole;
import cn.lili.modules.permission.mapper.DepartmentRoleMapper;
import cn.lili.modules.permission.service.DepartmentRoleService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 部门角色业务层实现
 *
 * @author Chopper
 * @date 2020/11/22 12:08
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class DepartmentRoleServiceImpl extends ServiceImpl<DepartmentRoleMapper, DepartmentRole> implements DepartmentRoleService {
    @Autowired
    private DepartmentRoleMapper departmentRoleMapper;

    @Override
    public List<DepartmentRole> listByDepartmentId(String departmentId) {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("department_id", departmentId);
        return departmentRoleMapper.selectList(queryWrapper);
    }

    @Override
    public void updateByDepartmentId(String departmentId, List<DepartmentRole> departmentRoles) {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("department_id", departmentId);
        departmentRoleMapper.delete(queryWrapper);

        this.saveBatch(departmentRoles);
    }

    @Override
    public void deleteByDepartment(List<String> ids) {
        QueryWrapper queryWrapper = new QueryWrapper<>();
        queryWrapper.in("department_id", ids);
        departmentRoleMapper.delete(queryWrapper);
    }
}