package com.composite.utils;

import org.mybatis.generator.api.MyBatisGenerator;
import org.mybatis.generator.api.ProgressCallback;
import org.mybatis.generator.api.VerboseProgressCallback;
import org.mybatis.generator.config.Configuration;
import org.mybatis.generator.config.xml.ConfigurationParser;
import org.mybatis.generator.internal.DefaultShellCallback;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Component
public class GeneratorUtils {

    @PostConstruct
    public void init() {
        generatorFile();
    }

    private void generatorFile() {
        List<String> warnings = new ArrayList<>();
        boolean overwrite = true;
        String configFilePath = System.getProperty("user.dir")
                .concat("/springboot-mybatis-generator/src/main/resources")
                .concat("/mybatis-generator/generatorConfig.xml");
        try {
            File configFile = new File(configFilePath);
            ConfigurationParser cp = new ConfigurationParser(warnings);
            Configuration config = cp.parseConfiguration(configFile);
            DefaultShellCallback callback = new DefaultShellCallback(overwrite);
            MyBatisGenerator myBatisGenerator = new MyBatisGenerator(config, callback, warnings);
            ProgressCallback progressCallback = new VerboseProgressCallback();
            myBatisGenerator.generate(progressCallback);
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (String warning : warnings) {
            System.out.println(warning);
        }
    }
}
