package com.novice.project.test.definition;


import com.novice.framework.datamodel.annotation.Field;
import com.novice.framework.datamodel.annotation.Type;
import com.novice.framework.datamodel.datatype.Image;
import com.novice.framework.datamodel.meta.Meta;

import java.util.Date;

@Type(id = Test.ID, displayAs = "测试类型", module = DEV.class)
public abstract class Test implements Meta {

	public static final String ID = "7e806f42b91142dc880098ebf6fff150";

	@Field(display = true, displayAs = "姓名")
	private String name;

	@Field(displayAs = "地址")
	private String address;

	@Field
	private String email;

	@Field
	private int age = 23;

	@Field(displayAs = "头像", dataType = Image.ID)
	private String avatar;

	@Field(displayAs = "日期")
	private Date date;

}
