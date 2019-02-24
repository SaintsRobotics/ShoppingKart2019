/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package com.saintsrobotics.shoppingkart.config;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

import edu.wpi.first.wpilibj.Filesystem;

/**
 * Add your docs here.
 */
public class Config {

    private Properties config;

    private Config(Properties config) {
        this.config = config;
    }

    public int getInt(String key) {
        return Integer.parseInt(this.config.getProperty(key));
    }

    public double getDouble(String key) {
        return Double.parseDouble(this.config.getProperty(key));
    }

    public boolean getBoolean(String key) {
        return Boolean.parseBoolean(this.config.getProperty(key));
    }

    public static Config fromFile(boolean isTest) throws IOException {
        String filename = isTest ? "test.properties" : "competition.properties";
        String deploymentDirectory = Filesystem.getDeployDirectory().getAbsolutePath();
        java.io.File configFile = Paths.get(deploymentDirectory, filename).toFile();
        Properties p = new Properties();
        p.load(new FileReader(configFile));
        return new Config(p);
    }
}
