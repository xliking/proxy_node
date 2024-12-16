package com.easy.utils.proxy;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * @author Administrator
 */
public class GetProxyUrlUtil {

    public static List<String> getLink(String resJson, String path) {
        JSONObject object = JSONObject.parseObject(resJson);
        // 所有的列表 - clientStats 里面包含了所有的用户
        JSONArray jsonArray = object.getJSONArray("obj");
        JSONArray urlArray = new JSONArray();
        for (Object obj : jsonArray) {
            JSONObject prObj = (JSONObject) obj;
            String port = prObj.getString("port");
            // 节点是否启用
            String enable = prObj.getString("enable");
            if (!"true".equals(enable)) {
                continue;
            }
            // 节点类型  vless / vmess / shadowsocks 等等
            String protocol = prObj.getString("protocol");
            // 获取所有没有禁用的用户
            JSONObject settings = prObj.getJSONObject("settings");
            JSONArray clients = settings.getJSONArray("clients");
            // 有可能为空
            if (clients == null || clients.isEmpty()) {
                continue;
            }
            // 所有的用户
            JSONArray allUser = getAllUser(clients);
            JSONObject streamSettings = prObj.getJSONObject("streamSettings");
            // tcp 之类的
            String network = streamSettings.getString("network");
            // 加密方式
            String security = streamSettings.getString("security");
            // 伪装类型
            JSONObject tcpSettings = streamSettings.getJSONObject("tcpSettings");
            JSONObject header = null;
            JSONArray host = null;
            String type = null;
            if(tcpSettings != null){
                header = tcpSettings.getJSONObject("header");
                if(header != null){
                    type = header.getString("type");
                    // 伪装类型
                    if(type != null){
                        JSONObject request = header.getJSONObject("request");
                        JSONObject headers = null;
                        if (request != null){
                            headers = request.getJSONObject("headers");
                            // 伪装域名
                            host = headers.getJSONArray("Host");
                        }
                    }
                }
            }
            String hostStr = null;
            if (host != null && !host.isEmpty()) {
                hostStr = host.getString(0);
            }
            for (Object objUser : allUser) {
                JSONObject user = (JSONObject) objUser;
                String remarks = user.getString("remarks");
                String userId = user.getString("id");
                urlArray.add(buildObj(remarks, path, port, userId, network, protocol, type, hostStr));
            }
        }
        return buildUrl(urlArray);
    }


    /**
     * 根据  protocol  这个字段 来区分不同的节点类型
     */
    private static List<String> buildUrl(JSONArray jsonArray) {
        if(jsonArray == null || jsonArray.isEmpty()){
            return new ArrayList<>();
        }
        List<String> arrayList = new ArrayList<>();
        for (Object obj : jsonArray) {
            JSONObject jsonObject = (JSONObject) obj;
            String protocol = jsonObject.getString("protocol");
            String ps = jsonObject.getString("ps");
            String address = jsonObject.getString("add");
            int port = jsonObject.getIntValue("port");
            String uuid = jsonObject.getString("id");
            String net = jsonObject.getString("net");
            String type = jsonObject.getString("type");
            String host = jsonObject.getString("host");
            String tls = jsonObject.getString("tls");
            // 根据协议类型分支处理
            if ("vless".equalsIgnoreCase(protocol)) {
                // VLESS处理，与之前的示例相同，指定encryption=none, security=none/tls, headerType=http等
                StringBuilder params = new StringBuilder("encryption=none");
                if (tls != null && !tls.isEmpty() && !tls.equalsIgnoreCase("none")) {
                    params.append("&security=tls");
                } else {
                    params.append("&security=none");
                }
                if ("http".equalsIgnoreCase(type)) {
                    params.append("&headerType=http");
                }
                if (host != null && !host.isEmpty()) {
                    params.append("&host=").append(host);
                }
                String vlessLink = String.format("vless://%s@%s:%d?%s#%s", uuid, address, port, params.toString(), ps);
                arrayList.add(vlessLink);
            } else if ("vmess".equalsIgnoreCase(protocol)) {
                // VMess处理
                JSONObject vmessJson = new JSONObject();
                vmessJson.put("v", "2");
                vmessJson.put("ps", ps);
                vmessJson.put("add", address);
                vmessJson.put("port", port);
                vmessJson.put("id", uuid);
                vmessJson.put("aid", 0);
                vmessJson.put("net", net != null ? net : "tcp");
                vmessJson.put("type", type != null ? type : "none");
                vmessJson.put("host", host != null ? host : "");
                vmessJson.put("path", "");
                vmessJson.put("tls", tls != null ? tls : "none");
                String vmessStr = vmessJson.toJSONString();
                String base64Str = Base64.getEncoder().encodeToString(vmessStr.getBytes());
                String vmessLink = "vmess://" + base64Str;
                arrayList.add(vmessLink);
            } else {
                // 其他协议类型，后续添加
            }
        }
        return arrayList;
    }


    private static JSONObject buildObj(String remark, String listen, String port, String id, String network, String protocol,
                                   String obfuscationType, String obfuscationDomain) {
        return new JSONObject()
                .fluentPut("v", "2")
                .fluentPut("protocol", protocol)
                .fluentPut("ps", remark)
                .fluentPut("add", listen)
                .fluentPut("port", Integer.valueOf(port))
                .fluentPut("id", id)
                .fluentPut("net", network)
                .fluentPut("type", obfuscationType)
                .fluentPut("host", obfuscationDomain)
//                .fluentPut("tls", tls)
                .fluentPut("aid", 0)
                .fluentPut("path", null);
    }

    private static JSONArray getAllUser(JSONArray clients) {
        JSONArray jsonArray = new JSONArray();
        for (Object client : clients) {
            JSONObject clientObj = (JSONObject) client;
            String email = clientObj.getString("email");
            String userId = clientObj.getString("id");
            jsonArray.add(new JSONObject()
                    .fluentPut("remarks", email)
                    .fluentPut("id", userId));
        }
        return jsonArray;
    }


}
