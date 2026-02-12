package org.sunbird.exception;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.sunbird.message.ResponseCode;

public class AuthorizationExceptionTest {

  @Test
  public void testNotAuthorized() {
    ResponseCode code = ResponseCode.unAuthorized;
    AuthorizationException.NotAuthorized ex = new AuthorizationException.NotAuthorized(code);
    assertEquals(code.getErrorCode(), ex.getCode());
    // The message might be null for unAuthorized, checking ResponseCode definition
    // ResponseCode.unAuthorized has "UNAUTHORIZED_USER" as message key, which is not null.
    // And IResponseMessage.Message.UNAUTHORIZED_USER = "you are an unauthorized user"
    assertEquals(code.getErrorMessage(), ex.getMessage());
    assertEquals(401, ex.getResponseCode());
  }
}
