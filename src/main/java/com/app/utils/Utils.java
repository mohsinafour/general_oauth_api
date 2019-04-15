package com.app.utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import com.google.api.client.googleapis.auth.oauth2.GoogleBrowserClientRequestUrl;
import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.auth.oauth2.GoogleTokenResponse;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.people.v1.PeopleService;
import com.google.api.services.people.v1.PeopleServiceScopes;
import com.google.api.services.people.v1.model.ListConnectionsResponse;
import com.google.api.services.people.v1.model.Person;

public class Utils {

	private static final Map<String, Function<String, String>> map;
	private static final Map<String, Function<Map<String, String>, ResponseEntity<String>>> postCallmap;
	private static final Map<String, Function<String, String>> fetchDatamap;
	private static final Map<String, String> datamap;
	private static final Map<String, Function<String, String>> processTokenmap;

	static {

		datamap = new HashMap<String, String>();
		datamap.put("google_id", "355702574650-p3d0dj5urm9bghls8qk0rfcic7g6gaj2.apps.googleusercontent.com");
		datamap.put("google_secret", "EFo05h91VTIQgVn-MZUJhcp6");
		datamap.put("jira_id", "X5HlpxkKWgEnoLKX0ZNAGkCTcBN43INv");
		datamap.put("jira_secret", "mvjq-QgImb7rRm_k5QJwFs0sryLwXwLtFIk5QDiIDoQrEvDnZsjqQsFI35XtlDs5");
		datamap.put("github_id", "9647494c6d55774479e1");
		datamap.put("github_secret", "3c3a9c09672e159d81a3c24f3e56ef0465b66d9a");
		datamap.put("redirectUrl", "http://localhost:8888/general/general_oauth_redirect");
		datamap.put("linkedin_id", "86t6phf8kn1l8c");
		datamap.put("linkedin_secret", "C4MBdCaDE0QV0jmm");

		map = new HashMap<String, Function<String, String>>();
		map.put("GOOGLE", (sid) -> {
			String authorizationUrl = new GoogleBrowserClientRequestUrl(datamap.get("google_id"),
					datamap.get("redirectUrl"),
					Arrays.asList(PeopleServiceScopes.CONTACTS, PeopleServiceScopes.PLUS_LOGIN)).build();
			return authorizationUrl;
		});

		map.put("JIRA", (sid) -> {
			String authorizationUrl = "https://auth.atlassian.com/authorize?audience=api.atlassian.com&client_id="
					+ datamap.get("jira_id") + "&scope=read:jira-user&redirect_uri=" + datamap.get("redirectUrl")
					+ "&state=" + sid + "&response_type=code&prompt=consent";
			return authorizationUrl;
		});

		map.put("GITHUB", (sid) -> {
			String authorizationUrl = "https://github.com/login/oauth/authorize?client_id=" + datamap.get("github_id")
					+ "&redirect_uri=" + datamap.get("redirectUrl") + "&login=&scope=read:user&state=" + sid
					+ "&allow_signup=true";
			return authorizationUrl;
		});

		map.put("LINKEDIN", sid -> {
			String authorizationUrl = "https://www.linkedin.com/oauth/v2/authorization?response_type=code&client_id="
					+ datamap.get("linkedin_id") + "&redirect_uri=" + datamap.get("redirectUrl") + "&state=" + sid
					+ "&scope=r_emailaddress,r_liteprofile,w_member_social";
			return authorizationUrl;
		});

		postCallmap = new HashMap<String, Function<Map<String, String>, ResponseEntity<String>>>();
		postCallmap.put("GITHUB", valuesMap -> {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			String url = "https://github.com/login/oauth/access_token";
			MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
			map.add("client_id", datamap.get("github_id"));
			map.add("client_secret", datamap.get("github_secret"));
			map.add("code", valuesMap.get("code"));
			map.add("redirect_uri", "http://localhost:8888/general/general_oauth_redirect");
			map.add("state", valuesMap.get("state"));
			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map,
					headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> recievedResponse = restTemplate.postForEntity(url, request, String.class);
			return recievedResponse;
		});

		postCallmap.put("LINKEDIN", valuesMap -> {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			String url = "https://www.linkedin.com/oauth/v2/accessToken";
			MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
			map.add("grant_type", "authorization_code");
			map.add("code", valuesMap.get("code"));
			map.add("redirect_uri", datamap.get("redirectUrl"));
			map.add("client_id", datamap.get("linkedin_id"));
			map.add("client_secret", datamap.get("linkedin_secret"));
			// map.add("state", valuesMap.get("state"));
			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map,
					headers);
			RestTemplate restTemplate = new RestTemplate();
			ResponseEntity<String> recievedResponse = restTemplate.postForEntity(url, request, String.class);
			return recievedResponse;
		});

		fetchDatamap = new HashMap<String, Function<String, String>>();
		fetchDatamap.put("GOOGLE", access_token -> {
			try {
				return fetchDataGoogle(access_token);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		});

		fetchDatamap.put("GITHUB", access_token -> {
			try {
				return fetchDataGitHub(access_token);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		});

		fetchDatamap.put("LINKEDIN", access_token -> {
			try {
				return fetchDataLinkedI(access_token);
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		});

		processTokenmap = new HashMap<String, Function<String, String>>();
		processTokenmap.put("GITHUB", body -> {
			String x = body.substring(body.indexOf("access_token"), body.indexOf("&"));
			return x;
		});

		processTokenmap.put("LINKEDIN", body -> {
			String x = body.substring(StringUtils.ordinalIndexOf(body, "\"", 3) + 1,
					StringUtils.ordinalIndexOf(body, "\"", 4));
			return x;
		});
	}

	public static String landProcess(String name, String state) {
		return map.get(name).apply(state);
	}

	public static ResponseEntity<String> postForToken(Map<String, String> params) {
		return postCallmap.get(params.get("site")).apply(params);
	}

	public static String fetchData(String site, String access_token) {
		return fetchDatamap.get(site).apply(access_token);
	}

	public static String fetchDataGoogle(String access_token) throws IOException {
		HttpTransport httpTransport = new NetHttpTransport();
		JacksonFactory jsonFactory = new JacksonFactory();
		GoogleTokenResponse tokenResponse = new GoogleTokenResponse().setAccessToken(access_token);
		GoogleCredential credential = new GoogleCredential.Builder().setTransport(httpTransport)
				.setJsonFactory(jsonFactory).setClientSecrets(datamap.get("google_id"), datamap.get("google_secret"))
				.build().setFromTokenResponse(tokenResponse);
		PeopleService peopleService = new PeopleService.Builder(httpTransport, jsonFactory, credential).build();
		ListConnectionsResponse connectionResponse = peopleService.people().connections().list("people/me")
				.setPersonFields("names,emailAddresses").execute();
		List<Person> connections = connectionResponse.getConnections();
		return connections.toString();
	}

	public static String fetchDataGitHub(String access_token) throws IOException {

		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate.getForObject("https://api.github.com/user?" + access_token, String.class);
		return result;
	}

	public static String fetchDataLinkedI(String access_token) throws IOException {

		String header = "Bearer " + access_token;
		HttpHeaders headers = new HttpHeaders();
		headers.set("Authorization", header);
		headers.set("Connection", "Keep-Alive");
		HttpEntity<String> entity = new HttpEntity<>("parameters", headers);
		RestTemplate restTemplate = new RestTemplate();
		String result = restTemplate
				.exchange("https://api.linkedin.com/v2/me", HttpMethod.GET, entity, String.class)
				.toString();
		return result;
	}

	public static String processToken(String site, String body) {
		return processTokenmap.get(site).apply(body);
	}

}
