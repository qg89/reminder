package com.q.reminder.reminder.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.q.reminder.reminder.constant.JsonCharsConstant;
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
    public void records(@RequestBody String entity) {
        JSONObject json = json(entity);
        TTableFeatureTmp featureTmp = json.to(TTableFeatureTmp.class);
        Map<String, String> userConfigMap = tTableUserConfigService.list().stream().collect(Collectors.toMap(TTableUserConfig::getPrjctName, TTableUserConfig::getPrjctKey));
        String prjct = featureTmp.getPrjct();
        featureTmp.setPrjctKey(userConfigMap.get(prjct));
        tTableFeatureTmpService.saveOrUpdate(featureTmp);
    }

    @PostMapping("/user_config")
    public void userConfig(@RequestBody FeatureUserConfigVo vo) {
        String prjctKey = vo.getPrjctKey();
        LambdaQueryWrapper<ProjectInfo> lambdaQueryWrapper = Wrappers.lambdaQuery();
        lambdaQueryWrapper.eq(ProjectInfo::getPKey, prjctKey);
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
        entity.setPrjctName(vo.getPrjct());
        entity.setTestId(userMap.get(vo.getTest()));
        entity.setPId(projectInfo.getId());
        entity.setArchtctId(userMap.get(vo.getArchtct()));
        entity.setPrjctKey(prjctKey);
        tTableUserConfigService.saveOrUpdate(entity);
    }

    private JSONObject json(String str) {
        JSONObject jsonKey = jsonKey();
        jsonKey.forEach((k, v) -> {
            String s = str.substring(str.indexOf(k) + k.length());
            String value = StrUtil.subBetween(s, JsonCharsConstant.CHARS_JSON);
            if (str.contains(k)) {
                jsonKey.put(k, value);
            }
        });
        return jsonKey;
    }

    public JSONObject jsonKey() {
        JSONObject jsonKey = new JSONObject();
        jsonKey.put("recordsId", "");
        jsonKey.put("menuOne", "");
        jsonKey.put("prdct", "");
        jsonKey.put("front", "");
        jsonKey.put("back", "");
        jsonKey.put("bgdt", "");
        jsonKey.put("implmntton", "");
        jsonKey.put("archtct", "");
        jsonKey.put("mdl", "");
        jsonKey.put("test", "");
        jsonKey.put("prodTime", "");
        jsonKey.put("andrd", "");
        jsonKey.put("prjct", "");
        jsonKey.put("menuTwo", "");
        jsonKey.put("menuThree", "");
        jsonKey.put("algrthm", "");
        jsonKey.put("oprton", "");
        jsonKey.put("featureId", "");
        jsonKey.put("dscrptn", "");
        return jsonKey;
    }
}
