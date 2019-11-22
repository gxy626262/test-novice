package com.novice.project.test.novice;

import com.novice.framework.core.annotation.Module;
import com.novice.framework.datamodel.annotation.MetaScan;

import static com.novice.project.test.novice.ProjectConfig.NAME;

@MetaScan
@Module(name = NAME, i18n = "i18n/test/messages")
public class ProjectConfig {
	public static final String NAME = "test";
}
