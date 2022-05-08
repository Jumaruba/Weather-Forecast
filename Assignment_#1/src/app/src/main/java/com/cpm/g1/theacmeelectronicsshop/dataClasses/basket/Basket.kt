package com.cpm.g1.theacmeelectronicsshop.dataClasses.basket

import java.io.Serializable

data class Basket(
    var userUUID: String = "",
    var products: List<Product>,
    var date: String? = "",
    var hour: String? = ""
): Serializable {
}

data class Basket(
    var userUUID: String,
    var products: List<ItemQuantity>,
    var total: String
): Serializable {
}