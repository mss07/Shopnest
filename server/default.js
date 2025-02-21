
import { products } from "./constants/data.js"

import Product from "./model/productschema.js";



const defaultData= async ()=>{

  try{

      
       await Product.insertMany(products);

       console.log('data imported');

  }catch(error)
  {
   console.log("error while inserting default data", error.message);


  }



    
}

export default defaultData;
