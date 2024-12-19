package com.easy.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author muchi
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
public class IpResVO {

    private String ip;

    // ipv4 or ipv6
    private String type;

    // 所属企业
    private String companyName;

    // 国家 区域 地址
    private String countryAddress;

    // 是否已经滥用
    private Boolean isAbuser;

    // 是否托管
    private Boolean isCloudProvider;

    // 是否 是 已知代理
    private Boolean isProxy;

    // 是否 中继
    private Boolean isRelay;

    // 是否 vpn
    private Boolean isVpn;


}
