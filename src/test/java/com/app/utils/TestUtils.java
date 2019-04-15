package com.app.utils;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

@DisplayName("Tests for Utils Class")
public class TestUtils {


	@Test
	@DisplayName("Testing landProcess()")
	public void testLandProcess() {
		assertEquals("https://accounts.google.com/o/oauth2/auth?client_id=355702574650-p3d0dj5urm9bghls8qk0rfcic7g6gaj2.apps.googleusercontent.com&redirect_uri=http://localhost:8888/general/general_oauth_redirect&response_type=token&scope=https://www.googleapis.com/auth/contacts%20https://www.googleapis.com/auth/plus.login",
				Utils.landProcess("GOOGLE", "asdhsadhskajhdsakjdh"));
		assertEquals("https://github.com/login/oauth/authorize?client_id=9647494c6d55774479e1&redirect_uri=http://localhost:8888/general/general_oauth_redirect&login=&scope=read:user&state=asdhsadhskajhdsakjdh&allow_signup=true",
				Utils.landProcess("GITHUB", "asdhsadhskajhdsakjdh"));
		assertEquals("https://www.linkedin.com/oauth/v2/authorization?response_type=code&client_id=86t6phf8kn1l8c&redirect_uri=http://localhost:8888/general/general_oauth_redirect&state=asdhsadhskajhdsakjdh&scope=r_emailaddress,r_liteprofile,w_member_social"
				,Utils.landProcess("LINKEDIN", "asdhsadhskajhdsakjdh"));
	}
	

}
