import React from "react";

import {AppBar, Box, Toolbar, Typography} from '@mui/material';
// import { Menu } from "@mui/icons-material";
import GlobalStyles from "@mui/styled-engine" ;

//components
import Search from "./search";
import CustomButtons from "./custombuttons";

import { Link } from "react-router-dom";

const StyledHeader = GlobalStyles(AppBar)`
 background : #8A2BE2
;

`
const Component = GlobalStyles(Link)`

   margin-left: 12%;
   line-height:0;
   text-decoration:none;
   color: inherit;


`

const Subheading = GlobalStyles(Typography)`

font-size:20px;
font-style:italic;
font-weight: 525;


`
const Plusimage= GlobalStyles('img')({
  width:10,
  height:10,
  marginLeft:4



})


const CustomButtonwrapper = GlobalStyles(Box)`
  margin : 0 5% 0 auto;


`


function Header() {

    const logourl='https://see.fontimg.com/api/rf5/Rpx1A/YjI2MDEzZjc5MzdhNGUzNGEwNmNmZGM1MWMxMzhiYzUub3Rm/U2hvcG5lc3Q/miracles-autumn.png?r=fs&h=85&w=1250&fg=FFFEF8&bg=FFFFFF&tb=1&s=68' 
    return (
        
        <StyledHeader>
            <Toolbar>
              {/* <IconButton>
                <Menu/>
              </IconButton> */}
              <Component to='/'>

              <img  src= {logourl} alt ="logo" style={{width:75, color: '#ffffff'}} />
              <Box style={{display: 'flex' }}>
                {/* <Subheading>ShopNest&nbsp; */}
                    {/* <Box component="span" style={{color:"#FFE500"}}>Plus</Box> */}
                    {/* </Subheading> */}
                    {/* <Plusimage src={ 'https://static-assets-web.flixcart.com/www/linchpin/fk-cp-zion/img/plus_aef861.png'} alt='sublogo'></Plusimage> */}
                
              </Box>
              
              </Component>
              <Search/>
              <CustomButtonwrapper>
              <CustomButtons/>

              </CustomButtonwrapper>
              
            </Toolbar>
            </StyledHeader>


    );



}

export default Header;