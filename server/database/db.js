import mongoose from "mongoose";

export const Connection =async ()=>{

  

  const URL = `mongodb+srv://user:mehar007@ecommerce-website.mosho.mongodb.net/?retryWrites=true&w=majority&appName=Ecommerce-website`;
   try{

    await mongoose.connect(URL);
    console.log("database connected successfully");



   } catch(error){


     console.log("error while connecting with db", error.message);



   }





}

export default Connection;