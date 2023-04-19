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
import static org.junit.Assert.assertTrue;

public class UserLoginTest {
    private UserMethods userMethods;
    private User user;
    private UserAccount userAccount;
    private String accessToken;
    private String refreshToken;

    @Before
    public void setUp() {
        userMethods = new UserMethods();
        user = new User("hatake@mail.com", "1999", "Kakashi");
        userAccount = new UserAccount("hatake@mail.com", "1999");
    }

    @After
    public void clearData() {
        UserAccount userAccount = new UserAccount("hatake@mail.com", "1999");
        ValidatableResponse loginResponse = userMethods.login(userAccount);
        accessToken = loginResponse.extract().path("accessToken");
        userMethods.delete(accessToken);
    }

    @Test
    @DisplayName("Пользователь может авторизоваться")
    @Description("Проверяем код ответа и success: true")
    public void loginUserPositiveResult() {
        userMethods.create(user);
        ValidatableResponse loginResponse = userMethods.login(userAccount);
        //System.out.println(loginResponse.extract().statusCode());
        int statusCode = loginResponse.extract().statusCode();
        boolean isSuccessTrue = loginResponse.extract().path("success");
        assertEquals("Не верный код статуса", HTTP_OK, statusCode);
        assertTrue("Не верное значение ключа success", isSuccessTrue);
    }

    @Test
    @DisplayName("Нельзя авторизоваться c неверным логином и паролем")
    @Description("Проверям код ошибки и сообщение")
    public void loginUserWithWrongPasswordAndEmailError() {
        userMethods.create(user);
        UserAccount userAccount = new UserAccount("ххх", "xxx");
        ValidatableResponse loginResponse = userMethods.login(userAccount);
        //System.out.println(loginResponse.extract().asString());
        int statusCode = loginResponse.extract().statusCode();
        String errorMessageExpected = "email or password are incorrect";
        String errorMessageActual = loginResponse.extract().path("message");
        assertEquals("Не верный код статуса", HTTP_UNAUTHORIZED, statusCode);
        assertEquals("Не верное сообщение ошибки", errorMessageExpected, errorMessageActual);
    }

}
