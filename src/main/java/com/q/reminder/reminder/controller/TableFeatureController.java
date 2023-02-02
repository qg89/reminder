package com.q.reminder.reminder.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.q.reminder.reminder.entity.ProjectInfo;
import com.q.reminder.reminder.entity.RedmineUserInfo;
import com.q.reminder.reminder.entity.TTableFeatureTmp;
import com.q.reminder.reminder.entity.TTableUserConfig;
import com.q.reminder.reminder.service.ProjectInfoService;
import com.q.reminder.reminder.service.RedmineUserInfoService;
import com.q.reminder.reminder.service.TTableFeatureTmpService;
import com.q.reminder.reminder.service.TTableUserConfigService;
import com.q.reminder.reminder.vo.table.FeatureUserConfigVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.controller.TableFeatureController
 * @Description :
 * @date :  2023.02.01 15:26
 */
@RestController
@RequestMapping("/table")
public class TableFeatureController {
    @Autowired
    private TTableFeatureTmpService tTableFeatureTmpService;
    @Autowired
    private RedmineUserInfoService redmineUserInfoService;
    @Autowired
    private ProjectInfoService projectInfoService;
    @Autowired
    private TTableUserConfigService tTableUserConfigService;


    @PostMapping("/records")
    public void records(@RequestBody TTableFeatureTmp entity) {
        tTableFeatureTmpService.saveOrUpdate(entity);
    }

    @PostMapping("/user_config")
    public void userConfig(@RequestBody FeatureUserConfigVo vo) {
        LambdaQueryWrapper<ProjectInfo> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(ProjectInfo::getPKey, vo.getProjectKey());
        ProjectInfo projectInfo = projectInfoService.getOne(lambdaQueryWrapper);
        LambdaQueryWrapper<RedmineUserInfo> lq = Wrappers.lambdaQuery();
        lq.eq(RedmineUserInfo::getRedmineType, projectInfo.getRedmineType());
        Map<String, Integer> userMap = redmineUserInfoService.list(lq).stream().collect(Collectors.toMap(RedmineUserInfo::getAssigneeName, RedmineUserInfo::getAssigneeId));
        TTableUserConfig entity = new TTableUserConfig();
        entity.setPrdctId(userMap.get(vo.getPrdct()));
        entity.setAlgrthmId(userMap.get(vo.getAlgrthm()));
        entity.setBackId(userMap.get(vo.getBack()));
        entity.setFrontId(userMap.get(vo.getFront()));
        entity.setImplmnttonId(userMap.get(vo.getImplmntton()));
        entity.setBgdtId(userMap.get(vo.getBgdt()));
        entity.setOprtonId(userMap.get(vo.getOprton()));
        entity.setPrdctName(vo.getPrjct());
        entity.setTestId(userMap.get(vo.getTest()));
        entity.setPId(projectInfo.getId());
        entity.setArchtctId(userMap.get(vo.getArchtct()));
        tTableUserConfigService.saveOrUpdate(entity);
    }
}
