import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import ru.yandex.praktikum.diplom_2.model.User;
import ru.yandex.praktikum.diplom_2.model.UserAccount;
import ru.yandex.praktikum.diplom_2.model.UserMethods;

import static java.net.HttpURLConnection.HTTP_FORBIDDEN;
import static java.net.HttpURLConnection.HTTP_OK;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class CreatingUserTest {
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
        UserAccount userAccount = new UserAccount("hatake@mail.com", "1999");
        ValidatableResponse loginResponse = userMethods.login(userAccount);
        accessToken = loginResponse.extract().path("accessToken");
        userMethods.delete(accessToken);
    }

    @Test
    @DisplayName("Пользователя можно создать")
    @Description("Проверяем код ответа и success: true")
    public void сreateUserPositiveResult() {
        ValidatableResponse createResponse = userMethods.create(user);
        //System.out.println(createResponse.extract().asString());
        int statusCode = createResponse.extract().statusCode();
        boolean isSuccessTrue = createResponse.extract().path("success");
        assertEquals("Не верный код статуса", HTTP_OK, statusCode);
        assertTrue("Не верное значение ключа success", isSuccessTrue);
    }

    @Test
    @DisplayName("Нельзя создать двух одинаковых пользователей")
    @Description("Проверям код ошибки и сообщение")
    public void сreateTwoIdenticalUserError() {
        userMethods.create(user);
        ValidatableResponse createResponse = userMethods.create(user);
        //System.out.println(createResponse.extract().asString());
        int statusCode = createResponse.extract().statusCode();
        String errorMessageExpected = "User already exists";
        String errorMessageActual = createResponse.extract().path("message");
        assertEquals("Не верный код статуса", HTTP_FORBIDDEN, statusCode);
        assertEquals("Не верное сообщение ошибки", errorMessageExpected, errorMessageActual);
    }

    @Test
    @DisplayName("Нельзя создать пользователя без email")
    @Description("Проверям код ошибки и сообщение")
    public void createUserWithoutEmailError() {
        User userWithoutEmail = new User(null, "1999", "Kakashi");
        ValidatableResponse createResponse = userMethods.create(userWithoutEmail);
        //System.out.println(createResponse.extract().asString());
        int statusCode = createResponse.extract().statusCode();
        String errorMessageExpected = "Email, password and name are required fields";
        String errorMessageActual = createResponse.extract().path("message");
        assertEquals("Не верный код статуса", HTTP_FORBIDDEN, statusCode);
        assertEquals("Не верное сообщение ошибки", errorMessageExpected, errorMessageActual);
        userMethods.create(user); //Иначе clearData() падает с NullPointerException
    }


}
