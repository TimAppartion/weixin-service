package com.example.jiuzhou.common.utils;

import com.example.jiuzhou.user.model.AbpMonthlyCars;
import com.example.jiuzhou.user.model.fee.*;

import java.util.*;

/**
 *
 * 费率计算
 * @author litong
 * @data 2022/10/21
 * 一入代码深似海，从此生活是路人
 */
public class RateCalculate {

    /**
     * 费率计算，增加早班，中班，晚班费率处理
     *
     * @param inCarTime
     * @param outCarTime
     * @param cartype
     * @param PlateNumber
     * @param ratemodel
     * @param parkType
     * @return
     */
    static double freecount = 0;

    public static RateCalculateModel ProcessRateCalculate(Date inCarTime, Date outCarTime, int cartype,
                                                          String PlateNumber, Rates rates, int parkType, AbpMonthlyCars monthCar) {
        RateCalculateModel rateCalculateModel = new RateCalculateModel();
        freecount = 0;

        // inCarTime = AbDateUtil.getDateByFormat("2018-12-29 08:00:00",
        // AbDateUtil.dateFormatYMDHM2);
        // outCarTime = AbDateUtil.getDateByFormat("2018-12-29 08:19:00",
        // AbDateUtil.dateFormatYMDHM2);

        if (inCarTime.getTime() > outCarTime.getTime()) {
            rateCalculateModel.exceptionMsg = "入场时间大于出场时间";
            return rateCalculateModel;
        }
        // 早班费率必须是启用状态
        if (!rates.rateMode.IsActive) {
            rateCalculateModel.exceptionMsg = "费率未启用";
            return rateCalculateModel;
        }
        // 将入场时间、出场时间，往前推进早班费率起始时间值
        Date rateInCarTime = AbDateUtil.getDateByOffset(
                AbDateUtil.getDateByOffset(inCarTime, Calendar.HOUR,
                        -Integer.parseInt(rates.rateMode.TimeSettingList.get(0).beginTime.split(":")[0])),
                Calendar.MINUTE, -Integer.parseInt(rates.rateMode.TimeSettingList.get(0).beginTime.split(":")[1]));

        Date rateOutCarTime = AbDateUtil.getDateByOffset(
                AbDateUtil.getDateByOffset(outCarTime, Calendar.HOUR,
                        -Integer.parseInt(rates.rateMode.TimeSettingList.get(0).beginTime.split(":")[0])),
                Calendar.MINUTE, -Integer.parseInt(rates.rateMode.TimeSettingList.get(0).beginTime.split(":")[1]));

        int days = AbDateUtil.getOffectDay(
                AbDateUtil.getDateByFormat(
                        AbDateUtil.getStringByFormat(rateOutCarTime.getTime(), AbDateUtil.dateFormatYMD2) + " 00:00:00",
                        AbDateUtil.dateFormatYMDHM2).getTime(),
                AbDateUtil.getDateByFormat(
                        AbDateUtil.getStringByFormat(rateInCarTime.getTime(), AbDateUtil.dateFormatYMD2) + " 00:00:00",
                        AbDateUtil.dateFormatYMDHM2).getTime())
                - 1;// 计算整天数，判断是否需要比较进场时间与出场时间的大小对比
        if (days > 0) {
            rateCalculateModel.CalculateMoney = ProcessDayMaxMoney(rates, cartype, days, monthCar);
        }
        // double preMoney = rateCalculateModel.CalculateMoney;

        rateCalculateModel.ParkTime = AbDateUtil.getOffectMinutes(outCarTime.getTime(), inCarTime.getTime());

        if (monthCar.getMonthyType() != null && monthCar.getMonthyType().equals("MonthyAll")) {
            rateCalculateModel.CalculateMoney = 0;
            return rateCalculateModel;
        }

        if (monthCar.getMonthyType() == null || !monthCar.getMonthyType().equals("MonthyMorning")) {
            rateCalculateModel.CalculateMoney += ProcessCarTime(rates, rates.rateMode, cartype, rateInCarTime, 0,
                    rateOutCarTime, days);
        }

        if (monthCar.getMonthyType() == null || !monthCar.getMonthyType().equals("MonthyMiddle")) {
            rateCalculateModel.CalculateMoney += ProcessCarTime(rates, rates.rateMode1, cartype, rateInCarTime, 0,
                    rateOutCarTime, days);
        }

        if (monthCar.getMonthyType() == null || !monthCar.getMonthyType().equals("MonthyNight")) {
            rateCalculateModel.CalculateMoney += ProcessCarTime(rates, rates.rateMode2, cartype, rateInCarTime, 0,
                    rateOutCarTime, days);
        }
        // double preMoney2 = rateCalculateModel.CalculateMoney;
        if (AbDateUtil
                .getDateByFormat(
                        AbDateUtil.getStringByFormat(rateOutCarTime.getTime(), AbDateUtil.dateFormatYMD2) + " 00:00:00",
                        AbDateUtil.dateFormatYMDHM2)
                .getTime() > AbDateUtil.getDateByFormat(
                AbDateUtil.getStringByFormat(rateInCarTime.getTime(), AbDateUtil.dateFormatYMD2) + " 00:00:00",
                AbDateUtil.dateFormatYMDHM2).getTime()) {
            if (monthCar.getMonthyType() == null || !monthCar.getMonthyType().equals("MonthyMorning")) {
                rateCalculateModel.CalculateMoney += ProcessCarTime(rates, rates.rateMode, cartype, rateInCarTime, 1,
                        rateOutCarTime, days);
            }

            if (monthCar.getMonthyType() == null || !monthCar.getMonthyType().equals("MonthyMiddle")) {
                rateCalculateModel.CalculateMoney += ProcessCarTime(rates, rates.rateMode1, cartype, rateInCarTime, 1,
                        rateOutCarTime, days);
            }

            if (monthCar.getMonthyType() == null || !monthCar.getMonthyType().equals("MonthyNight")) {
                rateCalculateModel.CalculateMoney += ProcessCarTime(rates, rates.rateMode2, cartype, rateInCarTime, 1,
                        rateOutCarTime, days);
            }
        }
        return rateCalculateModel;
    }

    /**
     * 处理半段时间金额
     * @param rates
     * @param rateMode
     * @param cartype
     * @param rateInCarTime
     * @param Type
     * @param rateOutCarTime
     * @param days
     * @return
     */
    private static double ProcessCarTime(Rates rates, RateModel rateMode, int cartype, Date rateInCarTime,
                                         int Type, Date rateOutCarTime, int days) {
        Date endTime = rateOutCarTime;
        Date beginTime = rateInCarTime;
        Date tempTime = rateInCarTime;

        if (rateMode == null)
            return 0;

        if (rateMode.CarRateList == null)
            return 0;

        if (Type == 1 && days < 0) {
            return 0;
        }
        ParkTime parktime = new ParkTime();
        if (days >= 0) {
            if (Type == 0) { // 进场时间
                endTime = AbDateUtil.getDateByFormat(
                        AbDateUtil.getStringByFormat(beginTime.getTime(), AbDateUtil.dateFormatYMD2) + " 23:59:59",
                        AbDateUtil.dateFormatYMDHM2);// 结束时间

                tempTime = beginTime;
            } else { // 出场时间
                beginTime = AbDateUtil.getDateByFormat(
                        AbDateUtil.getStringByFormat(endTime.getTime(), AbDateUtil.dateFormatYMD2) + " 00:00:00",
                        AbDateUtil.dateFormatYMDHM2);// 结束时间
                tempTime = endTime;
            }
        }

        parktime.beginTime = AbDateUtil
                .getDateByFormat(AbDateUtil.getStringByFormat(tempTime.getTime(), AbDateUtil.dateFormatYMD2) + " "
                        + rateMode.TimeSettingList.get(0).beginTime + ":00", AbDateUtil.dateFormatYMDHM2);

        parktime.beginTime = AbDateUtil.getDateByOffset(
                AbDateUtil.getDateByOffset(parktime.beginTime, Calendar.HOUR,
                        -Integer.parseInt(rates.rateMode.TimeSettingList.get(0).beginTime.split(":")[0])),
                Calendar.MINUTE, -Integer.parseInt(rates.rateMode.TimeSettingList.get(0).beginTime.split(":")[1]));

        parktime.endTime = AbDateUtil
                .getDateByFormat(AbDateUtil.getStringByFormat(tempTime.getTime(), AbDateUtil.dateFormatYMD2) + " "
                        + rateMode.TimeSettingList.get(0).endTime + ":00", AbDateUtil.dateFormatYMDHM2);

        parktime.endTime = AbDateUtil.getDateByOffset(
                AbDateUtil.getDateByOffset(parktime.endTime, Calendar.HOUR,
                        -Integer.parseInt(rates.rateMode.TimeSettingList.get(0).beginTime.split(":")[0])),
                Calendar.MINUTE, -Integer.parseInt(rates.rateMode.TimeSettingList.get(0).beginTime.split(":")[1]));

        if (parktime.endTime.getTime() < parktime.beginTime.getTime()) {
            parktime.endTime = AbDateUtil.getDateByOffset(parktime.endTime, Calendar.DAY_OF_YEAR, 1);
        }

        if (endTime.getTime() <= parktime.beginTime.getTime())// 如果出场时间小于费率开始时间
            return 0;

        if (beginTime.getTime() > parktime.beginTime.getTime())// 如果停车时间大于费率开始时间
        {
            parktime.beginTime = beginTime;
        }

        if (endTime.getTime() < parktime.endTime.getTime()) {
            parktime.endTime = endTime;
        }

        parktime.timeTotal = AbDateUtil.getOffectMinutes(parktime.endTime.getTime(), parktime.beginTime.getTime());

        if (rateMode.TimeSettingList.get(0).RateMethod.equals("1"))// 按次收
        {
            for (CarRateModel carfee : rateMode.CarRateList) {
                if (cartype == StringUilt.toInt(carfee.CarType)) {
                    double freeTime = StringUilt.toInt(carfee.FreeTime);
                    if (parktime.timeTotal > freeTime)// 如果收费时间总和小于免费时间 收费金额为0
                    {
                        parktime.parkMoney = carfee.CarFeeScaleList.get(0).RateMoney;
                        break;
                    } else {
                        parktime.parkMoney = 0;
                        break;
                    }
                }
            }
            return parktime.parkMoney;
        }

        for (CarRateModel carfee : rateMode.CarRateList) {
            if (cartype == StringUilt.toInt(carfee.CarType)) {
                // 时间排序
                Comparator comp = new Comparator() {
                    public int compare(Object o1, Object o2) {
                        CarTimeQuantumModel p1 = (CarTimeQuantumModel) o1;
                        CarTimeQuantumModel p2 = (CarTimeQuantumModel) o2;
                        if (p1.beginTime < p2.beginTime)
                            return -1;
                        else if (p1.beginTime == p2.beginTime)
                            return 0;
                        else if (p1.beginTime > p2.beginTime)
                            return 1;
                        return 0;
                    }
                };
                Collections.sort(carfee.CarTimeQuantumList, comp);
                // double preMoney = parktime.parkMoney;
                // #region 免费时段
                double freeTime = StringUilt.toInt(carfee.FreeTime);
                if (parktime.timeTotal <= freeTime)// 如果收费时间总和小于免费时间 收费金额为0
                {
                    parktime.parkMoney = 0;
                    return parktime.parkMoney;

                }
                // #endregion

                if (carfee.ContentFreeTimeFlag == false && parktime.timeTotal > freeTime && freecount == 0)// 免费时间是否包含在收费时间段内
                {
                    parktime.timeTotal -= freeTime;
                    freecount++;
                }

                for (int i = 0; i < carfee.CarTimeQuantumList.size(); i++) {// 获取时间段的长度

                    if (carfee.CarTimeQuantumList.get(i).RateMethod.equals("0")) {// 判断时间段收费方式是分钟还是小时:
                        // 0分钟，1小时
                        if (parktime.timeTotal >= carfee.CarTimeQuantumList.get(i).beginTime
                                && parktime.timeTotal <= carfee.CarTimeQuantumList.get(i).endTime
                                && parktime.timeTotal != carfee.CarTimeQuantumList.get(i).beginTime) {// 获取时间段的结束时间
                            for (int m = 0; m <= i; m++) {
                                parktime.parkMoney += StringUilt
                                        .toDouble(carfee.CarTimeQuantumList.get(m).TimeQuantumMoney);
                            }
                        } else if (parktime.timeTotal > carfee.CarTimeQuantumList
                                .get(carfee.CarTimeQuantumList.size() - 1).endTime
                                && i == carfee.CarTimeQuantumList.size() - 1) {
                            // 超过时间段，xx元一分钟
                            double parktimeminutes = (parktime.timeTotal
                                    - carfee.CarTimeQuantumList.get(carfee.CarTimeQuantumList.size() - 1).endTime);
                            double halfhours = parktimeminutes % Integer.parseInt( carfee.CarFeeScaleList.get(0).RateTime) == 0
                                    ? parktimeminutes / Integer.parseInt( carfee.CarFeeScaleList.get(0).RateTime)
                                    : Math.ceil(parktimeminutes / Integer.parseInt( carfee.CarFeeScaleList.get(0).RateTime));
                            for (int m = 0; m < carfee.CarTimeQuantumList.size(); m++) {
                                parktime.parkMoney += StringUilt
                                        .toDouble(carfee.CarTimeQuantumList.get(m).TimeQuantumMoney);
                            }
                            if (carfee.CarTimeQuantumList.get(0).TimeQuantumMoney != null) {
                                if (halfhours > 0) {
                                    parktime.parkMoney += halfhours * (carfee.CarFeeScaleList.get(0).RateMoney);
                                }
                            }
                        }
                    }

                    else {

                        if (parktime.timeTotal / 60 >= carfee.CarTimeQuantumList.get(i).beginTime / 60
                                && parktime.timeTotal / 60 <= carfee.CarTimeQuantumList.get(i).endTime / 60) {
                            for (int m = 0; m <= i; m++) {
                                parktime.parkMoney += StringUilt
                                        .toDouble(carfee.CarTimeQuantumList.get(m).TimeQuantumMoney);
                            }
                        } else if (i < 1) {
                            // 超过时间段，xx元一小时
                            double parktimeminutes = (parktime.timeTotal
                                    - carfee.CarTimeQuantumList.get(carfee.CarTimeQuantumList.size() - 1).endTime);
                            double hours = parktimeminutes % 12 == 0 ? parktimeminutes / 12
                                    : Math.ceil(parktimeminutes / 12);
                            for (int m = 0; m < carfee.CarTimeQuantumList.size(); m++) {
                                parktime.parkMoney += StringUilt
                                        .toDouble(carfee.CarTimeQuantumList.get(m).TimeQuantumMoney);
                            }
                            if (carfee.CarTimeQuantumList.get(0).TimeQuantumMoney != null) {
                                if (hours > 0) {
                                    parktime.parkMoney += hours
                                            * StringUilt.toDouble(carfee.CarTimeQuantumList.get(0).TimeQuantumMoney);
                                }
                            }
                        }
                    }
                }

            }
        }
        double DayMaxMoney = 0;
        for (CarRateModel carfee : rateMode.CarRateList) {
            if (cartype == StringUilt.toInt(carfee.CarType)) {
                if (rateMode.TimeSettingList.get(0).RateMethod.equals("1"))// 按次
                {

                    DayMaxMoney += (int) carfee.OnceMaxMoney;
                    break;
                }
                if (rateMode.TimeSettingList.get(0).RateMethod.equals("0"))// 按时间
                {
                    DayMaxMoney += (int) carfee.DayMaxMoney;
                    break;
                }
            }
        }

        if (parktime.parkMoney > DayMaxMoney)
            return DayMaxMoney;
        return parktime.parkMoney;
    }

    /**
     * 处理天最高收费
     *
     * @param days
     * @return
     */
    private static double ProcessDayMaxMoney(Rates rates, int cartype, int days, AbpMonthlyCars monthCar) {
        double DayMaxMoney = 0;

        if (monthCar.getMonthyType() != null && monthCar.getMonthyType().equals("MonthyAll"))
            return 0;
        if (monthCar.getMonthyType() == null || !monthCar.getMonthyType().equals("MonthyMorning")) {
            // 早班
            for (CarRateModel carfee : rates.rateMode.CarRateList) {
                if (cartype == StringUilt.toInt(carfee.CarType)) {
                    if (rates.rateMode.TimeSettingList.get(0).RateMethod.equals("1"))// 按次
                    {
                        DayMaxMoney += (int) carfee.OnceMaxMoney;
                        break;
                    }
                    if (rates.rateMode.TimeSettingList.get(0).RateMethod.equals("0"))// 按时间
                    {
                        DayMaxMoney += (int) carfee.DayMaxMoney;
                        break;
                    }
                }
            }
        }

        if (monthCar.getMonthyType() == null || !monthCar.getMonthyType().equals("MonthyMiddle")) {
            if (rates.rateMode1 != null && rates.rateMode1.CarRateList != null && rates.rateMode1.IsActive)// 中班
                for (CarRateModel carfee : rates.rateMode1.CarRateList) {
                    if (cartype == StringUilt.toInt(carfee.CarType)) {
                        if (rates.rateMode1.TimeSettingList.get(0).RateMethod.equals("1"))// 按次
                        {
                            DayMaxMoney += (int) carfee.OnceMaxMoney;
                            break;
                        }
                        if (rates.rateMode1.TimeSettingList.get(0).RateMethod.equals("0"))// 按时间
                        {
                            DayMaxMoney += (int) carfee.DayMaxMoney;
                            break;
                        }
                    }
                }
        }

        if (monthCar.getMonthyType() == null || !monthCar.getMonthyType().equals("MonthyNight")) {
            if (rates.rateMode1 != null && rates.rateMode2.CarRateList != null && rates.rateMode2.IsActive)// 晚班
                for (CarRateModel carfee : rates.rateMode2.CarRateList) {
                    if (cartype == StringUilt.toInt(carfee.CarType)) {
                        if (rates.rateMode2.TimeSettingList.get(0).RateMethod.equals("1"))// 按次
                        {
                            DayMaxMoney += (int) carfee.OnceMaxMoney;
                            break;
                        }
                        if (rates.rateMode2.TimeSettingList.get(0).RateMethod.equals("0"))// 按时间
                        {
                            DayMaxMoney += (int) carfee.DayMaxMoney;
                            break;
                        }
                    }
                }
        }

        return DayMaxMoney * days;
    }

    /**
     *
     * @param inCarTime
     * @param outCarTime
     * @param cartype
     * @param PlateNumber
     * @param ratemodel
     * @return
     */
    public static RateCalculateModel RateCalculate11(Date inCarTime, Date outCarTime, int cartype, String PlateNumber,
                                                     RateModel ratemodel) {
        // String PlateNumber = "";
        RateCalculateModel rateclaculatemodel = new RateCalculateModel();
        // RateModel ratemodel =
        // Newtonsoft.Json.JsonConvert.DeserializeObject<RateModel>(RateJson);
        // #region 时间合理性和费率是否启用
        if (inCarTime.getTime() > outCarTime.getTime()) {
            rateclaculatemodel.exceptionMsg = "入场时间大于出场时间";
            return rateclaculatemodel;
        }
        if (ratemodel.IsActive == false) {
            rateclaculatemodel.exceptionMsg = "费率未启用";
            return rateclaculatemodel;
        }
        // #endregion
        double totalParkTime = 0;
        // TimeSpan ts = outCarTime.Date.Subtract(inCarTime.Date);
        // int days = ts.Days;
        int days = AbDateUtil.getOffectDay(outCarTime.getTime(), inCarTime.getTime());
        // #region 对停车时间分天处理

        // 设置list 对于多天收费分段保存收费时间
        // 对头尾进行处理
        List<ParkTime> timelist = new ArrayList<ParkTime>();
        double qEnd = 0;// 结束时间段 为前一个的开始
        for (int i = 0; i <= days; i++) {
            ParkTime parktime = new ParkTime();

            Calendar c1 = Calendar.getInstance();
            c1.setTime(inCarTime);
            c1.add(Calendar.DATE, i);
            String beginTimeStr = AbDateUtil.getStringByFormat(c1.getTime(), AbDateUtil.dateFormatYMD2) + " "
                    + ratemodel.TimeSettingList.get(0).beginTime + ":00";
            String endTimeStr = AbDateUtil.getStringByFormat(c1.getTime(), AbDateUtil.dateFormatYMD2) + " "
                    + ratemodel.TimeSettingList.get(0).endTime + ":00";
            parktime.beginTime = AbDateUtil.getDateByFormat(beginTimeStr, AbDateUtil.dateFormatYMDHM2);
            parktime.endTime = AbDateUtil.getDateByFormat(endTimeStr, AbDateUtil.dateFormatYMDHM2);
            // parktime.beginTime =
            // Convert.ToDateTime(inCarTime.AddDays(i).Date.ToString("yyyy-MM-dd")
            // + " " + ratemodel.TimeSettingList[0].beginTime);
            // parktime.endTime =
            // Convert.ToDateTime(inCarTime.AddDays(i).Date.ToString("yyyy-MM-dd")
            // + " " + ratemodel.TimeSettingList[0].endTime);
            if (i == 0) {
                if (inCarTime.getTime() > parktime.beginTime.getTime()) {
                    parktime.beginTime = inCarTime;
                }
                // parktime.beginTime = DateTime.Compare(inCarTime,
                // parktime.beginTime) > 0 ? inCarTime : parktime.beginTime;
                parktime.quantumBegin = 0;
            }
            if (i == days) {
                if (parktime.endTime.getTime() > outCarTime.getTime()) {
                    parktime.endTime = outCarTime;
                }
                // parktime.endTime = DateTime.Compare(parktime.endTime,
                // outCarTime) > 0 ? outCarTime : parktime.endTime;
            }
            parktime.timeTotal = AbDateUtil.getOffectMinutes(parktime.endTime.getTime(), parktime.beginTime.getTime());

            if ("1".equals(ratemodel.TimeSettingList.get(0).ManyDayMethod))// 0
            // 按停车总和
            // 1
            // 分天计算
            {
                parktime.quantumBegin = 0;
                parktime.quantumEnd = parktime.timeTotal > 0 ? parktime.timeTotal : 0;

            } else {
                parktime.quantumBegin = qEnd;
                parktime.quantumEnd = (qEnd + parktime.timeTotal) > 0 ? (qEnd + parktime.timeTotal) : 0;
                qEnd = parktime.quantumEnd;
            }

            totalParkTime += parktime.timeTotal;
            rateclaculatemodel.ParkTime += parktime.timeTotal;
            timelist.add(parktime);
        }

        // #endregion
        // #region 获取车辆入场前当天已收金额
        // List<BusinessDetail> carBusinessDetails =
        // _abpBusinessDetailRepository.GetAll().Where(bd => bd.CarPayTime >=
        // inCarTime.Date && bd.CarPayTime <= inCarTime && bd.PlateNumber ==
        // PlateNumber).ToList();
        // timelist[0].alreadyCharge = carBusinessDetails.Sum(bd =>
        // bd.FactReceive);
        // #endregion
        for (CarRateModel carfee : ratemodel.CarRateList) {
            if (cartype == StringUilt.toInt(carfee.CarType))// 车辆类型
            {
                // #region 免费时段
                double freeTime = StringUilt.toInt(carfee.FreeTime);
                if (totalParkTime <= freeTime)// 如果收费时间总和小于免费时间 收费金额为0
                {
                    rateclaculatemodel.CalculateMoney = 0;
                    return rateclaculatemodel;
                }
                // #endregion
                // #region 按次收费
                if ("1".equals(ratemodel.TimeSettingList.get(0).RateMethod)) // 收费方式（0
                // 按时
                // 1
                // 按次）
                {
                    // 跨天按多次
                    rateclaculatemodel.CalculateMoney = carfee.CarFeeScaleList.get(0).RateMoney * timelist.size();
                    return rateclaculatemodel;
                }
                // #endregion
                // #region 跨天计费方式 按停车总和
                if ("0".equals(ratemodel.TimeSettingList.get(0).ManyDayMethod))// 0
                // 按停车总和
                // 1
                // 分天计算
                {
                    // #region 免费时间是否包含在收费时间段内 不包含需要减去1次 计算减去金额
                    if (carfee.ContentFreeTimeFlag == false)// 免费时间是否包含在收费时间段内
                    // 不包含需要减去1次 计算减去金额
                    {
                        for (ParkTime pt : timelist) {
                            if (freeTime > 0) {
                                pt.timeTotal -= freeTime;
                                if (pt.timeTotal > 0)// 免费时间减去了
                                {
                                    freeTime = 0;
                                    break;
                                } else {
                                    pt.timeTotal = 0;
                                    freeTime -= pt.timeTotal;
                                }

                            } else
                                break;
                        }
                    }
                    // #endregion
                    // #region 时间段计算 只计算一次
                    // 时间段只计算一次
                    // var CarTimeQuantumListOrder =
                    // carfee.CarTimeQuantumList.OrderBy(a => a.beginTime);

                    // Comparator comp = new SortComparator();
                    // Collections.sort(carfee.CarTimeQuantumList,comp);

                    Comparator comp = new Comparator() {
                        public int compare(Object o1, Object o2) {
                            CarTimeQuantumModel p1 = (CarTimeQuantumModel) o1;
                            CarTimeQuantumModel p2 = (CarTimeQuantumModel) o2;
                            if (p1.beginTime < p2.beginTime)
                                return -1;
                            else if (p1.beginTime == p2.beginTime)
                                return 0;
                            else if (p1.beginTime > p2.beginTime)
                                return 1;
                            return 0;
                        }
                    };
                    Collections.sort(carfee.CarTimeQuantumList, comp);
                    for (CarTimeQuantumModel tq : carfee.CarTimeQuantumList) {
                        double btime = tq.beginTime;// 开始时间段
                        double etime = tq.endTime;// 结束时间段
                        if (tq.beginTime >= tq.endTime)
                            continue;
                        // 查找时间段对应的天数 比如多天数 第一天 第二天 一般用不到
                        /*
                         * 处理情况如下： 停车时间 第一天 0-0.5h 第二天 全天9.5h 第三天 4.5h
                         * timelist时间段为 0-0.5 (0-30min) 0.5-10 (30-600min)
                         * 10-14.5 (600-870min) 时间段设置为 0.1-0.2(6-12min) 5元
                         * 0.4-1(24-60min) 8元 10-15(600-900min) 5元 计算后
                         * timelist[0] timeTotal:0.5-0.1-0.1=0.3
                         * parkMoney:5+8=13 timelist[1] timeTotal:9.5-0.5=9
                         * parkMoney:0 timelist[2] timeTotal:4.5-3=2.5
                         * parkMoney:5
                         */
                        for (ParkTime pt : timelist) {

                            if (btime >= pt.quantumBegin && etime <= pt.quantumEnd)// 1、如若费率时间段在同一天时间段内
                            {
                                pt.timeTotal -= (etime - btime);// 把当天的停车时间减去时间段内的时长
                                pt.parkMoney += StringUilt.toDouble(tq.TimeQuantumMoney);
                                break;
                            } else if (btime >= pt.quantumBegin && btime < pt.quantumEnd && etime > pt.quantumEnd)// 2、若果费率开始时间段在当天时间段内
                            // 将费率时间段金额算入当天
                            {
                                pt.timeTotal -= (pt.quantumEnd - btime);
                                pt.parkMoney += StringUilt.toDouble(tq.TimeQuantumMoney);
                            } else if (btime < pt.quantumBegin && etime >= pt.quantumBegin && etime < pt.quantumEnd)// 3、如果费率结束时间段在当天时间段内
                            // 将费率时间段金额算入当天
                            // 因为2已经算过了
                            {
                                pt.timeTotal -= (etime - pt.quantumBegin);//
                                break;
                            } else if (btime < pt.quantumBegin && etime >= pt.quantumEnd)// 4、如果费率开始时间小于当天停车开始时间
                            // 费率结束时间大于当天停车时间
                            {
                                pt.timeTotal = 0;
                            }
                        }

                    }
                    // #endregion
                    // #region 剩余时间计算
                    if (StringUilt.toDouble(carfee.CarFeeScaleList.get(0).RateTime) > 0) {
                        double overplusTime = 0;// 多计算的时间 比如第一天停车剩余30分钟 每小时2元
                        // 30-60=-30 多计算的30分钟要算在第二天里减去

                        // 剩余时长计算
                        for (ParkTime pt : timelist) {
                            pt.timeTotal += overplusTime;// overplusTime<=0
                            // overplusTime += pt.timeTotal;
                            while (pt.timeTotal > 0) {
                                pt.parkMoney += carfee.CarFeeScaleList.get(0).RateMoney;
                                pt.timeTotal = pt.timeTotal
                                        - StringUilt.toDouble(carfee.CarFeeScaleList.get(0).RateTime);
                            }
                            overplusTime = pt.timeTotal;
                            // 先计算次最大收费 然后在计算日最大收费
                            pt.parkMoney = pt.parkMoney <= carfee.OnceMaxMoney ? pt.parkMoney : carfee.OnceMaxMoney;
                            if (carfee.DayMaxMoney != 0 && pt.alreadyCharge > carfee.DayMaxMoney) {
                                pt.parkMoney = 0;
                            } else if (pt.alreadyCharge + pt.parkMoney >= carfee.DayMaxMoney) {
                                pt.parkMoney = carfee.DayMaxMoney - pt.alreadyCharge;
                            }
                            rateclaculatemodel.CalculateMoney += pt.parkMoney;
                        }
                    }
                    // #endregion

                }
                // #endregion
                // #region 跨天计费方式 分天计算
                else if ("1".equals(ratemodel.TimeSettingList.get(0).ManyDayMethod))// 0
                // 按停车总和
                // 1
                // 分天计算
                {
                    // #region 免费时间不包含在收费时间段内 需要每天都减去
                    if (carfee.ContentFreeTimeFlag == false)// 免费时间是否包含在收费时间段内
                    // 不包含需要每天都减去 计算减去金额
                    {
                        for (ParkTime pt : timelist) {
                            pt.timeTotal -= freeTime;
                            pt.timeTotal = pt.timeTotal > 0 ? pt.timeTotal : 0;
                        }
                    }

                    // #endregion
                    // #region 时间段计算 每天重复

                    for (ParkTime pt : timelist) {
                        if (carfee.ContentFreeTimeFlag == true && pt.timeTotal <= freeTime) {
                            pt.timeTotal = 0;
                            pt.parkMoney = 0;
                            continue;
                        }
                        Comparator comp = new Comparator() {
                            public int compare(Object o1, Object o2) {
                                CarTimeQuantumModel p1 = (CarTimeQuantumModel) o1;
                                CarTimeQuantumModel p2 = (CarTimeQuantumModel) o2;
                                if (p1.beginTime < p2.beginTime)
                                    return -1;
                                else if (p1.beginTime == p2.beginTime)
                                    return 0;
                                else if (p1.beginTime > p2.beginTime)
                                    return 1;
                                return 0;
                            }
                        };
                        Collections.sort(carfee.CarTimeQuantumList, comp);
                        // var CarTimeQuantumListOrder =
                        // carfee.CarTimeQuantumList.OrderBy(a => a.beginTime);

                        for (CarTimeQuantumModel tq : carfee.CarTimeQuantumList) {
                            double btime = tq.beginTime;// 开始时间段
                            double etime = tq.endTime;// 结束时间段
                            if (tq.beginTime >= tq.endTime)
                                continue;
                            // 查找时间段对应的天数 比如多天数 第一天 第二天 一般用不到
                            /*
                             * 处理情况如下： 停车时间 第一天 0-0.5h 第二天 全天9.5h 第三天 4.5h
                             * timelist时间段为 0-0.5 (0-30min) 0.5-10 (30-600min)
                             * 10-14.5 (600-870min) 时间段设置为 0.1-0.2(6-12min) 5元
                             * 0.4-1(24-60min) 8元 10-15(600-900min) 5元 计算后
                             * timelist[0] timeTotal:0.5-0.1-0.1=0.3
                             * parkMoney:5+8=13 timelist[1] timeTotal:9.5-0.5=9
                             * parkMoney:0 timelist[2] timeTotal:4.5-3=2.5
                             * parkMoney:5
                             */

                            if (btime >= pt.quantumBegin && etime <= pt.quantumEnd)// 1、如若费率时间段在同一天时间段内
                            {
                                pt.timeTotal -= (etime - btime);// 把当天的停车时间减去时间段内的时长
                                pt.parkMoney += StringUilt.toDouble(tq.TimeQuantumMoney);

                            } else if (btime >= pt.quantumBegin && btime < pt.quantumEnd && etime > pt.quantumEnd)// 2、若果费率开始时间段在当天时间段内
                            // 将费率时间段金额算入当天
                            {
                                pt.timeTotal -= (pt.quantumEnd - btime);
                                pt.parkMoney += StringUilt.toDouble(tq.TimeQuantumMoney);
                            }
                        }

                    }
                    // #endregion
                    // #region 剩余时间计算
                    if (StringUilt.toDouble(carfee.CarFeeScaleList.get(0).RateTime) > 0) {
                        // 剩余时长计算
                        for (ParkTime pt : timelist) {
                            // overplusTime += pt.timeTotal;
                            while (pt.timeTotal > 0) {
                                pt.parkMoney += carfee.CarFeeScaleList.get(0).RateMoney;
                                pt.timeTotal = pt.timeTotal
                                        - StringUilt.toDouble(carfee.CarFeeScaleList.get(0).RateTime);
                            }
                            // 先计算次最大收费 然后在计算日最大收费
                            pt.parkMoney = pt.parkMoney <= carfee.OnceMaxMoney ? pt.parkMoney : carfee.OnceMaxMoney;
                            if (carfee.DayMaxMoney != 0 && pt.alreadyCharge >= carfee.DayMaxMoney) {
                                pt.parkMoney = 0;
                            } else if (pt.alreadyCharge + pt.parkMoney >= carfee.DayMaxMoney) {
                                pt.parkMoney = carfee.DayMaxMoney - pt.alreadyCharge;
                            }
                            rateclaculatemodel.CalculateMoney += pt.parkMoney;
                        }
                    }
                    // #endregion
                }

            }

        }
        return rateclaculatemodel;
    }

    /**
     *
     * @param inCarTime
     * @param outCarTime
     * @param cartype
     * @param PlateNumber
     * @param ratemodel
     * @param parkType
     *            1封闭停车场, 2道路停车场
     * @return
     */
    public static RateCalculateModel RateCalculate12(Date inCarTime, Date outCarTime, int cartype, String PlateNumber,
                                                     RateModel ratemodel, int parkType) {
        RateCalculateModel rateCalculateModel = new RateCalculateModel();
        if (parkType == 2) {// 封闭停车场
            return RateCalculate11(inCarTime, outCarTime, cartype, PlateNumber, ratemodel);
        } else {// 道路停车场
            if (inCarTime.getTime() > outCarTime.getTime()) {
                rateCalculateModel.exceptionMsg = "入场时间大于出场时间";
                return rateCalculateModel;
            }
            if (ratemodel.IsActive == false) {
                rateCalculateModel.exceptionMsg = "费率未启用";
                return rateCalculateModel;
            }
            double totalParkTime = 0;
            int days = AbDateUtil.getOffectDay(outCarTime.getTime(), inCarTime.getTime());
            ParkTime parktime = new ParkTime();
            for (int i = 0; i <= days; i++) {

                Calendar c1 = Calendar.getInstance();
                c1.setTime(inCarTime);
                c1.add(Calendar.DATE, i);
                String beginTimeStr = AbDateUtil.getStringByFormat(c1.getTime(), AbDateUtil.dateFormatYMD2) + " "
                        + ratemodel.TimeSettingList.get(0).beginTime + ":00";
                String endTimeStr = AbDateUtil.getStringByFormat(c1.getTime(), AbDateUtil.dateFormatYMD2) + " "
                        + ratemodel.TimeSettingList.get(0).endTime + ":00";
                parktime.beginTime = AbDateUtil.getDateByFormat(beginTimeStr, AbDateUtil.dateFormatYMDHM2);
                parktime.endTime = AbDateUtil.getDateByFormat(endTimeStr, AbDateUtil.dateFormatYMDHM2);
                if (i == 0) {
                    if (inCarTime.getTime() > parktime.beginTime.getTime()) {
                        parktime.beginTime = inCarTime;
                    }
                }
                if (i == days) {
                    if (parktime.endTime.getTime() > outCarTime.getTime()) {
                        parktime.endTime = outCarTime;
                    }
                }
                parktime.timeTotal = AbDateUtil.getOffectMinutes(parktime.endTime.getTime(),
                        parktime.beginTime.getTime());

                totalParkTime += parktime.timeTotal;
                rateCalculateModel.ParkTime += parktime.timeTotal;
            }

            for (CarRateModel carfee : ratemodel.CarRateList) {
                if (cartype == StringUilt.toInt(carfee.CarType)) {
                    Comparator comp = new Comparator() {
                        public int compare(Object o1, Object o2) {
                            CarTimeQuantumModel p1 = (CarTimeQuantumModel) o1;
                            CarTimeQuantumModel p2 = (CarTimeQuantumModel) o2;
                            if (p1.beginTime < p2.beginTime)
                                return -1;
                            else if (p1.beginTime == p2.beginTime)
                                return 0;
                            else if (p1.beginTime > p2.beginTime)
                                return 1;
                            return 0;
                        }
                    };
                    Collections.sort(carfee.CarTimeQuantumList, comp);
                    for (int i = 0; i < carfee.CarTimeQuantumList.size(); i++) {// 获取时间段的长度
                        if (carfee.CarTimeQuantumList.get(i).RateMethod == "0") {// 判断时间段收费方式是分钟还是小时
                            // 0分钟，1小时
                            if (days == 0) {
                                if (totalParkTime >= carfee.CarTimeQuantumList.get(i).beginTime
                                        && totalParkTime <= carfee.CarTimeQuantumList.get(i).endTime) {// 获取时间段的结束时间
                                    for (int m = 0; m <= i; m++) {
                                        parktime.parkMoney += StringUilt
                                                .toDouble(carfee.CarTimeQuantumList.get(m).TimeQuantumMoney);
                                    }
                                }
                            } else {

                                double reParkTime = totalParkTime - days * 24 * 60;
                                if (reParkTime == 0) {
                                    if (days > 0) {
                                        parktime.parkMoney += days * carfee.DayMaxMoney;
                                    }
                                }

                                else if (reParkTime < 0) {
                                    days = days - 1;
                                    reParkTime = totalParkTime - days * 24 * 60;
                                }

                                if (reParkTime >= carfee.CarTimeQuantumList.get(i).beginTime
                                        && reParkTime <= carfee.CarTimeQuantumList.get(i).endTime) {

                                    for (int m = 0; m <= i; m++) {
                                        parktime.parkMoney += StringUilt
                                                .toDouble(carfee.CarTimeQuantumList.get(m).TimeQuantumMoney);

                                    }
                                    if (days > 0) {
                                        parktime.parkMoney += days * carfee.DayMaxMoney;
                                    }

                                }
                            }

                        } else {
                            if (days == 0) {

                                if (totalParkTime / 60 >= carfee.CarTimeQuantumList.get(i).beginTime / 60
                                        && totalParkTime / 60 <= carfee.CarTimeQuantumList.get(i).endTime / 60) {
                                    for (int m = 0; m <= i; m++) {
                                        parktime.parkMoney += StringUilt
                                                .toDouble(carfee.CarTimeQuantumList.get(m).TimeQuantumMoney);
                                    }
                                }
                            } else {

                                double reParkTime = totalParkTime / 60 - days * 24;
                                if (reParkTime == 0) {
                                    if (days > 0) {
                                        parktime.parkMoney += days * carfee.DayMaxMoney;
                                    }

                                } else if (reParkTime < 0) {
                                    days = days - 1;
                                    reParkTime = totalParkTime / 60 - days * 24;
                                }
                                if (reParkTime >= carfee.CarTimeQuantumList.get(i).beginTime / 60
                                        && reParkTime <= carfee.CarTimeQuantumList.get(i).endTime / 60) {
                                    for (int m = 0; m <= i; m++) {
                                        parktime.parkMoney += StringUilt
                                                .toDouble(carfee.CarTimeQuantumList.get(m).TimeQuantumMoney);
                                    }
                                    if (days > 0) {
                                        parktime.parkMoney += days * carfee.DayMaxMoney;
                                    }
                                }

                            }
                        }

                    }

                    rateCalculateModel.CalculateMoney += parktime.parkMoney;
                }
            }
            return rateCalculateModel;
        }
    }
}
