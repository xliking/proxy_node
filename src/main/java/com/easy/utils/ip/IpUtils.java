package com.easy.utils.ip;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.easy.pojo.vo.IpResVO;
import com.easy.utils.okhttp3.OkHttpUtils;
import okhttp3.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xlike
 */
public class IpUtils {

    private static final String IP_REGISTRY_URL = "https://api.ipregistry.co/?key=ira_YgGPCzMLCfgWGZyEas0oqjUC6N6jWs329qSu";


    public static List<IpResVO> getIpHttpRes(String ip) throws IOException {
        // ip格式化 如果有，号，则根据，号或如果有空格 则空格分割
        List<String> ipList = ipFormat(ip);
        // List转化为JSON字符串
        String json = JSONObject.toJSONString(ipList);
        Response response = OkHttpUtils.post(IP_REGISTRY_URL, OkHttpUtils.createJsonRequestBody(json));
        String bodyAsString = OkHttpUtils.getResponseBodyAsString(response);
        if(bodyAsString == null){
            return null;
        }
        JSONObject resObj = JSONObject.parseObject(bodyAsString);
        JSONArray resArray = resObj.getJSONArray("results");
        List<IpResVO> resVOList = new ArrayList<>();
        resArray.forEach(item -> {
            IpResVO ipResVO = new IpResVO();
            JSONObject resObjItem = (JSONObject) item;
            ipResVO.setIp(resObjItem.getString("ip"));
            if(resObjItem.containsKey("code")){
                // 查询出错的
                ipResVO.setCountryAddress("INVALID_IP_ADDRESS");
            }else {
                ipResVO.setType(resObjItem.getString("type"));
                JSONObject companyJson = resObjItem.getJSONObject("company");
                String name = companyJson.getString("name");
                // 获取公司名称
                ipResVO.setCompanyName(name);
                // 地址
                JSONObject locationJson = resObjItem.getJSONObject("location");
                String city = locationJson.getString("city");
                JSONObject countryJson = locationJson.getJSONObject("country");
                String countryName = countryJson.getString("name");
                ipResVO.setCountryAddress(countryName + " " + city);
                JSONObject securityJson = resObjItem.getJSONObject("security");
                Boolean isProxy = securityJson.getBoolean("is_proxy");
                ipResVO.setIsProxy(isProxy);
                Boolean isCloudProvider = securityJson.getBoolean("is_cloud_provider");
                ipResVO.setIsCloudProvider(isCloudProvider);
                Boolean isAbuser = securityJson.getBoolean("is_abuser");
                ipResVO.setIsAbuser(isAbuser);
                Boolean isRelay = securityJson.getBoolean("is_relay");
                ipResVO.setIsRelay(isRelay);
                Boolean isVpn = securityJson.getBoolean("is_vpn");
                ipResVO.setIsVpn(isVpn);
            }
            resVOList.add(ipResVO);
        });
        OkHttpUtils.closeResponse(response);
        return resVOList;
    }


    /**
     * 提取输入字符串中的所有IPv4地址，并将它们格式化为JSON数组的字符串形式。
     *
     * @param input 输入的IP字符串，可能包含逗号、空格或URL等。
     * @return 格式化后的JSON数组字符串，例如 ["154.21.116.52"]
     */
    public static List<String> ipFormat(String input) {
        if (input == null || input.isEmpty()) {
            return new ArrayList<>();
        }
        // 定义IPv4地址的正则表达式，确保IP地址前后不被数字干扰
        String ipv4Pattern = "(?<!\\d)(?:(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)\\.){3}" +
                "(?:25[0-5]|2[0-4]\\d|1\\d\\d|[1-9]?\\d)(?!\\d)";
        Pattern pattern = Pattern.compile(ipv4Pattern);
        Matcher matcher = pattern.matcher(input);
        // 使用LinkedHashSet去除重复，并保持插入顺序
        Set<String> ipSet = new LinkedHashSet<>();
        // 查找所有匹配的IP地址
        while (matcher.find()) {
            String ip = matcher.group();
            ipSet.add(ip);
        }
        return new ArrayList<>(ipSet);
    }


}
