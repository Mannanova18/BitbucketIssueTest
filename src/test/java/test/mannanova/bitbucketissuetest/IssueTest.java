package test.mannanova.bitbucketissuetest;

import com.jayway.restassured.response.ResponseBody;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.aeonbits.owner.ConfigFactory;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class IssueTest {

  private WebDriver webDriver;
  private final String TITLE = RandomStringUtils.randomAlphabetic(15);
  private BitbucketConfig bitbucketConfig = ConfigFactory.create(BitbucketConfig.class);
  private Issue issue;

  @Before
  public void startWebDriver() {
    issue = createIssue(TITLE);
    WebDriverManager.chromedriver().setup();
    webDriver = new ChromeDriver();
  }

  @After
  public void stopWebDriver() {
    webDriver.quit();
    deleteIssue(issue);
  }

  @Test
  public void shouldSeeCreatedIssueTitle() {
    String url = String.format(
        "https://bitbucket.org/%s/%s/issues/%s",
            bitbucketConfig.userName(),
            bitbucketConfig.repo(),
            issue.getId()
    );

    webDriver.get(url);
    WebElement issueTitle = webDriver.findElement(By.xpath("//*[@id=\"issue-title\"]"));
    assertThat(issueTitle.getText(), equalTo(issue.getTitle()));
  }

  private Issue createIssue(String title) {
    issue = new Issue(title);
    issue.setTitle(title);

    ResponseBody response = given()
        .auth().basic(bitbucketConfig.userName(), bitbucketConfig.password())
        .header("Content-Type", "application/json")
        .baseUri(bitbucketConfig.apiHost())
        .body(issue)
        .when()
        .post(
            String.format(
                "2.0/repositories/%s/%s/issues",
                    bitbucketConfig.userName(),
                    bitbucketConfig.repo()
            )
        )
        .getBody();

    return response.as(Issue.class);
  }

  private void deleteIssue(Issue issue) {
    given()
        .auth().preemptive().basic(bitbucketConfig.userName(), bitbucketConfig.password())
        .baseUri(bitbucketConfig.apiHost())
        .when()
        .delete(
            String.format(
                "2.0/repositories/%s/%s/issues/%s",
                    bitbucketConfig.userName(),
                    bitbucketConfig.repo(),
                issue.getId()
            )
        )
        .then()
        .statusCode(204);
  }
}