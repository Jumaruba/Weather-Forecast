const { Product } = require("../models/product");
const express = require('express');
const router = express.Router();

router.get("/", async (req, res) => {
  const products = await Product.find();
  res.send(products);
});

router.get("/list", async (req, res) => {
  let products = req.query.prod;
  console.log(products);
  let response = []
  if (!Array.isArray(products)) {
    const product = await Product.findOne({ products });
    response.push(product);
  } else {
    for (let id of products) {
      const product = await Product.findOne({ id: id });
      response.push(product);
      console.log(product)
    }
  }
  console.log(response);
  res.status(200).send({ message: "OK", products: response });
});

router.get("/:id", async (req, res) => {
  const products = await Product.find({ id: req.params.id });
  res.send(products);
});



module.exports = router; 