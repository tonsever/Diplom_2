package ru.yandex.praktikum.diplom_2.model;

import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public class OrderMethods extends BurgersRestClient {
    private static final String ORDER_URI = BASE_URI + "orders/";


    @Step("Создаём заказ")
    public Response create(Ingredients ingredients, String accessToken) {
        return given()
                .spec(getBaseReqSpec())
                .header("authorization", accessToken)
                .body(ingredients)
                .when()
                .post(ORDER_URI);

    }

    @Step("Получить заказы конкретного пользователя")
    public Response getUserOrders(String accessToken) {
        return given()
                .spec(getBaseReqSpec())
                .header("authorization", accessToken)
                .when()
                .get(ORDER_URI);
    }
}
