package test.mannanova.bitbucketissuetest;

import org.aeonbits.owner.Config;

public interface BitbucketConfig extends Config{

  @DefaultValue("https://api.bitbucket.org/")
  String apiHost();

  @DefaultValue("Mannanova18")
  String userName();

  @DefaultValue("Jaguarwow13")
  String password();

  @DefaultValue("BitbucketIssueTest")
  String repo();
}