package com.github.gabrielhumbertdev.model.account;


public class CheckingAccount extends Account {

    private int nextCheckNumber = 1;

    // ============================
    // Question 3: Minimum Balance Criteria + Fine
    // ============================
    // Requirement: "You would need to set up the minimum balance limit for the
    // checking account."
    // This minimum balance rule is used by the Bank to detect violations and apply
    // a fine.
    private static final double MINIMUM_BALANCE = 50.0;
    private static final double MIN_BALANCE_FINE = 25.0;

    /**
     * Question 3: Minimum balance limit for checking accounts.
     */
    public double getMinimumBalance() {
        return MINIMUM_BALANCE;
    }

    /**
     * Question 3: Fine amount charged when balance violates minimum criteria.
     */
    public double getMinimumBalanceFine() {
        return MIN_BALANCE_FINE;
    }

    /**
     * Question 3: Apply the fine if the account balance is below the minimum.
     * Returns the fine charged (0.0 if no fine was applied).
     *
     * Note: We use correctBalance(...) from Account to set the new balance safely.
     */
    public double applyMinimumBalanceFineIfNeeded() {
        if (getBalance() < MINIMUM_BALANCE) {
            correctBalance(getBalance() - MIN_BALANCE_FINE);
            return MIN_BALANCE_FINE;
        }
        return 0.0;
    }

    // ============================
    // Existing functionality: Check numbers
    // ============================

    // Returns the next check number WITHOUT incrementing
    public int getNextCheckNumber() {
        return nextCheckNumber;
    }

    // Issues a check number and increments by 1
    public int issueNextCheckNumber() {
        int current = nextCheckNumber;
        nextCheckNumber++;
        return current;
    }

    // Spec asked for getter/setter
    public void setNextCheckNumber(int nextCheckNumber) {
        this.nextCheckNumber = nextCheckNumber;
    }
}
