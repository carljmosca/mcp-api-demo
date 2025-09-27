package org.sebi;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;

@QuarkusTest
class TicketResourceTest {
    @Test
    void testGetTicketsEndpoint() {
        given()
          .when().get("/tickets")
          .then()
             .statusCode(200);
    }

}