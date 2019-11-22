package com.novice.project.test.novice.web;

import com.novice.framework.core.enums.EventType;
import com.novice.framework.datamodel.definition.Entity;
import com.novice.framework.datamodel.manager.EntityManager;
import com.novice.framework.toolkit.file.service.StorageService;
import com.novice.project.test.novice.TableType;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/test")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class TestController {
	private final EntityManager entityManager;
	private final StorageService storageService;

	@GetMapping("/create")
	public String create() {
		var entity = new Entity(TableType.LMS_STAFF)
				.setProperty("name", "zhangsan").setProperty("date", new Date())
				.setProperty("flag", true).setProperty("enums", EventType.Update);
		return entityManager.create(entity);
	}

	@GetMapping("/list")
	public List<Entity> list(@Param("tableId") String tableId) {
		return entityManager.listAll(tableId);
	}

	@GetMapping("/view")
	public Entity view(@Param("tableId") String tableId, @Param("id") String id) {
		var e = entityManager.get(id, tableId);
		System.out.println(e.getProperties().get("name"));
		return e;
	}

	@GetMapping("/file")
	public com.novice.framework.toolkit.file.File file() {
		File file = new File("/data/test.png");
		com.novice.framework.toolkit.file.File file1 = this.storageService.importFile(file);
		return file1;
	}
}
