package com.github.gabrielhumbertdev.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

//=======================
//Story 1: Fee Calculation
//=======================
//Purpose: Verify that account fees are calculated correctly
//based on the maintained account balance.
public class FeeCalculatorServiceImplTest {

    @Test
    public void calculateFee_returns20_whenBalanceIs100OrLess() {
        FeeCalculatorService service = new FeeCalculatorServiceImpl();
        assertEquals(20, service.calculateFee(100));
    }

    @Test
    public void calculateFee_returns15_whenBalanceBetween101And500() {
        FeeCalculatorService service = new FeeCalculatorServiceImpl();
        assertEquals(15, service.calculateFee(500));
    }

    @Test
    public void calculateFee_returns10_whenBalanceBetween501And1000() {
        FeeCalculatorService service = new FeeCalculatorServiceImpl();
        assertEquals(10, service.calculateFee(1000));
    }

    @Test
    public void calculateFee_returns5_whenBalanceBetween1001And2000() {
        FeeCalculatorService service = new FeeCalculatorServiceImpl();
        assertEquals(5, service.calculateFee(2000));
    }

    @Test
    public void calculateFee_returns0_whenBalanceAbove2000() {
        FeeCalculatorService service = new FeeCalculatorServiceImpl();
        assertEquals(0, service.calculateFee(2500));
    }
}
