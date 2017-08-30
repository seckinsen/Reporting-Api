package net.seckinsen.util;

import org.junit.Before;
import org.mockito.MockitoAnnotations;

/**
 * Created by seck on 30.08.2017.
 */
public class BaseTestCase {

    @Before
    public void initMock() {
        MockitoAnnotations.initMocks(this);
    }

}
