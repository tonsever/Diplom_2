import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.diplom_2.model.User;
import ru.yandex.praktikum.diplom_2.model.UserAccount;
import ru.yandex.praktikum.diplom_2.model.UserMethods;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.junit.Assert.assertEquals;

public class UpdatingUserDataTest {
    private UserMethods userMethods;
    private User user;
    private UserAccount userAccount;
    private String accessToken;

    @Before
    public void setUp() {
        userMethods = new UserMethods();
        user = new User("hatake@mail.com", "1999", "Kakashi");
        userAccount = new UserAccount("hatake@mail.com", "1999");
    }

    @After
    public void clearData() {
        userMethods.delete(accessToken);
    }

    @Test
    @DisplayName("Обновление данных пользователя с авторизацией")
    @Description("Проверям код ответа и новые данные")
    public void updatingUserDataWithAuthorizationPositiveResult() {
        userMethods.create(user);
        ValidatableResponse loginResponse = userMethods.login(userAccount);
        accessToken = loginResponse.extract().path("accessToken");
        String newEmailExpected = "hatake@mail.ru";
        String newNameExpected = "KakAshi";
        User updateUser = new User(newEmailExpected, newNameExpected);
        ValidatableResponse updateResponse = userMethods.update(updateUser, accessToken);
        //System.out.println(updateResponse.extract().asString());
        int statusCode = updateResponse.extract().statusCode();
        String newEmailActual = updateResponse.extract().path("user.email");
        String newNameActual = updateResponse.extract().path("user.name");
        assertEquals("Не верный код статуса", HTTP_OK, statusCode);
        assertEquals("email не обновился", newEmailExpected, newEmailActual);
        assertEquals("name не обновился", newNameExpected, newNameActual);
    }

    @Test
    @DisplayName("Обновление данных пользователя без авторизацией")
    @Description("Проверям код ответа и сообщение ошибки")
    public void updatingUserDataWithoutAuthorizationError() {
        accessToken = "";
        String newEmailExpected = "hatake@mail.ru";
        String newNameExpected = "KakAshi";
        User updateUser = new User(newEmailExpected, newNameExpected);
        ValidatableResponse updateResponse = userMethods.update(updateUser, accessToken);
        //System.out.println(updateResponse.extract().asString());
        int statusCode = updateResponse.extract().statusCode();
        String errorMessageExpected = "You should be authorised";
        String errorMessageActual = updateResponse.extract().path("message");
        assertEquals("Не верный код статуса", HTTP_UNAUTHORIZED, statusCode);
        assertEquals("Не верное сообщение ошибки", errorMessageExpected, errorMessageActual);
    }
}
