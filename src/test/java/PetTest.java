import config.env_target;
import models.PetModel;
import java.io.File;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import io.qameta.allure.*;

public class PetTest {
    long dynamicPetId;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = env_target.BASE_URL;
    }

    @Test(priority = 1)
    public void addNewPet() {
        PetModel petBody = new PetModel(12345, "Doggie", "available");

        Response response = given()
                .contentType(ContentType.JSON)
                .body(petBody)
                .when()
                .post("/pet")
                .then()
                .statusCode(200)
                .extract().response();

        dynamicPetId = response.jsonPath().getLong("id");
        System.out.println("ID yang baru dibuat: " + dynamicPetId);
    }

    @Test(priority = 2, dependsOnMethods = "addNewPet")
    public void getPetById() {
        given()
                .pathParam("petId", dynamicPetId)
                .when()
                .get("/pet/{petId}")
                .then()
                .statusCode(200)
                .body("id", equalTo((int) dynamicPetId))
                .log().all();
    }

    @Test(priority = 3, dependsOnMethods = "getPetById")
    public void testUpdatePet() {
        PetModel updatedPet = new PetModel(dynamicPetId, "Doggie Updated", "sold");

        given()
                .contentType(ContentType.JSON)
                .body(updatedPet)
                .when()
                .put("/pet")
                .then()
                .statusCode(200)
                .body("status", equalTo("sold"))
                .body("id", equalTo((int) dynamicPetId))
                .log().all();
    }

    @Test(priority = 4)
    public void testGetPetWithInvalidIdFormat() {
        given()
                .pathParam("petId", "abcDoge")
                .when()
                .get("/pet/{petId}")
                .then()
                .statusCode(404)
                .body("type", equalTo("unknown"));
    }

    @Test(priority = 5, dependsOnMethods = "addNewPet")
    public void testUploadPetImage() {
        File imageFile = new File("src/test/resources/dog.jpg");

        given()
                .pathParam("petId", dynamicPetId)
                .multiPart("file", imageFile)
                .multiPart("additionalMetadata", "Foto Update")
                .when()
                .post("/pet/{petId}/uploadImage")
                .then()
                .statusCode(200)
                .body("message", containsString("uploaded to"));
    }

    @Test(priority = 6)
    public void testAddNewPetWithEmptyBody() {
        given()
                .contentType(ContentType.JSON)
                .body("{}") // Mengirim JSON kosong
                .when()
                .post("/pet")
                .then()
                .statusCode(anyOf(is(400), is(405), is(200)))
                .log().all();
    }

    @Test(priority = 7)
    public void testFindPetsByStatusAvailable() {
        given()
                .queryParam("status", "available")
                .when()
                .get("/pet/findByStatus")
                .then()
                .statusCode(200)
                .body("status", everyItem(equalTo("available")))
                .log().all();
    }

    @Test(priority = 8)
    public void testFindPetsByStatusPending() {
        given()
                .queryParam("status", "pending")
                .when()
                .get("/pet/findByStatus")
                .then()
                .statusCode(200)
                .body("status", everyItem(equalTo("pending")))
                .log().all();
    }

    @Test(priority = 9)
    public void testFindPetsByStatusSold() {
        given()
                .queryParam("status", "sold")
                .when()
                .get("/pet/findByStatus")
                .then()
                .statusCode(200)
                .body("status", everyItem(equalTo("sold")))
                .log().all();
    }

    @Test(priority = 10)
    public void testFindPetWithEmptyId() {
        given()
                .pathParam("petId", "")
                .when()
                .get("/pet/{petId}")
                .then()
                .statusCode(anyOf(is(404), is(405)))
                .log().all();
    }

    @Test(priority = 11)
    public void testUploadImageWithEmptyId() {
        File imageFile = new File("src/test/resources/dog.jpg");

        given()
                .pathParam("petId", "")
                .multiPart("file", imageFile)
                .when()
                .post("/pet/{petId}/uploadImage")
                .then()
                .statusCode(anyOf(is(404), is(415)))
                .log().all();
    }

    @Test(priority = 12, dependsOnMethods = "addNewPet")
    public void testUpdatePetWithFormData() {
        given()
                .contentType("application/x-www-form-urlencoded")
                .pathParam("petId", dynamicPetId)
                .formParam("name", "Doggie Form Update")
                .formParam("status", "pending")
                .when()
                .post("/pet/{petId}")
                .then()
                .statusCode(200)
                .body("message", equalTo(String.valueOf(dynamicPetId)))
                .log().all();
    }

    @Test(priority = 13, dependsOnMethods = "testUpdatePet")
    public void testDeletePet() {
        given()
                .pathParam("id", dynamicPetId)
                .when()
                .delete("/pet/{id}")
                .then()
                .statusCode(200)
                .body("message", equalTo(String.valueOf(dynamicPetId)));

        given()
                .pathParam("id", dynamicPetId)
                .when()
                .get("/pet/{id}")
                .then()
                .statusCode(404);
    }
}