import { useEffect } from "react";
import { useDispatch , useSelector} from "react-redux";
import { useParams } from "react-router-dom";
import { getproductdetails } from "../../redux/actions/productAction";
import { Box, Typography} from '@mui/material';
import ActionItem from "./ActionItem";
import GlobalStyles from "@mui/styled-engine" ;
import { Grid } from "@mui/material";
import ProductDetail from './ProductDetails';

const Component= GlobalStyles(Box)`

  Background: #f2f2f2;
  margin-top: 55px;
`;

const Container= GlobalStyles(Grid)`

  Background: #ffffff;
  display: flex;
`;

const RightContainer= GlobalStyles(Grid)`

  
  margin-top: 50px;
`;
const DetailView= ()=>{

    const dispatch = useDispatch();

    const {id}= useParams();

    const {loading,product}= useSelector(state => state.getproductdetails);

    const fassured = 'https://static-assets-web.flixcart.com/www/linchpin/fk-cp-zion/img/fa_62673a.png'


    useEffect(()=>{
      if(product && id!== product.id)
         dispatch(getproductdetails(id))
    },[dispatch,id,product,loading])
    return(
       <Component>
         {
          product && Object.keys(product).length &&
               <Container container>
                 <Grid item lg={4} md={4} sm={8} xs={12}>
                  <ActionItem product = {product}/>
                 </Grid>
                 <RightContainer item lg={8} md={8} sm={8} xs={12}>
                  <Typography>{product.title.longTitle}</Typography>
                  <Typography style={{margin: 5, color: '#878787', fontSize: 14}}>
                    8 Rating & 1 Reviews
                    {/* <Box component="span"><img src={fassured} alt='fassured'style={{width: 77, marginLeft: 20}}/></Box> */}
                    </Typography>
                    <Typography>
                            <span style={{ fontSize: 28 }}>₹{product.price.cost}</span>&nbsp;&nbsp;&nbsp; 
                            <span style={{ color: '#878787' }}><strike>₹{product.price.mrp}</strike></span>&nbsp;&nbsp;&nbsp;
                            <span style={{ color: '#388E3C' }}>{product.price.discount} off</span>
                    </Typography>
                    <ProductDetail product={product} />
                    
                 </RightContainer>
                   
                 </Container>
               




         }
       </Component>
      

    )





}

export default DetailView;