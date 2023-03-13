package com.camojojo.center.common.iot;
/*
* Copyright 2017 Alibaba Group
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONStrFormater;
import com.alibaba.cloudapi.sdk.model.ApiResponse;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yc.mytool.common.pojo.CacheConstants;
import com.yc.mytool.common.utils.RedisService;
import com.yc.mytool.component.iot.IoTApiClientBuilderParams;
import com.yc.mytool.component.iot.IoTApiRequest;
import com.yc.mytool.component.iot.SyncApiClient;
import com.yc.mytool.component.iot.SyncApiGetClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * author yuchen
 * createTime 2023/2/24 13:35
 **/
@Slf4j
@Component
public class AliIotUtil {

    // https://help.aliyun.com/document_detail/169790.html
    // 阿里物联网生活平台自有APP开放WEB API密钥
    private static final String APP_KEY = "xxx";
    private static final String APP_SECRET = "xxxx";

    // 阿里物联网生活平台自有APP,APPSDK的appKey，安卓和ios通用
    private static final String OPEN_ID_APP_KEY = "xxxx";
    private static final String OPEN_ID_APP_SECRET = "xxxx";

    // 阿里物联网生活平台自有APP项目id
    private static final String PROJECT_ID = "xxxx";

    // 数据中心
    private static final String CN_SHANGHAI = "api.link.aliyun.com";//华东2（上海）
    private static final String AP_SOUTHEAST_1 = "ap-southeast-1.api-iot.aliyuncs.com";//新加坡
    private static final String EU_CENTRAL_1 = "eu-central-1.api-iot.aliyuncs.com";//德国（法兰克福）
    private static final String US_EAST_1 = "us-east-1.api-iot.aliyuncs.com";//美国（弗吉尼亚）

    @Autowired
    private RedisService redisService;

    public String bindIotUser(String imei,String productKey,String deviceName) {
        log.info("绑定iot设备和用户(bindIotUser),iotUser={},productKey={},deviceName={}",imei,productKey,deviceName);
        String cloudToken = getCloudToken();
        if(StrUtil.isBlank(cloudToken)){
            log.info("绑定iot设备和用户失败,cloudToken为空");
            return "";
        }
        //请求参数域名、path、request
        String host = this.getIotUserHost(imei,cloudToken);
        if(StrUtil.isBlank(host)){
            log.info("绑定iot设备和用户失败,查询不到用户iot地址");
            return "";
        }
        String path = "/living/cloud/user/binding/device/batch/bind";
        IoTApiRequest request = new IoTApiRequest();
        //设置api的版本
        request.setApiVer("1.0.1");
        //如果需要登陆，设置当前的会话的token
        request.setCloudToken(cloudToken);
        //设置接口的参数
        request.putParam("openId", imei);
        request.putParam("openIdAppKey",OPEN_ID_APP_KEY );
        request.putParam("projectId",PROJECT_ID );
        JSONObject object = new JSONObject();
        object.put("productKey",productKey);
        object.put("deviceName",deviceName);
        JSONArray array = new JSONArray();
        array.add(object);
        request.putParam("deviceList",array);
        try {
            JSONObject jsonObject = iotApiPost(host, path,request);
            JSONArray jsonArray = jsonObject.getJSONArray("data");
            if(jsonArray != null){
                JSONObject data = jsonArray.getJSONObject(0);
                if(null != data){
                    return data.getString("iotId")==null?"":data.getString("iotId");
                }
            }
        }catch (Exception e){
            log.info("bindIotUser,error:",e);
        }
        return "";
    }

    public String getIotUserHost(String openId,String cloudToken) {
        log.info("获取用户注册的区域,openId={}",openId);
        String host = redisService.getCacheObject(CacheConstants.ALI_REGION + openId);
        if(StrUtil.isNotBlank(host)){
            return host;
        }
        if(StrUtil.isBlank(cloudToken)){
            return "";
        }
        //请求参数域名、path、request
        String path = "/living/cloud/user/region/get";
        IoTApiRequest request = new IoTApiRequest();
        //设置api的版本
        request.setApiVer("1.0.1");
        request.setCloudToken(cloudToken);
        //设置接口的参数
        request.putParam("openId", openId);
        JSONObject jsonObject = iotApiPost(CN_SHANGHAI, path,request);
        String region = jsonObject.getString("data") == null?"":jsonObject.getString("data");
        host = this.getIotHost(region);
        // 刷新redis中iot用户区域地址缓存
        redisService.setCacheObject(CacheConstants.ALI_REGION+openId,host,7L, TimeUnit.DAYS);
        return host;
    }

    private String getIotHost(String region){
        log.info("region={}",region);
        switch (region){
            case "cn-shanghai":
                region = CN_SHANGHAI;
                break;
            case "ap-southeast-1":
                region = AP_SOUTHEAST_1;
                break;
            case "eu-central-1":
                region = EU_CENTRAL_1;
                break;
            case "us-east-1":
                region = US_EAST_1;
                break;
            default:
                region =  "";
                break;
        }
        return region;
    }


    public String getMqttEndpoint(String openId) {
        log.info("获取iot用户mqtt地址(getMqttEndpoint),openId={}",openId);
        String mqttEndpoint = redisService.getCacheObject(CacheConstants.ALI_MQTT + openId);
        if(StrUtil.isNotBlank(mqttEndpoint)){
            return mqttEndpoint;
        }
        String cloudToken = getCloudToken();
        if(StrUtil.isBlank(cloudToken)){
            log.info("绑定iot设备和用户失败,cloudToken为空");
            return "";
        }
        String host = this.getIotUserHost(openId,cloudToken);
        if(StrUtil.isBlank(host)){
            log.info("绑定iot设备和用户失败,查询不到用户iot地址");
            return "";
        }
        String path = "/living/account/region/get";
        IoTApiRequest request = new IoTApiRequest();
        request.setApiVer("1.0.2");
        request.putParam("type", "THIRD_AUTHCODE");
        request.putParam("authCode", openId);
        JSONObject jsonObject = iotAppPost( host, path,request);
        JSONObject data = jsonObject.getJSONObject("data");
        if(data!=null){
            mqttEndpoint = data.getString("mqttEndpoint") == null?"":data.getString("mqttEndpoint");
            if(StrUtil.isNotBlank(mqttEndpoint)){
                redisService.setCacheObject(CacheConstants.ALI_MQTT + openId,mqttEndpoint);
            }
        }
        return mqttEndpoint;
    }



    /**
     * description 获取cloud_token
     * 因为正式和测试使用的是同一个token,重新获取token,之前的token都会过期,
     * 做了缓存无法保证缓存的token,当前是可用的,所以每次都要重新获取
     **/
    private static String getCloudToken() {
        log.info("获取cloudCode");
        String cloudToken="";
        String path = "/cloud/token";
        try {
            IoTApiRequest request = new IoTApiRequest();
            //设置api的版本
            request.setApiVer("1.0.1");
            request.putParam("grantType", "project");
            request.putParam("res", PROJECT_ID);
            JSONObject jsonObject = iotApiPost(CN_SHANGHAI, path,request);
            JSONObject data = jsonObject.getJSONObject("data");
            if(null != data){
                cloudToken = data.getString("cloudToken") == null?"":data.getString("cloudToken");
            }
        }catch (Exception e){
            log.info("getCloudToken,error:",e);
        }
        log.info("获取cloudToken={}",cloudToken);
        return cloudToken;
    }

    public static JSONObject iotApiPost(String host, String path, IoTApiRequest request) {
        log.info("iotApiPost={},path={},params={}",host,path, JSONStrFormater.format(JSONObject.toJSONString(request)));
        try {
            IoTApiClientBuilderParams builderParams = new IoTApiClientBuilderParams();
            builderParams.setAppKey(APP_KEY);
            builderParams.setAppSecret(APP_SECRET);
            SyncApiClient syncClient = new SyncApiClient(builderParams);
            ApiResponse response = syncClient.postBody(host, path, request,true);
            String content = new String(response.getBody(), UTF_8);
            log.info("ResponseContent = {}",content);
            JSONObject jsonObject = JSONObject.parseObject(content);
            return jsonObject == null?new JSONObject():jsonObject;
        } catch (Exception e) {
            log.info("iotApiPost,error:",e);
        }
        return new JSONObject();
    }

    public static JSONObject iotAppPost(String host, String path, IoTApiRequest request) {
        log.info("iotApiPost={},path={},params={}",host,path,JSONStrFormater.format(JSONObject.toJSONString(request)));
        try {
            IoTApiClientBuilderParams builderParams = new IoTApiClientBuilderParams();
            builderParams.setAppKey(OPEN_ID_APP_KEY);
            builderParams.setAppSecret(OPEN_ID_APP_SECRET);
            SyncApiClient syncClient = new SyncApiClient(builderParams);
            ApiResponse response = syncClient.postBody(host, path, request,true);
            String content = new String(response.getBody(), UTF_8);
            log.info("ResponseContent = {}",content);
            JSONObject jsonObject = JSONObject.parseObject(content);
            return jsonObject == null?new JSONObject():jsonObject;
        } catch (Exception e) {
            log.info("iotApiPost,error:",e);
        }
        return new JSONObject();
    }

    public static JSONObject iotApiGet(String host, String path, Map<String, String> headers, Map<String, String> querys) {
        log.info("iotApiGet={},path={},params={}",host,path,JSONStrFormater.format(JSONObject.toJSONString(querys)));
        try {
            IoTApiClientBuilderParams builderParams = new IoTApiClientBuilderParams();
            builderParams.setAppKey(APP_KEY);
            builderParams.setAppSecret(APP_SECRET);
            SyncApiGetClient syncClient = new SyncApiGetClient(builderParams);
            ApiResponse response = syncClient.doGet(host, path, true, headers, querys);
            String content = new String(response.getBody(), UTF_8);
            log.info("ResponseContent = {}",content);
            JSONObject jsonObject = JSONObject.parseObject(content);
            return jsonObject == null?new JSONObject():jsonObject;
        } catch (Exception e) {
            log.info("iotApiPost,error:",e);
        }
        return new JSONObject();
    }

    public static void main(String[] args) {
        String host ="";
//        String host = CN_SHANGHAI;
        //请求参数域名、path、request
        String path = "/living/account/region/get";
        IoTApiRequest request = new IoTApiRequest();
        //设置api的版本
        request.setApiVer("1.0.2");
        //如果需要登陆，设置当前的会话的token
//        request.setCloudToken(cloudToken);
        //设置接口的参数
        request.putParam("type", "THIRD_AUTHCODE");
        request.putParam("authCode", "866728061690060");
        JSONObject jsonObject = iotAppPost( host, path,request);
        log.info("return={}",jsonObject);
    }

}