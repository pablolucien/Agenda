package org.pclg.tools;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.assertEquals;

public class VariableSubstitutionHelperTest {
    private static final Map<String, String> variables = new HashMap<>(2);

    @BeforeClass
    public void setUp() throws Exception {
        variables.put("user", "pepito");
        variables.put("password", "secret");
    }

    @DataProvider(name = "testData")
    public Object[][] testData() {
        return new Object[][] {
            {"The user ${user} has password ${password}", "The user pepito has password secret"},
            {"The user ${usr} has password ${pwd}", "The user ${usr} has password ${pwd}"},
            {"The user ${user} has password ${pwd}", "The user pepito has password ${pwd}"},
            {"The user ${usr} has password ${password}", "The user ${usr} has password secret"},
            {"The user ${usr} has password ${password}. I mean the user ${user}" , "The user ${usr} has password secret. I mean the user pepito"},
            {"" , ""},
        };
    }

    @Test(dataProvider = "testData")
    public void replace(final String testExpression, final String expectedExpression) {
        assertEquals(VariableSubstitutionHelper.replace(testExpression, variables), expectedExpression);
    }
}