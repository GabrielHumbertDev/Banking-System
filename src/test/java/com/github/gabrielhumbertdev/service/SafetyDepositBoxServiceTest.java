package com.github.gabrielhumbertdev.service;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.github.gabrielhumbertdev.model.depositbox.SafetyDepositBox;

public class SafetyDepositBoxServiceTest {

    private SafetyDepositBoxService service;

    @BeforeEach
    public void setUp() throws Exception {
        service = SafetyDepositBoxService.getInstance();

        service.getSafetyDepositBoxes().clear();
        service.setNumberOfSafetyDepositBoxes(2);

        Field waitingField = SafetyDepositBoxService.class.getDeclaredField("waitingFlag");
        waitingField.setAccessible(true);
        waitingField.setBoolean(service, false);
    }

    @Test
    public void twoThreadsRequestBox_holdFiveSeconds_release_noThreadWaited() throws Exception {
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch doneGate = new CountDownLatch(2);

        Runnable task = () -> {
            try {
                startGate.await();
                SafetyDepositBox box = service.allocateSafetyDepositBox();
                Thread.sleep(5000);
                service.releaseSafetyDepositBox(box);
            } catch (Exception ignored) {
            } finally {
                doneGate.countDown();
            }
        };

        new Thread(task).start();
        new Thread(task).start();

        startGate.countDown();
        doneGate.await();

        assertFalse(getWaitingFlag(service));
    }

    @Test
    public void threeThreadsRequestBox_holdFiveSeconds_release_aThreadWaited() throws Exception {
        CountDownLatch startGate = new CountDownLatch(1);
        CountDownLatch doneGate = new CountDownLatch(3);

        Runnable task = () -> {
            try {
                startGate.await();
                SafetyDepositBox box = service.allocateSafetyDepositBox();
                Thread.sleep(5000);
                service.releaseSafetyDepositBox(box);
            } catch (Exception ignored) {
            } finally {
                doneGate.countDown();
            }
        };

        new Thread(task).start();
        new Thread(task).start();
        new Thread(task).start();

        startGate.countDown();
        doneGate.await();

        assertTrue(getWaitingFlag(service));
    }

    private boolean getWaitingFlag(SafetyDepositBoxService service) throws Exception {
        Field waitingField = SafetyDepositBoxService.class.getDeclaredField("waitingFlag");
        waitingField.setAccessible(true);
        return waitingField.getBoolean(service);
    }
}
