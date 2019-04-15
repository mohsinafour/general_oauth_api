package com.app.execs;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.app.utils.Utils;

@RestController
@RequestMapping("/general")
public class MainController {

	@GetMapping("/")
	public String home() {
		return "home";
	}

	@GetMapping("/request_data")
	@ResponseBody
	public void landGet(@RequestParam String name, HttpServletResponse response, HttpSession hs) throws IOException {
		hs.setMaxInactiveInterval(3600);
		hs.setAttribute("site", name.toUpperCase());
		response.sendRedirect(Utils.landProcess(name.toUpperCase(), hs.getId()));
	}

	@GetMapping("general_oauth_redirect")
	public void authoris(@RequestParam(value = "code", required = false) String code,
			@RequestParam(value = "state", required = false) String state, HttpServletResponse response, HttpSession hs)
			throws IOException {
		// This controller handles authorization code sent by authentication server
		// (OAuth server).
		// code is authorization_code. If it is not present this means that requested
		// SSO is google's so it will
		// take access_code if it is sent in redirected controller.
		System.out.println("after redirect");
		System.out.println("in redirect from resource server hs="+hs.getId());
		Optional<String> codeOptional = Optional.ofNullable(code);
		codeOptional.ifPresent(code1 -> {
			Map<String, String> params = new HashMap<>();
			params.put("code", code);
			params.put("state", state);
			params.put("site", hs.getAttribute("site").toString());
			ResponseEntity<String> recievedResponse = Utils.postForToken(params);
			System.out.println("recieved response"+recievedResponse);
			hs.setAttribute("access_response_" + hs.getAttribute("site").toString(), recievedResponse);
		});
		response.sendRedirect("http://localhost:3000/redirect");
	}

	@SuppressWarnings("unchecked")
	@GetMapping("general_oauth_redirected")
	@ResponseBody
	public void authorised(@RequestParam(value = "access_token", required = false) String access_token,
			HttpServletResponse response, HttpServletRequest request, HttpSession hs) throws IOException {
		
		if (access_token != null) {
			System.out.println(Utils.fetchData(hs.getAttribute("site").toString(), access_token));
			System.out.println("asdhashd");
		} 
		else 
		{
			String x = ((ResponseEntity<String>) hs
					.getAttribute("access_response_" + hs.getAttribute("site").toString())).getBody();
			String result = Utils.fetchData(hs.getAttribute("site").toString(),
					Utils.processToken(hs.getAttribute("site").toString(), x));
			System.out.println(result);
			System.out.println("in spring redirect hs="+hs.getId());
			hs.setAttribute("result", result);
		}
		response.sendRedirect("http://localhost:3000/data");
	}

	@GetMapping("fetch")
	@ResponseBody
	@CrossOrigin(origins = "*", allowedHeaders = "*")
	public String testing(HttpSession hs) {
		if(hs.getAttribute("result") == null)
			System.out.println("null");
		//System.out.println(hs.getAttribute("result").toString());
		System.out.println("in fetch data hs="+hs.getId());
		return hs.getAttribute("result").toString();
	}

}
