package org.zowe.apiml.security.common.login;

import java.io.IOException;

public interface AccessTokenProvider {

    String createToken(String userId, AccessTokenRequest accessTokenRequest) throws IOException;
}
