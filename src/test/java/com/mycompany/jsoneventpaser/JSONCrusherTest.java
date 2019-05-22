/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.jsoneventpaser;

import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.apache.commons.io.IOUtils;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 *
 * @author Mark
 */
public class JSONCrusherTest {

    public JSONCrusherTest() {
    }

    @BeforeAll
    public static void setUpClass() {
    }

    @AfterAll
    public static void tearDownClass() {
    }

    @BeforeEach
    public void setUp() {
    }

    @AfterEach
    public void tearDown() {
    }

    /**
     * Test of crush method, of class JSONCrusher.
     */
    @Test
    public void testCrush() throws IOException {

        String json = getResource("/srss.json");
        String expResult = getResource("/crushed.json");

        String result = JSONCrusher.crush(json);

        System.out.println(result);
        assertEquals(expResult, result);

    }

    private String getResource(String resourceName) throws IOException {
        return IOUtils.toString(
                this.getClass().getResourceAsStream(resourceName),
                "UTF-8"
        );
    }

}
