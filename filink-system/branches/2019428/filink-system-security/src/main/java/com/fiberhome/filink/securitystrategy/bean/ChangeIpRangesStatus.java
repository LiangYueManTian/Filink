package com.fiberhome.filink.securitystrategy.bean;


import lombok.Data;

import java.util.List;
@Data
public class ChangeIpRangesStatus {
    private List<String> rangeIds;
    private String rangeStatus;
}
