package com.wunamor.lotterysystem.common.interceptor;

import com.wunamor.lotterysystem.common.utils.JWTUtil;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;

class LoginInterceptorTest {

    private final LoginInterceptor interceptor = new LoginInterceptor();

    @Test
    void missingTokenReturns401WithJsonBody() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean passed = interceptor.preHandle(
                new MockHttpServletRequest("GET", "/user/profile"), response, new Object());

        assertFalse(passed);
        assertEquals(401, response.getStatus());
        assertTrue(response.getContentAsString().contains("\"code\":401"));
    }

    @Test
    void invalidTokenReturns401() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/user/profile");
        request.addHeader("Authorization", "Bearer not-a-jwt");
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertFalse(interceptor.preHandle(request, response, new Object()));
        assertEquals(401, response.getStatus());
    }

    @Test
    void validTokenInAuthorizationHeaderPasses() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/user/profile");
        request.addHeader("Authorization", "Bearer " + validToken());

        assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
    }

    @Test
    void validTokenInLegacyHeadersPasses() throws Exception {
        String token = validToken();

        MockHttpServletRequest tokenRequest = new MockHttpServletRequest("GET", "/user/profile");
        tokenRequest.addHeader("token", token);
        assertTrue(interceptor.preHandle(tokenRequest, new MockHttpServletResponse(), new Object()));

        MockHttpServletRequest userTokenRequest = new MockHttpServletRequest("GET", "/user/profile");
        userTokenRequest.addHeader("user_token", token);
        assertTrue(interceptor.preHandle(userTokenRequest, new MockHttpServletResponse(), new Object()));
    }

    @Test
    void optionsRequestPassesWithoutToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("OPTIONS", "/user/profile");

        assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
    }

    @Test
    void staticResourcePassesWithoutToken() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/css/app.css");

        assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(),
                mock(ResourceHttpRequestHandler.class)));
    }

    @Test
    void registerWithoutTokenReturns401() throws Exception {
        MockHttpServletResponse response = new MockHttpServletResponse();

        boolean passed = interceptor.preHandle(
                new MockHttpServletRequest("POST", "/user/register"), response, new Object());

        assertFalse(passed);
        assertEquals(401, response.getStatus());
    }

    @Test
    void registerWithNormalUserTokenReturns403() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/user/register");
        request.addHeader("Authorization", "Bearer " + tokenWithIdentity(1));
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertFalse(interceptor.preHandle(request, response, new Object()));
        assertEquals(403, response.getStatus());
        assertTrue(response.getContentAsString().contains("\"code\":403"));
    }

    @Test
    void registerWithAdminTokenPasses() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/user/register");
        request.addHeader("Authorization", "Bearer " + tokenWithIdentity(0));

        assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
    }

    @Test
    void listWithNormalUserTokenReturns403() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/user/list");
        request.addHeader("Authorization", "Bearer " + tokenWithIdentity(1));
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertFalse(interceptor.preHandle(request, response, new Object()));
        assertEquals(403, response.getStatus());
        assertTrue(response.getContentAsString().contains("\"code\":403"));
    }

    @Test
    void listWithAdminTokenPasses() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/user/list");
        request.addHeader("Authorization", "Bearer " + tokenWithIdentity(0));

        assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
    }

    @Test
    void fileUploadWithNormalUserTokenReturns403() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/file/upload/prize");
        request.addHeader("Authorization", "Bearer " + tokenWithIdentity(1));
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertFalse(interceptor.preHandle(request, response, new Object()));
        assertEquals(403, response.getStatus());
        assertTrue(response.getContentAsString().contains("\"code\":403"));
    }

    @Test
    void fileUploadWithAdminTokenPasses() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/file/upload/prize");
        request.addHeader("Authorization", "Bearer " + tokenWithIdentity(0));

        assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
    }

    @Test
    void prizeCreateWithNormalUserTokenReturns403() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/prize/create");
        request.addHeader("Authorization", "Bearer " + tokenWithIdentity(1));
        MockHttpServletResponse response = new MockHttpServletResponse();

        assertFalse(interceptor.preHandle(request, response, new Object()));
        assertEquals(403, response.getStatus());
        assertTrue(response.getContentAsString().contains("\"code\":403"));
    }

    @Test
    void prizeCreateWithAdminTokenPasses() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/prize/create");
        request.addHeader("Authorization", "Bearer " + tokenWithIdentity(0));

        assertTrue(interceptor.preHandle(request, new MockHttpServletResponse(), new Object()));
    }

    private String validToken() {
        return JWTUtil.genJwt(Map.<String, Object>of("userId", 1));
    }

    private String tokenWithIdentity(int identity) {
        return JWTUtil.genJwt(Map.<String, Object>of("userId", 1, "identity", identity));
    }
}
