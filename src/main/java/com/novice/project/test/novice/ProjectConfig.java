package com.novice.project.test.novice;

import com.novice.framework.toolkit.i18n.I18n;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootConfiguration
@ComponentScan("com.novice.project.test")
@I18n("i18n/messages")
public class ProjectConfig {

}
