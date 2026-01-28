package com.github.gabrielhumbertdev.service;


public class FeeCalculatorServiceImpl implements FeeCalculatorService {

    @Override
    public double calculateFee(double balance) {
        if (balance <= 100) {
            return 20;
        } else if (balance <= 500) {
            return 15;
        } else if (balance <= 1000) {
            return 10;
        } else if (balance <= 2000) {
            return 5;
        } else {
            return 0;
        }
    }
}
