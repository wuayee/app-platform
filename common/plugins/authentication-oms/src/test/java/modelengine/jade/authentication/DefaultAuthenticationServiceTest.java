/*
 * Copyright (c) Huawei Technologies Co., Ltd. 2024-2024. All rights reserved.
 */

package modelengine.jade.authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import modelengine.fit.http.Cookie;
import modelengine.fit.http.server.HttpClassicServerRequest;
import modelengine.fit.http.support.DefaultCookieCollection;
import modelengine.fitframework.annotation.Fit;
import modelengine.fitframework.serialization.ObjectSerializer;
import modelengine.fitframework.test.annotation.FitTestWithJunit;
import modelengine.jade.authentication.impl.DefaultAuthenticationService;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

/**
 * {@link DefaultAuthenticationService} 的测试。
 *
 * @since 2024-08-10
 */
@FitTestWithJunit(includeClasses = {

})
public class DefaultAuthenticationServiceTest {
    private static final String USERNAME_KEY = "username";
    private static final String DEFAULT_USERNAME = "Jade";
    private static final String MODEL_ENGINE_USERNAME = "admin";

    @Fit
    private ObjectSerializer objectSerializer;

    @Test
    @Disabled("单测需要重新写，后补充")
    void testGetUserName_CookiesNotContain() {
        HttpClassicServerRequest request = Mockito.mock(HttpClassicServerRequest.class);
        when(request.cookies()).thenReturn(new DefaultCookieCollection());

        DefaultAuthenticationService authenticationService = new DefaultAuthenticationService(this.objectSerializer);

        String userName = authenticationService.getUserName(request);
        assertEquals(DEFAULT_USERNAME, userName);
    }

    @Test
    @Disabled("单测需要重新写，后补充")
    void testGetUserName_CookiesContain() {
        HttpClassicServerRequest request = Mockito.mock(HttpClassicServerRequest.class);
        Cookie userNameCookie = Cookie.builder().name(USERNAME_KEY).value(MODEL_ENGINE_USERNAME).build();
        DefaultCookieCollection cookies = new DefaultCookieCollection();
        cookies.add(userNameCookie);
        when(request.cookies()).thenReturn(cookies);

        DefaultAuthenticationService authenticationService = new DefaultAuthenticationService(this.objectSerializer);

        String userName = authenticationService.getUserName(request);
        assertEquals(MODEL_ENGINE_USERNAME, userName);
    }
}