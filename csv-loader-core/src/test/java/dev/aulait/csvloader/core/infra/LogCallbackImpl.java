package dev.aulait.csvloader.core.infra;

public class LogCallbackImpl implements LogCallback {

  @Override
  public void info(String log) {
    System.out.println(log);
  }
}
