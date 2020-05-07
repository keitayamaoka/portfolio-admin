package com.seattleacademy.team20;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
public class skillController {

	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(skillController.class);

	@RequestMapping(value = "/skillUpload" , method = RequestMethod.GET)
	public String skillUpload(Locale locale, Model model) {
		logger.info("welcome SkillUpload! The client locale is {}", locale);

		Date date = new Date();
		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.LONG, DateFormat.LONG, locale);

		String formattedDate = dateFormat.format(date);

		model.addAttribute("serverTime", formattedDate );

		return "skillUpload";
	}
}