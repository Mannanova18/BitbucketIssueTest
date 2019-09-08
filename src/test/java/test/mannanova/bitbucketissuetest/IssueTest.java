package test.mannanova.bitbucketissuetest;

import com.jayway.restassured.response.ResponseBody;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;

import org.apache.log4j.Logger;
import static com.jayway.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class IssueTest {

  private WebDriver webDriver;
  private final String TITLE = "Test Issue 1234";
  private BitbucketConfig bitbucketConfig = new BitbucketConfig();
  private Issue issue;
  final static Logger logger = Logger.getLogger(IssueTest.class);

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
  public void CreateIssueTest(){
//    BasicConfigurator.configure();
//    logger.debug("Hello World!");
//    logger.info("Info");
//    logger.warn("warning!");
//    logger.error("error");
//    issue = createIssue(TITLE);
  }

  @Test
  public void checkTitleAfterCreate() {
    String url = String.format(
        "https://bitbucket.org/%s/%s/issues/%s",
            bitbucketConfig.userName(),
            bitbucketConfig.repository(),
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
        .auth().basic(bitbucketConfig.eMail(), bitbucketConfig.password())
        .header("Content-Type", "application/json")
        .baseUri(bitbucketConfig.apiUrl())
        .body(issue)
        .when()
        .post("2.0/repositories/"+bitbucketConfig.userName()+ "/"+bitbucketConfig.repository()+"/issues")
        .getBody();
    logger.info(response.asString());
    return response.as(Issue.class);
  }

  private void deleteIssue(Issue issue) {
    given()
        .auth().preemptive()
            .basic(bitbucketConfig.userName(), bitbucketConfig.password())
            .baseUri(bitbucketConfig.apiUrl())
            .when()
            .delete(
                    "2.0/repositories/"+bitbucketConfig.userName()+
                            "/"+bitbucketConfig.repository()+
                            "/issues/"+issue.getId()
            )
            .then().statusCode(204);
  }
}