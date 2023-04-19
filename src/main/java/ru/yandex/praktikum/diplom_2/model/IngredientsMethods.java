package ru.yandex.praktikum.diplom_2.model;

import io.qameta.allure.Step;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class IngredientsMethods extends BurgersRestClient {
    private static final String INGREDIENTS_URI = BASE_URI + "ingredients/";

    @Step("Получение данных об ингредиентах")
    public Response get() {
        return given()
                .spec(getBaseReqSpec())
                .when()
                .get(INGREDIENTS_URI);
    }
}
