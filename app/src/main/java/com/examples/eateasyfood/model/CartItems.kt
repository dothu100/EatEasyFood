package com.examples.eateasyfood.model

data class CartItems(
    val foodName: String? = null,
    val foodPrice: String? = null,
    val foodDescription: String? = null,
    val foodImage: String? = null,
    val foodQuantity: Int? = null,
    val foodIngredient: String? = null // Thêm dấu "?" để đánh dấu tham số này là lựa chọn
)
