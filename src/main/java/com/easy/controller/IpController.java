package com.easy.controller;

import com.easy.pojo.vo.IpResVO;
import com.easy.resutils.R;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

/**
 * @author muchi
 */
@RestController
public class IpController {

    @GetMapping("/ip")
    public R<List<IpResVO>> getIpRes() {
        IpResVO ipResVO = new IpResVO();
        List<IpResVO> ipResVOList = List.of();
        return R.ok(ipResVOList);
    }


}
