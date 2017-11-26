package com.gzy.util;

import com.gzy.FakeApplication;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Access Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Nov 22, 2017</pre>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = FakeApplication.class)
public class AccessTest {

    @Autowired
    Access access;

    @Before
    public void before() throws Exception {
    }

    @After
    public void after() throws Exception {
    }

    /**
     * Method: access()
     */
    @Test
    public void testAccess() throws Exception {
        access.access();
    }

    /**
     * Method: doAccess(String website)
     */
    @Test
    public void testDoAccess() throws Exception {

    }


}
