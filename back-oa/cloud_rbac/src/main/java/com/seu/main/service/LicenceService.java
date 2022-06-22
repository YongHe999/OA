package com.seu.main.service;

import com.seu.util.entity.vo.ResultVo;
import org.springframework.stereotype.Service;

/**
 * @author Ajie
 * @version 1.0
 * @date 2020/3/13 09:08
 * @description
 */
@Service
public interface LicenceService {
    ResultVo save(String licenceCode);

    ResultVo getLicence();

    ResultVo del();
}
