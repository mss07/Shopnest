import { Card, Box, Typography, Button} from '@mui/material';
import GlobalStyles from "@mui/styled-engine" ;


import { addEllipsis } from '../../utils/util';
import GroupButton from './GroupButton';

const Component = GlobalStyles(Card)`
    border-top: 1px solid #f0f0f0;
    border-radius: 0px;
    display: flex;
`;

const LeftComponent = GlobalStyles(Box)`
    margin: 20px; 
    display: flex;
    flex-direction: column;
`;

const SmallText = GlobalStyles(Typography)`
    color: #878787;
    font-size: 14px;
    margin-top: 10px;
`;

const Cost = GlobalStyles(Typography)`
    font-size: 18px;
    font-weight: 600;
`;

const MRP = GlobalStyles(Typography)`
    color: #878787;
`;

const Discount = GlobalStyles(Typography)`
    color: #388E3C;
`;

const Remove = GlobalStyles(Button)`
    margin-top: 20px;
    font-size: 16px;
`;

const CartItem = ({ item, removeItemFromCart }) => {
    const fassured = 'https://static-assets-web.flixcart.com/www/linchpin/fk-cp-zion/img/fa_62673a.png';
    
    return (
        <Component>
            <LeftComponent>
                <img src={item.url} style={{ height: 110, width: 110 }} />
                <GroupButton  />
            </LeftComponent>
            <Box style={{ margin: 20 }}>
                <Typography>{addEllipsis(item.title.longTitle)}</Typography>
                <SmallText>Seller:RetailNet
                    <span><img src={fassured} alt = 'fassured' style={{ width: 50, marginLeft: 10 }} /></span>
                </SmallText>
                <Typography style={{margin: '20px 0'}}>
                    <Cost component="span">₹{item.price.cost}</Cost>&nbsp;&nbsp;&nbsp;
                    <MRP component="span"><strike>₹{item.price.mrp}</strike></MRP>&nbsp;&nbsp;&nbsp;
                    <Discount component="span">{item.price.discount} off</Discount>
                </Typography>
                <Remove onClick={() => removeItemFromCart(item.id)}>Remove</Remove>
            </Box>
        </Component>
    )
}

export default CartItem;