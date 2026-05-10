import config.env_target;
import models.UserModel;
import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.annotations.DataProvider;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@Feature("User Management")
public class UserTest {
    String dynamicUsername = "aufaahsnt";

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = env_target.BASE_URL;
    }

    @Test(priority = 1)
    public void testCreateUser() {
        UserModel userBody = new UserModel(
                10101, dynamicUsername, "Aufaa", "Husniati",
                "aufaahsnt21@gmail.com", "aufaa123", "08123456789", 1
        );

        given()
                .contentType(ContentType.JSON)
                .body(userBody)
                .when()
                .post("/user")
                .then()
                .statusCode(200)
                .body("message", equalTo("10101"));
    }

    @Test(priority = 2, dependsOnMethods = "testCreateUser")
    public void testGetUserByName() {
        given()
                .pathParam("username", dynamicUsername)
                .when()
                .get("/user/{username}")
                .then()
                .statusCode(200)
                .body("username", equalTo(dynamicUsername))
                .body("firstName", equalTo("Aufaa"))
                .log().all();
    }

    @Test(priority = 3, dependsOnMethods = "testGetUserByName")
    public void testUpdateUser() {
        UserModel updatedUser = new UserModel();
        updatedUser.setId(10101);
        updatedUser.setUsername(dynamicUsername);
        updatedUser.setFirstName("Aufaa");
        updatedUser.setLastName("Upeh");
        updatedUser.setEmail("aufaahsnt@gmail.com");
        updatedUser.setPassword("aufaa123");
        updatedUser.setPhone("08999999999");
        updatedUser.setUserStatus(1);

        given()
                .contentType(ContentType.JSON)
                .pathParam("username", dynamicUsername)
                .body(updatedUser)
                .when()
                .put("/user/{username}")
                .then()
                .statusCode(200)
                .log().all();
    }

    @Test(priority = 4, dependsOnMethods = "testUpdateUser")
    public void testDeleteUser() {
        given()
                .pathParam("username", dynamicUsername)
                .when()
                .delete("/user/{username}")
                .then()
                .statusCode(200)
                .body("message", equalTo(dynamicUsername));
    }

    @Test(priority = 5, dependsOnMethods = "testDeleteUser")
    public void testGetDeletedUser() {
        given()
                .pathParam("username", dynamicUsername)
                .when()
                .get("/user/{username}")
                .then()
                .statusCode(404)
                .body("message", equalTo("User not found"));
    }

    @Test(priority = 6)
    public void testDeleteNonExistentUser() {
        given()
                .pathParam("username", "userGhost")
                .when()
                .delete("/user/{username}")
                .then()
                .statusCode(404);
    }

    @DataProvider(name = "invalidLoginData")
    public Object[][] provideInvalidLoginData() {
        return new Object[][] {
                {"invaliduser", "aufaa123", 404},
                {dynamicUsername, "Invaliduser123", 404},
                {"", "aufaa123", 404},
                {dynamicUsername, "", 404},
                {"", "", 404}
        };
    }

    @Test(priority = 7, dataProvider = "invalidLoginData")
    public void testLoginNegativeCases(String username, String password, int expectedStatusCode) {
        given()
                .queryParam("username", username)
                .queryParam("password", password)
                .when()
                .get("/user/login")
                .then()
                .statusCode(expectedStatusCode);
    }

    @Test(priority = 8)
    public void testLogoutSuccess() {
        given()
                .when()
                .get("/user/logout")
                .then()
                .statusCode(200)
                .body("message", equalTo("ok"))
                .log().all();
    }

    @Test(priority = 9)
    public void testFindUserEmptyUsername() {
        given()
                .pathParam("username", "")
                .when()
                .get("/user/{username}")
                .then()
                .statusCode(anyOf(is(404), is(405)))
                .log().all();
    }

    @Test(priority = 10)
    public void testUpdateUserWithInvalidUsername() {
        UserModel updatedUser = new UserModel(20202, "nonExistentUser", "Empty", "User", "empty@mail.com", "pass123", "081", 1);

        given()
                .contentType(ContentType.JSON)
                .pathParam("username", "empty_user")
                .body(updatedUser)
                .when()
                .put("/user/{username}")
                .then()
                .statusCode(404)
                .log().all();
    }

    @Test(priority = 11)
    public void testUpdateUserWithEmptyUsername() {
        UserModel updatedUser = new UserModel(10101, "", "Empty", "User", "empty@mail.com", "pass123", "081", 1);

        given()
                .contentType(ContentType.JSON)
                .pathParam("username", "")
                .body(updatedUser)
                .when()
                .put("/user/{username}")
                .then()
                .statusCode(anyOf(is(404), is(405), is(400)))
                .log().all();
    }

    @Test(priority = 12)
    public void testFindUserWithInvalidUsername() {
        String invalidUsername = "!@#$%^&*()_+";

        given()
                .pathParam("username", invalidUsername)
                .when()
                .get("/user/{username}")
                .then()
                .statusCode(404)
                .body("message", equalTo("User not found"))
                .log().all();
    }

    @Test(priority = 13)
    public void testDeleteUserWithEmptyUsername() {
        given()
                .pathParam("username", "")
                .when()
                .delete("/user/{username}")
                .then()
                .statusCode(anyOf(is(404), is(405)))
                .log().all();
    }
}