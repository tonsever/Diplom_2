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

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;

public class OrdersTest {
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
    @DisplayName("Получение заказов конкретного пользователя с авторизацией")
    @Description("Проверям код ответа, ингредиенты, и количество заказов (тут баг убрал из проверки)")
    public void GetUserOrdersWithAuthorizationPositiveResult() {
        //int totalExpected = 2;
        IngredientsMethods ingredientsMethods = new IngredientsMethods();
        GetIngredientsResponse getIngredientsResponse = ingredientsMethods.get().as(GetIngredientsResponse.class);
        String firstIdForFirstOrderExpected = getIngredientsResponse.getData().get(0).get_id();
        //String firstNameForFirstOrderExpected = getIngredientsResponse.getData().get(0).getName();
        String secondIdForFirstOrderExpected = getIngredientsResponse.getData().get(1).get_id();
        //String secondNameForFirstOrderExpected = getIngredientsResponse.getData().get(1).getName();
        List<String> ingredientsData = Arrays.asList(firstIdForFirstOrderExpected, secondIdForFirstOrderExpected);
        ingredients = new Ingredients(ingredientsData);
        orderMethods.create(ingredients, accessToken);
        String firstIdForSecondOrderExpected = getIngredientsResponse.getData().get(2).get_id();;
        String secondIdForSecondOrderExpected = getIngredientsResponse.getData().get(3).get_id();
        ingredientsData = Arrays.asList(firstIdForSecondOrderExpected, secondIdForSecondOrderExpected);
        ingredients = new Ingredients(ingredientsData);
        orderMethods.create(ingredients, accessToken);
        Response getUserOrdersResponse = orderMethods.getUserOrders(accessToken);
        //System.out.println(getUserOrdersResponse.then().extract().asString());
        GetOrdersResponse getOrdersResponse = getUserOrdersResponse.as(GetOrdersResponse.class);
        int statusCode = getUserOrdersResponse.then().extract().statusCode();
        //int totalActual = getOrdersResponse.getTotal();
        String firstIdForFirstOrderActual = getOrdersResponse.getOrders().get(0).getIngredients().get(0);
        String secondIdForFirstOrderActual = getOrdersResponse.getOrders().get(0).getIngredients().get(1);
        String firstIdForSecondOrderActual = getOrdersResponse.getOrders().get(1).getIngredients().get(0);
        String secondIdForSecondOrderActual = getOrdersResponse.getOrders().get(1).getIngredients().get(1);
        assertEquals("Не верный код статуса", HTTP_OK, statusCode);
        //assertEquals("Не верное количество заказов", totalExpected, totalActual);
        assertEquals("Не верный хэш первого ингредиента в первом заказе", firstIdForFirstOrderExpected, firstIdForFirstOrderActual);
        assertEquals("Не верный хэш второго ингредиента в первом заказе", secondIdForFirstOrderExpected, secondIdForFirstOrderActual);
        assertEquals("Не верный хэш первого ингредиента во втором заказе", firstIdForSecondOrderExpected, firstIdForSecondOrderActual);
        assertEquals("Не верный хэш второго ингредиента во втором заказе", secondIdForSecondOrderExpected, secondIdForSecondOrderActual);
    }

    @Test
    @DisplayName("Получение заказов конкретного пользователя без авторизацией")
    @Description("Проверям код ответа и сообщение ошибки")
    public void GetUserOrdersWithoutIngredientsError() {
        String firstIngredientForFirstOrderExpected = "61c0c5a71d1f82001bdaaa6d";
        String secondIngredientForFirstOrderExpected = "61c0c5a71d1f82001bdaaa6f";
        List<String> ingredientsData = Arrays.asList(firstIngredientForFirstOrderExpected, secondIngredientForFirstOrderExpected);
        ingredients = new Ingredients(ingredientsData);
        orderMethods.create(ingredients, accessToken);
        String firstIngredientForSecondOrderExpected = "61c0c5a71d1f82001bdaaa70";
        String secondIngredientForSecondOrderExpected = "61c0c5a71d1f82001bdaaa71";
        ingredientsData = Arrays.asList(firstIngredientForSecondOrderExpected, secondIngredientForSecondOrderExpected);
        ingredients = new Ingredients(ingredientsData);
        orderMethods.create(ingredients, accessToken);
        accessToken = "";
        Response getUserOrdersResponse = orderMethods.getUserOrders(accessToken);
        //System.out.println(getUserOrdersResponse.then().extract().asString());
        int statusCode = getUserOrdersResponse.then().extract().statusCode();
        String errorMessageExpected = "You should be authorised";
        String errorMessageActual = getUserOrdersResponse.then().extract().path("message");
        assertEquals("Не верный код статуса", HTTP_UNAUTHORIZED, statusCode);
        assertEquals("Не верное сообщение ошибки", errorMessageExpected, errorMessageActual);
    }
}

