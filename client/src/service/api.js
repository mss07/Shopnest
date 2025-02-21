import axios from 'axios';

const URL = 'http://localhost:8000';

export const authenticatesignup = async (data) => {
    try {
        const response = await axios.post(`${URL}/signup`, data);
        return response.data;
    } catch (error) {
        console.log('Error while calling signup API', error);
        throw error; // Re-throw the error to handle it in the calling code
    }
};

export const authenticatelogin = async (data) => {
    try {
        //const response1 = await axios.post(`${URL}/login`, data);
        //return response1.data;
        
        return await axios.post(`${URL}/login`, data)
        
    } catch (error) {
        console.log('Error while calling login API', error);
        return error.response;
    }
};



export  const payUsingPaytm = async (data) => {
    try {
        let response = await axios.post(`${URL}/payment`, data);
        return response.data;
    } catch (error) {
        console.log('Error', error);
        return error.response;
    }
};