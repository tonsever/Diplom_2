package ru.yandex.praktikum.diplom_2.model;

import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;

import static io.restassured.RestAssured.given;

public class UserMethods extends BurgersRestClient {
    private static final String USER_URI = BASE_URI + "auth/";

    @Step("Создаём пользователя")
    public ValidatableResponse create(User user) {
        return given()
                .spec(getBaseReqSpec())
                .body(user)
                .when()
                .post(USER_URI + "register")
                .then();
    }

    @Step("Логируемся")
    public ValidatableResponse login(UserAccount userAccount) {
        return given()
                .spec(getBaseReqSpec())
                .body(userAccount)
                .when()
                .post(USER_URI + "login/")
                .then();
    }

    @Step("Удаляем пользователя")
    public ValidatableResponse delete(String accessToken) {
        return given()
                .spec(getBaseReqSpec())
                .header("authorization", accessToken)
                .when()
                .delete(USER_URI + "user/")
                .then();
    }

    @Step("Изменение данных пользователя")
    public ValidatableResponse update(User user, String accessToken) {
        return given()
                .spec(getBaseReqSpec())
                .header("authorization", accessToken)
                .body(user)
                .when()
                .patch(USER_URI + "user/")
                .then();
    }
}
