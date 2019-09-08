package test.mannanova.bitbucketissuetest;

import org.aeonbits.owner.Config;

public interface BitbucketConfig extends Config{

  @DefaultValue("https://api.bitbucket.org/")
  String apiHost();

  @DefaultValue("mannanova-apitest")
  String userName();

  @DefaultValue("2u2HbZAs")
  String password();

  @DefaultValue("create-issue-test-repo")
  String repo();
}