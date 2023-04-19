package ru.yandex.praktikum.diplom_2.model;

import java.util.List;

public class GetIngredientsResponse {
    private boolean success;
    private List<Ingredient> data;

    public GetIngredientsResponse(boolean success, List<Ingredient> data) {
        this.success = success;
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public List<Ingredient> getData() {
        return data;
    }

    public void setData(List<Ingredient> data) {
        this.data = data;
    }
}
