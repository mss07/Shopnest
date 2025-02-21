import { useEffect } from 'react';


import Navbar from "./navbar";
import Banner from "./banner";
import Slide from './Slide.jsx';
import { Box } from "@mui/material";
import GlobalStyles from "@mui/styled-engine" ;

import MidSlide from './MidSlide.jsx';

import MidSection from './MidSection.jsx';

import { getproducts } from '../../redux/actions/productAction.js';
import {useDispatch,useSelector} from 'react-redux';



const Component = GlobalStyles(Box)`

 padding: 10px 10px;
 background: #F2F2F2


`

const Home = ()=>{

   const {products}  = useSelector((state) => state.getProducts);
   
   
   //  const {products} = yoproducts;
   //  console.log(products);
     
   
   const dispatch = useDispatch();

   useEffect(()=>{
     
      dispatch(getproducts());



   }, [dispatch])
   
   

   return(
    <>
    
    <Navbar/>
    <Component>


    <Banner/>
    <MidSlide products={products} />
                <MidSection />
                <Slide
                    data={products} 
                    title='Discounts for You'
                    timer={false} 
                    multi={true} 
                />
                <Slide
                    data={products} 
                    title='Suggested Items'
                    timer={false} 
                    multi={true} 
                />
                <Slide
                    data={products} 
                    title='Top Selection'
                    timer={false} 
                    multi={true} 
                />
                <Slide
                    data={products} 
                    title='Recommended Items'
                    timer={false} 
                    multi={true} 
                />
    </Component>
   
    
    </>
    



   )




}

export default Home;