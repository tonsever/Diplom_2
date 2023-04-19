import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.diplom_2.model.*;

import java.util.Arrays;
import java.util.List;

import static java.net.HttpURLConnection.*;
import static org.junit.Assert.assertEquals;

public class OrderTest {
    private Ingredients ingredients;
    private OrderMethods orderMethods;
    private UserMethods userMethods;
    private User user;
    private UserAccount userAccount;
    private CreatingOrderResponse creatingOrderResponse;
    private String accessToken;

    @Before
    public void setUp() {
        orderMethods = new OrderMethods();
        userMethods = new UserMethods();
        user = new User("hatake@mail.com", "1999", "Kakashi");
        userMethods.create(user);
        userAccount = new UserAccount("hatake@mail.com", "1999");
        ValidatableResponse loginResponse = userMethods.login(userAccount);
        accessToken = loginResponse.extract().path("accessToken");
    }

    @After
    public void clearData() {
        UserAccount userAccount = new UserAccount("hatake@mail.com", "1999");
        ValidatableResponse loginResponse = userMethods.login(userAccount);
        accessToken = loginResponse.extract().path("accessToken");
        userMethods.delete(accessToken);
    }

    @Test
    @DisplayName("Создание заказа с авторизацией")
    @Description("Проверям код ответа и ингредиенты")
    public void CreatingOrderWithAuthorizationPositiveResult() {
        String firstIngredientExpected = "61c0c5a71d1f82001bdaaa6d";
        String secondIngredientExpected = "61c0c5a71d1f82001bdaaa6f";
        List<String> ingredientsData = Arrays.asList(firstIngredientExpected, secondIngredientExpected);
        ingredients = new Ingredients(ingredientsData);
        Response createResponse = orderMethods.create(ingredients, accessToken);
        creatingOrderResponse = createResponse.as(CreatingOrderResponse.class);
        int statusCode = createResponse.then().extract().statusCode();
        String firstIngredientActual = creatingOrderResponse.getOrder().getIngredients().get(0).get_id();
        String secondIngredientActual = creatingOrderResponse.getOrder().getIngredients().get(1).get_id();
        //System.out.println(createResponse.then().extract().asString());
        assertEquals("Не верный код статуса", HTTP_OK, statusCode);
        assertEquals("Не верный хэш первого ингредиента", firstIngredientExpected, firstIngredientActual);
        assertEquals("Не верный хэш второго ингредиента", secondIngredientExpected, secondIngredientActual);
    }

    @Test
    @DisplayName("Создание заказа без ингредиентов")
    @Description("Проверям код ответа и сообщение ошибки")
    public void CreatingOrderWithoutIngredientsError() {
        List<String> ingredientsData = List.of();
        ingredients = new Ingredients(ingredientsData);
        Response createResponse = orderMethods.create(ingredients, accessToken);
        creatingOrderResponse = createResponse.as(CreatingOrderResponse.class);
        int statusCode = createResponse.then().extract().statusCode();
        String errorMessageExpected = "Ingredient ids must be provided";
        String errorMessageActual = createResponse.then().extract().path("message");
        assertEquals("Не верный код статуса", HTTP_BAD_REQUEST, statusCode);
        assertEquals("Не верное сообщение ошибки", errorMessageExpected, errorMessageActual);
    }

    @Test
    @DisplayName("Создание заказа без авторизацией")
    @Description("Проверям код ответа и сообщение ошибки (тут нашёлся баг)")
    public void CreatingOrderWithoutAuthorizationPositiveResult() {
        accessToken = "";
        String firstIngredientExpected = "61c0c5a71d1f82001bdaaa6d";
        String secondIngredientExpected = "61c0c5a71d1f82001bdaaa6f";
        List<String> ingredientsData = Arrays.asList(firstIngredientExpected, secondIngredientExpected);
        ingredients = new Ingredients(ingredientsData);
        Response createResponse = orderMethods.create(ingredients, accessToken);
        creatingOrderResponse = createResponse.as(CreatingOrderResponse.class);
        int statusCode = createResponse.then().extract().statusCode();
        String errorMessageExpected = "You should be authorised";
        String errorMessageActual = createResponse.then().extract().path("message");
        assertEquals("Не верный код статуса", HTTP_UNAUTHORIZED, statusCode);
        assertEquals("Не верное сообщение ошибки", errorMessageExpected, errorMessageActual);
    }

    @Test
    @DisplayName("Создание заказа с неверным хешем ингредиентов")
    @Description("Проверям код ответа")
    public void CreatingOrderWithIncorrectHashIngredientsError() {
        String firstIngredientExpected = "xxx";
        String secondIngredientExpected = "xxx";
        List<String> ingredientsData = Arrays.asList(firstIngredientExpected, secondIngredientExpected);
        ingredients = new Ingredients(ingredientsData);
        Response createResponse = orderMethods.create(ingredients, accessToken);
        int statusCode = createResponse.then().extract().statusCode();
        assertEquals("Не верный код статуса", HTTP_INTERNAL_ERROR, statusCode);
    }
}
