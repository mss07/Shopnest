import mongoose from "mongoose";
import dns from "dns";
// Set DNS servers to Google's public DNS to resolve MongoDB Atlas SRV records
dns.setServers(["8.8.8.8", "8.8.4.4"]);

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