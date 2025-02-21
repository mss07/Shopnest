import { Box, InputBase, List, ListItem } from "@mui/material";
import { useState , useEffect} from "react";
import GlobalStyles from "@mui/styled-engine";
import SearchIcon from '@mui/icons-material/Search';
import { useSelector, useDispatch } from "react-redux";
import { getproducts } from "../../redux/actions/productAction";
import { Link } from "react-router-dom";

const Searchcontainer = GlobalStyles(Box)`

background: #fff;
width: 38%;
border-radius:2px;
margin-left: 10px;
display: flex;

`
const Inputsearchbase = GlobalStyles(InputBase)`

padding-left: 20px;
width: 100%;
font-size: unset;


`
const Seachiconwrapper= GlobalStyles(Box)`
   color: blue;
   padding: 5px;
   display: flex;


`

const ListWrapper = GlobalStyles(List)`
 position: absolute;
 Background: #FFFFFF;
 color: #000;
 margin-top:36px;

`

const Search= ()=>{
    const [text, setText] = useState('');
    const {products} = useSelector((state) =>state.getProducts);
    const dispatch= useDispatch();

    useEffect(()=>{

       dispatch(getproducts());


    },[dispatch])
   const getText = (text) =>{

     setText(text);




   }
     return(
        <Searchcontainer>

        <Inputsearchbase
           placeholder= 'Search for products, brands and more'
           onChange={(e)=>getText(e.target.value)}
           value={text}
        
        />
             <Seachiconwrapper>
                <SearchIcon/>
             </Seachiconwrapper>
             {

               text &&
               <ListWrapper>
                  {
                     products.filter((product) =>product.title.longTitle.toLowerCase().includes(text.toLocaleLowerCase())).map(product =>(

                         <ListItem>
                           

                           <Link to= {`/product/${product.id}`}
                            onClick = {()=> setText('')}
                            style={{textDecoration: 'none', color: 'inherit'}}
                           >
                           {product.title.longTitle}

                            </Link>
                           
                           



                         </ListItem>



                     ))



                  }



               </ListWrapper>




             }
        </Searchcontainer>
        
        
        
        
        
     )




}

export default Search;