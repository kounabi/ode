package org.apache.ode.axis2;

/**
 * Thrown on deployment error.
 */
public class DeploymentException extends RuntimeException {

  public DeploymentException(String message) {
    super(message);
  }

  public DeploymentException(String message, Throwable cause) {
    super(message, cause);
  }

  public DeploymentException(Throwable cause) {
    super(cause);
  }
}
