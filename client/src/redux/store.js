import {createStore} from '@reduxjs/toolkit'
import {combineReducers, applyMiddleware } from 'redux';
import {thunk} from 'redux-thunk';
import {composeWithDevTools} from '@redux-devtools/extension';
import { getProductDetailReducer, getProductsReducer } from './reducers/productReducer';
import {cartReducer} from './reducers/CartReducer';

const reducer = combineReducers({

     getProducts: getProductsReducer,
     getproductdetails: getProductDetailReducer,
     cart:cartReducer


});


const middleware = [thunk];



const store = createStore(


   reducer, 
   composeWithDevTools(applyMiddleware(...middleware))

);

export default store;