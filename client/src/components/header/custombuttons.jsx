import { useState , useContext} from "react";


import { Badge, Box,Button, Typography } from "@mui/material";
import {ShoppingCart} from '@mui/icons-material';
import GlobalStyles from "@mui/styled-engine";
import { useSelector } from "react-redux";

import  { Datacontext } from "../../context/dataprovider";

//components
import Logindialog from "../login/logindialog";
import Profile from "./profile";
import { Link } from "react-router-dom";
import CartItem from "../Cart/Cartitem";




const Wrapper = GlobalStyles(Box)`

display: flex;
margin: 0 3% 0 auto;
& > button, & > p, & > div {
    margin-right:40px;
    font-size: 16px;
    align-items: centre;
}
`
const Container = GlobalStyles(Link)`

 display:flex;
 text-Decoration: none;
 color:inherit;
 margin-Top: 4px;


`

const LoginButton = GlobalStyles(Button)`


color: #8A2BE2;
background: #ffffff;
text-transform: none;
padding: 5px 40px;
border-radius:2px;
box-shadow: none;
font-weight: 600;
height: 32px;


`
const CustomButtons= ()=>{


    const [open,setopen]= useState(false);

    const {account, setaccount} = useContext(Datacontext);
    const {cartItems } = useSelector(state => state.cart)

    const openDialog = ()=>{

     setopen(true);



    }




    return(

    <Wrapper>
         {        
              account ? <Profile account= {account} setaccount={setaccount}/>
              :
             <LoginButton variant="contained" onClick={()=> openDialog() }>Login</LoginButton>


         }

     <Typography style={{marginTop: 3, width:135}}>Become a Seller</Typography>
     <Typography style={{marginTop: 3}}> More </Typography>

     <Container to = '/Cart'>
       <Badge badgeContent= {cartItems?.length} color="secondary">

        <ShoppingCart/>

       </Badge>

     <Typography style={{marginLeft:10}}>cart</Typography>

     </Container>
     <Logindialog open={open} setopen={setopen}/>

    </Wrapper>
        
    


    )



}
export default CustomButtons;