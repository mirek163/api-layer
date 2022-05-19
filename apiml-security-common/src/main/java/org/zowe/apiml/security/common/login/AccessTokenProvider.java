package org.zowe.apiml.security.common.login;

import java.io.IOException;

public interface AccessTokenProvider {

    String getToken(String userId) throws IOException;
}
