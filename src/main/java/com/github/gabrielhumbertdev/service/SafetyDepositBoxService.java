package com.github.gabrielhumbertdev.service;

import java.util.ArrayList;
import java.util.List;

import com.github.gabrielhumbertdev.model.customer.Customer;
import com.github.gabrielhumbertdev.model.depositbox.SafetyDepositBox;
import com.github.gabrielhumbertdev.model.depositbox.SmallSafetyDepositBox;

public class SafetyDepositBoxService {

    private static SafetyDepositBoxService safetyDepositBoxService;

    // ============================
    // Question 4: Deposit Box Alerts (Allotted + Revoked)
    // ============================
    // Requirement: generate email alerts if the deposit box is allotted
    // and revoked from the customer.
    private final SafetyDepositBoxAlertService alertService = new SafetyDepositBoxAlertService();

    private List<SafetyDepositBox> safetyDepositBoxes;
    private int numberOfSafetyDepositBoxes;

    private boolean waitingFlag;

    private SafetyDepositBoxService() {
        this.safetyDepositBoxes = new ArrayList<>();
        this.numberOfSafetyDepositBoxes = 2; // max set to TWO
        this.waitingFlag = false;
    }

    public static synchronized SafetyDepositBoxService getInstance() {
        if (safetyDepositBoxService == null) {
            safetyDepositBoxService = new SafetyDepositBoxService();
        }
        return safetyDepositBoxService;
    }

    public synchronized void setNumberOfSafetyDepositBoxes(int numberOfSafetyDepositBoxes) {
        this.numberOfSafetyDepositBoxes = numberOfSafetyDepositBoxes;
    }

    public synchronized int getNumberOfSafetyDepositBoxes() {
        return numberOfSafetyDepositBoxes;
    }

    /**
     * Existing method kept for backward compatibility. Allocates a box but does NOT
     * send alerts (no customer provided).
     */
    public synchronized SafetyDepositBox allocateSafetyDepositBox() {
        SafetyDepositBox released = getReleasedSafetyDepositBox();
        if (released != null) {
            released.setAllotted(true);
            return released;
        }

        if (safetyDepositBoxes.size() < numberOfSafetyDepositBoxes) {
            return createNewBox();
        }

        // FIX: If no boxes can ever exist, return null immediately (prevents deadlock)
        if (numberOfSafetyDepositBoxes <= 0) {
            return null;
        }

        waitingFlag = true;

        while (true) {
            try {
                wait();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return null;
            }

            SafetyDepositBox box = getReleasedSafetyDepositBox();
            if (box != null) {
                box.setAllotted(true);
                return box;
            }
        }
    }

    // ============================
    // Question 4: Allocation (Allotted) + Email Alert
    // ============================
    /**
     * Allocates a box and sends an allocation alert to the customer.
     */
    public synchronized SafetyDepositBox allocateSafetyDepositBox(Customer customer) {
        if (customer == null) {
            return null;
        }

        SafetyDepositBox box = allocateSafetyDepositBox(); // reuse existing logic
        if (box != null) {
            alertService.sendAllocationAlert(customer, box);
        }
        return box;
    }

    /**
     * Existing method kept for backward compatibility. Releases a box but does NOT
     * send alerts (no customer provided).
     */
    public synchronized void releaseSafetyDepositBox(SafetyDepositBox box) {
        if (box == null) {
            return;
        }
        box.setAllotted(false);
        notifyAll();
    }

    // ============================
    // Question 4: Release (Revoked) + Email Alert
    // ============================
    /**
     * Releases a box and sends a release/revoked alert to the customer. (Message
     * wording is handled inside SafetyDepositBoxAlertService)
     */
    public synchronized void releaseSafetyDepositBox(Customer customer, SafetyDepositBox box) {
        if (customer == null || box == null) {
            return;
        }

        releaseSafetyDepositBox(box); // reuse existing logic
        alertService.sendReleaseAlert(customer, box);
    }

    public synchronized int getNumberOfAvailableSafetyDepositBoxes() {
        int count = 0;
        for (SafetyDepositBox box : safetyDepositBoxes) {
            if (!box.isAllotted()) {
                count++;
            }
        }
        return count;
    }

    public synchronized SafetyDepositBox getReleasedSafetyDepositBox() {
        for (SafetyDepositBox box : safetyDepositBoxes) {
            if (!box.isAllotted()) {
                return box;
            }
        }
        return null;
    }

    public synchronized List<SafetyDepositBox> getSafetyDepositBoxes() {
        return safetyDepositBoxes;
    }

    private SafetyDepositBox createNewBox() {
        SafetyDepositBox box = new SmallSafetyDepositBox();
        box.setAllotted(true);
        box.setId(safetyDepositBoxes.size() + 1);
        safetyDepositBoxes.add(box);
        return box;
    }
}