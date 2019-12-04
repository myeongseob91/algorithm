package com.geotwo.o2editor.feature.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.geotwo.o2editor.common.env.EditorConfigure;
import com.geotwo.o2editor.common.env.EditorLanguage;
import com.geotwo.o2editor.common.env.EditorMapConfigure;
import com.geotwo.o2editor.common.util.ObjectMapperSupport;
import com.geotwo.o2editor.common.util.ThreadUtil;
import com.geotwo.o2editor.util.Cast;

@Controller
@EnableWebMvc
@RequestMapping("/o2editor/feature")
public class FeatureController {

	private static final Logger logger = LoggerFactory.getLogger(FeatureController.class);

	@RequestMapping(value = "/edit/start.do", method = RequestMethod.POST, produces = "application/json; charset=utf8")
	@ResponseBody
	public String editStart(HttpSession session, HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, Object> param) {
		Map<String, Object> result = new HashMap<>();
		result.put("SUCCESS", true);

		Map<String, String> editinfo = new HashMap<>();
		editinfo.put("userName", new Cast(param.get("userName")).cString());
		editinfo.put("layerId", new Cast(param.get("layerId")).cString());
		editinfo.put("layerName", new Cast(param.get("layerName")).cString());
		editinfo.put("UUID", new Cast(param.get("UUID")).cString());
		
		session.setAttribute("feature.editinfo", editinfo);

		return ObjectMapperSupport.objectToJson(result);
	}

	@RequestMapping(value = "/edit/end.do", method = { RequestMethod.GET, RequestMethod.POST }, produces = "application/json; charset=utf8")
	@ResponseBody
	public String editEnd(HttpSession session, HttpServletRequest request, HttpServletResponse response, @RequestParam Map<String, Object> param) {
		Map<String, Object> result = new HashMap<>();
		boolean flag = true;
		
		Object obj = session.getAttribute("feature.editinfo");
		if (obj == null) {
			flag = false;
		} else {
			//@SuppressWarnings("unchecked")
			//Map<String, String> editinfo = (Map<String, String>) obj;
			session.removeAttribute("feature.editinfo");
		}
		result.put("SUCCESS", flag);
		return ObjectMapperSupport.objectToJson(result);
	}

	@RequestMapping("/tableedit.do")
	public String tableedit(HttpServletRequest request, HttpServletResponse response, HttpSession session, Map<String, Object> model) throws IOException {

		try {
			String lang = request.getParameter("LANG");
			if (lang == null || lang.equals("")) {
				lang = EditorConfigure.getInstance().getValue("o2editor.language", "en");
			}

			Map<String, Object> language = new HashMap<String, Object>();
			language.put("lang.menu", EditorLanguage.getInstance().LANG_MENU);
			language.put("lang.form", EditorLanguage.getInstance().LANG_FORM);
			language.put("lang.msg", EditorLanguage.getInstance().LANG_MSG);

			model.put("LANG_TYPE", lang);
			model.put("LANG_JSON", ObjectMapperSupport.objectToJson(language));
			model.put("LANG_OBJT", language);
			model.put("MAP_CONF", ObjectMapperSupport.objectToJson(EditorMapConfigure.getConfigure()));
			model.put("DEV_MODE", EditorConfigure.getInstance().getValue("o2editor.devmode", "false"));

			return "/o2editor/feature/tableedit";

		} catch (Exception e) {
			logger.error(ThreadUtil.getStackTrace(e));

			String contextPath = request.getContextPath();
			if (StringUtils.isEmpty(contextPath))
				contextPath = "/o2editor";
			
			response.sendRedirect(request.getContextPath() + "/login.do");
			return null;
		}

	}
	
	@RequestMapping("/editsummary.do")
	public String editsummary(HttpServletRequest request, HttpServletResponse response, HttpSession session, Map<String, Object> model) throws IOException {

		try {
			String lang = request.getParameter("LANG");
			if (lang == null || lang.equals("")) {
				lang = EditorConfigure.getInstance().getValue("o2editor.language", "en");
			}

			Map<String, Object> language = new HashMap<String, Object>();
			language.put("lang.menu", EditorLanguage.getInstance().LANG_MENU);
			language.put("lang.form", EditorLanguage.getInstance().LANG_FORM);
			language.put("lang.msg", EditorLanguage.getInstance().LANG_MSG);

			model.put("LANG_TYPE", lang);
			model.put("LANG_JSON", ObjectMapperSupport.objectToJson(language));
			model.put("LANG_OBJT", language);
			model.put("MAP_CONF", ObjectMapperSupport.objectToJson(EditorMapConfigure.getConfigure()));
			model.put("DEV_MODE", EditorConfigure.getInstance().getValue("o2editor.devmode", "false"));

			return "/o2editor/feature/editsummary";

		} catch (Exception e) {
			logger.error(ThreadUtil.getStackTrace(e));

			String contextPath = request.getContextPath();
			if (StringUtils.isEmpty(contextPath))
				contextPath = "/o2editor";
			
			response.sendRedirect(request.getContextPath() + "/login.do");
			return null;
		}

	}	

}
