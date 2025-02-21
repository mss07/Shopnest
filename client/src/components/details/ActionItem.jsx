import { useState } from "react";

import { Box, Button } from "@mui/material"
import GlobalStyles from "@mui/styled-engine" ;
import { ShoppingCart as Cart, FlashOn as Flash } from '@mui/icons-material';
import { useNavigate } from "react-router-dom";
import { useDispatch } from "react-redux";
import { addToCart } from "../../redux/actions/cartActions";
import { payUsingPaytm } from "../../service/api";
import { post } from "../../utils/paytm";

const LeftContainer = GlobalStyles(Box)`

  min-width: 40%;
  padding: 40px 0 0 80px;




`;

const Image= GlobalStyles('img')({

    padding: '15px 20px',
    border: '1px solid #f0f0f0',
    width: '95%'
   
})

const StyledButton = GlobalStyles(Button)`
    width: 46%;
    border-radius: 2px;
    height: 50px;
    color: #FFF;
`;

const ActionItem = ({product}) =>{

    const navigate = useNavigate();
    const dispatch = useDispatch();
    const [quantity, setquantity] = useState(1);

    const {id}= product;

    const buyNow = async () => {
        let response = await payUsingPaytm({ amount: 500, email: 'singh.prabhmeet2001@gmail.com'});
        var information = {
            action: 'https://securegw-stage.paytm.in/order/process',
            params: response    
        }
        post(information);
    }

    const addItemToCart = () =>{
        dispatch(addToCart(id,quantity));
        navigate('/cart');





    }
    return (
       <LeftContainer>
        <Box  style={{border: '1px solid #f0f0f0', width: '90%'}}>
        <Image src={product.url} alt="product"/>

        </Box>
        
        <StyledButton variant="contained" onClick={()=>addItemToCart()} style={{marginRight: 10, background: '#8A2BE2'}}><Cart/>Add to Cart</StyledButton>
        <StyledButton variant="contained" onClick= {()=> buyNow()}style={{background: '#8A2BE2'}}><Flash/>Buy Now</StyledButton>
       </LeftContainer>
    )
}

export default ActionItem;