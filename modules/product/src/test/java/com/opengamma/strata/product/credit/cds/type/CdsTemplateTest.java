/**
 * Copyright (C) 2016 - present by OpenGamma Inc. and the OpenGamma group of companies
 *
 * Please see distribution for license.
 */
package com.opengamma.strata.product.credit.cds.type;

import static com.opengamma.strata.basics.currency.Currency.EUR;
import static com.opengamma.strata.basics.currency.Currency.USD;
import static com.opengamma.strata.basics.date.Tenor.TENOR_10Y;
import static com.opengamma.strata.basics.date.Tenor.TENOR_2Y;
import static com.opengamma.strata.collect.TestHelper.assertSerialization;
import static com.opengamma.strata.collect.TestHelper.coverBeanEquals;
import static com.opengamma.strata.collect.TestHelper.coverImmutableBean;
import static com.opengamma.strata.collect.TestHelper.date;
import static com.opengamma.strata.product.common.BuySell.BUY;
import static org.testng.Assert.assertEquals;

import java.time.LocalDate;
import java.util.Optional;

import org.testng.annotations.Test;

import com.opengamma.strata.basics.ReferenceData;
import com.opengamma.strata.basics.StandardId;
import com.opengamma.strata.basics.currency.AdjustablePayment;
import com.opengamma.strata.basics.schedule.PeriodicSchedule;
import com.opengamma.strata.basics.schedule.RollConventions;
import com.opengamma.strata.product.credit.cds.Cds;
import com.opengamma.strata.product.credit.cds.CdsTrade;

/**
 * Test {@link CdsTemplate}.
 */
@Test
public class CdsTemplateTest {

  private static final ReferenceData REF_DATA = ReferenceData.standard();
  private static final double NOTIONAL_2M = 2_000_000d;
  private static final CdsConvention CONV1 = CdsConventions.EUR_GB_STANDARD;
  private static final CdsConvention CONV2 = CdsConventions.USD_STANDARD;
  private static final StandardId LEGAL_ENTITY = StandardId.of("OG", "BCD");

  //-------------------------------------------------------------------------
  public void test_of() {
    CdsTemplate test = CdsTemplate.of(TENOR_10Y, CONV1);
    assertEquals(test.getAccrualStart(), AccrualStart.IMM_DATE);
    assertEquals(test.getTenor(), TENOR_10Y);
    assertEquals(test.getConvention(), CONV1);
  }

  public void test_of_accStart() {
    CdsTemplate test = CdsTemplate.of(AccrualStart.NEXT_DAY, TENOR_10Y, CONV2);
    assertEquals(test.getAccrualStart(), AccrualStart.NEXT_DAY);
    assertEquals(test.getTenor(), TENOR_10Y);
    assertEquals(test.getConvention(), CONV2);
  }

  //-------------------------------------------------------------------------
  public void test_createTrade() {
    CdsTemplate base1 = CdsTemplate.of(TENOR_10Y, CONV1);
    CdsTemplate base2 = CdsTemplate.of(AccrualStart.NEXT_DAY, TENOR_2Y, CONV2);
    LocalDate tradeDate = LocalDate.of(2015, 5, 5);
    LocalDate startDate1 = date(2015, 3, 20);
    LocalDate endDate1 = date(2025, 6, 20);
    LocalDate startDate2 = date(2015, 5, 6);
    LocalDate endDate2 = date(2017, 6, 20);
    CdsTrade test1 = base1.createTrade(LEGAL_ENTITY, tradeDate, BUY, NOTIONAL_2M, 0.05d, REF_DATA);
    CdsTrade test2 = base2.createTrade(LEGAL_ENTITY, tradeDate, BUY, NOTIONAL_2M, 0.05d, REF_DATA);
    Cds expected1 = Cds.of(BUY, LEGAL_ENTITY, CONV1.getCurrency(), NOTIONAL_2M, startDate1, endDate1,
        CONV1.getSettlementDateOffset().getCalendar(), 0.05d);
    PeriodicSchedule sch1 = expected1.getAccrualSchedule();
    expected1 = expected1.toBuilder()
        .accrualSchedule(sch1.toBuilder()
            .startDateBusinessDayAdjustment(sch1.getBusinessDayAdjustment())
            .rollConvention(RollConventions.DAY_20)
            .build())
        .build();
    Cds expected2 = Cds.of(BUY, LEGAL_ENTITY, CONV2.getCurrency(), NOTIONAL_2M, startDate2, endDate2,
        CONV2.getSettlementDateOffset().getCalendar(), 0.05d);
    PeriodicSchedule sch2 = expected2.getAccrualSchedule();
    expected2 = expected2.toBuilder()
        .accrualSchedule(sch2.toBuilder()
            .startDateBusinessDayAdjustment(sch2.getBusinessDayAdjustment())
            .rollConvention(RollConventions.DAY_20)
            .build())
        .build();
    assertEquals(test1.getInfo().getTradeDate(), Optional.of(tradeDate));
    assertEquals(test1.getProduct(), expected1);
    assertEquals(test1.getUpFrontFee(), Optional.empty());
    assertEquals(test2.getInfo().getTradeDate(), Optional.of(tradeDate));
    assertEquals(test2.getUpFrontFee(), Optional.empty());
    assertEquals(test2.getProduct(), expected2);
  }

  public void test_createTrade_withFee() {
    CdsTemplate base1 = CdsTemplate.of(TENOR_10Y, CONV1);
    CdsTemplate base2 = CdsTemplate.of(AccrualStart.NEXT_DAY, TENOR_2Y, CONV2);
    LocalDate tradeDate = LocalDate.of(2015, 5, 5);
    AdjustablePayment payment1 =
        AdjustablePayment.of(EUR, NOTIONAL_2M, CONV1.getSettlementDateOffset().adjust(tradeDate, REF_DATA));
    AdjustablePayment payment2 =
        AdjustablePayment.of(USD, NOTIONAL_2M, CONV2.getSettlementDateOffset().adjust(tradeDate, REF_DATA));
    LocalDate startDate1 = date(2015, 3, 20);
    LocalDate endDate1 = date(2025, 6, 20);
    LocalDate startDate2 = date(2015, 5, 6);
    LocalDate endDate2 = date(2017, 6, 20);
    CdsTrade test1 = base1.createTrade(LEGAL_ENTITY, tradeDate, BUY, NOTIONAL_2M, 0.05d, payment1, REF_DATA);
    CdsTrade test2 = base2.createTrade(LEGAL_ENTITY, tradeDate, BUY, NOTIONAL_2M, 0.05d, payment2, REF_DATA);
    Cds expected1 = Cds.of(BUY, LEGAL_ENTITY, CONV1.getCurrency(), NOTIONAL_2M, startDate1, endDate1,
        CONV1.getSettlementDateOffset().getCalendar(), 0.05d);
    PeriodicSchedule sch1 = expected1.getAccrualSchedule();
    expected1 = expected1.toBuilder()
        .accrualSchedule(sch1.toBuilder()
            .startDateBusinessDayAdjustment(sch1.getBusinessDayAdjustment())
            .rollConvention(RollConventions.DAY_20)
            .build())
        .build();
    Cds expected2 = Cds.of(BUY, LEGAL_ENTITY, CONV2.getCurrency(), NOTIONAL_2M, startDate2, endDate2,
        CONV2.getSettlementDateOffset().getCalendar(), 0.05d);
    PeriodicSchedule sch2 = expected2.getAccrualSchedule();
    expected2 = expected2.toBuilder()
        .accrualSchedule(sch2.toBuilder()
            .startDateBusinessDayAdjustment(sch2.getBusinessDayAdjustment())
            .rollConvention(RollConventions.DAY_20)
            .build())
        .build();
    assertEquals(test1.getInfo().getTradeDate(), Optional.of(tradeDate));
    assertEquals(test1.getUpFrontFee(), Optional.of(payment1));
    assertEquals(test1.getProduct(), expected1);
    assertEquals(test2.getInfo().getTradeDate(), Optional.of(tradeDate));
    assertEquals(test2.getUpFrontFee(), Optional.of(payment2));
    assertEquals(test2.getProduct(), expected2);
  }

  //-------------------------------------------------------------------------
  public void coverage() {
    CdsTemplate test1 = CdsTemplate.of(TENOR_10Y, CONV1);
    coverImmutableBean(test1);
    CdsTemplate test2 = CdsTemplate.of(AccrualStart.NEXT_DAY, TENOR_10Y, CONV2);
    coverBeanEquals(test1, test2);
  }

  public void test_serialization() {
    CdsTemplate test = CdsTemplate.of(TENOR_10Y, CONV1);
    assertSerialization(test);
  }

}