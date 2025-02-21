// import * as actionTypes from "../constants/productsConstants";


// export const getProductsReducer = ( state = { products:[]},action)=>{

//   switch(action.type) {
//     case actionTypes.GET_PRODUCTS_SUCCESS:
//      return {products : action.payload}


//     case actionTypes.GET_PRODUCTS_FAIL:
//     return {error : action.payload }

//     default:
//         return state
//   }




// };

import * as actionTypes from "../constants/productsConstants";

const initialState = {
  products: [],
  error: null, // Add error to initial state
};

export const getProductsReducer = (state = initialState, action) => {
  switch (action.type) {
    case actionTypes.GET_PRODUCTS_SUCCESS:
      return {
        ...state, // Preserve existing state
        products: action.payload,
        error: null, // Clear error on success
      };

    case actionTypes.GET_PRODUCTS_FAIL:
      return {
        ...state, // Preserve existing state
        error: action.payload,
      };

    default:
      return state;
  }
};

export const getProductDetailReducer = (state = {product: {}}, action) => {
  switch (action.type) {
    case actionTypes.GET_PRODUCT_DETAILS_REQUEST:
      return {
        loading:true,
        error: null, // Clear error on success
      };

    case actionTypes.GET_PRODUCT_DETAILS_SUCCESS:
      return {
        loading:false,
        product: action.payload,
        error: action.payload,
      };

      case actionTypes.GET_PRODUCT_DETAILS_FAIL:
      return {
        loading:false,
        error: action.payload,
      };
      case actionTypes.GET_PRODUCT_DETAILS_RESET:
      return {
       
        product:{}
        
      };
    default:
      return state;
  }
};

