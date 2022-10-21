package com.example.jiuzhou.user.model.fee;

import lombok.Data;

/**
 * @author Appartion
 * @data 2022/10/21
 * 一入代码深似海，从此生活是路人
 */
@Data
public class Rates {
    public RateModel rateMode = new RateModel();	//早班
    public RateModel rateMode1 = new RateModel();	//中班
    public RateModel rateMode2 = new RateModel();	//晚班
}
