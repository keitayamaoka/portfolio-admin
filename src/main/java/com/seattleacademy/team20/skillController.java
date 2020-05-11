package com.seattleacademy.team20;

import java.io.FileInputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

@Controller
public class skillController {

	private static final Logger logger = LoggerFactory.getLogger(skillController.class);

	@RequestMapping(value = "/skillUpload" , method = RequestMethod.GET)
	public String skillUpload(Locale locale, Model model) {
		logger.info("welcome SkillUpload! The client locale is {}", locale);

		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);

		String formattedDate = dateFormat.format(date);

		model.addAttribute("serverTime", formattedDate );


		try {
				initialize();
		} catch (IOException e) {
				e.printStackTrace();
		};
		List<SkillCategory> categories = selectSkillCategories();

		uploadSkill(categories);

		return "skillUpload";
	}


@Autowired

private JdbcTemplate jdbcTemplate;

public List<SkillCategory> selectSkillCategories() {
	final String sql = "select * from skills";
	return jdbcTemplate.query(sql, new RowMapper<SkillCategory>() {
			public SkillCategory mapRow(ResultSet rs, int rowNum) throws SQLException{
					return new SkillCategory(rs.getString("category"), rs.getString("name"), rs.getInt("score"));
			}
	});
}

private FirebaseApp app;

public void initialize() throws IOException {
	FileInputStream refreshToken = new FileInputStream("/Users/yamaokatakashi/key/dev-portfolio-db7ac-firebase-adminsdk-pzlqc-94cb3456e5.json");
	FirebaseOptions options = new FirebaseOptions.Builder()
		.setCredentials(GoogleCredentials.fromStream(refreshToken))
		.setDatabaseUrl("https://dev-portfolio-db7ac.firebaseio.com/")
		.build();
	app = FirebaseApp.initializeApp(options, "other");
}

public void uploadSkill(List<SkillCategory> categories) {
		final FirebaseDatabase database = FirebaseDatabase.getInstance(app);
		DatabaseReference ref = database.getReference("skills");

//		List<Map<String, Object>> dataList = new ArrayList<Map<String, Object>>();
//		Map<String, Object> dataMap;
//		for (SkillCategory category : categories) {
//			dataMap = new HashMap<>();
//			dataMap.put("category", category.getCategory());
//			dataMap.put("skills", ((Object) category.stream())
//					.filter(s -> s.getCategory() == category.getCategory())
//					.collect(Collectors.toList()));
//			dataList.add(dataMap);
//		}


		Map<String, Object> dataMap;
		Map<String,List<SkillCategory>>skillMap =  categories.stream()
				.collect(Collectors.groupingBy(SkillCategory::getCategory));
		List<Map<String, Object>> dataList = new ArrayList<>(skillMap.size());
//		for(Map.Entry<String,List<SkillCategory>> entry : skillMap.entrySet()) {
		String[] Categories = { "front-end", "back-end", "Devops" };
		for (String category : Categories) {
			dataMap = new HashMap<>();
//			dataMap.put("category", entry.getKey());
//			dataMap.put("skill", entry.getValue());
			dataMap.put("category", category);
			dataMap.put("skills", skillMap.get(category));
			dataList.add(dataMap);

//			switch (entry.getKey()) {
//			case "front-end":
//				dataList.add (0, dataMap);
//				break;
//			case "back-end":
//				dataList.add(1, dataMap);
//				break;
//			case "DevOps":
//				dataList.add(2, dataMap);
//				break;
//			}

//			dataList.add(0,dataMap);
//
//			System.out.println("Test");
		}
		ref.setValue(dataList, new DatabaseReference.CompletionListener() {
				@Override
				public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
					if(databaseError != null) {
							System.out.println("Data could be saved" + databaseError.getMessage());
					} else {
							System.out.println("Data save successfully.");
					}
				}
		});
	}
}