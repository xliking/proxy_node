package com.easy.controller;

import com.easy.pojo.dto.IpDTO;
import com.easy.pojo.vo.IpResVO;
import com.easy.resutils.R;
import com.easy.utils.ip.IpUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author muchi
 */
@RestController
@CrossOrigin
public class IpController {


    @PostMapping("/ip")
    public R<List<IpResVO>> getIpRes(@RequestBody IpDTO ipDTO) {
        if (ipDTO.getQuery() == null || ipDTO.getQuery().isEmpty()) {
            return R.failed("IP不能为空");
        }
        List<IpResVO> ipList;
        try {
            ipList = IpUtils.getIpHttpRes(ipDTO.getQuery());
        } catch (IOException e) {
            ipList = Collections.emptyList();
        }
        return R.ok(ipList);
    }


}
