package com.q.reminder.reminder.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.q.reminder.reminder.constant.JsonCharsConstant;
import com.q.reminder.reminder.entity.RProjectInfo;
import com.q.reminder.reminder.entity.RedmineUserInfo;
import com.q.reminder.reminder.entity.TTableFeatureTmp;
import com.q.reminder.reminder.entity.TTableUserConfig;
import com.q.reminder.reminder.service.*;
import com.q.reminder.reminder.service.otherService.TableFeatureService;
import com.q.reminder.reminder.vo.FeatureAllVo;
import com.q.reminder.reminder.vo.base.ReturnT;
import com.q.reminder.reminder.vo.table.FeatureUserConfigVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author : saiko
 * @version : v1.0
 * @ClassName : com.q.reminder.reminder.controller.TableFeatureController
 * @Description :
 * @date :  2023.02.01 15:26
 */
@Log4j2
@RestController
@RequestMapping("/table")
@RequiredArgsConstructor
public class TableFeatureController {
    private final TTableFeatureTmpService tTableFeatureTmpService;
    private final RedmineUserInfoService redmineUserInfoService;
    private final ProjectInfoService projectInfoService;
    private final TTableUserConfigService tTableUserConfigService;
    private final TTableInfoService tTableInfoService;
    private final TableFeatureService tableFeatureService;


    @PostMapping("/records")
    public void records(@RequestBody String entity) {
        log.info("/records/user_config : {}", entity);
        JSONObject json = json(entity);
        TTableFeatureTmp featureTmp = json.to(TTableFeatureTmp.class);
        if (!"是".equals(featureTmp.getWriteType())) {
            return;
        }
        Map<String, String> userConfigMap = tTableUserConfigService.listAll().stream().collect(Collectors.toMap(TTableUserConfig::getPrjctName, TTableUserConfig::getPrjctKey, (v1, v2) -> v1));
        String prjct = featureTmp.getPrjct();
        featureTmp.setPrjctKey(userConfigMap.get(prjct));
        log.info("/records/user_config save : {}", featureTmp);
        tTableFeatureTmpService.saveOrUpdate(featureTmp);
    }

    @PostMapping("/user_config")
    public void userConfig(@RequestBody FeatureUserConfigVo vo) {
        log.info("/table/user_config  FeatureUserConfigVo : {}", vo);
        String prjctKey = vo.getPrjctKey();
        if (StringUtils.isBlank(prjctKey)) {
            return;
        }
        RProjectInfo RProjectInfo = projectInfoService.projectInfoByPrjctKey(prjctKey);
        LambdaQueryWrapper<RedmineUserInfo> lq = Wrappers.lambdaQuery();
        lq.eq(RedmineUserInfo::getRedmineType, RProjectInfo.getRedmineType());
        List<RedmineUserInfo> userInfoList = redmineUserInfoService.listUsers(RProjectInfo.getRedmineType());
        Map<String, Integer> userMap = userInfoList.stream().collect(Collectors.toMap(RedmineUserInfo::getUserName, RedmineUserInfo::getAssigneeId, (v1, v2) -> v1));
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
        entity.setPId(RProjectInfo.getId());
        entity.setArchtctId(userMap.get(vo.getArchtct()));
        entity.setPrjctKey(prjctKey);
        tTableUserConfigService.saveInfo(entity);
    }

    @GetMapping("/feature/{name}")
    @CrossOrigin(maxAge = 3600)
    public ReturnT<List<FeatureAllVo>> feature(@PathVariable("name") String projectName) {
        return new ReturnT<>(tableFeatureService.records(projectName));
    }

    private JSONObject json(String str) {
        JSONObject jsonKey = JSONObject.parseObject(str);
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
        jsonKey.put("writeType", "");
        jsonKey.put("featureType", "");
        return jsonKey;
    }
}
