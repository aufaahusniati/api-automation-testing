import config.env_target;
import models.OrderModel;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import io.qameta.allure.*;

public class StoreTest {

    int dynamicOrderId;

    @BeforeClass
    public void setup() {
        RestAssured.baseURI = env_target.BASE_URL;
    }

    @Test(priority = 1)
    public void testGetInventory() {
        given()
                .when()
                .get("/store/inventory")
                .then()
                .statusCode(200)
                .body("available", is(notNullValue()))
                .log().all();
    }

    @Test(priority = 2)
    public void testPlaceAnOrder() {
        OrderModel orderBody = new OrderModel(
                5,
                12345L,
                1,
                "2026-05-10T00:00:00.000Z",
                "placed",
                true
        );

        Response response = given()
                .contentType(ContentType.JSON)
                .body(orderBody)
                .when()
                .post("/store/order")
                .then()
                .statusCode(200)
                .body("status", equalTo("placed"))
                .extract().response();

        dynamicOrderId = response.jsonPath().getInt("id");
        System.out.println("Order ID yang berhasil dibuat: " + dynamicOrderId);
    }

    @Test(priority = 3, dependsOnMethods = "testPlaceAnOrder")
    public void testFindOrderById() {
        given()
                .pathParam("orderId", dynamicOrderId)
                .when()
                .get("/store/order/{orderId}")
                .then()
                .statusCode(200)
                .body("id", equalTo(dynamicOrderId))
                .body("petId", equalTo(12345))
                .log().all();
    }

    @Test(priority = 4, dependsOnMethods = "testFindOrderById")
    public void testDeleteOrder() {
        given()
                .pathParam("orderId", dynamicOrderId)
                .when()
                .delete("/store/order/{orderId}")
                .then()
                .statusCode(200)
                .body("message", equalTo(String.valueOf(dynamicOrderId)));
    }

    @Test(priority = 5)
    public void testFindOrderWithInvalidId() {
        given()
                .pathParam("orderId", 9999)
                .when()
                .get("/store/order/{orderId}")
                .then()
                .statusCode(404)
                .body("message", equalTo("Order not found"));
    }

    @Test(priority = 6)
    public void testDeleteNonExistentOrder() {
        given()
                .pathParam("orderId", 9999)
                .when()
                .delete("/store/order/{orderId}")
                .then()
                .statusCode(404)
                .body("message", equalTo("Order Not Found"));
    }
}