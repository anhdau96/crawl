/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.sunfrogcrawl;

import java.util.ArrayList;
import java.util.List;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 *
 * @author SaiBack
 */
public class MyDriver {
    private PhantomJSDriver driver;
    
    public PhantomJSDriver getDriver(){
        System.out.println("Get Data By Cate");
        System.setProperty("phantomjs.binary.path", "phantomjs.exe");
        List<String> cliArgsCap = new ArrayList<>();
        DesiredCapabilities capabilities = DesiredCapabilities.phantomjs();
        cliArgsCap.add("--web-security=false");
        cliArgsCap.add("--ssl-protocol=any");
        cliArgsCap.add("--ignore-ssl-errors=true");
        cliArgsCap.add("--webdriver-loglevel=INFO");
        cliArgsCap.add("--load-images=false");

        capabilities.setCapability(CapabilityType.SUPPORTS_FINDING_BY_CSS, true);
        capabilities.setCapability(CapabilityType.TAKES_SCREENSHOT, true);
        capabilities.setCapability(PhantomJSDriverService.PHANTOMJS_CLI_ARGS, cliArgsCap);
        driver = new PhantomJSDriver(capabilities);
        return driver;
    }
    
    public void quitDriver(){
        driver.quit();
    }
}
