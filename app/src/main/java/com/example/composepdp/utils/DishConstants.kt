package com.example.composepdp.utils

import com.example.composepdp.viewmodel.model.DishItem

object DishConstants {
    val DEFAULT_DISHES = listOf(
        "Паста Карбонара",
        "Суши Филадельфия",
        "Том Ям",
        "Бургер BBQ",
        "Пельмени",
        "Шаурма",
        "Рамен",
        "Тирамису",
        "Чизкейк",
        "Стейк медиум",
        "Пицца Маргарита",
        "Греческий салат",
        "Куриные наггетсы",
        "Медовик",
        "Борщ",
        "Цезарь с курицей",
        "Оливье",
        "Хачапури по-аджарски",
        "Фалафель",
        "Гаспачо",
        "Курица терияки",
        "Мороженое пломбир"
    ).mapIndexed { index, name ->
        DishItem(id = index, name = name)
    }
}