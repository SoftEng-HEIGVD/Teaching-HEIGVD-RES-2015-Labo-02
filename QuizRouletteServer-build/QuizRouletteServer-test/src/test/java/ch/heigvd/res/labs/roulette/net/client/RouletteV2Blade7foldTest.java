/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.heigvd.res.labs.roulette.net.client;

import ch.heigvd.res.labs.roulette.data.Student;
import ch.heigvd.res.labs.roulette.net.client.RouletteV2ClientImpl;
import ch.heigvd.schoolpulse.TestAuthor;
import java.io.IOException;
import java.util.List;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Nathan
 */
public class RouletteV2Blade7foldTest {
    
    public RouletteV2Blade7foldTest() {
    }
    
    @BeforeClass
    public static void setUpClass() {
    }
    
    @AfterClass
    public static void tearDownClass() {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }

    /**
     * Test of clearDataStore method, of class RouletteV2ClientImpl.
     * @throws java.io.IOException
     */
    @Test
    @TestAuthor(githubId = "Blade7fold")
    public void testClearDataStore() throws IOException {
        System.out.println("Clear Data Store");
        RouletteV2ClientImpl instance = new RouletteV2ClientImpl();
        instance.clearDataStore();
        List<Student> result = instance.listStudents();
        assertTrue(result.isEmpty());
    }

    /**
     * Test of listStudents method, of class RouletteV2ClientImpl.
     * @throws java.io.IOException
     */
    @Test
    @TestAuthor(githubId = "Blade7fold")
    public void testListStudents() throws IOException {
        System.out.println("List Students");
        RouletteV2ClientImpl instance = new RouletteV2ClientImpl();
        List<Student> expResult = null;
        List<Student> result = instance.listStudents();
        assertEquals(expResult, result);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }
    
}
