import mongoose from "mongoose";

const productschema = new mongoose.Schema({


  id:{
   type: String,
   required: true,
   unique : true

  },
  url:String,
  detailurl:String,
  title: Object,
  price: Object,
  quantity: Number,
  description: String,
  tagline: String


});

const Product = mongoose.model("product", productschema);

export default Product;