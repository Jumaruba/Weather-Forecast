const { User } = require("../models/user");
const { Basket } = require("../models/basket");
const { Product } = require("../models/product");
const { ObjectId } = require('mongodb');

const express = require('express');
const bcrypt = require('bcrypt');
const crypto = require('crypto');
const router = express.Router();
const node_uuid = require('node-uuid');


router.post('/checkout', async (req, res) => {

    // Validate request body params
    if(!req.body.basket || !req.body.signature){
        return res.status(400).send({message: "Please provide the signed basket and uuid"})
    }   

    const uuid = req.body.basket.userUUID
    const products = req.body.basket.products

    if(products.length == 0)
        return res.status(401).send({"message": "Empty basket"})

    // Check signature
    try {
        const verifier = crypto.createVerify('RSA-SHA256')

        let user = await User.findOne({ _id: uuid});
        if(!user) return res.status(400).send({"message": "Uknown user"})

        let jsonBasket = JSON.stringify(req.body.basket)
        verifier.update(jsonBasket)

        const result = verifier.verify(user.pk, req.body.signature, 'hex')
        
        if(result){  
            // TODO: verify credit card, save basket in the server with new identifier(new uuid)
            const basket_uuid = node_uuid.v1()
            const keyObj = crypto.createPublicKey(user.pk)

            var encrypted = crypto.publicEncrypt({key: keyObj, padding: crypto.constants.RSA_PKCS1_PADDING}, Buffer.from(basket_uuid))
            return res.status(200).send({"message": encrypted.toString('base64')})
        } else {
            return res.status(401).send({"message": "No authorization"})
        }
        
    } catch(err){
        console.log(err)
        return res.status(400).send({"message": "Something went wrong"})
    }
});


/**
 * Get requests may have a body, but it shouldn't have any meaning.
 * Thus using post seems to be the more correct option. 
 */
router.post('/history', async (req, res) => {
    console.log(req.body)
    if (!req.body.userUUID || !req.body.signature) {
        return res.status(400).send({ message: "Please provide the signed basket and uuid" });
    }


    try {
        let history = await Basket.find({ userUUID: req.params.userUUID });
        console.log(history)
        return res.status(200).send(history);
    } catch (err) {
        console.log(err);
        return res.status(400).send({ message: "Couldn't proceed with the request" });
    }
});


const checkSignature = async (uuid, req) => {
    const verifier = crypto.createVerify('RSA-SHA256')

    let user = await User.findOne({ _id: uuid });
    let jsonBasket = JSON.stringify(req.body.basket);
    verifier.update(jsonBasket);

    return verifier.verify(user.pk, req.body.signature, 'hex')
}

const addToDatabase = async (req) => {
    // Get hour 
    const now = new Date();

    const current = now.getHours().toString().padStart(2, 0) + ':' + now.getMinutes().toString().padStart(2, '0');

    var today = new Date().toISOString().slice(0, 10);
    let basket = new Basket({ ...req.body.basket, date: today, hour: current });
    // TODO : verify price  

    await basket.save(function (err, doc) {
        if (err) return res.status(400).send({ message: err })
        console.log("Basket saved with successs!");
    });
}
router.get("/products", async(req, res) => {
    try {
        const ids = req.query.ids.split(",").map(item => parseInt(item, 10))
        const products = await Product.find().where('id').in(ids)
        return res.status(200).send({"message": "Successfully retrieved all your products", "products": products}); 
    } catch(err){
        console.log(err)
        return res.status(400).send({"message": "Error retrieving your products", "products": []}); 
    }
});
module.exports = router; 