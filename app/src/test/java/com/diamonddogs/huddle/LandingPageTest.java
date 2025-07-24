package com.diamonddogs.huddle;

import junit.framework.TestCase;

import org.junit.Assert;

public class LandingPageTest extends TestCase {

    public void testDistFrom() {
        assertEquals(222.38985328911747, LandingPage.distFrom(52, 160, 50, 160));
    }
}